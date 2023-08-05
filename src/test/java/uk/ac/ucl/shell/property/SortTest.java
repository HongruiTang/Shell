package uk.ac.ucl.shell.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.app.Sort;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;
import uk.ac.ucl.shell.util.property.StrArr;
import uk.ac.ucl.shell.util.property.StrArrGenerator;

@RunWith(JUnitQuickcheck.class)
public class SortTest {
    Shell.Env env;
    String currentDirectory;

    @Before
    public void setCurrentDirectory() throws IOException {
        env = new Shell().getEnv();
        currentDirectory = FileUtil.createTempDirectory("sort");
        env.setProperty("user.dir", currentDirectory);
    }

    @After
    public void removeCurrentDirectory() throws IOException {
        FileUtil.removeDirectory(currentDirectory);
    }

    @Property(trials = 5)
    public void noArgsShouldReadFromIn(@From(StrArrGenerator.class)StrArr strIn) {
        ArrayList<String> elems = strIn.getStrArr();

        DequeAdaptor in = new DequeAdaptor();
        for (String s : elems) {
            in.write(s);
        }

        DequeAdaptor out = new DequeAdaptor();
        Sort sort = new Sort();
        sort.exec(new ArrayList<>(), in, out, env);

        Collections.sort(elems);

        for (String s : elems) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Property(trials = 5)
    public void outputShouldBeReversedGivenReversedFlagWhenReadingFromIn(@From(StrArrGenerator.class)StrArr strIn){
        ArrayList<String> elems = strIn.getStrArr();

        DequeAdaptor in = new DequeAdaptor();
        for (String s : elems) {
            in.write(s);
        }

        ArrayList<String> args = new ArrayList<>();
        args.add("-r");

        DequeAdaptor out = new DequeAdaptor();
        Sort sort = new Sort();
        sort.exec(args, in, out, env);

        Collections.sort(elems, Collections.reverseOrder());

        for (String s : elems) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }


    @Property(trials = 5)
    public void outputShouldReadFiles(@From(StrArrGenerator.class)StrArr strIn) throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        Sort sort = new Sort();

        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);
        ArrayList<String> content = strIn.getStrArr();      

        FileUtil.populateFile(currentDirectory, filename, content);

        Collections.sort(content);

        ArrayList<String> args = new ArrayList<>();
        args.add(filename);
        sort.exec(args, new DequeAdaptor(), out, env);

        for (String s : content) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        FileUtil.removeFile(currentDirectory, filename);
    }

    @Property(trials = 5)
    public void outputShouldBeReversedGivenReversedFlagWhenReadingFromFile(@From(StrArrGenerator.class)StrArr strIn) throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        Sort sort = new Sort();

        String filename = "no-this-file.txt";
        FileUtil.createFile(currentDirectory, filename);

        ArrayList<String> content = strIn.getStrArr();
        ArrayList<String> elem = new ArrayList<>();

        for (String str : content) {
            elem.add(str);
        }
        Collections.shuffle(elem);

        FileUtil.populateFile(currentDirectory, filename, elem);
        ArrayList<String> args = new ArrayList<>();
        args.add("-r");
        args.add(filename);

        sort.exec(args, new DequeAdaptor(), out, env);

        Collections.sort(content, Collections.reverseOrder());
        for (String s : content) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });

        FileUtil.removeFile(currentDirectory, filename);
    }
}
