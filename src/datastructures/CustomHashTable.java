package datastructures;

import model.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Hash Table implementing both Separate Chaining and Open Addressing options.
 * Indexes Student records by Student ID, Roll Number, or Email with detailed collision tracking.
 */
public class CustomHashTable implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class HashEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        public String key;
        public Student student;
        public HashEntry next; // For Separate Chaining

        public HashEntry(String key, Student student) {
            this.key = key;
            this.student = student;
            this.next = null;
        }
    }

    public static class HashLookupResult {
        public Student student;
        public int hashValue;
        public int bucketIndex;
        public int collisionCount;
        public int stepsTaken;
        public boolean found;
        public String complexity;

        @Override
        public String toString() {
            return String.format("Lookup Result: Found=%b | Hash=%d | Bucket=%d | Collisions In Bucket=%d | Steps=%d | Complexity=%s",
                    found, hashValue, bucketIndex, collisionCount, stepsTaken, complexity);
        }
    }

    private int capacity;
    private int size;
    private HashEntry[] chainBuckets;
    private int totalCollisions;

    // Open Addressing table
    private HashEntry[] openTable;
    private static final HashEntry DELETED = new HashEntry("__DELETED__", null);

    public CustomHashTable(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.chainBuckets = new HashEntry[capacity];
        this.openTable = new HashEntry[capacity];
        this.totalCollisions = 0;
    }

    public CustomHashTable() {
        this(67); // Default prime capacity
    }

    /**
     * Polynomial rolling hash function for String keys.
     */
    public int computeHash(String key) {
        if (key == null) return 0;
        long hash = 0;
        long p = 31;
        long m = 1_000_000_009;
        long pPow = 1;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash + (key.charAt(i) - ' ' + 1) * pPow) % m;
            pPow = (pPow * p) % m;
        }
        return (int) Math.abs(hash);
    }

    public int getBucketIndex(int hashValue) {
        return Math.abs(hashValue) % capacity;
    }

    // ================= SEPARATE CHAINING =================

    public void insertChaining(String key, Student student) {
        if (key == null || student == null) return;
        int hash = computeHash(key);
        int index = getBucketIndex(hash);

        HashEntry head = chainBuckets[index];
        if (head == null) {
            chainBuckets[index] = new HashEntry(key, student);
            size++;
            return;
        }

        // Collision detected
        totalCollisions++;
        HashEntry curr = head;
        while (curr != null) {
            if (curr.key.equals(key)) {
                curr.student = student; // Update existing
                return;
            }
            if (curr.next == null) break;
            curr = curr.next;
        }
        curr.next = new HashEntry(key, student);
        size++;

        // Resize if load factor exceeds 0.75
        if ((double) size / capacity > 0.75) {
            rehash();
        }
    }

    public HashLookupResult searchChaining(String key) {
        HashLookupResult result = new HashLookupResult();
        if (key == null) {
            result.found = false;
            return result;
        }
        int hash = computeHash(key);
        int index = getBucketIndex(hash);

        result.hashValue = hash;
        result.bucketIndex = index;
        result.complexity = "O(1) Avg, O(n) Worst";

        HashEntry curr = chainBuckets[index];
        int bucketDepth = 0;
        int steps = 0;

        // Calculate bucket depth
        HashEntry temp = curr;
        while (temp != null) {
            bucketDepth++;
            temp = temp.next;
        }
        result.collisionCount = Math.max(0, bucketDepth - 1);

        while (curr != null) {
            steps++;
            if (curr.key.equals(key)) {
                result.student = curr.student;
                result.found = true;
                result.stepsTaken = steps;
                return result;
            }
            curr = curr.next;
        }

        result.found = false;
        result.stepsTaken = steps;
        return result;
    }

    public boolean deleteChaining(String key) {
        if (key == null) return false;
        int hash = computeHash(key);
        int index = getBucketIndex(hash);

        HashEntry curr = chainBuckets[index];
        HashEntry prev = null;

        while (curr != null) {
            if (curr.key.equals(key)) {
                if (prev == null) {
                    chainBuckets[index] = curr.next;
                } else {
                    prev.next = curr.next;
                }
                size--;
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    private void rehash() {
        int oldCap = capacity;
        capacity = nextPrime(capacity * 2);
        HashEntry[] oldBuckets = chainBuckets;
        chainBuckets = new HashEntry[capacity];
        size = 0;
        totalCollisions = 0;

        for (int i = 0; i < oldCap; i++) {
            HashEntry curr = oldBuckets[i];
            while (curr != null) {
                insertChaining(curr.key, curr.student);
                curr = curr.next;
            }
        }
    }

    // ================= OPEN ADDRESSING (Linear Probing) =================

    public boolean insertOpenAddressing(String key, Student student) {
        if (key == null || student == null) return false;
        int hash = computeHash(key);
        int index = getBucketIndex(hash);

        for (int i = 0; i < capacity; i++) {
            int probeIdx = (index + i) % capacity;
            if (openTable[probeIdx] == null || openTable[probeIdx] == DELETED) {
                openTable[probeIdx] = new HashEntry(key, student);
                return true;
            } else if (openTable[probeIdx].key.equals(key)) {
                openTable[probeIdx].student = student;
                return true;
            }
        }
        return false; // Table full
    }

    public HashLookupResult searchOpenAddressing(String key) {
        HashLookupResult result = new HashLookupResult();
        if (key == null) {
            result.found = false;
            return result;
        }
        int hash = computeHash(key);
        int index = getBucketIndex(hash);
        result.hashValue = hash;
        result.bucketIndex = index;
        result.complexity = "O(1) Avg, O(n) Worst";

        int steps = 0;
        for (int i = 0; i < capacity; i++) {
            steps++;
            int probeIdx = (index + i) % capacity;
            if (openTable[probeIdx] == null) {
                break;
            }
            if (openTable[probeIdx] != DELETED && openTable[probeIdx].key.equals(key)) {
                result.student = openTable[probeIdx].student;
                result.found = true;
                result.stepsTaken = steps;
                result.collisionCount = i;
                return result;
            }
        }
        result.found = false;
        result.stepsTaken = steps;
        return result;
    }

    private static int nextPrime(int n) {
        while (true) {
            n++;
            if (isPrime(n)) return n;
        }
    }

    private static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            HashEntry curr = chainBuckets[i];
            while (curr != null) {
                list.add(curr.student);
                curr = curr.next;
            }
        }
        return list;
    }

    public int getCapacity() { return capacity; }
    public int getSize() { return size; }
    public int getTotalCollisions() { return totalCollisions; }
    public double getLoadFactor() { return (double) size / capacity; }

    public String getTableStatistics() {
        int occupiedBuckets = 0;
        int maxChainLength = 0;
        for (int i = 0; i < capacity; i++) {
            int chainLen = 0;
            HashEntry curr = chainBuckets[i];
            while (curr != null) {
                chainLen++;
                curr = curr.next;
            }
            if (chainLen > 0) occupiedBuckets++;
            if (chainLen > maxChainLength) maxChainLength = chainLen;
        }
        return String.format("HashTable Stats: Capacity=%d | Entries=%d | Load Factor=%.2f | Occupied Buckets=%d | Max Chain Depth=%d | Total Collisions=%d",
                capacity, size, getLoadFactor(), occupiedBuckets, maxChainLength, totalCollisions);
    }
}
