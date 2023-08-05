package uk.ac.ucl.shell.visitor;

import java.util.ArrayList;
import java.util.Optional;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.ShellGrammarBaseVisitor;
import uk.ac.ucl.shell.ShellGrammarParser;
import uk.ac.ucl.shell.ast.argument.AbstractArg;
import uk.ac.ucl.shell.ast.command.Call;
import uk.ac.ucl.shell.ast.command.Command;
import uk.ac.ucl.shell.ast.command.Pipe;
import uk.ac.ucl.shell.ast.command.Seq;
import uk.ac.ucl.shell.exception.ShellException;

public class GrammarCommandVisitor extends ShellGrammarBaseVisitor<Command> {
    Shell.Env env_;

    public GrammarCommandVisitor(Shell.Env env) {
        this.env_ = env;
    }

    @Override
    public Command visitCommand(ShellGrammarParser.CommandContext ctx) {
        return visit(ctx.getChild(0));
    }

    @Override
    public Command visitPipe(ShellGrammarParser.PipeContext ctx) {
        Command left = visit(ctx.getChild(0));
        Command right = visit(ctx.getChild(2));
        return new Pipe(left, right);
    }

    @Override
    public Command visitSeq(ShellGrammarParser.SeqContext ctx) {
        Command left = visit(ctx.getChild(0));
        Optional<Command> right = ctx.getChildCount() == 3 ? Optional.of(visit(ctx.getChild(2))) : Optional.empty();
        return new Seq(left, right);
    }

    @Override
    public Command visitCall(ShellGrammarParser.CallContext ctx) {
        GrammarArgumentVisitor argumentVisitor = new GrammarArgumentVisitor(env_);
        ArrayList<AbstractArg> args = new ArrayList<>();

        for (int i = 0; i < ctx.getChildCount(); ++i) {
            args.add(argumentVisitor.visit(ctx.getChild(i)));
        }

        ArrayList<AbstractArg> redirectOut = argumentVisitor.getOutRedirection();
        ArrayList<AbstractArg> redirectIn = argumentVisitor.getInRedirection();

        if (redirectIn.size() > 1 || redirectOut.size() > 1) {
            throw new ShellException("COMP0010 shell: multiple files used for IO redirection");
        }

        Optional<AbstractArg> inFile = redirectIn.size() == 1 ? Optional.of(redirectIn.get(0)) : Optional.empty();
        Optional<AbstractArg> outFile = redirectOut.size() == 1 ? Optional.of(redirectOut.get(0)) : Optional.empty();

        return new Call(args, inFile, outFile);
    }
}
