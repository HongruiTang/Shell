package uk.ac.ucl.shell.app;

import java.util.ArrayList;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

public class UnsafeDecorator implements Application {
    Application app_;

    public UnsafeDecorator(Application app) {
        this.app_ = app;
    }

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        try {
            this.app_.exec(args, in, out, env);
        } catch (Exception exception) {
            // prints to its stdout
            out.write(exception.getMessage());
        }
    }
}
