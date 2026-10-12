package algorithms.sorting;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Quick Sort on Student records.
 * Uses in-place partitioning with pivot tracking, recursive metrics, and step logs.
 */
public class QuickSort {

    private static class Metrics {
        long comparisons = 0;
        long swaps = 0;
        long recursiveCalls = 0;
        List<String> steps = new ArrayList<>();
    }

    public static AlgorithmResult sort(List<Student> inputList, SortCriteria criteria, boolean ascending) {
        long start = System.nanoTime();
        List<Student> list = new ArrayList<>(inputList);
        Metrics metrics = new Metrics();

        metrics.steps.add(String.format("Step 1: Initiating Quick Sort on %d student records by %s.",
                list.size(), criteria.name()));

        if (list.size() > 1) {
            quickSortRec(list, 0, list.size() - 1, criteria, ascending, metrics, 0);
        }

        metrics.steps.add("Final Step: Return sorted records.");

        long duration = System.nanoTime() - start;

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Quick Sort");
        result.setCategory("Sorting");
        result.setPurpose(String.format("Sort students according to %s", criteria.name()));
        result.setInputSize(list.size());
        result.setDataStructureUsed("ArrayList<Student>");
        result.setOutput(list);
        result.setExecutionTimeNs(duration);
        result.setComparisons(metrics.comparisons);
        result.setIterations(0);
        result.setSwaps(metrics.swaps);
        result.setRecursionCount(metrics.recursiveCalls);
        result.setTimeComplexity("Best: O(n log n) | Avg: O(n log n) | Worst: O(n²)");
        result.setSpaceComplexity("O(log n) Call Stack");
        result.setExplanation(String.format("Partitioned records using pivot selections with %d comparisons and %d swaps.",
                metrics.comparisons, metrics.swaps));
        result.setSteps(metrics.steps);

        return result;
    }

    private static void quickSortRec(List<Student> list, int low, int high,
                                     SortCriteria criteria, boolean ascending,
                                     Metrics metrics, int depth) {
        metrics.recursiveCalls++;
        if (low < high) {
            int pivotIndex = partition(list, low, high, criteria, ascending, metrics, depth);

            if (depth <= 2) {
                metrics.steps.add(String.format("Recursively sort left side [%d..%d]", low, pivotIndex - 1));
            }
            quickSortRec(list, low, pivotIndex - 1, criteria, ascending, metrics, depth + 1);

            if (depth <= 2) {
                metrics.steps.add(String.format("Recursively sort right side [%d..%d]", pivotIndex + 1, high));
            }
            quickSortRec(list, pivotIndex + 1, high, criteria, ascending, metrics, depth + 1);
        }
    }

    private static int partition(List<Student> list, int low, int high,
                                 SortCriteria criteria, boolean ascending,
                                 Metrics metrics, int depth) {
        Student pivot = list.get(high);
        if (depth <= 2) {
            metrics.steps.add(String.format("Select pivot: Student ID %d at index %d; Partition range [%d..%d]",
                    pivot.getStudentId(), high, low, high));
        }

        int i = low - 1;
        for (int j = low; j < high; j++) {
            metrics.comparisons++;
            if (SortCriteria.compareStudents(list.get(j), pivot, criteria, ascending) <= 0) {
                i++;
                swap(list, i, j);
                metrics.swaps++;
            }
        }
        swap(list, i + 1, high);
        metrics.swaps++;

        if (depth <= 2) {
            metrics.steps.add(String.format("Partition records: Placed pivot at final sorted index %d", i + 1));
        }
        return i + 1;
    }

    private static void swap(List<Student> list, int i, int j) {
        Student temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
