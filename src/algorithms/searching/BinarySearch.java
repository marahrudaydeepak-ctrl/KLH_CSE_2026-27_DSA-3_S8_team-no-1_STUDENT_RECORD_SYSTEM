package algorithms.searching;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Binary Search on pre-sorted Student records.
 * Tracks divide-and-conquer bounds (low, mid, high) and comparisons without built-in helpers.
 */
public class BinarySearch {

    public static AlgorithmResult searchById(List<Student> sortedStudents, long targetId) {
        long start = System.nanoTime();
        long comparisons = 0;
        long iterations = 0;
        List<String> steps = new ArrayList<>();
        Student foundStudent = null;
        int foundIndex = -1;

        int low = 0;
        int high = sortedStudents.size() - 1;

        steps.add(String.format("Starting Binary Search for Student ID: %d over %d sorted records [Range: 0 to %d].",
                targetId, sortedStudents.size(), high));

        while (low <= high) {
            iterations++;
            int mid = low + (high - low) / 2;
            Student midStudent = sortedStudents.get(mid);
            long midId = midStudent.getStudentId();

            comparisons++;
            steps.add(String.format("Iteration %d: Low=%d, High=%d, Mid=%d (Inspecting ID: %d)",
                    iterations, low, high, mid, midId));

            if (midId == targetId) {
                foundStudent = midStudent;
                foundIndex = mid;
                steps.add(String.format("Target ID %d located at index %d after %d comparisons.", targetId, mid, comparisons));
                break;
            } else if (midId < targetId) {
                steps.add(String.format("Target ID %d > Mid ID %d. Narrowing search interval to right half [%d, %d].",
                        targetId, midId, mid + 1, high));
                low = mid + 1;
            } else {
                steps.add(String.format("Target ID %d < Mid ID %d. Narrowing search interval to left half [%d, %d].",
                        targetId, midId, low, mid - 1));
                high = mid - 1;
            }
        }

        if (foundStudent == null) {
            steps.add(String.format("Exhausted search interval. Student ID %d is not present in database.", targetId));
        }

        long duration = System.nanoTime() - start;

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Binary Search");
        result.setCategory("Searching");
        result.setPurpose("Logarithmic search on sorted student records");
        result.setInputSize(sortedStudents.size());
        result.setDataStructureUsed("Sorted Array / List of Student records");
        result.setOutput(foundStudent != null ? foundStudent.toString() : "Not Found");
        result.setExecutionTimeNs(duration);
        result.setComparisons(comparisons);
        result.setIterations(iterations);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("Best: O(1) | Avg: O(log n) | Worst: O(log n)");
        result.setSpaceComplexity("O(1)");
        result.setExplanation(foundStudent != null
                ? String.format("Found student '%s' at index %d using logarithmic pruning in %d comparisons.",
                    foundStudent.getName(), foundIndex, comparisons)
                : "Student record was not found after halving intervals.");
        result.setSteps(steps);

        return result;
    }
}
