package uk.ac.ucl.shell.visitor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.ast.argument.AbstractArg;
import uk.ac.ucl.shell.ast.argument.CommandArg;
import uk.ac.ucl.shell.ast.argument.PlainArg;
import uk.ac.ucl.shell.ast.command.Call;
import uk.ac.ucl.shell.exception.ShellException;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;

public class ExecVisitorTest {
    private ExecVisitor visitor;
    private Shell.Env env;
    DequeAdaptor out;
    String currentDirectory;

    @Before
    public void setUp() throws IOException {
        DequeAdaptor in = new DequeAdaptor();
        out = new DequeAdaptor();
        env = new Shell().getEnv();
        visitor = new ExecVisitor(in, out, env);
        currentDirectory = FileUtil.createTempDirectory("exec");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void cleanUp() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    public ArrayList<String> populateFileContent() {
        ArrayList<String> content = new ArrayList<>();
        content.add("hello");
        return content;
    }

    @Test
    public void testCallWithFileOut() throws IOException {
        ArrayList<AbstractArg> args = new ArrayList<>();
        PlainArg fst = new PlainArg("echo");
        PlainArg snd = new PlainArg(" ");
        PlainArg thd = new PlainArg("hello");

        args.add(fst);
        args.add(snd);
        args.add(thd);

        PlainArg fileName = new PlainArg("out.txt");
        Optional<AbstractArg> inFile = Optional.empty();
        Optional<AbstractArg> outFile = Optional.of(fileName);

        Call call = new Call(args, inFile, outFile);
        call.accept(visitor);

        File outPutFile = new File(env.getProperty("user.dir"), "out.txt");
        BufferedReader reader = new BufferedReader(new FileReader(outPutFile));
        assertEquals("hello", reader.readLine());
        reader.close();
    }

    @Test
    public void testCallWithFileIn() throws IOException {
        ArrayList<AbstractArg> args = new ArrayList<>();
        PlainArg fst = new PlainArg("cat");
        PlainArg snd = new PlainArg(" ");

        args.add(fst);
        args.add(snd);

        String filename = "input.txt";
        FileUtil.createFile(currentDirectory, filename);
        ArrayList<String> content = populateFileContent();
        FileUtil.populateFile(currentDirectory, filename, content);

        PlainArg fileName = new PlainArg(filename);
        Optional<AbstractArg> inFile = Optional.of(fileName);
        Optional<AbstractArg> outFile = Optional.empty();

        Call call = new Call(args, inFile, outFile);
        call.accept(visitor);

        assertEquals("hello", out.readline());

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Test
    public void testCallWithFileInAndOut() throws IOException {
        ArrayList<AbstractArg> args = new ArrayList<>();
        PlainArg fst = new PlainArg("cat");
        PlainArg snd = new PlainArg(" ");

        args.add(fst);
        args.add(snd);

        String filenameIn = "input.txt";
        FileUtil.createFile(currentDirectory, filenameIn);
        ArrayList<String> content = populateFileContent();
        FileUtil.populateFile(currentDirectory, filenameIn, content);

        PlainArg fileInName = new PlainArg(filenameIn);
        PlainArg fileOutName = new PlainArg("out.txt");
        Optional<AbstractArg> inFile = Optional.of(fileInName);
        Optional<AbstractArg> outFile = Optional.of(fileOutName);

        Call call = new Call(args, inFile, outFile);
        call.accept(visitor);

        File outPutFile = new File(env.getProperty("user.dir"), "out.txt");
        BufferedReader reader = new BufferedReader(new FileReader(outPutFile));
        assertEquals("hello", reader.readLine());
        reader.close();

        FileUtil.removeFile(currentDirectory, filenameIn);
    }

    @Test
    public void testCallWithEmptyArg() throws IOException {
        ArrayList<AbstractArg> args = new ArrayList<>();

        Optional<AbstractArg> inFile = Optional.empty();
        Optional<AbstractArg> outFile = Optional.empty();

        Call call = new Call(args, inFile, outFile);
        call.accept(visitor);

        assertTrue(out.inputEnded());
    }

    public void callWithGlobGivenEmptyFolder() throws IOException {
        ArrayList<AbstractArg> args = new ArrayList<>();
        PlainArg fst = new PlainArg("echo");
        PlainArg snd = new PlainArg(" ");
        PlainArg third = new PlainArg("*.txt");
        args.add(fst);
        args.add(snd);
        args.add(third);

        Call call = new Call(args, Optional.empty(), Optional.empty());
        call.accept(visitor);

        assertEquals("*.txt", out.readline());
    }

    @Test
    public void callWithGlobGivenFile() throws IOException {
        ArrayList<AbstractArg> args = new ArrayList<>();
        PlainArg fst = new PlainArg("echo");
        PlainArg snd = new PlainArg(" ");
        PlainArg third = new PlainArg("*.txt");
        args.add(fst);
        args.add(snd);
        args.add(third);

        FileUtil.createFile(currentDirectory, "input.txt");

        Call call = new Call(args, Optional.empty(), Optional.empty());
        call.accept(visitor);

        assertEquals("input.txt", out.readline());
    }

    @Test
    public void callWithRedirectionGivenNoFile() throws IOException {
        ArrayList<AbstractArg> args = new ArrayList<>();
        PlainArg fst = new PlainArg("cat");
        PlainArg snd = new PlainArg(" ");
        args.add(fst);
        args.add(snd);

        CommandArg fileInName = new CommandArg("    ", env);
        Optional<AbstractArg> inFile = Optional.of(fileInName);
        Call call = new Call(args, inFile, Optional.empty());

        Exception exception = assertThrows(ShellException.class, () -> {
            call.accept(visitor);
        });

        String actualMessage = "no file is provided for IO redirection";
        assertTrue(exception.getMessage().contains(actualMessage));
    }

    @Test
    public void callWithRedirectionGivenTooMuchFiles() throws IOException {
        ArrayList<AbstractArg> args = new ArrayList<>();
        PlainArg fst = new PlainArg("cat");
        PlainArg snd = new PlainArg(" ");
        args.add(fst);
        args.add(snd);

        CommandArg fileInName = new CommandArg("echo hello world", env);
        Optional<AbstractArg> inFile = Optional.of(fileInName);
        Call call = new Call(args, inFile, Optional.empty());

        Exception exception = assertThrows(ShellException.class, () -> {
            call.accept(visitor);
        });

        String actualMessage = "multiple files are provided for IO redirection";
        assertTrue(exception.getMessage().contains(actualMessage));
    }

    @Test
    public void callWithRedirectionGivenOneFile() throws IOException {
        ArrayList<AbstractArg> args = new ArrayList<>();
        PlainArg fst = new PlainArg("cat");
        PlainArg snd = new PlainArg(" ");
        args.add(fst);
        args.add(snd);

        String filename = "input.txt";
        FileUtil.createFile(currentDirectory, filename);
        ArrayList<String> content = populateFileContent();
        FileUtil.populateFile(currentDirectory, filename, content);

        CommandArg fileName = new CommandArg("echo input.txt", env);
        Optional<AbstractArg> inFile = Optional.of(fileName);

        Call call = new Call(args, inFile, Optional.empty());
        call.accept(visitor);

        assertEquals("hello", out.readline());
    }

}