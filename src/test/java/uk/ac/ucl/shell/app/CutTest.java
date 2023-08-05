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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class CutTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("cut");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void OutputShouldBeTheSelectedBytesGivenOneLine() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String fileName = "somefile.txt";
        FileUtil.createFile(currentDirectory, fileName);
        String content = "123456789 123456789 123456789 123456789";
        FileUtil.populateFile(currentDirectory, fileName, content);

        String bytesRequired = "29-100,-3";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);
        args.add(fileName);

        cut.exec(args, new DequeAdaptor(), out, env);

        String expectedOut = "1239 123456789";
        FileUtil.removeFile(currentDirectory, fileName);

        assertEquals(expectedOut, out.readline());
    }

    @Test
    public void InputByteRequiredAsAbove30() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String fileName = "somefile.txt";

        FileUtil.createFile(currentDirectory, fileName);
        String content = "123456789";
        FileUtil.populateFile(currentDirectory, fileName, content);

        String bytesRequired = "5-";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);
        args.add(fileName);

        cut.exec(args, new DequeAdaptor(), out, env);

        String expectedOut = "56789";

        FileUtil.removeFile(currentDirectory, fileName);

        assertEquals(expectedOut, out.readline());
        assertTrue(out.inputEnded());
    }

    @Test
    public void InputByteRequiredAsOnlyOneValue() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String fileName = "somefile.txt";

        FileUtil.createFile(currentDirectory, fileName);
        String content = "123456789 123456789 123456789 123456789";
        FileUtil.populateFile(currentDirectory, fileName, content);

        String bytesRequired = "30";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);
        args.add(fileName);

        cut.exec(args, new DequeAdaptor(), out, env);

        String expectedOut = " ";

        FileUtil.removeFile(currentDirectory, fileName);

        assertEquals(expectedOut, out.readline());
    }

    @Test
    public void outputShouldNotContainDuplicatesGivenOverlappingInterval() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String fileName = "somefile.txt";

        FileUtil.createFile(currentDirectory, fileName);
        String content = "123456789";
        FileUtil.populateFile(currentDirectory, fileName, content);

        String bytesRequired = "3-6,4-8,4-5";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);
        args.add(fileName);

        cut.exec(args, new DequeAdaptor(), out, env);

        String expectedOut = "345678";

        FileUtil.removeFile(currentDirectory, fileName);

        assertEquals(expectedOut, out.readline());
        assertTrue(out.inputEnded());
    }

    @Test
    public void outputShouldProcessMultipleLines() throws IOException {
        String lineOne = "123456789";
        String lineTwo = "abcdefghi";
        String lineThree = "987654321";
        String bytesRequired = "3,5,7";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);

        DequeAdaptor in = new DequeAdaptor();
        in.write(lineOne + System.getProperty("line.separator"));
        in.write(lineTwo + System.getProperty("line.separator"));
        in.write(lineThree + System.getProperty("line.separator"));

        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();
        cut.exec(args, in, out, env);

        String expectedLineOne = "357";
        String expectedLineTwo = "ceg";
        String expectedLineThree = "753";

        assertEquals(expectedLineOne, out.readline());
        assertEquals(expectedLineTwo, out.readline());
        assertEquals(expectedLineThree, out.readline());
        assertTrue(out.inputEnded());
    }

    @Test
    public void OutputShouldBeStdInIfNoFile() {
        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String bytesRequired = "2-4";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);

        String stdStr = "123456789";
        in.write(stdStr);

        cut.exec(args, in, out, env);

        String expectedOut = "234";
        assertEquals(expectedOut, out.readline());
    }

    @Test
    public void TooManyArgumentsTest() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String bytesRequired = "29-100,-3";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);
        args.add("no-such-file.txt");
        args.add("another_arg");

        String expectedOut = "cut: too many arguments";

        AppException exception = assertThrows(
                AppException.class, () -> {
                    cut.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals(expectedOut, exception.getMessage());
    }

    @Test
    public void WrongArgumentTest() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String bytesRequired = "29-100,-3";

        ArrayList<String> args = new ArrayList<>();
        args.add(bytesRequired);
        args.add("no-such-file.txt");

        String expectedOut = "cut: wrong argument " + bytesRequired;

        AppException exception = assertThrows(
                AppException.class, () -> {
                    cut.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals(expectedOut, exception.getMessage());
    }

    @Test
    public void MissingArgumentTest() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        ArrayList<String> args = new ArrayList<>();
        args.add("no-this-file.txt");

        String expectedOut = "cut: missing arguments";

        AppException exception = assertThrows(
                AppException.class, () -> {
                    cut.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals(expectedOut, exception.getMessage());
    }

    @Test
    public void InvalidNumberForByteAsInputTest() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String bytesRequired = "0-10";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);
        args.add("no-this-file.txt");

        String expectedOut = "cut: byte selection entered is not correct";

        AppException exception = assertThrows(
                AppException.class, () -> {
                    cut.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals(expectedOut, exception.getMessage());
    }

    @Test
    public void FileDoesNotExist() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String bytesRequired = "1-3";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);
        args.add("no-such-file.txt");

        String expectedOut = "cut: no-such-file.txt does not exist";

        AppException exception = assertThrows(
                AppException.class, () -> {
                    cut.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals(expectedOut, exception.getMessage());
    }
    
        @Test
    public void InputEndsWithCommaThrowsException() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String bytesRequired = "6,7,";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);
        args.add("no-this-file.txt");

        String expectedOut = "cut: byte selection entered is not correct";

        AppException exception = assertThrows(
                AppException.class, () -> {
                    cut.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals(expectedOut, exception.getMessage());
    }
}
