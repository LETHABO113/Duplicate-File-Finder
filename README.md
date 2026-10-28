# Duplicate File Finder

**Software Design group project | CPUT | 2026**

![Java 25](https://img.shields.io/badge/Java-25-orange)
![NetBeans](https://img.shields.io/badge/IDE-Apache%20NetBeans-blue)
![Build](https://img.shields.io/badge/Build-Apache%20Ant-red)

Repository: https://github.com/LETHABO113/Duplicate-file-finder

## Overview

Duplicate File Finder is a Java application that scans a selected directory and
finds files with identical content, even when their names or locations are different.
The application is available as both a command-line interface (CLI) and a Swing
graphical user interface (GUI).

Detected copies can be reviewed, exported to CSV or JSON, moved into quarantine,
and restored. The GUI can also permanently delete selected duplicate copies after
confirmation.

> **Safety warning:** only test quarantine and deletion on disposable copies of files.
> Do not test these actions on the only copy of personal or important data.

## Features

- Recursively scans folders and subfolders.
- The scanning engine supports minimum-size and file-extension filters.
- Groups files by size before hashing to avoid unnecessary work.
- Calculates SHA-256 hashes in parallel using a thread pool.
- Displays duplicate groups and estimated wasted space.
- Provides both CLI and Swing GUI modes.
- Exports results to CSV or JSON.
- Quarantines files instead of immediately deleting them.
- Restores quarantined files to their original paths.
- Records application activity and quarantine actions in log files.
- Includes support for serializing application settings between runs.

## How duplicate detection works

File names are not reliable evidence that two files are duplicates. Two files can
have different names but contain the same data, or have the same name but contain
different data. The application therefore compares content:

1. `FileScanner` recursively collects files that match the current filters.
2. `DuplicateDetector` groups the files by byte size.
3. Files with a unique size are skipped because they cannot have an exact copy in
   the scanned set.
4. `HashCalculator` calculates SHA-256 hashes for files in the remaining groups.
5. Files with the same size and SHA-256 hash are placed in a `DuplicateGroup`.
6. Duplicate groups are sorted by their estimated wasted space.

SHA-256 is collision-resistant and is suitable for identifying duplicate content in
this project. The application does not decide duplicates from file names alone.

## Requirements

- Apache NetBeans with Java SE/Ant support
- JDK 25, matching the current `javac.source` and `javac.target` project settings
- Git, if the repository will be updated

The project has no external runtime dependencies. It is a **NetBeans Java Ant
project**, not a Maven project, so there is no `pom.xml` and no `mvn` command is
needed.

If the team later changes Java versions, everyone should update their JDK and the
NetBeans project source level together.

## Open, build, and run

### NetBeans (recommended)

1. Clone or download this repository.
2. In NetBeans, select **File > Open Project**.
3. Open the inner `Duplicate File Finder` folder, which contains `build.xml` and
   `nbproject`.
4. Right-click the project and choose **Clean and Build**.
5. Run the required application mode as described below.

The build creates:

```text
Duplicate File Finder/dist/Duplicate_File_Finder.jar
```

### Application modes

`duplicatefilefinder.MainApp` is the configured entry point.

- **CLI:** leave the project arguments empty, or use `--cli`.
- **GUI:** open **Project Properties > Run**, set **Arguments** to `--gui`, and run
  the project.

The CLI is the default when no argument is supplied.

### Terminal (optional)

If Apache Ant is installed and available on `PATH`:

```powershell
cd "Duplicate File Finder"
ant clean jar
java -jar .\dist\Duplicate_File_Finder.jar --cli
java -jar .\dist\Duplicate_File_Finder.jar --gui
```

NetBeans can run the Ant build internally, so a separate command-line Ant
installation is not required when building from the IDE.

## Basic use

### CLI

The CLI provides the following menu:

```text
1. Scan directory
2. View last scan result
3. Export results
4. Quarantine duplicates
5. Restore from quarantine
6. Settings
7. Exit
```

Start by scanning a directory. Review the reported groups before exporting or
moving any file. When quarantining files, keep at least one copy from every duplicate
group.

### GUI

1. Browse to the directory to scan.
2. Start the scan and review the results table.
3. Select only the duplicate copies on which an action should be performed.
4. Export the results, quarantine selected copies, or open the quarantine window to
   restore files.
5. Confirm paths and selections carefully before any permanent deletion.

## Project structure

The structure below matches the NetBeans Ant project in this repository:

```text
Duplicate-File-Finder/
|-- .gitignore
|-- README.md
`-- Duplicate File Finder/
    |-- build.xml
    |-- manifest.mf
    |-- nbproject/
    |   |-- build-impl.xml
    |   |-- genfiles.properties
    |   |-- project.properties
    |   `-- project.xml
    |-- src/
    |   `-- duplicatefilefinder/
    |       |-- AppController.java
    |       |-- AppSettings.java
    |       |-- CLIMenu.java
    |       |-- DuplicateDetector.java
    |       |-- DuplicateGroup.java
    |       |-- FileScanner.java
    |       |-- FileUtility.java
    |       |-- HashCalculator.java
    |       |-- HashUtil.java
    |       |-- Logger.java
    |       |-- how-to-use-icon.png
    |       |-- Main.java
    |       |-- Main.form
    |       |-- MainApp.java
    |       |-- QuarantineManager.java
    |       |-- QuarantineWindow.java
    |       |-- QuarantineWindow.form
    |       |-- ReportGenerator.java
    |       `-- resources/
    |-- test/                            # configured test source root; no committed tests
    |-- sample-data/
    |-- DFF-Video-Demo/
    |-- logs/
    `-- quarantine/
```

NetBeans generates `build/`, `dist/`, and `nbproject/private/` locally. These paths
are excluded by `.gitignore`. The Ant project configures `test/` as its test source
directory, but no automated test files are currently committed.

## Main classes

| Class | Responsibility |
|---|---|
| `MainApp` | Application entry point and CLI/GUI mode selection |
| `Main` | Main Swing window and user actions |
| `QuarantineWindow` | Refreshes the quarantine list and restores selected files |
| `AppController` | Connects scanning, detection, reporting, settings, and quarantine services |
| `CLIMenu` | Command-line menu and input handling |
| `FileScanner` | Recursive directory traversal and file filtering |
| `DuplicateDetector` | Size grouping, hash grouping, and duplicate detection |
| `DuplicateGroup<T>` | Stores a group of matching files and calculates wasted space |
| `HashCalculator` | Parallel file hashing |
| `HashUtil` | SHA-256 and hexadecimal conversion helpers |
| `QuarantineManager` | Quarantine, restore, permanent deletion, and audit logging |
| `ReportGenerator` | Console, CSV, and JSON result output |
| `AppSettings` | Serializable application settings |
| `Logger` | Timestamped application log |
| `FileUtility` | Shared file and size helpers |

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

## Git workflow used by the team

The team worked mainly on the shared `develop` branch. The `main` branch was reserved
for stable or submission versions, while active integration happened on `develop`.

A typical update was:

```bash
git switch develop
git pull origin develop

# Make and check the changes
git status
git add <changed-files>
git commit -m "clear summary of the change"
git push origin develop
```

The team **did not use a formal commit-message prefix convention**. Prefixes such as
`feat:`, `fix:`, or `docs:` are therefore not project requirements. A short, clear
message describing the actual change is enough.

Because several people use the same branch, each member should pull before starting,
check `git status` before committing, and coordinate before editing the same file.
Feature branches can still be used for risky or experimental work, but they are not
presented here as the workflow followed by every member.

## Testing approach

### Current state

The Ant configuration declares `test/` as the test source directory, but the
repository contains no committed automated test files. Although a JUnit 5 library
reference exists in the NetBeans project properties, the repository does not contain
a JUnit 5 test suite. This README therefore does not claim that the project uses
JUnit 5.

For the current version, the most practical approach is **repeatable manual functional
testing** with disposable test folders and a written record of expected and actual
results.

### Recommended test process

1. Copy `sample-data` to a temporary test location. Do not quarantine files directly
   from the tracked repository folder.
2. In NetBeans, run **Clean and Build** before the test session.
3. Run the user-facing scan, export, and quarantine cases in both CLI and GUI modes.
4. For every case, record the input, expected result, actual result, pass/fail status,
   tester, and date.
5. After quarantine or deletion tests, recreate the disposable test folder before the
   next run so every test starts from the same state.
6. Re-test scanning and export after any change to the detection code.

### Baseline sample-data test

With the default settings, scanning the included `sample-data` folder should report:

- **2 duplicate groups**
- `contentA.txt` and `contentB.txt` as one group
- `empty1.txt` and `empty2.txt` as one group
- `contentC.txt` as a unique file
- **15 bytes** of total estimated wasted space on the current Windows checkout

The two groups and their membership are the stable expectations. The exact byte total
can be 13 bytes on an LF checkout or 15 bytes when Git converts the sample files to
Windows CRLF line endings. The current Windows result was verified against the source
on 28 October 2026.

### Manual test matrix

| Test case | Expected result |
|---|---|
| Included `sample-data` | Two groups with the file memberships listed above; 15 bytes on the current Windows checkout |
| Same-size files with different content | Files are not reported as duplicates |
| Unique files only | Zero duplicate groups |
| Empty directory | Scan completes with zero groups |
| Duplicate files in nested folders | Duplicates are found recursively |
| Same name but different content | Files are not grouped together |
| Different names but identical content | Files are grouped together |
| Spaces and non-ASCII characters in paths | Files are scanned and displayed correctly |
| CSV and JSON export | Output contains every displayed duplicate path |
| Quarantine one copy | Selected copy is moved and at least one original copy remains |
| Restore from quarantine | File returns to its original path with unchanged content |
| Permanent deletion (GUI only) | Only the selected disposable file is removed after confirmation |
| Large disposable files | Scan completes and elapsed time is recorded |
| Invalid or unreadable location | A clear error is shown without corrupting other files |

A simple test record can use this format:

| ID | Test case | Expected | Actual | Pass/Fail | Tester | Date |
|---|---|---|---|---|---|---|
| T01 | Scan copied sample data | 2 groups with the expected membership |  |  |  |  |

### If automated tests are required later

Confirm the required framework with the lecturer before adding a dependency. If no
testing framework is required, a small Java test runner in `test/duplicatefilefinder`
can call the core classes and throw `AssertionError` when a check fails. If JUnit is
required, use the version supported or specified for the course rather than assuming
JUnit 5.

Good first automation targets are `HashUtil`, `FileScanner`, `DuplicateDetector`,
`ReportGenerator`, `QuarantineManager`, and `AppSettings`. A small test harness can
construct `FileScanner` with different minimum-size and extension filters, and can
save then reload `AppSettings` to compare the values. Tests that move or delete files
must use a new temporary directory for every run.

The current CLI and GUI Settings screens display the active settings but do not let
the user edit the minimum size or extension filter. Those two behaviors should
therefore be tested through code until editing controls are implemented.

## Runtime files

Paths are resolved relative to the directory from which the application is started:

- `config/settings.ser` stores serialized application settings.
- `logs/dff.log` stores general activity messages.
- `quarantine/` stores quarantined files by default.
- `quarantine/audit.log` records original and quarantined paths for restoration.

These files can change while the application is running.

## Concepts demonstrated

| Concept | Project example |
|---|---|
| Object-oriented design | Separate scanner, detector, controller, reporting, and UI classes |
| Recursion | Traversing nested directories |
| Collections | Grouping files by size and hash with maps and lists |
| Generics | `DuplicateGroup<T>` |
| File handling | Scanning, hashing, exporting, moving, restoring, and deleting files |
| Serialization | Saving and loading `AppSettings` |
| Multithreading | `ExecutorService` for parallel hashing |
| Regular expressions | Extension filtering |
| Error handling | Validation, logging, and per-directory scan warnings |
| GUI development | Swing forms created with NetBeans |

## Academic use

This repository was created as a CPUT Software Design 2 group project for 2026.

**Last updated:** 28 October 2026
