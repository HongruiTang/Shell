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
import java.util.Random;

import static org.junit.Assert.*;

public class HeadTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("head");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void givenTooManyArgs_shouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Head head = new Head();
            ArrayList<String> args = new ArrayList<>();
            args.add(" ");
            args.add(" ");
            args.add(" ");
            args.add(" ");
            head.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "head: wrong arguments";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenIncorrectFlagAndRightNumAndFileName_shouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Head head = new Head();
            ArrayList<String> args = new ArrayList<>();
            args.add("-h");
            args.add("1");
            args.add("hello.txt");
            head.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "head: wrong argument -h";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenIncorrectFlagAndWrongFileName_shouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Head head = new Head();
            ArrayList<String> args = new ArrayList<>();
            args.add("-s");
            args.add("1");
            args.add("non-exist.txt");
            head.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "head: wrong argument -s";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenRightFlagButNonIntAsSecondArg_shouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Head head = new Head();
            ArrayList<String> args = new ArrayList<>();
            args.add("-n");
            args.add("abcd");
            args.add("hello.txt");
            head.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "head: wrong argument abcd";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenNegativeInt_shouldThrowException() {
        Exception exception = assertThrows(AppException.class, () -> {
            ArrayList<String> args = new ArrayList<>();
            args.add("-n");
            args.add("-1");

            DequeAdaptor in = new DequeAdaptor();
            in.write("abcd" + System.getProperty("line.separator"));
            in.write("efgh" + System.getProperty("line.separator"));

            DequeAdaptor out = new DequeAdaptor();
            Head head = new Head();
            head.exec(args, in, out, env);
        });

        String expectedMessage = "head: illegal line count -- -1";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void givenZeroAsLineCountArg_shouldThrowException() {
        ArrayList<String> args = new ArrayList<>();
        args.add("-n");
        args.add("0");

        DequeAdaptor in = new DequeAdaptor();
        in.write("abcd" + System.getProperty("line.separator"));
        in.write("efgh" + System.getProperty("line.separator"));

        DequeAdaptor out = new DequeAdaptor();
        Head head = new Head();
        head.exec(args, in, out, env);

        assertTrue(out.inputEnded());
    }

    @Test
    public void givenNoExistingFile_shouldCauseException() {
        String dirString = "non_exist_file.txt";
        Exception exception = assertThrows(AppException.class, () -> {
            Head head = new Head();
            ArrayList<String> args = new ArrayList<>();
            args.add(dirString);
            head.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "head: non_exist_file.txt does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenOneArgInvalidFlag_shouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Head head = new Head();
            ArrayList<String> args = new ArrayList<>();
            args.add("-n");
            head.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "head: option requires an argument";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenNoArgs_thenShouldReadFromIn() {
        DequeAdaptor in = new DequeAdaptor();
        ArrayList<String> result = new ArrayList<>();
        String lineSeparator = System.getProperty("line.separator");
        for (int i = 0; i < 100; ++i) {
            String line = i + lineSeparator;
            in.write(line);
            result.add(line);
        }

        DequeAdaptor out = new DequeAdaptor();
        Head head = new Head();
        head.exec(new ArrayList<>(), in, out, env);

        for (int i = 0; i < Integer.min(result.size(), 10); i++) {
            assertEquals(result.get(i).stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void givenTwoArgs_thenShouldReadFromIn() {
        DequeAdaptor in = new DequeAdaptor();
        ArrayList<String> result = new ArrayList<>();
        String lineSeparator = System.getProperty("line.separator");
        for (int i = 0; i < 100; ++i) {
            String line = Integer.toString(i) + lineSeparator;
            in.write(line);
            result.add(line);
        }

        int linesToRead = 20;
        ArrayList<String> args = new ArrayList<>();
        args.add("-n");
        args.add(Integer.toString(linesToRead));

        DequeAdaptor out = new DequeAdaptor();
        Head head = new Head();
        head.exec(args, in, out, env);

        for (int i = 0; i < Integer.min(result.size(), linesToRead); i++) {
            assertEquals(result.get(i).stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    public ArrayList<String> populateFileContent() {
        ArrayList<String> content = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 100; ++i) {
            if (random.nextBoolean()) {
                content.add(i + System.getProperty("line.separator"));
            }
        }

        return content;
    }

    @Test
    public void givenAFile_shouldOutputNoMoreThanTenLinesOfFiles() throws Exception {
        Head head = new Head();

        String filename = "no-this-file.txt";
        ArrayList<String> content = populateFileContent();
        ArrayList<String> args = new ArrayList<>();
        args.add(filename);
        FileUtil.createFile(currentDirectory, filename);
        FileUtil.populateFile(currentDirectory, filename, content);

        DequeAdaptor out = new DequeAdaptor();
        head.exec(args, new DequeAdaptor(), out, env);

        for (int i = 0; i < Integer.min(content.size(), 10); i++) {
            assertEquals(content.get(i).stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Test
    public void givenFile_shouldReadCorrectNumberOfLines() throws Exception {
        String filename = "no-this-file.txt";

        for (int i = 1; i < 100; i++) {
            ArrayList<String> args = new ArrayList<>();
            args.add("-n");
            args.add(Integer.toString(i));
            args.add(filename);

            FileUtil.createFile(currentDirectory, filename);

            Head head = new Head();
            DequeAdaptor out = new DequeAdaptor();

            ArrayList<String> content = populateFileContent();
            FileUtil.populateFile(currentDirectory, filename, content);

            head.exec(args, new DequeAdaptor(), out, env);
            int contentLength = Integer.min(content.size(), i);
            for (int j = 0; j < contentLength; j++) {
                assertEquals(content.get(j).stripTrailing(), out.readline());
            }

            assertThrows(NoSuchElementException.class, () -> {
                out.readline();
            });
        }

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Test
    public void givenFileAndReadZeroLine_outputShouldThrowException() throws Exception {
        String filename = "no-this-file.txt";

        ArrayList<String> args = new ArrayList<>();
        args.add("-n");
        args.add("0");
        args.add(filename);
        FileUtil.createFile(currentDirectory, filename);

        Head head = new Head();
        DequeAdaptor out = new DequeAdaptor();
        head.exec(args, new DequeAdaptor(), out, env);

        FileUtil.removeFile(currentDirectory, filename);

        assertTrue(out.inputEnded());
    }

    @Test
    public void givenFileAndReadNegativeLine_outputShouldThrowException() throws Exception {
        String filename = "no-this-file.txt";
        Exception exception = assertThrows(AppException.class, () -> {
            FileUtil.createFile(currentDirectory, filename);

            ArrayList<String> args = new ArrayList<>();
            args.add("-n");
            args.add("-2");
            args.add(filename);

            Head head = new Head();
            DequeAdaptor out = new DequeAdaptor();
            head.exec(args, new DequeAdaptor(), out, env);
        });

        FileUtil.createFile(currentDirectory, filename);

        String expectedMessage = "head: illegal line count -- -2";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
