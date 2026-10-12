package algorithms.dynamicprogramming;

import model.AlgorithmResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of the Subset Sum Problem using Dynamic Programming.
 * Application: Evaluates whether a combination of module assessment scores or credits
 * can precisely sum to a target academic benchmark or passing boundary.
 */
public class SubsetSum {

    public static class SubsetSumResult {
        public boolean possible;
        public int target;
        public List<Integer> selectedScores;
        public boolean[][] dpTable;
        public int[] scores;

        public SubsetSumResult(int target, int[] scores) {
            this.target = target;
            this.scores = scores;
            this.selectedScores = new ArrayList<>();
        }
    }

    public static SubsetSumResult solve(int[] scores, int target) {
        int n = scores.length;
        boolean[][] dp = new boolean[n + 1][target + 1];

        // Base case: 0 target sum is always possible with empty set
        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }

        for (int i = 1; i <= n; i++) {
            for (int s = 1; s <= target; s++) {
                if (scores[i - 1] <= s) {
                    dp[i][s] = dp[i - 1][s - scores[i - 1]] || dp[i - 1][s];
                } else {
                    dp[i][s] = dp[i - 1][s];
                }
            }
        }

        SubsetSumResult res = new SubsetSumResult(target, scores);
        res.possible = dp[n][target];
        res.dpTable = dp;

        if (res.possible) {
            int curr = target;
            for (int i = n; i > 0 && curr > 0; i--) {
                if (!dp[i - 1][curr]) {
                    res.selectedScores.add(scores[i - 1]);
                    curr -= scores[i - 1];
                }
            }
        }

        return res;
    }

    public static AlgorithmResult executeSubsetSum(int[] assessmentScores, int target) {
        long start = System.nanoTime();
        SubsetSumResult res = solve(assessmentScores, target);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Target assessment score: %d | Available component scores: %d",
                target, assessmentScores.length));
        steps.add("Step 2: Constructed boolean DP table dp[n+1][target+1].");
        steps.add("Step 3: Recurrence: dp[i][s] = dp[i-1][s - score[i-1]] || dp[i-1][s].");

        if (res.possible) {
            steps.add(String.format("Step 4: Target %d achievable! Selected scores: %s", target, res.selectedScores));
        } else {
            steps.add(String.format("Step 4: Target %d cannot be formed by any subset.", target));
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Subset Sum (DP)");
        result.setCategory("Dynamic Programming");
        result.setPurpose("Identify exact assessment score combination reaching target threshold");
        result.setInputSize(assessmentScores.length);
        result.setDataStructureUsed(String.format("Boolean DP Matrix [%d][%d]", assessmentScores.length + 1, target + 1));
        result.setOutput(res.possible ? String.format("Possible! Subset: %s", res.selectedScores) : "Not Possible");
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) assessmentScores.length * target);
        result.setIterations((long) assessmentScores.length * target);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(n × Target) Pseudo-polynomial");
        result.setSpaceComplexity("O(n × Target)");
        result.setExplanation(res.possible
                ? String.format("Found valid subset summing to %d: %s", target, res.selectedScores)
                : String.format("No combination of the provided %d scores sums to %d.", assessmentScores.length, target));
        result.setSteps(steps);

        return result;
    }
}
