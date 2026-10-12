package algorithms.searching;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Linear Search on Student records.
 * Searches unsorted or arbitrary lists sequentially without built-in helpers.
 */
public class LinearSearch {

    public enum SearchField {
        STUDENT_ID, ROLL_NUMBER, NAME, EMAIL
    }

    public static AlgorithmResult search(List<Student> students, String targetValue, SearchField field) {
        long start = System.nanoTime();
        long comparisons = 0;
        long iterations = 0;
        List<String> steps = new ArrayList<>();
        Student foundStudent = null;
        int foundIndex = -1;

        steps.add(String.format("Initiating Linear Search for '%s' across %d student records.", targetValue, students.size()));

        for (int i = 0; i < students.size(); i++) {
            iterations++;
            Student s = students.get(i);
            comparisons++;

            boolean match = false;
            switch (field) {
                case STUDENT_ID:
                    try {
                        long id = Long.parseLong(targetValue);
                        match = (s.getStudentId() == id);
                    } catch (NumberFormatException e) {
                        match = false;
                    }
                    break;
                case ROLL_NUMBER:
                    match = s.getRollNumber().equalsIgnoreCase(targetValue);
                    break;
                case NAME:
                    match = s.getName().equalsIgnoreCase(targetValue);
                    break;
                case EMAIL:
                    match = s.getEmail().equalsIgnoreCase(targetValue);
                    break;
            }

            if (match) {
                foundStudent = s;
                foundIndex = i;
                steps.add(String.format("Match verified at index %d after %d comparisons.", i, comparisons));
                break;
            }
        }

        if (foundStudent == null) {
            steps.add(String.format("Completed full scan of %d elements. Target not found.", students.size()));
        }

        long duration = System.nanoTime() - start;

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Linear Search");
        result.setCategory("Searching");
        result.setPurpose("Sequential student search without requiring pre-sorted records");
        result.setInputSize(students.size());
        result.setDataStructureUsed("Array / List of Student objects");
        result.setOutput(foundStudent != null ? foundStudent.toString() : "Not Found");
        result.setExecutionTimeNs(duration);
        result.setComparisons(comparisons);
        result.setIterations(iterations);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("Best: O(1) | Avg: O(n) | Worst: O(n)");
        result.setSpaceComplexity("O(1)");
        result.setExplanation(foundStudent != null
                ? String.format("Found student '%s' (ID %d) at index %d after %d comparisons.",
                    foundStudent.getName(), foundStudent.getStudentId(), foundIndex, comparisons)
                : "Target not found after inspecting all records.");
        result.setSteps(steps);

        return result;
    }
}
