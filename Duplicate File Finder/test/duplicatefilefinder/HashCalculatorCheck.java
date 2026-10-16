package duplicatefilefinder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class HashCalculatorCheck {
    public static void main(String[] args) throws Exception {
        Path folder = Files.createTempDirectory("hash-calculator-check");
        Path first = folder.resolve("first.txt");
        Path second = folder.resolve("second.txt");
        Files.writeString(first, "hello");
        Files.writeString(second, "hello");

        try (HashCalculator calculator = new HashCalculator(2)) {
            String expectedHash = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";
            check(expectedHash.equals(calculator.calculateHash(first)), "Single file hash is incorrect");

            Map<File, String> hashes = calculator.calculateHashes(
                    List.of(first.toFile(), second.toFile()));
            check(hashes.size() == 2, "Both file hashes should be returned");
            check(expectedHash.equals(hashes.get(first.toFile())), "First parallel hash is incorrect");
            check(expectedHash.equals(hashes.get(second.toFile())), "Second parallel hash is incorrect");
        }

        Files.deleteIfExists(first);
        Files.deleteIfExists(second);
        Files.deleteIfExists(folder);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
