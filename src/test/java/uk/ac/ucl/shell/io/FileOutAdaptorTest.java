package uk.ac.ucl.shell.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.util.FileUtil;

import static org.junit.Assert.*;

public class FileOutAdaptorTest {
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
    public void FileOutAdaptorShouldWriteAllLines() throws IOException {
        ArrayList<String> content = getContent();
        FileUtil.createFile(currentDirectory, "output.txt");

        try (FileOutAdaptor out = new FileOutAdaptor(currentDirectory + File.separator + "output.txt");) {
            for (String line : content) {
                out.write(line);
            }
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(currentDirectory + File.separator + "output.txt"));) {
            for (int i = 0; i < content.size(); ++i) {
                assertEquals(content.get(i).stripTrailing(), reader.readLine());
            }
        }
    }

}
