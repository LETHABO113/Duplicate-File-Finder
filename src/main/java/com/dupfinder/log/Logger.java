package com.dupfinder.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Owner: Member 7 (Amogelang) - Testing & QA Lead (backup role: Logger, AppSettings)
 *
 * Timestamped activity log - used for the audit trail (especially by
 * QuarantineManager) and general app events/errors.
 */
public class Logger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path logFile;

    public Logger(Path logFile) {
        this.logFile = Objects.requireNonNull(logFile, "logFile");
        createParentDirectoryIfNeeded();
    }

    public void info(String message) {
        write("INFO", message);
    }

    public void warn(String message) {
        write("WARN", message);
    }

    public void error(String message, Throwable t) {
        String fullMessage = message;
        if (t != null) {
            fullMessage = fullMessage + " - " + t.getClass().getSimpleName() + ": " + t.getMessage();
        }
        write("ERROR", fullMessage);
    }

    private void createParentDirectoryIfNeeded() {
        Path parentDir = logFile.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                System.err.println("Failed to create log directory: " + e.getMessage());
            }
        }
    }

    private void write(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format("[%s] %s: %s%n", timestamp, level, message == null ? "" : message);

        try (BufferedWriter writer = Files.newBufferedWriter(
                logFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
