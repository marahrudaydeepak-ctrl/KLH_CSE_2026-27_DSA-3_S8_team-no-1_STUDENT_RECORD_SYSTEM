package datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom Trie (Prefix Tree) supporting case-insensitive prefix search and autocomplete
 * for student names, skills, and courses.
 */
public class Trie implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class TrieNode implements Serializable {
        private static final long serialVersionUID = 1L;
        public Map<Character, TrieNode> children;
        public boolean isEndOfWord;
        public List<Object> associatedData;

        public TrieNode() {
            this.children = new HashMap<>();
            this.isEndOfWord = false;
            this.associatedData = new ArrayList<>();
        }
    }

    public static class TrieSearchResult {
        public String prefix;
        public List<String> matchedWords;
        public List<Object> matchedData;
        public int comparisons;
        public long executionTimeNs;

        public TrieSearchResult(String prefix) {
            this.prefix = prefix;
            this.matchedWords = new ArrayList<>();
            this.matchedData = new ArrayList<>();
        }
    }

    private TrieNode root;
    private int wordCount;

    public Trie() {
        this.root = new TrieNode();
        this.wordCount = 0;
    }

    public void insert(String word, Object payload) {
        if (word == null || word.trim().isEmpty()) return;
        String normalized = word.trim().toLowerCase();
        TrieNode curr = root;

        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            curr.children.putIfAbsent(ch, new TrieNode());
            curr = curr.children.get(ch);
        }

        if (!curr.isEndOfWord) {
            curr.isEndOfWord = true;
            wordCount++;
        }
        if (payload != null) {
            curr.associatedData.add(payload);
        }
    }

    public TrieSearchResult prefixSearch(String prefix) {
        TrieSearchResult result = new TrieSearchResult(prefix);
        if (prefix == null) return result;

        long start = System.nanoTime();
        String normalized = prefix.trim().toLowerCase();
        TrieNode curr = root;
        int steps = 0;

        for (int i = 0; i < normalized.length(); i++) {
            steps++;
            char ch = normalized.charAt(i);
            if (!curr.children.containsKey(ch)) {
                result.comparisons = steps;
                result.executionTimeNs = System.nanoTime() - start;
                return result; // No matches found
            }
            curr = curr.children.get(ch);
        }

        // Collect all descendant words
        collectWords(curr, new StringBuilder(prefix.trim()), result);
        result.comparisons = steps;
        result.executionTimeNs = System.nanoTime() - start;
        return result;
    }

    private void collectWords(TrieNode node, StringBuilder currentWord, TrieSearchResult result) {
        if (node.isEndOfWord) {
            result.matchedWords.add(currentWord.toString());
            result.matchedData.addAll(node.associatedData);
        }

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            currentWord.append(entry.getKey());
            collectWords(entry.getValue(), currentWord, result);
            currentWord.deleteCharAt(currentWord.length() - 1);
        }
    }

    public boolean containsExact(String word) {
        if (word == null) return false;
        String normalized = word.trim().toLowerCase();
        TrieNode curr = root;
        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            if (!curr.children.containsKey(ch)) return false;
            curr = curr.children.get(ch);
        }
        return curr.isEndOfWord;
    }

    public int getWordCount() { return wordCount; }
    public TrieNode getRoot() { return root; }
}
