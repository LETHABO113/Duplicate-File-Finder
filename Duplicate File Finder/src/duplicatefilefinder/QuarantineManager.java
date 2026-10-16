package duplicatefilefinder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class QuarantineManager {
    private static final String AUDIT_FILE_NAME = "audit.log";

    private final Path quarantineDirectory;
    private final Path auditLog;
    private final Logger logger;

    public QuarantineManager(Path quarantineDirectory) {
        if (quarantineDirectory == null) {
            throw new IllegalArgumentException("Quarantine directory cannot be null");
        }
        this.quarantineDirectory = quarantineDirectory.toAbsolutePath().normalize();
        this.auditLog = this.quarantineDirectory.resolve(AUDIT_FILE_NAME);
        this.logger = Logger.getInstance();
    }

    public synchronized List<Path> quarantineAll(List<File> files) throws IOException {
        validateFiles(files);
        for (File file : files) {
            Path original = file.toPath().toAbsolutePath().normalize();
            if (!Files.isRegularFile(original, LinkOption.NOFOLLOW_LINKS) || !Files.isReadable(original)) {
                throw new IOException("File is missing, unreadable, or not a regular file: " + original);
            }
            if (original.startsWith(quarantineDirectory)) {
                throw new IOException("File is already inside the quarantine directory: " + original);
            }
        }

        Files.createDirectories(quarantineDirectory);
        List<Path> movedFiles = new ArrayList<>(files.size());
        for (File file : files) {
            Path original = file.toPath().toAbsolutePath().normalize();

            Path quarantined = quarantineDirectory.resolve(
                    UUID.randomUUID() + "_" + original.getFileName());
            moveSafely(original, quarantined);
            try {
                writeAuditEntry("QUARANTINE", original, quarantined);
            } catch (IOException e) {
                try {
                    moveSafely(quarantined, original);
                } catch (IOException restoreError) {
                    e.addSuppressed(restoreError);
                }
                throw e;
            }
            movedFiles.add(quarantined);
            logger.info("Quarantined file: " + original);
        }

        return movedFiles;
    }

    public synchronized List<Path> restoreAll(List<Path> quarantinedPaths) throws IOException {
        if (quarantinedPaths == null || quarantinedPaths.isEmpty()) {
            throw new IllegalArgumentException("No files to restore");
        }

        Map<Path, Path> originalPaths = readActiveEntries();
        List<Path> restoredFiles = new ArrayList<>(quarantinedPaths.size());
        List<Path> pathsToRestore = new ArrayList<>(quarantinedPaths.size());
        List<Path> originalDestinations = new ArrayList<>(quarantinedPaths.size());
        Set<Path> pathsSeen = new HashSet<>();

        for (Path requestedPath : quarantinedPaths) {
            if (requestedPath == null) {
                throw new IllegalArgumentException("Quarantined paths cannot contain null values");
            }

            Path quarantined = requestedPath.toAbsolutePath().normalize();
            if (!quarantined.startsWith(quarantineDirectory)) {
                throw new IllegalArgumentException("Path is outside the quarantine directory: " + quarantined);
            }
            if (!pathsSeen.add(quarantined)) {
                throw new IllegalArgumentException("Path was listed more than once: " + quarantined);
            }

            Path original = originalPaths.get(quarantined);
            if (original == null || !Files.isRegularFile(quarantined, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("No quarantined file is recorded at: " + quarantined);
            }
            if (Files.exists(original)) {
                throw new FileAlreadyExistsException(original.toString());
            }

            pathsToRestore.add(quarantined);
            originalDestinations.add(original);
        }

        for (int i = 0; i < pathsToRestore.size(); i++) {
            Path quarantined = pathsToRestore.get(i);
            Path original = originalDestinations.get(i);
            Path parent = original.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            moveSafely(quarantined, original);
            try {
                writeAuditEntry("RESTORE", original, quarantined);
            } catch (IOException e) {
                try {
                    moveSafely(original, quarantined);
                } catch (IOException quarantineError) {
                    e.addSuppressed(quarantineError);
                }
                throw e;
            }
            restoredFiles.add(original);
            logger.info("Restored file: " + original);
        }

        return restoredFiles;
    }

    public synchronized List<Path> listQuarantined() {
        try {
            List<Path> activePaths = new ArrayList<>();
            for (Path path : readActiveEntries().keySet()) {
                if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                    activePaths.add(path);
                }
            }
            return activePaths;
        } catch (IOException e) {
            logger.error("Unable to read the quarantine audit log", e);
            return new ArrayList<>();
        }
    }

    private void validateFiles(List<File> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("No files to quarantine");
        }

        Set<Path> pathsSeen = new HashSet<>();
        for (File file : files) {
            if (file == null) {
                throw new IllegalArgumentException("Files cannot contain null values");
            }
            Path path = file.toPath().toAbsolutePath().normalize();
            if (!pathsSeen.add(path)) {
                throw new IllegalArgumentException("File was listed more than once: " + path);
            }
        }
    }

    private Map<Path, Path> readActiveEntries() throws IOException {
        Map<Path, Path> activeEntries = new LinkedHashMap<>();
        if (!Files.exists(auditLog)) {
            return activeEntries;
        }

        for (String line : Files.readAllLines(auditLog, StandardCharsets.UTF_8)) {
            String[] fields = line.split("\\t", 4);
            if (fields.length != 4) {
                continue;
            }
            try {
                Path original = decodePath(fields[2]);
                Path quarantined = decodePath(fields[3]);
                if (fields[1].equals("QUARANTINE")) {
                    activeEntries.put(quarantined, original);
                } else if (fields[1].equals("RESTORE")) {
                    activeEntries.remove(quarantined);
                }
            } catch (IllegalArgumentException e) {
                logger.warn("Skipping an invalid entry in the quarantine audit log");
            }
        }

        return activeEntries;
    }

    private void writeAuditEntry(String action, Path original, Path quarantined) throws IOException {
        String line = Instant.now() + "\t" + action + "\t"
                + encodePath(original) + "\t" + encodePath(quarantined)
                + System.lineSeparator();
        Files.createDirectories(quarantineDirectory);
        Files.writeString(auditLog, line, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private String encodePath(Path path) {
        return path.toString();
    }

    private Path decodePath(String encodedPath) {
        return Path.of(encodedPath).toAbsolutePath().normalize();
    }

    private void moveSafely(Path source, Path target) throws IOException {
        try {
            move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            try {
                move(source, target);
            } catch (IOException moveError) {
                copyThenMove(source, target, moveError);
            }
        } catch (IOException moveError) {
            copyThenMove(source, target, moveError);
        }
    }

    private void copyThenMove(Path source, Path target, IOException moveError) throws IOException {
        boolean targetCreated = false;
        try {
            try (InputStream input = Files.newInputStream(source);
                    OutputStream output = Files.newOutputStream(
                            target, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                targetCreated = true;
                input.transferTo(output);
            }
            Files.delete(source);
        } catch (IOException copyError) {
            if (targetCreated) {
                try {
                    if (Files.exists(source)) {
                        Files.deleteIfExists(target);
                    }
                } catch (IOException cleanupError) {
                    copyError.addSuppressed(cleanupError);
                }
            }
            if (moveError != null) {
                copyError.addSuppressed(moveError);
            }
            throw copyError;
        }
    }

    private void move(Path source, Path target, CopyOption... options) throws IOException {
        Files.move(source, target, options);
    }
}
