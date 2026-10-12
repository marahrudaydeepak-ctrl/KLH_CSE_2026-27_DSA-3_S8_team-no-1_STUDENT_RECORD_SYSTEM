package ui;

import algorithms.searching.BinarySearch;
import algorithms.searching.LinearSearch;
import algorithms.sorting.MergeSort;
import algorithms.sorting.SortCriteria;
import algorithms.string.*;
import datastructures.AVLTree;
import datastructures.BinarySearchTree;
import datastructures.CustomHashTable;
import datastructures.Trie;
import model.AlgorithmResult;
import model.Student;
import services.StudentService;
import java.util.*;

/**
 * Console submenu for the Advanced Algorithm Search System.
 * Supports Linear, Binary, BST, AVL, Hash, KMP, Rabin-Karp, Boyer-Moore,
 * Z Algorithm, Aho-Corasick, Trie, and Fuzzy Search.
 */
public class SearchMenu {

    private final StudentService studentService;
    private final Scanner scanner;

    public SearchMenu(StudentService studentService, Scanner scanner) {
        this.studentService = studentService;
        this.scanner = scanner;
    }

    public void display() {
        while (true) {
            System.out.println("\n====================================================");
            System.out.println("              ADVANCED SEARCH ENGINE MENU           ");
            System.out.println("====================================================");
            System.out.println("0. ⚡ Intelligent Multi-Algorithm Omni-Search (ALL 10 Algos!)");
            System.out.println("1. Linear Search (Unsorted records)");
            System.out.println("2. Binary Search (Logarithmic on sorted records)");
            System.out.println("3. Binary Search Tree (BST) Search");
            System.out.println("4. AVL Tree (Balanced Tree) Search");
            System.out.println("5. Custom Hash Table Lookup (Chaining / Open Addressing)");
            System.out.println("6. Knuth-Morris-Pratt (KMP) Substring Search");
            System.out.println("7. Rabin-Karp Rolling Hash Search");
            System.out.println("8. Boyer-Moore Bad Character Search");
            System.out.println("9. Z-Algorithm Exact Pattern Search");
            System.out.println("10. Aho-Corasick Multi-Keyword Search");
            System.out.println("11. Trie Prefix Autocomplete Search");
            System.out.println("12. Fuzzy Search (Levenshtein / Damerau-Levenshtein)");
            System.out.println("13. Back to Main Menu");
            System.out.print("Enter your choice (0-13): ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "0": handleOmniSearch(); break;
                case "1": handleLinearSearch(); break;
                case "2": handleBinarySearch(); break;
                case "3": handleBSTSearch(); break;
                case "4": handleAVLSearch(); break;
                case "5": handleHashSearch(); break;
                case "6": handleKMPSearch(); break;
                case "7": handleRabinKarpSearch(); break;
                case "8": handleBoyerMooreSearch(); break;
                case "9": handleZSearch(); break;
                case "10": handleAhoCorasickSearch(); break;
                case "11": handleTrieSearch(); break;
                case "12": handleFuzzySearch(); break;
                case "13": return;
                default: System.out.println("Invalid selection. Please enter a valid number (0-13).");
            }
        }
    }

    private void handleOmniSearch() {
        System.out.print("\nEnter Search Query (Name, ID, Skill, or Typo): ");
        String q = scanner.nextLine().trim();
        services.MultiAlgorithmSearchService.OmniSearchResult res =
                services.MultiAlgorithmSearchService.search(studentService, q, null);
        System.out.println("\n" + res.getFormattedSummary());
    }

    private void handleLinearSearch() {
        System.out.print("\nEnter query string to search via Linear Search: ");
        String q = scanner.nextLine().trim();
        AlgorithmResult res = LinearSearch.search(studentService.getAllStudents(), q, LinearSearch.SearchField.NAME);
        if (res.getOutput().equals("Not Found")) {
            // Also try ID
            res = LinearSearch.search(studentService.getAllStudents(), q, LinearSearch.SearchField.STUDENT_ID);
        }
        studentService.getExecutionService().recordExecution(res);
        System.out.println("\n" + res.toFormattedDisplay());
    }

    private void handleBinarySearch() {
        System.out.print("\nEnter Student ID to search via Binary Search: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            // Sort records by ID first using MergeSort
            AlgorithmResult sortRes = MergeSort.sort(studentService.getAllStudents(), SortCriteria.STUDENT_ID, true);
            @SuppressWarnings("unchecked")
            List<Student> sorted = (List<Student>) sortRes.getOutput();

            AlgorithmResult res = BinarySearch.searchById(sorted, id);
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } catch (NumberFormatException e) {
            System.out.println("Student ID must be numeric.");
        }
    }

