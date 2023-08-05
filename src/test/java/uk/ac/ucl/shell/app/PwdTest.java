package uk.ac.ucl.shell.app;

import org.junit.After;
import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.io.DequeAdaptor;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import uk.ac.ucl.shell.util.FileUtil;

public class PwdTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("pwd");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Test
    public void outputShouldMatchUserDirectory() throws Exception {
        Pwd pwd = new Pwd();
        DequeAdaptor out = new DequeAdaptor();
        pwd.exec(new ArrayList<>(), new DequeAdaptor(), out, env);
        String line = out.readline();
        assertEquals(currentDirectory, line);
        assertTrue(out.inputEnded());
    }

    @Test
    public void outputShouldMatchAfterChangeDirectoryAgain() throws Exception {
        Pwd pwd = new Pwd();
        String newDirectory = "randomDir";
        env.setProperty("user.dir", newDirectory);
        DequeAdaptor out = new DequeAdaptor();
        pwd.exec(new ArrayList<>(), new DequeAdaptor(), out, env);
        String line = out.readline();
        assertEquals(newDirectory, line);
        assertTrue(out.inputEnded());
    }
}
