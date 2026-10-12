package algorithms.networkflow;

import datastructures.CustomQueue;
import model.AlgorithmResult;
import java.util.*;

/**
 * Manual implementation of Edmonds-Karp Algorithm for Maximum Network Flow.
 * Computes shortest augmenting paths using BFS, ensuring strongly polynomial O(V × E²) complexity.
 */
public class EdmondsKarp {

    public static class EdmondsKarpResult {
        public int maxFlow;
        public int iterations;
        public List<String> augmentationSteps;
        public int[][] residualGraph;
        public List<String> indexToNode;
        public Map<String, Integer> nodeIndices;

        public EdmondsKarpResult() {
            this.augmentationSteps = new ArrayList<>();
        }
    }

    public static EdmondsKarpResult computeMaxFlow(Map<String, Map<String, Integer>> capacityMap,
                                                   String source, String sink) {
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
        int[] parent = new int[n];
        EdmondsKarpResult result = new EdmondsKarpResult();
        result.nodeIndices = nodeIndices;
        result.indexToNode = nodes;

        while (bfsAugment(rGraph, s, t, parent)) {
            iter++;
            int pathFlow = Integer.MAX_VALUE;
            List<String> pathNodes = new ArrayList<>();

            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, rGraph[u][v]);
                pathNodes.add(0, nodes.get(v));
            }
            pathNodes.add(0, nodes.get(s));

            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                rGraph[u][v] -= pathFlow;
                rGraph[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
            result.augmentationSteps.add(String.format("Phase %d (BFS): Path [%s] | Bottleneck = %d | Flow = %d",
                    iter, String.join(" -> ", pathNodes), pathFlow, maxFlow));
        }

        result.maxFlow = maxFlow;
        result.iterations = iter;
        result.residualGraph = rGraph;
        return result;
    }

    private static boolean bfsAugment(int[][] rGraph, int s, int t, int[] parent) {
        int n = rGraph.length;
        boolean[] visited = new boolean[n];
        CustomQueue<Integer> queue = new CustomQueue<>();

        queue.enqueue(s);
        visited[s] = true;
        parent[s] = -1;

        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            if (u == t) return true;

            for (int v = 0; v < n; v++) {
                if (!visited[v] && rGraph[u][v] > 0) {
                    parent[v] = u;
                    visited[v] = true;
                    queue.enqueue(v);
                }
            }
        }
        return false;
    }

    public static AlgorithmResult executeEdmondsKarp(Map<String, Map<String, Integer>> capacityMap,
                                                     String source, String sink, String purpose) {
        long start = System.nanoTime();
        EdmondsKarpResult res = computeMaxFlow(capacityMap, source, sink);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Initialized Edmonds-Karp with BFS shortest augmenting paths (Source: '%s', Sink: '%s').", source, sink));
        steps.addAll(res.augmentationSteps);
        steps.add(String.format("Converged: Maximum Flow = %d in %d BFS phases.", res.maxFlow, res.iterations));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Edmonds-Karp Algorithm");
        result.setCategory("Network Flow");
        result.setPurpose(purpose != null ? purpose : "Strongly polynomial maximum flow using BFS augmenting paths");
        result.setInputSize(res.nodeIndices.size());
        result.setDataStructureUsed("Residual Graph & CustomQueue BFS");
        result.setOutput(String.format("Max Flow: %d units (%d phases)", res.maxFlow, res.iterations));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) res.iterations * res.nodeIndices.size());
        result.setIterations(res.iterations);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(V × E²)");
        result.setSpaceComplexity("O(V²)");
        result.setExplanation(String.format("Found max flow %d using %d BFS shortest augmenting paths.", res.maxFlow, res.iterations));
        result.setSteps(steps);

        return result;
    }
}
