package uk.ac.ucl.shell.app;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

import java.util.ArrayList;

public interface Application {
    void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env);
}
