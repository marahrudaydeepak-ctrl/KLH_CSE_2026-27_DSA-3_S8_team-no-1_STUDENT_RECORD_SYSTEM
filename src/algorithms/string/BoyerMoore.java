package algorithms.string;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manual implementation of the Boyer-Moore pattern matching algorithm
 * using the Bad Character Heuristic.
 */
public class BoyerMoore {

    public static class BMMatch {
        public Student student;
        public String field;
        public int position;

        public BMMatch(Student student, String field, int position) {
            this.student = student;
            this.field = field;
            this.position = position;
        }

        @Override
        public String toString() {
            return String.format("[%s (ID: %d) in %s @ %d]", student.getName(), student.getStudentId(), field, position);
        }
    }

    public static int[] buildBadCharTable(String pattern) {
        int[] badChar = new int[256];
        Arrays.fill(badChar, -1);
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (ch < 256) {
                badChar[ch] = i;
            }
        }
        return badChar;
    }

    public static AlgorithmResult search(List<Student> students, String pattern) {
        long start = System.nanoTime();
        long[] comparisons = new long[]{0};
        List<BMMatch> matches = new ArrayList<>();
        List<String> steps = new ArrayList<>();

        if (pattern == null || pattern.isEmpty()) {
            return new AlgorithmResult("Boyer-Moore", "String Matching", "Empty pattern", 0,
                    "Bad Character Table", "No matches", 0, 0, 0, 0, 0, "O(n/m)", "O(Σ)", "Empty pattern", steps);
        }

        String patLower = pattern.toLowerCase();
        int m = patLower.length();
        int[] badChar = buildBadCharTable(patLower);

        steps.add(String.format("Constructed Bad-Character heuristic table for pattern \"%s\".", pattern));
        StringBuilder sampleTable = new StringBuilder();
        for (int i = 0; i < m; i++) {
            char ch = patLower.charAt(i);
            sampleTable.append(String.format("'%c'->%d  ", ch, badChar[ch]));
        }
        steps.add("Bad-Character entries: " + sampleTable.toString().trim());

        for (Student s : students) {
            searchField(s.getName(), "Name", s, patLower, m, badChar, matches, comparisons, steps);
            for (String skill : s.getSkills()) {
                searchField(skill, "Skill", s, patLower, m, badChar, matches, comparisons, steps);
            }
            for (String proj : s.getProjects()) {
                searchField(proj, "Project", s, patLower, m, badChar, matches, comparisons, steps);
            }
        }

        long duration = System.nanoTime() - start;

        steps.add(String.format("Search concluded. Located %d matches with %d comparisons.", matches.size(), comparisons[0]));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Boyer-Moore (Bad Character)");
        result.setCategory("String Matching");
        result.setPurpose("Sub-linear average time string search scanning right-to-left");
        result.setInputSize(students.size());
        result.setDataStructureUsed("Bad Character Jump Table (256 entries)");
        result.setOutput(String.format("%d matches found", matches.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(comparisons[0]);
        result.setIterations(students.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("Best: O(n / m) | Avg: O(n / m) | Worst: O(n × m)");
        result.setSpaceComplexity("O(Σ) where Σ is alphabet size");
        result.setExplanation(String.format("Right-to-left matching with bad-character shift rules. Matches: %d.", matches.size()));
        result.setSteps(steps);

        return result;
    }

    private static void searchField(String text, String field, Student s, String patLower, int m,
                                    int[] badChar, List<BMMatch> matches, long[] comparisons, List<String> steps) {
        if (text == null) return;
        String txtLower = text.toLowerCase();
        int n = txtLower.length();

        int sShift = 0;
        while (sShift <= (n - m)) {
            int j = m - 1;

            while (j >= 0 && patLower.charAt(j) == txtLower.charAt(sShift + j)) {
                comparisons[0]++;
                j--;
            }

            if (j < 0) {
                matches.add(new BMMatch(s, field, sShift));
                if (matches.size() <= 4) {
                    steps.add(String.format("Match located in '%s' [%s] at offset %d.", s.getName(), field, sShift));
                }
                char nextChar = (sShift + m < n) ? txtLower.charAt(sShift + m) : 0;
                int badCharIdx = nextChar < 256 ? badChar[nextChar] : -1;
                sShift += (sShift + m < n) ? m - badCharIdx : 1;
            } else {
                comparisons[0]++; // For the mismatch
                char mismatchChar = txtLower.charAt(sShift + j);
                int badCharIdx = mismatchChar < 256 ? badChar[mismatchChar] : -1;
                sShift += Math.max(1, j - badCharIdx);
            }
        }
    }
}
