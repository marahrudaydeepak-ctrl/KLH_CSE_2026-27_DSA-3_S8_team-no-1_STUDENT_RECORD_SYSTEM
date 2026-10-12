package algorithms.sorting;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Heap Sort on Student records.
 * Transforms the array into a binary max/min-heap in-place and extracts elements.
 */
public class HeapSort {

    private static class Metrics {
        long comparisons = 0;
        long swaps = 0;
        List<String> steps = new ArrayList<>();
    }

    public static AlgorithmResult sort(List<Student> inputList, SortCriteria criteria, boolean ascending) {
        long start = System.nanoTime();
        List<Student> list = new ArrayList<>(inputList);
        int n = list.size();
        Metrics metrics = new Metrics();

        metrics.steps.add(String.format("Phase 1: Building initial heap from %d student records.", n));

        // Build heap (rearrange list)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i, criteria, ascending, metrics);
        }
        metrics.steps.add("Heap structure successfully built. Commencing element extraction.");

        // One by one extract elements from heap
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            Student temp = list.get(0);
            list.set(0, list.get(i));
            list.set(i, temp);
            metrics.swaps++;

            if (i == n - 1 || i == n / 2 || i == 1) {
                metrics.steps.add(String.format("Extracted root to index %d. Restoring heap property for size %d.", i, i));
            }

            // Call heapify on the reduced heap
            heapify(list, i, 0, criteria, ascending, metrics);
        }

        long duration = System.nanoTime() - start;

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Heap Sort");
        result.setCategory("Sorting");
        result.setPurpose(String.format("In-place heap-based student sorting by %s", criteria.name()));
        result.setInputSize(n);
        result.setDataStructureUsed("Binary Heap (In-place array)");
        result.setOutput(list);
        result.setExecutionTimeNs(duration);
        result.setComparisons(metrics.comparisons);
        result.setIterations(n);
        result.setSwaps(metrics.swaps);
        result.setRecursionCount(0);
        result.setTimeComplexity("Best: O(n log n) | Avg: O(n log n) | Worst: O(n log n)");
        result.setSpaceComplexity("O(1) Auxiliary");
        result.setExplanation(String.format("Heapified array and sorted elements with %d comparisons and %d swaps.",
                metrics.comparisons, metrics.swaps));
        result.setSteps(metrics.steps);

        return result;
    }

    private static void heapify(List<Student> list, int n, int i,
                                SortCriteria criteria, boolean ascending,
                                Metrics metrics) {
        int target = i; // Initialize largest/smallest as root
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n) {
            metrics.comparisons++;
            if (SortCriteria.compareStudents(list.get(left), list.get(target), criteria, ascending) > 0) {
                target = left;
            }
        }

        if (right < n) {
            metrics.comparisons++;
            if (SortCriteria.compareStudents(list.get(right), list.get(target), criteria, ascending) > 0) {
                target = right;
            }
        }

        if (target != i) {
            Student swap = list.get(i);
            list.set(i, list.get(target));
            list.set(target, swap);
            metrics.swaps++;

            // Recursively heapify the affected sub-tree
            heapify(list, n, target, criteria, ascending, metrics);
        }
    }
}
