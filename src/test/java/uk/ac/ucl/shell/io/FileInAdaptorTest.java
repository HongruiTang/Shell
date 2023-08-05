package uk.ac.ucl.shell.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.ShellException;
import uk.ac.ucl.shell.util.FileUtil;

import static org.junit.Assert.*;

public class FileInAdaptorTest {
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

    public ArrayList<String> getContent() {
        ArrayList<String> content = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            content.add(Integer.toString(i) + System.getProperty("line.separator"));
        }
        return content;
    }

    @Test
    public void FileInAdaptorShouldThrowIfFileDoesNotExist() throws IOException {
        Exception exception = assertThrows(ShellException.class, () -> {
            new FileInAdaptor(new File(currentDirectory, "input.txt").toString());
        });

        String actualMessage = "No such file or directory";
        assertTrue(exception.getMessage().contains(actualMessage));
    }

    @Test
    public void FileInAdaptorShouldReadAllLines() throws IOException {
        ArrayList<String> content = getContent();
        FileUtil.createFile(currentDirectory, "input.txt");
        FileUtil.populateFile(currentDirectory, "input.txt", content);

        FileInAdaptor in = new FileInAdaptor(new File(currentDirectory, "input.txt").toString());

        for (String line : content) {
            assertTrue(!in.inputEnded());
            assertEquals(line.stripTrailing(), in.readline());
        }

        assertTrue(in.inputEnded());

        in.close();
    }
}
