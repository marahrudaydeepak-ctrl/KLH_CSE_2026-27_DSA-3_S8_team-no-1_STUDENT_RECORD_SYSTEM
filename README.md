# Intelligent Student Record Management and Advanced Algorithm Search System

A Java-based Data Structures and Algorithms (DSA-3) project that combines student-record management with implementations and demonstrations of searching, sorting, string matching, data structures, graph algorithms, dynamic programming, and other algorithmic techniques.

## Project Overview

Traditional student-record applications mainly focus on adding, updating, viewing, and searching records. This project also uses student-record tasks as practical examples for studying and comparing DSA techniques. The application includes a console menu and a Java Swing desktop interface.

The system models student information and related academic details, supports file-based persistence, and provides algorithm execution/benchmarking functionality so users can examine results and performance metrics.

## Main Features

- Add, view, update, and search student records.
- Store student data in CSV files and support binary backup/restore.
- Search by common student fields using linear and binary search approaches.
- Search names and text using string-matching algorithms.
- Use a Trie for prefix-oriented lookup/autocomplete.
- Demonstrate custom data structures rather than relying exclusively on built-in implementations.
- Run sorting algorithms and compare their behaviour.
- Explore graph traversal and shortest-path algorithms.
- Demonstrate dynamic programming, network-flow, approximation, randomized, parameterized, and parallel algorithms.
- Display algorithm results, execution time, and related metrics where supported.
- Use the console interface or the Java Swing graphical interface.
- Run automated tests using the provided test suite and batch launcher, when included in the downloaded project.

## Technologies

- **Language:** Java (the project documentation targets Java 17 or newer)
- **GUI:** Java Swing
- **Persistence:** CSV files and Java object serialization
- **Build/run:** JDK command-line tools and Windows batch scripts
- **Dependencies:** The project documentation describes a standard-Java implementation without third-party runtime libraries.

## Repository Structure

```text
.
├── src/
│   ├── Main.java
│   ├── model/                 # Student and academic domain models
│   ├── datastructures/        # Custom lists, stacks, queues, trees, heaps, hash table, Trie, graph
│   ├── algorithms/
│   │   ├── searching/
│   │   ├── sorting/
│   │   ├── string/
│   │   ├── dynamicprogramming/
│   │   ├── graph/
│   │   ├── networkflow/
│   │   ├── npcomplete/
│   │   ├── approximation/
│   │   ├── parameterized/
│   │   ├── randomized/
│   │   └── parallel/
│   ├── services/              # Student operations, algorithm selection, benchmarking, similarity
│   ├── file/                  # CSV, backup, and report handling
│   ├── ui/                    # Console menus and Swing interface
│   └── test/                  # Comprehensive test suite
├── data/                      # Sample/persisted data, if supplied
├── dashboard.html             # Browser-based dashboard, if supplied
├── build.bat                  # Windows build script, if supplied
├── run_console.bat            # Console launcher, if supplied
├── run_gui.bat                # GUI launcher, if supplied
└── run_tests.bat              # Test launcher, if supplied
```

> The exact files present may vary by project archive/version. Keep the source tree and any supplied data/configuration files together when running the application.

## Data Structures Covered

| Data structure | Typical role in the project |
|---|---|
| Custom linked lists | Managing sequences of records/items |
| Stack | Last-in, first-out operations and history-style workflows |
| Queue | First-in, first-out processing |
| Hash table | Fast key-based record lookup |
| Binary search tree | Ordered storage and traversal |
| AVL tree | Self-balancing ordered tree |
| Min-heap / max-heap | Priority-based operations and heap sorting |
| Trie | Prefix search and autocomplete |
| Graph | Representing relationships and running graph algorithms |

## Algorithms Covered

### Searching and sorting
- Linear search and binary search
- Bubble, selection, insertion, merge, quick, and heap sort
- Parallel search and parallel sorting variants

