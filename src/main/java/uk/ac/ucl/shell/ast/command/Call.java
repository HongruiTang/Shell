package uk.ac.ucl.shell.ast.command;

import java.util.ArrayList;
import java.util.Optional;

import uk.ac.ucl.shell.ast.argument.AbstractArg;

public class Call implements Command {
    public ArrayList<AbstractArg> args_;
    public Optional<AbstractArg> fileIn_, fileOut_;

    public Call(ArrayList<AbstractArg> args, Optional<AbstractArg> fileIn, Optional<AbstractArg> fileOut) {
        args_ = args;
        fileIn_ = fileIn;
        fileOut_ = fileOut;
    }

    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}