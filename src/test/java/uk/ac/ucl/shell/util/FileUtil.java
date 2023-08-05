package uk.ac.ucl.shell.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileUtil {

    public static String createTempDirectory (String prefix) throws IOException {
        return Files.createTempDirectory(prefix).toFile().getCanonicalPath();
    }

    public static void createFile (String currentPath, String fileName) throws IOException {
        File file = new File(currentPath, fileName);
        file.createNewFile();
    }

    public static void createFiles (String currentPath, ArrayList<String> fileNames) throws IOException {
        for (String fileName : fileNames) {
            createFile(currentPath, fileName);
        }
    }

    public static void createDirectory (String currentPath, String directoryName)
            throws IOException {
        Path path = Paths.get(currentPath, directoryName);
        Files.createDirectory(path);
    }

    public static void createDirectoryWithFiles (String currentPath, String directoryName, ArrayList<String> fileNames)
            throws IOException {
        createDirectory(currentPath, directoryName);
        createFiles(currentPath + File.separator + directoryName, fileNames);
    }

    public static void populateFile (String currentPath, String fileName, ArrayList<String> content) throws IOException {
        File file = new File(currentPath, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String elem : content) {
                writer.write(elem);
            }
        }
    }

    public static void populateFile (String currentPath, String fileName, String content) throws IOException {
        File file = new File(currentPath, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    public static void removeDirectory (String currentPath, String directoryName) throws IOException {
        File dirToBeDeleted = new File(currentPath, directoryName);
        removeDirectory(dirToBeDeleted);
    }

    public static void removeDirectory (String currentPath) throws IOException {
        File dirToBeDeleted = new File(currentPath);
        removeDirectory(dirToBeDeleted);
    }

    private static void removeDirectory (File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                removeDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    public static void removeFile (String currentPath, String filename) {
        File file = new File(currentPath, filename);
        file.delete();
    }

    public static void removeFiles (String currentPath, ArrayList<String> fileNames) throws IOException {
        for (String fileName : fileNames) {
            removeFile(currentPath, fileName);
        }
    }
}
