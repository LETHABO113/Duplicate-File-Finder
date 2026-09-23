package com.dupfinder.hash;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Owner: Member 3 (Lethabo) - HashCalculator Developer
 *
 * Computes the SHA-256 hash of a file's content, and hashes many files in
 * parallel using an ExecutorService.
 *
 * TODO (Member 3):
 *   - Implement single-file hashing using MessageDigest("SHA-256") and a
 *     BufferedInputStream, reading in chunks (do not load whole file into memory).
 *   - Implement hashAll() using an ExecutorService (fixed thread pool, size ~= cores)
 *     to hash a batch of files in parallel and collect results into a Map.
 *   - Handle IOExceptions per-file (unreadable file should not kill the whole batch).
 *   - Write tests: identical files -> same hash, one-byte-different files -> different
 *     hash, empty file, large file, unreadable file.
 */
public class HashCalculator {

    /**
     * Computes the SHA-256 hash of a single file's content.
     *
     * @param file path to the file
     * @return lowercase hex string of the SHA-256 digest
     */
    public String hashFile(Path file) throws IOException {
        throw new UnsupportedOperationException("TODO: implement in HashCalculator (Member 3)");
    }

    /**
     * Hashes a batch of files in parallel.
     *
     * @param files files to hash
     * @return map of file path -> SHA-256 hex string (files that failed to hash are omitted)
     */
    public Map<Path, String> hashAll(List<Path> files) {
        throw new UnsupportedOperationException("TODO: implement in HashCalculator (Member 3)");
    }
}
