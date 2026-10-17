package duplicatefilefinder;

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
        this.controller = controller;
        this.input = new Scanner(System.in);
    }

    public void launch() {
        boolean running = true;
        while (running && input.hasNextLine()) {
            showMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> scanDirectory();
                case 2 -> showLastScan();
                case 3 -> exportResults();
                case 4 -> quarantineDuplicates();
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
        System.out.println("4. Quarantine duplicates");
        System.out.println("5. Restore from quarantine");
        System.out.println("6. Settings");
        System.out.println("7. Exit");
    }

    private int readMenuChoice() {
        System.out.print("Choose an option: ");
        try {
            return Integer.parseInt(readLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void scanDirectory() {
        System.out.print("Folder to scan: ");
        String folder = readLine().trim();
        if (folder.isEmpty()) {
            System.out.println("Enter a folder path to start the scan.");
            return;
        }

        try {
            lastScanResults = controller.scanDirectory(Path.of(folder));
            hasScanned = true;
            showResults(lastScanResults);
        } catch (InvalidPathException | IOException e) {
            System.out.println("Could not scan that folder: " + e.getMessage());
        }
    }

    private void showLastScan() {
        if (!hasScanned) {
            System.out.println("There are no scan results to show. Scan a folder first.");
            return;
        }
        showResults(lastScanResults);
    }

    private void showResults(List<DuplicateGroup<File>> groups) {
        if (groups.isEmpty()) {
            System.out.println("No duplicate files were found.");
            return;
        }

        System.out.println("Duplicate groups found: " + groups.size());
        for (int i = 0; i < groups.size(); i++) {
            DuplicateGroup<File> group = groups.get(i);
            System.out.println();
            System.out.println("Group " + (i + 1) + " - " + group.getCount()
                    + " files, " + group.getWastedSpace() + " bytes wasted");
            System.out.println("SHA-256: " + group.getHash());
            for (File file : group.getFiles()) {
                System.out.println("  " + file.getAbsolutePath());
            }
        }
    }

    private void exportResults() {
        if (!hasScanned || lastScanResults.isEmpty()) {
            System.out.println("There are no duplicate results to export.");
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

    private void quarantineDuplicates() {
        if (!hasScanned || lastScanResults.isEmpty()) {
            System.out.println("Scan a folder with duplicates first.");
            return;
        }

        List<File> duplicateFiles = new ArrayList<>();
        List<Integer> groupNumbers = new ArrayList<>();
        for (int i = 0; i < lastScanResults.size(); i++) {
            for (File file : lastScanResults.get(i).getFiles()) {
                duplicateFiles.add(file);
                groupNumbers.add(i);
            }
        }

        System.out.println("Choose the duplicate files to move. Keep at least one file in each group.");
        for (int i = 0; i < duplicateFiles.size(); i++) {
            System.out.println((i + 1) + ". Group " + (groupNumbers.get(i) + 1)
                    + " - " + duplicateFiles.get(i).getAbsolutePath());
        }
        System.out.print("File numbers, separated by commas: ");
        List<Integer> selectedNumbers = readNumberSelection(readLine(), duplicateFiles.size());
        if (selectedNumbers.isEmpty()) {
            System.out.println("No files were selected.");
            return;
        }

        int[] filesSelectedByGroup = new int[lastScanResults.size()];
        List<File> filesToQuarantine = new ArrayList<>();
        for (int number : selectedNumbers) {
            int index = number - 1;
            filesToQuarantine.add(duplicateFiles.get(index));
            filesSelectedByGroup[groupNumbers.get(index)]++;
        }
        for (int i = 0; i < filesSelectedByGroup.length; i++) {
            if (filesSelectedByGroup[i] >= lastScanResults.get(i).getCount()) {
                System.out.println("Keep at least one file from group " + (i + 1) + ".");
                return;
            }
        }

        if (!confirm("Move " + filesToQuarantine.size() + " file(s) into quarantine? (y/n): ")) {
            System.out.println("Quarantine cancelled.");
            return;
        }

        try {
            List<Path> movedFiles = controller.quarantine(filesToQuarantine);
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
        System.out.print("File numbers to restore, separated by commas: ");
        List<Integer> selectedNumbers = readNumberSelection(readLine(), quarantinedFiles.size());
        if (selectedNumbers.isEmpty()) {
            System.out.println("No files were selected.");
            return;
        }

        List<Path> selectedFiles = new ArrayList<>(selectedNumbers.size());
        for (int number : selectedNumbers) {
            selectedFiles.add(quarantinedFiles.get(number - 1));
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

    private List<Integer> readNumberSelection(String answer, int maximum) {
        if (answer.isBlank()) {
            return List.of();
        }

        Set<Integer> selectedNumbers = new LinkedHashSet<>();
        for (String part : answer.split(",")) {
            try {
                int number = Integer.parseInt(part.trim());
                if (number < 1 || number > maximum) {
                    System.out.println("Choose numbers from 1 to " + maximum + ".");
                    return List.of();
                }
                selectedNumbers.add(number);
            } catch (NumberFormatException e) {
                System.out.println("Enter numbers separated by commas, for example: 1, 3");
                return List.of();
            }
        }

        return new ArrayList<>(selectedNumbers);
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
}
