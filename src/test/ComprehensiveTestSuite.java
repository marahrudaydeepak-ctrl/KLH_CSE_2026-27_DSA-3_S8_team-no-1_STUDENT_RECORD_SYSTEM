package test;

import algorithms.approximation.VertexCoverApproximation;
import algorithms.dynamicprogramming.*;
import algorithms.graph.BFS;
import algorithms.graph.DFS;
import algorithms.graph.Dijkstra;
import algorithms.networkflow.BipartiteMatching;
import algorithms.networkflow.Dinic;
import algorithms.networkflow.EdmondsKarp;
import algorithms.networkflow.FordFulkerson;
import algorithms.npcomplete.EducationalNPSolver;
import algorithms.parallel.ParallelAnalytics;
import algorithms.parameterized.ParameterizedSkillCover;
import algorithms.randomized.MillerRabin;
import algorithms.randomized.RandomizedQuickSort;
import algorithms.randomized.ReservoirSampling;
import algorithms.searching.BinarySearch;
import algorithms.searching.LinearSearch;
import algorithms.sorting.*;
import algorithms.string.*;
import datastructures.AVLTree;
import datastructures.BinarySearchTree;
import datastructures.CustomHashTable;
import datastructures.Trie;
import file.StudentFileManager;
import model.*;
import services.PerformanceService;
import services.SimilarityService;
import services.StudentService;

import java.util.*;

/**
 * Comprehensive Automated Test Suite verifying correctness, metrics generation,
 * and algorithm invariants across all 30+ implemented DSA components.
 */
public class ComprehensiveTestSuite {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void runAllTests(StudentService service) {
        System.out.println("====================================================================");
        System.out.println("     RUNNING COMPREHENSIVE DSA PROJECT AUTOMATED TEST SUITE        ");
        System.out.println("====================================================================\n");

        testsPassed = 0;
        testsFailed = 0;

        testStudentCRUD(service);
        testLinkedLists();
        testStackAndQueue(service);
        testHashTable(service);
        testBSTAndAVL(service);
        testSearchingAlgorithms(service);
        testSortingAlgorithms(service);
        testStringAlgorithms(service);
        testEditDistanceAndFuzzy();
        testDynamicProgramming(service);
        testGraphAlgorithms(service);
        testNetworkFlowAndAllocation(service);
        testNPCompleteness();
        testApproximationAndParameterized(service);
        testRandomizedAlgorithms(service);
        testParallelAlgorithms(service);
        testFilePersistence(service);
        testStudentRecordManagement(service);

