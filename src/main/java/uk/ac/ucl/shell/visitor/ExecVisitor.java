package uk.ac.ucl.shell.visitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import uk.ac.ucl.shell.ApplicationFactory;
import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.app.Application;
import uk.ac.ucl.shell.ast.argument.AbstractArg;

import uk.ac.ucl.shell.ast.command.CommandVisitor;
import uk.ac.ucl.shell.ast.command.Command;
import uk.ac.ucl.shell.ast.command.Call;
import uk.ac.ucl.shell.ast.command.Pipe;
import uk.ac.ucl.shell.ast.command.Seq;

import uk.ac.ucl.shell.exception.ShellException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;
import uk.ac.ucl.shell.io.FileInAdaptor;
import uk.ac.ucl.shell.io.FileOutAdaptor;
import uk.ac.ucl.shell.io.DequeAdaptor;

import uk.ac.ucl.shell.util.Globbing;

public class ExecVisitor implements CommandVisitor {
    InputDevice in_;
    OutputDevice out_;
    Shell.Env env_;

    public ExecVisitor(InputDevice in, OutputDevice out, Shell.Env env) {
        this.in_ = in;
        this.out_ = out;
        this.env_ = env;
    }

    private static class Token {
        private StringBuffer sbuf;
        private boolean canGlob_;

        public Token() {
            sbuf = new StringBuffer();
            canGlob_ = false;
        }

        public void append(char character) {
            sbuf.append(character);
        }

        public void append(String content) {
            sbuf.append(content);
        }

        public void allowGlobbing() {
            canGlob_ = true;
        }

        public boolean canGlob() {
            return canGlob_ && sbuf.indexOf("*") != -1;
        }

        @Override
        public String toString() {
            return sbuf.toString();
        }
    }

    private static ArrayList<AbstractArg.Type> getTypes(ArrayList<AbstractArg> abstractArgs) {
        ArrayList<AbstractArg.Type> args = new ArrayList<>();
        for (AbstractArg arg : abstractArgs) {
            args.add(arg.type());
        }
        return args;
    }

    private static ArrayList<String> getArgs(ArrayList<AbstractArg> abstractArgs) {
        ArrayList<String> args = new ArrayList<>();
        for (AbstractArg arg : abstractArgs) {
            args.add(arg.eval());
        }
        return args;
    }

    private static boolean processQuoted(String content, ArrayList<Token> storage, boolean whitespace) {
        if (whitespace) {
            storage.add(new Token());
            whitespace = false;
        }

        Token back = storage.get(storage.size() - 1);
        back.append(content);
        return whitespace;
    }

    private static boolean processCommand(String content, ArrayList<Token> storage, boolean whitespace) {
        for (int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);

            if (whitespace) {
                if (Character.isWhitespace(c)) {
                    continue;
                }
                storage.add(new Token());
                whitespace = false;
            }

            Token back = storage.get(storage.size() - 1);
            if (Character.isWhitespace(c)) {
                whitespace = true;
            } else {
                back.append(c);
            }
        }

