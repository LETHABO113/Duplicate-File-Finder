# Duplicate File Finder

**Group Project | Software Design | CPUT 2026**
**Project Topic #2 | Team of 8**

[![Java](https://img.shields.io/badge/Java-17%2B-orange)]()
[![Maven](https://img.shields.io/badge/Build-Maven-blue)]()
[![Status](https://img.shields.io/badge/Status-In%20Development-yellow)]()

**Repo:** https://github.com/LETHABO113/Duplicate-file-finder

---

## Table of Contents

1. [What Is This Project?](#1-what-is-this-project)
2. [The Core Idea](#2-the-core-idea)
3. [How We Compare Content — Hashing](#3-how-we-compare-content--hashing)
4. [The Full Algorithm](#4-the-full-algorithm)
5. [What the User Sees](#5-what-the-user-sees)
6. [Why Quarantine and Not Delete?](#6-why-quarantine-and-not-delete)
7. [Classes](#7-classes)
8. [Syllabus Concepts Covered](#8-syllabus-concepts-covered)
9. [The Team — Who Does What](#9-the-team--who-does-what)
10. [Tools We Use](#10-tools-we-use)
11. [Git Workflow](#11-git-workflow)
12. [Project Structure](#12-project-structure)
13. [How to Build and Run](#13-how-to-build-and-run)
14. [Testing](#14-testing)
15. [How We're Graded](#15-how-were-graded)
16. [Graduate Attributes](#16-graduate-attributes)
17. [What We Submit](#17-what-we-submit)
18. [Risks and Mitigations](#18-risks-and-mitigations)
19. [Communication](#19-communication)
20. [Glossary](#20-glossary)

---

## 1. What Is This Project?

We are building a **Duplicate File Finder** — a Java desktop tool that scans a folder,
finds files that have the **exact same content** (even if they have different names),
and lets the user safely **quarantine** the copies instead of deleting them.

> It finds wasted space caused by duplicate files and gives the user a safe way to
> reclaim it.

---

## 2. The Core Idea

*Understand this and you understand the whole project.*

### Case 1 — Same file, different name

```
CPUT.jpg
CPUT_FYE.jpg
CPUT_FYE_DECEE.jpg
```

If these files contain the same picture, comparing names alone would miss them.

### Case 2 — Different files, same name

```
/personal/cput.docx
/work/cput.docx
```

Same name, but if the files contain different content, comparing names alone would
flag a false positive.

**Conclusion:** we must compare **content**, not names.

---

## 3. How We Compare Content — Hashing

A **hash** is a fingerprint of a file's content.

- Feed the file into **SHA-256** → get back a short string like `a3f1cgy78...`
- Same content → same hash
- One byte different → completely different hash

This is 100% reliable and much faster than reading every byte of every pair of files.

### Note: hashing is slow — so we filter by size first

- Files with **different sizes** cannot be identical. Skip them.
- Only files with the **same size** get hashed.

In a real folder, roughly 90% of files have a unique size, so we never hash them.
**This is what makes our tool fast.**

---

## 4. The Full Algorithm

```
Step 1: Walk the folder recursively → collect every file
Step 2: Group by size → HashMap<size, list of files>
Step 3: For each size group with more than 1 file:
            compute SHA-256 of each file (in parallel, with threading)
            group by hash → HashMap<hash, list of files>
Step 4: Any hash group with more than 1 file = confirmed duplicate
```

That is the whole engine. Everything else is UI, safety, and reporting around it.

### Complexity

Let **n** = number of files, **b** = average file size, **c** = same-size candidates,
**T** = number of threads.

| Stage | Complexity |
|---|---|
| Directory walk | O(n) |
| Size grouping | O(n) average |
| Hashing | O(c · b) |
| Hash grouping | O(c) average |
| Parallel hashing | ≈ O(c · b / T) |

---

## 5. What the User Sees

### CLI (console menu)

```
==== Duplicate File Finder ====
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
Wasted space:           1.87 GB

Top 3 groups:
  [d73brfs8...]  4 files · 612 MB · /pictures/downloaded
  [he38nbfd...]  3 files ·  84 MB · /user/pictures
  [h75ifka9...]  2 files ·  57 MB · /pictures/wallpapers
```

### GUI

- **Choose Folder** button
- Progress bar while scanning
- Table of duplicate groups, sorted by wasted space
- Checkboxes to select which files to act on
- **Quarantine**, **Restore**, **Export** buttons

---

## 6. Why Quarantine and Not Delete?

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
- Sets up the GitHub repo, `.gitignore`, Maven `pom.xml`
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
| **Maven** | Build tool (creates JAR, runs tests) |
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
git checkout develop
git pull origin develop
git checkout -b feature/yourname-task
# ... do work ...
git add .
git commit -m "feat: describe what you did"
git push origin feature/yourname-task
# open a PR on GitHub → base: develop
```

### Commit message rules

Use one of these prefixes:

| Prefix | When to use |
|---|---|
| `feat:` | Adding a new feature |
| `fix:` | Fixing a bug |
| `docs:` | Documentation only |
| `test:` | Adding or fixing tests |
| `refactor:` | Reorganising code without changing behaviour |
| `chore:` | Build files, `.gitignore`, config |

Example: `feat: add recursive directory walker to FileScanner`

### Pull Request rules

1. Push your branch to GitHub.
2. Open a Pull Request into `develop`.
3. At least **one teammate reviews and approves**.
4. All tests must pass (`mvn test`).
5. Then merge.

### Team rules

1. **Pull `develop` every morning** before you start coding.
2. **Push at least once a day** — even if work is incomplete.
3. **One branch = one task.** Don't mix unrelated work.
4. **Never commit to `main` or `develop` directly.** Always PR.
5. **Keep PRs small.** Under 400 lines where possible.
6. **Review PRs within 24 hours.** Unblock your teammate.
7. **Never force-push.** Ask the Project Lead if you think you need to.
8. **Add a JUnit test** for every new class before PR.
9. **No secrets in the repo.** No passwords, no API keys.
10. **Ask for help early.** A 5-minute question saves a 5-hour debug.

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
| **Maven** | Java build tool |
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
