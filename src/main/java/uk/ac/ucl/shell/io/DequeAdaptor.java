package uk.ac.ucl.shell.io;

import java.util.ArrayDeque;
import java.util.Deque;

public class DequeAdaptor implements InputDevice, OutputDevice {

    private Deque<String> queue_;

    public DequeAdaptor() {
        this.queue_ = new ArrayDeque<String>();
    }

    @Override
    public void write(String content) {
        // empty write is not allowed
        if (content.isEmpty()) {
            return;
        }

        queue_.addLast(content);
    }

    @Override
    public String readline() {
        StringBuffer content = new StringBuffer(queue_.removeFirst());
        String lineSeparator = System.getProperty("line.separator");

        int idx = content.indexOf(lineSeparator, 0);
        while (idx == -1 && !inputEnded()) {
            int prevIdx = content.length();
            content.append(queue_.removeFirst());
            idx = content.indexOf(lineSeparator, prevIdx);
        }

        if (idx != -1) {
            int newSubstringStart = idx + lineSeparator.length();
            if (newSubstringStart != content.length()) {
                queue_.addFirst(content.substring(newSubstringStart));
                content.delete(newSubstringStart, content.length());
            }
            content.delete(newSubstringStart - lineSeparator.length(), content.length());
        }

        return content.toString();
    }

    @Override
    public boolean inputEnded() {
        return queue_.isEmpty();
    }

    @Override
    public void setColor(Color color) {
    }

}
