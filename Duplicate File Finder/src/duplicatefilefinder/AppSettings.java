package duplicatefilefinder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private int threadPoolSize = Math.max(1, Runtime.getRuntime().availableProcessors());
    private long minFileSize = 1;
    private String extensionFilter = "";
    private String quarantine = "quarantine";
    private String lastScanPath = "";

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        if (threadPoolSize < 1) {
            throw new IllegalArgumentException("Thread pool size must be at least 1");
        }
        this.threadPoolSize = threadPoolSize;
    }

    public long getMinFileSize() {
        return minFileSize;
    }

    public void setMinFileSize(long minFileSize) {
        if (minFileSize < 0) {
            throw new IllegalArgumentException("Minimum file size cannot be negative");
        }
        this.minFileSize = minFileSize;
    }

    public String getExtensionFilter() {
        return extensionFilter;
    }

    public void setExtensionFilter(String extensionFilter) {
        this.extensionFilter = extensionFilter == null ? "" : extensionFilter.trim();
    }

    public String getQuarantine() {
        return quarantine;
    }

    public void setQuarantine(String quarantine) {
        if (quarantine == null || quarantine.isBlank()) {
            throw new IllegalArgumentException("Quarantine directory cannot be empty");
        }
        this.quarantine = quarantine.trim();
    }

    public String getLastScanPath() {
        return lastScanPath;
    }

    public void setLastScanPath(String lastScanPath) {
        this.lastScanPath = lastScanPath == null ? "" : lastScanPath;
    }

    public void save(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Settings path cannot be null");
        }
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(path))) {
            output.writeObject(this);
        }
    }

    public static AppSettings load(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Settings path cannot be null");
        }
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
            Object savedSettings = input.readObject();
            if (savedSettings instanceof AppSettings settings) {
                return settings;
            }
            throw new IOException("The settings file has an unexpected format");
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not read saved settings", e);
        }
    }
}
