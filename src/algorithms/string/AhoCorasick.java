package algorithms.string;

import datastructures.CustomQueue;
import model.AlgorithmResult;
import model.Student;
import java.util.*;

/**
 * Manual implementation of the Aho-Corasick Automaton for simultaneous
 * multi-pattern keyword searching across all student records.
 */
public class AhoCorasick {

    public static class Node {
        Map<Character, Node> children = new HashMap<>();
        Node fail = null;
        List<String> output = new ArrayList<>();
    }

    public static class MultiPatternMatch {
        public String pattern;
        public Student student;
        public String field;
        public int position;

        public MultiPatternMatch(String pattern, Student student, String field, int position) {
            this.pattern = pattern;
            this.student = student;
            this.field = field;
            this.position = position;
        }

        @Override
        public String toString() {
            return String.format("['%s' in %s (%s)]", pattern, student.getName(), field);
        }
    }

    private Node root;
    private List<String> patterns;

    public AhoCorasick(List<String> patterns) {
        this.root = new Node();
        this.patterns = patterns != null ? patterns : new ArrayList<>();
        buildAutomaton();
    }

    private void buildAutomaton() {
        // 1. Build standard Trie
        for (String pattern : patterns) {
            if (pattern == null || pattern.isEmpty()) continue;
            String norm = pattern.toLowerCase();
            Node curr = root;
            for (char ch : norm.toCharArray()) {
                curr.children.putIfAbsent(ch, new Node());
                curr = curr.children.get(ch);
            }
            curr.output.add(pattern);
        }

        // 2. Build Failure and Output Links using BFS CustomQueue
        CustomQueue<Node> queue = new CustomQueue<>();
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.enqueue(child);
        }

        while (!queue.isEmpty()) {
            Node curr = queue.dequeue();

            for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();

                Node f = curr.fail;
                while (f != null && !f.children.containsKey(ch)) {
                    f = f.fail;
                }

                if (f != null) {
                    child.fail = f.children.get(ch);
                } else {
                    child.fail = root;
                }

                if (child.fail != null) {
                    child.output.addAll(child.fail.output);
                }

                queue.enqueue(child);
            }
        }
    }

    public AlgorithmResult searchAll(List<Student> students) {
        long start = System.nanoTime();
        long comparisons = 0;
        List<MultiPatternMatch> matches = new ArrayList<>();
        Map<String, Set<Long>> patternToStudentIds = new HashMap<>();
        List<String> steps = new ArrayList<>();

        steps.add(String.format("Initialized Aho-Corasick automaton with %d target keywords: %s",
                patterns.size(), patterns));

        for (String p : patterns) {
            patternToStudentIds.put(p, new HashSet<>());
        }

        for (Student s : students) {
            searchFieldText(s.getName(), "Name", s, matches, patternToStudentIds, comparisons);
            for (String skill : s.getSkills()) {
                searchFieldText(skill, "Skill", s, matches, patternToStudentIds, comparisons);
            }
            for (String proj : s.getProjects()) {
                searchFieldText(proj, "Project", s, patternToStudentIds, comparisons, matches);
            }
        }

        long duration = System.nanoTime() - start;

        for (String p : patterns) {
            steps.add(String.format("Keyword '%s': matched across %d distinct student profiles.",
                    p, patternToStudentIds.get(p).size()));
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Aho-Corasick Automaton");
        result.setCategory("String Algorithms (Multi-Pattern)");
        result.setPurpose("Simultaneously scan student records for all target technology keywords in linear time");
        result.setInputSize(students.size());
        result.setDataStructureUsed("Trie Automaton with BFS Failure/Dictionary Links");
        result.setOutput(String.format("%d total keyword occurrences matched across %d students",
                matches.size(), students.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(comparisons);
        result.setIterations(students.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(N + M + Z) where N=text length, M=patterns length, Z=matches");
        result.setSpaceComplexity("O(M × Σ) for automaton transitions");
        result.setExplanation(String.format("Scanned all student names, skills, and projects in a single pass using finite state transitions. Matches: %d.", matches.size()));
        result.setSteps(steps);

        return result;
    }

    private void searchFieldText(String text, String field, Student s,
                                 List<MultiPatternMatch> matches,
                                 Map<String, Set<Long>> patternToStudentIds,
                                 long comparisons) {
        if (text == null) return;
        String norm = text.toLowerCase();
        Node curr = root;

        for (int i = 0; i < norm.length(); i++) {
            char ch = norm.charAt(i);

            while (curr != null && !curr.children.containsKey(ch)) {
                curr = curr.fail;
            }

            if (curr == null) {
                curr = root;
            } else {
                curr = curr.children.get(ch);
            }

            if (curr != null && !curr.output.isEmpty()) {
                for (String pat : curr.output) {
                    matches.add(new MultiPatternMatch(pat, s, field, i - pat.length() + 1));
                    patternToStudentIds.get(pat).add(s.getStudentId());
                }
            }
        }
    }

    private void searchFieldText(String text, String field, Student s,
                                 Map<String, Set<Long>> patternToStudentIds,
                                 long comparisons,
                                 List<MultiPatternMatch> matches) {
        searchFieldText(text, field, s, matches, patternToStudentIds, comparisons);
    }
}
