package ui;

import algorithms.approximation.VertexCoverApproximation;
import algorithms.dynamicprogramming.*;
import algorithms.graph.BFS;
import algorithms.graph.DFS;
import algorithms.graph.Dijkstra;
import algorithms.networkflow.BipartiteMatching;
import algorithms.networkflow.MinCut;
import algorithms.npcomplete.EducationalNPSolver;
import algorithms.parallel.ParallelAnalytics;
import algorithms.parameterized.ParameterizedSkillCover;
import algorithms.randomized.MillerRabin;
import algorithms.randomized.RandomizedHashing;
import algorithms.randomized.ReservoirSampling;
import datastructures.AVLTree;
import model.AlgorithmResult;
import model.Project;
import model.Student;
import services.StudentService;
import java.util.*;

/**
 * Controller for advanced DSA syllabus module operations:
 * Linked Lists, Hashing, BST, AVL, Graphs, Dynamic Programming,
 * Network Flow, NP-Completeness, Approximation, Parameterized, Randomized, and Parallel modules.
 */
public class AlgorithmMenu {

    private final StudentService studentService;
    private final Scanner scanner;

    public AlgorithmMenu(StudentService studentService, Scanner scanner) {
        this.studentService = studentService;
        this.scanner = scanner;
    }

    // ================= 1. LINKED LIST OPERATIONS =================
    public void handleLinkedLists() {
        System.out.println("\n====================================================");
        System.out.println("            LINKED LIST OPERATIONS DEMO             ");
        System.out.println("====================================================");
        System.out.println("1. Demonstrate Singly Linked List (Insert, Delete, Reverse, Cycle Detect)");
        System.out.println("2. Demonstrate Doubly Linked List (Bidirectional Traversal, Delete)");
        System.out.println("3. Demonstrate Circular Linked List (Circular Traversal)");
        System.out.print("Choice (1-3): ");
        String c = scanner.nextLine().trim();

        if (c.equals("1")) {
            System.out.println("\n--- SINGLY LINKED LIST ---");
            System.out.printf("Current SLL Node Count: %d\n", studentService.getSinglyLinkedList().getSize());
            System.out.println("Checking for cycle using Floyd's Tortoise and Hare algorithm...");
            boolean hasCycle = studentService.getSinglyLinkedList().detectCycle();
            System.out.printf("Cycle Detected: %b (Time Complexity: O(n), Space: O(1))\n", hasCycle);

            System.out.print("Enter Student ID to remove from Singly Linked List: ");
            try {
                long id = Long.parseLong(scanner.nextLine().trim());
                long start = System.nanoTime();
                boolean removed = studentService.getSinglyLinkedList().deleteById(id);
                long duration = System.nanoTime() - start;

                AlgorithmResult res = new AlgorithmResult("Singly Linked List Deletion", "Linked Lists",
                        "Remove student node by ID", studentService.getSinglyLinkedList().getSize() + 1,
                        "CustomLinkedList", removed ? "Node Deleted" : "Node Not Found", duration,
                        studentService.getSinglyLinkedList().getSize(), studentService.getSinglyLinkedList().getSize(),
                        0, 0, "O(n)", "O(1)", "Traversed pointers sequentially to find target ID.",
                        List.of("Scanned nodes from head", removed ? "Relinked predecessor.next to curr.next" : "Reached end of list"));
                System.out.println("\n" + res.toFormattedDisplay());
            } catch (Exception ignored) {}

        } else if (c.equals("2")) {
            System.out.println("\n--- DOUBLY LINKED LIST ---");
            System.out.printf("Current DLL Node Count: %d\n", studentService.getDoublyLinkedList().getSize());
            System.out.println("First 5 Nodes (Forward Traversal):");
            List<Student> fwd = studentService.getDoublyLinkedList().traverseForward();
            for (int i = 0; i < Math.min(5, fwd.size()); i++) System.out.println("  " + fwd.get(i));

            System.out.println("\nFirst 5 Nodes (Backward Traversal from Tail):");
            List<Student> bwd = studentService.getDoublyLinkedList().traverseBackward();
            for (int i = 0; i < Math.min(5, bwd.size()); i++) System.out.println("  " + bwd.get(i));

            System.out.print("\nEnter Student ID to demonstrate Doubly Linked List removal: ");
            try {
                long id = Long.parseLong(scanner.nextLine().trim());
                long start = System.nanoTime();
                boolean removed = studentService.getDoublyLinkedList().deleteById(id);
                long dur = System.nanoTime() - start;

                System.out.println("\nOperation:\nDelete Student");
                System.out.println("Data Structure:\nDoubly Linked List");
                System.out.printf("Student ID:\n%d\n", id);
                System.out.printf("Node Found:\n%s\n", removed ? "YES" : "NO");
                System.out.printf("Node Removed:\n%s\n", removed ? "YES" : "NO");
                System.out.printf("Execution Time:\n%.4f ms\n", dur / 1_000_000.0);
                System.out.println("Time Complexity:\nO(n)");
            } catch (Exception ignored) {}

        } else if (c.equals("3")) {
            System.out.println("\n--- CIRCULAR LINKED LIST ---");
            System.out.printf("Current CLL Node Count: %d\n", studentService.getCircularLinkedList().getSize());
            System.out.println("Traversing Circular Linked List nodes:");
            List<Student> list = studentService.getCircularLinkedList().toList();
            for (int i = 0; i < Math.min(6, list.size()); i++) {
                System.out.println("  " + list.get(i));
            }
        }
    }

