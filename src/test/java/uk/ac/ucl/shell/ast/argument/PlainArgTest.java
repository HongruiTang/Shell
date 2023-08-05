package uk.ac.ucl.shell.ast.argument;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PlainArgTest {
    @Test
    public void plainArgShouldReturnTheSameString() {
        PlainArg arg = new PlainArg("hello world");
        assertEquals("hello world", arg.eval());
    }
}
