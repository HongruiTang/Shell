package uk.ac.ucl.shell.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StdoutAdaptorTest {
    PrintStream stdout;
    ByteArrayOutputStream out;

    @Before
    public void setStdout() {
        stdout = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @After
    public void clearStdout() throws IOException {
        out.close();
        System.setOut(stdout);
    }

    @Test
    public void contentShouldPrintToStdout() throws IOException {
        StdoutAdaptor outAdaptor = new StdoutAdaptor();
        outAdaptor.write("123456789");
        outAdaptor.write("\n");
        outAdaptor.write("abcdefg");

        String result = out.toString();
        assertEquals("123456789\nabcdefg", result);
    }

}
