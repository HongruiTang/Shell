package uk.ac.ucl.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import uk.ac.ucl.shell.exception.ParserException;
import uk.ac.ucl.shell.util.Parser;

public class ParserTest {
    Shell.Env env;

    @Test
    public void parserShouldThrowErrorWhenGrammarIsWrong() {
        String line = "echo |";
        String expectedOut = "line 1:6 mismatched input '<EOF>' expecting {''', '\"', '`', '#', '<', '>', NONSPECIAL, WS}";

        ParserException exception = assertThrows(
                ParserException.class, () -> {
                    Parser.parse(line, env);
                });

        assertEquals(expectedOut, exception.getMessage());
    }

    @Test
    public void parserShouldNotThrowErrorWhenInputIsCorrect() {
        String line = "echo hi";
        Parser.parse(line, env);
    }
}
