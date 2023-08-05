package uk.ac.ucl.shell.app;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

import java.util.ArrayList;

public class Echo implements Application {

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        StringBuffer finalArgs = new StringBuffer();
        boolean firstArgPassed = false;

        for (String arg : args) {
            if (firstArgPassed) {
                finalArgs.append(" ");
            }
            finalArgs.append(arg);
            firstArgPassed = true;
        }

        out.write(finalArgs.toString());
        out.write(System.getProperty("line.separator"));
    }
}
