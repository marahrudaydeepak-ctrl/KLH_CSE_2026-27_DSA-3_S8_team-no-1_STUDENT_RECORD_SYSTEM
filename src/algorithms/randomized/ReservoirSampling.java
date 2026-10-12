package algorithms.randomized;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manual implementation of Reservoir Sampling Algorithm (Algorithm R).
 * Application: Randomly selects an unbiased sample of K student records from an arbitrarily
 * large stream or dataset of size N in a single pass O(N) using O(K) space.
 */
public class ReservoirSampling {

    public static List<Student> sample(List<Student> stream, int k, long seed) {
        List<Student> reservoir = new ArrayList<>(k);
        int n = stream.size();
        if (n == 0 || k <= 0) return reservoir;

        // Fill initial reservoir with first k elements
        for (int i = 0; i < Math.min(k, n); i++) {
            reservoir.add(stream.get(i));
        }

        Random rand = (seed != 0) ? new Random(seed) : new Random();

        // Process remaining elements from index k to n - 1
        for (int i = k; i < n; i++) {
            int j = rand.nextInt(i + 1); // Random index from 0 to i
            if (j < k) {
                reservoir.set(j, stream.get(i));
            }
        }

        return reservoir;
    }

    public static AlgorithmResult executeReservoirSampling(List<Student> students, int k, long seed) {
        long start = System.nanoTime();
        List<Student> sampled = sample(students, k, seed);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Large input population size N = %,d records. Target sample K = %d.",
                students.size(), k));
        steps.add(String.format("Step 2: Filled reservoir buffer with first %d elements.", Math.min(k, students.size())));
        steps.add(String.format("Step 3: Streamed remaining %d items, replacing elements with probability k/(i+1).",
                Math.max(0, students.size() - k)));
        steps.add(String.format("Step 4: Unbiased reservoir sample formed (each student had exact probability %d/%,d).",
                k, students.size()));

        for (int i = 0; i < Math.min(5, sampled.size()); i++) {
            Student s = sampled.get(i);
            steps.add(String.format("  Sample [%d]: ID %d - %s (%s)", i + 1, s.getStudentId(), s.getName(), s.getDepartment()));
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Reservoir Sampling (Algorithm R)");
        result.setCategory("Randomized Algorithms");
        result.setPurpose(String.format("Uniformly sample K=%d students from population in a single pass", k));
        result.setInputSize(students.size());
        result.setDataStructureUsed(String.format("Reservoir Array Buffer of size K=%d", k));
        result.setOutput(String.format("Sampled %d students from %d records", sampled.size(), students.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(students.size());
        result.setIterations(students.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(N) Single-Pass Linear Time");
        result.setSpaceComplexity(String.format("O(K) Reservoir Buffer [K=%d]", k));
        result.setExplanation(String.format("Guaranteed each student record in the %d population has identical k/N probability of inclusion.", students.size()));
        result.setSteps(steps);

        return result;
    }
}
