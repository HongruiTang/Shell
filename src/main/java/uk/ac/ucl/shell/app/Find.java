package uk.ac.ucl.shell.app;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;
import uk.ac.ucl.shell.util.Globbing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Find implements Application {

    private ArrayList<String> matchFiles(String path, String pattern) {
        ArrayList<String> matchValues = new ArrayList<>();

        if (!pattern.contains("/")) {
            pattern = "**/" + pattern;
            try {
                matchValues = Globbing.match(path, pattern);
            } catch (IOException ioException) {
                throw new AppException("find", "file not found");
            }
        }

        return matchValues;
    }

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.size() >= 4) {
            throw new AppException("find", "too many arguments");
        } else if (args.size() == 1 || args.isEmpty()) {
            throw new AppException("find", "not enough arguments");
        }

        ArrayList<String> matchValues = new ArrayList<>();
        String baseDir = ".";

        if (args.size() == 3 && args.get(1).equals("-name")) {
            String path = env.getProperty("user.dir") + File.separator + args.get(0);
            String pattern = args.get(2);
            baseDir = args.get(0);
            matchValues = matchFiles(path, pattern);
        } else if (args.size() == 2 && args.get(0).equals("-name")) {
            String path = env.getProperty("user.dir");
            String pattern = args.get(1);
            matchValues = matchFiles(path, pattern);
        } else {
            throw new AppException("find", "wrong arguments");
        }

        for (String strLine : matchValues) {
            out.write(baseDir + File.separator);
            out.write(strLine);
            out.write(System.getProperty("line.separator"));
        }
    }
}
