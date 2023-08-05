package uk.ac.ucl.shell.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class FindTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("find");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void searchWithoutInputShouldOutputCurrDirFiles() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Find find = new Find();

        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("somefile.txt");
        String bigDirName = "bigdir";
        String directoryName = "somedir";
        String directoryName2 = "somedir2";

        FileUtil.createDirectory(currentDirectory, bigDirName);
        FileUtil.createDirectoryWithFiles(currentDirectory + File.separator + bigDirName, directoryName2, fileNames);
        FileUtil.createDirectoryWithFiles(currentDirectory + File.separator + bigDirName, directoryName, fileNames);

        ArrayList<String> args = new ArrayList<>();
        args.add("-name");
        args.add("*.txt");

        env.setProperty("user.dir", currentDirectory + File.separator + bigDirName);

        find.exec(args, null, out, env);

        FileUtil.removeDirectory(currentDirectory, bigDirName);

        String baseDir = ".";
        String[] expectedOutput = new String[2];
        expectedOutput[0] = baseDir + File.separator + "somedir2" + File.separator + "somefile.txt";
        expectedOutput[1] = baseDir + File.separator + "somedir" + File.separator + "somefile.txt";

        String[] actualOutput = new String[2];
        int i = 0;
        while (!out.inputEnded()) {
            actualOutput[i] = out.readline();
            i++;
        }

        Arrays.sort(actualOutput);
        Arrays.sort(expectedOutput);

        assertArrayEquals(expectedOutput, actualOutput);
    }

    @Test
    public void searchWithInputFileShouldOutputDocsInThatFileOnly() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Find find = new Find();

        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("somefile.txt");
        String directoryName = "somedir";
        String subDirName = "subdir";
        String directoryName2 = "somedir2";

        FileUtil.createDirectoryWithFiles(currentDirectory, directoryName, fileNames);
        FileUtil.createDirectoryWithFiles(currentDirectory, directoryName2, fileNames);
        FileUtil.createDirectoryWithFiles(currentDirectory + File.separator + directoryName,
                subDirName, fileNames);

        ArrayList<String> args = new ArrayList<>();
        args.add("somedir");
        args.add("-name");
        args.add("*.txt");

        find.exec(args, null, out, env);

        FileUtil.removeDirectory(currentDirectory, directoryName);
        FileUtil.removeDirectory(currentDirectory, directoryName2);

        String[] expectedOutput = new String[2];
        expectedOutput[0] = args.get(0) + File.separator + "somefile.txt";
        expectedOutput[1] = args.get(0) + File.separator + "subdir" + File.separator + "somefile.txt";

        String[] actualOutput = new String[2];
        int i = 0;

        while (!out.inputEnded()) {
            actualOutput[i] = out.readline();
            i++;
        }

        Arrays.sort(actualOutput);
        Arrays.sort(expectedOutput);

        assertArrayEquals(expectedOutput, actualOutput);
    }

    @Test
    public void shouldThrowErrorWhenTooManyInputs() {
        DequeAdaptor out = new DequeAdaptor();
        Find find = new Find();

        ArrayList<String> args = new ArrayList<>();
        args.add("somedir");
        args.add("-name");
        args.add("*.txt");
        args.add("anotherInput");

        AppException exception = assertThrows(
                AppException.class, () -> {
                    find.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals("find: too many arguments", exception.getMessage());
    }

    @Test
    public void shouldThrowErrorWhenTooLittleInputs() {
        DequeAdaptor out = new DequeAdaptor();
        Find find = new Find();

        ArrayList<String> args = new ArrayList<>();
        args.add("somedir");

        AppException exception = assertThrows(
                AppException.class, () -> {
                    find.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals("find: not enough arguments", exception.getMessage());
    }

    @Test
    public void wrongArgumentForNameShouldThrowError() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Find find = new Find();

        ArrayList<String> args = new ArrayList<>();
        args.add("somedir");
        args.add("-nami");
        args.add("*.txt");

        AppException exception = assertThrows(
                AppException.class, () -> {
                    find.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals("find: wrong arguments", exception.getMessage());
    }

}