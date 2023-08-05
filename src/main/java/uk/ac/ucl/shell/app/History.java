package uk.ac.ucl.shell.app;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

import java.util.ArrayList;

public class History implements Application {

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.size() > 1) {
            throw new AppException("history", "too many arguments");
        }

        ArrayList<String> content;
        if (args.size() == 0) {
            content = env.getHistory();
        } else {
            try {
                content = env.getHistory(Integer.parseInt(args.get(0)));
            } catch (Exception e) {
                throw new AppException("history", "not a integer");
            }
        }

        String lineSeparator = System.getProperty("line.separator");
        for (int i = 0; i < content.size(); i++) {
            out.write((i + 1) + " " + content.get(i) + lineSeparator);
        }
    }
}
