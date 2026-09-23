package com.dupfinder.quarantine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class QuarantineManager {

    private final Path quarantineDirectory;
    private final Path logFile;

    public QuarantineManager(Path quarantineDirectory) {
        this.quarantineDirectory = quarantineDirectory;
        this.logFile = quarantineDirectory.resolve("quarantine-log.txt");
    }

    public List<Path> quarantine(List<Path> files) throws IOException {
        Files.createDirectories(quarantineDirectory);
        List<Path> moved = new ArrayList<>();

        for (Path file : files) {
            try {
                Path destination = uniqueDestination(file.getFileName().toString());
                Files.move(file, destination, StandardCopyOption.REPLACE_EXISTING);
                appendToLog(destination.getFileName().toString(), file.toAbsolutePath().toString());
                moved.add(destination);
            } catch (IOException e) {
                // Skip this file, don't let it stop the rest of the batch
                System.err.println("Could not quarantine " + file + ": " + e.getMessage());
            }
        }

        return moved;
    }

    public void restore(Path quarantinedFile) throws IOException {
        String quarantinedName = quarantinedFile.getFileName().toString();
        List<String> lines = Files.exists(logFile)
                ? Files.readAllLines(logFile)
                : new ArrayList<>();

        String originalPath = null;
        List<String> remainingLines = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split("\\|", 2);
            if (parts.length == 2 && parts[0].equals(quarantinedName) && originalPath == null) {
                originalPath = parts[1];
            } else {
                remainingLines.add(line);
            }
        }

        if (originalPath == null) {
            throw new IOException("No record of original location for " + quarantinedName);
        }

        Path destination = Path.of(originalPath);
        Files.createDirectories(destination.getParent());
        Files.move(quarantinedFile, destination, StandardCopyOption.REPLACE_EXISTING);

        Files.write(logFile, remainingLines);
    }

    // Avoids overwriting an existing file in the quarantine folder if two
    // duplicates from different places happen to share the same filename.
    private Path uniqueDestination(String fileName) {
        Path candidate = quarantineDirectory.resolve(fileName);
        if (!Files.exists(candidate)) {
            return candidate;
        }

        String namePart = fileName;
        String extension = "";
        int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            namePart = fileName.substring(0, dot);
            extension = fileName.substring(dot);
        }

        int counter = 1;
        Path unique;
        do {
            unique = quarantineDirectory.resolve(namePart + "_" + counter + extension);
            counter++;
        } while (Files.exists(unique));

        return unique;
    }

    private void appendToLog(String quarantinedName, String originalPath) throws IOException {
        String line = quarantinedName + "|" + originalPath + System.lineSeparator();
        Files.writeString(logFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}