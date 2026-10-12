package algorithms.dynamicprogramming;

import model.AlgorithmResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manual implementation of Travelling Salesperson Problem (TSP) using Bitmask Dynamic Programming
 * (Held-Karp Algorithm).
 * Application: Finds the shortest travel path for a student visiting campus academic facilities:
 * Department, Library, Laboratory, Faculty Office, Examination Cell.
 */
public class TSPBitmask {

    public static final String[] CAMPUS_LOCATIONS = {
            "Department Office",
            "Central Library",
            "Advanced Computer Lab",
            "Faculty Chambers",
            "Examination Cell"
    };

    public static final int[][] DEFAULT_CAMPUS_DISTANCES = {
            {0, 10, 15, 20, 25},
            {10, 0, 35, 25, 30},
            {15, 35, 0, 30, 10},
            {20, 25, 30, 0, 15},
            {25, 30, 10, 15, 0}
    };

    public static class TSPResult {
        public int minCost;
        public List<String> route;
        public int[][] dp;
        public int numLocations;

        public TSPResult() {
            this.route = new ArrayList<>();
        }
    }

    public static TSPResult solve(int[][] dist, String[] locations) {
        int n = locations.length;
        int totalStates = 1 << n;
        int[][] dp = new int[totalStates][n];
        int[][] parent = new int[totalStates][n];

        for (int[] row : dp) {
            Arrays.fill(row, 1_000_000_000);
        }
        for (int[] row : parent) {
            Arrays.fill(row, -1);
        }

        // Starting at vertex 0 (Department)
        dp[1][0] = 0;

        for (int mask = 1; mask < totalStates; mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0 || dp[mask][u] >= 1_000_000_000) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0) {
                        int nextMask = mask | (1 << v);
                        int newCost = dp[mask][u] + dist[u][v];
                        if (newCost < dp[nextMask][v]) {
                            dp[nextMask][v] = newCost;
                            parent[nextMask][v] = u;
                        }
                    }
                }
            }
        }

        // Return to start vertex 0
        int finalMask = totalStates - 1;
        int minCost = 1_000_000_000;
        int lastNode = -1;

        for (int i = 1; i < n; i++) {
            int cost = dp[finalMask][i] + dist[i][0];
            if (cost < minCost) {
                minCost = cost;
                lastNode = i;
            }
        }

        // Reconstruct path
        TSPResult res = new TSPResult();
        res.minCost = minCost;
        res.dp = dp;
        res.numLocations = n;

        List<Integer> path = new ArrayList<>();
        int currMask = finalMask;
        int currNode = lastNode;

        while (currNode != -1) {
            path.add(0, currNode);
            int p = parent[currMask][currNode];
            currMask ^= (1 << currNode);
            currNode = p;
        }

        res.route.add(locations[0]);
        for (int idx : path) {
            res.route.add(locations[idx]);
        }
        res.route.add(locations[0]); // Return to base

        return res;
    }

    public static AlgorithmResult executeCampusTour(int[][] customDistances, String[] customLocations) {
        long start = System.nanoTime();
        String[] locations = customLocations != null ? customLocations : CAMPUS_LOCATIONS;
        int[][] dist = customDistances != null ? customDistances : DEFAULT_CAMPUS_DISTANCES;

        TSPResult res = solve(dist, locations);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Locations to visit (%d nodes): %s",
                locations.length, Arrays.toString(locations)));
        steps.add(String.format("Step 2: Allocated Bitmask DP table of size 2^%d × %d = %d states.",
                locations.length, locations.length, (1 << locations.length) * locations.length));
        steps.add("Step 3: Recurrence: dp[mask | (1<<v)][v] = min_u { dp[mask][u] + dist[u][v] }.");
        steps.add(String.format("Step 4: Optimal round-trip cost found: %d meters / minutes.", res.minCost));
        steps.add(String.format("Step 5: Final efficient route sequence: %s", String.join(" -> ", res.route)));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Bitmask DP (Campus Tour TSP)");
        result.setCategory("Dynamic Programming");
        result.setPurpose("Calculate minimum distance/time route visiting all critical academic administrative hubs");
        result.setInputSize(locations.length);
        result.setDataStructureUsed(String.format("Bitmask DP State Table [2^%d][%d]", locations.length, locations.length));
        result.setOutput(String.format("Min Cost: %d | Route: %s", res.minCost, String.join(" -> ", res.route)));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) (1 << locations.length) * locations.length * locations.length);
        result.setIterations((long) (1 << locations.length) * locations.length);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(n² × 2ⁿ)");
        result.setSpaceComplexity("O(n × 2ⁿ)");
        result.setExplanation(String.format("Explored exponential state space through bitmask memoization. Optimal visiting sequence: %s.", String.join(" -> ", res.route)));
        result.setSteps(steps);

        return result;
    }
}
