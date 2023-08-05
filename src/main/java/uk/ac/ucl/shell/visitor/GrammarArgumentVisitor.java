package uk.ac.ucl.shell.visitor;

import java.util.ArrayList;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.ShellGrammarBaseVisitor;
import uk.ac.ucl.shell.ShellGrammarParser;
import uk.ac.ucl.shell.ast.argument.AbstractArg;
import uk.ac.ucl.shell.ast.argument.CommandArg;
import uk.ac.ucl.shell.ast.argument.PlainArg;
import uk.ac.ucl.shell.ast.argument.SingleQuotedArg;
import uk.ac.ucl.shell.ast.argument.DoubleQuotedArg;

public class GrammarArgumentVisitor extends ShellGrammarBaseVisitor<AbstractArg> {
    private ArrayList<AbstractArg> redirectOut_, redirectIn_;

    Shell.Env env_;

    public GrammarArgumentVisitor(Shell.Env env) {
        redirectOut_ = new ArrayList<>();
        redirectIn_ = new ArrayList<>();
        this.env_ = env;
    }

    public ArrayList<AbstractArg> getOutRedirection() {
        return redirectOut_;
    }

    public ArrayList<AbstractArg> getInRedirection() {
        return redirectIn_;
    }

    @Override
    public AbstractArg visitNonKeyword(ShellGrammarParser.NonKeywordContext ctx) {
        return new PlainArg(ctx.getChild(0).getText());
    }

    @Override
    public AbstractArg visitSingleQuoted(ShellGrammarParser.SingleQuotedContext ctx) {
        return visit(ctx.getChild(1));
    }

    @Override
    public AbstractArg visitDoubleQuoted(ShellGrammarParser.DoubleQuotedContext ctx) {
        ArrayList<AbstractArg> args = new ArrayList<>();
        for (int i = 1; i < ctx.getChildCount() - 1; ++i) {
            args.add(visit(ctx.getChild(i)));
        }
        return new DoubleQuotedArg(args);
    }

    @Override
    public AbstractArg visitBackQuoted(ShellGrammarParser.BackQuotedContext ctx) {
        return visit(ctx.getChild(1));
    }

    @Override
    public AbstractArg visitBackQuotedContent(ShellGrammarParser.BackQuotedContentContext ctx) {
        StringBuffer sbuf = new StringBuffer();
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            sbuf.append(ctx.getChild(i).getText());
        }
        return new CommandArg(sbuf.toString(), env_);
    }

    @Override
    public AbstractArg visitSingleQuoteContent(ShellGrammarParser.SingleQuoteContentContext ctx) {
        StringBuffer sbuf = new StringBuffer();
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            sbuf.append(ctx.getChild(i).getText());
        }
        return new SingleQuotedArg(sbuf.toString());
    }

    @Override
    public AbstractArg visitDoubleQuoteContent(ShellGrammarParser.DoubleQuoteContentContext ctx) {
        StringBuffer sbuf = new StringBuffer();
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            sbuf.append(ctx.getChild(i).getText());
        }
        return new PlainArg(sbuf.toString());
    }

    @Override
    public AbstractArg visitWhitespace(ShellGrammarParser.WhitespaceContext ctx) {
        return new PlainArg(" ");
    }

    @Override
    public AbstractArg visitComment(ShellGrammarParser.CommentContext ctx) {
        return new PlainArg("");
    }

    @Override
    public AbstractArg visitRedirection(ShellGrammarParser.RedirectionContext ctx) {
        if (ctx.getChild(0).getText().equals("<")) {
            redirectIn_.add(visit(ctx.getChild(ctx.getChildCount() - 1)));
        } else {
            redirectOut_.add(visit(ctx.getChild(ctx.getChildCount() - 1)));
        }
        return new PlainArg("");
    }

    @Override
    public AbstractArg visitArgument(ShellGrammarParser.ArgumentContext ctx) {
        return visit(ctx.getChild(0));
    }
}
