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

import static org.junit.Assert.*;

public class CdTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("cd");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void givenNoDirectory_whenUsingAppCd_thenThrowException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Cd cd = new Cd();
            ArrayList<String> args = new ArrayList<>();
            cd.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "cd: missing argument";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenMoreThanOneDirectory_whenUsingAppCd_thenThrowException() {
        Exception exception = assertThrows(AppException.class, () -> {
            Cd cd = new Cd();
            ArrayList<String> args = new ArrayList<>();
            args.add(" ");
            args.add(" ");
            args.add(" ");
            cd.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "cd: too many arguments";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenNonExistDirectory_whenUsingAppCd_thenThrowException() {
        String dirString = "non_exist_dir";
        Exception exception = assertThrows(AppException.class, () -> {
            Cd cd = new Cd();
            ArrayList<String> args = new ArrayList<>();
            args.add(dirString);
            cd.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        });
        String expectedMessage = "cd: " + dirString + " is not an existing directory";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenOneDirectory_whenUsingAppCd_thenGoToThatDirectory() throws IOException {
        FileUtil.createDirectory(currentDirectory, "testDir");
        Cd cd = new Cd();
        ArrayList<String> args = new ArrayList<>();
        args.add("testDir");
        cd.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        assertEquals(currentDirectory + File.separator + "testDir", env.getProperty("user.dir"));
        FileUtil.removeDirectory(currentDirectory, "testDir");
    }

    @Test
    public void givenOneDirectoryAndDotDot_whenUsingAppCd_thenGoToThatDirectoryAndGoBack() throws IOException {
        FileUtil.createDirectory(currentDirectory, "testDir");

        Cd cd = new Cd();
        ArrayList<String> args1 = new ArrayList<>();
        args1.add("testDir");
        cd.exec(args1, new DequeAdaptor(), new DequeAdaptor(), env);

        ArrayList<String> args2 = new ArrayList<>();
        args2.add("..");
        cd.exec(args2, new DequeAdaptor(), new DequeAdaptor(), env);

        FileUtil.removeDirectory(currentDirectory, "testDir");

        assertEquals(currentDirectory, env.getProperty("user.dir"));
    }

    @Test
    public void givenDot_whenUsingAppCd_thenRemainInTheDirectory() throws IOException {
        Cd cd = new Cd();
        ArrayList<String> args = new ArrayList<>();
        args.add(".");
        cd.exec(args, new DequeAdaptor(), new DequeAdaptor(), env);
        assertEquals(currentDirectory, env.getProperty("user.dir"));
    }
}