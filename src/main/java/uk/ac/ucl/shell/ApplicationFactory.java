package uk.ac.ucl.shell;

import uk.ac.ucl.shell.app.Application;
import uk.ac.ucl.shell.app.Cat;
import uk.ac.ucl.shell.app.Cd;
import uk.ac.ucl.shell.app.Cut;
import uk.ac.ucl.shell.app.Echo;
import uk.ac.ucl.shell.app.Find;
import uk.ac.ucl.shell.app.Grep;
import uk.ac.ucl.shell.app.Head;
import uk.ac.ucl.shell.app.History;
import uk.ac.ucl.shell.app.Ls;
import uk.ac.ucl.shell.app.Pwd;
import uk.ac.ucl.shell.app.Sort;
import uk.ac.ucl.shell.app.Tail;
import uk.ac.ucl.shell.app.Uniq;
import uk.ac.ucl.shell.app.UnsafeDecorator;
import uk.ac.ucl.shell.exception.ShellException;

public class ApplicationFactory {

    public static Application getApplication(String appName) {
        if (appName.startsWith("_")) {
            String newAppName = appName.substring(1);
            return new UnsafeDecorator(getNormalApplicaton(newAppName));
        }
        return getNormalApplicaton(appName);
    }

    private static Application getNormalApplicaton(String appName) {
        switch (appName) {
            case "pwd":
                return new Pwd();
            case "cd":
                return new Cd();
            case "ls":
                return new Ls();
            case "cat":
                return new Cat();
            case "echo":
                return new Echo();
            case "head":
                return new Head();
            case "tail":
                return new Tail();
            case "grep":
                return new Grep();
            case "cut":
                return new Cut();
            case "find":
                return new Find();
            case "uniq":
                return new Uniq();
            case "sort":
                return new Sort();
            case "history":
                return new History();
        }
        throw new ShellException("no existing application");
    }
}
