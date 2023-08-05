package uk.ac.ucl.shell.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.app.Tail;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;
import uk.ac.ucl.shell.util.property.StrArr;
import uk.ac.ucl.shell.util.property.StrArrGenerator;

@RunWith(JUnitQuickcheck.class)
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

    @Property(trials = 5)
    public void noArgsShouldReadFromIn(@From(StrArrGenerator.class)StrArr strIn) {
        DequeAdaptor in = new DequeAdaptor();
        ArrayList<String> result = strIn.getStrArr();
        for (String line: result) {
            in.write(line);
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

    @Property(trials = 5)
    public void shouldReadFromInWhenGivenTwoArgs(@From(StrArrGenerator.class)StrArr strIn) {
        DequeAdaptor in = new DequeAdaptor();
        ArrayList<String> result = strIn.getStrArr();

        for (String line: result) {
            in.write(line);
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

    @Property(trials = 5)
    public void outputShouldReadFiles(@From(StrArrGenerator.class)StrArr strIn) throws Exception {
        Tail tail = new Tail();
        DequeAdaptor out = new DequeAdaptor();

        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);

        ArrayList<String> content = strIn.getStrArr();
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

    @Property(trials = 5)
    public void outputShouldReadCorrectNumberOfLines(@From(StrArrGenerator.class)StrArr strIn) 
    throws Exception {
        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);

        ArrayList<String> content = strIn.getStrArr();
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
