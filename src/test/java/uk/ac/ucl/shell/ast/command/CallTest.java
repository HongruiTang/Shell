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

public class CallTest {
    Call call;
    ExecVisitor visitor;
    Shell.Env env;
    DequeAdaptor out;

    @Before
    public void setUp() {
        DequeAdaptor in = new DequeAdaptor();
        out = new DequeAdaptor();
        env = new Shell().getEnv();
        visitor = new ExecVisitor(in, out, env);
    }

    @Test
    public void testCallAccept() {
        ArrayList<AbstractArg> args = new ArrayList<>();
        PlainArg fst = new PlainArg("echo");
        PlainArg snd = new PlainArg(" ");
        PlainArg thd = new PlainArg("hello");

        args.add(fst);
        args.add(snd);
        args.add(thd);

        Optional<AbstractArg> inFile = Optional.empty();
        Optional<AbstractArg> outFile = Optional.empty();

        call = new Call(args, inFile, outFile);
        call.accept(visitor);

        assertEquals("hello", out.readline());
    }
}
