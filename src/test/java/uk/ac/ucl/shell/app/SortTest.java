package uk.ac.ucl.shell.app;

import org.junit.After;
import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;

import org.junit.Test;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import org.junit.Before;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

public class SortTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("sort");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void tooManyArgsShouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Sort sort = new Sort();
            ArrayList<String> args = new ArrayList<>();
            args.add(" ");
            args.add(" ");
            args.add(" ");
            args.add(" ");
            sort.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "sort: wrong arguments";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void incorrectFlagShouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Sort sort = new Sort();
            ArrayList<String> args = new ArrayList<>();
            args.add("-f");
            args.add("somefile.txt");
            sort.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "sort: wrong argument -f";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void noExistingFileShouldCauseException() {
        String dirString = "non_exist_file.txt";
        Exception exception = assertThrows(AppException.class, () -> {
            Sort sort = new Sort();
            ArrayList<String> args = new ArrayList<>();
            args.add(dirString);
            sort.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "sort: non_exist_file.txt does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void noArgsShouldReadFromIn() {
        String lineSeparator = System.getProperty("line.separator");

        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> elems = new ArrayList<>();
        for (int i = 100; i < 999; ++i) {
            String line = Integer.toString(i) + lineSeparator;
            elems.add(line);
            result.add(line);
        }
        shuffle(elems);

        DequeAdaptor in = new DequeAdaptor();
        for (String s : elems) {
            in.write(s);
        }

        DequeAdaptor out = new DequeAdaptor();
        Sort sort = new Sort();
        sort.exec(new ArrayList<>(), in, out, env);

        for (String s : result) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void outputShouldBeReversedGivenReversedFlagWhenReadingFromIn() {
        String lineSeparator = System.getProperty("line.separator");

        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> elems = new ArrayList<>();
        for (int i = 100; i < 999; ++i) {
            String line = Integer.toString(i) + lineSeparator;
            elems.add(line);
            result.add(line);
        }

        Collections.shuffle(elems);

        DequeAdaptor in = new DequeAdaptor();
        for (String s : elems) {
            in.write(s);
        }

        ArrayList<String> args = new ArrayList<>();
        args.add("-r");
        DequeAdaptor out = new DequeAdaptor();
        Sort sort = new Sort();
        sort.exec(args, in, out, env);

        sort(result, reverseOrder());
        for (String s : result) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    private ArrayList<String> populateFileContent() throws IOException {
        ArrayList<String> content = new ArrayList<>();
        String lineSeparator = System.getProperty("line.separator");

        for (int i = 100; i < 999; ++i) {
            String line = Integer.toString(i) + lineSeparator;
            content.add(line);
        }

        return content;
    }

    @Test
    public void outputShouldReadFiles() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        Sort sort = new Sort();

        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);
        ArrayList<String> content = populateFileContent();
        ArrayList<String> elem = new ArrayList<>();

        for (String str : content) {
            elem.add(str);
        }
        Collections.shuffle(elem);

        FileUtil.populateFile(currentDirectory, filename, elem);

        ArrayList<String> args = new ArrayList<>();
        args.add(filename);
        sort.exec(args, new DequeAdaptor(), out, env);

        for (String s : content) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Test
    public void outputShouldBeReversedGivenReversedFlagWhenReadingFromFile() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        Sort sort = new Sort();

        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);

        ArrayList<String> content = populateFileContent();
        ArrayList<String> elem = new ArrayList<>();

        for (String str : content) {
            elem.add(str);
        }
        Collections.shuffle(elem);

        FileUtil.populateFile(currentDirectory, filename, elem);
        ArrayList<String> args = new ArrayList<>();
        args.add("-r");
        args.add(filename);

        sort.exec(args, new DequeAdaptor(), out, env);

        sort(content, reverseOrder());
        for (String s : content) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        FileUtil.removeFile(currentDirectory, filename);
    }
}
