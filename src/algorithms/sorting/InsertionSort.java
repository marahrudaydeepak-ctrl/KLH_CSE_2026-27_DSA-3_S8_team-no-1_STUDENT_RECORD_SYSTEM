package algorithms.sorting;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Insertion Sort on Student records.
 * Efficient for small or nearly sorted collections of student data.
 */
public class InsertionSort {

    public static AlgorithmResult sort(List<Student> inputList, SortCriteria criteria, boolean ascending) {
        long start = System.nanoTime();
        List<Student> list = new ArrayList<>(inputList);
        int n = list.size();
        long comparisons = 0;
        long swaps = 0; // element shifts
        long iterations = 0;
        List<String> steps = new ArrayList<>();

        steps.add(String.format("Starting Insertion Sort on %d records by %s (%s).",
                n, criteria.name(), ascending ? "Ascending" : "Descending"));

        for (int i = 1; i < n; i++) {
            iterations++;
            Student key = list.get(i);
            int j = i - 1;

            while (j >= 0) {
                comparisons++;
                if (SortCriteria.compareStudents(list.get(j), key, criteria, ascending) > 0) {
                    list.set(j + 1, list.get(j));
                    swaps++;
                    j--;
                } else {
                    break;
                }
            }
            list.set(j + 1, key);

            if (i < 3 || i == n - 1) {
                steps.add(String.format("Inserted record #%d into sorted partition [0..%d]. Shifts so far: %d.",
                        key.getStudentId(), i, swaps));
            }
        }

        long duration = System.nanoTime() - start;

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Insertion Sort");
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
        result.setExplanation(String.format("Sorted %d records in %d iterations with %d comparisons and %d shifts.",
                n, iterations, comparisons, swaps));
        result.setSteps(steps);

        return result;
    }
}
