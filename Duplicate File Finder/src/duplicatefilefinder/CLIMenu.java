package duplicatefilefinder;

<<<<<<< HEAD
import java.util.Scanner;
import java.io.File;
import java.util.List;

public class CLIMenu {
    

    private Scanner input;
    private AppController controller;

    public CLIMenu(AppController controller) {
=======
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class CLIMenu {
    private final AppController controller;
    private final Scanner input;
    private List<DuplicateGroup<File>> lastScanResults = List.of();
    private boolean hasScanned;

    public CLIMenu(AppController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null");
        }
>>>>>>> 1a6b1e78bfe5e6c547b795375ca74e49f62a48bc
        this.controller = controller;
        this.input = new Scanner(System.in);
    }

<<<<<<< HEAD
    public void startMenu() {

        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("      DUPLICATE FILE FINDER");
            System.out.println("================================");
            System.out.println("1. Scan a folder");
            System.out.println("2. View duplicate files");
            System.out.println("3. Quarantine duplicates");
            System.out.println("4. Generate report");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String choice = input.nextLine().trim();

            switch (choice) {

                case "1":
                    scanFolder();
                    break;

                case "2":
                    viewDuplicates();
                    break;

                case "3":
                    quarantineDuplicates();
                    break;

                case "4":
                    generateReport();
                    break;

                case "5":
                    running = false;
                    System.out.println("Thank you for using Duplicate File Finder!");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter 1-5.");
            }
        }
    }

    private void scanFolder() {

        System.out.print("Enter folder path: ");
        String folderPath = input.nextLine().trim();

        if (folderPath.isEmpty()) {
            System.out.println("Folder path cannot be empty.");
            return;
        }

        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder. Please try again.");
=======
    public void launch() {
        boolean running = true;
        while (running && input.hasNextLine()) {
            showMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> scanDirectory();
                case 2 -> showLastScan();
                case 3 -> exportResults();
                case 4 -> quarantineFiles();
                case 5 -> restoreFiles();
                case 6 -> showSettings();
                case 7 -> running = false;
                default -> System.out.println("Choose a number from 1 to 7.");
            }
        }

        System.out.println("Duplicate File Finder closed.");
    }

    private void showMenu() {
        System.out.println();
        System.out.println("==== Duplicate File Finder ====");
        System.out.println("1. Scan directory");
        System.out.println("2. View last scan result");
        System.out.println("3. Export results");
        System.out.println("4. Quarantine files");
        System.out.println("5. Restore from quarantine");
        System.out.println("6. Settings");
        System.out.println("7. Exit");
    }

    private int readMenuChoice() {
        System.out.print("Choose an option: ");
        String answer = readLine().trim();
        try {
            return Integer.parseInt(answer);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void scanDirectory() {
        System.out.print("Folder to scan: ");
        String folder = readLine().trim();
        if (folder.isEmpty()) {
            System.out.println("Enter a folder path to start the scan.");
>>>>>>> 1a6b1e78bfe5e6c547b795375ca74e49f62a48bc
            return;
        }

        try {
<<<<<<< HEAD
            controller.scanDirectory(folder.toPath());
            System.out.println("Folder scanning completed!");

        } catch (Exception e) {
            System.out.println("Scanning failed: " + e.getMessage());
        }
    }

    private void viewDuplicates() {

        // Retrieve duplicate groups from the controller.
        controller.printResults(groups);
    }

    private void quarantineDuplicates() {

        System.out.print("Are you sure you want to quarantine duplicates? (yes/no): ");

        String answer = input.nextLine().trim();

        if (answer.equalsIgnoreCase("yes")) {
            controller.quarantineDuplicates();
        } else {
            System.out.println("Quarantine cancelled.");
        }
    }

    private void generateReport() {

        System.out.println("\nChoose report format:");
        System.out.println("1. Console");
        System.out.println("2. CSV");
        System.out.println("3. JSON");

        String format = input.nextLine().trim();

        switch (format) {

            case "1":
                controller.generateConsoleReport();
                break;

            case "2":
                controller.generateCsvReport();
                break;

            case "3":
                controller.generateJsonReport();
                break;

            default:
                System.out.println("Invalid report format.");
        }
    }
=======
            Path directory = Path.of(folder);
            lastScanResults = controller.scanDirectory(directory);
            hasScanned = true;
            controller.printResults(lastScanResults);
        } catch (InvalidPathException | IOException e) {
            System.out.println("Could not scan that folder: " + e.getMessage());
        }
    }

    private void showLastScan() {
        if (!hasScanned) {
            System.out.println("There are no scan results to show. Scan a folder first.");
            return;
        }
        if (lastScanResults.isEmpty()) {
            System.out.println("The last scan found no duplicate groups.");
            return;
        }
        controller.printResults(lastScanResults);
    }

