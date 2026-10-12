package algorithms.string;

import model.AlgorithmResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of the Damerau-Levenshtein Distance algorithm.
 * Accounts for Insertions, Deletions, Substitutions, and Transpositions of adjacent characters.
 */
public class DamerauLevenshtein {

    public static class DLResult {
        public int distance;
        public double similarityPercentage;
        public int[][] dpMatrix;
        public String s1;
        public String s2;
    }

    public static DLResult compute(String str1, String str2) {
        String s1 = str1 != null ? str1.toLowerCase() : "";
        String s2 = str2 != null ? str2.toLowerCase() : "";

        int m = s1.length();
        int n = s2.length();
        int[][] d = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) d[i][0] = i;
        for (int j = 0; j <= n; j++) d[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                d[i][j] = Math.min(
                        d[i - 1][j] + 1, // deletion
                        Math.min(
                                d[i][j - 1] + 1, // insertion
                                d[i - 1][j - 1] + cost // substitution
                        )
                );

                // Transposition check
                if (i > 1 && j > 1 &&
                        s1.charAt(i - 1) == s2.charAt(j - 2) &&
                        s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + 1);
                }
            }
        }

        DLResult res = new DLResult();
        res.distance = d[m][n];
        res.s1 = s1;
        res.s2 = s2;
        res.dpMatrix = d;
        int maxLen = Math.max(m, n);
        res.similarityPercentage = (maxLen == 0) ? 100.0 : ((1.0 - (double) res.distance / maxLen) * 100.0);
        return res;
    }

    public static AlgorithmResult compareWithDetails(String query, String target) {
        long start = System.nanoTime();
        DLResult res = compute(query, target);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Allocated (%d × %d) DP table including transposition lookback.",
                query.length() + 1, target.length() + 1));
        steps.add("Evaluated 4 edit operations: Insertion, Deletion, Substitution, and Adjacent Transposition.");
        steps.add(String.format("Resulting Damerau-Levenshtein Distance = %d | Similarity = %.2f%%",
                res.distance, res.similarityPercentage));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Damerau-Levenshtein Distance");
        result.setCategory("Fuzzy String Matching / Dynamic Programming");
        result.setPurpose("Typo-tolerant fuzzy student matching supporting transposed characters");
        result.setInputSize(query.length() + target.length());
        result.setDataStructureUsed(String.format("2D DP Matrix [%d][%d]", query.length() + 1, target.length() + 1));
        result.setOutput(String.format("Distance: %d | Similarity: %.2f%%", res.distance, res.similarityPercentage));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) query.length() * target.length());
        result.setIterations((long) query.length() * target.length());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(m × n)");
        result.setSpaceComplexity("O(m × n)");
        result.setExplanation(String.format("Determined edit distance between '%s' and '%s' handling transpositions.", query, target));
        result.setSteps(steps);

        return result;
    }
}
