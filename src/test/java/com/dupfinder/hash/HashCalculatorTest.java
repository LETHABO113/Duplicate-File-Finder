package com.dupfinder.hash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HashCalculatorTest {

    private HashCalculator hashCalculator;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        hashCalculator = new HashCalculator();
    }

    @Test
    void identicalFilesProduceSameHash() throws IOException {
        Path fileA = tempDir.resolve("a.txt");
        Path fileB = tempDir.resolve("b.txt");
        Files.writeString(fileA, "hello world");
        Files.writeString(fileB, "hello world");

        String hashA = hashCalculator.hashFile(fileA);
        String hashB = hashCalculator.hashFile(fileB);

        assertEquals(hashA, hashB);
    }

    @Test
    void oneByteDifferenceProducesDifferentHash() throws IOException {
        Path fileA = tempDir.resolve("a.txt");
        Path fileB = tempDir.resolve("b.txt");
        Files.writeString(fileA, "hello world");
        Files.writeString(fileB, "hello worlD");

        String hashA = hashCalculator.hashFile(fileA);
        String hashB = hashCalculator.hashFile(fileB);

        assertNotEquals(hashA, hashB);
    }

    @Test
    void emptyFileHashesConsistently() throws IOException {
        Path fileA = tempDir.resolve("empty1.txt");
        Path fileB = tempDir.resolve("empty2.txt");
        Files.createFile(fileA);
        Files.createFile(fileB);

        assertEquals(hashCalculator.hashFile(fileA), hashCalculator.hashFile(fileB));
    }

    @Test
    void hashFileReturns64CharacterHexString() throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "some content");

        String hash = hashCalculator.hashFile(file);

        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]+"));
    }

    @Test
    void hashAllReturnsCorrectMapSize() throws IOException {
        Path file1 = tempDir.resolve("f1.txt");
        Path file2 = tempDir.resolve("f2.txt");
        Path file3 = tempDir.resolve("f3.txt");
        Files.writeString(file1, "content one");
        Files.writeString(file2, "content two");
        Files.writeString(file3, "content one");

        Map<Path, String> results = hashCalculator.hashAll(List.of(file1, file2, file3));

        assertEquals(3, results.size());
        assertEquals(results.get(file1), results.get(file3));
        assertNotEquals(results.get(file1), results.get(file2));
    }
}