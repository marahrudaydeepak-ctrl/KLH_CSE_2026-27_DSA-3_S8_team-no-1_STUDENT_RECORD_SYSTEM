package services;

/**
 * Intelligent Algorithm Selection Engine.
 * Recommends or automatically dispatches optimal algorithms and data structures
 * tailored to input characteristics, size, query topology, and problem domain.
 */
public class AlgorithmSelectionEngine {

    public enum ProblemType {
        ID_SEARCH,
        PREFIX_AUTOCOMPLETE,
        SUBSTRING_SEARCH,
        MULTI_KEYWORD_SEARCH,
        TYPO_FUZZY_SEARCH,
        DATA_SORTING,
        ACADEMIC_RANKING,
        COURSE_ALLOCATION,
        ACTIVITY_SCHEDULING,
        RANDOM_SAMPLING,
        CYCLE_DETECTION
    }

    public static class Recommendation {
        public String recommendedAlgorithm;
        public String dataStructure;
        public String rationale;
        public String timeComplexity;
        public String spaceComplexity;

        public Recommendation(String recommendedAlgorithm, String dataStructure, String rationale,
                              String timeComplexity, String spaceComplexity) {
            this.recommendedAlgorithm = recommendedAlgorithm;
            this.dataStructure = dataStructure;
            this.rationale = rationale;
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
        }

        @Override
        public String toString() {
            return String.format("Recommended: %s (%s)\n  Rationale: %s\n  Complexity: Time %s | Space %s",
                    recommendedAlgorithm, dataStructure, rationale, timeComplexity, spaceComplexity);
        }
    }

    public static Recommendation selectAlgorithm(ProblemType problem, int inputSize, boolean isDataSorted) {
        switch (problem) {
            case ID_SEARCH:
                if (isDataSorted && inputSize >= 50) {
                    return new Recommendation("Binary Search", "Sorted Array",
                            "Data is pre-sorted; binary search halves intervals achieving O(log n).", "O(log n)", "O(1)");
                } else if (inputSize > 500) {
                    return new Recommendation("Hash Lookup / AVL Search", "CustomHashTable / AVL Tree",
                            "Large dataset benefits from O(1) hashing or guaranteed O(log n) balanced BST.", "O(1) avg / O(log n)", "O(n)");
                } else {
                    return new Recommendation("Linear Search", "List / Array",
                            "Small or unsorted dataset where pre-sorting overhead exceeds simple O(n) scan.", "O(n)", "O(1)");
                }

            case PREFIX_AUTOCOMPLETE:
                return new Recommendation("Trie Prefix Search", "Trie (Prefix Tree)",
                        "Trie enables character-by-character branch traversal independent of student database size N.", "O(L)", "O(Words × L)");

            case SUBSTRING_SEARCH:
                if (inputSize > 1000) {
                    return new Recommendation("Boyer-Moore (Bad Character)", "Bad Character Jump Table",
                        "Sublinear average time scanning right-to-left with maximal skip distance.", "O(n / m) avg", "O(Σ)");
                } else {
                    return new Recommendation("Knuth-Morris-Pratt (KMP)", "LPS Array",
                        "Avoids redundant backtrack comparisons using prefix-suffix table.", "O(n + m)", "O(m)");
                }

            case MULTI_KEYWORD_SEARCH:
                return new Recommendation("Aho-Corasick Automaton", "Trie Automaton with Failure Links",
                        "Processes all keyword patterns simultaneously in a single linear pass over the text.", "O(n + sum(m) + z)", "O(sum(m) × Σ)");

            case TYPO_FUZZY_SEARCH:
                return new Recommendation("Damerau-Levenshtein Distance", "2D DP Table with Transpositions",
                        "Detects phonetic typos, character omissions, insertions, and letter transpositions.", "O(m × n)", "O(m × n)");

            case DATA_SORTING:
                if (inputSize <= 30) {
                    return new Recommendation("Insertion Sort", "In-Place Array",
                            "Minimal overhead and fastest execution for small and nearly-sorted collections.", "O(n) best / O(n²)", "O(1)");
                } else if (inputSize <= 2000) {
                    return new Recommendation("Merge Sort", "Array Buffer",
                            "Guaranteed stable O(n log n) divide-and-conquer performance.", "O(n log n)", "O(n)");
                } else {
                    return new Recommendation("Parallel ForkJoin MergeSort", "ForkJoinPool Multi-Core Buffers",
                            "Utilizes multi-core parallel speedup to sort large batches of student records.", "Work: O(n log n) | Span: O(log² n)", "O(n)");
                }

            case ACADEMIC_RANKING:
                return new Recommendation("Merge Sort / Heap Sort", "Binary Max-Heap / Array",
                        "In-place or stable O(n log n) sort suitable for precise student grade rank orders.", "O(n log n)", "O(1) to O(n)");

            case COURSE_ALLOCATION:
                return new Recommendation("Dinic's Maximum Network Flow", "Capacitated Residual Flow Network",
                        "Maximizes total feasible student-course allocations while honoring seat quotas.", "O(V² × E)", "O(V + E)");

            case ACTIVITY_SCHEDULING:
                return new Recommendation("Weighted Interval Scheduling (DP)", "Memoized Predecessor Table",
                        "Solves optimal non-overlapping event selection maximizing academic priority points.", "O(n log n)", "O(n)");

            case RANDOM_SAMPLING:
                return new Recommendation("Reservoir Sampling", "Reservoir Buffer",
                        "Selects unbiased uniform sample of size K in a single pass without loading all data.", "O(n)", "O(k)");

            case CYCLE_DETECTION:
                return new Recommendation("Floyd's Tortoise and Hare", "Singly Linked List",
                        "Two-pointer cycle detection in linear time without extra memory.", "O(n)", "O(1)");

            default:
                return new Recommendation("Linear Search", "ArrayList", "Default baseline strategy.", "O(n)", "O(1)");
        }
    }
}
