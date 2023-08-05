package uk.ac.ucl.shell.io;

import java.io.Console;

public class StdoutAdaptor implements OutputDevice {

    @Override
    public void write(String content) {
        System.out.print(content);
    }

    @Override
    public void setColor(Color color) {
        Console console = System.console();
        switch (color) {
            case BLUE:
                System.out.print(console == null ? "" : (char) 27 + "[34m");
                return;
            case RED:
                System.out.print(console == null ? "" : (char) 27 + "[31m");
                return;
            case RESET:
                System.out.print(console == null ? "" : "\u001b[0m");
                return;
        }
    }

}
