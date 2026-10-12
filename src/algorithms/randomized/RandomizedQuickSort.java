package algorithms.randomized;

import algorithms.sorting.SortCriteria;
import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manual implementation of Randomized QuickSort on Student records.
 * Chooses a pivot uniformly at random from the subarray to avoid worst-case O(n²) behavior.
 */
public class RandomizedQuickSort {

    private static class Metrics {
        long comparisons = 0;
        long swaps = 0;
        long recursiveCalls = 0;
        List<String> steps = new ArrayList<>();
    }

    public static AlgorithmResult sort(List<Student> inputList, SortCriteria criteria, boolean ascending, long seed) {
        long start = System.nanoTime();
        List<Student> list = new ArrayList<>(inputList);
        Random rand = (seed != 0) ? new Random(seed) : new Random();
        Metrics metrics = new Metrics();

        metrics.steps.add(String.format("Starting Randomized QuickSort on %d students (Seed: %d, Field: %s).",
                list.size(), seed, criteria.name()));

        if (list.size() > 1) {
            randomizedQuickSortRec(list, 0, list.size() - 1, criteria, ascending, rand, metrics, 0);
        }

        long duration = System.nanoTime() - start;

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Randomized QuickSort");
        result.setCategory("Randomized Algorithms");
        result.setPurpose("Sort student records with guaranteed expected O(n log n) time irrespective of input order");
        result.setInputSize(list.size());
        result.setDataStructureUsed("Random Pivot Partitioning on ArrayList<Student>");
        result.setOutput(list);
        result.setExecutionTimeNs(duration);
        result.setComparisons(metrics.comparisons);
        result.setIterations(0);
        result.setSwaps(metrics.swaps);
        result.setRecursionCount(metrics.recursiveCalls);
        result.setTimeComplexity("Expected: O(n log n) | Worst: O(n²) (Probability ~ 0)");
        result.setSpaceComplexity("O(log n) Expected Stack");
        result.setExplanation(String.format("Random pivot selection prevented adversarial O(n²) degradations. Total swaps: %d.", metrics.swaps));
        result.setSteps(metrics.steps);

        return result;
    }

    private static void randomizedQuickSortRec(List<Student> list, int low, int high,
                                               SortCriteria criteria, boolean ascending,
                                               Random rand, Metrics metrics, int depth) {
        metrics.recursiveCalls++;
        if (low < high) {
            // Pick random pivot between low and high
            int randomPivotIdx = low + rand.nextInt(high - low + 1);
            swap(list, randomPivotIdx, high);
            metrics.swaps++;

            if (depth <= 2) {
                metrics.steps.add(String.format("Random Pivot chosen at index %d (Student ID: %d) for range [%d..%d]",
                        randomPivotIdx, list.get(high).getStudentId(), low, high));
            }

            int pIndex = partition(list, low, high, criteria, ascending, metrics);

            randomizedQuickSortRec(list, low, pIndex - 1, criteria, ascending, rand, metrics, depth + 1);
            randomizedQuickSortRec(list, pIndex + 1, high, criteria, ascending, rand, metrics, depth + 1);
        }
    }

    private static int partition(List<Student> list, int low, int high,
                                 SortCriteria criteria, boolean ascending, Metrics metrics) {
        Student pivot = list.get(high);
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
        return i + 1;
    }

    private static void swap(List<Student> list, int i, int j) {
        Student temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