    private void exportResults() {
        if (!hasScanned || lastScanResults.isEmpty()) {
            System.out.println("There are no scan results to export. Scan a folder first.");
            return;
        }

        System.out.print("Export format (CSV or JSON): ");
        String formatAnswer = readLine().trim();
        AppController.ExportFormat format;
        if (formatAnswer.equalsIgnoreCase("CSV")) {
            format = AppController.ExportFormat.CSV;
        } else if (formatAnswer.equalsIgnoreCase("JSON")) {
            format = AppController.ExportFormat.JSON;
        } else {
            System.out.println("Choose CSV or JSON.");
            return;
        }

        System.out.print("Save file path: ");
        String output = readLine().trim();
        if (output.isEmpty()) {
            System.out.println("Enter a file path for the export.");
            return;
        }

        try {
            controller.exportResults(lastScanResults, format, Path.of(output));
            System.out.println("Results exported to " + output);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Could not export the results: " + e.getMessage());
        }
    }

    private void quarantineFiles() {
        if (!hasScanned) {
            System.out.println("Scan a folder first, then enter the paths you want to quarantine.");
            return;
        }
        if (lastScanResults.isEmpty()) {
            System.out.println("The last scan found no duplicate files to quarantine.");
            return;
        }

        System.out.println("Enter each duplicate file path on its own line.");
        System.out.println("Press Enter on an empty line when you are done.");
        List<File> files = new ArrayList<>();
        while (true) {
            System.out.print("File path: ");
            String pathText = readLine().trim();
            if (pathText.isEmpty()) {
                break;
            }
            try {
                files.add(Path.of(pathText).toFile());
            } catch (InvalidPathException e) {
                System.out.println("That path is not valid.");
            }
        }

        if (files.isEmpty()) {
            System.out.println("No files were selected.");
            return;
        }
        if (!confirm("Move " + files.size() + " file(s) into quarantine? (y/n): ")) {
            System.out.println("Quarantine cancelled.");
            return;
        }

        try {
            List<Path> movedFiles = controller.quarantine(files);
            System.out.println(movedFiles.size() + " file(s) moved to quarantine.");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Could not quarantine the selected files: " + e.getMessage());
        }
    }

    private void restoreFiles() {
        List<Path> quarantinedFiles = controller.listQuarantined();
        if (quarantinedFiles.isEmpty()) {
            System.out.println("There are no files in quarantine.");
            return;
        }

        System.out.println("Files in quarantine:");
        for (int i = 0; i < quarantinedFiles.size(); i++) {
            System.out.println((i + 1) + ". " + quarantinedFiles.get(i));
        }
        System.out.print("Enter the file numbers to restore, separated by commas: ");
        String answer = readLine().trim();
        List<Path> selectedFiles = selectQuarantinedFiles(answer, quarantinedFiles);
        if (selectedFiles.isEmpty()) {
            return;
        }
        if (!confirm("Restore " + selectedFiles.size() + " file(s)? (y/n): ")) {
            System.out.println("Restore cancelled.");
            return;
        }

        try {
            List<Path> restoredFiles = controller.restore(selectedFiles);
            System.out.println(restoredFiles.size() + " file(s) restored.");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Could not restore the selected files: " + e.getMessage());
        }
    }

    private List<Path> selectQuarantinedFiles(String answer, List<Path> quarantinedFiles) {
        if (answer.isEmpty()) {
            System.out.println("No files were selected.");
            return List.of();
        }

        Set<Integer> selectedNumbers = new LinkedHashSet<>();
        for (String part : answer.split(",")) {
            try {
                int number = Integer.parseInt(part.trim());
                if (number < 1 || number > quarantinedFiles.size()) {
                    System.out.println("Choose numbers from 1 to " + quarantinedFiles.size() + ".");
                    return List.of();
                }
                selectedNumbers.add(number);
            } catch (NumberFormatException e) {
                System.out.println("Enter file numbers separated by commas, for example: 1, 3");
                return List.of();
            }
        }

        List<Path> selectedFiles = new ArrayList<>(selectedNumbers.size());
        for (int number : selectedNumbers) {
            selectedFiles.add(quarantinedFiles.get(number - 1));
        }
        return selectedFiles;
    }

    private void showSettings() {
        AppSettings settings = controller.getSettings();
        System.out.println("Minimum file size: " + settings.getMinFileSize() + " bytes");
        System.out.println("Hashing threads: " + settings.getThreadPoolSize());
        System.out.println("Quarantine folder: " + settings.getQuarantine());
        if (confirm("Save current settings? (y/n): ")) {
            try {
                controller.saveSettings();
                System.out.println("Settings saved.");
            } catch (IOException e) {
                System.out.println("Could not save settings: " + e.getMessage());
            }
        }
    }

    private boolean confirm(String prompt) {
        System.out.print(prompt);
        String answer = readLine().trim();
        return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
    }

    private String readLine() {
        return input.hasNextLine() ? input.nextLine() : "";
    }
>>>>>>> 1a6b1e78bfe5e6c547b795375ca74e49f62a48bc
}


