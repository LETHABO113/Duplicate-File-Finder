package duplicatefilefinder;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class QuarantineManagerCheck {
    public static void main(String[] args) throws Exception {
        Path folder = Files.createTempDirectory("quarantine-manager-check");
        Path original = folder.resolve("notes.txt");
        Path quarantineFolder = folder.resolve("quarantine");
        Files.writeString(original, "keep this file");

        QuarantineManager manager = new QuarantineManager(quarantineFolder);
        List<Path> moved = manager.quarantineAll(List.of(original.toFile()));
        check(moved.size() == 1, "One quarantined path should be returned");
        check(!Files.exists(original), "The original file should be moved");
        check(Files.readString(moved.get(0)).equals("keep this file"), "Quarantined content changed");
        check(manager.listQuarantined().equals(moved), "Quarantine listing is incorrect");

        QuarantineManager reopenedManager = new QuarantineManager(quarantineFolder);
        check(reopenedManager.listQuarantined().equals(moved), "Quarantine listing should survive a new manager instance");

        Files.writeString(original, "keep this other file");
        boolean conflictFound = false;
        try {
            reopenedManager.restoreAll(moved);
        } catch (FileAlreadyExistsException e) {
            conflictFound = true;
        }
        check(conflictFound, "Restore should not overwrite a file at the original path");
        check(Files.exists(moved.get(0)), "The quarantined file should remain after a restore conflict");

        Files.delete(original);
        List<Path> restored = reopenedManager.restoreAll(moved);
        check(restored.equals(List.of(original)), "The original path should be returned after restore");
        check(Files.readString(original).equals("keep this file"), "Restored content changed");
        check(reopenedManager.listQuarantined().isEmpty(), "Restored file should not remain in the listing");

        List<String> auditEntries = Files.readAllLines(quarantineFolder.resolve("audit.log"));
        check(auditEntries.size() == 2, "Quarantine and restore actions should both be audited");
        check(auditEntries.get(0).contains("\tQUARANTINE\t"), "Quarantine action is missing from the audit log");
        check(auditEntries.get(1).contains("\tRESTORE\t"), "Restore action is missing from the audit log");

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
