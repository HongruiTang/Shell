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
import uk.ac.ucl.shell.app.Head;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;
import uk.ac.ucl.shell.util.property.StrArr;
import uk.ac.ucl.shell.util.property.StrArrGenerator;

@RunWith(JUnitQuickcheck.class)
public class HeadTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("head");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Property(trials = 5)
    public void givenNoArgs_thenShouldReadFromIn(@From(StrArrGenerator.class)StrArr strIn) {
        DequeAdaptor in = new DequeAdaptor();
        ArrayList<String> result = strIn.getStrArr();

        for (String s: result) {
            in.write(s);
        }

        DequeAdaptor out = new DequeAdaptor();
        Head head = new Head();
        head.exec(new ArrayList<>(), in, out, env);

        for (int i = 0; i < Integer.min(result.size(), 10); i++) {
            assertEquals(result.get(i).stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Property(trials = 5)
    public void givenTwoArgs_thenShouldReadFromIn(@From(StrArrGenerator.class)StrArr strIn) {
        DequeAdaptor in = new DequeAdaptor();
        ArrayList<String> result = strIn.getStrArr();

        for (String s: result) {
            in.write(s);
        }

        int linesToRead = 20;
        ArrayList<String> args = new ArrayList<>();
        args.add("-n");
        args.add(Integer.toString(linesToRead));

        DequeAdaptor out = new DequeAdaptor();
        Head head = new Head();
        head.exec(args, in, out, env);

        for (int i = 0; i < Integer.min(result.size(), linesToRead); i++) {
            assertEquals(result.get(i).stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Property(trials = 5)
    public void givenAFile_shouldOutputNoMoreThanTenLinesOfFiles(@From(StrArrGenerator.class)StrArr strIn) 
    throws Exception {
        Head head = new Head();

        String filename = "no-this-file.txt";
        ArrayList<String> content = strIn.getStrArr();

        ArrayList<String> args = new ArrayList<>();
        args.add(filename);
        FileUtil.createFile(currentDirectory, filename);
        FileUtil.populateFile(currentDirectory, filename, content);

        DequeAdaptor out = new DequeAdaptor();
        head.exec(args, new DequeAdaptor(), out, env);

        for (int i = 0; i < Integer.min(content.size(), 10); i++) {
            assertEquals(content.get(i).stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Property(trials = 5)
    public void givenFile_shouldReadCorrectNumberOfLines(@From(StrArrGenerator.class)StrArr strIn) 
    throws Exception {
        String filename = "no-this-file.txt";

        for (int i = 1; i < 100; i++) {
            ArrayList<String> args = new ArrayList<>();
            args.add("-n");
            args.add(Integer.toString(i));
            args.add(filename);

            FileUtil.createFile(currentDirectory, filename);

            Head head = new Head();
            DequeAdaptor out = new DequeAdaptor();

            ArrayList<String> content = strIn.getStrArr();
            FileUtil.populateFile(currentDirectory, filename, content);

            head.exec(args, new DequeAdaptor(), out, env);
            int contentLength = Integer.min(content.size(), i);
            for (int j = 0; j < contentLength; j++) {
                assertEquals(content.get(j).stripTrailing(), out.readline());
            }

            assertThrows(NoSuchElementException.class, () -> {
                out.readline();
            });
        }

        FileUtil.removeFile(currentDirectory, filename);
    }

}
