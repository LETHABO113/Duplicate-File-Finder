package duplicatefilefinder;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
public class FileScanner {
    private long minSize;                   
    private Pattern extensionPattern;        
    private boolean followSymlinks = false;  
    private final Set<Path> visitedPath = new HashSet<>();  

 
    public FileScanner(long minSize, String extensionRegex) {
        setMinSize(minSize);
        setExtensionFilter(extensionRegex);
    }
   
    public List<Path> scanDirectory(Path root) throws IOException {
        if (!Files.isDirectory(root)) {
            throw new IOException("Not a directory: " + root);
        }
        visitedPath.clear();                
        List<Path> result = new ArrayList<>();
        walkDirectory(root, result);
        return result;
    }
    
    public void walkDirectory(Path folder, List<Path> output) {

        
        try {
            Path realFolder = folder.toRealPath();
            if (!visitedPath.add(realFolder)) {
                return;
            }
        } catch (IOException e) {
            Logger.getInstance().warn("Cannot access " + folder + ": " + e.getMessage());
            return;
        }
       
        try (DirectoryStream<Path> items = Files.newDirectoryStream(folder)) {
            for (Path item : items) {

                if (Files.isSymbolicLink(item) && !followSymlinks) {
                    continue;                                
                }

                if (Files.isDirectory(item)) {
                    walkDirectory(item, output);              
                } else if (Files.isRegularFile(item) && matchesFilter(item)) {
                    output.add(item.toAbsolutePath().normalize());
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
            Logger.getInstance().warn("Cannot read " + folder + ": " + e.getMessage());
        }
    }

   
    public boolean matchesFilter(Path file) {
        try {
            if (Files.size(file) < minSize) {
                return false;
            }
        } catch (IOException e) {
            return false;                                   
        }

        if (extensionPattern == null) {
            return true;                                  
        }
        return extensionPattern.matcher(getExtension(file)).matches();
    }

    
    private String getExtension(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        if (dot <= 0 || dot == name.length() - 1) {
            return "";
        }
        return name.substring(dot + 1);
    }

    public final void setMinSize(long minSize) {
        if (minSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        this.minSize = minSize;
    }

   
    public final void setExtensionFilter(String regex) {
        if (regex == null || regex.isBlank()) {
            this.extensionPattern = null;
        } else {
            this.extensionPattern = Pattern.compile(regex.trim(), Pattern.CASE_INSENSITIVE);
        }
    }

    public void setFollowSymlinks(boolean follow) {
        this.followSymlinks = follow;
    }
}

