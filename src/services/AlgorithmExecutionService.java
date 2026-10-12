package services;

import algorithms.networkflow.Dinic;
import algorithms.networkflow.EdmondsKarp;
import algorithms.networkflow.FordFulkerson;
import algorithms.searching.BinarySearch;
import algorithms.searching.LinearSearch;
import algorithms.sorting.*;
import algorithms.string.*;
import datastructures.CustomStack;
import model.AlgorithmResult;
import model.Student;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service managing Algorithm Execution History (backed by CustomStack)
 * and providing side-by-side benchmarking comparisons across algorithm families.
 */
public class AlgorithmExecutionService {

    public static class HistoryEntry {
        public AlgorithmResult result;
        public LocalDateTime timestamp;

        public HistoryEntry(AlgorithmResult result) {
            this.result = result;
            this.timestamp = LocalDateTime.now();
        }

        @Override
        public String toString() {
            return String.format("[%s] %-25s | Category: %-15s | Size: %-5d | Time: %7.3f ms | %s",
                    timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    result.getAlgorithmName(), result.getCategory(), result.getInputSize(),
                    result.getExecutionTimeMs(), result.getTimeComplexity());
        }
    }

    private final CustomStack<HistoryEntry> historyStack;

    public AlgorithmExecutionService() {
        this.historyStack = new CustomStack<>();
    }

    public void recordExecution(AlgorithmResult result) {
        if (result != null) {
            historyStack.push(new HistoryEntry(result));
        }
    }

    public List<HistoryEntry> getHistory() {
        return historyStack.toList();
    }

    public void clearHistory() {
        historyStack.clear();
    }

    // ================= COMPARISON BENCHMARKS =================

    /**
     * Compares Sorting algorithms on an identical student dataset.
     */
    public String compareSortingAlgorithms(List<Student> students, SortCriteria criteria) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================\n");
        sb.append(String.format("                      SORTING ALGORITHMS BENCHMARK COMPARISON (Input: %d records)\n", students.size()));
        sb.append("========================================================================================================\n");
        sb.append(String.format("%-18s | %-12s | %-12s | %-10s | %-24s | %-10s\n",
                "Algorithm", "Time (ms)", "Comparisons", "Swaps", "Time Complexity", "Space"));
        sb.append("--------------------------------------------------------------------------------------------------------\n");

        List<AlgorithmResult> results = new ArrayList<>();

        // If dataset is very large (> 2000), omit quadratic algorithms to prevent stalling
        boolean runQuadratic = students.size() <= 2000;

        if (runQuadratic) {
            results.add(BubbleSort.sort(students, criteria, false));
            results.add(SelectionSort.sort(students, criteria, false));
            results.add(InsertionSort.sort(students, criteria, false));
        }

        results.add(MergeSort.sort(students, criteria, false));
        results.add(QuickSort.sort(students, criteria, false));
        results.add(HeapSort.sort(students, criteria, false));

        for (AlgorithmResult res : results) {
            recordExecution(res);
            sb.append(String.format("%-18s | %10.4f ms | %,12d | %,10d | %-24s | %-10s\n",
                    res.getAlgorithmName(),
                    res.getExecutionTimeMs(),
                    res.getComparisons(),
                    res.getSwaps(),
                    res.getTimeComplexity(),
                    res.getSpaceComplexity()));
        }

