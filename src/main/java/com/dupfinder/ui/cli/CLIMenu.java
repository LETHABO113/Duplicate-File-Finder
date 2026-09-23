package com.dupfinder.ui.cli;

import com.dupfinder.core.AppController;

import java.util.Scanner;

/**
 * Owner: Member 5 (Nkazimulo) - CLI Developer
 *
 * Interactive console menu loop:
 *   1. Scan directory
 *   2. View last scan result
 *   3. Export to CSV
 *   4. Quarantine duplicates
 *   5. Restore from quarantine
 *   6. Settings
 *   7. Exit
 *
 * TODO (Member 5):
 *   - Implement the menu loop with input validation (reject non-numeric /
 *     out-of-range choices without crashing).
 *   - Wire each option to the matching AppController method.
 *   - Print the "Top 3 groups" summary after a scan as shown in the project brief.
 *   - Write input-validation tests.
 */
public class CLIMenu {

    private final AppController controller;
    private final Scanner scanner = new Scanner(System.in);

    public CLIMenu(AppController controller) {
        this.controller = controller;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> System.out.println("TODO: Scan directory (Member 5)");
                case "2" -> System.out.println("TODO: View last scan result (Member 5)");
                case "3" -> System.out.println("TODO: Export to CSV (Member 5)");
                case "4" -> System.out.println("TODO: Quarantine duplicates (Member 5)");
                case "5" -> System.out.println("TODO: Restore from quarantine (Member 5)");
                case "6" -> System.out.println("TODO: Settings (Member 5)");
                case "7" -> running = false;
                default -> System.out.println("Invalid choice, please enter 1-7.");
            }
        }
        System.out.println("Goodbye!");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("==== Duplicate File Finder ====");
        System.out.println("1. Scan directory");
        System.out.println("2. View last scan result");
        System.out.println("3. Export to CSV");
        System.out.println("4. Quarantine duplicates");
        System.out.println("5. Restore from quarantine");
        System.out.println("6. Settings");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }
}
