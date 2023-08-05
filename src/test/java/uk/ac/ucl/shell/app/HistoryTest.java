package uk.ac.ucl.shell.app;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.DequeAdaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class HistoryTest {
    Shell.Env env;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
    }

    @Test
    public void givenTooManyArgs_shouldCauseException() {
        Exception exception = assertThrows(AppException.class, () -> {
            env.addHistory("echo AAA > dir1/file1.txt");
            env.addHistory("head < dir1/longfile.txt");
            env.addHistory("cut -b 2-,3- dir1/file1.txt");

            History history = new History();
            ArrayList<String> args = new ArrayList<>();
            args.add(" ");
            args.add(" ");

            history.exec(args, null, null, env);
        });

        String expectedMessage = "history: too many arguments";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenInvalidArgs_thenShouldThrowException() {
        Exception exception = assertThrows(AppException.class, () -> {
            ArrayList<String> result = new ArrayList<>();

            for (int i = 0; i < 10005; i++) {
                String line = "echo " + i + " > dir1/file1.txt";
                env.addHistory(line);
                if (i > 1004) {
                    result.add((i - 4) + " " + line);
                }
            }

            DequeAdaptor in = new DequeAdaptor();
            DequeAdaptor out = new DequeAdaptor();
            History history = new History();
            ArrayList<String> args = new ArrayList<>();
            args.add("abcd");
            history.exec(args, in, out, env);
        });

        String expectedMessage = "history: not a integer";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenNoArgs_thenShouldOutputAllTheHistory() {
        ArrayList<String> result = new ArrayList<>();

        for (int i = 0; i < 10005; i++) {
            String line = "echo " + i + " > dir1/file1.txt";
            env.addHistory(line);
            if (i > 4) {
                result.add((i - 4) + " " + line);
            }
        }

        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();
        History history = new History();
        history.exec(new ArrayList<>(), in, out, env);

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void givenLineNum_thenShouldOutputSubHistory() {
        ArrayList<String> result = new ArrayList<>();

        for (int i = 0; i < 10005; i++) {
            String line = "echo " + i + " > dir1/file1.txt";
            env.addHistory(line);
            if (i > 1004) {
                result.add((i - 1004) + " " + line);
            }
        }

        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();
        History history = new History();
        ArrayList<String> args = new ArrayList<>();
        args.add("1000");
        history.exec(args, in, out, env);

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }
}