        sb.append("========================================================================================================\n");
        return sb.toString();
    }

    /**
     * Compares String pattern searching algorithms.
     */
    public String compareStringAlgorithms(List<Student> students, String pattern) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================\n");
        sb.append(String.format("                   STRING SEARCH ALGORITHMS BENCHMARK (Pattern: \"%s\")\n", pattern));
        sb.append("========================================================================================================\n");
        sb.append(String.format("%-22s | %-12s | %-12s | %-15s | %-20s\n",
                "Algorithm", "Time (ms)", "Comparisons", "Output", "Complexity"));
        sb.append("--------------------------------------------------------------------------------------------------------\n");

        List<AlgorithmResult> results = new ArrayList<>();
        results.add(NaiveStringMatching.search(students, pattern));
        results.add(KMP.search(students, pattern));
        results.add(ZAlgorithm.search(students, pattern));
        results.add(RabinKarp.search(students, pattern));
        results.add(BoyerMoore.search(students, pattern));

        for (AlgorithmResult res : results) {
            recordExecution(res);
            sb.append(String.format("%-22s | %10.4f ms | %,12d | %-15s | %-20s\n",
                    res.getAlgorithmName(),
                    res.getExecutionTimeMs(),
                    res.getComparisons(),
                    res.getOutput(),
                    res.getTimeComplexity()));
        }

        sb.append("========================================================================================================\n");
        return sb.toString();
    }

    /**
     * Compares Network Flow algorithms on the same capacitated network.
     */
    public String compareFlowAlgorithms(Map<String, Map<String, Integer>> network, String source, String sink) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================\n");
        sb.append(String.format("               MAXIMUM NETWORK FLOW BENCHMARK COMPARISON (Source: %s -> Sink: %s)\n", source, sink));
        sb.append("========================================================================================================\n");
        sb.append(String.format("%-20s | %-12s | %-12s | %-10s | %-20s\n",
                "Algorithm", "Time (ms)", "Max Flow", "Phases/Iter", "Complexity"));
        sb.append("--------------------------------------------------------------------------------------------------------\n");

        AlgorithmResult ff = FordFulkerson.executeFordFulkerson(network, source, sink, "Benchmark");
        AlgorithmResult ek = EdmondsKarp.executeEdmondsKarp(network, source, sink, "Benchmark");
        AlgorithmResult dinic = Dinic.executeDinic(network, source, sink, "Benchmark");

        List<AlgorithmResult> results = List.of(ff, ek, dinic);
        for (AlgorithmResult res : results) {
            recordExecution(res);
            sb.append(String.format("%-20s | %10.4f ms | %-12s | %,10d | %-20s\n",
                    res.getAlgorithmName(),
                    res.getExecutionTimeMs(),
                    res.getOutput(),
                    res.getIterations(),
                    res.getTimeComplexity()));
        }

        sb.append("========================================================================================================\n");
        return sb.toString();
    }

    /**
     * Compares Searching algorithms on identical student ID lookups.
     */
    public String compareSearchAlgorithms(List<Student> students, long targetId) {
        // Pre-sort for binary search
        AlgorithmResult sortRes = MergeSort.sort(students, SortCriteria.STUDENT_ID, true);
        @SuppressWarnings("unchecked")
        List<Student> sortedStudents = (List<Student>) sortRes.getOutput();

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================\n");
        sb.append(String.format("                     SEARCH ALGORITHMS BENCHMARK COMPARISON (Target ID: %d)\n", targetId));
        sb.append("========================================================================================================\n");
        sb.append(String.format("%-20s | %-12s | %-12s | %-15s | %-20s\n",
                "Algorithm", "Time (ms)", "Comparisons", "Output", "Complexity"));
        sb.append("--------------------------------------------------------------------------------------------------------\n");

        AlgorithmResult lin = LinearSearch.search(students, String.valueOf(targetId), LinearSearch.SearchField.STUDENT_ID);
        AlgorithmResult bin = BinarySearch.searchById(sortedStudents, targetId);

        List<AlgorithmResult> results = List.of(lin, bin);
        for (AlgorithmResult res : results) {
            recordExecution(res);
            sb.append(String.format("%-20s | %10.4f ms | %,12d | %-15s | %-20s\n",
                    res.getAlgorithmName(),
                    res.getExecutionTimeMs(),
                    res.getComparisons(),
                    res.getOutput().toString().length() > 14 ? "Found Record" : res.getOutput(),
                    res.getTimeComplexity()));
        }

        sb.append("========================================================================================================\n");
        return sb.toString();
    }
}
