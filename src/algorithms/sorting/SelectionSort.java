package algorithms.sorting;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Selection Sort on Student records.
 * Repeatedly identifies the extremum element from the unsorted sub-array.
 */
public class SelectionSort {

    public static AlgorithmResult sort(List<Student> inputList, SortCriteria criteria, boolean ascending) {
        long start = System.nanoTime();
        List<Student> list = new ArrayList<>(inputList);
        int n = list.size();
        long comparisons = 0;
        long swaps = 0;
        long iterations = 0;
        List<String> steps = new ArrayList<>();

        steps.add(String.format("Starting Selection Sort on %d records by %s (%s).",
                n, criteria.name(), ascending ? "Ascending" : "Descending"));

        for (int i = 0; i < n - 1; i++) {
            iterations++;
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                comparisons++;
                if (SortCriteria.compareStudents(list.get(j), list.get(minIdx), criteria, ascending) < 0) {
                    minIdx = j;
                }
            }

            if (minIdx != i) {
                Student temp = list.get(i);
                list.set(i, list.get(minIdx));
                list.set(minIdx, temp);
                swaps++;
            }

            if (i < 3 || i == n - 2) {
                steps.add(String.format("Pass %d: Minimum element placed at position %d. Total swaps so far: %d.",
                        i + 1, i, swaps));
            }
        }

        long duration = System.nanoTime() - start;

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Selection Sort");
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
        result.setTimeComplexity("Best: O(n²) | Avg: O(n²) | Worst: O(n²)");
        result.setSpaceComplexity("O(1) Auxiliary");
        result.setExplanation(String.format("Completed %d passes with %d comparisons and %d swaps.",
                n - 1, comparisons, swaps));
        result.setSteps(steps);

        return result;
    }
}