    private void handleBSTSearch() {
        System.out.print("\nEnter Student ID to search in Binary Search Tree: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            long start = System.nanoTime();
            BinarySearchTree.SearchResult bstRes = studentService.getBst().search(id);
            long duration = System.nanoTime() - start;

            AlgorithmResult res = new AlgorithmResult();
            res.setAlgorithmName("Binary Search Tree Lookup");
            res.setCategory("Tree Searching");
            res.setPurpose("Locate student by ID using BST pointer traversal");
            res.setInputSize(studentService.getBst().getSize());
            res.setDataStructureUsed("BinarySearchTree");
            res.setOutput(bstRes.found ? bstRes.student.toString() : "Not Found");
            res.setExecutionTimeNs(duration);
            res.setComparisons(bstRes.comparisons);
            res.setIterations(bstRes.comparisons);
            res.setTimeComplexity("Best: O(1) | Avg: O(log n) | Worst: O(n)");
            res.setSpaceComplexity("O(1)");
            res.setExplanation(String.format("BST search traversed %d nodes before %s.", bstRes.comparisons, bstRes.found ? "finding student" : "reaching null"));
            res.addStep(String.format("Searched BST root node and branched left/right based on Student ID %d.", id));
            res.addStep(String.format("Result: %s", bstRes.found ? bstRes.student.getName() : "Not Found"));

            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric ID.");
        }
    }

    private void handleAVLSearch() {
        System.out.print("\nEnter Student ID to search in Self-Balancing AVL Tree: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            long start = System.nanoTime();
            AVLTree.AVLSearchResult avlRes = studentService.getAvlTree().search(id);
            long duration = System.nanoTime() - start;

            AlgorithmResult res = new AlgorithmResult();
            res.setAlgorithmName("AVL Tree Search");
            res.setCategory("Balanced Tree Searching");
            res.setPurpose("Guaranteed logarithmic lookup on height-balanced student records");
            res.setInputSize(studentService.getAvlTree().getSize());
            res.setDataStructureUsed("AVLTree (Height: " + studentService.getAvlTree().height(studentService.getAvlTree().getRoot()) + ")");
            res.setOutput(avlRes.found ? avlRes.student.toString() : "Not Found");
            res.setExecutionTimeNs(duration);
            res.setComparisons(avlRes.comparisons);
            res.setIterations(avlRes.comparisons);
            res.setTimeComplexity("O(log n) Strictly Balanced");
            res.setSpaceComplexity("O(1)");
            res.setExplanation(String.format("Balanced AVL Tree completed search in %d comparisons. Bound is ceil(1.44 log2 n).", avlRes.comparisons));
            res.addStep("Queried height-balanced root node and traversed binary branches.");
            res.addStep(String.format("Inspection finished with status: %b", avlRes.found));

            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric ID.");
        }
    }

    private void handleHashSearch() {
        System.out.print("\nEnter Student ID, Roll Number, or Email to lookup in CustomHashTable: ");
        String key = scanner.nextLine().trim();

        long start = System.nanoTime();
        CustomHashTable.HashLookupResult lookup = studentService.getHashTable().searchChaining(key);
        long duration = System.nanoTime() - start;

        AlgorithmResult res = new AlgorithmResult();
        res.setAlgorithmName("Custom Hash Table Lookup");
        res.setCategory("Hashing");
        res.setPurpose("Constant-time O(1) average lookup using polynomial rolling hash");
        res.setInputSize(studentService.getHashTable().getSize());
        res.setDataStructureUsed(String.format("CustomHashTable [Cap: %d, Bucket: %d]", studentService.getHashTable().getCapacity(), lookup.bucketIndex));
        res.setOutput(lookup.found ? lookup.student.toString() : "Not Found in Hash Table");
        res.setExecutionTimeNs(duration);
        res.setComparisons(lookup.stepsTaken);
        res.setIterations(lookup.stepsTaken);
        res.setTimeComplexity("Best: O(1) | Avg: O(1) | Worst: O(n)");
        res.setSpaceComplexity("O(1)");
        res.setExplanation(String.format("Key '%s' produced Hash=%d, Bucket=%d, Collisions in bucket=%d.",
                key, lookup.hashValue, lookup.bucketIndex, lookup.collisionCount));
        res.addStep(String.format("Step 1: Computed hash value: %d", lookup.hashValue));
        res.addStep(String.format("Step 2: Mapped to bucket index: %d", lookup.bucketIndex));
        res.addStep(String.format("Step 3: Traversed collision chain (%d steps taken).", lookup.stepsTaken));

        studentService.getExecutionService().recordExecution(res);
        System.out.println("\n" + res.toFormattedDisplay());
    }

    private void handleKMPSearch() {
        System.out.print("\nEnter pattern to search via Knuth-Morris-Pratt (e.g. 'Java', 'Reddy', 'Cloud'): ");
        String pat = scanner.nextLine().trim();
        AlgorithmResult res = KMP.search(studentService.getAllStudents(), pat);
        studentService.getExecutionService().recordExecution(res);
        System.out.println("\n" + res.toFormattedDisplay());
    }

    private void handleRabinKarpSearch() {
        System.out.print("\nEnter pattern to search via Rabin-Karp Rolling Hash: ");
        String pat = scanner.nextLine().trim();
        AlgorithmResult res = RabinKarp.search(studentService.getAllStudents(), pat);
        studentService.getExecutionService().recordExecution(res);
        System.out.println("\n" + res.toFormattedDisplay());
    }

