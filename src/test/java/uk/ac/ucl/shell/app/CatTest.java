package uk.ac.ucl.shell.app;

import org.junit.After;
import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

public class CatTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("cat");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void shouldOutputEmptyIfNoFile() {
        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();
        Cat cat = new Cat();
        cat.exec(new ArrayList<>(), in, out, env);
        assertEquals(true, out.inputEnded());
    }

    @Test
    public void outputShouldMatchStdinIfNoFile() {
        DequeAdaptor in = new DequeAdaptor();
        ArrayList<String> result = new ArrayList<>();
        String lineSeparator = System.getProperty("line.separator");
        for (int i = 0; i < 100; ++i) {
            String line = Integer.toString(i) + lineSeparator;
            in.write(line);
            result.add(line);
        }

        DequeAdaptor out = new DequeAdaptor();
        Cat cat = new Cat();
        cat.exec(new ArrayList<>(), in, out, env);

        for (String s : result) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void shouldThrowIfNoFile() {
        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();

        String nonExistFile = "non-exist.txt";
        ArrayList<String> args = new ArrayList<>();
        args.add(nonExistFile);

        Cat cat = new Cat();
        Exception exception = assertThrows(AppException.class, () -> {
            cat.exec(args, in, out, env);
        });

        String expectedMessage = "cat: file does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    private String generateFileContent() {
        StringBuffer content = new StringBuffer();
        Random random = new Random();

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 100; ++i) {
            if (random.nextBoolean()) {
                buffer.append(Integer.toString(i));
                buffer.append(System.getProperty("line.separator"));
            }
        }
        content.append(buffer);

        return content.toString();
    }

    @Test
    public void shouldOutputFileContent() throws Exception {
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add("no-this-file-a.txt");
        filenames.add("no-this-file-b.txt");
        filenames.add("no-this-file-c.txt");
        FileUtil.createFiles(currentDirectory, filenames);

        StringBuffer content = new StringBuffer();
        for (String filename : filenames) {
            String contentForFile = generateFileContent();
            content.append(contentForFile);
            FileUtil.populateFile(currentDirectory, filename, contentForFile);
        }

        Cat cat = new Cat();
        DequeAdaptor out = new DequeAdaptor();
        cat.exec(filenames, new DequeAdaptor(), out, env);

        String lineSeparator = System.getProperty("line.separator");
        StringBuffer actual = new StringBuffer();
        while (!out.inputEnded()) {
            actual.append(out.readline());
            actual.append(lineSeparator);
        }

        assertEquals(content.toString(), actual.toString());

        FileUtil.removeFiles(currentDirectory, filenames);
    }

    @Test
    public void shouldOutputFileContentWhenHasBothFileAndStdinAsArguments() throws Exception {
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add("no-this-file-a.txt");
        filenames.add("no-this-file-b.txt");
        filenames.add("no-this-file-c.txt");
        FileUtil.createFiles(currentDirectory, filenames);

        StringBuffer content = new StringBuffer();
        for (String filename : filenames) {
            String contentForFile = generateFileContent();
            content.append(contentForFile);
            FileUtil.populateFile(currentDirectory, filename, contentForFile);
        }

        Cat cat = new Cat();
        DequeAdaptor out = new DequeAdaptor();
        DequeAdaptor in = new DequeAdaptor();
        in.write("abcd");
        in.write("efghijl");
        cat.exec(filenames, in, out, env);

        String lineSeparator = System.getProperty("line.separator");
        StringBuffer actual = new StringBuffer();
        while (!out.inputEnded()) {
            actual.append(out.readline() + lineSeparator);
        }

        assertEquals(content.toString(), actual.toString());

        FileUtil.removeFiles(currentDirectory, filenames);
    }
}