        return whitespace;
    }

    private static boolean processPlain(String content, ArrayList<Token> storage, boolean whitespace) {
        for (int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);

            if (whitespace) {
                if (Character.isWhitespace(c)) {
                    continue;
                }
                storage.add(new Token());
                whitespace = false;
            }

            Token back = storage.get(storage.size() - 1);
            if (Character.isWhitespace(c)) {
                whitespace = true;
            } else {
                back.allowGlobbing();
                back.append(c);
            }
        }

        return whitespace;
    }

    private static ArrayList<Token> splitArgs(ArrayList<String> args, ArrayList<AbstractArg.Type> types) {
        ArrayList<Token> storage = new ArrayList<>();
        boolean whitespace = true;

        for (int i = 0; i < args.size(); ++i) {
            String evalResult = args.get(i);
            if (evalResult.isEmpty()) {
                continue;
            }

            switch (types.get(i)) {
                case Plain:
                    whitespace = processPlain(evalResult, storage, whitespace);
                    break;
                case Command:
                    whitespace = processCommand(evalResult, storage, whitespace);
                    break;
                case Quoted:
                    whitespace = processQuoted(evalResult, storage, whitespace);
                    break;
            }
        }

        return storage;
    }

    private ArrayList<String> globArgs(ArrayList<Token> args) {
        ArrayList<String> globedArg = new ArrayList<>();

        String currentDirectory = env_.getProperty("user.dir");

        for (Token t : args) {
            if (!t.canGlob()) {
                globedArg.add(t.toString());
                continue;
            }

            try {
                ArrayList<String> matchFiles = Globbing.match(currentDirectory, t.toString());
                if (!matchFiles.isEmpty()) {
                    for (String filename : matchFiles) {
                        globedArg.add(filename);
                    }
                } else {
                    globedArg.add(t.toString());
                }
            } catch (IOException ioException) {
                throw new ShellException("COMP0010 shell: globing errors");
            }
        }

        return globedArg;
    }

    public ArrayList<String> evalArgs(Call call) {
        ArrayList<Token> splittedArgs = splitArgs(getArgs(call.args_), getTypes(call.args_));
        return globArgs(splittedArgs);
    }

    public Optional<String> evalRedirection(Optional<AbstractArg> ioRedirection) {
        if (!ioRedirection.isEmpty()) {
            AbstractArg fileInNameArg = ioRedirection.get();
            String resolvedName = fileInNameArg.eval();

            if (fileInNameArg.type() == AbstractArg.Type.Command) {
                String[] fileNames = fileInNameArg.eval().split("\\s+");
                ArrayList<String> resolvedFileNames = new ArrayList<>();
                for (String filename : fileNames) {
                    if (!filename.isBlank()) {
                        resolvedFileNames.add(filename);
                    }
                }
                if (resolvedFileNames.size() == 0) {
                    throw new ShellException("no file is provided for IO redirection");
                } else if (fileNames.length > 1) {
                    throw new ShellException("multiple files are provided for IO redirection");
                }
                return Optional.of(resolvedFileNames.get(0));
            }

            return Optional.of(resolvedName);
        }
        return Optional.empty();
    }

    @Override
    public void visit(Call call) {
        ArrayList<String> args = evalArgs(call);
        if (args.isEmpty()) {
            return;
        }

        Application app = ApplicationFactory.getApplication(args.remove(0));
        Optional<String> fileInName = evalRedirection(call.fileIn_);
        Optional<String> fileOutName = evalRedirection(call.fileOut_);
        boolean fileInEmpty = fileInName.isEmpty();
        boolean fileOutEmpty = fileOutName.isEmpty();

        if (fileInEmpty && fileOutEmpty) {
            app.exec(args, in_, out_, env_);
            return;
        }

        if (!fileInEmpty && fileOutEmpty) {
            try (FileInAdaptor in = new FileInAdaptor(
                    env_.getProperty("user.dir") + File.separator + fileInName.get());) {
                app.exec(args, in, out_, env_);
            }
            return;
        }

        if (fileInEmpty && !fileOutEmpty) {
            try (FileOutAdaptor out = new FileOutAdaptor(
                    env_.getProperty("user.dir") + File.separator + fileOutName.get());) {
                app.exec(args, in_, out, env_);
            }
            return;
        }

        try (FileInAdaptor in = new FileInAdaptor(env_.getProperty("user.dir") + File.separator + fileInName.get());
                FileOutAdaptor out = new FileOutAdaptor(
                        env_.getProperty("user.dir") + File.separator + fileOutName.get());) {
            app.exec(args, in, out, env_);
        }
    }

    @Override
    public void visit(Pipe pipe) {
        OutputDevice backupOut = out_;
        DequeAdaptor out = new DequeAdaptor();
        out_ = out;
        pipe.left_.accept(this);

        in_ = out;
        out_ = backupOut;
        pipe.right_.accept(this);
    }

    @Override
    public void visit(Seq seq) {
        seq.left_.accept(this);
        if (seq.right_.isPresent()) {
            Command right = seq.right_.get();
            right.accept(this);
        }
    }
}
