package uk.ac.ucl.shell.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;

import uk.ac.ucl.shell.Shell;
import uk.ac.ucl.shell.exception.AppException;
import uk.ac.ucl.shell.io.InputDevice;
import uk.ac.ucl.shell.io.OutputDevice;

public class Cat implements Application {

    @Override
    public void exec(ArrayList<String> args, InputDevice in, OutputDevice out, Shell.Env env) {
        if (args.isEmpty()) {
            while (!in.inputEnded()) {
                out.write(in.readline());
                out.write(System.getProperty("line.separator"));
            }
            return;
        }

        for (String arg : args) {
            Charset encoding = StandardCharsets.UTF_8;
            File currFile = new File(env.getProperty("user.dir"), arg);

            if (currFile.exists()) {
                Path filePath = Paths.get(env.getProperty("user.dir"), arg);
                try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        out.write(line);
                        out.write(System.getProperty("line.separator"));
                    }
                } catch (IOException e) {
                    throw new AppException("cat", "cannot open " + arg);
                }
            } else {
                throw new AppException("cat", "file does not exist");
            }
        }
    }

}
