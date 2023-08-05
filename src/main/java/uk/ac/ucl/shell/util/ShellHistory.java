package uk.ac.ucl.shell.util;

import java.util.ArrayList;

public class ShellHistory {
    String[] content_;
    int start_;
    int size_;

    public ShellHistory(int limit) {
        this.content_ = new String[limit];
        this.start_ = 0;
        this.size_ = 0;
    }

    public void add(String cmd) {
        if (size_ == content_.length) {
            content_[start_] = cmd;
            start_ = (start_ + 1) % content_.length;
        } else {
            content_[size_++] = cmd;
        }
    }

    public ArrayList<String> getAll(int linesNum) {
        ArrayList<String> contentOut = new ArrayList<>();

        for (int i = linesNum; i < size_; i++) {
            contentOut.add(content_[(start_ + i) % content_.length]);
        }

        return contentOut;
    }
}
