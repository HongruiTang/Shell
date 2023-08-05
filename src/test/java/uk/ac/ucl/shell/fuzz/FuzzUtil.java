package uk.ac.ucl.shell.fuzz;

import java.util.ArrayList;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class FuzzUtil {
    private static int MAX_STRING_LENGTH = 10000;

    public static ArrayList<String> getStringContent(FuzzedDataProvider data, int min, int max) {
        int numArgs = data.consumeInt(min, max);
        ArrayList<String> args = new ArrayList<>();
        for (int i = 0; i < numArgs; ++i) {
            args.add(data.consumeAsciiString(data.consumeInt(0, MAX_STRING_LENGTH)));
        }
        return args;
    }

    public static ArrayList<Integer> getIntegerContent(FuzzedDataProvider data, int min, int max) {
        ArrayList<Integer> deque = new ArrayList<>();
        int numArgs = data.consumeInt(min, max);
        for (int i = 0; i < numArgs; ++i) {
            deque.add(data.consumeInt());
        }
        return deque;
    }
}
