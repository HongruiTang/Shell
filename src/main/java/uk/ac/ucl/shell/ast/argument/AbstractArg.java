package uk.ac.ucl.shell.ast.argument;

public abstract class AbstractArg {
    public enum Type {
        Plain,
        Quoted,
        Command
    }

    private Type type_;

    public AbstractArg(Type type) {
        this.type_ = type;
    }

    public Type type() {
        return type_;
    }

    public abstract String eval();
}