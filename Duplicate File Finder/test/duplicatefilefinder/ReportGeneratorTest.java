package duplicatefilefinder;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.Assert.*;

public class ReportGeneratorTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void exportToCsv_WritesExpectedContent() throws IOException {
        Path output = tempDir.getRoot().toPath().resolve("report.csv");
        ReportGenerator generator = new ReportGenerator();
        
        DuplicateGroup<File> group = new DuplicateGroup<>("hash123", 100L, List.of(new File("test.txt")));
        generator.exportToCsv(List.of(group), output);
        
        assertTrue(Files.exists(output));
        String content = Files.readString(output);
        assertTrue(content.contains("Group,Hash,File Size (Bytes)"));
        assertTrue(content.contains("hash123"));
        assertTrue(content.contains("test.txt"));
    }

    @Test
    public void exportToJson_WritesExpectedContent() throws IOException {
        Path output = tempDir.getRoot().toPath().resolve("report.json");
        ReportGenerator generator = new ReportGenerator();
        
        DuplicateGroup<File> group = new DuplicateGroup<>("hash123", 100L, List.of(new File("test.txt")));
        generator.exportToJson(List.of(group), output);
        
        assertTrue(Files.exists(output));
        String content = Files.readString(output);
        assertTrue(content.contains("\"duplicateGroups\": ["));
        assertTrue(content.contains("\"hash\": \"hash123\""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exportToCsv_NullList_ThrowsException() throws IOException {
        new ReportGenerator().exportToCsv(null, tempDir.getRoot().toPath().resolve("out.csv"));
    }
}