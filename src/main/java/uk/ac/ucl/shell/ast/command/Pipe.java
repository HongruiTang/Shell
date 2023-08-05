package uk.ac.ucl.shell.ast.command;

public class Pipe implements Command {
    public Command left_;
    public Command right_;

    public Pipe(Command left, Command right) {
        this.left_ = left;
        this.right_ = right;
    }

    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
