package uk.ac.ucl.shell.app;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Cd implements Application {

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.isEmpty()) {
            throw new AppException("cd", "missing argument");
        } else if (args.size() > 1) {
            throw new AppException("cd", "too many arguments");
        }

        String dirString = args.get(0);
        String currentDirectory = env.getProperty("user.dir");
        File dir = new File(currentDirectory, dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new AppException("cd", dirString + " is not an existing directory");
        }

        try {
            currentDirectory = dir.getCanonicalPath();
        } catch (IOException e) {
            throw new AppException("cd", "cannot access this directory");
        }
        env.setProperty("user.dir", currentDirectory);
    }
}
