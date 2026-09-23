package com.dupfinder.core;

import com.dupfinder.hash.HashCalculator;
import com.dupfinder.log.Logger;
import com.dupfinder.quarantine.QuarantineManager;
import com.dupfinder.report.ReportGenerator;
import com.dupfinder.scanner.FileScanner;
import com.dupfinder.settings.AppSettings;
import com.dupfinder.ui.cli.CLIMenu;
import com.dupfinder.ui.gui.MainWindow;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Owner: Member 1 (Ntokozo) - Project Lead / Integrator
 *
 * Wires every component together (FileScanner -> DuplicateDetector ->
 * QuarantineManager / ReportGenerator) and exposes the operations that both
 * the CLI and the GUI call into. Neither CLIMenu nor MainWindow should talk
 * to FileScanner/HashCalculator/etc directly - they go through here, so the
 * two UIs never drift out of sync.
 *
 * Current state: wiring is in place and compiles end-to-end; the individual
 * components are stubs (throw UnsupportedOperationException) until each
 * owner implements their piece on their feature branch. Once a teammate's
 * PR lands on develop, their part of this flow starts working with no
 * changes needed here.
 */
public class AppController {

    private final FileScanner fileScanner = new FileScanner();
    private final HashCalculator hashCalculator = new HashCalculator();
    private final DuplicateDetector duplicateDetector = new DuplicateDetector(hashCalculator);
    private final ReportGenerator reportGenerator = new ReportGenerator();
    private final Logger logger = new Logger(Paths.get("app.log"));

    private AppSettings settings = new AppSettings();
    private QuarantineManager quarantineManager =
            new QuarantineManager(Paths.get(settings.getQuarantineFolder()));

    /** Result of the most recent scan, kept in memory for "View last scan result" / export / quarantine. */
    private List<DuplicateGroup<Path>> lastScanResult;

    // ----- Entry points from MainApp -----

    public void startCli() {
        new CLIMenu(this).run();
    }

    public void startGui() {
        javax.swing.SwingUtilities.invokeLater(() -> new MainWindow(this).display());
    }

    // ----- Operations shared by CLI and GUI -----

    /**
     * Scans {@code rootDirectory}, detects duplicates, and stores the result
     * for later viewing/export/quarantine. Per the design brief, a per-file
     * error should not abort the whole scan - FileScanner and HashCalculator
     * are responsible for catching and logging those internally and skipping
     * the offending file.
     */
    public List<DuplicateGroup<Path>> runScan(Path rootDirectory) {
        try {
            List<Path> allFiles = fileScanner.scan(rootDirectory);
            lastScanResult = duplicateDetector.detect(allFiles);
            return lastScanResult;
        } catch (Exception e) {
            logger.error("Scan failed for " + rootDirectory, e);
            throw new RuntimeException("Scan failed: " + e.getMessage(), e);
        }
    }

    public List<DuplicateGroup<Path>> getLastScanResult() {
        return lastScanResult;
    }

    public void printSummary() {
        reportGenerator.printConsoleSummary(lastScanResult);
    }

    public void exportCsv(Path outputFile) throws Exception {
        reportGenerator.exportCsv(lastScanResult, outputFile);
    }

    public List<Path> quarantine(List<Path> filesToQuarantine) throws Exception {
        List<Path> moved = quarantineManager.quarantine(filesToQuarantine);
        logger.info("Quarantined " + moved.size() + " file(s)");
        return moved;
    }

    public void restore(Path quarantinedFile) throws Exception {
        quarantineManager.restore(quarantinedFile);
        logger.info("Restored " + quarantinedFile);
    }

    public AppSettings getSettings() {
        return settings;
    }

    public void updateSettings(AppSettings newSettings) {
        this.settings = newSettings;
        this.quarantineManager = new QuarantineManager(Paths.get(settings.getQuarantineFolder()));
    }
}
