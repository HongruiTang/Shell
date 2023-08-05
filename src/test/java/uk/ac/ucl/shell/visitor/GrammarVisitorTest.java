package uk.ac.ucl.shell.visitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.ast.command.Command;
import uk.ac.ucl.shell.exception.ShellException;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.util.FileUtil;
import uk.ac.ucl.shell.util.Parser;

public class GrammarVisitorTest {
    Shell.Env env;
    private ExecVisitor visitor;
    DequeAdaptor out;

    @Before
    public void setUp() {
        env = new Shell().getEnv();
    }
    
    @Test
    public void ifInputHasMultipleFilesShouldThrowShellException() {
        String line = "echo >out >xhs";
        String expectedOut = "COMP0010 shell: multiple files used for IO redirection";

        ShellException exception = assertThrows(
                ShellException.class, () -> {
                    Parser.parse(line, env);
                });

        assertEquals(expectedOut, exception.getMessage());
    }

    @Test
    public void correctInputShouldVisitPipe(){
        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();

        String line = "echo hello | echo world";

        Command cmd = Parser.parse(line, env);  
        visitor = new ExecVisitor(in, out, env);
        cmd.accept(visitor);

        assertEquals("world", out.readline());
        assertTrue(out.inputEnded());
    }

    @Test
    public void correctInputShouldVisitSeq(){
        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();

        String line = "echo hello; echo world";

        Command cmd = Parser.parse(line, env);  
        visitor = new ExecVisitor(in, out, env);
        cmd.accept(visitor);

        assertEquals("hello", out.readline());
        assertEquals("world", out.readline());
        assertTrue(out.inputEnded());
    }

    @Test
    public void seqInputIsEmptyShouldBeAllowed(){
        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();

        String line = "echo hello;";

        Command cmd = Parser.parse(line, env);  
        visitor = new ExecVisitor(in, out, env);
        cmd.accept(visitor);

        assertEquals("hello", out.readline());
        assertTrue(out.inputEnded());
    }

    @Test
    public void singleQuotedInputShouldBeAccepted(){
        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();

        String line = "echo 'hello';";

        Command cmd = Parser.parse(line, env);  
        visitor = new ExecVisitor(in, out, env);
        cmd.accept(visitor);

        assertEquals("hello", out.readline());
        assertTrue(out.inputEnded());
    }

    @Test
    public void doubleQuotedInputShouldBeAccepted(){
        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();

        String line = "echo \" hello\"";

        Command cmd = Parser.parse(line, env);  
        visitor = new ExecVisitor(in, out, env);
        cmd.accept(visitor);

        assertEquals(" hello", out.readline());
        assertTrue(out.inputEnded());
    }

    @Test
    public void backQuotedInputShouldBeAccepted(){
        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();

        String line = "echo `echo world`";

        Command cmd = Parser.parse(line, env);  
        visitor = new ExecVisitor(in, out, env);
        cmd.accept(visitor);

        assertEquals("world", out.readline());
        assertTrue(out.inputEnded());
    }

    @Test
    public void shouldNotOutputAnythingAfterHash(){
        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();

        String line = "echo hello #world";

        Command cmd = Parser.parse(line, env);  
        visitor = new ExecVisitor(in, out, env);
        cmd.accept(visitor);

        assertEquals("hello", out.readline());
        assertTrue(out.inputEnded());
    }

    @Test
    public void singleRedirectionShouldBeAccepted() throws IOException{
        String currentDirectory = FileUtil.createTempDirectory("grammar");
        env.setProperty("user.dir", currentDirectory);
        Charset encoding = StandardCharsets.UTF_8;

        FileUtil.createFile(currentDirectory, "file.txt");

        DequeAdaptor in = new DequeAdaptor();
        DequeAdaptor out = new DequeAdaptor();

        String line = "echo hello > file.txt";

        Command cmd = Parser.parse(line, env);  
        visitor = new ExecVisitor(in, out, env);
        cmd.accept(visitor);

        File file = new File(currentDirectory, "file.txt");
        try(BufferedReader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()), encoding);){
            assertEquals("hello", reader.readLine());
        }
        
        FileUtil.removeDirectory(currentDirectory);
    }
    
}
