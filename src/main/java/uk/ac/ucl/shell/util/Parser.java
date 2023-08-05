package uk.ac.ucl.shell.util;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.ShellGrammarLexer;
import uk.ac.ucl.shell.ShellGrammarParser;
import uk.ac.ucl.shell.ast.command.Command;
import uk.ac.ucl.shell.exception.ParserException;
import uk.ac.ucl.shell.visitor.GrammarCommandVisitor;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class Parser {
    /*
     * Adopted from Handling errors in ANTLR4
     * https://stackoverflow.com/a/26573239
     */
    public static class ThrowingErrorListener extends BaseErrorListener {
        public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            throw new ParserException("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }

    public static Command parse(String cmd, Shell.Env env) {
        CharStream parserInput = CharStreams.fromString(cmd);
        ShellGrammarLexer lexer = new ShellGrammarLexer(parserInput);
        lexer.removeErrorListeners();
        lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ShellGrammarParser parser = new ShellGrammarParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);
        ParseTree tree = parser.command();
        GrammarCommandVisitor commandVisitor = new GrammarCommandVisitor(env);
        return tree.accept(commandVisitor);
    }
}
