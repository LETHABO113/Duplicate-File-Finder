package com.dupfinder.quarantine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuarantineManagerTest {

    private QuarantineManager quarantineManager;

    @TempDir
    Path tempDir;

    private Path sourceDir;
    private Path quarantineDir;

    @BeforeEach
    void setUp() throws IOException {
        sourceDir = tempDir.resolve("source");
        quarantineDir = tempDir.resolve("quarantine");
        Files.createDirectories(sourceDir);

        quarantineManager = new QuarantineManager(quarantineDir);
    }

    @Test
    void quarantineMovesFileOutOfSource() throws IOException {
        Path file = sourceDir.resolve("dup.txt");
        Files.writeString(file, "duplicate content");

        List<Path> moved = quarantineManager.quarantine(List.of(file));

        assertFalse(Files.exists(file));
        assertEquals(1, moved.size());
        assertTrue(Files.exists(moved.get(0)));
    }

    @Test
    void quarantineThenRestoreRoundTrip() throws IOException {
        Path file = sourceDir.resolve("dup.txt");
        Files.writeString(file, "duplicate content");

        List<Path> moved = quarantineManager.quarantine(List.of(file));
        Path quarantinedFile = moved.get(0);

        quarantineManager.restore(quarantinedFile);

        assertTrue(Files.exists(file));
        assertFalse(Files.exists(quarantinedFile));
        assertEquals("duplicate content", Files.readString(file));
    }

    @Test
    void quarantiningTwoFilesWithSameNameDoesNotOverwrite() throws IOException {
        Path subDirA = sourceDir.resolve("folderA");
        Path subDirB = sourceDir.resolve("folderB");
        Files.createDirectories(subDirA);
        Files.createDirectories(subDirB);

        Path fileA = subDirA.resolve("same.txt");
        Path fileB = subDirB.resolve("same.txt");
        Files.writeString(fileA, "content A");
        Files.writeString(fileB, "content B");

        List<Path> moved = quarantineManager.quarantine(List.of(fileA, fileB));

        assertEquals(2, moved.size());
        assertNotEquals(moved.get(0).getFileName(), moved.get(1).getFileName());
    }

    @Test
    void restoringFileWithNoRecordThrows() {
        Path fakeQuarantinedFile = quarantineDir.resolve("ghost.txt");

        assertThrows(IOException.class, () -> quarantineManager.restore(fakeQuarantinedFile));
    }

    @Test
    void restoreRecreatesOriginalFolderIfDeleted() throws IOException {
        Path file = sourceDir.resolve("dup.txt");
        Files.writeString(file, "duplicate content");

        List<Path> moved = quarantineManager.quarantine(List.of(file));

        Files.deleteIfExists(sourceDir);

        quarantineManager.restore(moved.get(0));

        assertTrue(Files.exists(file));
    }
}