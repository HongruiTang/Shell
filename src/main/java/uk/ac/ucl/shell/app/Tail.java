package uk.ac.ucl.shell.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

public class Tail implements Application {

    private void readFromIn(InputDevice in, ArrayList<String> storage) {
        while (!in.inputEnded()) {
            storage.add(in.readline());
        }
    }

    private void readFromFile(String dir, String filename, ArrayList<String> storage) {
        File tailFile = new File(dir, filename);
        if (!tailFile.exists()) {
            throw new AppException("tail", filename + " does not exist");
        }

        Charset encoding = StandardCharsets.UTF_8;
        Path filePath = Paths.get(dir, filename);
        try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                storage.add(line);
            }
        } catch (IOException e) {
            throw new AppException("tail", "cannot open " + filename);
        }
    }

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.size() > 3) {
            throw new AppException("tail", "wrong arguments");
        }

        ArrayList<String> storage = new ArrayList<>();
        String currentDirectory = env.getProperty("user.dir");
        int tailLines = 10;

        if (args.size() == 0) {
            readFromIn(in, storage);
        } else if (args.size() == 1) {
            if (args.get(0).equals("-n")) {
                throw new AppException("tail", "option requires an argument");
            }
            readFromFile(currentDirectory, args.get(0), storage);
        } else if (args.size() >= 2) {
            if (!args.get(0).equals("-n")) {
                throw new AppException("tail", "wrong argument " + args.get(0));
            }

            try {
                tailLines = Integer.parseInt(args.get(1));
            } catch (Exception e) {
                throw new AppException("tail", "wrong argument " + args.get(1));
            }

            if (tailLines < 0) {
                throw new AppException("tail", "illegal line count " + args.get(1));
            }

            if (args.size() == 2) {
                readFromIn(in, storage);
            } else if (args.size() == 3) {
                readFromFile(currentDirectory, args.get(2), storage);
            }
        }

        int index = Integer.max(0, storage.size() - tailLines);
        for (int i = index; i < storage.size(); ++i) {
            out.write(storage.get(i));
            out.write(System.getProperty("line.separator"));
        }
    }
}
