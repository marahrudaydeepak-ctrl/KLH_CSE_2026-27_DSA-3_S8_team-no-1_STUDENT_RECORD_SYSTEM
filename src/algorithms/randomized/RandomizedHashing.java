package algorithms.randomized;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manual implementation of Universal / Randomized Hashing for Student records.
 * Uses a 2-universal hash family: h_{a,b}(k) = ((a * k + b) mod p) mod m
 * with randomly chosen parameters (a, b).
 */
public class RandomizedHashing {

    private static final long LARGE_PRIME = 1_000_000_007L;

    public static class UniversalHashStats {
        public long a;
        public long b;
        public int numBuckets;
        public int totalCollisions;
        public int maxBucketDepth;
        public int[] bucketCounts;

        public UniversalHashStats(long a, long b, int m) {
            this.a = a;
            this.b = b;
            this.numBuckets = m;
            this.bucketCounts = new int[m];
        }
    }

    public static UniversalHashStats evaluate(List<Student> students, int numBuckets, long seed) {
        Random rand = (seed != 0) ? new Random(seed) : new Random();
        long a = 1 + Math.abs(rand.nextLong()) % (LARGE_PRIME - 1);
        long b = Math.abs(rand.nextLong()) % LARGE_PRIME;

        UniversalHashStats stats = new UniversalHashStats(a, b, numBuckets);

        for (Student s : students) {
            long k = s.getStudentId();
            int bucket = (int) (Math.abs((a * k + b) % LARGE_PRIME) % numBuckets);
            stats.bucketCounts[bucket]++;
        }

        for (int count : stats.bucketCounts) {
            if (count > 1) {
                stats.totalCollisions += (count - 1);
            }
            if (count > stats.maxBucketDepth) {
                stats.maxBucketDepth = count;
            }
        }

        return stats;
    }

    public static AlgorithmResult executeRandomizedHashing(List<Student> students, int buckets, long seed) {
        long start = System.nanoTime();
        UniversalHashStats stats = evaluate(students, buckets, seed);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Drawn random hash coefficients from Universal Family: a = %d, b = %d (Prime p = %,d).",
                stats.a, stats.b, LARGE_PRIME));
        steps.add(String.format("Function: h(k) = ((%d × k + %d) mod %,d) mod %d.",
                stats.a, stats.b, LARGE_PRIME, buckets));
        steps.add(String.format("Hashed %d student IDs across %d buckets.", students.size(), buckets));
        steps.add(String.format("Resulting Collisions = %d | Max Chain Depth = %d.",
                stats.totalCollisions, stats.maxBucketDepth));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Randomized Universal Hashing");
        result.setCategory("Randomized Algorithms");
        result.setPurpose("Prevent adversarial hash clustering by sampling from a 2-universal hash family");
        result.setInputSize(students.size());
        result.setDataStructureUsed(String.format("Universal Hash Table [%d Buckets]", buckets));
        result.setOutput(String.format("Collisions: %d | Max Depth: %d", stats.totalCollisions, stats.maxBucketDepth));
        result.setExecutionTimeNs(duration);
        result.setComparisons(students.size());
        result.setIterations(students.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(1) Expected Lookup");
        result.setSpaceComplexity(String.format("O(M) for %d Buckets", buckets));
        result.setExplanation(String.format("Universal family guarantees collision probability <= 1/m = %.4f per pair.", 1.0 / buckets));
        result.setSteps(steps);

        return result;
    }
}
