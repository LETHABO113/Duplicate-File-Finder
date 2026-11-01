<p align="center">
  <img src="Duplicate%20File%20Finder/src/duplicatefilefinder/resources/heading.png" alt="Duplicate File Finder logo" width="96">
</p>

# Duplicate File Finder

**Software Design 2 | CPUT | 2026**

![Java 25](https://img.shields.io/badge/Java-25-orange)
![NetBeans](https://img.shields.io/badge/IDE-Apache%20NetBeans-blue)
![Build](https://img.shields.io/badge/Build-Apache%20Ant-red)
![Status](https://img.shields.io/badge/Status-Check--in%20Build-yellow)

Repository: https://github.com/LETHABO113/Duplicate-file-finder

Duplicate File Finder is a Java desktop application that scans a directory and finds
files with identical content, even when their names or locations are different. It
provides a Swing graphical interface and a command-line interface, then lets the user
review, export, quarantine, restore, or safely remove duplicate copies.

> **Safety warning:** only demonstrate quarantine or permanent deletion on disposable
> copies. Never test these actions on the only copy of important files.

## Contents

- [Check-in status](#check-in-status)
- [Quick start](#quick-start)
- [Check-in demonstration](#check-in-demonstration)
- [User guide](#user-guide)
- [How duplicate detection works](#how-duplicate-detection-works)
- [Testing](#testing)
- [Architecture and project structure](#architecture-and-project-structure)
- [Team responsibilities](#team-responsibilities)
- [Git workflow](#git-workflow)
- [Known limitations](#known-limitations)

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
| Automated Java tests | Not yet implemented |

## Quick start

### Requirements

- Windows 10 or 11 for the complete current GUI/CLI launch experience
- Apache NetBeans with Java SE and Ant support
- JDK 25, matching the project's current source and target settings
- Git, if the repository will be cloned or updated

This is a **NetBeans Java Ant project**, not a Maven project. It has no external
runtime dependencies and does not use a `pom.xml` or `mvn` commands.

### Build and run with NetBeans

1. Clone or download the repository.
2. Open Apache NetBeans.
3. Select **File > Open Project**.
4. Open the inner `Duplicate File Finder` folder containing `build.xml` and
   `nbproject`.
5. Right-click the project and choose **Clean and Build**.
6. Click **Run Project**.
7. On the welcome screen, choose **Launch GUI** or **Launch CLI**.

No NetBeans run arguments are needed for normal use. The configured entry point is
`duplicatefilefinder.WelcomeScreen`.

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

NetBeans includes Ant support, so a separate command-line Ant installation is not
required when building inside the IDE.

## Check-in demonstration

The committed binary files in `Duplicate File Finder/test/duplicatefilefinder` provide
a stable demonstration set. For scan-only demonstrations, the tracked folder can be
used directly. Before demonstrating quarantine or deletion, copy the entire folder to
a disposable location and scan the copy.

### Expected result

Scanning the nine fixture files should produce **3 duplicate groups** and **7 rows**
in the GUI results table:

| Duplicate group | Matching files |
|---|---|
| Images | `ImageTest.jpg`, `ImageTest1.jpg` |
| Documents | `TestDocument.docx`, `TestDocument1.docx` |
| Images | `TestImage - Copy.jpg`, `TestImage.jpg`, `TestImage1.jpg` |

`Test.docx` and `testmage.jpg` are unique and should not appear in the duplicate
results. The three groups represent **333,824 bytes** of estimated wasted space.

### Suggested 3-minute demo

1. Run the project and choose **Launch GUI**.
2. Click **Browse** and select a disposable copy of
   `test/duplicatefilefinder`.
3. Click **Scan** and confirm that three groups and seven duplicate rows appear.
4. Open **File > Export Results**, choose CSV or JSON, and save the report.
5. Tick one file in a duplicate group and click **Quarantine**.
6. Open **View > Quarantine**, click **Refresh**, tick the quarantined file, and
   click **Restore Selected**.
7. Use the quarantine window's **Close** button, then scan again to confirm that the
   three original groups return.

Export before moving or deleting files. After a file action, scan again before using
the results for another action or report.

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

Large scans may take time. Do not close the program while it is reading and hashing
files.

#### 2. Read the results

Only confirmed duplicate files are shown. The results table contains:

| Column | Meaning |
|---|---|
| Select | Checkbox used to choose a file for an action |
| File Name | Name of the duplicate file |
| File Path | Full location of the file |
| Size (KB) | File size in kilobytes |
| Duplicate Group | Files with the same number contain matching content |

Tick the checkbox in the **Select** column. Highlighting a row does not select the
file for quarantine or deletion.

#### 3. Export results

1. Scan a directory first.
2. Open **File > Export Results**.
3. Choose **CSV** or **JSON**.
4. Choose the output filename and location.

Export the current scan before quarantining or deleting files. Otherwise, rescan
before exporting.

#### 4. Quarantine duplicate copies

1. Tick the files to move.
2. Keep at least one file unchecked in every duplicate group.
3. Click **Quarantine** and confirm the action.

The application prevents the user from quarantining every copy in a group. Files are
moved into the configured quarantine folder and recorded in `quarantine/audit.log` so
they can be restored.

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

#### Menu reference

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
- File choices for quarantine and restore are entered as comma-separated numbers,
  for example `1, 3`.
- At least one copy in every duplicate group must remain outside quarantine.
- Scan again after quarantining or restoring files.
- Option 6 displays and saves the current settings; it does not edit them.

## How duplicate detection works

The application compares content rather than file names:

1. `FileScanner` recursively collects files from the selected directory.
2. Files smaller than the configured minimum or outside the extension filter are
   excluded.
3. `DuplicateDetector` groups the remaining files by byte size.
4. Files with a unique size are skipped because they cannot have an exact copy in the
   scanned set.
5. `HashCalculator` calculates SHA-256 hashes for the remaining candidates in
   parallel.
6. Files with matching content hashes are placed into `DuplicateGroup` objects and
   sorted by estimated wasted space.

Symlinks are skipped by default. Unreadable nested folders are logged and skipped so
the rest of the scan can continue.

## Testing

### Current approach

The `test/duplicatefilefinder` directory contains nine committed JPG and DOCX fixtures
for repeatable manual testing. It does not contain Java unit-test classes, and the
project does not currently claim an implemented JUnit suite.

The current manual process is:

1. Clean and build the project.
2. Copy the fixture folder before any file-changing test.
3. Record the test case, expected result, actual result, pass/fail status, tester, and
   date.
4. Recreate the disposable folder before repeating quarantine or deletion tests.

| Core test | Expected result |
|---|---|
| Scan committed fixture set | 3 groups and 7 duplicate rows |
| Folder containing unique files only | No duplicate groups |
| Same-size files with different content | Files are not grouped together |
| Identical content with different names | Files are grouped together |
| Duplicates in nested folders | Files are found recursively |
| CSV and JSON export | Report contains all current duplicate paths |
| Quarantine and restore | Copy is moved, audited, and restored unchanged |
| Attempt to select every copy | Action is blocked |
| Invalid or unreadable directory | Clear error or warning without data loss |

### Important classes

| Class | Responsibility |
|---|---|
| `WelcomeScreen` | Configured entry point and GUI/CLI mode chooser |
| `MainApp` | Internal argument-based application launcher |
| `Main` | Main Swing window, results, export, quarantine, and deletion actions |
| `CLIMenu` | Interactive console menu |
| `QuarantineWindow` | Displays and restores quarantined files |
| `AppController` | Coordinates scanning, detection, reports, settings, and quarantine |
| `FileScanner` | Recursive file collection and filtering |
| `DuplicateDetector` | Size grouping and duplicate detection |
| `HashCalculator`, `HashUtil` | Parallel SHA-256 hashing support |
| `DuplicateGroup<T>` | Stores matching files and calculates wasted space |
| `QuarantineManager` | Quarantine, audit logging, restoration, and deletion services |
| `ReportGenerator` | Console, CSV, and JSON output |
| `AppSettings`, `Logger`, `FileUtility` | Settings, logging, and shared helpers |

### Repository layout

```text
Duplicate-File-Finder/
|-- README.md
|-- .gitignore
`-- Duplicate File Finder/
    |-- build.xml
    |-- manifest.mf
    |-- Duplicate-File-Finder-Class-Diagram.drawio
    |-- nbproject/                       # NetBeans Ant configuration
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
    |   `-- resources/                   # Icons and welcome-screen images
    |-- test/duplicatefilefinder/        # Manual JPG and DOCX fixtures
    |-- sample-data/                     # Small text fixtures
    |-- config/                          # Serialized settings
    |-- logs/                            # Activity log
    |-- quarantine/                      # Quarantined files and audit log
    `-- DFF-Video-Demo/                  # Additional demonstration files
```

NetBeans generates `build/`, `dist/`, and `nbproject/private/` locally. These paths
are excluded by `.gitignore`.

## Team responsibilities

| Name | Student number | Primary role | Related responsibility |
|---|---:|---|---|
| **Lethabo** | 251511588 | Project Lead / Integrator | `MainApp`, `AppController` |
| **Mahlogono** | 251565998 | File Scanner Developer | `FileScanner` |
| **Ntokozo** | 250070480 | Hashing and Quarantine Developer | `HashCalculator`, `QuarantineManager` |
| **Thapelo** | 231248822 | Duplicate Detection Developer | `DuplicateDetector`, `DuplicateGroup` |
| **Nkazimulo** | 250448890 | CLI Developer | `CLIMenu`, `ReportGenerator` |
| **Kungawo** | 250078732 | GUI Developer | Swing interface and CLI support |
| **Amogelang** | 250336405 | Testing and QA Lead | `Logger`, `AppSettings`, test coordination |
| **Buhle** | 251224239 | Documentation and UML Lead | `FileUtility`, `HashUtil`, documentation |

## Git workflow

The team used `develop` as the shared working and integration branch. `main` was
reserved for stable or submission versions.

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

Runtime paths are relative to the directory from which the application is started.
For consistent behavior, run the project from the inner `Duplicate File Finder`
project folder.

- `config/settings.ser` stores serialized application settings.
- `logs/dff.log` stores activity and error messages.
- `quarantine/` stores quarantined files.
- `quarantine/audit.log` records original paths for restoration.

If saved settings cause problems on another computer, close the application and
remove the local `config/settings.ser` file. The application uses default settings
when that file is absent.

## Known limitations

- The welcome screen's **Launch CLI** button is Windows-specific because it uses
  `cmd.exe` and `java.exe`.
- Large GUI scans run synchronously and may make the window appear unresponsive until
  scanning finishes.
- The Settings screens display current values but do not edit them.
- The quarantine window requires **Refresh** before files appear.
- Use the quarantine window's **Close** button; its title-bar **X** currently exits the
  application.
- Export before a file action or rescan afterward so the report reflects the current
  filesystem.
- The default minimum file size is one byte, so zero-byte files are excluded.
- Automated Java tests have not yet been implemented.

## Academic use

This repository was created as a CPUT Software Design 2 group project for 2026.

**Last updated:** 1 November 2026
