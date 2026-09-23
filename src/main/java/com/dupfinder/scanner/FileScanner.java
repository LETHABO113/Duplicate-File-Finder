package com.dupfinder.scanner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Owner: Member 2 (Mahlogono) - FileScanner Developer
 *
 * Walks a directory tree recursively and collects every file found,
 * applying optional filters (extension regex, minimum size).
 *
 * TODO (Member 2):
 *   - Implement recursive walk using Files.walkFileTree (NOT File.listFiles recursion -
 *     walkFileTree handles permission errors and symlink loops much more cleanly).
 *   - Support an extension filter (regex) and a minimum file size filter.
 *   - Catch and log per-file IOExceptions (permission denied, broken symlink) and
 *     continue the scan rather than aborting - see AppController's error-handling policy.
 *   - Write FileScannerTest.java covering: nested folders, empty folders, symlink loops,
 *     unreadable files, extension filtering.
 */
public class FileScanner {

    /**
     * Recursively scans {@code rootDirectory} and returns every regular file found.
     *
     * @param rootDirectory the folder to scan
     * @return list of all files discovered
     * @throws IOException if the root directory itself cannot be read
     */
    public List<Path> scan(Path rootDirectory) throws IOException {
        throw new UnsupportedOperationException("TODO: implement in FileScanner (Member 2)");
    }
}
