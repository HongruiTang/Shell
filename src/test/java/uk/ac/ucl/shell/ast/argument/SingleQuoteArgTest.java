package uk.ac.ucl.shell.ast.argument;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SingleQuoteArgTest {
    @Test
    public void singleQuoteArgShouldReturnTheSameString() {
        SingleQuotedArg arg = new SingleQuotedArg("hello world");
        assertEquals("hello world", arg.eval());
    }
}