    private void handleBoyerMooreSearch() {
        System.out.print("\nEnter pattern to search via Boyer-Moore (Bad Character Heuristic): ");
        String pat = scanner.nextLine().trim();
        AlgorithmResult res = BoyerMoore.search(studentService.getAllStudents(), pat);
        studentService.getExecutionService().recordExecution(res);
        System.out.println("\n" + res.toFormattedDisplay());
    }

    private void handleZSearch() {
        System.out.print("\nEnter pattern to search via Z-Algorithm: ");
        String pat = scanner.nextLine().trim();
        AlgorithmResult res = ZAlgorithm.search(studentService.getAllStudents(), pat);
        studentService.getExecutionService().recordExecution(res);
        System.out.println("\n" + res.toFormattedDisplay());
    }

    private void handleAhoCorasickSearch() {
        System.out.println("\n--- AHO-CORASICK MULTI-KEYWORD SEARCH ---");
        System.out.println("Default Keywords: [Java, Python, C++, SQL, Machine Learning, Data Structures]");
        System.out.print("Enter comma-separated keywords (or press Enter to use default set): ");
        String input = scanner.nextLine().trim();

        List<String> keywords;
        if (input.isEmpty()) {
            keywords = List.of("Java", "Python", "C++", "SQL", "Machine Learning", "Data Structures");
        } else {
            keywords = Arrays.asList(input.split(","));
        }

        AhoCorasick ac = new AhoCorasick(keywords);
        AlgorithmResult res = ac.searchAll(studentService.getAllStudents());
        studentService.getExecutionService().recordExecution(res);
        System.out.println("\n" + res.toFormattedDisplay());
    }

    private void handleTrieSearch() {
        System.out.print("\nEnter prefix for student-name / skill autocomplete (e.g. 'Te', 'Py', 'Ja'): ");
        String prefix = scanner.nextLine().trim();

        Trie.TrieSearchResult nameRes = studentService.getNameTrie().prefixSearch(prefix);
        Trie.TrieSearchResult skillRes = studentService.getSkillTrie().prefixSearch(prefix);

        AlgorithmResult res = new AlgorithmResult();
        res.setAlgorithmName("Trie Prefix Autocomplete");
        res.setCategory("Prefix Tree Searching");
        res.setPurpose("Locate all student names and skills sharing common prefix in O(L) time");
        res.setInputSize(studentService.getNameTrie().getWordCount());
        res.setDataStructureUsed("Trie (Prefix Tree)");
        res.setOutput(String.format("Found %d matching names, %d matching skills",
                nameRes.matchedWords.size(), skillRes.matchedWords.size()));
        res.setExecutionTimeNs(nameRes.executionTimeNs + skillRes.executionTimeNs);
        res.setComparisons(nameRes.comparisons + skillRes.comparisons);
        res.setIterations(prefix.length());
        res.setTimeComplexity("O(L) where L = prefix length");
        res.setSpaceComplexity("O(L) search stack");
        res.setExplanation(String.format("Prefix '%s' matched words in Trie across %d nodes.", prefix, nameRes.comparisons));

        res.addStep("Step 1: Traversed Trie from root following characters: " + prefix);
        res.addStep(String.format("Step 2: Autocomplete matching Student Names: %s", nameRes.matchedWords));
        res.addStep(String.format("Step 3: Autocomplete matching Skills: %s", skillRes.matchedWords));

        studentService.getExecutionService().recordExecution(res);
        System.out.println("\n" + res.toFormattedDisplay());
    }

    private void handleFuzzySearch() {
        System.out.print("\nEnter misspelled student query (e.g. 'Tejes Reddy', 'Sneha Nyer'): ");
        String query = scanner.nextLine().trim();

        Student bestMatch = null;
        double bestSimilarity = 0.0;
        int bestDist = Integer.MAX_VALUE;

        for (Student s : studentService.getAllStudents()) {
            DamerauLevenshtein.DLResult dl = DamerauLevenshtein.compute(query, s.getName());
            if (dl.similarityPercentage > bestSimilarity) {
                bestSimilarity = dl.similarityPercentage;
                bestMatch = s;
                bestDist = dl.distance;
            }
        }

        if (bestMatch != null) {
            AlgorithmResult res = DamerauLevenshtein.compareWithDetails(query, bestMatch.getName());
            res.setPurpose("Typo-tolerant fuzzy student record matching");
            res.addStep(String.format("Closest Database Match: ID %d - %s (%s)",
                    bestMatch.getStudentId(), bestMatch.getName(), bestMatch.getDepartment()));
            res.addStep(String.format("Edit Distance = %d | Calculated Similarity = %.2f%%",
                    bestDist, bestSimilarity));

            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
            System.out.println("BEST MATCH PROFILE:\n" + bestMatch.toDetailString());
        } else {
            System.out.println("No matching student profiles found.");
        }
    }
}
