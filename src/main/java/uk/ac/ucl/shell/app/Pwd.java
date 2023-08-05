package uk.ac.ucl.shell.app;

import java.util.ArrayList;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

public class Pwd implements Application {

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        out.write(env.getProperty("user.dir"));
        out.write(System.getProperty("line.separator"));
    }

}