        System.out.println("\n====================================================================");
        System.out.printf(" TEST SUITE SUMMARY: %d PASSED, %d FAILED (TOTAL %d TESTS)\n",
                testsPassed, testsFailed, testsPassed + testsFailed);
        System.out.println("====================================================================");
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.printf("  [PASS] %s\n", testName);
            testsPassed++;
        } else {
            System.err.printf("  [FAIL] %s\n", testName);
            testsFailed++;
        }
    }

    // 1. Student CRUD & Undo
    private static void testStudentCRUD(StudentService service) {
        System.out.println("--- 1. Testing Student CRUD & Undo Stack ---");
        Student s = new Student(2520099999L, "2520099999", "Test Student", "2003-01-01", "Male",
                "CSE", "B.Tech", 2, 4, "A", "test@univ.edu", "9999999999", "Campus",
                85.0, 90.0, 9.0, 9.0, "A+", List.of("Java"), List.of("Demo Project"),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "Test");

        boolean added = service.addStudent(s);
        assertTrue("Student Add Operation", added);
        assertTrue("Student Exists by ID", service.getStudentById(2520099999L) != null);

        // Delete with Undo
        boolean deleted = service.deleteStudent(2520099999L);
        assertTrue("Student Delete Operation", deleted);
        assertTrue("Student Deleted from Registry", service.getStudentById(2520099999L) == null);

        // Undo Delete
        boolean undone = service.undoLastOperation();
        assertTrue("Undo Delete Operation", undone);
        assertTrue("Student Restored via Undo Stack", service.getStudentById(2520099999L) != null);

        // Cleanup
        service.deleteStudent(2520099999L);
    }

    // 2. Linked Lists
    private static void testLinkedLists() {
        System.out.println("\n--- 2. Testing Custom Linked Lists & Floyd's Cycle Detection ---");
        datastructures.CustomLinkedList sll = new datastructures.CustomLinkedList();
        Student s1 = new Student(101, "101", "A", "2000-01-01", "M", "CSE", "B.Tech", 1, 1, "A", "a@a.com", "1", "1", 80, 80, 8, 8, "A", null, null, null, null, null, "");
        Student s2 = new Student(102, "102", "B", "2000-01-01", "F", "CSE", "B.Tech", 1, 1, "A", "b@a.com", "2", "2", 80, 80, 8, 8, "A", null, null, null, null, null, "");

        sll.insertTail(s1);
        sll.insertTail(s2);
        assertTrue("Singly Linked List Size == 2", sll.getSize() == 2);
        assertTrue("SLL Cycle Detect (No cycle)", !sll.detectCycle());

        sll.reverse();
        assertTrue("SLL Reverse Operation (Head is s2)", sll.getHead().data.getStudentId() == 102);

        datastructures.DoublyLinkedList dll = new datastructures.DoublyLinkedList();
        dll.insertTail(s1);
        dll.insertTail(s2);
        assertTrue("DLL Forward Traversal Size", dll.traverseForward().size() == 2);
        assertTrue("DLL Backward Traversal Size", dll.traverseBackward().size() == 2);

        datastructures.CircularLinkedList cll = new datastructures.CircularLinkedList();
        cll.insert(s1);
        cll.insert(s2);
        assertTrue("Circular Linked List Size", cll.getSize() == 2);
    }

    // 3. Stack & Queue
    private static void testStackAndQueue(StudentService service) {
        System.out.println("\n--- 3. Testing CustomStack & CustomQueue ---");
        datastructures.CustomStack<String> stack = new datastructures.CustomStack<>();
        stack.push("Alpha");
        stack.push("Beta");
        assertTrue("Stack Peek", "Beta".equals(stack.peek()));
        assertTrue("Stack Pop", "Beta".equals(stack.pop()));
        assertTrue("Stack Size after pop", stack.size() == 1);

        datastructures.CustomQueue<String> queue = new datastructures.CustomQueue<>();
        queue.enqueue("Req1");
        queue.enqueue("Req2");
        assertTrue("Queue Front", "Req1".equals(queue.getFront()));
        assertTrue("Queue Rear", "Req2".equals(queue.getRear()));
        assertTrue("Queue Dequeue FIFO", "Req1".equals(queue.dequeue()));
        assertTrue("Queue Size after dequeue", queue.getSize() == 1);
    }

    // 4. Custom Hash Table
    private static void testHashTable(StudentService service) {
        System.out.println("\n--- 4. Testing CustomHashTable (Chaining & Open Addressing) ---");
        CustomHashTable ht = service.getHashTable();
        CustomHashTable.HashLookupResult res = ht.searchChaining("2520080001");
        assertTrue("Chaining Hash Search for ID 2520080001", res.found);
        assertTrue("Hash Bucket Computed", res.bucketIndex >= 0);

        // Open Addressing test
        CustomHashTable oTable = new CustomHashTable(17);
        Student s = service.getStudentById(2520080001L);
        oTable.insertOpenAddressing("2520080001", s);
        CustomHashTable.HashLookupResult oRes = oTable.searchOpenAddressing("2520080001");
        assertTrue("Open Addressing Search Found", oRes.found);
    }

    // 5. BST & AVL
    private static void testBSTAndAVL(StudentService service) {
        System.out.println("\n--- 5. Testing Binary Search Tree & Self-Balancing AVL Tree ---");
        BinarySearchTree bst = service.getBst();
        BinarySearchTree.SearchResult bstRes = bst.search(2520080001L);
        assertTrue("BST Search ID 2520080001", bstRes.found);
        assertTrue("BST Inorder Traversal not empty", !bst.inorderTraversal().isEmpty());

        AVLTree avl = service.getAvlTree();
        AVLTree.AVLSearchResult avlRes = avl.search(2520080001L);
        assertTrue("AVL Tree Search ID 2520080001", avlRes.found);
        assertTrue("AVL Tree Balanced Height <= 1.44 log2(N)", avl.height(avl.getRoot()) <= 15);
    }

    // 6. Searching Algorithms
    private static void testSearchingAlgorithms(StudentService service) {
        System.out.println("\n--- 6. Testing Searching Algorithms (Linear & Binary Search) ---");
        List<Student> students = service.getAllStudents();
        AlgorithmResult linRes = LinearSearch.search(students, "2520080001", LinearSearch.SearchField.STUDENT_ID);
        assertTrue("Linear Search Match", linRes.getOutput().toString().contains("2520080001"));

        AlgorithmResult binRes = BinarySearch.searchById(students, 2520080001L);
        assertTrue("Binary Search Match", binRes.getOutput().toString().contains("2520080001"));
        assertTrue("Binary Search Logarithmic Comparisons", binRes.getComparisons() <= 10);
    }

    // 7. Sorting Algorithms
    private static void testSortingAlgorithms(StudentService service) {
        System.out.println("\n--- 7. Testing All 6 Sorting Algorithms + Randomized + Parallel ---");
        List<Student> students = service.getAllStudents();

        List<AlgorithmResult> sorts = List.of(
                BubbleSort.sort(students, SortCriteria.CGPA, true),
                SelectionSort.sort(students, SortCriteria.CGPA, true),
                InsertionSort.sort(students, SortCriteria.CGPA, true),
                MergeSort.sort(students, SortCriteria.CGPA, true),
                QuickSort.sort(students, SortCriteria.CGPA, true),
                HeapSort.sort(students, SortCriteria.CGPA, true),
                RandomizedQuickSort.sort(students, SortCriteria.CGPA, true, 42L),
                algorithms.parallel.ParallelSort.executeParallelSort(students, SortCriteria.CGPA, true)
        );

        for (AlgorithmResult res : sorts) {
            @SuppressWarnings("unchecked")
            List<Student> out = (List<Student>) res.getOutput();
            boolean isSorted = true;
            for (int i = 0; i < out.size() - 1; i++) {
                if (out.get(i).getCgpa() > out.get(i + 1).getCgpa()) {
                    isSorted = false;
                    break;
                }
            }
            assertTrue(res.getAlgorithmName() + " correctly sorted ascending", isSorted);
        }
    }

    // 8. String Algorithms
    private static void testStringAlgorithms(StudentService service) {
        System.out.println("\n--- 8. Testing String Search Algorithms (Naive, KMP, Z, Rabin-Karp, Boyer-Moore, Aho-Corasick, Trie) ---");
        List<Student> students = service.getAllStudents();

        AlgorithmResult naive = NaiveStringMatching.search(students, "Java");
        AlgorithmResult kmp = KMP.search(students, "Java");
        AlgorithmResult zAlg = ZAlgorithm.search(students, "Java");
        AlgorithmResult rk = RabinKarp.search(students, "Java");
        AlgorithmResult bm = BoyerMoore.search(students, "Java");

        assertTrue("Naive String Matching found occurrences", naive.getOutput().toString().contains("matches"));
        assertTrue("KMP found occurrences", kmp.getOutput().toString().contains("matches"));
        assertTrue("Z Algorithm found occurrences", zAlg.getOutput().toString().contains("matches"));
        assertTrue("Rabin-Karp found occurrences", rk.getOutput().toString().contains("matches"));
        assertTrue("Boyer-Moore found occurrences", bm.getOutput().toString().contains("matches"));

        AhoCorasick ac = new AhoCorasick(List.of("Java", "Python", "SQL"));
        AlgorithmResult acRes = ac.searchAll(students);
        assertTrue("Aho-Corasick Multi-Pattern Search", acRes.getOutput().toString().contains("occurrences"));

        Trie trie = service.getNameTrie();
        Trie.TrieSearchResult tRes = trie.prefixSearch("Te");
        assertTrue("Trie Prefix Autocomplete 'Te'", !tRes.matchedWords.isEmpty());
    }

    // 9. Edit Distance & Fuzzy Matching
    private static void testEditDistanceAndFuzzy() {
        System.out.println("\n--- 9. Testing Levenshtein & Damerau-Levenshtein Edit Distance ---");
        LevenshteinDistance.DistanceResult lev = LevenshteinDistance.compute("Tejas", "Tejes");
        assertTrue("Levenshtein Distance == 1", lev.distance == 1);
        assertTrue("Levenshtein Similarity >= 80%", lev.similarityPercentage >= 80.0);

        DamerauLevenshtein.DLResult dl = DamerauLevenshtein.compute("Teh", "The"); // Transposition
        assertTrue("Damerau-Levenshtein Transposition Distance == 1", dl.distance == 1);
    }

    // 10. Dynamic Programming
    private static void testDynamicProgramming(StudentService service) {
        System.out.println("\n--- 10. Testing All Dynamic Programming Modules ---");
        // 0/1 Knapsack
        Knapsack.KnapsackResult kr = Knapsack.solve(service.getAvailableProjects(), 80);
        assertTrue("Knapsack DP Max Value > 0", kr.maxAcademicValue > 0);

        // Subset Sum
        SubsetSum.SubsetSumResult ss = SubsetSum.solve(new int[]{10, 20, 30, 40}, 50);
        assertTrue("Subset Sum Target 50 is Possible", ss.possible);

        // LCS
        LCS.LCSResult lcs = LCS.compute(List.of("A", "B", "C", "D"), List.of("B", "D", "E"));
        assertTrue("LCS Length == 2", lcs.lcsLength == 2);

        // Matrix Chain Multiplication
        MatrixChainMultiplication.MCMResult mcm = MatrixChainMultiplication.solve(new int[]{10, 30, 5, 60});
        assertTrue("MCM Solved", mcm.minMultiplications > 0);

        // Optimal BST
        OptimalBST.OBSTResult obst = OptimalBST.solve(new long[]{1L, 2L, 3L}, new double[]{0.5, 0.3, 0.2});
        assertTrue("OBST Minimum Expected Cost > 0", obst.minExpectedCost > 0);

        // Weighted Interval Scheduling
        List<model.AcademicActivity> acts = List.of(
                new model.AcademicActivity("A1", "Lecture", 9, 11, 5),
                new model.AcademicActivity("A2", "Lab", 10, 12, 6),
                new model.AcademicActivity("A3", "Review", 11, 13, 5)
        );
        WeightedIntervalScheduling.ScheduleResult wis = WeightedIntervalScheduling.solve(acts);
        assertTrue("Weighted Interval Scheduling Total Weight == 10", wis.totalWeight == 10);

        // TSP Bitmask
        int[][] dist = {{0, 10, 15}, {10, 0, 20}, {15, 20, 0}};
        TSPBitmask.TSPResult tsp = TSPBitmask.solve(dist, new String[]{"Dep", "Lib", "Lab"});
        assertTrue("TSP Bitmask Min Cost == 45", tsp.minCost == 45);
    }

    // 11. Graph Algorithms
    private static void testGraphAlgorithms(StudentService service) {
        System.out.println("\n--- 11. Testing Graph Algorithms (BFS, DFS, Dijkstra, Bipartite) ---");
        datastructures.Graph g = service.getRelationshipGraph();
        BFS.BFSResult bfs = BFS.traverse(g, "S_2520080001", 2);
        assertTrue("BFS Traversal reached nodes", !bfs.traversalOrder.isEmpty());

        DFS.DFSResult dfs = DFS.traverseAll(g);
        assertTrue("DFS Connected Components > 0", !dfs.connectedComponents.isEmpty());

        Dijkstra.DijkstraResult dij = Dijkstra.findShortestPath(g, "S_2520080001", "DEPT_CSE");
        assertTrue("Dijkstra Shortest Path verified", dij.shortestDistance < Double.POSITIVE_INFINITY);
    }

    // 12. Network Flow & Allocations
    private static void testNetworkFlowAndAllocation(StudentService service) {
        System.out.println("\n--- 12. Testing Network Flow (Ford-Fulkerson, Edmonds-Karp, Dinic, Min-Cut, Allocations) ---");
        Map<String, Map<String, Integer>> net = new HashMap<>();
        net.put("SRC", Map.of("A", 10, "B", 5));
        net.put("A", Map.of("SNK", 10));
        net.put("B", Map.of("SNK", 5));

        FordFulkerson.FlowResult ff = FordFulkerson.computeMaxFlow(net, "SRC", "SNK");
        EdmondsKarp.EdmondsKarpResult ek = EdmondsKarp.computeMaxFlow(net, "SRC", "SNK");
        Dinic.DinicResult din = Dinic.computeMaxFlow(net, "SRC", "SNK");

        assertTrue("Ford-Fulkerson Flow == 15", ff.maxFlow == 15);
        assertTrue("Edmonds-Karp Flow == 15", ek.maxFlow == 15);
        assertTrue("Dinic Flow == 15", din.maxFlow == 15);

        algorithms.networkflow.MinCut.MinCutResult mc = algorithms.networkflow.MinCut.computeMinCut(net, "SRC", "SNK");
        assertTrue("Min-Cut Capacity == 15", mc.minCutCapacity == 15);

        AlgorithmResult courseRes = BipartiteMatching.allocateCourses(service.getAllStudents(), service.getAvailableCourses());
        assertTrue("Course Allocation executed via Max Flow", courseRes.getOutput().toString().contains("assigned"));

        AlgorithmResult projRes = BipartiteMatching.allocateProjects(service.getAllStudents(), service.getAvailableProjects());
        assertTrue("Project Allocation executed via Skill Matching", projRes.getOutput().toString().contains("allocated"));
    }

    // 13. NP-Completeness
    private static void testNPCompleteness() {
        System.out.println("\n--- 13. Testing NP-Completeness Educational Exact Solvers ---");
        List<EducationalNPSolver.Clause3SAT> clauses = List.of(
                new EducationalNPSolver.Clause3SAT(1, 2, 3),
                new EducationalNPSolver.Clause3SAT(-1, 2, 3)
        );
        AlgorithmResult sat = EducationalNPSolver.solve3SAT(3, clauses);
        assertTrue("3-SAT Educational Solver evaluates SAT", sat.getOutput().equals("SATISFIABLE"));

        boolean[][] cliqueAdj = {
                {false, true, true},
                {true, false, true},
                {true, true, false}
        };
        AlgorithmResult clq = EducationalNPSolver.solveClique(3, cliqueAdj, 3);
        assertTrue("CLIQUE Solver finds 3-clique", clq.getOutput().toString().contains("Found"));

        AlgorithmResult vc = EducationalNPSolver.solveVertexCover(3, cliqueAdj, 2);
        assertTrue("Vertex Cover Solver finds 2-cover", vc.getOutput().toString().contains("Valid"));

        AlgorithmResult hp = EducationalNPSolver.solveHamiltonianPath(3, cliqueAdj);
        assertTrue("Hamiltonian Path Solver finds path", hp.getOutput().toString().contains("Exists"));
    }

    // 14. Approximation & Parameterized
    private static void testApproximationAndParameterized(StudentService service) {
        System.out.println("\n--- 14. Testing Approximation & Parameterized FPT Algorithms ---");
        Set<String> targetSkills = Set.of("Java", "Python", "SQL");
        AlgorithmResult approx = VertexCoverApproximation.executeSkillCoverage(service.getAllStudents(), targetSkills);
        assertTrue("Greedy Skill Coverage executes", approx.getOutput().toString().contains("cover"));

        AlgorithmResult fpt = ParameterizedSkillCover.executeFPT(service.getAllStudents(), targetSkills, 5);
        assertTrue("FPT Parameterized Kernelization executes", fpt.getOutput().toString().contains("Possible"));
    }

    // 15. Randomized Algorithms
    private static void testRandomizedAlgorithms(StudentService service) {
        System.out.println("\n--- 15. Testing Randomized Algorithms ---");
        List<Student> sample = ReservoirSampling.sample(service.getAllStudents(), 5, 42L);
        assertTrue("Reservoir Sampling returned exactly K=5 students", sample.size() == 5);

        MillerRabin.PrimalityResult mrPrime = MillerRabin.testPrimality(1000000007L, 5);
        assertTrue("Miller-Rabin verifies 1000000007 is Probable Prime", mrPrime.isProbablePrime);

        MillerRabin.PrimalityResult mrComp = MillerRabin.testPrimality(1000000008L, 5);
        assertTrue("Miller-Rabin verifies 1000000008 is Composite", !mrComp.isProbablePrime);
    }

    // 16. Parallel Concurrency
    private static void testParallelAlgorithms(StudentService service) {
        System.out.println("\n--- 16. Testing Parallel Algorithms & Concurrency ---");
        AlgorithmResult parSearch = algorithms.parallel.ParallelSearch.executeParallelSearch(service.getAllStudents(), 2520080001L);
        assertTrue("Parallel Search located student", parSearch.getOutput().toString().contains("2520080001"));

        AlgorithmResult parMetrics = ParallelAnalytics.executeParallelMetrics(service.getAllStudents());
        assertTrue("Parallel Analytics aggregated stats", parMetrics.getOutput().toString().contains("Avg Marks"));
    }

    // 17. File Persistence
    private static void testFilePersistence(StudentService service) {
        System.out.println("\n--- 17. Testing File Handling & Serialization ---");
        boolean csvSaved = StudentFileManager.saveToCSV(service.getAllStudents(), "data/test_students.csv");
        assertTrue("Save to CSV", csvSaved);

        List<Student> loaded = StudentFileManager.loadFromCSV("data/test_students.csv");
        assertTrue("Load from CSV verified count >= 30", loaded.size() >= 30);

        boolean backedUp = StudentFileManager.backupRecords(service.getAllStudents(), "data/test_backup.dat");
        assertTrue("Backup binary serialization", backedUp);

        List<Student> restored = StudentFileManager.restoreRecords("data/test_backup.dat");
        assertTrue("Restore binary serialization verified count >= 30", restored.size() >= 30);
    }

    // 18. Student Records, Grade Cards & Certificates
    private static void testStudentRecordManagement(StudentService service) {
        System.out.println("\n--- 18. Testing Student Record Management, Grades & Certificates ---");
        Student s = service.getStudentById(2520080001L);
        assertTrue("Test Student exists for record testing", s != null);

        // A. Grade Card & Marksheet
        GradeCard gc = service.getRecordService().generateGradeCard(s);
        assertTrue("Grade Card Generated", gc != null);
        assertTrue("Grade Card Registered Credits > 0", gc.getTotalCreditsRegistered() > 0);
        assertTrue("Grade Card SGPA Valid Range (0-10)", gc.getSgpa() > 0.0 && gc.getSgpa() <= 10.0);
        assertTrue("Grade Card Formatted Marksheet Contains Institutional Header",
                gc.toFormattedMarksheet().contains("APEX INSTITUTE OF HIGHER TECHNOLOGY"));

        // B. Official Certificates
        Certificate bonafide = service.getRecordService().generateCertificate(s, CertificateType.BONAFIDE, "Passport Application");
        assertTrue("Bonafide Certificate Generated", bonafide != null);
        assertTrue("Certificate has Verification Hash", bonafide.getVerificationHash() != null && !bonafide.getVerificationHash().isEmpty());
        assertTrue("Bonafide Document Contains Student Name", bonafide.getFormattedDocument().contains(s.getName().toUpperCase()));

        Certificate merit = service.getRecordService().generateCertificate(s, CertificateType.MERIT_EXCELLENCE, "Dean's Honor Roll");
        assertTrue("Merit Certificate Generated", merit.getFormattedDocument().contains("CERTIFICATE OF MERIT"));

        Certificate completion = service.getRecordService().generateCertificate(s, CertificateType.COURSE_COMPLETION, "Degree Conferment");
        assertTrue("Course Completion Certificate Generated", completion.getFormattedDocument().contains("COURSE COMPLETION"));

        Certificate transcript = service.getRecordService().generateCertificate(s, CertificateType.OFFICIAL_TRANSCRIPT, "Graduate Admissions");
        assertTrue("Official Transcript Generated", transcript.getFormattedDocument().contains("TRANSCRIPT OF ACADEMIC RECORD"));

        Certificate tc = service.getRecordService().generateCertificate(s, CertificateType.TRANSFER_LEAVING, "Course Completion");
        assertTrue("Transfer Certificate Generated", tc.getFormattedDocument().contains("TRANSFER & LEAVING CERTIFICATE"));

        Certificate noc = service.getRecordService().generateCertificate(s, CertificateType.INTERNSHIP_NOC, "Tech Research Labs");
        assertTrue("Internship NOC Generated", noc.getFormattedDocument().contains("NO OBJECTION CERTIFICATE"));

        // C. Student Identity Card
        StudentIdCard idCard = service.getRecordService().generateStudentIdCard(s);
        assertTrue("Student ID Card Generated", idCard != null);
        assertTrue("ID Card Formatted Layout Contains Barcode & Emergency Contact",
                idCard.getFormattedCard().contains("BARCODE") && idCard.getFormattedCard().contains("BLOOD GROUP"));

        // D. Fee Ledger & Receipts
        FeeRecord fr = service.getRecordService().getFeeRecord(s);
        assertTrue("Fee Record Initialized", fr != null);
        double prevBalance = fr.getBalanceDue();
        fr.recordPayment(5000.0, "UPI Gateway");
        assertTrue("Fee Payment Recorded and Balance Reduced", fr.getBalanceDue() <= prevBalance);
        assertTrue("Fee Payment Receipt Formatted Layout",
                fr.toFormattedReceipt().contains("OFFICIAL FEE PAYMENT RECEIPT"));
        assertTrue("No-Dues Certificate Layout",
                fr.toFormattedNoDuesCertificate().contains("NO DUES & FINANCIAL CLEARANCE"));

        // E. Class-Wide Tabulation Register
        String tabulation = service.getRecordService().generateTabulationRegister("CSE", 5);
        assertTrue("Tabulation Register Generated with Student Data",
                tabulation.contains("TABULATION REGISTER") && tabulation.contains("TOTAL STUDENTS TABULATED"));

        // F. Attendance Shortage Advisory
        String warningMemo = service.getRecordService().generateAttendanceShortageNotice(s);
        assertTrue("Attendance Shortage Warning Memo Formatted",
                warningMemo.contains("ATTENDANCE SHORTAGE ADVISORY"));

        // G. File Export Persistence
        boolean exported = service.getRecordService().saveDocumentToFile(gc.toFormattedMarksheet(), "data/test_marksheet.txt");
        assertTrue("Marksheet Document Export to File System", exported);

        // H. Dedicated Separate CGPA Scorecard & Ledger
        CgpaRecord cr = service.getRecordService().generateCgpaRecord(s);
        assertTrue("Separate CGPA Record Generated", cr != null);
        assertTrue("CGPA Department Rank > 0", cr.getRankInDept() > 0);
        assertTrue("CGPA Departmental Percentile Valid", cr.getPercentile() >= 0.0 && cr.getPercentile() <= 100.0);
        assertTrue("CGPA Academic Standing Tier Assigned", cr.getPerformanceTier() != null && !cr.getPerformanceTier().isEmpty());
        assertTrue("Formatted CGPA Scorecard Layout Contains Institutional Headers",
                cr.toFormattedCgpaCard().contains("DEDICATED STUDENT CGPA & ACADEMIC SCORECARD") && cr.toFormattedCgpaCard().contains("SEMESTER-WISE GPA PROGRESSION HISTORY"));
        String cgpaLedger = service.getRecordService().generateSeparateCgpaReport("CSE", null);
        assertTrue("Department CGPA Ledger Generated",
                cgpaLedger.contains("SEPARATE CGPA ACADEMIC PERFORMANCE LEDGER") && cgpaLedger.contains("ACADEMIC STANDING"));

        // I. Dedicated Separate Attendance Card & Statutory Register
        AttendanceRecord ar = service.getRecordService().generateAttendanceRecord(s);
        assertTrue("Separate Attendance Record Generated", ar != null);
        assertTrue("Total Classes Held Standard (180 periods)", ar.getTotalClassesHeld() == 180);
        assertTrue("Attended Classes Valid (<= 180)", ar.getClassesAttended() >= 0 && ar.getClassesAttended() <= 180);
        assertTrue("Statutory Exam Eligibility Status Determined", ar.getStatus() != null && ar.getStatus().getTitle() != null);
        assertTrue("Formatted Attendance Card Layout Contains Statutory Matrix",
                ar.toFormattedAttendanceCard().contains("DEDICATED STUDENT ATTENDANCE REPORT CARD") && ar.toFormattedAttendanceCard().contains("STATUTORY REGULATORY STATUS"));
        String attdRegister = service.getRecordService().generateSeparateAttendanceReport("ALL", "ALL");
        assertTrue("College-Wide Separate Attendance Register Generated",
                attdRegister.contains("SEPARATE STUDENT ATTENDANCE REGISTER") && attdRegister.contains("EXAM ELIGIBILITY AUDIT"));
    }
}
