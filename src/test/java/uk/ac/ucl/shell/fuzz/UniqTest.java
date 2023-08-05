package uk.ac.ucl.shell.fuzz;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.app.Uniq;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.DequeAdaptor;

import java.util.ArrayList;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;

class UniqFuzzTest {

    public Shell.Env setCurrentDirectory() {
        Shell.Env env = new Shell().getEnv();
        env.setProperty("user.dir", System.getProperty("user.dir"));
        return env;
    }

    @FuzzTest(maxDuration = "1m")
    public void fuzzCaseInsensitive(FuzzedDataProvider data) {
        Shell.Env env = setCurrentDirectory();
        ArrayList<String> content = FuzzUtil.getStringContent(data, 0, 10000);

        ArrayList<String> args = new ArrayList<>();
        if (data.consumeBoolean()) {
            args.add("-i");
        }

        DequeAdaptor in = new DequeAdaptor();
        for (String line : content) {
            in.write(line + System.getProperty("line.separator"));
        }

        try {
            Uniq uniq = new Uniq();
            uniq.exec(new ArrayList<>(), in, new DequeAdaptor(), env);
        } catch (Exception exception) {
            if (!(exception instanceof AppException)) {
                throw exception;
            }
        }
    }
}