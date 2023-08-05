package uk.ac.ucl.shell.ast.argument;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import uk.ac.ucl.shell.Shell;

public class DoubleQuoteArgTest {

    @Test
    public void emptyDoubleQuoteShouldReturnNothing() {
        DoubleQuotedArg arg = new DoubleQuotedArg(new ArrayList<>());
        assertEquals("", arg.eval());
    }

    @Test
    public void doubleQuotedArgShouldReturnTheEvaluatedCommand() {
        ArrayList<AbstractArg> args = new ArrayList<>();
        args.add(new PlainArg("plain text here "));
        args.add(new CommandArg("echo hello world", new Shell().getEnv()));
        args.add(new SingleQuotedArg(" content in single quote"));

        DoubleQuotedArg arg = new DoubleQuotedArg(args);
        assertEquals("plain text here hello world content in single quote", arg.eval());
    }

}
