package uk.ac.ucl.shell.ast.argument;

public abstract class QuotedArg extends AbstractArg {

    public QuotedArg() {
        super(Type.Quoted);
    }

    public abstract String eval();

}
