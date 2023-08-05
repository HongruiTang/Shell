package uk.ac.ucl.shell.app;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Integer.MAX_VALUE;

public class Cut implements Application {

    private final Charset encoding = StandardCharsets.UTF_8;

    private ArrayList<String> readFromLine(InputDevice in) {
        ArrayList<String> lines = new ArrayList<>();
        while (!in.inputEnded()) {
            lines.add(in.readline());
        }
        return lines;
    }

    private ArrayList<String> readFromFile(String dir, String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        File target = new File(dir, fileName);
        if (!target.exists()) {
            throw new AppException("cut", fileName + " does not exist");
        }

        Path path = Paths.get(dir, fileName);
        try (BufferedReader reader = Files.newBufferedReader(path, encoding)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ioException) {
            throw new AppException("cut", fileName + " cannot be opened");
        }

        return lines;
    }

    private class Range implements Comparable<Range> {

        private int front;
        private int end;

        public Range(int front, int end) {
            this.front = front;
            this.end = end;
        }

        public int getFront() {
            return front;
        }

        public int getEnd() {
            return end;
        }

        @Override
        public int compareTo(Range range) {
            int myFront = getFront();
            int compareFront = range.getFront();

            if (compareFront != myFront) {
                return myFront - compareFront;
            }

            return getEnd() - range.getEnd();
        }
    }

    private int parsePositiveInt(String extractBytes) {
        try {
            int num = Integer.parseUnsignedInt(extractBytes);
            if (num == 0) {
                throw new AppException("cut", "byte selection entered is not correct");
            }
            return num;
        } catch (Exception e) {
            throw new AppException("cut", "byte selection entered is not correct");
        }
    }

    private ArrayList<Range> byteDissection(String extractBytes) {
        if(extractBytes.endsWith(",")){
            throw new AppException("cut", "byte selection entered is not correct");
        }
        
        String[] splitBytes = extractBytes.split(",");

        ArrayList<Range> rangeOfInteger = new ArrayList<>();

        for (String splitByte : splitBytes) {
            int numOfDash = 0;
            for (int i = 0; i < splitByte.length(); i++) {
                if (splitByte.charAt(i) == '-') {
                    numOfDash++;
                }
            }

            if (numOfDash == 0) {
                int selectedByte = parsePositiveInt(splitByte);
                rangeOfInteger.add(new Range(selectedByte, selectedByte));
                continue;
            }

            if (numOfDash == 1) {
                if (splitByte.charAt(0) == '-') {
                    rangeOfInteger.add(new Range(1, parsePositiveInt(splitByte.substring(1))));
                    continue;
                }

                int length = splitByte.length();

                if (splitByte.charAt(length - 1) == '-') {
                    rangeOfInteger.add(new Range(parsePositiveInt(splitByte.substring(0, length - 1)), MAX_VALUE));
                    continue;
                }
            }

            String[] splitSmallerByte = splitByte.split("-", 2);

            int[] number = new int[splitSmallerByte.length];
            for (int idx = 0; idx < splitSmallerByte.length; idx++) {
                number[idx] = parsePositiveInt(splitSmallerByte[idx]);
            }

            rangeOfInteger.add(new Range(number[0], number[1]));
        }

        Collections.sort(rangeOfInteger);

        return rangeOfInteger;
    }

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.size() >= 4) {
            throw new AppException("cut", "too many arguments");
        } else if (args.size() < 2) {
            throw new AppException("cut", "missing arguments");
        }

        if (!args.get(0).equals("-b")) {
            throw new AppException("cut", "wrong argument " + args.get(0));
        }

        ArrayList<Range> extractBytesArray = byteDissection(args.get(1));

        ArrayList<String> lines;
        if (args.size() == 3) {
            lines = readFromFile(env.getProperty("user.dir"), args.get(2));
        } else {
            lines = readFromLine(in);
        }

        for (String line : lines) {
            byte[] data = line.getBytes();
            ArrayList<Byte> outputBytes = new ArrayList<>();

            int prevEnd = 0;
            for (int i = 0; i < extractBytesArray.size(); ++i) {
                int front = Math.max(extractBytesArray.get(i).getFront(), prevEnd);
                int end = Math.min(extractBytesArray.get(i).getEnd(), data.length);
                prevEnd = Math.max(prevEnd, end + 1);

                if (front > end) {
                    continue;
                }

                for (int idx = front - 1; idx < end; ++idx) {
                    outputBytes.add(data[idx]);
                }
            }

            byte[] outputByteArray = new byte[outputBytes.size()];
            for (int num = 0; num < outputBytes.size(); num++) {
                outputByteArray[num] = outputBytes.get(num).byteValue();
            }

            out.write(new String(outputByteArray, encoding));
            out.write(System.getProperty("line.separator"));
        }
    }
}
