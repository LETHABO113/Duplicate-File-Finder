package duplicatefilefinder;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuplicateGroup<T> implements Serializable, Comparable<DuplicateGroup<T>> {

    private static final long serialVersionUID = 1L;

    private final String hash;
    private final long fileSize;
    private final List<T> files;

    public DuplicateGroup(String hash, long fileSize, List<T> files) {
        this.hash = hash;
        this.fileSize = fileSize;
        this.files = files != null ? new ArrayList<>(files) : new ArrayList<>();
    }

    
    public DuplicateGroup(String hash, List<T> files) {
        this.hash = hash;
        this.files = files != null ? new ArrayList<>(files) : new ArrayList<>();
        if (!this.files.isEmpty() && this.files.get(0) instanceof File firstFile) {
            this.fileSize = firstFile.length();
        } else {
            this.fileSize = 0L;
        }
    }

    public String getHash() {
        return hash;
    }

    public long getFileSize() {
        return fileSize;
    }

    public List<T> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public int getCount() {
        return files.size();
    }

  
    public long getWastedSpace() {
        if (files.size() <= 1) {
            return 0L;
        }
        return fileSize * (files.size() - 1);
    }

    @Override
    public int compareTo(DuplicateGroup<T> other) {
        return Long.compare(other.getWastedSpace(), this.getWastedSpace());
    }

    @Override
    public String toString() {
        return String.format("[%s] %d files | %d bytes each | %d bytes wasted",
                hash, getCount(), fileSize, getWastedSpace());
    }
}