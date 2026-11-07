<p align="center">
  <img src="Duplicate%20File%20Finder/src/duplicatefilefinder/resources/heading.png" alt="Duplicate File Finder logo" width="96">
</p>

# Duplicate File Finder

**Software Design 2 | CPUT | 2026**

[![Java](https://img.shields.io/badge/Java-17%2B-orange)]()
[![ant](https://img.shields.io/badge/Build-ant-blue)]()
[![Status](https://img.shields.io/badge/Status-In%20Development-yellow)]()

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
| **Mahlogonolo** | 251565998 | File Scanner Developer | `FileScanner` |
| **Ntokozo** | 250070480 | Hashing and Quarantine Developer | `HashCalculator`, `QuarantineManager` |
| **Thapelo** | 231248822 | Duplicate Detection Developer | `DuplicateDetector`, `DuplicateGroup` |
| **Nkazimulo** | 250448890 | CLI Developer | `CLIMenu`, `ReportGenerator` |
| **Kungawo** | 250078732 | GUI Developer | Swing interface and CLI support |
| **Amogelang** | 250336405 | Testing and QA Lead | `Logger`, `AppSettings`, test coordination |
| **Buhle** | 251224239 | Documentation and UML Lead | `FileUtility`, `HashUtil`, documentation |

## Git workflow

**Safety.** Deleting is irreversible. Quarantine moves duplicates to a safe folder and
writes an audit log. The user can always restore. This is also a **sustainability point**
(socially responsible engineering).

---

## 7. Classes

| Class | What it does | Primary Owner |
|---|---|---|
| `MainApp` | Program entry point | 1 |
| `AppController` | Wires everything together | 1 |
| `CLIMenu` | Console menu loop | 5 |
| `MainWindow` | GUI | 6 |
| `FileScanner` | Walks folders recursively | 2 |
| `HashCalculator` | Computes SHA-256 of one file | 3 |
| `DuplicateDetector` | Size + hash grouping | 4 |
| `DuplicateGroup<T>` | Holds one set of duplicates | 4 |
| `QuarantineManager` | Moves/restores files; writes audit log | 3 |
| `ReportGenerator` | Console, CSV, JSON output | 5 |
| `Logger` | Timestamped activity log | 7 |
| `AppSettings` | Saves/loads user settings | 7 |
| `FileUtils`, `HashUtils` | Shared helpers | 8 |

**Key point:** every class has a clear interface, so **no one is blocked by anyone else**.
You can build your class and test it in isolation.

---

## 8. Syllabus Concepts Covered

| Concept | Where it appears |
|---|---|
| **Recursion** | `FileScanner` walks folders recursively |
| **Collections** | `HashMap` for size and hash grouping |
| **Generics** | `DuplicateGroup<T>` |
| **File Handling** | `Files.walkFileTree`, `BufferedInputStream` |
| **File Streams** | Reading file bytes for hashing |
| **Serialization** | `AppSettings`, saved scan results |
| **Algorithms** | Size-then-hash strategy; Big O analysis |
| **Multithreading** | `ExecutorService` hashes files in parallel |
| **Regex** | Extension filter |
| **OOP** | Every class — encapsulation, abstraction |
| **Error Handling** | Try/catch per file; scan continues on errors |
| **GUI** | Swing window with progress bar |
| **Testing** | JUnit 5 tests for every class |

---

## 9. The Team — Who Does What

| # | Name | Student No. | Primary Role | Backup Role |
|---|---|---|---|---|
| 1 | Ntokozo | 250070480 | **Project Lead / Integrator** | AppController |
| 2 | Mahlogono | 251565998 | **FileScanner Developer** | HashCalculator |
| 3 | Lethabo | 251511588 | **HashCalculator Developer** | QuarantineManager |
| 4 | Thapelo | 231248822 | **DuplicateDetector Developer** | DuplicateGroup |
| 5 | Nkazimulo | 250448890 | **CLI Developer** | ReportGenerator |
| 6 | Kungawo | 250078732 | **GUI Developer** | CLIMenu |
| 7 | Amogelang | 250336405 | **Testing & QA Lead** | Logger, AppSettings |
| 8 | Buhle | 251224239 | **Documentation & UML Lead** | FileUtils, HashUtils |

### What each role actually does

**Member 1 — Project Lead / Integrator**
- Sets up the GitHub repo, `.gitignore`, ant `pom.xml`
- Writes `MainApp` and `AppController`
- Reviews pull requests, keeps the schedule, resolves blockers
- Runs the Week 6 demo

**Member 2 — FileScanner Developer**
- Writes recursive directory walker (`FileScanner`)
- Handles extension regex filter, minimum size filter
- Handles permission errors and symlink loops
- Delivers: `FileScanner.java` + `FileScannerTest.java`

**Member 3 — HashCalculator Developer**
- Writes `HashCalculator` (SHA-256)
- Adds multithreading with `ExecutorService`
- Writes `QuarantineManager` (move/restore + audit log)
- Delivers: `HashCalculator.java`, `QuarantineManager.java` + tests

**Member 4 — DuplicateDetector Developer**
- Writes `DuplicateDetector` (size pass + hash pass)
- Writes generic `DuplicateGroup<T>`
- Owns Big O analysis in the design doc
- Delivers: `DuplicateDetector.java`, `DuplicateGroup.java` + tests

**Member 5 — CLI Developer**
- Writes `CLIMenu` (interactive menu loop)
- Writes `ReportGenerator` (console, CSV, JSON)
- Owns user input validation
- Delivers: `CLIMenu.java`, `ReportGenerator.java` + tests

**Member 6 — GUI Developer**
- Writes `MainWindow` using Swing
- Progress bar, results table, checkboxes
- Integrates with `AppController`
- Delivers: `MainWindow.java`

**Member 7 — Testing & QA Lead**
- Writes JUnit tests for all core classes
- Builds `sample-data/` folder with edge cases
- Writes `Logger` and `AppSettings`
- Owns the Testing section of the report
- Delivers: test suite, sample data, edge-case list

**Member 8 — Documentation & UML Lead**
- Maintains `README.md` and `docs/`
- Draws the UML class diagram (draw.io or PlantUML)
- Writes System Design Document (5–10 pages)
- Writes GA2 + GA7 reflection
- Delivers: `docs/UML_ClassDiagram.png`, `docs/SystemDesignDocument.pdf`, `docs/GA_Reflection.md`

**Backup rule:** every class has a second person who understands it. If someone is
sick, the project still moves.

---

## 10. Tools We Use

| Tool | Purpose |
|---|---|
| **Java JDK 17+** | Language runtime |
| **Apache NetBeans** or **VS Code** | IDE |
| **ant** | Build tool (creates JAR, runs tests) |
| **GitHub** | Version control, collaboration |
| **JUnit 5** | Automated testing |
| **draw.io** or **PlantUML** | UML diagrams |

---

## 11. Git Workflow

### Branches

- `main` — protected. **Nobody commits directly.** Only merged via PR.
- `develop` — integration branch.
- `feature/<your-name>-<thing>` — your work branch.

Example: `feature/thabo-filescanner`

### The daily loop

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

---

## 12. Project Structure

```
duplicate-file-finder/
├── README.md
├── .gitignore
├── pom.xml
├── docs/
│   ├── SystemDesignDocument.pdf
│   ├── UML_ClassDiagram.png
│   ├── GA_Reflection.md
│   └── team/
├── sample-data/
└── src/
    ├── main/
    │   ├── java/za/ac/cput/dff/
    │   │   ├── MainApp.java
    │   │   ├── AppController.java
    │   │   ├── cli/CLIMenu.java
    │   │   ├── gui/MainWindow.java
    │   │   ├── core/
    │   │   │   ├── FileScanner.java
    │   │   │   ├── HashCalculator.java
    │   │   │   ├── DuplicateDetector.java
    │   │   │   ├── DuplicateGroup.java
    │   │   │   └── QuarantineManager.java
    │   │   ├── report/ReportGenerator.java
    │   │   └── util/
    │   │       ├── Logger.java
    │   │       ├── AppSettings.java
    │   │       ├── FileUtils.java
    │   │       └── HashUtils.java
    │   └── resources/config.properties
    └── test/
        └── java/za/ac/cput/dff/
            ├── FileScannerTest.java
            ├── HashCalculatorTest.java
            ├── DuplicateDetectorTest.java
            ├── QuarantineManagerTest.java
            ├── ReportGeneratorTest.java
            └── AppSettingsTest.java
```

---

## 13. How to Build and Run

### Build

```bash
mvn clean package
```

The runnable JAR is produced at `target/duplicate-file-finder-1.0.jar`.

### Run — CLI

```bash
java -jar target/duplicate-file-finder-1.0.jar --cli
```

### Run — GUI

```bash
java -jar target/duplicate-file-finder-1.0.jar --gui
```

### Configuration

Edit `src/main/resources/config.properties`:

```properties
hash.algorithm=SHA-256
thread.pool.size=8
min.file.size.bytes=1
quarantine.dir=./quarantine
log.file=./logs/dff.log
```

---

## 14. Testing

### Run all tests

```bash
mvn test
```

### Test suite

| Test class | Covers |
|---|---|
| `FileScannerTest` | Recursion, filters, symlink handling, permission errors |
| `HashCalculatorTest` | Known hashes, empty files, large files, non-ASCII names |
| `DuplicateDetectorTest` | Size filter, hash grouping, nested duplicates |
| `QuarantineManagerTest` | Move, restore, audit log integrity |
| `ReportGeneratorTest` | CSV/JSON structure, escaping |
| `AppSettingsTest` | Serialization round-trip |

### Edge cases we must all test

- Empty files (0 bytes)
- Files with same size but different content
- Symbolic links / circular references
- Permission-denied folders
- Very large files (> 1 GB)
- Non-ASCII and emoji filenames
- Nested duplicates (A = B, B = C)
- Same filename in different folders
- Zero-file folders
- Windows vs Linux path separators

---

## 15. How We're Graded

| Category | Marks | How we hit it |
|---|---|---|
| Problem Definition | 10 | Clear statement in report + README |
| System Design & Modularity | 15 | UML, one responsibility per class |
| Data Structures & Algorithms | 15 | HashMap, generics, recursion, Big O |
| Implementation Quality | 20 | OOP, comments, error handling |
| Concurrency & Advanced Features | 10 | Thread pool, serialization, regex |
| Testing & Evaluation | 10 | JUnit + edge cases + performance notes |
| Documentation & Presentation | 10 | README + design doc + demo |
| Graduate Attributes | 5 | GA2 + GA7 sections |
| Team Collaboration | 5 | Git history, PRs, contribution table |

**Total: 100%** — we cover every row.

---

## 16. Graduate Attributes

### GA2 — Application of Scientific and Engineering Knowledge

> "We take theory from class — hashing, Big O, recursion, concurrency — and apply it to
> a real engineering problem. We didn't just code; we **designed** a solution using
> known Computer Science principles and justified every choice."

Concrete examples:
- **Algorithm design** — size pre-filtering reduces hashing work by an order of magnitude.
- **Data structures** — `HashMap` gives O(1) average grouping; custom generic
  `DuplicateGroup<T>` demonstrates generics and type safety.
- **Recursion** — directory traversal is naturally recursive; we reasoned about base
  cases and loop protection (symlinks).
- **Concurrency** — `ExecutorService` parallelises I/O-bound hashing.
- **Engineering process** — requirements, UML, modular design, unit testing, and
  version control applied throughout.

### GA7 — Sustainability and Impact of Engineering Activity

> "Our tool reduces wasted storage. Fewer duplicates means less disk space, less backup
> traffic, less cloud replication, less electricity in data centres. We also chose
> quarantine over delete — because engineering decisions have **social consequences**
> and users shouldn't lose data by accident."

Concrete examples:
- **Direct environmental impact** — reducing redundant storage lowers energy consumption.
- **Efficient resource use** — size pre-filtering and parallel hashing minimise CPU time
  and energy per scan.
- **Social responsibility** — quarantine (never delete) prevents accidental data loss.
- **Sustainable software** — modular architecture, unit tests, and documentation reduce
  future maintenance cost.

Full reflection: [`docs/GA_Reflection.md`](docs/GA_Reflection.md)

---

## 17. What We Submit

- [ ] Single PDF report (problem, design, implementation, testing, GA2/GA7, team contribution)
- [ ] Zipped source code named `GroupXX_DuplicateFileFinder.zip`
- [ ] README with setup + user guide (this file)
- [ ] System Design Document (5–10 pages)
- [ ] UML class diagram
- [ ] Live demo in Week 12 (7–10 minutes)

**Missing the demo or deliverables = 50% deduction.**

---

## 18. Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| Member unavailable | Medium | High | Cross-train; every class has a backup owner |
| Threads cause bugs | Medium | Medium | Ship single-threaded first; add threads in Week 5 |
| Permission errors crash scan | High | Medium | Try/catch per file; log and continue |
| Symlinks loop forever | Medium | High | Track visited canonical paths |
| GUI takes too long | Medium | Medium | CLI must fully work before GUI polish |
| Merge conflicts | Medium | Medium | Small PRs, frequent pulls, feature branches |
| Late submission | Low | Severe | Internal deadline = 3 days early |
| Plagiarism / AI flag | Low | Severe | Original code, cite sources, rewrite in own words |

---

## 19. Communication

| | |
|---|---|
| **Repo** | https://github.com/LETHABO113/Duplicate-file-finder |
| **Group chat** | _add link_ |
| **Weekly meeting** | _add day + time + place_ |
| **Project Lead** | Ntokozo — _add contact_ |
| **Docs Lead** | Buhle — _add contact_ |

---

## 20. Glossary

| Term | Meaning |
|---|---|
| **Duplicate** | Two files with identical content |
| **Hash / SHA-256** | A short fingerprint of a file's content. Same content = same hash |
| **Recursion** | A method that calls itself. Used to walk folder trees |
| **HashMap** | A Java collection for fast key → value lookup |
| **Generics** | Java feature letting a class work with any type safely, e.g. `DuplicateGroup<T>` |
| **Multithreading** | Running several tasks at once to save time |
| **Thread pool** | A managed set of worker threads |
| **Serialization** | Saving an object to disk and loading it back |
| **Quarantine** | Safely moving a file aside instead of deleting it |
| **Big O** | Notation describing how an algorithm's time grows with input size |
| **CLI** | Command-Line Interface (text menu) |
| **GUI** | Graphical User Interface (windows, buttons) |
| **JUnit** | Java testing framework |
| **ant** | Java build tool |
| **PR** | Pull Request — a request to merge your branch |
| **Repo** | Repository — the project folder tracked by Git |

---

## Current Status

| Component | Status |
|---|---|
| Repo created | ✅ |
| README | ✅ |
| UML diagram | ⏳ |
| `FileScanner` | ⏳ |
| `HashCalculator` | ⏳ |
| `DuplicateDetector` | ⏳ |
| `CLIMenu` | ⏳ |
| `MainWindow` | ⏳ |
| Tests | ⏳ |
| System Design Document | ⏳ |
| GA Reflection | ⏳ |

Legend: ✅ done · ⏳ in progress · 🔴 blocked

---

## License

Academic project — CPUT Software Design, 2026. Not for commercial use.

---

*If anything in this README is unclear, ask Buhle (Documentation Lead). If it's wrong,
fix it and open a PR.*

**Last updated:** _add date_
