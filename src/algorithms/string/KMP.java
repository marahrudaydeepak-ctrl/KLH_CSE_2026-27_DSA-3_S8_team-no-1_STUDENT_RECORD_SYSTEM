package algorithms.string;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manual implementation of the Knuth-Morris-Pratt (KMP) pattern searching algorithm.
 * Constructs the Longest Proper Prefix which is also Suffix (LPS) table to achieve O(n + m) time.
 */
public class KMP {

    public static class KMPMatch {
        public Student student;
        public String field;
        public int position;

        public KMPMatch(Student student, String field, int position) {
            this.student = student;
            this.field = field;
            this.position = position;
        }

        @Override
        public String toString() {
            return String.format("[%s (ID: %d) in %s @ %d]", student.getName(), student.getStudentId(), field, position);
        }
    }

    public static int[] computeLPSArray(String pattern, long[] comparisonCounter) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            comparisonCounter[0]++;
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static AlgorithmResult search(List<Student> students, String pattern) {
        long start = System.nanoTime();
        long[] comparisons = new long[]{0};
        List<KMPMatch> matches = new ArrayList<>();
        List<String> steps = new ArrayList<>();

        if (pattern == null || pattern.isEmpty()) {
            return new AlgorithmResult("KMP", "String Matching", "Empty search pattern", 0,
                    "LPS Array", "No matches", 0, 0, 0, 0, 0, "O(n + m)", "O(m)", "Empty pattern", steps);
        }

        String patLower = pattern.toLowerCase();
        int[] lps = computeLPSArray(patLower, comparisons);

        steps.add(String.format("Preprocessed pattern: \"%s\"", pattern));
        steps.add(String.format("Constructed LPS Array: %s", Arrays.toString(lps)));

        for (Student s : students) {
            searchInField(s.getName(), "Name", s, patLower, lps, matches, comparisons, steps);
            for (String skill : s.getSkills()) {
                searchInField(skill, "Skill", s, patLower, lps, matches, comparisons, steps);
            }
            for (String proj : s.getProjects()) {
                searchInField(proj, "Project", s, patLower, lps, matches, comparisons, steps);
            }
        }

        long duration = System.nanoTime() - start;

        steps.add(String.format("Scan finished. Located %d matches with %d comparisons.", matches.size(), comparisons[0]));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Knuth-Morris-Pratt (KMP)");
        result.setCategory("String Matching");
        result.setPurpose("Linear pattern search in student profiles avoiding redundant backtrack comparisons");
        result.setInputSize(students.size());
        result.setDataStructureUsed(String.format("LPS Array [Length %d]", lps.length));
        result.setOutput(String.format("%d matches found", matches.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(comparisons[0]);
        result.setIterations(students.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("Best: O(n) | Avg: O(n + m) | Worst: O(n + m)");
        result.setSpaceComplexity(String.format("O(m) for LPS table [m=%d]", patLower.length()));
        result.setExplanation(String.format("Pattern '%s' searched using LPS failure transitions. LPS=%s.",
                pattern, Arrays.toString(lps)));
        result.setSteps(steps);

        return result;
    }

    private static void searchInField(String text, String field, Student s, String patLower, int[] lps,
                                      List<KMPMatch> matches, long[] comparisons, List<String> steps) {
        if (text == null) return;
        String txtLower = text.toLowerCase();
        int n = txtLower.length();
        int m = patLower.length();

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            comparisons[0]++;
            if (patLower.charAt(j) == txtLower.charAt(i)) {
                j++;
                i++;
            }

            if (j == m) {
                matches.add(new KMPMatch(s, field, i - j));
                if (matches.size() <= 5) {
                    steps.add(String.format("KMP found match in student '%s' [%s] at offset %d.", s.getName(), field, i - j));
                }
                j = lps[j - 1];
            } else if (i < n && patLower.charAt(j) != txtLower.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
    }
}
