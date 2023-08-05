package uk.ac.ucl.shell.app;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

public class Sort implements Application {

    private void readFromIn(InputDevice in, ArrayList<String> storage) {
        while (!in.inputEnded()) {
            storage.add(in.readline());
        }
    }

    private void readFromFile(String dir, String filename, ArrayList<String> storage) {
        File file = new File(dir, filename);
        if (!file.exists()) {
            throw new AppException("sort", filename + " does not exist");
        }

        Charset encoding = StandardCharsets.UTF_8;
        Path filePath = Paths.get(dir, filename);
        try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                storage.add(line);
            }
        } catch (IOException e) {
            throw new AppException("sort", "cannot open " + filename);
        }
    }

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.size() > 2) {
            throw new AppException("sort", "wrong arguments");
        }

        String currentDirectory = env.getProperty("user.dir");
        ArrayList<String> storage = new ArrayList<>();
        boolean reversed = false;

        if (args.size() == 0) {
            readFromIn(in, storage);
        } else if (args.size() == 1) {
            // match args first
            if (args.get(0).equals("-r")) {
                reversed = true;
                readFromIn(in, storage);
            } else {
                readFromFile(currentDirectory, args.get(0), storage);
            }
        } else if (args.size() == 2) {
            if (!args.get(0).equals("-r")) {
                throw new AppException("sort", "wrong argument " + args.get(0));
            }
            reversed = true;
            readFromFile(currentDirectory, args.get(1), storage);
        }

        if (reversed) {
            Collections.sort(storage, Collections.reverseOrder());
        } else {
            Collections.sort(storage);
        }

        for (String s : storage) {
            out.write(s);
            out.write(System.getProperty("line.separator"));
        }
    }

}
