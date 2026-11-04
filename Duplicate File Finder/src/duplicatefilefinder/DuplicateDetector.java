package duplicatefilefinder;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DuplicateDetector {

    private static final String DEFAULT_ALGORITHM = "SHA-256";
    private final HashCalculator hashCalculator;

    public DuplicateDetector() {
        this(new HashCalculator());
    }

    public DuplicateDetector(HashCalculator hashCalculator) {
        if (hashCalculator == null) {
            throw new IllegalArgumentException("Hash calculator cannot be null");
        }
        this.hashCalculator = hashCalculator;
    }


    public List<DuplicateGroup<File>> findDuplicates(List<File> files) {
        return findDuplicates(files, DEFAULT_ALGORITHM);
    }

    
    public List<DuplicateGroup<File>> findDuplicates(List<File> files, String algorithm) {
        if (files == null || files.size() < 2) {
            return Collections.emptyList();
        }

        String hashAlgo = (algorithm == null || algorithm.isBlank()) ? DEFAULT_ALGORITHM : algorithm;

        
        Map<Long, List<File>> sizeGroups = groupBySize(files);

       
        List<File> sameSizeCandidates = new ArrayList<>();
        for (List<File> group : sizeGroups.values()) {
            if (group.size() > 1) {
                sameSizeCandidates.addAll(group);
            }
        }

        if (sameSizeCandidates.isEmpty()) {
            return Collections.emptyList();
        }

        
        Map<File, String> fileHashes;
        try {
            fileHashes = hashCalculator.calculateHashes(sameSizeCandidates, hashAlgo);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Map<String, List<File>> hashGroups = new LinkedHashMap<>();
        for (Map.Entry<File, String> entry : fileHashes.entrySet()) {
            hashGroups.computeIfAbsent(entry.getValue(), key -> new ArrayList<>())
                    .add(entry.getKey());
        }

       
        List<DuplicateGroup<File>> confirmedDuplicates = new ArrayList<>();
        for (Map.Entry<String, List<File>> entry : hashGroups.entrySet()) {
            List<File> matchingFiles = entry.getValue();
            if (matchingFiles != null && matchingFiles.size() > 1) {
                long fileSize = matchingFiles.get(0).length();
                confirmedDuplicates.add(new DuplicateGroup<>(entry.getKey(), fileSize, matchingFiles));
            }
        }

        
        Collections.sort(confirmedDuplicates);
        return confirmedDuplicates;
    }

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
