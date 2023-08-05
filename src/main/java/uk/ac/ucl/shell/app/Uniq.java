package uk.ac.ucl.shell.app;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Uniq implements Application {
    private void readFromIn(InputDevice in, ArrayList<String> storage) {
        while (!in.inputEnded()) {
            storage.add(in.readline());
        }
    }

    private void readFromFile(String dir, String filename, ArrayList<String> storage) {
        File uniqFile = new File(dir, filename);
        if (!uniqFile.exists()) {
            throw new AppException("uniq", filename + " does not exist");
        }
        Charset encoding = StandardCharsets.UTF_8;
        Path filePath = Paths.get(dir, filename);
        try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                storage.add(line);
            }
        } catch (IOException e) {
            throw new AppException("uniq", "cannot open " + filename);
        }
    }

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.size() > 2) {
            throw new AppException("uniq", "wrong arguments");
        }

        String currentDirectory = env.getProperty("user.dir");
        ArrayList<String> storage = new ArrayList<>();
        boolean insensitive = false;

        if (args.size() == 0) {
            readFromIn(in, storage);
        } else if (args.size() == 1) {
            if (args.get(0).equals("-i")) {
                insensitive = true;
                readFromIn(in, storage);
            } else {
                readFromFile(currentDirectory, args.get(0), storage);
            }
        } else {
            if (!args.get(0).equals("-i")) {
                throw new AppException("uniq", "wrong argument " + args.get(0));
            }
            insensitive = true;

            readFromFile(currentDirectory, args.get(1), storage);
        }

        if (storage.isEmpty()) {
            return;
        }

        String line = storage.get(0);
        String lineToCompare = insensitive ? line.toLowerCase() : line;
        for (int i = 1; i < storage.size(); ++i) {
            String nextLine = storage.get(i);
            if (!insensitive && !nextLine.equals(line)
                    || insensitive && !nextLine.toLowerCase().equals(lineToCompare)) {
                out.write(line);
                out.write(System.getProperty("line.separator"));
                line = nextLine;
                lineToCompare = insensitive ? line.toLowerCase() : line;
            }
        }
        out.write(line);
        out.write(System.getProperty("line.separator"));
    }
}
