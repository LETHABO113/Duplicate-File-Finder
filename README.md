# Duplicate-File-Finder

## What is this project?

We are building a **Duplicate File Finder** - a java desktop tool thata scans a folder, finds duplicates taht have the **exact sane content** (even if they have different names), and lets the user safely **quarantine** the copies instead of deleting them.

It finds wasted space cased by duplicate files and gives the user a safe way to reclaim it.

##  The core idea

**Case 1 - same file, different name:**
```
CPUT.jpg
CPUT_FYE.jpg
CPUT_FYE_DECEE.jpg
```
IF the provided name caontain the same picture. Name comparison misses them.

**Case 2 - Different files, same name:**
```
/personl/cput.docx
/work/cput.docx
```
Provided that the files contain different content. the name comparison flags a false positive.

## How we compare content - hashing

A **hash** is a fingerprint of a file's content.

- Feed the file into SHA-256 - get back a short string like `a3f1cgy78...`
- same content - same hash.
- One byte differnt - completely different hash

This 100% reliable and much faster than reading every byte of every pair of files.

### NB: hashing is slow - so we filter by size first

- Files with **different sizes** cannot be identical. Skip them
-  Onl files with the **same size get hashed.

### The full algorithm

```
Step 1: Walk the folder recursively - collect everyfile
Step 2: Group by size - HashMap<size, list of files>
Step 3: For each size group with more than 1 file:
	compute SHA-256 of each file (in parallel, with threading)
	group by hash - HashMap<hash, list of files>
Step 4: Any hash group with more tahn 1 file = confirmed duplicate
```

## What the user sees

### CLI (console menu)

```
==== Dupiccate File FInder ====
1. Scan directory
2. View last scan result
3. Export to CSV
4. Quarantine duplicates
5. Restore from quarantine
6. Settings
7. Exit
```

After scanning:

```
Duplicate groups found: 42
Wasted space: 1.87 GB

Top 3 groups:
[d73brfs8...] 4 files, 612 MB, /pitures/downloaded
[he38nbfd...] 3 files, 84 MB, /user/pictures
[h75ifka9...] 2 files, 57 MB, /pictures/wallpapers
```

### GUI
- **Choose folder** button
- Progress bar while scanning
- Table of duplicate groups, sorted by wasted space
- Checkbox to select which files to on
- **Quarantine**, **Restore**, **Export** buttons

### Why quarantine and note delete?
**Safety. ** Deleting is irreversible. Qurantine moves duplicates to the safe folder and writes an audit log. The user can always restore.

## Classes

| Class | What it does | Primary Owner|
|---|---|---|
| `MainApp` | Program entry point | |
| `AppController` | Wires everything together | |
| `CLIMenu` | Console menu loop | |
| `MainWindow` | GUI | |
| `FileScanner` | Walks folders recursively | |
| `HashCalculator` | Computes SHA-256 of one file | |
| `DuplicateDetector` | Size + hash grouping | |
| `DuplicateGroup<T>` | Holds one set of duplicates | |
| `QuarantinManager` | Moves/restores files; writes audites log | |
| `ReportGenerator` | Console, CSV, JSON output | |
| `Logger` | Timestamped activity log | |
| `AppSettings` | Saves/loads user settings | |

## Which syllabus concepts we cover

| Concept | Where it appears |
|---|---|
| **Recursion** | `FileScaner` walks folders recursively |
| **Collections** | `HashMap` for soze and has grouping |
| **Generics** | `DuplicationGroup<T>` |
| **File Handling** | `Files.walkFileTree`, `BufferedInputStream` |
| **File Streams** | Reading file bytes for hashing |
| **Serialization** |`AppSettings`, saved can results |
| **Algorithms** | Size-then-hash strategy; Big O analysis |
| **Multithreading** | `ExecutorServices` hashes files in parallel |
| **Regex** | Extension filter |
| **OOP** | Every class - ecapsulation, abstraction |
| **Error handling** | Try/catch per file; scan continues on errors |
| **GUI** | Swing windows with progress bar |
| **Testing** | |


##  The Team - who does what

| # | Name | Student No. | Primary Role | Backup Role |
|---|---|---|---|---|
| 1 |  |  | **Project lead/intergrator** | AppCOntroller |
| 2 |  |  | **FileScannner dev** | HashCalculator |
| 3 |  |  | **HashCalculator dev** | QuarantineManager |
| 4 |  |  | **DuplicateDetector dev** | DuplicateGroup |
| 5 |  |  | **CLI dev** | ReportGenerator |
| 6 |  |  | **GUI dev** | CLI Menu |
| 7 |  |  | **Testing & QA lead** | logger, AppSetting |
| 8 |  |  | **Documantaion & lead ** | FileUtils, HashUtils |

### What each role actually does

**Member 1 — Project Lead / Integrator**
- Sets up the GitHub repo, `.gitignore`, Maven `pom.xml`
- Writes `MainApp` and `AppController`
- Reviews pull requests, keeps the schedule, resolves blockers
- Runs the Week 12 demo

**Member 2 — FileScanner Developer**
- Writes recursive directory walker (`FileScanner`)
- Handles extension regex filter, min size filter
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

## 🛠️ Tools We Use

| Tool | Purpose |
|---|---|
| **Java JDK 17+** | Language runtime |
| **Apache NetBeans** or **VS Code** | IDE |
| **Maven** | Build tool (creates JAR, runs tests) |
| **GitHub** | Version control, collaboration |
| **JUnit 5** | Automated testing |
| **draw.io** or **PlantUML** | UML diagrams |

---

## 🌳 Git Workflow (everyone must follow this)

### Branches
- `main` — protected. **Nobody commits directly.** Only merged via PR.
- `develop` — integration branch.
- `feature/<your-name>-<thing>` — your work branch.

Example: `feature/thabo-filescanner`


### Pull Request rules
1. Push your branch to GitHub.
2. Open a Pull Request into `develop`.
3. At least **one teammate reviews and approves**.
4. All tests must pass.
5. Then merge.
