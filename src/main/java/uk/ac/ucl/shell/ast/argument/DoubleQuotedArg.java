package uk.ac.ucl.shell.ast.argument;

import java.util.ArrayList;

public class DoubleQuotedArg extends QuotedArg {
    ArrayList<AbstractArg> args_;

    public DoubleQuotedArg(ArrayList<AbstractArg> args) {
        this.args_ = args;
    }

    @Override
    public String eval() {
        StringBuffer sbuf = new StringBuffer();
        for (AbstractArg arg : args_) {
            sbuf.append(arg.eval());
        }
        return sbuf.toString();
    }

}
