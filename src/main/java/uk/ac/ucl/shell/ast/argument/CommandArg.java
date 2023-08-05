package uk.ac.ucl.shell.ast.argument;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.io.DequeAdaptor;

public class CommandArg extends AbstractArg {
    String command_;

    // Command Substitution is related to Shell Env
    // New command is executed in a new Shell Env
    //
    // Some states (e.g. User Dir) of the new Shell Env
    // is determined by the old Shell Env
    Shell.Env env_;

    public CommandArg(String command, Shell.Env env) {
        super(Type.Command);
        this.command_ = command;
        this.env_ = env;
    }

    @Override
    public String eval() {
        if (command_.isBlank()) {
            return "";
        }

        // eval in new shell
        Shell shell = new Shell(env_);
        DequeAdaptor out = new DequeAdaptor();
        shell.eval(command_, out);

        StringBuffer sbuf = new StringBuffer();
        while (!out.inputEnded()) {
            sbuf.append(out.readline());
            sbuf.append(System.getProperty("line.separator"));
        }

        String newline = System.getProperty("line.separator");
        String result = sbuf.toString();

        while (result.endsWith(newline)) {
            result = result.substring(0, result.length() - newline.length());
        }
        return result;
    }
}
