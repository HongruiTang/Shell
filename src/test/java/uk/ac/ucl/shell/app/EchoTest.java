package uk.ac.ucl.shell.app;

import uk.ac.ucl.shell.Shell;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.ucl.shell.io.DequeAdaptor;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class EchoTest {
    Shell.Env env;

    @Before
    public void setCurrentDirectory() {
        env = new Shell().getEnv();
    }

    @Test
    public void outputShouldMatchInput() throws Exception {
        Echo echo = new Echo();
        ArrayList<String> args = new ArrayList<>();
        args.add("Hello");
        args.add("World");
        DequeAdaptor out = new DequeAdaptor();
        echo.exec(args, new DequeAdaptor(), out, env);

        String line = out.readline();
        String expectedOutput = "Hello World";

        assertEquals(expectedOutput, line);

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void emptyInputTest() throws Exception {
        Echo echo = new Echo();
        ArrayList<String> args = new ArrayList<>();
        DequeAdaptor out = new DequeAdaptor();
        echo.exec(args, new DequeAdaptor(), out, env);
        String line = out.readline();
        String expectedOutput = "";

        assertEquals(expectedOutput, line);

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

}
