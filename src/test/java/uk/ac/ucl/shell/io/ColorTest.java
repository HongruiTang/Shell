package uk.ac.ucl.shell.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.io.OutputDevice.Color;
import uk.ac.ucl.shell.util.FileUtil;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ColorTest {

    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("filein");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void StdoutAdaptorShouldOutputColor() {
        StdoutAdaptor outAdaptor = new StdoutAdaptor();

        outAdaptor.setColor(Color.BLUE);
        String commandLineOne = "hello";
        outAdaptor.write(commandLineOne);
        outAdaptor.setColor(Color.RESET);

        outAdaptor.setColor(Color.RED);
        String commandLineTwo = " world";
        outAdaptor.write(commandLineTwo);
        outAdaptor.setColor(Color.RESET);
    }

    @Test
    public void FileOutAdaptorShouldIgnoreColor() throws IOException {
        FileUtil.createFile(currentDirectory, "output.txt");

        try (FileOutAdaptor out = new FileOutAdaptor(currentDirectory + File.separator + "output.txt");) {
            out.setColor(Color.BLUE);
            out.write("hello world");
            out.setColor(Color.RESET);
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(currentDirectory + File.separator + "output.txt"));) {
            assertEquals("hello world", reader.readLine());
        }
    }

    @Test
    public void DequeAdaptorShouldIgnoreColor() {
        DequeAdaptor out = new DequeAdaptor();

        out.setColor(Color.BLUE);
        String command = "hello world";
        out.write(command);
        out.setColor(Color.RESET);

        assertEquals(command, out.readline());
        assertTrue(out.inputEnded());
    }

}
