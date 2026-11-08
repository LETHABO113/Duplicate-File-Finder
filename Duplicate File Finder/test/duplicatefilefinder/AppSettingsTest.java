package duplicatefilefinder;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import java.io.IOException;
import java.nio.file.Path;
import static org.junit.Assert.*;

public class AppSettingsTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void defaults_AreSensible() {
        AppSettings settings = new AppSettings();
        assertEquals(1L, settings.getMinFileSize());
        assertEquals("quarantine", settings.getQuarantine());
        assertTrue(settings.getThreadPoolSize() >= 1);
    }

    @Test
    public void setters_UpdateValues() {
        AppSettings settings = new AppSettings();
        settings.setMinFileSize(500L);
        assertEquals(500L, settings.getMinFileSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setters_RejectsNegativeMinFileSize() {
        new AppSettings().setMinFileSize(-1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setters_RejectsZeroThreadPoolSize() {
        new AppSettings().setThreadPoolSize(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setters_RejectsBlankQuarantine() {
        new AppSettings().setQuarantine("   ");
    }

    @Test
    public void saveAndLoad_RoundTripsCorrectly() throws IOException {
        Path file = tempDir.getRoot().toPath().resolve("settings.ser");
        AppSettings original = new AppSettings();
        original.setMinFileSize(1024L);
        original.setQuarantine("custom_quarantine");
        original.setExtensionFilter("pdf");
        
        original.save(file);
        
        AppSettings loaded = AppSettings.load(file);
        assertEquals(1024L, loaded.getMinFileSize());
        assertEquals("custom_quarantine", loaded.getQuarantine());
        assertEquals("pdf", loaded.getExtensionFilter());
    }
}