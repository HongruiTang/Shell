package uk.ac.ucl.shell.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class UnsafeDecoratorTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("unsafeDecorator");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void unsafeCdShouldPrintExceptionGivenNoArg() {
        DequeAdaptor out = new DequeAdaptor();
        UnsafeDecorator unsafeCd = new UnsafeDecorator(new Cd());
        unsafeCd.exec(new ArrayList<>(), new DequeAdaptor(), out, env);

        String expectedMessage = "cd: missing argument";
        String actualMessage = out.readline();
        assertEquals(expectedMessage, actualMessage);

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void givenMoreThanOneDirectory_whenUsingAppCd_thenThrowException() {
        DequeAdaptor out = new DequeAdaptor();
        UnsafeDecorator unsafeCd = new UnsafeDecorator(new Cd());
        ArrayList<String> args = new ArrayList<>();
        args.add(" ");
        args.add(" ");
        args.add(" ");
        unsafeCd.exec(args, new DequeAdaptor(), out, env);

        String expectedMessage = "cd: too many arguments";
        String actualMessage = out.readline();
        assertEquals(expectedMessage, actualMessage);

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void outputShouldMatchUserDirectory() throws Exception {
        UnsafeDecorator unsafePwd = new UnsafeDecorator(new Pwd());
        DequeAdaptor out = new DequeAdaptor();
        unsafePwd.exec(new ArrayList<>(), new DequeAdaptor(), out, env);
        String line = out.readline();
        assertEquals(currentDirectory, line);

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }
}
