package duplicatefilefinder;

import java.util.Scanner;
import java.io.File;
import java.util.List;

public class CLIMenu {
    

    private Scanner input;
    private AppController controller;

    public CLIMenu(AppController controller) {
        this.controller = controller;
        this.input = new Scanner(System.in);
    }

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
            return;
        }

        try {
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
}


