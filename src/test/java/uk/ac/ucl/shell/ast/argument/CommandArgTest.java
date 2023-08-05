package uk.ac.ucl.shell.ast.argument;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ucl.shell.Shell;

public class CommandArgTest {

    @Test
    public void commandArgShouldReturnNothingGivenBlankContent() {
        CommandArg arg = new CommandArg(" \t ", new Shell().getEnv());
        assertEquals("", arg.eval());
    }

    @Test
    public void commandArgShouldReturnTheEvaluatedCommand() {
        CommandArg arg = new CommandArg("echo hello world", new Shell().getEnv());
        assertEquals("hello world", arg.eval());
    }

}
