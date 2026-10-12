package algorithms.graph;

import datastructures.CustomQueue;
import datastructures.Graph;
import model.AlgorithmResult;
import java.util.*;

/**
 * Manual implementation of Breadth-First Search (BFS) on the Student Relationship Graph.
 * Discovers friendship, collaborative, and course-sharing networks level by level.
 */
public class BFS {

    public static class BFSResult {
        public String startNode;
        public List<String> traversalOrder;
        public Map<String, Integer> distances; // Levels from start
        public List<String> visitedOrder;

        public BFSResult(String startNode) {
            this.startNode = startNode;
            this.traversalOrder = new ArrayList<>();
            this.distances = new HashMap<>();
            this.visitedOrder = new ArrayList<>();
        }
    }

    public static BFSResult traverse(Graph graph, String startVertexId, int maxLevel) {
        BFSResult result = new BFSResult(startVertexId);
        if (!graph.getVertexMap().containsKey(startVertexId)) return result;

        Set<String> visited = new HashSet<>();
        CustomQueue<String> queue = new CustomQueue<>();

        visited.add(startVertexId);
        queue.enqueue(startVertexId);
        result.distances.put(startVertexId, 0);

        while (!queue.isEmpty()) {
            String u = queue.dequeue();
            result.traversalOrder.add(u);
            result.visitedOrder.add(u);
            int currentLevel = result.distances.get(u);

            if (maxLevel > 0 && currentLevel >= maxLevel) {
                continue;
            }

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                String v = edge.targetId;
                if (!visited.contains(v)) {
                    visited.add(v);
                    result.distances.put(v, currentLevel + 1);
                    queue.enqueue(v);
                }
            }
        }

        return result;
    }

    public static AlgorithmResult executeBFS(Graph graph, String startId, int maxHops) {
        long start = System.nanoTime();
        BFSResult res = traverse(graph, startId, maxHops);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Starting BFS from node '%s' [%s].",
                startId, graph.getVertex(startId) != null ? graph.getVertex(startId).label : "N/A"));
        steps.add("Step 2: Initialized FIFO Queue and Visited Set.");
        steps.add(String.format("Step 3: Traversed %d reachable academic entities within %d hops.",
                res.traversalOrder.size(), maxHops));

        int stepNum = 4;
        for (String node : res.traversalOrder) {
            if (stepNum <= 8) {
                int level = res.distances.get(node);
                Graph.Vertex v = graph.getVertex(node);
                steps.add(String.format("Step %d: Visited '%s' (Level %d) - %s",
                        stepNum++, node, level, v != null ? v.label : ""));
            }
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Breadth-First Search (BFS)");
        result.setCategory("Graph Algorithms");
        result.setPurpose(String.format("Explore academic relationship connections within %d degrees of separation", maxHops));
        result.setInputSize(graph.getVertexCount());
        result.setDataStructureUsed("Adjacency List + CustomQueue");
        result.setOutput(String.format("Discovered %d nodes in network: %s",
                res.traversalOrder.size(), res.traversalOrder));
        result.setExecutionTimeNs(duration);
        result.setComparisons(graph.getEdgeCount());
        result.setIterations(res.traversalOrder.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(V + E)");
        result.setSpaceComplexity("O(V)");
        result.setExplanation(String.format("Traversed level-by-level starting at '%s'. Discovered %d connected entities.", startId, res.traversalOrder.size()));
        result.setSteps(steps);

        return result;
    }
}
