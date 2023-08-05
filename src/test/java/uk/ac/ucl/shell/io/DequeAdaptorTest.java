package uk.ac.ucl.shell.io;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

public class DequeAdaptorTest {
    @Test
    public void newlineStringShouldBeReadCorrectly() throws Exception {
        ArrayList<String> listOfStringsWithoutNewLine = new ArrayList<>();

        String lineSeparator = System.getProperty("line.separator");
        for (int i = 0; i < 100; ++i) {
            listOfStringsWithoutNewLine.add(Integer.toString(i));
        }

        DequeAdaptor out = new DequeAdaptor();
        for (String s : listOfStringsWithoutNewLine) {
            out.write(s);
            out.write(lineSeparator);
        }

        for (String s : listOfStringsWithoutNewLine) {
            assertEquals(s, out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void readlineShouldBeCorrectWithMixingStringAndNewline() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        String lineSeparator = System.getProperty("line.separator");
        out.write("a");
        out.write("b");
        out.write("cd");
        out.write(lineSeparator);
        out.write("efghi");
        out.write(lineSeparator);
        assertEquals("abcd", out.readline());
        assertEquals("efghi", out.readline());

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void readlineShouldBeCorrectWithoutNewline() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        out.write("a");
        out.write("b");
        out.write("cd");
        out.write("efghi");
        assertEquals("abcdefghi", out.readline());

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void readlineShouldBeCorrectWithOnlyNewline() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        String lineSeparator = System.getProperty("line.separator");
        out.write(lineSeparator + lineSeparator + lineSeparator);
        out.write(lineSeparator);
        out.write(lineSeparator + lineSeparator);
        out.write(lineSeparator);
        out.write(lineSeparator);

        int counter = 0;
        while (!out.inputEnded()) {
            assertEquals("", out.readline());
            ++counter;
        }
        assertEquals(8, counter);
    }

    @Test
    public void readlineShouldBeCorrectWithComplexString() throws Exception {
        ArrayList<String> listOfStrings = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        Random random = new Random();

        String lineSeparator = System.getProperty("line.separator");
        for (int i = 0; i < 100; ++i) {
            int prevNewLine = 0;
            StringBuffer contentBuffer = new StringBuffer();
            for (int j = 0; j < 100; ++j) {
                contentBuffer.append(Integer.toString(j));
                if (random.nextBoolean()) {
                    contentBuffer.append(lineSeparator);
                    result.add(contentBuffer.substring(prevNewLine));
                    prevNewLine = contentBuffer.length();
                }
            }
            contentBuffer.append(lineSeparator);
            result.add(contentBuffer.substring(prevNewLine));
            listOfStrings.add(contentBuffer.toString());
        }

        DequeAdaptor out = new DequeAdaptor();
        for (String s : listOfStrings) {
            out.write(s);
        }

        for (String s : result) {
            assertEquals(s.stripTrailing(), out.readline());
        }

        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

    @Test
    public void emptyStringShouldNotAppearInDeque() throws Exception {
        DequeAdaptor out = new DequeAdaptor();
        out.write("");
        out.write("");
        assertThrows(NoSuchElementException.class, () -> {
            out.readline();
        });
    }

}
