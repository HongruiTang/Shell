package uk.ac.ucl.shell.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.app.Cut;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;
import uk.ac.ucl.shell.util.property.StrArr;
import uk.ac.ucl.shell.util.property.StrArrGenerator;

@RunWith(JUnitQuickcheck.class)
public class CutTest {
    Shell.Env env;
    String currentDirectory;

    private final Charset encoding = StandardCharsets.UTF_8;

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

    @Property(trials = 5)
    public void outputShouldProcessMultipleLines(@From(StrArrGenerator.class)StrArr strIn) 
    throws IOException {
        ArrayList<String> content = strIn.getStrArr();
        String bytesRequired = "3,5,7";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);

        DequeAdaptor in = new DequeAdaptor();
        
        for(String line: content){
            in.write(line);
        }

        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();
        cut.exec(args, in, out, env);

        for (int i = 0; i < content.size(); i++) {
            byte[] line = content.get(i).getBytes();
            byte[] target = new byte[3];
            target[0] = line[2];
            target[1] = line[4];
            target[2] = line[6];
            String result = new String(target, encoding);
            assertEquals(result, out.readline());
        }

        assertTrue(out.inputEnded());
    }

    @Property(trials = 5)
    public void outputShouldNotContainDuplicatesGivenOverlappingIntervalAndMultipleLineDocument(@From(StrArrGenerator.class)StrArr strIn) 
    throws IOException {
        DequeAdaptor out = new DequeAdaptor();
        Cut cut = new Cut();

        String fileName = "somefile.txt";

        FileUtil.createFile(currentDirectory, fileName);
        ArrayList<String> content = strIn.getStrArr();
        FileUtil.populateFile(currentDirectory, fileName, content);

        String bytesRequired = "3-6,4-8,4-5";

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(bytesRequired);
        args.add(fileName);

        cut.exec(args, new DequeAdaptor(), out, env);

        for (int i = 0; i < content.size(); i++) {
            byte[] line = content.get(i).getBytes();
            byte[] target = new byte[6];
            target[0] = line[2];
            target[1] = line[3];
            target[2] = line[4];
            target[3] = line[5];
            target[4] = line[6];
            target[5] = line[7];
            String result = new String(target, encoding);
            assertEquals(result, out.readline());
        }

        FileUtil.removeFile(currentDirectory, fileName);

        assertTrue(out.inputEnded());
    }
}

