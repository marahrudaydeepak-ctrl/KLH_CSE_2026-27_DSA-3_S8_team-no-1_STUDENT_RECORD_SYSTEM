package algorithms.string;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Naive Pattern Matching on Student records.
 * Searches student profiles character by character across names, skills, and projects.
 */
public class NaiveStringMatching {

    public static class StringMatchOccurrence {
        public Student student;
        public String field;
        public int position;

        public StringMatchOccurrence(Student student, String field, int position) {
            this.student = student;
            this.field = field;
            this.position = position;
        }

        @Override
        public String toString() {
            return String.format("[%s (ID: %d) in '%s' at pos %d]",
                    student.getName(), student.getStudentId(), field, position);
        }
    }

    public static AlgorithmResult search(List<Student> students, String pattern) {
        long start = System.nanoTime();
        long comparisons = 0;
        long iterations = 0;
        List<StringMatchOccurrence> occurrences = new ArrayList<>();
        List<String> steps = new ArrayList<>();

        steps.add(String.format("Starting Naive String Matching for pattern: \"%s\" across %d student profiles.",
                pattern, students.size()));

        if (pattern == null || pattern.isEmpty()) {
            return emptyResult();
        }

        String patLower = pattern.toLowerCase();
        int m = patLower.length();

        for (Student s : students) {
            iterations++;
            // Check student name
            searchInText(s.getName(), "Name", s, patLower, m, occurrences, steps, comparisonsRef(comparisons));
            comparisons = lastComparisons;

            // Check skills
            for (String skill : s.getSkills()) {
                searchInText(skill, "Skill", s, patLower, m, occurrences, steps, comparisonsRef(comparisons));
                comparisons = lastComparisons;
            }

            // Check projects
            for (String project : s.getProjects()) {
                searchInText(project, "Project", s, patLower, m, occurrences, steps, comparisonsRef(comparisons));
                comparisons = lastComparisons;
            }
        }

        long duration = System.nanoTime() - start;

        steps.add(String.format("Scan finished. Located %d match occurrences across records.", occurrences.size()));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Naive String Matching");
        result.setCategory("String Matching");
        result.setPurpose("Locate occurrences of substring without pre-processing");
        result.setInputSize(students.size());
        result.setDataStructureUsed("Character Array / String buffers");
        result.setOutput(String.format("%d total matches found", occurrences.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(comparisons);
        result.setIterations(iterations);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("Best: O(n) | Avg: O(n × m) | Worst: O(n × m)");
        result.setSpaceComplexity("O(1)");
        result.setExplanation(String.format("Scanned text by sliding pattern window character by character. Recorded %d matches.", occurrences.size()));
        result.setSteps(steps);

        return result;
    }

    private static long lastComparisons = 0;
    private static long comparisonsRef(long cmp) {
        lastComparisons = cmp;
        return cmp;
    }

    private static void searchInText(String text, String field, Student s, String patLower, int m,
                                     List<StringMatchOccurrence> occurrences, List<String> steps, long cmp) {
        if (text == null) return;
        String txtLower = text.toLowerCase();
        int n = txtLower.length();

        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                lastComparisons++;
                if (txtLower.charAt(i + j) != patLower.charAt(j)) {
                    break;
                }
            }
            if (j == m) {
                occurrences.add(new StringMatchOccurrence(s, field, i));
                if (occurrences.size() <= 5) {
                    steps.add(String.format("Match verified in student '%s' [%s] at offset %d.", s.getName(), field, i));
                }
            }
        }
    }

    private static AlgorithmResult emptyResult() {
        AlgorithmResult res = new AlgorithmResult();
        res.setAlgorithmName("Naive String Matching");
        res.setCategory("String Matching");
        res.setOutput("Empty pattern provided");
        res.setTimeComplexity("O(1)");
        res.setSpaceComplexity("O(1)");
        return res;
    }
}
