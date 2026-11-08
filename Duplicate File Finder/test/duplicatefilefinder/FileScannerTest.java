package duplicatefilefinder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.assertEquals;

public class FileScannerTest {
    
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void findsMatchingFilesRecursively() throws IOException {
        Path tempDir = temporaryFolder.getRoot().toPath();
        Path nested = Files.createDirectories(tempDir.resolve("nested"));
        Path topLevelFile = tempDir.resolve("first.txt");
        Path nestedFile = nested.resolve("second.txt");
        
        Files.writeString(topLevelFile, "same content", StandardCharsets.UTF_8);
        Files.writeString(nestedFile, "same content", StandardCharsets.UTF_8);

        FileScanner scanner = new FileScanner(1, "txt");

        assertEquals(2, scanner.scanDirectory(tempDir).size());
    }
}