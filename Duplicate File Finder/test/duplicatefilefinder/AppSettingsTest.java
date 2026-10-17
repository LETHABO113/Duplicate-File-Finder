package duplicatefilefinder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class AppSettingsTest {

    @Test
    public void testSaveAndLoadSerialization(@TempDir Path tempDir) throws Exception {
        // 1. Setup specific test data
        Path testFile = tempDir.resolve("test_settings.ser");
        AppSettings originalSettings = new AppSettings();
        
        originalSettings.setThreadPoolSize(12);
        originalSettings.setMinFileSize(500);
        originalSettings.setExtention(".pdf");
        originalSettings.setQuarantine("./safe_zone");
        originalSettings.setLastScanPath("C:/test/path");

        // 2. Save to disk
        originalSettings.save(testFile);

        // 3. Load from disk
        AppSettings loadedSettings = AppSettings.load(testFile);

        // 4. Verify all fields match exactly
        assertNotNull(loadedSettings, "Loaded settings should not be null");
        assertEquals(12, loadedSettings.getThreadPoolSize(), "Thread pool size mismatch");
        assertEquals(500, loadedSettings.getMinFileSize(), "Minimum file size mismatch");
        assertEquals(".pdf", loadedSettings.getExtention(), "Extension filter mismatch");
        assertEquals("./safe_zone", loadedSettings.getQuarantine(), "Quarantine path mismatch");
        assertEquals("C:/test/path", loadedSettings.getLastScanPath(), "Last scan path mismatch");
    }
}