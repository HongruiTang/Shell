package uk.ac.ucl.shell.ast.command;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.ast.argument.AbstractArg;
import uk.ac.ucl.shell.ast.argument.PlainArg;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.visitor.ExecVisitor;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class PipeTest {
    Pipe pipe;
    Call callFst;
    Call callSnd;
    ExecVisitor visitor;
    Shell.Env env;
    DequeAdaptor out;

    @Before
    public void setUp() {
        DequeAdaptor in = new DequeAdaptor();
        out = new DequeAdaptor();
        env = new Shell().getEnv();
        visitor = new ExecVisitor(in, out, env);

        ArrayList<AbstractArg> argsFst = new ArrayList<>();
        PlainArg fst = new PlainArg("echo");
        PlainArg snd = new PlainArg(" ");
        PlainArg thd = new PlainArg("hello");

        argsFst.add(fst);
        argsFst.add(snd);
        argsFst.add(thd);

        Optional<AbstractArg> inFile = Optional.empty();
        Optional<AbstractArg> outFile = Optional.empty();
        callFst = new Call(argsFst, inFile, outFile);

        ArrayList<AbstractArg> argsSnd = new ArrayList<>();
        fst = new PlainArg("grep");
        snd = new PlainArg(" ");
        thd = new PlainArg("he");

        argsSnd.add(fst);
        argsSnd.add(snd);
        argsSnd.add(thd);

        callSnd = new Call(argsSnd, inFile, outFile);
    }

    @Test
    public void testPipeAccept() {
        pipe = new Pipe(callFst, callSnd);
        pipe.accept(visitor);

        assertEquals("hello", out.readline());
    }
}
