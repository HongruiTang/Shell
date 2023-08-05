package uk.ac.ucl.shell.ast.command;

import java.util.Optional;

public class Seq implements Command {
    public Command left_;
    public Optional<Command> right_;

    public Seq(Command left, Optional<Command> right) {
        this.left_ = left;
        this.right_ = right;
    }

    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
