package uk.ac.ucl.shell.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class UniqTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("uniq");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void givenTooManyArgs_shouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Uniq uniq = new Uniq();
            ArrayList<String> args = new ArrayList<>();
            args.add(" ");
            args.add(" ");
            args.add(" ");
            uniq.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "uniq: wrong arguments";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenNoExistingFile_shouldCauseException() {
        String dirString = "non_exist_file.txt";
        Exception exception = assertThrows(AppException.class, () -> {
            Uniq uniq = new Uniq();
            ArrayList<String> args = new ArrayList<>();
            args.add(dirString);
            uniq.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "uniq: non_exist_file.txt does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenZeroArg_shouldReadFromStdin() {
        String lineSeparator = System.getProperty("line.separator");
        DequeAdaptor in = new DequeAdaptor();
        for (int i = 0; i < 5; ++i) {
            String line = "this is a testing line" + lineSeparator;
            in.write(line);
        }
        in.write("not a line" + lineSeparator);
        in.write("this is a testing line" + lineSeparator);

        ArrayList<String> args = new ArrayList<>();
        DequeAdaptor out = new DequeAdaptor();
        Uniq uniq = new Uniq();
        uniq.exec(args, in, out, env);

        ArrayList<String> result = new ArrayList<>();
        result.add("this is a testing line" + lineSeparator);
        result.add("not a line" + lineSeparator);
        result.add("this is a testing line" + lineSeparator);

        for (String s : result) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void givenEmptyInput_shouldOutputNothing() {
        ArrayList<String> args = new ArrayList<>();
        DequeAdaptor out = new DequeAdaptor();

        Uniq uniq = new Uniq();
        uniq.exec(args, new DequeAdaptor(), out, env);

        assertTrue(out.inputEnded());

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    public ArrayList<String> populateFileContent() {
        ArrayList<String> content = new ArrayList<>();

        content.add("This is a testing line" + System.getProperty("line.separator"));

        for (int i = 0; i < 5; i++) {
            content.add("this is a testing line" + System.getProperty("line.separator"));
        }

        content.add("This is a testing line" + System.getProperty("line.separator"));

        for (int i = 0; i < 5; i++) {
            content.add("this is a testing line" + System.getProperty("line.separator"));
        }

        content.add("not a line" + System.getProperty("line.separator"));
        content.add("this is a testing line" + System.getProperty("line.separator"));

        return content;
    }

    @Test
    public void givenOneArgInvalidFlag_shouldReadFromFile() throws IOException {
        String lineSeparator = System.getProperty("line.separator");
        DequeAdaptor in = new DequeAdaptor();
        for (int i = 0; i < 5; ++i) {
            String line = "this is a testing line" + lineSeparator;
            in.write(line);
        }
        in.write("This is a testing line" + lineSeparator);
        in.write("this is a testing line" + lineSeparator);
        in.write("not a line" + lineSeparator);
        in.write("this is a testing line" + lineSeparator);

        ArrayList<String> args = new ArrayList<>();
        args.add("-i");
        DequeAdaptor out = new DequeAdaptor();
        Uniq uniq = new Uniq();
        uniq.exec(args, in, out, env);

        ArrayList<String> result = new ArrayList<>();
        result.add("this is a testing line" + lineSeparator);
        result.add("not a line" + lineSeparator);
        result.add("this is a testing line" + lineSeparator);

        for (String s : result) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void givenOneArgAsFilename_shouldReadFromFile() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        DequeAdaptor in = new DequeAdaptor();
        Uniq uniq = new Uniq();

        String lineSeparator = System.getProperty("line.separator");
        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);

        ArrayList<String> content = populateFileContent();
        FileUtil.populateFile(currentDirectory, filename, content);

        ArrayList<String> args = new ArrayList<>();
        args.add(filename);
        uniq.exec(args, in, out, env);

        ArrayList<String> result = new ArrayList<>();
        result.add("This is a testing line" + lineSeparator);
        result.add("this is a testing line" + lineSeparator);
        result.add("This is a testing line" + lineSeparator);
        result.add("this is a testing line" + lineSeparator);
        result.add("not a line" + lineSeparator);
        result.add("this is a testing line" + lineSeparator);

        for (String s : result) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Test
    public void givenTwoArgAsDashIAndFilename_shouldBeCaseInsensitive() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        DequeAdaptor in = new DequeAdaptor();
        Uniq uniq = new Uniq();

        String lineSeparator = System.getProperty("line.separator");
        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);
        ArrayList<String> content = populateFileContent();
        FileUtil.populateFile(currentDirectory, filename, content);

        ArrayList<String> args = new ArrayList<>();
        args.add("-i");
        args.add(filename);

        uniq.exec(args, in, out, env);

        ArrayList<String> result = new ArrayList<>();
        result.add("This is a testing line" + lineSeparator);
        result.add("not a line" + lineSeparator);
        result.add("this is a testing line" + lineSeparator);

        for (String s : result) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Test
    public void givenInvalidFlag_shouldThrowException() throws IOException {
        String filename = "no-this-file.txt";
        Exception exception = assertThrows(AppException.class, () -> {
            DequeAdaptor out = new DequeAdaptor();
            DequeAdaptor in = new DequeAdaptor();
            Uniq uniq = new Uniq();

            FileUtil.createFile(currentDirectory, filename);
            populateFileContent();
            FileUtil.populateFile(currentDirectory, filename, populateFileContent());

            ArrayList<String> args = new ArrayList<>();
            args.add("-h");
            args.add("no-this-file.txt");

            uniq.exec(args, in, out, env);
        });

        FileUtil.removeFile(currentDirectory, filename);

        String expectedMessage = "uniq: wrong argument -h";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
