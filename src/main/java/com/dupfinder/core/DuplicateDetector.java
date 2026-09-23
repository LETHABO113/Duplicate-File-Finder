package com.dupfinder.core;

import com.dupfinder.hash.HashCalculator;

import java.nio.file.Path;
import java.util.List;

/**
 * Owner: Member 4 (Thapelo) - DuplicateDetector Developer
 *
 * Runs the two-pass duplicate detection algorithm:
 *   Pass 1 (size grouping): group all files by size; discard groups of size 1
 *           (a file with a unique size cannot have a duplicate).
 *   Pass 2 (hash grouping): within each remaining size group, hash every file
 *           (via HashCalculator, in parallel) and group by hash. Any hash
 *           group with more than one file is a confirmed duplicate group.
 *
 * TODO (Member 4):
 *   - Implement groupBySize() -> Map<Long, List<Path>>.
 *   - Implement detect() using groupBySize() + HashCalculator.hashAll() per group.
 *   - Write up the Big O analysis for the design doc (this is your section):
 *     roughly O(n) for the size pass, O(n) file reads for the hash pass in the
 *     worst case (all files same size), which is why the size pre-filter matters.
 *   - Write tests: all-unique files, one duplicate group, multiple groups,
 *     files with same size but different content (must NOT be flagged).
 */
public class DuplicateDetector {

    private final HashCalculator hashCalculator;

    public DuplicateDetector(HashCalculator hashCalculator) {
        this.hashCalculator = hashCalculator;
    }

    /**
     * Runs the full size-then-hash duplicate detection algorithm.
     *
     * @param files all files discovered by FileScanner
     * @return list of confirmed duplicate groups (each with 2+ files)
     */
    public List<DuplicateGroup<Path>> detect(List<Path> files) {
        throw new UnsupportedOperationException("TODO: implement in DuplicateDetector (Member 4)");
    }
}
