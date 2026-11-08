package duplicatefilefinder;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.Assert.*;

public class QuarantineManagerTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void quarantine_MovesFileAndAudits() throws IOException {
        Path sourceDir = tempDir.newFolder("source").toPath();
        Path quarantineDir = tempDir.newFolder("quarantine").toPath();
        Path targetFile = sourceDir.resolve("test.txt");
        Files.writeString(targetFile, "sensitive data", StandardCharsets.UTF_8);

        QuarantineManager manager = new QuarantineManager(quarantineDir);
        List<Path> quarantined = manager.quarantineAll(List.of(targetFile.toFile()));

        assertEquals(1, quarantined.size());
        assertFalse("Original file should be moved", Files.exists(targetFile));
        assertTrue("Quarantined file should exist", Files.exists(quarantined.get(0)));
        assertEquals("sensitive data", Files.readString(quarantined.get(0)));
        
        List<Path> active = manager.listQuarantined();
        assertEquals(1, active.size());
        assertEquals(quarantined.get(0), active.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void quarantine_RejectsNullList() throws IOException {
        Path quarantineDir = tempDir.newFolder("quarantine").toPath();
        new QuarantineManager(quarantineDir).quarantineAll(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void quarantine_RejectsEmptyList() throws IOException {
        Path quarantineDir = tempDir.newFolder("quarantine").toPath();
        new QuarantineManager(quarantineDir).quarantineAll(List.of());
    }
    
    @Test(expected = IOException.class)
    public void quarantine_RejectsMissingFile() throws IOException {
        Path quarantineDir = tempDir.newFolder("quarantine").toPath();
        File missingFile = tempDir.getRoot().toPath().resolve("ghost.txt").toFile();
        new QuarantineManager(quarantineDir).quarantineAll(List.of(missingFile));
    }

    @Test
    public void restore_MovesFileBackAndAudits() throws IOException {
        Path sourceDir = tempDir.newFolder("source").toPath();
        Path quarantineDir = tempDir.newFolder("quarantine").toPath();
        Path targetFile = sourceDir.resolve("restore_test.txt");
        Files.writeString(targetFile, "restore me", StandardCharsets.UTF_8);

        QuarantineManager manager = new QuarantineManager(quarantineDir);
        List<Path> quarantined = manager.quarantineAll(List.of(targetFile.toFile()));
        
        List<Path> restored = manager.restoreAll(quarantined);
        
        assertEquals(1, restored.size());
        assertEquals(targetFile, restored.get(0));
        assertTrue("Original file should be back", Files.exists(targetFile));
        assertFalse("Quarantined file should be gone", Files.exists(quarantined.get(0)));
        
        assertTrue("Active quarantine list should be empty", manager.listQuarantined().isEmpty());
    }

    @Test
    public void delete_RemovesFileAndAudits() throws IOException {
        Path sourceDir = tempDir.newFolder("source").toPath();
        Path quarantineDir = tempDir.newFolder("quarantine").toPath();
        Path targetFile = sourceDir.resolve("delete_test.txt");
        Files.writeString(targetFile, "delete me", StandardCharsets.UTF_8);

        QuarantineManager manager = new QuarantineManager(quarantineDir);
        List<Path> quarantined = manager.quarantineAll(List.of(targetFile.toFile()));
        
        List<Path> deleted = manager.deleteAll(quarantined);
        
        assertEquals(1, deleted.size());
        assertFalse("Quarantined file should be physically deleted", Files.exists(quarantined.get(0)));
        assertTrue("Active quarantine list should be empty", manager.listQuarantined().isEmpty());
    }
}