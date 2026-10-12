package algorithms.dynamicprogramming;

import model.AlgorithmResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manual implementation of Optimal Binary Search Tree (OBST) using Dynamic Programming.
 * Application: Minimizes the expected search time for frequently looked-up student records
 * based on search frequency probabilities.
 */
public class OptimalBST {

    public static class OBSTResult {
        public long[] keys;
        public double[] frequencies;
        public double minExpectedCost;
        public int[][] rootTable;
        public double[][] costTable;
        public String treeStructure;
    }

    public static OBSTResult solve(long[] keys, double[] freq) {
        int n = keys.length;
        double[][] e = new double[n + 2][n + 2];
        double[][] w = new double[n + 2][n + 2];
        int[][] root = new int[n + 1][n + 1];

        for (int i = 1; i <= n + 1; i++) {
            e[i][i - 1] = 0;
            w[i][i - 1] = 0;
        }

        for (int l = 1; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                e[i][j] = Double.MAX_VALUE;
                w[i][j] = w[i][j - 1] + freq[j - 1];

                for (int r = i; r <= j; r++) {
                    double t = e[i][r - 1] + e[r + 1][j] + w[i][j];
                    if (t < e[i][j]) {
                        e[i][j] = t;
                        root[i][j] = r;
                    }
                }
            }
        }

        OBSTResult res = new OBSTResult();
        res.keys = keys;
        res.frequencies = freq;
        res.minExpectedCost = e[1][n];
        res.rootTable = root;
        res.costTable = e;
        res.treeStructure = buildTreeVisual(keys, root, 1, n, -1, "Root");

        return res;
    }

    private static String buildTreeVisual(long[] keys, int[][] root, int i, int j, long parentKey, String rel) {
        if (i > j) return "";
        int r = root[i][j];
        long currKey = keys[r - 1];
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Key %d (%s of %s)\n",
                currKey, rel, parentKey == -1 ? "N/A" : String.valueOf(parentKey)));
        sb.append(buildTreeVisual(keys, root, i, r - 1, currKey, "Left Child"));
        sb.append(buildTreeVisual(keys, root, r + 1, j, currKey, "Right Child"));
        return sb.toString();
    }

    public static AlgorithmResult executeOptimalBST(long[] studentIds, double[] searchFreqs) {
        long start = System.nanoTime();
        OBSTResult res = solve(studentIds, searchFreqs);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Keys (Student IDs): %s", Arrays.toString(studentIds)));
        steps.add(String.format("Search Frequencies: %s", Arrays.toString(searchFreqs)));
        steps.add("Constructed dynamic programming cost table e[i][j] and root choice matrix.");
        steps.add(String.format("Minimum expected search cost = %.4f comparisons per lookup.", res.minExpectedCost));
        steps.add("Optimal Tree Topology:\n" + res.treeStructure.trim());

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Optimal Binary Search Tree (OBST)");
        result.setCategory("Dynamic Programming");
        result.setPurpose("Minimize weighted search cost for non-uniformly accessed student IDs");
        result.setInputSize(studentIds.length);
        result.setDataStructureUsed("2D DP Cost & Root Tables");
        result.setOutput(String.format("Expected Cost: %.4f | Optimal Root: ID %d", res.minExpectedCost, studentIds[res.rootTable[1][studentIds.length] - 1]));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) studentIds.length * studentIds.length * studentIds.length / 6);
        result.setIterations((long) studentIds.length * studentIds.length);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(n³)");
        result.setSpaceComplexity("O(n²)");
        result.setExplanation(String.format("Arranged high-frequency student IDs closer to root to minimize search depth. Expected cost: %.4f.", res.minExpectedCost));
        result.setSteps(steps);

        return result;
    }
}
