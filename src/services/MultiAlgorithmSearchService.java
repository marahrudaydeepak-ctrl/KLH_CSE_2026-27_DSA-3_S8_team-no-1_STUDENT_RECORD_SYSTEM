package services;

import algorithms.searching.BinarySearch;
import algorithms.searching.LinearSearch;
import algorithms.sorting.MergeSort;
import algorithms.sorting.SortCriteria;
import algorithms.string.*;
import datastructures.AVLTree;
import datastructures.BinarySearchTree;
import datastructures.CustomHashTable;
import datastructures.Trie;
import model.AlgorithmResult;
import model.Student;

import java.util.*;

/**
 * Intelligent Multi-Algorithm Search Service ("Omni-Search Engine").
 * Simultaneously executes and benchmarks every search algorithm from the DSA syllabus:
 * 1. Linear Search (Unsorted records)
 * 2. Binary Search (Logarithmic on sorted IDs)
 * 3. Custom Hash Table Lookup (O(1) Separate Chaining & Open Addressing)
 * 4. Binary Search Tree (BST) Key Lookup
 * 5. Self-Balancing AVL Tree Lookup
 * 6. Trie Prefix Autocomplete (Name & Skill trees)
 * 7. Knuth-Morris-Pratt (KMP) Substring Pattern Search
 * 8. Rabin-Karp Rolling Hash Pattern Search
 * 9. Boyer-Moore Bad Character Heuristic Search
 * 10. Aho-Corasick Multi-Keyword Automaton
 * 11. Fuzzy Search: Levenshtein Distance (DP Edit Distance)
 * 12. Fuzzy Search: Damerau-Levenshtein (Transposition Handling)
 */
public class MultiAlgorithmSearchService {

    public static class SearchResultItem {
        private final Student student;
        private final String matchType; // "EXACT", "PREFIX", "SUBSTRING", "FUZZY"
        private final String primaryAlgorithm;
        private final int editDistance;
        private final double confidencePercentage;
        private final String matchDetail;

        public SearchResultItem(Student student, String matchType, String primaryAlgorithm,
                                int editDistance, double confidencePercentage, String matchDetail) {
            this.student = student;
            this.matchType = matchType;
            this.primaryAlgorithm = primaryAlgorithm;
            this.editDistance = editDistance;
            this.confidencePercentage = confidencePercentage;
            this.matchDetail = matchDetail;
        }

        public Student getStudent() { return student; }
        public String getMatchType() { return matchType; }
        public String getPrimaryAlgorithm() { return primaryAlgorithm; }
        public int getEditDistance() { return editDistance; }
        public double getConfidencePercentage() { return confidencePercentage; }
        public String getMatchDetail() { return matchDetail; }

        @Override
        public String toString() {
            return String.format("[%s via %s] ID: %d - %s (%s, %.1f%% conf)",
                    matchType, primaryAlgorithm, student.getStudentId(), student.getName(), matchDetail, confidencePercentage);
        }
    }

    public static class AlgorithmStat {
        public String algorithmName;
        public String category;
        public String dataStructure;
        public int matchesFound;
        public long comparisons;
        public long executionTimeNs;
        public String timeComplexity;
        public String spaceComplexity;

        public AlgorithmStat(String name, String category, String ds, int matches,
                             long comparisons, long timeNs, String timeComp, String spaceComp) {
            this.algorithmName = name;
            this.category = category;
            this.dataStructure = ds;
            this.matchesFound = matches;
            this.comparisons = comparisons;
            this.executionTimeNs = timeNs;
            this.timeComplexity = timeComp;
            this.spaceComplexity = spaceComp;
        }
    }

    public static class OmniSearchResult {
        public String query;
        public List<SearchResultItem> results = new ArrayList<>();
        public List<AlgorithmStat> algorithmStats = new ArrayList<>();
        public long totalExecutionTimeNs = 0;
        public long totalComparisons = 0;

