package uk.ac.ucl.shell.ast.argument;

public class SingleQuotedArg extends QuotedArg {
    String content_;

    public SingleQuotedArg(String content) {
        this.content_ = content;
    }

    @Override
    public String eval() {
        return content_;
    }

}