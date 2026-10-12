package datastructures;

import java.io.Serializable;
import java.util.*;

/**
 * Custom Academic Relationship Graph maintaining both Adjacency List
 * and Adjacency Matrix representations.
 * Vertices: Students, Courses, Skills, Projects, Subjects.
 * Algorithms: BFS, DFS, Dijkstra, Connected Components, Bipartite verification.
 */
public class Graph implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum VertexType {
        STUDENT, COURSE, SUBJECT, SKILL, PROJECT
    }

    public static class Vertex implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id;
        public String label;
        public VertexType type;

        public Vertex(String id, String label, VertexType type) {
            this.id = id;
            this.label = label;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Vertex)) return false;
            Vertex vertex = (Vertex) o;
            return Objects.equals(id, vertex.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return String.format("[%s: %s (%s)]", type, label, id);
        }
    }

    public static class Edge implements Serializable {
        private static final long serialVersionUID = 1L;
        public String targetId;
        public double weight;
        public String relationship;

        public Edge(String targetId, double weight, String relationship) {
            this.targetId = targetId;
            this.weight = weight;
            this.relationship = relationship;
        }

        @Override
        public String toString() {
            return String.format("-(%s: %.1f)-> %s", relationship, weight, targetId);
        }
    }

    private Map<String, Vertex> vertexMap;
    private List<String> vertexList; // Index to ID for matrix
    private Map<String, List<Edge>> adjacencyList;
    private double[][] adjacencyMatrix;
    private int maxMatrixCapacity;

    public Graph() {
        this(100);
    }

    public Graph(int initialCapacity) {
        this.vertexMap = new HashMap<>();
        this.vertexList = new ArrayList<>();
        this.adjacencyList = new HashMap<>();
        this.maxMatrixCapacity = initialCapacity;
        this.adjacencyMatrix = new double[initialCapacity][initialCapacity];
    }

    public void addVertex(String id, String label, VertexType type) {
        if (!vertexMap.containsKey(id)) {
            Vertex v = new Vertex(id, label, type);
            vertexMap.put(id, v);
            vertexList.add(id);
            adjacencyList.put(id, new ArrayList<>());

            if (vertexList.size() > maxMatrixCapacity) {
                expandMatrix();
            }
        }
    }

    private void expandMatrix() {
        int newCap = maxMatrixCapacity * 2;
        double[][] newMatrix = new double[newCap][newCap];
        for (int i = 0; i < maxMatrixCapacity; i++) {
            System.arraycopy(adjacencyMatrix[i], 0, newMatrix[i], 0, maxMatrixCapacity);
        }
        adjacencyMatrix = newMatrix;
        maxMatrixCapacity = newCap;
    }

    public void addEdge(String fromId, String toId, double weight, String relationship, boolean undirected) {
        if (!vertexMap.containsKey(fromId) || !vertexMap.containsKey(toId)) return;

        adjacencyList.get(fromId).add(new Edge(toId, weight, relationship));
        int u = vertexList.indexOf(fromId);
        int v = vertexList.indexOf(toId);
        adjacencyMatrix[u][v] = weight;

        if (undirected) {
            adjacencyList.get(toId).add(new Edge(fromId, weight, relationship));
            adjacencyMatrix[v][u] = weight;
        }
    }

    public Vertex getVertex(String id) {
        return vertexMap.get(id);
    }

    public List<Edge> getNeighbors(String id) {
        return adjacencyList.getOrDefault(id, Collections.emptyList());
    }

    public Map<String, Vertex> getVertexMap() { return vertexMap; }
    public List<String> getVertexList() { return vertexList; }
    public Map<String, List<Edge>> getAdjacencyList() { return adjacencyList; }
    public double[][] getAdjacencyMatrix() { return adjacencyMatrix; }

    public int getVertexCount() { return vertexMap.size(); }
    public int getEdgeCount() {
        int count = 0;
        for (List<Edge> edges : adjacencyList.values()) {
            count += edges.size();
        }
        return count;
    }

    /**
     * Checks if the academic relationship subgraph is bipartite (2-colorable).
     */
    public boolean isBipartite() {
        Map<String, Integer> color = new HashMap<>();
        for (String v : vertexList) {
            color.put(v, -1);
        }

        for (String start : vertexList) {
            if (color.get(start) == -1) {
                CustomQueue<String> q = new CustomQueue<>();
                q.enqueue(start);
                color.put(start, 0);

                while (!q.isEmpty()) {
                    String u = q.dequeue();
                    for (Edge edge : adjacencyList.get(u)) {
                        String v = edge.targetId;
                        if (color.get(v) == -1) {
                            color.put(v, 1 - color.get(u));
                            q.enqueue(v);
                        } else if (Objects.equals(color.get(v), color.get(u))) {
                            return false; // Same color neighbor detected
                        }
                    }
                }
            }
        }
        return true;
    }
}
