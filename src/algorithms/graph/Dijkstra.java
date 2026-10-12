package algorithms.graph;

import datastructures.Graph;
import datastructures.MinHeap;
import model.AlgorithmResult;
import java.util.*;

/**
 * Manual implementation of Dijkstra's Shortest Path Algorithm on the weighted Student Graph.
 * Uses a MinHeap to determine lowest affinity cost / distance between academic entities.
 */
public class Dijkstra {

    public static class PathNode implements Comparable<PathNode> {
        public String vertexId;
        public double distance;

        public PathNode(String vertexId, double distance) {
            this.vertexId = vertexId;
            this.distance = distance;
        }

        @Override
        public int compareTo(PathNode o) {
            return Double.compare(this.distance, o.distance);
        }
    }

    public static class DijkstraResult {
        public String sourceId;
        public String destinationId;
        public double shortestDistance;
        public List<String> path;
        public Map<String, Double> allDistances;
        public long pqOperations;

        public DijkstraResult(String sourceId, String destinationId) {
            this.sourceId = sourceId;
            this.destinationId = destinationId;
            this.path = new ArrayList<>();
            this.allDistances = new HashMap<>();
            this.pqOperations = 0;
        }
    }

    public static DijkstraResult findShortestPath(Graph graph, String sourceId, String destId) {
        DijkstraResult result = new DijkstraResult(sourceId, destId);
        if (!graph.getVertexMap().containsKey(sourceId)) return result;

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> settled = new HashSet<>();

        for (String v : graph.getVertexList()) {
            dist.put(v, Double.POSITIVE_INFINITY);
        }
        dist.put(sourceId, 0.0);

        MinHeap<PathNode> pq = new MinHeap<>();
        pq.insert(new PathNode(sourceId, 0.0));
        result.pqOperations++;

        while (!pq.isEmpty()) {
            PathNode curr = pq.extractMin();
            result.pqOperations++;
            String u = curr.vertexId;

            if (settled.contains(u)) continue;
            settled.add(u);

            if (destId != null && u.equals(destId)) {
                break; // Target reached
            }

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                String v = edge.targetId;
                if (!settled.contains(v)) {
                    double alt = dist.get(u) + edge.weight;
                    if (alt < dist.get(v)) {
                        dist.put(v, alt);
                        prev.put(v, u);
                        pq.insert(new PathNode(v, alt));
                        result.pqOperations++;
                    }
                }
            }
        }

        result.allDistances = dist;
        result.shortestDistance = dist.getOrDefault(destId, Double.POSITIVE_INFINITY);

        // Reconstruct path
        if (destId != null && dist.get(destId) < Double.POSITIVE_INFINITY) {
            String curr = destId;
            while (curr != null) {
                result.path.add(0, curr);
                curr = prev.get(curr);
            }
        }

        return result;
    }

    public static AlgorithmResult executeDijkstra(Graph graph, String sourceId, String destId) {
        long start = System.nanoTime();
        DijkstraResult res = findShortestPath(graph, sourceId, destId);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Initialized distances with infinity; source '%s' at distance 0.0.", sourceId));
        steps.add(String.format("Step 2: Used Min-Heap priority queue; executed %d heap operations.", res.pqOperations));

        if (res.shortestDistance < Double.POSITIVE_INFINITY) {
            steps.add(String.format("Step 3: Found shortest path to destination '%s' with total weight %.2f.",
                    destId, res.shortestDistance));
            steps.add(String.format("Step 4: Optimal Path Sequence: %s", String.join(" -> ", res.path)));
        } else {
            steps.add(String.format("Step 3: No connected path found between '%s' and '%s'.", sourceId, destId));
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Dijkstra's Algorithm");
        result.setCategory("Graph Algorithms");
        result.setPurpose("Find minimum-cost relationship path between academic entities");
        result.setInputSize(graph.getVertexCount());
        result.setDataStructureUsed("Min-Heap Priority Queue & Distance Map");
        result.setOutput(res.shortestDistance < Double.POSITIVE_INFINITY
                ? String.format("Distance: %.2f | Path: %s", res.shortestDistance, String.join(" -> ", res.path))
                : "Unreachable");
        result.setExecutionTimeNs(duration);
        result.setComparisons(res.pqOperations);
        result.setIterations(graph.getVertexCount());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O((V + E) log V)");
        result.setSpaceComplexity("O(V)");
        result.setExplanation(String.format("Computed shortest weighted path from '%s' to '%s' with %d heap ops.",
                sourceId, destId, res.pqOperations));
        result.setSteps(steps);

        return result;
    }
}
