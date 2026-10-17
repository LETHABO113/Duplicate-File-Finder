package duplicatefilefinder;

import java.io.*;
import java.nio.file.Path;

public class AppSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private int threadPoolSize = 8;
    private long minFileSize = 1;
    private String extention = ".*"; 
    private String quarantine = "./quarantine";
    private String lastScanPath = "";

    public AppSettings() {
    }

    // Getters and Setters matching AppController
    public int getThreadPoolSize() { return threadPoolSize; }
    public void setThreadPoolSize(int threadPoolSize) { this.threadPoolSize = threadPoolSize; }

    public long getMinFileSize() { return minFileSize; }
    public void setMinFileSize(long minFileSize) { this.minFileSize = minFileSize; }

    public String getExtention() { return extention; }
    public void setExtention(String extention) { this.extention = extention; }

    public String getQuarantine() { return quarantine; }
    public void setQuarantine(String quarantine) { this.quarantine = quarantine; }

    public String getLastScanPath() { return lastScanPath; }
    public void setLastScanPath(String lastScanPath) { this.lastScanPath = lastScanPath; }

    // Save settings to disk
    public void save(Path path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(this);
        }
    }

    // Load settings from disk
    public static AppSettings load(Path path) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (AppSettings) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to load settings class", e);
        }
    }
}