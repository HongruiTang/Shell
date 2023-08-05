package uk.ac.ucl.shell.util;

// import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class Globbing {

    public static ArrayList<String> match(String pathIn, String globIn) throws IOException {
        ArrayList<String> matchFiles = new ArrayList<>();

        // Construct globbing pattern
        // Needs to replace **/ with {,**} because Java implementation
        // requires **/ to match at least one directory
        globIn = globIn.replace("**/", "{,**/}");
        String glob = "glob:" + globIn;

        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
        Path dir = Paths.get(pathIn);

        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                path = dir.relativize(path);
                if (pathMatcher.matches(path)) {
                    matchFiles.add(String.valueOf(path));
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

        return matchFiles;
    }
}
