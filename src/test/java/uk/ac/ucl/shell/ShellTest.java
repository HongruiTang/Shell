package uk.ac.ucl.shell;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ShellTest {
    InputStream stdin;
    PipedInputStream in;
    PipedOutputStream inWriteEnd;

    PrintStream stdout;
    ByteArrayOutputStream out;
    PrintStream stderr;
    ByteArrayOutputStream err;

    @Before
    public void setIO() throws IOException {
        stdin = System.in;
        in = new PipedInputStream();
        inWriteEnd = new PipedOutputStream();
        in.connect(inWriteEnd);
        System.setIn(in);

        stdout = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        stderr = System.err;
        err = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err));
    }

    @After
    public void resetIO() throws IOException {
        System.setIn(stdin);
        System.setOut(stdout);
        System.setErr(stderr);

        in.close();
        inWriteEnd.close();
        out.close();
        err.close();
    }

    @Test
    public void shellInNonInteractiveModeShouldOutputToStderrGivenNotEnoughArg() {
        String[] args = new String[1];
        args[0] = "-c";

        Shell.main(args);

        String actualOutput = "COMP0010 shell: wrong number of arguments";
        assertTrue(err.toString().contains(actualOutput));
    }

    @Test
    public void shellInNonInteractiveModeShouldOutputToStderrGivenInvalidArgument() {
        String[] args = new String[2];
        args[0] = "-d";
        args[1] = "echo hello world";

        Shell.main(args);

        String actualOutput = "COMP0010 shell: -d: unexpected argument";
        assertTrue(err.toString().contains(actualOutput));
    }

    @Test
    public void shellEvalInNonInteractiveModeShouldOutputToStdout() {
        String[] args = new String[2];
        args[0] = "-c";
        args[1] = "echo hello world";

        Shell.main(args);

        String actualMessage = "hello world";
        assertTrue(out.toString().contains(actualMessage));
    }

    @Test
    public void shellEvalInInteractiveModeShouldOutputToStdout() throws IOException {
        String[] args = new String[0];

        String command = "echo hello world";
        inWriteEnd.write(command.getBytes());
        inWriteEnd.close();

        Shell.main(args);

        String actualMessage = "hello world";
        assertTrue(out.toString().contains(actualMessage));
    }

    @Test
    public void shellShouldCatchShellExceptionAndOutputToStderr() throws IOException {
        String[] args = new String[0];

        String command = "no-exist-app";
        inWriteEnd.write(command.getBytes());
        inWriteEnd.close();

        Shell.main(args);

        String actualMessage = "COMP0010 shell: no existing application";
        assertTrue(err.toString().contains(actualMessage));
    }

    @Test
    public void shellShouldCatchAppExceptionAndOutputToStderr() throws IOException {
        String[] args = new String[0];

        String command = "cd too more args";
        inWriteEnd.write(command.getBytes());
        inWriteEnd.close();

        Shell.main(args);

        String actualMessage = "cd: too many arguments";
        assertTrue(err.toString().contains(actualMessage));
    }

    @Test
    public void spawnedShellShouldInheritParentShellDirectory() throws IOException {
        Shell sh = new Shell();
        sh.getEnv().setProperty("user.dir", "");
        Shell spawnedShell = new Shell(sh.getEnv());
        assertEquals("", spawnedShell.getEnv().getProperty("user.dir"));
    }

}
