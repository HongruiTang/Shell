package uk.ac.ucl.shell.fuzz;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.app.Cut;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.DequeAdaptor;

import java.util.ArrayList;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;

class CutFuzzTest {

    public Shell.Env setCurrentDirectory() {
        Shell.Env env = new Shell().getEnv();
        env.setProperty("user.dir", System.getProperty("user.dir"));
        return env;
    }

    @FuzzTest(maxDuration = "1m")
    public void fuzz(FuzzedDataProvider data) {
        Shell.Env env = setCurrentDirectory();

        ArrayList<Integer> left = FuzzUtil.getIntegerContent(data, -10000, 10000);
        ArrayList<Integer> right = FuzzUtil.getIntegerContent(data, -10000, 10000);
        StringBuffer range = new StringBuffer();
        for (int i = 0; i < Math.min(left.size(), right.size()); ++i) {
            if (i != 0) {
                range.append(",");
            }
            range.append(left.get(i) + "-" + right.get(i));
        }

        ArrayList<String> args = new ArrayList<>();
        args.add("-b");
        args.add(range.toString());

        DequeAdaptor in = new DequeAdaptor();
        in.write(data.consumeRemainingAsAsciiString());

        try {
            Cut cut = new Cut();
            cut.exec(args, in, new DequeAdaptor(), env);
        } catch (Exception appException) {
            if (!(appException instanceof AppException)) {
                throw appException;
            }
        }
    }
}