    // ================= 2. HASHING OPERATIONS =================
    public void handleHashing() {
        System.out.println("\n====================================================");
        System.out.println("            CUSTOM HASH TABLE OPERATIONS            ");
        System.out.println("====================================================");
        System.out.println(studentService.getHashTable().getTableStatistics());
        System.out.print("\nEnter Student ID to inspect Hash bucket and collisions: ");
        String key = scanner.nextLine().trim();

        int hash = studentService.getHashTable().computeHash(key);
        int bucket = studentService.getHashTable().getBucketIndex(hash);
        var lookup = studentService.getHashTable().searchChaining(key);

        System.out.println("\n------------------------------------------------");
        System.out.println("Student ID / Key:\n" + key);
        System.out.println("\nHash Value:\n" + hash);
        System.out.println("\nBucket:\n" + bucket);
        System.out.println("\nCollision Count in Bucket:\n" + lookup.collisionCount);
        System.out.println("\nSearch Result:\n" + (lookup.found ? lookup.student.getName() : "Not Found"));
        System.out.println("\nComplexity:\nO(1) Average, O(n) Worst");
        System.out.println("------------------------------------------------");
    }

    // ================= 3. BST OPERATIONS =================
    public void handleBST() {
        System.out.println("\n====================================================");
        System.out.println("          BINARY SEARCH TREE (BST) OPERATIONS       ");
        System.out.println("====================================================");
        System.out.printf("BST Node Count: %d | Tree Height: %d\n",
                studentService.getBst().getSize(), studentService.getBst().getHeight());
        System.out.println("\n1. Display Inorder Traversal (Sorted by ID)");
        System.out.println("2. Display ASCII Tree Topology");
        System.out.println("3. Delete Node from BST");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            List<Student> in = studentService.getBst().inorderTraversal();
            System.out.printf("\nInorder Traversal (%d nodes):\n", in.size());
            for (int i = 0; i < Math.min(10, in.size()); i++) {
                System.out.printf("  %d: %s (ID %d)\n", i + 1, in.get(i).getName(), in.get(i).getStudentId());
            }
        } else if (ch.equals("2")) {
            System.out.println("\nASCII Tree Topology (First Branches):");
            System.out.println(studentService.getBst().printTreeVisual());
        } else if (ch.equals("3")) {
            System.out.print("Enter Student ID to delete from BST: ");
            try {
                long id = Long.parseLong(scanner.nextLine().trim());
                boolean del = studentService.getBst().delete(id);
                System.out.printf("Node %d deletion from BST: %b\n", id, del);
            } catch (Exception ignored) {}
        }
    }

    // ================= 4. AVL TREE OPERATIONS =================
    public void handleAVL() {
        System.out.println("\n====================================================");
        System.out.println("            AVL TREE (SELF-BALANCING) OPS           ");
        System.out.println("====================================================");
        System.out.printf("AVL Size: %d | Height: %d\n",
                studentService.getAvlTree().getSize(),
                studentService.getAvlTree().height(studentService.getAvlTree().getRoot()));

        System.out.println("\nRecent AVL Rotations Executed during Balance Operations:");
        List<AVLTree.RotationEvent> logs = studentService.getAvlTree().getRotationLogs();
        if (logs.isEmpty()) {
            System.out.println("Tree is currently in balance. (No new rotations since last reset).");
        } else {
            for (int i = Math.max(0, logs.size() - 5); i < logs.size(); i++) {
                System.out.println("------------------------------------------------");
                System.out.println(logs.get(i));
            }
        }

        System.out.println("\nDisplay Balanced AVL Tree Structure (with Balance Factors)? (yes/no): ");
        String yn = scanner.nextLine().trim();
        if (yn.equalsIgnoreCase("yes") || yn.equalsIgnoreCase("y")) {
            System.out.println(studentService.getAvlTree().printTreeVisual());
        }
    }

    // ================= 5. GRAPH OPERATIONS =================
    public void handleGraph() {
        System.out.println("\n====================================================");
        System.out.println("        STUDENT RELATIONSHIP GRAPH OPERATIONS       ");
        System.out.println("====================================================");
        System.out.printf("Vertices: %d | Edges: %d | Dual Storage: Adjacency List + Matrix\n",
                studentService.getRelationshipGraph().getVertexCount(),
                studentService.getRelationshipGraph().getEdgeCount());
        System.out.println("1. Run Breadth-First Search (BFS) for Relationship Degrees");
        System.out.println("2. Run Depth-First Search (DFS) for Connected Academic Cohorts");
        System.out.println("3. Run Dijkstra's Algorithm for Shortest Academic Affinity Path");
        System.out.println("4. Verify Bipartite Graph Structure (2-Colorability)");
        System.out.print("Choice (1-4): ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            System.out.print("Enter Start Student ID (e.g. 2520080001): ");
            String id = "S_" + scanner.nextLine().trim();
            AlgorithmResult res = BFS.executeBFS(studentService.getRelationshipGraph(), id, 2);
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("2")) {
            AlgorithmResult res = DFS.executeDFS(studentService.getRelationshipGraph());
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("3")) {
            System.out.print("Enter Source Student ID (e.g. 2520080001): ");
            String sId = "S_" + scanner.nextLine().trim();
            System.out.print("Enter Destination Entity ID (e.g. 'SKILL_JAVA' or Student ID e.g. 2520080002): ");
            String target = scanner.nextLine().trim();
            if (!target.startsWith("S_") && !target.startsWith("SKILL_") && !target.startsWith("DEPT_")) {
                target = "S_" + target;
            }
            AlgorithmResult res = Dijkstra.executeDijkstra(studentService.getRelationshipGraph(), sId, target);
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("4")) {
            boolean bip = studentService.getRelationshipGraph().isBipartite();
            System.out.printf("\nAcademic Subgraph Bipartite (2-Colorable): %b\n", bip);
        }
    }

    // ================= 6. DYNAMIC PROGRAMMING =================
    public void handleDynamicProgramming() {
        System.out.println("\n====================================================");
        System.out.println("             DYNAMIC PROGRAMMING MODULE             ");
        System.out.println("====================================================");
        System.out.println("1. 0/1 Knapsack (Student Project & Study Hour Selection)");
        System.out.println("2. Subset Sum (Assessment Component Combination to Reach Target)");
        System.out.println("3. Longest Common Subsequence (Curriculum / Skill Trajectory LCS)");
        System.out.println("4. Matrix Chain Multiplication (MCM for Analytical Calculations)");
        System.out.println("5. Optimal Binary Search Tree (OBST for Frequent Student Searches)");
        System.out.println("6. Weighted Interval Scheduling (Academic Activities / Exams)");
        System.out.println("7. Bitmask DP (Held-Karp Campus Tour / Scheduling TSP)");
        System.out.print("Choice (1-7): ");
        String ch = scanner.nextLine().trim();

        switch (ch) {
            case "1":
                System.out.print("Enter student's available project budget hours (e.g. 80): ");
                int hrs = 80;
                try { hrs = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception ignored) {}
                AlgorithmResult kr = Knapsack.executeKnapsack(studentService.getAvailableProjects(), hrs);
                studentService.getExecutionService().recordExecution(kr);
                System.out.println("\n" + kr.toFormattedDisplay());
                break;

            case "2":
                System.out.print("Enter target total marks score to form (e.g. 75): ");
                int tgt = 75;
                try { tgt = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception ignored) {}
                int[] scores = {15, 20, 25, 30, 10, 40, 5};
                AlgorithmResult ss = SubsetSum.executeSubsetSum(scores, tgt);
                studentService.getExecutionService().recordExecution(ss);
                System.out.println("\n" + ss.toFormattedDisplay());
                break;

            case "3":
                Student a = studentService.getStudentById(2520080001L);
                Student b = studentService.getStudentById(2520080002L);
                if (a != null && b != null) {
                    AlgorithmResult lcs = LCS.executeLCS(a.getName(), a.getSkills(), b.getName(), b.getSkills());
                    studentService.getExecutionService().recordExecution(lcs);
                    System.out.println("\n" + lcs.toFormattedDisplay());
                }
                break;

            case "4":
                int[] dims = {10, 30, 5, 60, 20}; // Matrices A1(10x30), A2(30x5), A3(5x60), A4(60x20)
                AlgorithmResult mcm = MatrixChainMultiplication.executeMCM(dims);
                studentService.getExecutionService().recordExecution(mcm);
                System.out.println("\n" + mcm.toFormattedDisplay());
                break;

            case "5":
                long[] keys = {1001L, 1002L, 1003L, 1004L, 1005L};
                double[] freqs = {0.15, 0.10, 0.05, 0.40, 0.30};
                AlgorithmResult obst = OptimalBST.executeOptimalBST(keys, freqs);
                studentService.getExecutionService().recordExecution(obst);
                System.out.println("\n" + obst.toFormattedDisplay());
                break;

            case "6":
                List<model.AcademicActivity> activities = List.of(
                        new model.AcademicActivity("ACT-1", "Algorithms Lecture", 9, 11, 4),
                        new model.AcademicActivity("ACT-2", "Machine Learning Lab", 10, 13, 7),
                        new model.AcademicActivity("ACT-3", "Database Consultation", 11, 12, 3),
                        new model.AcademicActivity("ACT-4", "Capstone Presentation", 13, 15, 8),
                        new model.AcademicActivity("ACT-5", "Code Review Seminar", 14, 16, 5),
                        new model.AcademicActivity("ACT-6", "Competitive Contest", 16, 18, 6)
                );
                AlgorithmResult wis = WeightedIntervalScheduling.executeScheduling(activities);
                studentService.getExecutionService().recordExecution(wis);
                System.out.println("\n" + wis.toFormattedDisplay());
                break;

            case "7":
                AlgorithmResult tsp = TSPBitmask.executeCampusTour(null, null);
                studentService.getExecutionService().recordExecution(tsp);
                System.out.println("\n" + tsp.toFormattedDisplay());
                break;
        }
    }

    // ================= 7. NETWORK FLOW & ALLOCATION =================
    public void handleNetworkFlow() {
        System.out.println("\n====================================================");
        System.out.println("           NETWORK FLOW & ALLOCATION ENGINE         ");
        System.out.println("====================================================");
        System.out.println("1. Course Allocation (Students -> Courses via Dinic Flow)");
        System.out.println("2. Project Allocation (Students -> Projects via Skill Matching)");
        System.out.println("3. Faculty-Subject Allocation (Faculty -> Subjects)");
        System.out.println("4. Minimum Cut Bottleneck Analysis");
        System.out.println("5. Benchmark Flow Algorithms (Ford-Fulkerson vs Edmonds-Karp vs Dinic)");
        System.out.print("Choice (1-5): ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            AlgorithmResult res = BipartiteMatching.allocateCourses(studentService.getAllStudents(), studentService.getAvailableCourses());
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("2")) {
            AlgorithmResult res = BipartiteMatching.allocateProjects(studentService.getAllStudents(), studentService.getAvailableProjects());
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("3")) {
            AlgorithmResult res = BipartiteMatching.allocateFacultySubjects(studentService.getAvailableFaculties(), studentService.getAvailableSubjects());
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("4")) {
            // Build a small demonstration network
            Map<String, Map<String, Integer>> net = new HashMap<>();
            net.put("SRC", Map.of("CSE_HUB", 10, "ECE_HUB", 8));
            net.put("CSE_HUB", Map.of("ECE_HUB", 2, "LAB_1", 7, "LAB_2", 4));
            net.put("ECE_HUB", Map.of("LAB_2", 6));
            net.put("LAB_1", Map.of("SNK", 8));
            net.put("LAB_2", Map.of("SNK", 9));
            AlgorithmResult res = MinCut.executeMinCut(net, "SRC", "SNK", "Academic Facility Flow Bottleneck");
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("5")) {
            Map<String, Map<String, Integer>> net = new HashMap<>();
            net.put("SRC", Map.of("A", 10, "B", 10));
            net.put("A", Map.of("C", 4, "D", 8));
            net.put("B", Map.of("C", 9, "D", 2));
            net.put("C", Map.of("SNK", 10));
            net.put("D", Map.of("SNK", 10));
            String comp = studentService.getExecutionService().compareFlowAlgorithms(net, "SRC", "SNK");
            System.out.println("\n" + comp);
        }
    }

    // ================= 8. NP-COMPLETENESS MODULE =================
    public void handleNPCompleteness() {
        System.out.println("\n====================================================");
        System.out.println("       NP-COMPLETENESS EDUCATIONAL SOLVER MODULE    ");
        System.out.println("====================================================");
        System.out.println("Theoretical Foundations: P, NP, NP-Hard, NP-Complete, and Polynomial Reductions.");
        System.out.println("Solvers run strictly on small educational instances (n <= 16).");
        System.out.println("1. 3-SAT Exact Solver");
        System.out.println("2. CLIQUE Problem Exact Solver");
        System.out.println("3. Vertex Cover Exact Solver");
        System.out.println("4. Hamiltonian Path Solver");
        System.out.print("Choice (1-4): ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            List<EducationalNPSolver.Clause3SAT> clauses = List.of(
                    new EducationalNPSolver.Clause3SAT(1, 2, -3),
                    new EducationalNPSolver.Clause3SAT(-1, 2, 3),
                    new EducationalNPSolver.Clause3SAT(-2, -3, 4),
                    new EducationalNPSolver.Clause3SAT(-4, 1, 3)
            );
            AlgorithmResult res = EducationalNPSolver.solve3SAT(4, clauses);
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("2")) {
            int n = 5;
            boolean[][] adj = {
                    {false, true, true, true, false},
                    {true, false, true, true, false},
                    {true, true, false, true, true},
                    {true, true, true, false, false},
                    {false, false, true, false, false}
            };
            AlgorithmResult res = EducationalNPSolver.solveClique(n, adj, 4);
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("3")) {
            int n = 5;
            boolean[][] adj = {
                    {false, true, true, false, false},
                    {true, false, true, true, false},
                    {true, true, false, true, true},
                    {false, true, true, false, true},
                    {false, false, true, true, false}
            };
            AlgorithmResult res = EducationalNPSolver.solveVertexCover(n, adj, 3);
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("4")) {
            int n = 5;
            boolean[][] adj = {
                    {false, true, false, false, true},
                    {true, false, true, false, false},
                    {false, true, false, true, false},
                    {false, false, true, false, true},
                    {true, false, false, true, false}
            };
            AlgorithmResult res = EducationalNPSolver.solveHamiltonianPath(n, adj);
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        }
    }

    // ================= 9. APPROXIMATION ALGORITHMS =================
    public void handleApproximation() {
        System.out.println("\n====================================================");
        System.out.println("              APPROXIMATION ALGORITHMS              ");
        System.out.println("====================================================");
        System.out.println("1. Student Skill Coverage (Greedy Set Cover Approximation)");
        System.out.println("2. Vertex Cover 2-Approximation (Maximal Matching)");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            Set<String> reqSkills = Set.of("Java", "Python", "SQL", "Machine Learning", "Web Development");
            AlgorithmResult res = VertexCoverApproximation.executeSkillCoverage(studentService.getAllStudents(), reqSkills);
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("2")) {
            int n = 6;
            boolean[][] adj = {
                    {false, true, true, false, false, false},
                    {true, false, true, true, false, false},
                    {true, true, false, false, true, false},
                    {false, true, false, false, true, true},
                    {false, false, true, true, false, true},
                    {false, false, false, true, true, false}
            };
            AlgorithmResult res = VertexCoverApproximation.executeVertexCover2Approx(n, adj);
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        }
    }

    // ================= 10. PARAMETERIZED ALGORITHMS =================
    public void handleParameterized() {
        System.out.println("\n====================================================");
        System.out.println("      PARAMETERIZED ALGORITHMS (FPT & KERNELIZATION)");
        System.out.println("====================================================");
        System.out.println("Application: Select at most K students who collectively cover required skills.");
        System.out.print("Enter Parameter K (budget of students allowed, e.g. 3): ");
        int k = 3;
        try { k = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception ignored) {}

        Set<String> targetSkills = Set.of("java", "python", "sql", "machine learning", "docker");
        AlgorithmResult res = ParameterizedSkillCover.executeFPT(studentService.getAllStudents(), targetSkills, k);
        studentService.getExecutionService().recordExecution(res);
        System.out.println("\n" + res.toFormattedDisplay());
    }

    // ================= 11. RANDOMIZED ALGORITHMS =================
    public void handleRandomized() {
        System.out.println("\n====================================================");
        System.out.println("               RANDOMIZED ALGORITHMS                ");
        System.out.println("====================================================");
        System.out.println("1. Reservoir Sampling (Uniform random sample of K students)");
        System.out.println("2. Miller-Rabin Primality Test (Probabilistic token check)");
        System.out.println("3. Randomized Universal Hashing (Collision distribution)");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            System.out.print("Enter sample size K (e.g. 5): ");
            int k = 5;
            try { k = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception ignored) {}
            AlgorithmResult res = ReservoirSampling.executeReservoirSampling(studentService.getAllStudents(), k, System.currentTimeMillis());
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("2")) {
            System.out.print("Enter integer student token to test primality (e.g. 1000000007 or 2520080001): ");
            long token = 1000000007L;
            try { token = Long.parseLong(scanner.nextLine().trim()); } catch (Exception ignored) {}
            AlgorithmResult res = MillerRabin.executeMillerRabin(token, 10);
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("3")) {
            AlgorithmResult res = RandomizedHashing.executeRandomizedHashing(studentService.getAllStudents(), 53, System.currentTimeMillis());
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        }
    }

    // ================= 12. PARALLEL ALGORITHMS =================
    public void handleParallel() {
        System.out.println("\n====================================================");
        System.out.println("          PARALLEL & CONCURRENT DSA MODULE          ");
        System.out.println("====================================================");
        System.out.println("Utilizing available CPU cores: " + Runtime.getRuntime().availableProcessors());
        System.out.println("1. Parallel Multi-Core Student Performance Aggregation");
        System.out.println("2. Parallel vs Sequential Search Comparison");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();

        if (ch.equals("1")) {
            AlgorithmResult res = ParallelAnalytics.executeParallelMetrics(studentService.getAllStudents());
            studentService.getExecutionService().recordExecution(res);
            System.out.println("\n" + res.toFormattedDisplay());
        } else if (ch.equals("2")) {
            System.out.print("Enter Student ID to search concurrently: ");
            try {
                long id = Long.parseLong(scanner.nextLine().trim());
                AlgorithmResult res = algorithms.parallel.ParallelSearch.executeParallelSearch(studentService.getAllStudents(), id);
                studentService.getExecutionService().recordExecution(res);
                System.out.println("\n" + res.toFormattedDisplay());
            } catch (Exception ignored) {}
        }
    }
}
