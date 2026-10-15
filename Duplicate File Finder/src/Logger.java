package duplicatefilefinder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {
    private static final Logger INSTANCE = new Logger();
    private static final DateTimeFormatter FORMART = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Path logFile;
    
    private Logger(){
        this.logFile = Path.of("logs", "diff.log");
        try{
            Files.createDirectories(logFile.getParent());
        }catch(IOException e){
            System.err.println("Unable to create log directory: " +e.getMessage());
        }
    }
    
    public static Logger getInstance(){
        return INSTANCE;
    }
    
    public void info(String message){
        write("INFO", message);
    }
    
    public void warn(String message){
        write("WARN", message);
    }
    
    public void error(String message, Throwable t){
        write("ERROR", message + (t != null ? " - " + t.getMessage(): " "));
    }
    
    private synchronized void write(String level, String message){
        String line = "[" + LocalDateTime.now().format(FORMART) + "]" + level + " - " + message;
        System.out.println(line);
        
        try{
            Files.writeString(logFile, line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }catch (IOException e){
            System.err.print("Unable to write to log file: " + e.getMessage());
        }
    }
}
