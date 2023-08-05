package uk.ac.ucl.shell.ast.command;

public interface Command {
    void accept(CommandVisitor visitor);
}
