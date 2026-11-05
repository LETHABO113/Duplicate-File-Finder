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

public class AppControllerTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void scanDirectory_FindsDuplicatesAndUpdateSettings() throws IOException {
        Path scanDir = tempDir.newFolder("scan_target").toPath();
        Path qDir = tempDir.newFolder("quarantine").toPath();
        
        Files.writeString(scanDir.resolve("dup1.txt"), "same", StandardCharsets.UTF_8);
        Files.writeString(scanDir.resolve("dup2.txt"), "same", StandardCharsets.UTF_8);
        Files.writeString(scanDir.resolve("unique.txt"), "unique", StandardCharsets.UTF_8);

        AppSettings settings = new AppSettings();
        settings.setQuarantine(qDir.toAbsolutePath().toString());

        try (AppController controller = new AppController(settings)) {
            List<DuplicateGroup<File>> groups = controller.scanDirectory(scanDir);
            
            assertEquals(1, groups.size());
            assertEquals(2, groups.get(0).getCount());
            assertEquals(scanDir.toAbsolutePath().toString(), settings.getLastScanPath());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void scanDirectory_RejectsNullPath() throws IOException {
        AppSettings settings = new AppSettings();
        settings.setQuarantine(tempDir.newFolder("quarantine").toPath().toAbsolutePath().toString());

        try (AppController controller = new AppController(settings)) {
            controller.scanDirectory(null);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void scanDirectory_RejectsMissingPath() throws IOException {
        AppSettings settings = new AppSettings();
        settings.setQuarantine(tempDir.newFolder("quarantine").toPath().toAbsolutePath().toString());

        try (AppController controller = new AppController(settings)) {
            controller.scanDirectory(tempDir.getRoot().toPath().resolve("ghost"));
        }
    }

    @Test
    public void exportResults_WritesFiles() throws IOException {
        Path qDir = tempDir.newFolder("quarantine").toPath();
        AppSettings settings = new AppSettings();
        settings.setQuarantine(qDir.toAbsolutePath().toString());

        try (AppController controller = new AppController(settings)) {
            DuplicateGroup<File> group = new DuplicateGroup<>("hash", 100L, List.of(tempDir.newFile("dup.txt")));
            Path exportPath = tempDir.getRoot().toPath().resolve("export.csv");
            
            controller.exportResults(List.of(group), AppController.ExportFormat.CSV, exportPath);
            assertTrue(Files.exists(exportPath));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void exportResults_RejectsNullFormat() throws IOException {
        Path qDir = tempDir.newFolder("quarantine").toPath();
        AppSettings settings = new AppSettings();
        settings.setQuarantine(qDir.toAbsolutePath().toString());

        try (AppController controller = new AppController(settings)) {
            DuplicateGroup<File> group = new DuplicateGroup<>("hash", 100L, List.of(tempDir.newFile("dup.txt")));
            Path exportPath = tempDir.getRoot().toPath().resolve("export.csv");
            
            controller.exportResults(List.of(group), null, exportPath);
        }
    }
}