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

public class DuplicateDetectorTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void findDuplicates_NullOrEmptyList_ReturnsEmpty() {
        DuplicateDetector detector = new DuplicateDetector();
        assertTrue(detector.findDuplicates(null).isEmpty());
        assertTrue(detector.findDuplicates(List.of()).isEmpty());
    }

    @Test
    public void findDuplicates_IdenticalContent_GroupsTogether() throws IOException {
        Path root = tempDir.getRoot().toPath();
        Path file1 = root.resolve("file1.txt");
        Path file2 = root.resolve("file2.txt");
        Path file3 = root.resolve("file3.txt");
        
        // file1 and file2 are identical, file3 is different but same size (to bypass size filter)
        Files.writeString(file1, "duplicate", StandardCharsets.UTF_8);
        Files.writeString(file2, "duplicate", StandardCharsets.UTF_8);
        Files.writeString(file3, "different", StandardCharsets.UTF_8);

        DuplicateDetector detector = new DuplicateDetector();
        List<DuplicateGroup<File>> groups = detector.findDuplicates(
                List.of(file1.toFile(), file2.toFile(), file3.toFile()));

        // Should only find 1 group (file1 and file2)
        assertEquals(1, groups.size());
        DuplicateGroup<File> group = groups.get(0);
        assertEquals(2, group.getCount());
        assertTrue(group.getFiles().contains(file1.toFile()));
        assertTrue(group.getFiles().contains(file2.toFile()));
        assertFalse(group.getFiles().contains(file3.toFile()));
    }

    @Test
    public void groupBySize_IgnoresMissingOrNonRegularFiles() throws IOException {
        Path root = tempDir.getRoot().toPath();
        Path realFile = root.resolve("real.txt");
        Files.writeString(realFile, "content", StandardCharsets.UTF_8);
        
        File missingFile = root.resolve("missing.txt").toFile();
        File directory = root.toFile();

        DuplicateDetector detector = new DuplicateDetector();
        var sizeGroups = detector.groupBySize(List.of(realFile.toFile(), missingFile, directory));

        // Should only contain the real file
        assertEquals(1, sizeGroups.size());
        assertEquals(1, sizeGroups.values().iterator().next().size());
        assertEquals(realFile.toFile(), sizeGroups.values().iterator().next().get(0));
    }
}