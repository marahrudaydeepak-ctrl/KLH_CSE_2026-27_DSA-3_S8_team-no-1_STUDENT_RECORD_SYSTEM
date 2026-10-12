package algorithms.networkflow;

import model.AlgorithmResult;
import java.util.*;

/**
 * Manual implementation of Ford-Fulkerson Algorithm for Maximum Network Flow
 * using DFS for augmenting paths.
 */
public class FordFulkerson {

    public static class FlowResult {
        public int maxFlow;
        public int iterations;
        public List<String> augmentationSteps;
        public int[][] residualGraph;
        public Map<String, Integer> nodeIndices;
        public List<String> indexToNode;

        public FlowResult() {
            this.augmentationSteps = new ArrayList<>();
        }
    }

    public static FlowResult computeMaxFlow(Map<String, Map<String, Integer>> capacityMap,
                                            String source, String sink) {
        // Collect all distinct nodes
        Set<String> allNodes = new TreeSet<>();
        allNodes.add(source);
        allNodes.add(sink);
        for (String u : capacityMap.keySet()) {
            allNodes.add(u);
            allNodes.addAll(capacityMap.get(u).keySet());
        }

        List<String> nodes = new ArrayList<>(allNodes);
        Map<String, Integer> nodeIndices = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeIndices.put(nodes.get(i), i);
        }

        int n = nodes.size();
        int s = nodeIndices.get(source);
        int t = nodeIndices.get(sink);

        int[][] rGraph = new int[n][n];
        for (String u : capacityMap.keySet()) {
            int uIdx = nodeIndices.get(u);
            for (Map.Entry<String, Integer> entry : capacityMap.get(u).entrySet()) {
                int vIdx = nodeIndices.get(entry.getKey());
                rGraph[uIdx][vIdx] = entry.getValue();
            }
        }

        int maxFlow = 0;
        int iter = 0;
        FlowResult result = new FlowResult();
        result.nodeIndices = nodeIndices;
        result.indexToNode = nodes;

        int[] parent = new int[n];

        while (dfsAugment(rGraph, s, t, new boolean[n], parent)) {
            iter++;
            // Find bottleneck capacity along DFS path
            int pathFlow = Integer.MAX_VALUE;
            List<String> pathNodes = new ArrayList<>();
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, rGraph[u][v]);
                pathNodes.add(0, nodes.get(v));
            }
            pathNodes.add(0, nodes.get(s));

            // Update residual capacities
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                rGraph[u][v] -= pathFlow;
                rGraph[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
            result.augmentationSteps.add(String.format("Iteration %d: Augmenting Path [%s] with Bottleneck Capacity = %d. Running Flow = %d",
                    iter, String.join(" -> ", pathNodes), pathFlow, maxFlow));
        }

        result.maxFlow = maxFlow;
        result.iterations = iter;
        result.residualGraph = rGraph;
        return result;
    }

    private static boolean dfsAugment(int[][] rGraph, int u, int t, boolean[] visited, int[] parent) {
        visited[u] = true;
        if (u == t) return true;

        for (int v = 0; v < rGraph.length; v++) {
            if (!visited[v] && rGraph[u][v] > 0) {
                parent[v] = u;
                if (dfsAugment(rGraph, v, t, visited, parent)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static AlgorithmResult executeFordFulkerson(Map<String, Map<String, Integer>> capacityMap,
                                                       String source, String sink, String purpose) {
        long start = System.nanoTime();
        FlowResult res = computeMaxFlow(capacityMap, source, sink);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Initialized residual network with Source='%s', Sink='%s'.", source, sink));
        steps.addAll(res.augmentationSteps);
        steps.add(String.format("Final Result: Achieved Maximum Flow = %d units across %d augmentation phases.",
                res.maxFlow, res.iterations));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Ford-Fulkerson Algorithm");
        result.setCategory("Network Flow");
        result.setPurpose(purpose != null ? purpose : "Calculate Maximum Network Flow via augmenting paths");
        result.setInputSize(res.nodeIndices.size());
        result.setDataStructureUsed("Residual Capacity Matrix & DFS Traversal");
        result.setOutput(String.format("Max Flow: %d units (%d iterations)", res.maxFlow, res.iterations));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) res.iterations * res.nodeIndices.size());
        result.setIterations(res.iterations);
        result.setSwaps(0);
        result.setRecursionCount(res.iterations * 4L);
        result.setTimeComplexity("O(E × max_flow)");
        result.setSpaceComplexity("O(V²)");
        result.setExplanation(String.format("Found max flow of %d by discovering %d augmenting paths using DFS.", res.maxFlow, res.iterations));
        result.setSteps(steps);

        return result;
    }
}
