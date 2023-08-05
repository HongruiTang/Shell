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

import static org.junit.Assert.*;

public class GrepTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("grep");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void givenTooLessArgs_shouldThrowException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Grep grep = new Grep();
            ArrayList<String> args = new ArrayList<>();
            grep.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });

        String expectedMessage = "grep: wrong number of arguments";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenOnlyOneArg_shouldGrepFromStdin() {
        DequeAdaptor in = new DequeAdaptor();
        String lineSeparator = System.getProperty("line.separator");
        for (int i = 0; i < 10; ++i) {
            String line = i + lineSeparator;
            in.write(line);
        }
        in.write("some_pattern" + lineSeparator);
        in.write("some_pattern_abcd" + lineSeparator);
        in.write("efgh" + lineSeparator);

        DequeAdaptor out = new DequeAdaptor();
        Grep grep = new Grep();
        ArrayList<String> args = new ArrayList<>();
        args.add("some_pattern");
        grep.exec(args, in, out, env);

        assertEquals("some_pattern", out.readline());
        assertEquals("some_pattern_abcd", out.readline());
    }

    @Test
    public void givenNonExistFileName_shouldThrowException() {
        String dirString = "non_exist_file.txt";

        Exception exception = assertThrows(AppException.class, () -> {
            Grep grep = new Grep();
            ArrayList<String> args = new ArrayList<>();
            args.add("some_pattern");
            args.add(dirString);
            grep.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });

        String expectedMessage = "grep: wrong file argument";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    public ArrayList<String> getFileContent() {
        ArrayList<String> content = new ArrayList<>();

        content.add("some_pattern" + System.getProperty("line.separator"));
        content.add("abcd_some_pattern" + System.getProperty("line.separator"));
        content.add("some_pattern_efgh" + System.getProperty("line.separator"));
        content.add("ijkl" + System.getProperty("line.separator"));

        return content;
    }

    @Test
    public void givenPatternAndOneFile_shouldGrepTheFindingLines() throws IOException {
        Grep grep = new Grep();
        DequeAdaptor out = new DequeAdaptor();

        String filename = "test.txt";
        FileUtil.createFile(currentDirectory, filename);
        ArrayList<String> content = getFileContent();
        FileUtil.populateFile(currentDirectory, filename, content);

        ArrayList<String> result = new ArrayList<>();
        result.add("some_pattern" + System.getProperty("line.separator"));
        result.add("abcd_some_pattern" + System.getProperty("line.separator"));
        result.add("some_pattern_efgh" + System.getProperty("line.separator"));

        ArrayList<String> args = new ArrayList<>();
        args.add("some_pattern");
        args.add(filename);
        grep.exec(args, new DequeAdaptor(), out, env);

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).stripTrailing(), out.readline());
        }

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Test
    public void givenPatternAndMultipleFiles_shouldGrepTheFindingLinesFromEachFileWithPrefix() throws IOException {
        Grep grep = new Grep();
        DequeAdaptor out = new DequeAdaptor();

        String filename1 = "test1.txt";
        String filename2 = "test2.txt";
        FileUtil.createFile(currentDirectory, filename1);
        FileUtil.createFile(currentDirectory, filename2);

        ArrayList<String> content = getFileContent();
        FileUtil.populateFile(currentDirectory, filename1, content);
        FileUtil.populateFile(currentDirectory, filename2, content);

        ArrayList<String> result = new ArrayList<>();
        result.add("test1.txt:" + "some_pattern"
                + System.getProperty("line.separator"));
        result.add("test1.txt:abcd_" + "some_pattern"
                + System.getProperty("line.separator"));
        result.add("test1.txt:" + "some_pattern" + "_efgh"
                + System.getProperty("line.separator"));
        result.add("test2.txt:" + "some_pattern"
                + System.getProperty("line.separator"));
        result.add("test2.txt:abcd_" + "some_pattern"
                + System.getProperty("line.separator"));
        result.add("test2.txt:" + "some_pattern" + "_efgh"
                + System.getProperty("line.separator"));

        ArrayList<String> args = new ArrayList<>();
        args.add("some_pattern");
        args.add(filename1);
        args.add(filename2);

        grep.exec(args, new DequeAdaptor(), out, env);

        FileUtil.removeFile(currentDirectory, filename1);
        FileUtil.removeFile(currentDirectory, filename2);

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).stripTrailing(), out.readline());
        }
    }

    @Test
    public void givenRegexPatternAndOneFile_shouldGrepTheFindingLines() throws IOException {
        Grep grep = new Grep();
        DequeAdaptor out = new DequeAdaptor();

        String filename = "test.txt";
        FileUtil.createFile(currentDirectory, filename);
        ArrayList<String> content = getFileContent();
        FileUtil.populateFile(currentDirectory, filename, content);

        ArrayList<String> result = new ArrayList<>();
        result.add("so" + "me_pattern"
                + System.getProperty("line.separator"));
        result.add("abcd_" + "so" + "me_pattern"
                + System.getProperty("line.separator"));
        result.add("so" + "me_pattern_efgh"
                + System.getProperty("line.separator"));

        ArrayList<String> args = new ArrayList<>();
        args.add("so*");
        args.add(filename);
        grep.exec(args, new DequeAdaptor(), out, env);

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).stripTrailing(), out.readline());
        }

        FileUtil.removeFile(currentDirectory, filename);
    }
}