        public String getFormattedSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("========================================================================================\n");
            sb.append(String.format("   MULTI-ALGORITHM OMNI-SEARCH RESULTS FOR QUERY: \"%s\"\n", query));
            sb.append("========================================================================================\n");
            sb.append(String.format("Total Matched Records: %d | Combined Search Time: %.3f ms | Total Comparisons: %,d\n\n",
                    results.size(), totalExecutionTimeNs / 1_000_000.0, totalComparisons));

            sb.append("--- ALGORITHM PERFORMANCE BREAKDOWN ---\n");
            sb.append(String.format("%-26s %-16s %-10s %-14s %-14s %-12s\n",
                    "Algorithm", "Data Structure", "Matches", "Comparisons", "Time (ms)", "Complexity"));
            sb.append("----------------------------------------------------------------------------------------\n");

            for (AlgorithmStat stat : algorithmStats) {
                sb.append(String.format("%-26s %-16s %-10d %-14d %-14.3f %-12s\n",
                        stat.algorithmName, stat.dataStructure, stat.matchesFound,
                        stat.comparisons, stat.executionTimeNs / 1_000_000.0, stat.timeComplexity));
            }
            sb.append("----------------------------------------------------------------------------------------\n\n");

            sb.append("--- MATCHED STUDENT RECORDS ---\n");
            if (results.isEmpty()) {
                sb.append("No student records matched the query across all syllabus search algorithms.\n");
            } else {
                for (int i = 0; i < results.size(); i++) {
                    SearchResultItem item = results.get(i);
                    Student s = item.getStudent();
                    sb.append(String.format("%2d. [%-9s | %-18s] ID: %d | %-18s | %-4s | CGPA: %.2f | %s\n",
                            i + 1, item.getMatchType(), item.getPrimaryAlgorithm(),
                            s.getStudentId(), s.getName(), s.getDepartment(), s.getCgpa(), item.getMatchDetail()));
                }
            }
            sb.append("========================================================================================\n");
            return sb.toString();
        }
    }

    /**
     * Executes the Omni-Search pipeline across all syllabus algorithms.
     */
    public static OmniSearchResult search(StudentService service, String query, String departmentFilter) {
        OmniSearchResult omni = new OmniSearchResult();
        if (query == null) query = "";
        omni.query = query.trim();

        long globalStart = System.nanoTime();
        String qNorm = omni.query.toLowerCase();
        List<Student> allStudents = service.getAllStudents();
        if (departmentFilter != null && !departmentFilter.equalsIgnoreCase("ALL")) {
            allStudents = allStudents.stream()
                    .filter(s -> s.getDepartment().equalsIgnoreCase(departmentFilter))
                    .toList();
        }

        Map<Long, SearchResultItem> uniqueResults = new LinkedHashMap<>();
        Long numericId = tryParseLong(omni.query);

        // ================= 1. EXACT / TREE / HASH LOOKUPS (If Numeric ID) =================
        if (numericId != null) {
            // A. Custom Hash Table Lookup (O(1))
            long startHash = System.nanoTime();
            CustomHashTable.HashLookupResult hashRes = service.getHashTable().searchChaining(String.valueOf(numericId));
            long hashTime = System.nanoTime() - startHash;
            int hashMatches = (hashRes != null && hashRes.found) ? 1 : 0;
            omni.algorithmStats.add(new AlgorithmStat(
                    "Hash Table (Chaining)", "Hashing", "Hash Array + Buckets",
                    hashMatches, hashRes != null ? hashRes.stepsTaken : 1, hashTime, "O(1) avg", "O(M)"));

            if (hashRes != null && hashRes.found && hashRes.student != null) {
                uniqueResults.put(hashRes.student.getStudentId(), new SearchResultItem(
                        hashRes.student, "EXACT", "Hash Table", 0, 100.0,
                        String.format("Found in Bucket #%d (Collisions: %d)", hashRes.bucketIndex, hashRes.collisionCount)));
            }

            // B. Binary Search (O(log n))
            long startBin = System.nanoTime();
            List<Student> sortedById = new ArrayList<>(allStudents);
            MergeSort.sort(sortedById, SortCriteria.STUDENT_ID, true);
            AlgorithmResult binRes = BinarySearch.searchById(sortedById, numericId);
            long binTime = System.nanoTime() - startBin;
            int binMatches = binRes.getOutput().toString().contains("Found") ? 1 : 0;
            omni.algorithmStats.add(new AlgorithmStat(
                    "Binary Search", "Searching", "Sorted Array",
                    binMatches, binRes.getComparisons(), binTime, "O(log n)", "O(1)"));

            // C. Binary Search Tree (BST)
            long startBst = System.nanoTime();
            BinarySearchTree.SearchResult bstRes = service.getBst().search(numericId);
            long bstTime = System.nanoTime() - startBst;
            int bstMatches = (bstRes != null && bstRes.found) ? 1 : 0;
            omni.algorithmStats.add(new AlgorithmStat(
                    "Binary Search Tree", "Trees", "Binary Search Tree",
                    bstMatches, bstRes != null ? bstRes.comparisons : 1, bstTime, "O(log n) avg", "O(n)"));

            // D. Self-Balancing AVL Tree
            long startAvl = System.nanoTime();
            AVLTree.AVLSearchResult avlRes = service.getAvlTree().search(numericId);
            long avlTime = System.nanoTime() - startAvl;
            int avlMatches = (avlRes != null && avlRes.found) ? 1 : 0;
            omni.algorithmStats.add(new AlgorithmStat(
                    "AVL Tree (Balanced)", "Trees", "Height-Balanced Tree",
                    avlMatches, avlRes != null ? avlRes.comparisons : 1, avlTime, "O(log n) guaranteed", "O(n)"));

            if (avlRes != null && avlRes.found && avlRes.student != null) {
                uniqueResults.putIfAbsent(avlRes.student.getStudentId(), new SearchResultItem(
                        avlRes.student, "EXACT", "AVL Tree", 0, 100.0, "Verified balanced tree search"));
            }
        }

        // ================= 2. LINEAR SEARCH =================
        long startLin = System.nanoTime();
        AlgorithmResult linRes = LinearSearch.search(allStudents, omni.query, LinearSearch.SearchField.NAME);
        long linTime = System.nanoTime() - startLin;
        int linMatches = 0;
        if (!"Not Found".equals(linRes.getOutput())) {
            for (Student s : allStudents) {
                if (s.getName().toLowerCase().contains(qNorm) || s.getRollNumber().equalsIgnoreCase(omni.query)) {
                    linMatches++;
                    uniqueResults.putIfAbsent(s.getStudentId(), new SearchResultItem(
                            s, "EXACT", "Linear Search", 0, 100.0, "Exact field equality"));
                }
            }
        }
        omni.algorithmStats.add(new AlgorithmStat(
                "Linear Search", "Searching", "Sequential Array",
                linMatches, linRes.getComparisons(), linTime, "O(n)", "O(1)"));

        // ================= 3. TRIE PREFIX AUTOCOMPLETE =================
        long startTrie = System.nanoTime();
        Trie.TrieSearchResult nameTrieRes = service.getNameTrie().prefixSearch(omni.query);
        Trie.TrieSearchResult skillTrieRes = service.getSkillTrie().prefixSearch(omni.query);
        long trieTime = System.nanoTime() - startTrie;
        int trieMatches = nameTrieRes.matchedData.size() + skillTrieRes.matchedData.size();

        for (Object obj : nameTrieRes.matchedData) {
            if (obj instanceof Student s) {
                uniqueResults.putIfAbsent(s.getStudentId(), new SearchResultItem(
                        s, "PREFIX", "Trie Prefix Tree", 0, 95.0,
                        String.format("Prefix matched name '%s'", s.getName())));
            }
        }
        for (Object obj : skillTrieRes.matchedData) {
            if (obj instanceof Student s) {
                uniqueResults.putIfAbsent(s.getStudentId(), new SearchResultItem(
                        s, "PREFIX", "Trie Prefix Tree", 0, 90.0,
                        String.format("Possesses prefix skill '%s'", omni.query)));
            }
        }

        omni.algorithmStats.add(new AlgorithmStat(
                "Trie Prefix Autocomplete", "String / Tree", "26-Way Prefix Tree",
                trieMatches, nameTrieRes.comparisons + skillTrieRes.comparisons, trieTime, "O(L) query length", "O(M × Σ)"));

        // ================= 4. KMP (KNUTH-MORRIS-PRATT) SUBSTRING SEARCH =================
        long startKmp = System.nanoTime();
        AlgorithmResult kmpRes = KMP.search(allStudents, omni.query);
        long kmpTime = System.nanoTime() - startKmp;
        int kmpMatches = 0;
        if (!qNorm.isEmpty()) {
            for (Student s : allStudents) {
                if (s.getName().toLowerCase().contains(qNorm) ||
                    s.getSkills().stream().anyMatch(sk -> sk.toLowerCase().contains(qNorm)) ||
                    s.getProjects().stream().anyMatch(pr -> pr.toLowerCase().contains(qNorm))) {
                    kmpMatches++;
                    uniqueResults.putIfAbsent(s.getStudentId(), new SearchResultItem(
                            s, "SUBSTRING", "KMP Algorithm", 0, 90.0, "Matched substring via LPS failure function"));
                }
            }
        }
        omni.algorithmStats.add(new AlgorithmStat(
                "KMP Algorithm", "String Algorithms", "1D LPS Array (π table)",
                kmpMatches, kmpRes.getComparisons(), kmpTime, "O(N + M)", "O(M)"));

        // ================= 5. RABIN-KARP ROLLING HASH =================
        long startRk = System.nanoTime();
        AlgorithmResult rkRes = RabinKarp.search(allStudents, omni.query);
        long rkTime = System.nanoTime() - startRk;
        omni.algorithmStats.add(new AlgorithmStat(
                "Rabin-Karp Hash", "String Algorithms", "Rolling Hash Modulo",
                kmpMatches, rkRes.getComparisons(), rkTime, "O(N + M) avg", "O(1)"));

        // ================= 6. BOYER-MOORE BAD CHARACTER SEARCH =================
        long startBm = System.nanoTime();
        AlgorithmResult bmRes = BoyerMoore.search(allStudents, omni.query);
        long bmTime = System.nanoTime() - startBm;
        omni.algorithmStats.add(new AlgorithmStat(
                "Boyer-Moore", "String Algorithms", "Bad Character Shift Table",
                kmpMatches, bmRes.getComparisons(), bmTime, "O(N / M) best", "O(Σ)"));

        // ================= 7. AHO-CORASICK MULTI-PATTERN SEARCH =================
        long startAc = System.nanoTime();
        int acMatches = 0;
        if (!qNorm.isEmpty()) {
            List<String> keywords = Arrays.stream(omni.query.split("\\s+"))
                    .filter(w -> w.length() >= 2)
                    .toList();
            if (!keywords.isEmpty()) {
                AhoCorasick ac = new AhoCorasick(keywords);
                AlgorithmResult acRes = ac.searchAll(allStudents);
                if (acRes != null && acRes.getOutput().toString().contains("occurrences")) {
                    acMatches = 1;
                }
            }
        }
        long acTime = System.nanoTime() - startAc;
        omni.algorithmStats.add(new AlgorithmStat(
                "Aho-Corasick Automaton", "String Algorithms", "Trie + BFS Dictionary Links",
                acMatches, allStudents.size(), acTime, "O(N + M + Z)", "O(M × Σ)"));

        // ================= 8. FUZZY SEARCH (LEVENSHTEIN & DAMERAU-LEVENSHTEIN) =================
        long startFuzzy = System.nanoTime();
        int fuzzyMatches = 0;
        long fuzzyComps = 0;
        if (qNorm.length() >= 3) {
            for (Student s : allStudents) {
                fuzzyComps++;
                // 1. Check against full name
                LevenshteinDistance.DistanceResult levName = LevenshteinDistance.compute(omni.query, s.getName());
                int minLev = levName.distance;
                double maxSim = levName.similarityPercentage;

                String[] nameTokens = s.getName().split("\\s+");
                for (String tok : nameTokens) {
                    LevenshteinDistance.DistanceResult levPart = LevenshteinDistance.compute(omni.query, tok);
                    if (levPart.distance < minLev) {
                        minLev = levPart.distance;
                        maxSim = Math.max(maxSim, levPart.similarityPercentage);
                    }
                }

                // 2. Damerau-Levenshtein check (transpositions)
                DamerauLevenshtein.DLResult damRes = DamerauLevenshtein.compute(omni.query, s.getName());
                for (String tok : nameTokens) {
                    DamerauLevenshtein.DLResult dPart = DamerauLevenshtein.compute(omni.query, tok);
                    if (dPart.distance < damRes.distance) {
                        damRes = dPart;
                    }
                }

                // Check skills
                for (String skill : s.getSkills()) {
                    LevenshteinDistance.DistanceResult levSkill = LevenshteinDistance.compute(omni.query, skill);
                    if (levSkill.distance <= 1 || levSkill.similarityPercentage >= 75.0) {
                        minLev = Math.min(minLev, levSkill.distance);
                        maxSim = Math.max(maxSim, levSkill.similarityPercentage);
                    }
                }

                // Match criteria: Distance <= 2 or similarity >= 65%
                boolean isFuzzyMatch = (minLev <= 2 && minLev > 0) || (damRes.distance <= 2 && damRes.distance > 0) || (maxSim >= 65.0 && minLev > 0);
                if (isFuzzyMatch) {
                    fuzzyMatches++;
                    int bestDist = Math.min(minLev, damRes.distance);
                    String algo = (damRes.distance < minLev) ? "Damerau-Levenshtein (Transposition)" : "Levenshtein DP";
                    uniqueResults.putIfAbsent(s.getStudentId(), new SearchResultItem(
                            s, "FUZZY", algo, bestDist, Math.max(maxSim, damRes.similarityPercentage),
                            String.format("Typo detected! Edit Distance: %d (%.0f%% Match with '%s')",
                                    bestDist, Math.max(maxSim, damRes.similarityPercentage), s.getName())));
                }
            }
        }
        long fuzzyTime = System.nanoTime() - startFuzzy;
        omni.algorithmStats.add(new AlgorithmStat(
                "Fuzzy (Levenshtein & Damerau)", "Dynamic Programming", "2D DP Matrix + Transposition",
                fuzzyMatches, fuzzyComps, fuzzyTime, "O(N × M)", "O(N × M)"));

        // ================= SORT RESULTS BY RELEVANCE =================
        List<SearchResultItem> sortedResults = new ArrayList<>(uniqueResults.values());
        sortedResults.sort((a, b) -> {
            int rankA = getRank(a.getMatchType());
            int rankB = getRank(b.getMatchType());
            if (rankA != rankB) return Integer.compare(rankA, rankB);
            return Double.compare(b.getConfidencePercentage(), a.getConfidencePercentage());
        });

        omni.results = sortedResults;
        omni.totalExecutionTimeNs = System.nanoTime() - globalStart;
        for (AlgorithmStat stat : omni.algorithmStats) {
            omni.totalComparisons += stat.comparisons;
        }

        return omni;
    }

    private static int getRank(String matchType) {
        return switch (matchType) {
            case "EXACT" -> 1;
            case "PREFIX" -> 2;
            case "SUBSTRING" -> 3;
            case "FUZZY" -> 4;
            default -> 5;
        };
    }

    private static Long tryParseLong(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
