package uk.ac.ucl.shell.app;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;
import uk.ac.ucl.shell.io.OutputDevice.Color;

import java.io.File;
import java.util.ArrayList;

public class Ls implements Application {

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.size() >= 2) {
            throw new AppException("ls", "too many arguments");
        }

        File currDir = null;

        if (args.size() == 1) {
            currDir = new File(env.getProperty("user.dir"), args.get(0));
        } else if (args.isEmpty()) {
            currDir = new File(env.getProperty("user.dir"));
        }

        try {
            File[] listOfFiles = currDir.listFiles();
            boolean atLeastOnePrinted = false;

            for (File file : listOfFiles) {
                if (!file.getName().startsWith(".")) {
                    if (file.isDirectory()) {
                        out.setColor(Color.BLUE);
                        out.write(file.getName());
                        out.setColor(Color.RESET);
                    } else {
                        out.write(file.getName());
                    }
                    out.write("\t");
                    atLeastOnePrinted = true;
                }
            }
            if (atLeastOnePrinted) {
                out.write(System.getProperty("line.separator"));
            }
        } catch (NullPointerException e) {
            throw new AppException("ls", "no such directory");
        }

    }
}
