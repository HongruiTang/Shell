package uk.ac.ucl.shell.app;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;
import uk.ac.ucl.shell.io.OutputDevice.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grep implements Application {

    private void readFromIn(InputDevice in, ArrayList<String> storage) {
        while (!in.inputEnded()) {
            storage.add(in.readline());
        }
    }

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.size() < 1) {
            throw new AppException("grep", "wrong number of arguments");
        }

        Pattern grepPattern = Pattern.compile(args.get(0));
        if (args.size() == 1) {
            ArrayList<String> storage = new ArrayList<>();
            readFromIn(in, storage);
            for (int i = 0; i < storage.size(); i++) {
                Matcher matcher = grepPattern.matcher(storage.get(i));
                if (matcher.find()) {
                    int endIdx = matcher.end();
                    int startIdx = matcher.start();
                    out.write(storage.get(i).substring(0, startIdx));

                    out.setColor(Color.RED);
                    out.write(storage.get(i).substring(startIdx, endIdx));
                    out.setColor(Color.RESET);

                    out.write(storage.get(i).substring(endIdx));
                    out.write(System.getProperty("line.separator"));
                }
            }
            return;
        }

        int numOfFiles = args.size() - 1;
        String currentDirectory = env.getProperty("user.dir");
        Path[] filePathArray = new Path[numOfFiles];
        Path currentDir = Paths.get(currentDirectory);
        for (int i = 0; i < numOfFiles; i++) {
            Path filePath;
            filePath = currentDir.resolve(args.get(i + 1));
            if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new AppException("grep", "wrong file argument");
            }
            filePathArray[i] = filePath;
        }

        for (int j = 0; j < filePathArray.length; j++) {
            Charset encoding = StandardCharsets.UTF_8;
            try (BufferedReader reader = Files.newBufferedReader(filePathArray[j], encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = grepPattern.matcher(line);
                    if (matcher.find()) {
                        if (numOfFiles > 1) {
                            out.write(args.get(j + 1));
                            out.write(":");
                        }
                        int endIdx = matcher.end();
                        int startIdx = matcher.start();
                        out.write(line.substring(0, startIdx));

                        out.setColor(Color.RED);
                        out.write(line.substring(startIdx, endIdx));
                        out.setColor(Color.RESET);

                        out.write(line.substring(endIdx));
                        out.write(System.getProperty("line.separator"));
                    }
                }
            } catch (IOException e) {
                throw new AppException("grep", "cannot open " + args.get(j + 1));
            }
        }

    }
}