### String matching and similarity
- Naive string matching
- Knuth–Morris–Pratt (KMP)
- Rabin–Karp
- Z algorithm
- Boyer–Moore
- Aho–Corasick
- Levenshtein distance and Damerau–Levenshtein distance

KMP and Rabin–Karp are string-pattern matching algorithms included in the algorithm collection. KMP uses prefix/LPS information to avoid repeated comparisons; Rabin–Karp uses rolling hashes to identify candidate matches. Edit-distance algorithms support measuring differences between strings and can be used for approximate matching.

### Other algorithm categories
- Dynamic programming: Knapsack, Subset Sum, Longest Common Subsequence, Matrix Chain Multiplication, Optimal BST, Weighted Interval Scheduling, and bitmask TSP
- Graph algorithms: BFS, DFS, and Dijkstra's shortest path
- Network flow: Ford–Fulkerson, Edmonds–Karp, Dinic, minimum cut, and bipartite matching
- Approximation and parameterized algorithms
- Randomized algorithms, including randomized quicksort and Miller–Rabin primality testing
- Parallel algorithms and analytics

## Requirements

- Java Development Kit (JDK) 17 or newer is recommended.
- Windows is required for the supplied `.bat` launchers. On macOS/Linux, compile and run the Java source files with the corresponding JDK commands, adjusting classpaths and paths as needed.
- Use a terminal opened at the project root.

Check the installed Java version:

```bash
java -version
javac -version
```

## Build and Run on Windows

If the corresponding batch files are present in the project root:

1. Build the project:
   ```bat
   build.bat
   ```
2. Start the console application:
   ```bat
   run_console.bat
   ```
3. Start the graphical application:
   ```bat
   run_gui.bat
   ```
4. Run the automated test suite:
   ```bat
   run_tests.bat
   ```

If a launcher is missing or does not work in your environment, inspect its compile/run command and use the installed JDK directly. Run commands from the project root so relative paths to source and data files resolve correctly.

## Running from an IDE

1. Extract the project archive.
2. Open the extracted project folder in an IDE such as IntelliJ IDEA, Eclipse, or Visual Studio Code with Java support.
3. Mark `src` as the source directory if the IDE does not detect it automatically.
4. Use `src/Main.java` as the entry point for the console application.
5. Inspect the UI package for the Swing application entry points and follow the provided launcher instructions.
6. Ensure the JDK version matches the project's requirements.

## Example Use Cases

- Find a student by ID or another supported field.
- Compare linear search with binary search on appropriate data.
- Search a name prefix using a Trie.
- Search for a pattern in text using KMP or Rabin–Karp.
- Measure string similarity using edit distance.
- Sort records and compare algorithm behaviour.
- Explore graph traversal, shortest paths, and allocation-related algorithms.
- Generate or inspect algorithm execution metrics for academic demonstration.

## Testing and Performance

The source tree includes a comprehensive test-suite class. Use the supplied test launcher if available. Performance results depend on the machine, Java version, dataset size, JVM warm-up, and the specific operation. Treat measured timings as experimental results rather than guaranteed timings.

## Data and Privacy

Use synthetic or authorized sample data for demonstrations. Do not commit real student personal information, credentials, or other sensitive academic records to a public repository. Check the `data/` directory before pushing to ensure it contains only data that is safe to publish.

## Academic Purpose

This project is intended to demonstrate how DSA concepts can be applied to a practical student-record use case. It is useful for DSA-3 coursework, project demonstrations, algorithm analysis, and viva preparation.

## Contributors

- **MVA Hruday Deepak** — Roll No. **2520030138**
- **Jaiswal** — Roll No. **2520030135**

Update this section if additional team members need to be listed.

## Notes

- The README describes the architecture and algorithm categories visible in the supplied project package.
- Exact functionality should be verified against the source code and the version you run.
- Do not claim an algorithm is used by a particular user workflow unless that integration is present in the source code; some algorithms may be included as independent demonstrations.

## License

No license file was supplied with the project archive. Add an appropriate license before permitting reuse or redistribution.
