package uk.ac.ucl.shell.io;

import uk.ac.ucl.shell.exception.ShellException;

import java.io.*;

public class FileOutAdaptor implements OutputDevice, AutoCloseable {

    private BufferedWriter writer;

    public FileOutAdaptor(String fileName) {
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
        } catch (IOException ioException) {
            throw new ShellException(ioException.getMessage());
        }
    }

    @Override
    public void write(String content) {
        try {
            writer.write(content);
        } catch (IOException ioException) {
            throw new ShellException(ioException.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException ioException) {
            throw new ShellException(ioException.getMessage());
        }
    }

    @Override
    public void setColor(Color color) {
    }
}
