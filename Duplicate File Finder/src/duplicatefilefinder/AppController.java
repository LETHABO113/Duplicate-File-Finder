package duplicatefilefinder;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AppController implements AutoCloseable {
    private static final Path SETTINGS_PATH = Path.of("config", "settings.ser");

    private final Logger logger;
    private final AppSettings settings;
    private FileScanner scanner;
    private HashCalculator hashCalculator;
    private DuplicateDetector detector;
    private QuarantineManager quarantineManager;
    private int configuredThreadPoolSize;
    private long configuredMinFileSize;
    private String configuredExtensionFilter;
    private String configuredQuarantine;

    public AppController(AppSettings settings) {
        this.settings = settings == null ? new AppSettings() : settings;
        this.logger = Logger.getInstance();
        refreshServices();
        logger.info("AppController initialized");
    }

    public List<DuplicateGroup<File>> scanDirectory(Path directory) throws IOException {
        if (directory == null) {
            throw new IllegalArgumentException("Directory path cannot be null");
        }
        if (!Files.exists(directory)) {
            throw new IllegalArgumentException("Path does not exist: " + directory);
        }
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Path is not a directory: " + directory);
        }
        if (!Files.isReadable(directory)) {
            throw new IllegalArgumentException("Directory is not readable: " + directory);
        }

        refreshServices();
        logger.info("Scanning directory: " + directory);
        long start = System.currentTimeMillis();

        List<File> candidates = scanner.scanDirectory(directory).stream()
                .map(Path::toFile)
                .toList();
        logger.info("Found " + candidates.size() + " candidate files");

        List<DuplicateGroup<File>> groups;
        try {
            groups = detector.findDuplicates(candidates);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }

        long elapsed = System.currentTimeMillis() - start;
        logger.info("Scan complete: " + groups.size() + " duplicate groups in " + elapsed + " ms");
        settings.setLastScanPath(directory.toString());
        return groups;
    }

    public List<Path> quarantine(List<File> files) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("No files to quarantine");
        }

        refreshServices();
        logger.info("Quarantining " + files.size() + " file(s)");
        List<Path> moved = quarantineManager.quarantineAll(files);
        logger.info("Quarantine completed: " + moved.size() + " file(s) moved");
        return moved;
    }

    public List<Path> restore(List<Path> quarantinedPaths) throws IOException {
        if (quarantinedPaths == null || quarantinedPaths.isEmpty()) {
            throw new IllegalArgumentException("No files to restore");
        }

        refreshServices();
        logger.info("Restoring " + quarantinedPaths.size() + " file(s)");
        List<Path> restored = quarantineManager.restoreAll(quarantinedPaths);
        logger.info("Restore complete: " + restored.size() + " file(s) restored");
        return restored;
    }

    public List<Path> listQuarantined() {
        refreshServices();
        return quarantineManager.listQuarantined();
    }

    public enum ExportFormat {
        CSV,
        JSON
    }

    public void exportResults(
            List<DuplicateGroup<File>> groups,
            ExportFormat format,
            Path outputPath) throws IOException {
        if (groups == null || groups.isEmpty()) {
            throw new IllegalArgumentException("No results to export");
        }
        if (format == null) {
            throw new IllegalArgumentException("Export format cannot be null");
        }
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path cannot be null");
        }

        ReportGenerator reportGenerator = new ReportGenerator();
        logger.info("Exporting " + groups.size() + " group(s) as " + format);
        switch (format) {
            case CSV -> reportGenerator.exportToCsv(groups, outputPath);
            case JSON -> reportGenerator.exportToJson(groups, outputPath);
        }
        logger.info("Export complete: " + outputPath);
    }

    public void printResults(List<DuplicateGroup<File>> groups) {
        new ReportGenerator().printToConsole(
                groups == null ? Collections.emptyList() : groups);
    }

    public AppSettings getSettings() {
        return settings;
    }

    public void saveSettings() throws IOException {
        Files.createDirectories(SETTINGS_PATH.getParent());
        settings.save(SETTINGS_PATH);
        logger.info("Settings saved to " + SETTINGS_PATH);
    }

    public static AppSettings loadSettings() {
        try {
            if (Files.exists(SETTINGS_PATH)) {
                return AppSettings.load(SETTINGS_PATH);
            }
        } catch (IOException e) {
            Logger.getInstance().warn("Unable to load settings, using defaults: " + e.getMessage());
        }
        return new AppSettings();
    }

    private void refreshServices() {
        int threadPoolSize = settings.getThreadPoolSize();
        long minFileSize = settings.getMinFileSize();
        String extensionFilter = settings.getExtensionFilter();
        String quarantine = settings.getQuarantine();

        if (hashCalculator != null
                && configuredThreadPoolSize == threadPoolSize
                && configuredMinFileSize == minFileSize
                && Objects.equals(configuredExtensionFilter, extensionFilter)
                && Objects.equals(configuredQuarantine, quarantine)) {
            return;
        }

        if (hashCalculator != null) {
            hashCalculator.close();
        }

        hashCalculator = new HashCalculator(threadPoolSize);
        detector = new DuplicateDetector(hashCalculator);
        scanner = new FileScanner(minFileSize, extensionFilter);
        quarantineManager = new QuarantineManager(Path.of(quarantine));
        configuredThreadPoolSize = threadPoolSize;
        configuredMinFileSize = minFileSize;
        configuredExtensionFilter = extensionFilter;
        configuredQuarantine = quarantine;
    }

    @Override
    public void close() {
        if (hashCalculator != null) {
            hashCalculator.close();
            hashCalculator = null;
        }
    }
}