# Duplicate File Finder

<p align="center">
  <img src="Duplicate%20File%20Finder/src/duplicatefilefinder/resources/heading.png" alt="Duplicate File Finder logo" width="96">
</p>

**Software Design 2 | CPUT | 2026**

![Java 25](https://img.shields.io/badge/Java-25-orange)
![NetBeans](https://img.shields.io/badge/IDE-Apache%20NetBeans-blue)
![Build](https://img.shields.io/badge/Build-Apache%20Ant-red)
![Testing](https://img.shields.io/badge/Testing-JUnit%204-green)
![Status](https://img.shields.io/badge/Status-Check--in%20Build-yellow)

**Repository:** https://github.com/LETHABO113/Duplicate-file-finder

Duplicate File Finder is a Java desktop application that scans a directory and finds files with identical content, even when their names or locations are different. It provides a Swing graphical interface and a command-line interface, then lets the user review, export, quarantine, restore, or safely remove duplicate copies.

> **Safety warning:** Only demonstrate quarantine or permanent deletion on disposable copies. Never test these actions on the only copy of important files.

## Contents

- [Check-in status](#check-in-status)
- [Quick start](#quick-start)
- [User guide](#user-guide)
- [How duplicate detection works](#how-duplicate-detection-works)
- [Testing](#testing)
- [Architecture and project structure](#architecture-and-project-structure)
- [Team responsibilities](#team-responsibilities)
- [Git workflow](#git-workflow)
- [Runtime files and settings](#runtime-files-and-settings)
- [Known limitations](#known-limitations)
- [Academic use](#academic-use)

## Check-in status

| Component | Current status |
|---|---|
| Welcome screen and mode selection | Implemented |
| Recursive directory scanning | Implemented |
| Size-first and SHA-256 duplicate detection | Implemented |
| Parallel hash calculation | Implemented |
| Swing GUI | Implemented |
| Interactive CLI | Implemented |
| CSV and JSON export | Implemented |
| Quarantine and restore | Implemented |
| Permanent deletion | Implemented in the GUI |
| Activity and quarantine logging | Implemented |
| UML class diagram | Added as a draw.io file |
| Manual test data | Added and verified |
| JUnit 4 automated test suite | Implemented and passed |
| Manual GUI testing | Completed |
| Data safety validation | Tested |

## Quick start

### Requirements

- Windows 10 or 11 for the complete current GUI/CLI launch experience.
- Apache NetBeans with Java SE and Ant support.
- JDK 25, matching the project's current source and target settings.
- Git, if the repository will be cloned or updated.

### Build and run with NetBeans

1. Clone or download the repository.
2. Open Apache NetBeans.
3. Select **File > Open Project**.
4. Open the inner `Duplicate File Finder` folder containing `build.xml` and `nbproject`.
5. Right-click the project and choose **Clean and Build**.
6. Click **Run Project**.
7. On the welcome screen, choose **Launch GUI** or **Launch CLI**.

No NetBeans run arguments are needed for normal use. The configured entry point is `duplicatefilefinder.WelcomeScreen`.

The built JAR is created at:

```text
Duplicate File Finder/dist/Duplicate_File_Finder.jar
```

### Run from a terminal

If Apache Ant is installed and available on `PATH`:

```powershell
cd "Duplicate File Finder"
ant clean jar
java -jar .\dist\Duplicate_File_Finder.jar
```

The JAR opens the welcome screen. To start the CLI directly after building:

```powershell
java -cp ".\dist\Duplicate_File_Finder.jar" duplicatefilefinder.MainApp --cli
```

NetBeans includes Ant support, so a separate command-line Ant installation is not required when building inside the IDE.

## User guide

### Welcome screen

Running the project first opens a welcome screen with two choices:

- **Launch GUI** opens the graphical application in a maximized window.
- **Launch CLI** opens the numbered menu in a separate Windows Command Prompt.

### GUI guide

#### 1. Select and scan a folder

1. Type a directory path or click **Browse**.
2. Select the folder to scan.
3. Click **Scan**, or select **File > Start Scan**.
4. Wait for the status line to report how many duplicate groups were found.

Large scans may take time. Do not close the program while it is reading and hashing files.

#### 2. Read the results

Only confirmed duplicate files are shown. The results table contains:

| Column | Meaning |
|---|---|
| Select | Checkbox used to choose a file for an action |
| File Name | Name of the duplicate file |
| File Path | Full location of the file |
| Size (KB) | File size in kilobytes |
| Duplicate Group | Files with the same number contain matching content |

Tick the checkbox in the **Select** column. Highlighting a row does not select the file for quarantine or deletion.

#### 3. Export results

1. Scan a directory first.
2. Open **File > Export Results**.
3. Choose **CSV** or **JSON**.
4. Choose the output filename and location.

Export the current scan before quarantining or deleting files. Otherwise, rescan before exporting.

#### 4. Quarantine duplicate copies

1. Tick the files to move.
2. Keep at least one file unchecked in every duplicate group.
3. Click **Quarantine** and confirm the action.

The application prevents the user from quarantining every copy in a group. Files are moved into the configured quarantine folder and recorded in `quarantine/audit.log` so they can be restored.

#### 5. Restore quarantined files

1. Open **View > Quarantine**.
2. Click **Refresh** to load the current quarantined files.
3. Tick the required files.
4. Click **Restore Selected** and confirm.
5. Use the window's **Close** button to return to the main window.

Restoration cannot overwrite another file already present at the original path.

#### 6. Permanently delete duplicate copies

1. Tick the duplicate files to remove.
2. Keep at least one file unchecked in every group.
3. Click **Delete Selected**.
4. Read the warning and confirm only if the files are disposable.

Permanent deletion cannot be undone. The CLI does not provide this option.

### Menu reference

| Menu | Available actions |
|---|---|
| File | Select Directory, Start Scan, Export Results, Exit |
| Edit | Display current Settings |
| View | Dashboard, Results, Quarantine |
| Help | How to Use, About |

### CLI guide

The CLI is an interactive numbered menu:

```text
1. Scan directory
2. View last scan result
3. Export results
4. Quarantine duplicates
5. Restore from quarantine
6. Settings
7. Exit
```

- Enter the directory path when prompted.
- Results show group count, wasted bytes, SHA-256 hashes, and full paths.
- CSV and JSON exports require a complete output path.
- File choices for quarantine and restore are entered as comma-separated numbers, for example `1, 3`.
- At least one copy in every duplicate group must remain outside quarantine.
- Scan again after quarantining or restoring files.
- Option 6 displays and saves the current settings; it does not edit them.

## How duplicate detection works

The application compares content rather than file names:

1. `FileScanner` recursively collects files from the selected directory.
2. Files smaller than the configured minimum or outside the extension filter are excluded.
3. `DuplicateDetector` groups the remaining files by byte size.
4. Files with a unique size are skipped because they cannot have an exact copy in the scanned set.
5. `HashCalculator` calculates SHA-256 hashes for the remaining candidates in parallel.
6. Files with matching content hashes are placed into `DuplicateGroup` objects and sorted by estimated wasted space.

Symlinks are skipped by default. Unreadable nested folders are logged and skipped so the rest of the scan can continue.

## Testing

The Duplicate File Finder underwent two phases of testing to verify application functionality and data safety:

1. **Automated testing:** JUnit 4 tests validate the core application components, including directory scanning, duplicate detection, hashing, settings, reporting, quarantine, and application control.
2. **Manual testing:** GUI tests using dummy files verify the application's real-world behaviour, including scanning, exporting, quarantine, restoration, permanent deletion, and the safety rule that prevents all copies in a duplicate group from being removed.

Combining automated logic tests with manual interface and file-operation tests provides coverage of both internal functionality and user-facing behaviour.

### 1. Automated testing (JUnit 4)

The project includes a JUnit 4 automated test suite.

The automated tests cover the following classes:

| Test class | What it tests | Verified behaviour |
|---|---|---|
| `FileScannerTest` | Directory scanning | Handles empty folders, deeply nested directories, and invalid or unreadable paths. |
| `DuplicateDetectorTest` | Duplicate detection and SHA-256 comparison | Groups identical files, ignores unique files, and handles zero-byte files. |
| `AppSettingsTest` | Application configuration | Rejects invalid settings such as negative file sizes and zero thread counts. |
| `ReportGeneratorTest` | Report generation | Tests CSV and JSON output, including handling null inputs safely. |
| `QuarantineManagerTest` | File movement and restoration | Verifies file movement, audit logging, restoration to original paths, and rejection of missing or null file lists. |
| `AppControllerTest` | Integration testing | Checks coordination between components and handling of invalid directory paths and export formats. |

#### Important automated test scenarios

- Recursive directory scanning.
- Empty and deeply nested directories.
- Invalid and unreadable directory paths.
- Identical files with different names.
- Unique files that must not be identified as duplicates.
- Zero-byte file handling.
- Invalid application settings.
- CSV and JSON report generation.
- Null input handling.
- Quarantine and restoration of files.
- Audit logging of file movements.
- Invalid export formats and directory paths.

#### Running automated tests in NetBeans

To execute the complete automated test suite:

1. Open the project in Apache NetBeans.
2. Right-click the project in the Projects window.
3. Select **Test**.
4. Wait for the automated test execution to finish.
5. Review the test results window.

To run an individual test class:

1. Locate the required test class.
2. Right-click the test file.
3. Select **Test File**.
4. Review the results for that particular class.

The automated test suite compiled successfully and passed at 100% during the documented testing process.

**Note on NetBeans IDE parsing:** During development, NetBeans intermittently displayed visual parsing errors on `AppControllerTest.java` and `AppSettingsTest.java`. However, the Ant build system successfully compiled the test classes, and the automated test suite passed. The issue was identified as a local IDE parsing/cache problem rather than a compilation or application failure.

### 2. Manual testing

Manual testing was performed using dummy files in the `test/duplicatefilefinder` directory.

The objective was to verify the application's behaviour through its graphical interface and confirm that file operations work correctly.

#### Manual test cases and results

| Test case | Expected result |
|---|---|
| Scan the test directory | Duplicate files are identified correctly. |
| Display scan results | Duplicate groups and matching file paths appear in the GUI. |
| Export CSV results | A CSV report is generated successfully. |
| Export JSON results | A JSON report is generated successfully. |
| Quarantine a duplicate file | The selected file is moved into quarantine. |
| Refresh quarantine window | Quarantined files appear in the window. |
| Restore a quarantined file | The file returns to its original directory. |
| Run a new scan after restoration | The restored file is detected again as a duplicate. |
| Attempt to quarantine every copy in a group | The application blocks the operation. |
| Permanently delete a selected duplicate | The selected file is permanently removed after confirmation. |

#### Data safety validation

A particularly important manual test verified the application's **Keep One Copy** rule.

The application does not allow the user to quarantine every file in a duplicate group. At least one copy must remain in the original directory.

The test confirmed that the application blocks an operation that would otherwise remove every copy of a duplicate file.

Quarantine and restoration were also tested to verify that:

- Selected files are moved successfully.
- Quarantine movements are recorded.
- Files can be restored to their original locations.
- A new scan recognises the restored duplicate files.

Permanent deletion was tested separately without using quarantine.

**Testing evidence:** The [Testing Results PDF](Testing%20results/Group_26_Testing_Results.pdf) contains supporting evidence of testing and application behaviour.

### 3. Testing conclusion

The automated and manual testing phases verified the core functionality and file safety mechanisms of the Duplicate File Finder.

JUnit 4 tests validated the internal logic and handling of invalid inputs, while manual GUI testing confirmed that the user-facing features operate as intended.

Particular attention was given to the Keep One Copy rule, quarantine restoration, and permanent deletion to reduce the risk of accidental data loss.

## Architecture and project structure

### Important classes

| Class | Responsibility |
|---|---|
| `WelcomeScreen` | Configured entry point and GUI/CLI mode chooser |
| `MainApp` | Internal argument-based application launcher |
| `Main` | Main Swing window, results, export, quarantine, and deletion actions (`Main.java`) |
| `CLIMenu` | Interactive console menu |
| `QuarantineWindow` | Displays and restores quarantined files |
| `AppController` | Coordinates scanning, detection, reports, settings, and quarantine |
| `FileScanner` | Recursive file collection and filtering |
| `DuplicateDetector` | Size grouping and duplicate detection |
| `HashCalculator`, `HashUtil` | Parallel SHA-256 hashing support |
| `DuplicateGroup<T>` | Stores matching files and calculates wasted space |
| `QuarantineManager` | Quarantine, audit logging, restoration, and deletion services |
| `ReportGenerator` | Console, CSV, and JSON output |
| `AppSettings`, `Logger`, `HashUtil` | Settings, logging, and shared helpers |

### Repository layout

```text
Duplicate-File-Finder/
|-- README.md
|-- .gitignore
|-- UML/
|   |-- Duplicate-File-Finder-Class-Diagram.drawio
|   |-- UML.png
|   `-- UML_FLOW.png
|-- Testing results/
|   `-- Group_26_Testing_Results.pdf
|-- Video and slides/
|   |-- Group_26_Duplicate_File_Finder_Slides.pptx
|   `-- Group_26_Presentation_Video.mp4
|-- logs/                         # Repository-level runtime log
|-- quarantine/                   # Repository-level quarantine audit log
`-- Duplicate File Finder/
    |-- build.xml
    |-- manifest.mf
    |-- nbproject/                  # NetBeans Ant configuration
    |-- src/duplicatefilefinder/
    |   |-- WelcomeScreen.java/.form
    |   |-- Main.java/.form
    |   |-- MainApp.java
    |   |-- CLIMenu.java
    |   |-- QuarantineWindow.java/.form
    |   |-- AppController.java
    |   |-- FileScanner.java
    |   |-- DuplicateDetector.java
    |   |-- HashCalculator.java
    |   |-- QuarantineManager.java
    |   |-- ReportGenerator.java
    |   `-- resources/               # Icons and welcome-screen images
    |-- test/duplicatefilefinder/   # JUnit tests and test files
    |-- sample-data/                # Small text fixtures
    |-- config/                     # Serialized settings
    |-- logs/                       # Project runtime activity log
    |-- quarantine/                 # Project quarantine and audit log
    `-- DFF-Video-Demo/              # Demonstration files
```

NetBeans generates `build/`, `dist/`, and `nbproject/private/` locally. These paths are excluded by `.gitignore`.

## Team responsibilities

| Name | Student number | Primary role | Related responsibility |
|---|---:|---|---|
| **Lethabo Ramatlhape** | 251511588 | Project Lead / Integrator | `MainApp`, `AppController` |
| **Mahlogonolo Mkhawane** | 251565998 | File Scanner Developer | `FileScanner` |
| **Ntokozo Tyanase** | 250070480 | Hashing and Quarantine Developer | `HashCalculator`, `QuarantineManager` |
| **Thapelo Vundla** | 231248822 | Duplicate Detection Developer | `DuplicateDetector`, `DuplicateGroup` |
| **Nkazimulo Geraldine Patuence Dhlamini** | 250448890 | CLI Developer | `CLIMenu`, `ReportGenerator` |
| **Kungawo Sonke Mpengesi** | 250078732 | GUI Developer | Swing interface and CLI support |
| **Amogelang Tshutse** | 250336405 | Testing and QA Lead | `Logger`, `AppSettings`, test coordination |
| **Nobuhle Angel Mamba** | 251224239 | Documentation and UML Lead | Documentation and UML |

## Git workflow

The team used `develop` as the shared working and integration branch. `main` was reserved for stable or submission versions.

```bash
git switch develop
git pull origin develop

# Make and check the changes
git status
git add <changed-files>
git commit -m "clear summary of the change"
git push origin develop
```

## Runtime files and settings

Runtime paths are relative to the directory from which the application is started. For consistent behaviour, run the project from the inner `Duplicate File Finder` project folder.

- `config/settings.ser` stores serialized application settings.
- `logs/dff.log` stores activity and error messages.
- `quarantine/` stores quarantined files.
- `quarantine/audit.log` records original paths for restoration.

If saved settings cause problems on another computer, close the application and remove the local `config/settings.ser` file. The application uses default settings when that file is absent.

## Known limitations

- The welcome screen's **Launch CLI** button is Windows-specific because it uses `cmd.exe` and `java.exe`.
- Large GUI scans run synchronously and may make the window appear unresponsive until scanning finishes.
- The Settings screens display current values but do not edit them.
- The quarantine window requires **Refresh** before files appear.
- Use the quarantine window's **Close** button; its title-bar **X** currently exits the application.
- Export before a file action or rescan afterward so the report reflects the current filesystem.
- The default minimum file size is one byte, so zero-byte files are excluded by default during normal scanning.
- Permanent deletion is available only through the GUI.
- The CLI does not provide permanent deletion.

## Academic use

This repository was created as a CPUT Software Design 2 group project for 2026.

**Last updated:** 7 November 2026
