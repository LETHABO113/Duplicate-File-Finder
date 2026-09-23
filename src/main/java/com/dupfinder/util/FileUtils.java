package com.dupfinder.util;

/**
 * Owner: Member 8 (Buhle) - Documentation & UML Lead (backup role: FileUtils, HashUtils)
 *
 * Shared file-related helper methods used across the project, e.g. formatting
 * byte counts into human-readable strings ("1.87 GB") for the CLI/GUI/report.
 *
 * TODO (Member 8):
 *   - formatBytes(long bytes): return a human-readable string like "1.87 GB".
 *   - Any other small, reusable file helpers the team needs as they build
 *     (e.g. safe filename generation for quarantine collisions).
 */
public class FileUtils {

    private FileUtils() {
        // utility class
    }

    public static String formatBytes(long bytes) {
        throw new UnsupportedOperationException("TODO: implement in FileUtils (Member 8)");
    }
}
