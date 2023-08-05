package uk.ac.ucl.shell.ast.command;

public interface CommandVisitor {
    void visit(Pipe pipe);

    void visit(Seq seq);

    void visit(Call call);
}
