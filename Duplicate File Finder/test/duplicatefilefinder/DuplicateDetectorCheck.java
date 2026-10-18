package duplicatefilefinder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class DuplicateDetectorCheck {
    public static void main(String[] args) throws Exception {
        Path folder = Files.createTempDirectory("duplicate-detector-check");
        Path first = folder.resolve("first.txt");
        Path second = folder.resolve("second.txt");
        Path different = folder.resolve("different.txt");
        Files.writeString(first, "same content");
        Files.writeString(second, "same content");
        Files.writeString(different, "other content");

        try (HashCalculator calculator = new HashCalculator(2)) {
            DuplicateDetector detector = new DuplicateDetector(calculator);
            List<DuplicateGroup<File>> groups = detector.findDuplicates(
                    List.of(first.toFile(), second.toFile(), different.toFile()));

            check(groups.size() == 1, "Only matching file contents should form a duplicate group");
            check(groups.get(0).getCount() == 2, "The duplicate group should contain both matching files");
            check(groups.get(0).getWastedSpace() == Files.size(first), "Wasted space should count one extra copy");
            check(detector.findDuplicates(List.of(first.toFile())).isEmpty(),
                    "A single file cannot form a duplicate group");
        }

        try (Stream<Path> paths = Files.walk(folder)) {
            for (Path path : paths.sorted((left, right) -> right.compareTo(left)).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
