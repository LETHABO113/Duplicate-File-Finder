package duplicatefilefinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuplicateDetector {

    private static final String DEFAULT_ALGORITHM = "SHA-256";

    public DuplicateDetector() {
    }

    /**
     * Overloaded helper that defaults to SHA-256.
     */
    public List<DuplicateGroup<File>> findDuplicates(List<File> files) {
        return findDuplicates(files, DEFAULT_ALGORITHM);
    }

    /**
     * Main entry point called by AppController.
     *
     * @param files     List of scanned files from FileScanner
     * @param algorithm Hash algorithm name (e.g., "SHA-256" or "MD5")
     * @return List of confirmed DuplicateGroup<File> sorted by wasted space (descending)
     */
    public List<DuplicateGroup<File>> findDuplicates(List<File> files, String algorithm) {
        if (files == null || files.size() < 2) {
            return Collections.emptyList();
        }

        String hashAlgo = (algorithm == null || algorithm.isBlank()) ? DEFAULT_ALGORITHM : algorithm;

        // Step 1: Group files by exact byte size
        Map<Long, List<File>> sizeGroups = groupBySize(files);

        // Step 2: Collect only files that share the same size with at least 1 other file
        List<File> sameSizeCandidates = new ArrayList<>();
        for (List<File> group : sizeGroups.values()) {
            if (group.size() > 1) {
                sameSizeCandidates.addAll(group);
            }
        }

        if (sameSizeCandidates.isEmpty()) {
            return Collections.emptyList();
        }

        // Step 3: Hash candidates in parallel using HashCalculator
        Map<String, List<File>> hashGroups =
                HashCalculator.groupFilesByHashParallel(sameSizeCandidates, hashAlgo);

        // Step 4: Any hash group with more than 1 file is a confirmed duplicate group
        List<DuplicateGroup<File>> confirmedDuplicates = new ArrayList<>();
        for (Map.Entry<String, List<File>> entry : hashGroups.entrySet()) {
            List<File> matchingFiles = entry.getValue();
            if (matchingFiles != null && matchingFiles.size() > 1) {
                long fileSize = matchingFiles.get(0).length();
                confirmedDuplicates.add(new DuplicateGroup<>(entry.getKey(), fileSize, matchingFiles));
            }
        }

        // Sort largest wasted space first for CLI and GUI display
        Collections.sort(confirmedDuplicates);
        return confirmedDuplicates;
    }

    /**
     * Groups a list of files by their byte size in O(n) average time.
     */
    public Map<Long, List<File>> groupBySize(List<File> files) {
        Map<Long, List<File>> sizeMap = new HashMap<>();
        if (files == null) {
            return sizeMap;
        }

        for (File file : files) {
            if (file != null && file.exists() && file.isFile()) {
                long size = file.length();
                sizeMap.computeIfAbsent(size, k -> new ArrayList<>()).add(file);
            }
        }
        return sizeMap;
    }
}