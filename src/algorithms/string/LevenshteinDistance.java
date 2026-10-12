package algorithms.string;

import model.AlgorithmResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Wagner-Fischer Dynamic Programming algorithm
 * for calculating Levenshtein Edit Distance between strings.
 * Computes operations (Insert, Delete, Substitute) and displays the DP matrix.
 */
public class LevenshteinDistance {

    public static class DistanceResult {
        public int distance;
        public double similarityPercentage;
        public int[][] dpMatrix;
        public String s1;
        public String s2;

        public String getMatrixVisual() {
            StringBuilder sb = new StringBuilder();
            sb.append("      ");
            for (int j = 0; j < s2.length(); j++) {
                sb.append(String.format(" %c ", s2.charAt(j)));
            }
            sb.append("\n");

            for (int i = 0; i <= s1.length(); i++) {
                if (i == 0) sb.append("   ");
                else sb.append(String.format(" %c ", s1.charAt(i - 1)));

                for (int j = 0; j <= s2.length(); j++) {
                    sb.append(String.format("%2d ", dpMatrix[i][j]));
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    public static DistanceResult compute(String str1, String str2) {
        String s1 = str1 != null ? str1.toLowerCase() : "";
        String s2 = str2 != null ? str2.toLowerCase() : "";

        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        dp[i - 1][j] + 1, // deletion
                        Math.min(
                                dp[i][j - 1] + 1, // insertion
                                dp[i - 1][j - 1] + cost // substitution
                        )
                );
            }
        }

        DistanceResult res = new DistanceResult();
        res.distance = dp[m][n];
        res.s1 = s1;
        res.s2 = s2;
        res.dpMatrix = dp;

        int maxLen = Math.max(m, n);
        res.similarityPercentage = (maxLen == 0) ? 100.0 : ((1.0 - (double) res.distance / maxLen) * 100.0);
        return res;
    }

    public static AlgorithmResult compareWithDetails(String query, String target) {
        long start = System.nanoTime();
        DistanceResult dr = compute(query, target);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Initialized Wagner-Fischer DP table of dimensions (%d × %d).",
                query.length() + 1, target.length() + 1));
        steps.add("Base case filled: dp[i][0]=i (deletions) and dp[0][j]=j (insertions).");
        steps.add(String.format("Computed transition: dp[i][j] = min(delete, insert, substitute)."));
        if (query.length() <= 15 && target.length() <= 15) {
            steps.add("DP Matrix:\n" + dr.getMatrixVisual());
        }
        steps.add(String.format("Final Levenshtein Distance = %d | Similarity = %.2f%%",
                dr.distance, dr.similarityPercentage));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Levenshtein Distance (Wagner-Fischer DP)");
        result.setCategory("Fuzzy String Matching / Dynamic Programming");
        result.setPurpose("Quantify minimum edit operations between student search terms");
        result.setInputSize(query.length() + target.length());
        result.setDataStructureUsed(String.format("2D DP Matrix [%d][%d]", query.length() + 1, target.length() + 1));
        result.setOutput(String.format("Distance: %d | Similarity: %.2f%%", dr.distance, dr.similarityPercentage));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) query.length() * target.length());
        result.setIterations((long) query.length() * target.length());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(m × n)");
        result.setSpaceComplexity("O(m × n)");
        result.setExplanation(String.format("Compared '%s' against '%s'. Found edit distance %d with %.2f%% similarity.",
                query, target, dr.distance, dr.similarityPercentage));
        result.setSteps(steps);

        return result;
    }
}
