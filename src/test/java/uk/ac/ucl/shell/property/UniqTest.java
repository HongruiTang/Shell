package uk.ac.ucl.shell.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.app.Uniq;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;
import uk.ac.ucl.shell.util.property.StrArr;
import uk.ac.ucl.shell.util.property.StrArrGenerator;

@RunWith(JUnitQuickcheck.class)
public class UniqTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("uniq");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Property(trials = 5)
    public void givenOneArgInvalidFlag_shouldReadFromFile(@From(StrArrGenerator.class)StrArr strIn) 
    throws IOException {

        DequeAdaptor in = new DequeAdaptor();
        Random rand = new Random();
        ArrayList<String> content = strIn.getStrArr();

        for (String line: content) {
            int randNum = rand.nextInt(20);
            in.write(line);

            for(int i = 0; i < randNum; i++){
                in.write(line);
            }
        }

        ArrayList<String> args = new ArrayList<>();
        args.add("-i");
        DequeAdaptor out = new DequeAdaptor();
        Uniq uniq = new Uniq();
        uniq.exec(args, in, out, env);

        for (String s : content) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Property(trials = 5)
    public void givenTwoArgAsDashIAndFilename_shouldBeCaseInsensitive(@From(StrArrGenerator.class)StrArr strIn) throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        DequeAdaptor in = new DequeAdaptor();
        Uniq uniq = new Uniq();

        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);
        ArrayList<String> content = strIn.getStrArr();
        ArrayList<String> input = new ArrayList<>();

        Random rand = new Random();

        for (String line: content) {
            int randNum = rand.nextInt(20);
            input.add(line);

            for(int i = 0; i < randNum; i++){
                input.add(line);
            }
        }

        FileUtil.populateFile(currentDirectory, filename, input);

        ArrayList<String> args = new ArrayList<>();
        args.add("-i");
        args.add(filename);

        uniq.exec(args, in, out, env);

        for (String s : content) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Property(trials = 5)
    public void givenZeroArg_shouldReadFromStdin(@From(StrArrGenerator.class)StrArr strIn) {
        DequeAdaptor in = new DequeAdaptor();
        ArrayList<String> content = strIn.getStrArr();

        Random rand = new Random();

        for (String line: content) {
            int randNum = rand.nextInt(20);
            in.write(line);

            for(int i = 0; i < randNum; i++){
                in.write(line);
            }
        }

        ArrayList<String> args = new ArrayList<>();
        DequeAdaptor out = new DequeAdaptor();
        Uniq uniq = new Uniq();
        uniq.exec(args, in, out, env);

        for (String s : content) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

}
