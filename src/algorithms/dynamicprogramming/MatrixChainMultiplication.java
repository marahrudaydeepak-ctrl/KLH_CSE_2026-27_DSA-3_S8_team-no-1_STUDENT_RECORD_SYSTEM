package algorithms.dynamicprogramming;

import model.AlgorithmResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual implementation of Matrix Chain Multiplication using Dynamic Programming.
 * Application: Optimizes chained matrix transformations for student performance metrics
 * (e.g., student-course, course-assessment, and assessment-credit linear transformations).
 */
public class MatrixChainMultiplication {

    public static class MCMResult {
        public int[] dimensions;
        public int numMatrices;
        public int minMultiplications;
        public int[][] mTable;
        public int[][] sTable;
        public String parenthesization;
    }

    public static MCMResult solve(int[] p) {
        int n = p.length - 1; // number of matrices
        int[][] m = new int[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        // L is chain length
        for (int L = 2; L <= n; L++) {
            for (int i = 1; i <= n - L + 1; i++) {
                int j = i + L - 1;
                m[i][j] = Integer.MAX_VALUE;
                for (int k = i; k <= j - 1; k++) {
                    int q = m[i][k] + m[k + 1][j] + p[i - 1] * p[k] * p[j];
                    if (q < m[i][j]) {
                        m[i][j] = q;
                        s[i][j] = k;
                    }
                }
            }
        }

        MCMResult res = new MCMResult();
        res.dimensions = p;
        res.numMatrices = n;
        res.minMultiplications = m[1][n];
        res.mTable = m;
        res.sTable = s;
        res.parenthesization = buildParenthesization(s, 1, n);

        return res;
    }

    private static String buildParenthesization(int[][] s, int i, int j) {
        if (i == j) {
            return "A" + i;
        } else {
            return "(" + buildParenthesization(s, i, s[i][j]) + " × " + buildParenthesization(s, s[i][j] + 1, j) + ")";
        }
    }

    public static AlgorithmResult executeMCM(int[] dimensions) {
        long start = System.nanoTime();
        MCMResult res = solve(dimensions);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Matrices count: %d with dimension array %s",
                res.numMatrices, java.util.Arrays.toString(dimensions)));
        steps.add("Constructed cost table m[i][j] and split index table s[i][j].");
        steps.add("Recurrence: m[i][j] = min_{i<=k<j} { m[i][k] + m[k+1][j] + p[i-1]*p[k]*p[j] }.");
        steps.add(String.format("Minimum scalar multiplications required: %,d operations.", res.minMultiplications));
        steps.add(String.format("Optimal Parenthesization: %s", res.parenthesization));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Matrix Chain Multiplication (MCM)");
        result.setCategory("Dynamic Programming");
        result.setPurpose("Optimize computational complexity of chained student analytical transformations");
        result.setInputSize(res.numMatrices);
        result.setDataStructureUsed(String.format("Cost Matrix [%d][%d] & Split Matrix [%d][%d]",
                res.numMatrices + 1, res.numMatrices + 1, res.numMatrices + 1, res.numMatrices + 1));
        result.setOutput(String.format("Min Multiplications: %,d | Order: %s", res.minMultiplications, res.parenthesization));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) res.numMatrices * res.numMatrices * res.numMatrices / 6);
        result.setIterations((long) res.numMatrices * res.numMatrices);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(n³)");
        result.setSpaceComplexity("O(n²)");
        result.setExplanation(String.format("Determined optimal matrix multiplication grouping to minimize scalar arithmetic. Result: %s.", res.parenthesization));
        result.setSteps(steps);

        return result;
    }
}
