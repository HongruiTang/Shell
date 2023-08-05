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

public class TailTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("tail");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void tooManyArgsShouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Tail tail = new Tail();
            ArrayList<String> args = new ArrayList<>();
            args.add(" ");
            args.add(" ");
            args.add(" ");
            args.add(" ");
            tail.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "tail: wrong arguments";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void incorrectFlagShouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Tail tail = new Tail();
            ArrayList<String> args = new ArrayList<>();
            args.add("-h");
            args.add("1");
            args.add("hello.txt");
            tail.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });

        String expectedMessage = "tail: wrong argument -h";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void optionWithoutArgumentShouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Tail tail = new Tail();
            ArrayList<String> args = new ArrayList<>();
            args.add("-n");
            tail.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });

        String expectedMessage = "tail: option requires an argument";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void parseNonIntShouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Tail tail = new Tail();
            ArrayList<String> args = new ArrayList<>();
            args.add("-n");
            args.add("abcd");
            args.add("hello.txt");
            tail.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });

        String expectedMessage = "tail: wrong argument abcd";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void receiveNegativeIntShouldThrowException() {
        ArrayList<String> args = new ArrayList<>();
        args.add("-n");
        args.add("-1");

        DequeAdaptor in = new DequeAdaptor();
        in.write("abcd" + System.getProperty("line.separator"));
        in.write("efgh" + System.getProperty("line.separator"));

        DequeAdaptor out = new DequeAdaptor();
        Tail tail = new Tail();

        Exception exception = assertThrows(AppException.class, () -> {
            tail.exec(args, in, out, env);
        });

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        String expectedMessage = "tail: illegal line count -1";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void noExistingFileShouldCauseException() {
        String dirString = "non_exist_file.txt";
        Exception exception = assertThrows(AppException.class, () -> {
            Tail tail = new Tail();
            ArrayList<String> args = new ArrayList<>();
            args.add(dirString);
            tail.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });

        String expectedMessage = "tail: non_exist_file.txt does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void noArgsShouldReadFromIn() {
        DequeAdaptor in = new DequeAdaptor();
        ArrayList<String> result = new ArrayList<>();
        String lineSeparator = System.getProperty("line.separator");
        for (int i = 0; i < 100; ++i) {
            String line = Integer.toString(i) + lineSeparator;
            in.write(line);
            result.add(line);
        }

        DequeAdaptor out = new DequeAdaptor();
        Tail tail = new Tail();
        tail.exec(new ArrayList<>(), in, out, env);

        for (int i = Integer.max(result.size() - 10, 0); i < result.size(); ++i) {
            assertEquals(result.get(i).stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void shouldReadFromInWhenGivenTwoArgs() {
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
        Tail tail = new Tail();
        tail.exec(args, in, out, env);

        for (int i = Integer.max(result.size() - linesToRead, 0); i < result.size(); ++i) {
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
                content.add(Integer.toString(i) + System.getProperty("line.separator"));
            }
        }

        return content;
    }

    @Test
    public void outputShouldReadFiles() throws Exception {
        Tail tail = new Tail();
        DequeAdaptor out = new DequeAdaptor();

        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);

        ArrayList<String> content = populateFileContent();
        FileUtil.populateFile(currentDirectory, filename, content);

        ArrayList<String> args = new ArrayList<>();
        args.add(filename);

        tail.exec(args, new DequeAdaptor(), out, env);

        for (int i = Integer.max(0, content.size() - 10); i < content.size(); ++i) {
            assertEquals(content.get(i).stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Test
    public void outputShouldReadCorrectNumberOfLines() throws Exception {
        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);

        ArrayList<String> content = populateFileContent();
        FileUtil.populateFile(currentDirectory, filename, content);

        for (int i = 0; i < 100; ++i) {
            ArrayList<String> args = new ArrayList<>();
            args.add("-n");
            args.add(Integer.toString(i));
            args.add(filename);

            Tail tail = new Tail();
            DequeAdaptor out = new DequeAdaptor();
            tail.exec(args, new DequeAdaptor(), out, env);

            for (int j = Integer.max(0, content.size() - i); j < content.size(); ++j) {
                assertEquals(content.get(j).stripTrailing(), out.readline());
            }

            assertThrows(NoSuchElementException.class, () -> {
                out.readline();
            });
        }

        FileUtil.removeFile(currentDirectory, filename);
    }
}
