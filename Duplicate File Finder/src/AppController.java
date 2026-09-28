import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AppController {
    
    private static final Path SETTINGS_PATH = Path.of("config", "settings.ser");
    
    private final Logger logger;
    private final FileScanner scanner;
    private final DuplicateDetector detector;
    private final QuarantineManager quarantineManager;
    private final ReportGenerator reportGenerator;
    private AppSettings settings;
    
    public AppController(AppSettings settings){
        this.settings = (settings != null) ? settings : new AppSettings();
        this.logger = Logger.getInstance();
        
        this.scanner = new FileScanner();
        this.scanner.setMinSize(this.settings.getMinFileSize());
        this.scanner.setExtensionFilter(this.settings.getExtention);
        
        this.detector = new DuplicateDetector(new HashCalculator(this.settings.getThreadPoolSize()));
        
        this.quarantineManager = new QuarantineManager(Path.of(this.settings.getQuarantine()));
        
        this.reportGenerator = new ReportGenerator();
        
        logger.info("AppController initialised");
    }
    
    public List<DuplicateGroup<File>> scanDirectory(Path directory) throws IOException{
        if (directory == null){
            throw new IllegalArgumentException("Directory path cannot be null");
        }
        if (!Files.exists(directory)){
            throw new IllegalArgumentException("Path does not exist: " + directory);
        }
        if (!Files.isDirectory(directory)){
            throw new IllegalArgumentException("Path is not directory: " + directory);
        }
        if (!Files.isReadable(directory)){
            throw new IllegalArgumentException("Directort is not readable: " + directory);
        }
        
        logger.info("Scanning directoey: " + directory);
        long start = System.currentTimeMillis();
        
        List<File> candidates = scanner.scanDirectory(directory);
        logger.info("Found" + candidates.size() + "candidate files");
        
        List<DuplicateGroup<File>> groups = detector.findDuplicates(candidates);
        
        long elapsed = System.currentTimeMillis() - start;
        logger.info("Scan complete: " + groups.size() + "duplicate groups in" + elapsed + "ms");
        
        settings.setLastScanPath(directory.toString());
        return groups;
    }
    
    //quarantine
    public List<Path> quarantine(List<File> files) throws IOException {
        if (files == null || files.isEmpty()){
            throw new IllegalArgumentException("No files to quarantine");
        }
        
        logger.info("Quarantining " + files.size() + "files");
        List<Path> moved = quarantineManager.quarantineAll(files);
        logger.info("Quarantine completed: " + moved.size() + " file(s) moved");
    }
    
    //restore
    public List<Path> restore(List<Path> quarantinedPaths) throws IOException {
        if (quarantinedPaths == null || quarantinedPaths.isEmpty()){
            throw new IllegalArgumentException("No files to restore");
        }
        
        logger.info("Restoring " +quarantinedPaths.size() + "file(s)");
        List<Path> restored = quarantineManager.restoreAll(quarantinedPaths);
        logger.info("Restore complete: " + restored.size() + "file(s) restored");
        return restored;
    }
    
    //quarantined paths
    public List<Path> listQuarantined(){
        return quarantineManger.listQuarantined();
    }
    
    //reporting
    public enum ExportFormat {CSV, JSON}
    
    public void exportResults(List<DuplicateGroup<Files>> groups, ExportFormat format, Path outputPath) throws IOException{
        if (groups == null || groups.isEmpty()){
            throw new IllegalArgumentException("no resultts to export: ");
        }
        if (outputPath == null){
            throw new IllegalArgumentException("Output path cannot be null");
        }
        
        logger.info("Exporting " + groups.size() + "group(s) as " + format);
        
        switch (format){
            case CSV -> reportGenerator.exportToCSV(groups, outputPath);
            
            case JSON -> reportGenerator.exportToJSON(groups, outputPath);
        }
        
        logger.info("Export complete: " + outputPath);
    }
    
    //duplicate groups
    public void printResults(List<DuplicateGroup<File>> groups){
        reportGenerator.printTOConsole(
        groups != null ? groups : Collections.emptyList());
    }
    
    //settings
    public AppSettings getSettings(){
        return settings;
    }
    
    //save the current setting to disk
    public void saveSettings() throws IOException {
        Files.createDirectories(SETTINGS_PATH.getParent());
        settings.save(SETTINGS_PATH);
        logger.info("Settings saved to " + SETTINGS_PATH);
    }
    
    //loads settings to disk
    public static AppSettings loadSettings(){
        try {
            if (Files.exists(SETTINGS_PATH)){
                return AppSettings.load(SETTINGS_PATH);
            }
        }catch (IOException e){
            logger.getInstance().warn("Unable to load settings, using defaults: " + e.getMessage());
        }
        return new AppSettings();
    }
}
