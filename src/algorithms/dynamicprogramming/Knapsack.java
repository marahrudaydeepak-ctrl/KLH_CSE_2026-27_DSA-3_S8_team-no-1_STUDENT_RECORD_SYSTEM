package algorithms.dynamicprogramming;

import model.AlgorithmResult;
import model.Project;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of 0/1 Knapsack using Dynamic Programming.
 * Application: Optimizes student project / academic resource selection
 * to maximize total academic value within a student's constrained study hours.
 */
public class Knapsack {

    public static class KnapsackResult {
        public List<Project> selectedProjects;
        public int totalTimeSpent;
        public int maxAcademicValue;
        public int[][] dpTable;
        public String recurrence;

        public KnapsackResult() {
            this.selectedProjects = new ArrayList<>();
        }
    }

    public static KnapsackResult solve(List<Project> availableProjects, int maxAvailableHours) {
        int n = availableProjects.size();
        int W = maxAvailableHours;
        int[][] dp = new int[n + 1][W + 1];

        // Fill DP table
        for (int i = 1; i <= n; i++) {
            Project proj = availableProjects.get(i - 1);
            int wt = proj.getTimeRequiredHours();
            int val = proj.getAcademicValue();

            for (int w = 0; w <= W; w++) {
                if (wt <= w) {
                    dp[i][w] = Math.max(val + dp[i - 1][w - wt], dp[i - 1][w]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Backtrack to find selected projects
        KnapsackResult res = new KnapsackResult();
        res.maxAcademicValue = dp[n][W];
        res.dpTable = dp;
        res.recurrence = "dp[i][w] = max(val[i] + dp[i-1][w - wt[i]], dp[i-1][w])";

        int currW = W;
        for (int i = n; i > 0 && currW > 0; i--) {
            if (dp[i][currW] != dp[i - 1][currW]) {
                Project selected = availableProjects.get(i - 1);
                res.selectedProjects.add(selected);
                res.totalTimeSpent += selected.getTimeRequiredHours();
                currW -= selected.getTimeRequiredHours();
            }
        }

        return res;
    }

    public static AlgorithmResult executeKnapsack(List<Project> projects, int maxHours) {
        long start = System.nanoTime();
        KnapsackResult kr = solve(projects, maxHours);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Input %d projects with budget of %d hours.", projects.size(), maxHours));
        steps.add("Step 2: Constructed (n+1) × (W+1) DP matrix.");
        steps.add("Step 3: Applied recurrence: " + kr.recurrence);
        steps.add(String.format("Step 4: Backtracked optimal subset achieving %d value points with %d hours spent.",
                kr.maxAcademicValue, kr.totalTimeSpent));
        for (Project p : kr.selectedProjects) {
            steps.add(String.format("  Selected: [%s] %s (Time: %dh, Value: %d)",
                    p.getProjectId(), p.getTitle(), p.getTimeRequiredHours(), p.getAcademicValue()));
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("0/1 Knapsack (DP)");
        result.setCategory("Dynamic Programming");
        result.setPurpose("Select optimal academic project portfolio under time constraints");
        result.setInputSize(projects.size());
        result.setDataStructureUsed(String.format("2D DP Matrix [%d][%d]", projects.size() + 1, maxHours + 1));
        result.setOutput(String.format("Max Value: %d pts | Time: %d/%dh | %d Projects Selected",
                kr.maxAcademicValue, kr.totalTimeSpent, maxHours, kr.selectedProjects.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) projects.size() * maxHours);
        result.setIterations((long) projects.size() * maxHours);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(n × W) Pseudo-polynomial");
        result.setSpaceComplexity("O(n × W)");
        result.setExplanation(String.format("Optimal sub-structure solved using bottom-up tabulation. Max value obtained: %d points.", kr.maxAcademicValue));
        result.setSteps(steps);

        return result;
    }
}
