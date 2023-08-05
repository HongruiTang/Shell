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

public class Head implements Application {

    private void readFromIn(InputDevice in, ArrayList<String> storage) {
        while (!in.inputEnded()) {
            storage.add(in.readline());
        }
    }

    private void readFromFile(String dir, String filename, ArrayList<String> storage) {
        File headFile = new File(dir, filename);
        if (!headFile.exists()) {
            throw new AppException("head", filename + " does not exist");
        }
        Charset encoding = StandardCharsets.UTF_8;
        Path filePath = Paths.get(dir, filename);
        try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                storage.add(line);
            }
        } catch (IOException e) {
            throw new AppException("head", "cannot open " + filename);
        }
    }

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.size() > 3) {
            throw new AppException("head", "wrong arguments");
        }

        String curDirectory = env.getProperty("user.dir");
        ArrayList<String> storage = new ArrayList<>();
        int headLines = 10;

        if (args.size() == 0) {
            readFromIn(in, storage);
        } else if (args.size() == 1) {
            if (args.get(0).equals("-n")) {
                throw new AppException("head", "option requires an argument");
            }
            readFromFile(curDirectory, args.get(0), storage);

        } else if (args.size() >= 2) {
            if (!args.get(0).equals("-n")) {
                throw new AppException("head", "wrong argument " + args.get(0));
            }

            try {
                headLines = Integer.parseInt(args.get(1));
            } catch (Exception e) {
                throw new AppException("head", "wrong argument " + args.get(1));
            }
            if (headLines < 0) {
                throw new AppException("head", "illegal line count -- " + headLines);
            }

            if (args.size() == 2) {
                readFromIn(in, storage);
            } else if (args.size() == 3) {
                readFromFile(curDirectory, args.get(2), storage);
            }
        }

        int index = Integer.min(storage.size(), headLines);
        for (int i = 0; i < index; i++) {
            out.write(storage.get(i));
            out.write(System.getProperty("line.separator"));
        }
    }
}
