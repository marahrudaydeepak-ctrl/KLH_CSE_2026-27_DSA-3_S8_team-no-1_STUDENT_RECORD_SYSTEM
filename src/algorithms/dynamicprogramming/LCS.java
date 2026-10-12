package algorithms.dynamicprogramming;

import model.AlgorithmResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Longest Common Subsequence (LCS) using Dynamic Programming.
 * Application: Compares curriculum learning paths, skill evolution sequences,
 * and project milestones between two students.
 */
public class LCS {

    public static class LCSResult {
        public List<String> sequenceA;
        public List<String> sequenceB;
        public List<String> lcsSequence;
        public int lcsLength;
        public int[][] dpTable;

        public LCSResult(List<String> a, List<String> b) {
            this.sequenceA = a;
            this.sequenceB = b;
            this.lcsSequence = new ArrayList<>();
        }

        public String getDPTableVisual() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-12s", ""));
            for (String b : sequenceB) {
                sb.append(String.format("%-10s", b.length() > 8 ? b.substring(0, 8) : b));
            }
            sb.append("\n");

            for (int i = 0; i <= sequenceA.size(); i++) {
                String label = (i == 0) ? "Ø" : sequenceA.get(i - 1);
                sb.append(String.format("%-12s", label.length() > 10 ? label.substring(0, 10) : label));
                for (int j = 0; j <= sequenceB.size(); j++) {
                    sb.append(String.format("%-10d", dpTable[i][j]));
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    public static LCSResult compute(List<String> seqA, List<String> seqB) {
        int m = seqA.size();
        int n = seqB.size();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (seqA.get(i - 1).equalsIgnoreCase(seqB.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        LCSResult res = new LCSResult(seqA, seqB);
        res.lcsLength = dp[m][n];
        res.dpTable = dp;

        // Backtracking to extract LCS elements
        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (seqA.get(i - 1).equalsIgnoreCase(seqB.get(j - 1))) {
                res.lcsSequence.add(0, seqA.get(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return res;
    }

    public static AlgorithmResult executeLCS(String labelA, List<String> seqA, String labelB, List<String> seqB) {
        long start = System.nanoTime();
        LCSResult res = compute(seqA, seqB);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Sequence A (%s): %s", labelA, seqA));
        steps.add(String.format("Sequence B (%s): %s", labelB, seqB));
        steps.add(String.format("Constructed (%d × %d) DP table with recurrence: dp[i][j] = eq ? dp[i-1][j-1]+1 : max(top, left).",
                seqA.size() + 1, seqB.size() + 1));
        steps.add(String.format("Extracted Longest Common Subsequence of length %d: %s", res.lcsLength, res.lcsSequence));

        if (seqA.size() <= 8 && seqB.size() <= 8) {
            steps.add("DP Matrix:\n" + res.getDPTableVisual());
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Longest Common Subsequence (LCS)");
        result.setCategory("Dynamic Programming");
        result.setPurpose("Identify common sequential milestones / skill progression between student profiles");
        result.setInputSize(seqA.size() + seqB.size());
        result.setDataStructureUsed(String.format("2D DP Matrix [%d][%d]", seqA.size() + 1, seqB.size() + 1));
        result.setOutput(String.format("LCS Length: %d | Sequence: %s", res.lcsLength, res.lcsSequence));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) seqA.size() * seqB.size());
        result.setIterations((long) seqA.size() * seqB.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(m × n)");
        result.setSpaceComplexity("O(m × n)");
        result.setExplanation(String.format("Found %d common elements preserving chronological order: %s", res.lcsLength, res.lcsSequence));
        result.setSteps(steps);

        return result;
    }
}
