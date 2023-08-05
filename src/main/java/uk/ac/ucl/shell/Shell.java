package uk.ac.ucl.shell;

import uk.ac.ucl.shell.ast.command.Command;
import uk.ac.ucl.shell.io.DequeAdaptor;
import uk.ac.ucl.shell.io.OutputDevice;
import uk.ac.ucl.shell.io.StdoutAdaptor;
import uk.ac.ucl.shell.util.Parser;
import uk.ac.ucl.shell.util.ShellHistory;
import uk.ac.ucl.shell.visitor.ExecVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Shell {

    private static int k_HISTORY_LIMIT = 10000;

    public class Env {
        private HashMap<String, String> properties_;
        ShellHistory history_;

        public Env() {
            properties_ = new HashMap<>();
            history_ = new ShellHistory(k_HISTORY_LIMIT);
        }

        public String getProperty(String key) {
            return properties_.get(key);
        }

        public void setProperty(String key, String val) {
            properties_.put(key, val);
        }

        public ArrayList<String> getHistory() {
            return history_.getAll(0);
        }

        public ArrayList<String> getHistory(int linesNum) {
            return history_.getAll(linesNum);
        }

        public void addHistory(String cmd) {
            history_.add(cmd);
        }
    }

    Env environment_;

    private void setupEnv(String homeDir) {
        environment_ = new Env();
        environment_.setProperty("user.dir", homeDir);
    }

    public Shell() {
        setupEnv(System.getProperty("user.dir"));
    }

    public Shell(Shell.Env parentEnv) {
        setupEnv(parentEnv.getProperty("user.dir"));
    }

    public Env getEnv() {
        return environment_;
    }

    public static void eval(String cmdline, OutputDevice out, Shell.Env env) {
        try {
            Command cmd = Parser.parse(cmdline, env);
            ExecVisitor execVisitor = new ExecVisitor(new DequeAdaptor(), out, env);
            cmd.accept(execVisitor);
        } catch (Exception e) {
            System.err.println("COMP0010 shell: " + e.getMessage());
        }
    }

    public void eval(String cmdline, OutputDevice out) {
        Shell.eval(cmdline, out, environment_);
    }

    public static void main(String[] args) {
        Shell sh = new Shell();

        if (args.length > 0) {
            if (args.length != 2) {
                System.err.println("COMP0010 shell: wrong number of arguments");
                return;
            }
            if (!args[0].equals("-c")) {
                System.err.println("COMP0010 shell: " + args[0] + ": unexpected argument");
                return;
            }
            sh.eval(args[1], new StdoutAdaptor());
            return;
        }

        try (Scanner input = new Scanner(System.in);) {
            System.out.print(sh.getEnv().getProperty("user.dir") + "> ");
            while (input.hasNextLine()) {
                String cmdline = input.nextLine();
                if (!cmdline.isBlank()) {
                    sh.eval(cmdline, new StdoutAdaptor());
                    sh.getEnv().addHistory(cmdline);
                }
                System.out.print(sh.getEnv().getProperty("user.dir") + "> ");
            }
        }
    }
}
