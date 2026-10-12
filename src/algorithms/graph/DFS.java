package algorithms.graph;

import datastructures.Graph;
import model.AlgorithmResult;
import java.util.*;

/**
 * Manual implementation of Depth-First Search (DFS) on the Student Relationship Graph.
 * Identifies connected academic clusters, student collaborative groups, and deep dependency chains.
 */
public class DFS {

    public static class DFSResult {
        public List<String> traversalOrder;
        public List<List<String>> connectedComponents;
        public long recursiveCalls;

        public DFSResult() {
            this.traversalOrder = new ArrayList<>();
            this.connectedComponents = new ArrayList<>();
            this.recursiveCalls = 0;
        }
    }

    public static DFSResult traverseAll(Graph graph) {
        DFSResult result = new DFSResult();
        Set<String> visited = new HashSet<>();

        for (String vId : graph.getVertexList()) {
            if (!visited.contains(vId)) {
                List<String> component = new ArrayList<>();
                dfsRec(graph, vId, visited, component, result);
                result.connectedComponents.add(component);
            }
        }

        return result;
    }

    private static void dfsRec(Graph graph, String u, Set<String> visited,
                               List<String> component, DFSResult result) {
        result.recursiveCalls++;
        visited.add(u);
        component.add(u);
        result.traversalOrder.add(u);

        for (Graph.Edge edge : graph.getNeighbors(u)) {
            String v = edge.targetId;
            if (!visited.contains(v)) {
                dfsRec(graph, v, visited, component, result);
            }
        }
    }

    public static AlgorithmResult executeDFS(Graph graph) {
        long start = System.nanoTime();
        DFSResult res = traverseAll(graph);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Starting full DFS exploration across %d graph vertices.", graph.getVertexCount()));
        steps.add(String.format("Step 2: Identified %d disjoint connected components (academic communities).",
                res.connectedComponents.size()));

        for (int i = 0; i < Math.min(5, res.connectedComponents.size()); i++) {
            List<String> comp = res.connectedComponents.get(i);
            steps.add(String.format("Component #%d (%d members): %s", i + 1, comp.size(), comp));
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Depth-First Search (DFS)");
        result.setCategory("Graph Algorithms");
        result.setPurpose("Identify connected academic study cohorts and collaborative groups");
        result.setInputSize(graph.getVertexCount());
        result.setDataStructureUsed("Adjacency List + Recursive Call Stack");
        result.setOutput(String.format("%d Connected Academic Clusters Discovered", res.connectedComponents.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(graph.getEdgeCount());
        result.setIterations(graph.getVertexCount());
        result.setSwaps(0);
        result.setRecursionCount(res.recursiveCalls);
        result.setTimeComplexity("O(V + E)");
        result.setSpaceComplexity("O(V) Recursion Stack");
        result.setExplanation(String.format("DFS partitioned graph into %d connected groups using %d recursive calls.",
                res.connectedComponents.size(), res.recursiveCalls));
        result.setSteps(steps);

        return result;
    }
}
