package algorithms.sorting;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Bubble Sort on Student records.
 * Uses adjacent element comparisons with early termination flag.
 */
public class BubbleSort {

    public static AlgorithmResult sort(List<Student> inputList, SortCriteria criteria, boolean ascending) {
        long start = System.nanoTime();
        List<Student> list = new ArrayList<>(inputList);
        int n = list.size();
        long comparisons = 0;
        long swaps = 0;
        long iterations = 0;
        List<String> steps = new ArrayList<>();

        steps.add(String.format("Starting Bubble Sort on %d records by %s (%s).",
                n, criteria.name(), ascending ? "Ascending" : "Descending"));

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            iterations++;
            for (int j = 0; j < n - i - 1; j++) {
                comparisons++;
                if (SortCriteria.compareStudents(list.get(j), list.get(j + 1), criteria, ascending) > 0) {
                    Student temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swaps++;
                    swapped = true;
                }
            }
            if (i < 3 || i == n - 2 || !swapped) {
                steps.add(String.format("Pass %d: Swapped in this pass = %b. Total swaps so far: %d.", i + 1, swapped, swaps));
            }
            if (!swapped) {
                steps.add(String.format("Early termination at pass %d: Array is fully sorted.", i + 1));
                break;
            }
        }

        long duration = System.nanoTime() - start;

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Bubble Sort");
        result.setCategory("Sorting");
        result.setPurpose(String.format("Sort student records by %s", criteria.name()));
        result.setInputSize(n);
        result.setDataStructureUsed("Array / List of Student records");
        result.setOutput(list);
        result.setExecutionTimeNs(duration);
        result.setComparisons(comparisons);
        result.setIterations(iterations);
        result.setSwaps(swaps);
        result.setRecursionCount(0);
        result.setTimeComplexity("Best: O(n) | Avg: O(n²) | Worst: O(n²)");
        result.setSpaceComplexity("O(1) Auxiliary");
        result.setExplanation(String.format("Sorted %d records in %d passes with %d comparisons and %d swaps.",
                n, iterations, comparisons, swaps));
        result.setSteps(steps);

        return result;
    }
}
