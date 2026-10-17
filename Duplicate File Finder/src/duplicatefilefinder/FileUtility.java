package duplicatefilefinder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtility {

    private FileUtility(){
        throw new AssertionError("do not instantiate the utility class", null);
    }

    public static String formatSize(long bytes){
        if (bytes < 0){
            return "0 B";
        }
        if (bytes < 1024){
            return bytes + " B";
        }
        else if (bytes < 1024 * 1024){
            return String.format("%.2f KB", bytes / 1024);
        }
        else if (bytes < 1024 * 1024 * 1024){
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        }
        else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    public static void ensureDirectory(Path dir){
        if (dir == null){
            throw new IllegalArgumentException("Null dir");
        }
        if (Files.exists(dir)) {
            if (!File.isDirectory(dir)){
                throw new IllegalArgumentException("Not a dir: " + dir);
                return ;
            }
        }    
        Files.createDirectories(dir);
    }

    public static boolean isAccessible(File file){
        return file != null && file.exists() && file.canRead();
    }

    public static Path getCanonicalPath(File file) throws IOException{
        if (file == null){
            throw new IllegalArgumentException("Null file");
            return file.getCanonicalFile().toPath();
        }
    }

    public static String getExtentsion(File file){
        if (file == null){
            return "";
        }
        String name = file.getName();
        int dot = name.lastIndexOf('.');

        if (dot < 0 || dot == name.length() - 1){
            return "";
        }
        return name.substring(dot + 1).toLowerCase();
    }

    public static boolean isSumLink(Path path){
        return path != null && Files.isSymbolicLink(path);
    }

    public static boolean isRegularFile(File file){
        return file != null && file.isFile() && !Files.isSymbolicLink(file.toPath());
    }
}