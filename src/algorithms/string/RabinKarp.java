package algorithms.string;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of the Rabin-Karp pattern searching algorithm using rolling hash.
 * Tracks pattern hash, window hashes, and hash collisions (spurious hits).
 */
public class RabinKarp {

    private static final int PRIME = 101;
    private static final int BASE = 256;

    public static class RabinKarpMatch {
        public Student student;
        public String field;
        public int position;

        public RabinKarpMatch(Student student, String field, int position) {
            this.student = student;
            this.field = field;
            this.position = position;
        }

        @Override
        public String toString() {
            return String.format("[%s (ID: %d) in %s @ %d]", student.getName(), student.getStudentId(), field, position);
        }
    }

    public static AlgorithmResult search(List<Student> students, String pattern) {
        long start = System.nanoTime();
        long comparisons = 0;
        long hashCollisions = 0;
        List<RabinKarpMatch> matches = new ArrayList<>();
        List<String> steps = new ArrayList<>();

        if (pattern == null || pattern.isEmpty()) {
            return new AlgorithmResult("Rabin-Karp", "String Matching", "Empty search pattern", 0,
                    "Rolling Hash", "No matches", 0, 0, 0, 0, 0, "O(n + m)", "O(1)", "Empty pattern", steps);
        }

        String patLower = pattern.toLowerCase();
        int m = patLower.length();

        // Calculate h = (BASE^(m-1)) % PRIME
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % PRIME;
        }

        // Calculate hash of pattern
        long patternHash = 0;
        for (int i = 0; i < m; i++) {
            patternHash = (BASE * patternHash + patLower.charAt(i)) % PRIME;
        }

        steps.add(String.format("Calculated Pattern Rolling Hash: %d (Modulo Prime: %d, Base: %d).",
                patternHash, PRIME, BASE));

        for (Student s : students) {
            long[] stats = searchField(s.getName(), "Name", s, patLower, m, h, patternHash, matches, steps);
            comparisons += stats[0];
            hashCollisions += stats[1];

            for (String skill : s.getSkills()) {
                stats = searchField(skill, "Skill", s, patLower, m, h, patternHash, matches, steps);
                comparisons += stats[0];
                hashCollisions += stats[1];
            }

            for (String proj : s.getProjects()) {
                stats = searchField(proj, "Project", s, patLower, m, h, patternHash, matches, steps);
                comparisons += stats[0];
                hashCollisions += stats[1];
            }
        }

        long duration = System.nanoTime() - start;

        steps.add(String.format("Finished Rabin-Karp scan. Verified %d matches, %d spurious hash collisions.",
                matches.size(), hashCollisions));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Rabin-Karp Algorithm");
        result.setCategory("String Matching");
        result.setPurpose("Rolling-hash substring search across student names, skills, and projects");
        result.setInputSize(students.size());
        result.setDataStructureUsed(String.format("Rolling Hash Window (Pattern Hash: %d)", patternHash));
        result.setOutput(String.format("%d matches found", matches.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(comparisons);
        result.setIterations(students.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("Best: O(n + m) | Avg: O(n + m) | Worst: O(n × m)");
        result.setSpaceComplexity("O(1)");
        result.setExplanation(String.format("Rolling hash computed in O(1) per step. Spurious hash collisions encountered: %d.", hashCollisions));
        result.setSteps(steps);

        return result;
    }

    private static long[] searchField(String text, String field, Student s, String patLower, int m,
                                      long h, long patternHash, List<RabinKarpMatch> matches, List<String> steps) {
        long cmp = 0;
        long coll = 0;
        if (text == null) return new long[]{cmp, coll};

        String txtLower = text.toLowerCase();
        int n = txtLower.length();
        if (n < m) return new long[]{cmp, coll};

        // Compute initial window hash
        long windowHash = 0;
        for (int i = 0; i < m; i++) {
            windowHash = (BASE * windowHash + txtLower.charAt(i)) % PRIME;
        }

        for (int i = 0; i <= n - m; i++) {
            if (patternHash == windowHash) {
                // Check characters
                boolean fullMatch = true;
                for (int j = 0; j < m; j++) {
                    cmp++;
                    if (txtLower.charAt(i + j) != patLower.charAt(j)) {
                        fullMatch = false;
                        coll++; // Spurious collision
                        break;
                    }
                }
                if (fullMatch) {
                    matches.add(new RabinKarpMatch(s, field, i));
                    if (matches.size() <= 4) {
                        steps.add(String.format("Match verified in '%s' [%s] at pos %d (Hash matched %d).",
                                s.getName(), field, i, windowHash));
                    }
                }
            }

            // Slide window hash
            if (i < n - m) {
                windowHash = (BASE * (windowHash - txtLower.charAt(i) * h) + txtLower.charAt(i + m)) % PRIME;
                if (windowHash < 0) {
                    windowHash = windowHash + PRIME;
                }
            }
        }
        return new long[]{cmp, coll};
    }
}
