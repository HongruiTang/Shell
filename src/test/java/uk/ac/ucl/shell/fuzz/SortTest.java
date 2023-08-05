package uk.ac.ucl.shell.fuzz;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.app.Sort;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.DequeAdaptor;

import java.util.ArrayList;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;

class SortFuzzTest {

    public Shell.Env setCurrentDirectory() {
        Shell.Env env = new Shell().getEnv();
        env.setProperty("user.dir", System.getProperty("user.dir"));
        return env;
    }

    @FuzzTest(maxDuration = "1m")
    public void fuzzWithReverse(FuzzedDataProvider data) {
        Shell.Env env = setCurrentDirectory();
        ArrayList<Integer> content = FuzzUtil.getIntegerContent(data, 0, 10000);

        ArrayList<String> args = new ArrayList<>();
        if (data.consumeBoolean()) {
            args.add("-r");
        }

        DequeAdaptor in = new DequeAdaptor();
        for (Integer line : content) {
            in.write(line + System.getProperty("line.separator"));
        }

        try {
            Sort sort = new Sort();
            sort.exec(args, in, new DequeAdaptor(), env);
        } catch (Exception exception) {
            if (!(exception instanceof AppException)) {
                throw exception;
            }
        }
    }
}