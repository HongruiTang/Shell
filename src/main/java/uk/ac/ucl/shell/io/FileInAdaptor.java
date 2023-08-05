package uk.ac.ucl.shell.io;

import uk.ac.ucl.shell.exception.ShellException;

import java.io.*;
import java.util.Scanner;

public class FileInAdaptor implements InputDevice, AutoCloseable {

    private Scanner scanner;

    public FileInAdaptor(String fileName) {
        try {
            scanner = new Scanner(new File(fileName));
        } catch (FileNotFoundException fo) {
            throw new ShellException(fo.getMessage());
        }
    }

    @Override
    public void close() {
        scanner.close();
    }

    @Override
    public String readline() {
        return scanner.nextLine();
    }

    @Override
    public boolean inputEnded() {
        return !scanner.hasNext();
    }
}