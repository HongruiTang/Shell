package uk.ac.ucl.shell.app;

import org.junit.After;
import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class LsTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("ls");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void outputShouldBeEmptyGivenNoArg() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        Ls ls = new Ls();

        String directoryName = "nodir";

        FileUtil.createDirectory(currentDirectory, directoryName);

        String currentDirectory = env.getProperty("user.dir");
        env.setProperty("user.dir", Paths.get(currentDirectory, directoryName).toString());

        ls.exec(new ArrayList<>(), new DequeAdaptor(), out, env);

        assertEquals(true, out.inputEnded());

        FileUtil.removeDirectory(currentDirectory, directoryName);
    }

    @Test
    public void outputShouldBeEmptyIfDestinationDirectoryIsEmpty() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        Ls ls = new Ls();

        String directoryName = "nodir";
        FileUtil.createDirectory(currentDirectory, directoryName);

        ArrayList<String> args = new ArrayList<>();
        args.add(directoryName);
        ls.exec(args, new DequeAdaptor(), out, env);

        assertEquals(true, out.inputEnded());

        FileUtil.removeDirectory(currentDirectory, directoryName);
    }

    @Test
    public void outputShouldBeNoSuchDirectoryIfDestinationIsFile() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Ls ls = new Ls();

        String fileName = "nofile";
        FileUtil.createFile(currentDirectory, fileName);

        ArrayList<String> args = new ArrayList<>();
        args.add(fileName);

        AppException exception = assertThrows(
                AppException.class, () -> {
                    ls.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals("ls: no such directory", exception.getMessage());

        FileUtil.removeFile(currentDirectory, fileName);
    }

    @Test
    public void outputShouldBeNoSuchFileIfDirectoryDoesntExist() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        Ls ls = new Ls();

        ArrayList<String> args = new ArrayList<>();
        args.add("nodir5");

        AppException exception = assertThrows(
                AppException.class, () -> {
                    ls.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals("ls: no such directory", exception.getMessage());
    }

    @Test
    public void outputShouldPeekDirectoryBelowIfDirectoryIsNotEmpty() throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Ls ls = new Ls();

        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("nofile");
        String directoryName = "nodir2";
        FileUtil.createDirectoryWithFiles(currentDirectory, directoryName, fileNames);

        String subDirectoryName = "subdir";
        FileUtil.createDirectoryWithFiles(currentDirectory + File.separator + directoryName, subDirectoryName,
                fileNames);

        ArrayList<String> args = new ArrayList<>();
        args.add(directoryName);

        String expectedOutput = subDirectoryName + "\t" +
                fileNames.get(0) + '\t';
        String[] expectedOutputArr = expectedOutput.split("\t");

        ls.exec(args, new DequeAdaptor(), out, env);
        String actualOutput = out.readline();
        String[] actualOutputArr = actualOutput.split("\t");
        Arrays.sort(actualOutputArr);
        Arrays.sort(expectedOutputArr);

        assertArrayEquals(expectedOutputArr, actualOutputArr);

        FileUtil.removeDirectory(currentDirectory, directoryName);
    }

    @Test
    public void errorThrowIfTooManyArg() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        Ls ls = new Ls();

        ArrayList<String> args = new ArrayList<>();
        args.add("nodir");
        args.add("nodir2");

        AppException exception = assertThrows(
                AppException.class, () -> {
                    ls.exec(args, new DequeAdaptor(), out, env);
                    out.readline();
                });

        assertEquals("ls: too many arguments", exception.getMessage());
    }

    @Test
    public void outputAllFileNamesInDirectory() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        Ls ls = new Ls();

        ArrayList<String> fileNames = new ArrayList<>();

        Random rand = new Random();

        int NUM_OF_FILES_LIMIT = 10;
        int numOfFiles = rand.nextInt(1, NUM_OF_FILES_LIMIT);

        String[] expectedOutputArray = new String[numOfFiles];

        for (int i = 0; i < numOfFiles; i++) {
            expectedOutputArray[i] = "nofile" + i;
            fileNames.add("nofile" + i);
        }

        String directoryName = "nodir";
        FileUtil.createDirectoryWithFiles(currentDirectory, directoryName, fileNames);

        ArrayList<String> args = new ArrayList<>();
        args.add(directoryName);

        ls.exec(args, new DequeAdaptor(), out, env);
        String actualOutput = out.readline();
        String[] actualOutputArray = actualOutput.split("\t");

        Arrays.sort(expectedOutputArray);
        Arrays.sort(actualOutputArray);

        assertArrayEquals(expectedOutputArray, actualOutputArray);

        FileUtil.removeDirectory(currentDirectory, directoryName);
    }

    @Test
    public void shouldNotOutputFilesStartWithDot() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        Ls ls = new Ls();

        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add(".nofile");

        String directoryName = "nodir";

        FileUtil.createDirectoryWithFiles(currentDirectory, directoryName, fileNames);

        ArrayList<String> args = new ArrayList<>();
        args.add(directoryName);

        ls.exec(args, new DequeAdaptor(), out, env);
        assertEquals(true, out.inputEnded());

        FileUtil.removeDirectory(currentDirectory, directoryName);
    }
}