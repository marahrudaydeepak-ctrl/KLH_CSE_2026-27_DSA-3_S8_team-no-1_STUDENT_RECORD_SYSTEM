package algorithms.string;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manual implementation of the Z-Algorithm for pattern matching.
 * Uses Pattern + '$' + Text concatenation and maintains [L, R] Z-boxes.
 */
public class ZAlgorithm {

    public static class ZMatch {
        public Student student;
        public String field;
        public int position;

        public ZMatch(Student student, String field, int position) {
            this.student = student;
            this.field = field;
            this.position = position;
        }

        @Override
        public String toString() {
            return String.format("[%s (ID: %d) in %s @ %d]", student.getName(), student.getStudentId(), field, position);
        }
    }

    public static int[] calculateZArray(String s, long[] comparisons) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i > r) {
                l = r = i;
                while (r < n && s.charAt(r - l) == s.charAt(r)) {
                    comparisons[0]++;
                    r++;
                }
                if (r < n) comparisons[0]++; // Mismatch
                z[i] = r - l;
                r--;
            } else {
                int k = i - l;
                if (z[k] < r - i + 1) {
                    z[i] = z[k];
                } else {
                    l = i;
                    while (r < n && s.charAt(r - l) == s.charAt(r)) {
                        comparisons[0]++;
                        r++;
                    }
                    if (r < n) comparisons[0]++;
                    z[i] = r - l;
                    r--;
                }
            }
        }
        return z;
    }

    public static AlgorithmResult search(List<Student> students, String pattern) {
        long start = System.nanoTime();
        long[] comparisons = new long[]{0};
        List<ZMatch> matches = new ArrayList<>();
        List<String> steps = new ArrayList<>();

        if (pattern == null || pattern.isEmpty()) {
            return new AlgorithmResult("Z Algorithm", "String Matching", "Empty search pattern", 0,
                    "Z-Array", "No matches", 0, 0, 0, 0, 0, "O(n + m)", "O(n + m)", "Empty pattern", steps);
        }

        String patLower = pattern.toLowerCase();
        int m = patLower.length();
        steps.add(String.format("Preparing Z-Algorithm for pattern: \"%s\" (Length: %d).", pattern, m));

        int sampleCount = 0;
        for (Student s : students) {
            sampleCount = searchField(s.getName(), "Name", s, patLower, m, matches, comparisons, steps, sampleCount);
            for (String skill : s.getSkills()) {
                sampleCount = searchField(skill, "Skill", s, patLower, m, matches, comparisons, steps, sampleCount);
            }
            for (String proj : s.getProjects()) {
                sampleCount = searchField(proj, "Project", s, patLower, m, matches, comparisons, steps, sampleCount);
            }
        }

        long duration = System.nanoTime() - start;

        steps.add(String.format("Scan completed. Located %d match positions with %d character comparisons.",
                matches.size(), comparisons[0]));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Z Algorithm");
        result.setCategory("String Matching");
        result.setPurpose("Linear pattern matching via longest prefix match lengths");
        result.setInputSize(students.size());
        result.setDataStructureUsed("Z-Array");
        result.setOutput(String.format("%d matches found", matches.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(comparisons[0]);
        result.setIterations(students.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("Best: O(n + m) | Avg: O(n + m) | Worst: O(n + m)");
        result.setSpaceComplexity("O(n + m) for concatenated Z-buffer");
        result.setExplanation(String.format("Constructed Z-boxes for concatenation (Pattern + '$' + Text) finding %d exact matches.", matches.size()));
        result.setSteps(steps);

        return result;
    }

    private static int searchField(String text, String field, Student s, String patLower, int m,
                                   List<ZMatch> matches, long[] comparisons, List<String> steps, int sampleCount) {
        if (text == null) return sampleCount;
        String txtLower = text.toLowerCase();
        String concat = patLower + "$" + txtLower;

        int[] z = calculateZArray(concat, comparisons);

        if (sampleCount < 2) {
            steps.add(String.format("Sample Z-Array on '%s' (First 15 values): %s",
                    text, Arrays.toString(Arrays.copyOf(z, Math.min(z.length, 15)))));
            sampleCount++;
        }

        for (int i = m + 1; i < concat.length(); i++) {
            if (z[i] == m) {
                int matchedPos = i - (m + 1);
                matches.add(new ZMatch(s, field, matchedPos));
                if (matches.size() <= 4) {
                    steps.add(String.format("Z[i] == %d match found in student '%s' [%s] at pos %d.",
                            m, s.getName(), field, matchedPos));
                }
            }
        }
        return sampleCount;
    }
}
