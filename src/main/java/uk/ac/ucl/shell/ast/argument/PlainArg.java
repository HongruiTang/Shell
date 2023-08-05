package uk.ac.ucl.shell.ast.argument;

public class PlainArg extends AbstractArg {
    String arg_;

    public PlainArg(String arg) {
        super(Type.Plain);
        this.arg_ = arg;
    }

    @Override
    public String eval() {
        return arg_;
    }
}
