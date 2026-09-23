package com.dupfinder.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Owner: Member 4 (Thapelo) - DuplicateDetector Developer
 *
 * Holds one set of duplicate files that share the same content (same hash).
 * Generic over T so it can hold Path objects (or a lightweight FileRecord
 * wrapper, if the team decides one is useful) - this satisfies the
 * "Generics" syllabus requirement.
 *
 * TODO (Member 4):
 *   - Decide whether T should be java.nio.file.Path or a custom FileRecord.
 *   - Add helper methods as needed, e.g. wastedSpace() = (fileSize * (files.size() - 1)).
 */
public class DuplicateGroup<T> {

    private final String hash;
    private final long fileSize;
    private final List<T> files = new ArrayList<>();

    public DuplicateGroup(String hash, long fileSize) {
        this.hash = hash;
        this.fileSize = fileSize;
    }

    public void addFile(T file) {
        files.add(file);
    }

    public String getHash() {
        return hash;
    }

    public long getFileSize() {
        return fileSize;
    }

    public List<T> getFiles() {
        return files;
    }

    /** Space that could be reclaimed by keeping only one copy. */
    public long getWastedSpace() {
        if (files.isEmpty()) return 0;
        return fileSize * (files.size() - 1L);
    }
}
