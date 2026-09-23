package com.dupfinder.settings;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Owner: Member 7 (Amogelang) - Testing & QA Lead (backup role: Logger, AppSettings)
 *
 * Saves/loads user settings (e.g. last scanned folder, quarantine folder
 * location, extension filters) - this is the "Serialization" syllabus item.
 *
 * TODO (Member 7):
 *   - Make this class implement Serializable and pick the fields that matter
 *     (quarantine folder path, extension filter regex, min file size, etc.).
 *   - Implement save()/load() using ObjectOutputStream / ObjectInputStream,
 *     or switch to a simple JSON file if the team prefers that over binary
 *     serialization - either satisfies the syllabus requirement, just be
 *     consistent and document the choice in the design doc.
 */
public class AppSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private String quarantineFolder = "quarantine";
    private String extensionFilterRegex = "";
    private long minFileSizeBytes = 0;

    public static AppSettings load(Path settingsFile) throws IOException {
        throw new UnsupportedOperationException("TODO: implement in AppSettings (Member 7)");
    }

    public void save(Path settingsFile) throws IOException {
        throw new UnsupportedOperationException("TODO: implement in AppSettings (Member 7)");
    }

    public String getQuarantineFolder() {
        return quarantineFolder;
    }

    public String getExtensionFilterRegex() {
        return extensionFilterRegex;
    }

    public long getMinFileSizeBytes() {
        return minFileSizeBytes;
    }
}
