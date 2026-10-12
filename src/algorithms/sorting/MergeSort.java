package algorithms.sorting;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Merge Sort on Student records.
 * Stable divide-and-conquer sorting tracking recursive calls and comparisons.
 */
public class MergeSort {

    private static class Metrics {
        long comparisons = 0;
        long swaps = 0; // copy writes
        long recursiveCalls = 0;
        List<String> steps = new ArrayList<>();
    }

    public static AlgorithmResult sort(List<Student> inputList, SortCriteria criteria, boolean ascending) {
        long start = System.nanoTime();
        List<Student> list = new ArrayList<>(inputList);
        Metrics metrics = new Metrics();

        metrics.steps.add(String.format("Starting Merge Sort on %d student records by %s.",
                list.size(), criteria.name()));

        if (list.size() > 1) {
            mergeSortRec(list, 0, list.size() - 1, criteria, ascending, metrics, 0);
        }

        long duration = System.nanoTime() - start;

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Merge Sort");
        result.setCategory("Sorting");
        result.setPurpose(String.format("Stable divide-and-conquer student sorting by %s", criteria.name()));
        result.setInputSize(list.size());
        result.setDataStructureUsed("Array / List with Auxiliary Buffers");
        result.setOutput(list);
        result.setExecutionTimeNs(duration);
        result.setComparisons(metrics.comparisons);
        result.setIterations(0);
        result.setSwaps(metrics.swaps);
        result.setRecursionCount(metrics.recursiveCalls);
        result.setTimeComplexity("Best: O(n log n) | Avg: O(n log n) | Worst: O(n log n)");
        result.setSpaceComplexity("O(n) Auxiliary Buffer");
        result.setExplanation(String.format("Divided list recursively (%d calls) and merged partitions with %d comparisons.",
                metrics.recursiveCalls, metrics.comparisons));
        result.setSteps(metrics.steps);

        return result;
    }

    private static void mergeSortRec(List<Student> list, int left, int right,
                                     SortCriteria criteria, boolean ascending,
                                     Metrics metrics, int depth) {
        metrics.recursiveCalls++;
        if (left < right) {
            int mid = left + (right - left) / 2;

            if (depth <= 2) {
                metrics.steps.add(String.format("Divide: Splitting subarray [%d..%d] at mid=%d", left, right, mid));
            }

            mergeSortRec(list, left, mid, criteria, ascending, metrics, depth + 1);
            mergeSortRec(list, mid + 1, right, criteria, ascending, metrics, depth + 1);

            merge(list, left, mid, right, criteria, ascending, metrics, depth);
        }
    }

    private static void merge(List<Student> list, int left, int mid, int right,
                              SortCriteria criteria, boolean ascending,
                              Metrics metrics, int depth) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        List<Student> leftSub = new ArrayList<>(n1);
        List<Student> rightSub = new ArrayList<>(n2);

        for (int i = 0; i < n1; i++) leftSub.add(list.get(left + i));
        for (int j = 0; j < n2; j++) rightSub.add(list.get(mid + 1 + j));

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            metrics.comparisons++;
            if (SortCriteria.compareStudents(leftSub.get(i), rightSub.get(j), criteria, ascending) <= 0) {
                list.set(k, leftSub.get(i));
                i++;
            } else {
                list.set(k, rightSub.get(j));
                j++;
            }
            metrics.swaps++;
            k++;
        }

        while (i < n1) {
            list.set(k, leftSub.get(i));
            metrics.swaps++;
            i++;
            k++;
        }

        while (j < n2) {
            list.set(k, rightSub.get(j));
            metrics.swaps++;
            j++;
            k++;
        }

        if (depth <= 2) {
            metrics.steps.add(String.format("Conquer: Merged ranges [%d..%d] and [%d..%d] into [%d..%d]",
                    left, mid, mid + 1, right, left, right));
        }
    }
}
