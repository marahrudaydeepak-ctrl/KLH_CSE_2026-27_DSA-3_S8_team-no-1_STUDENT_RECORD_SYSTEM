package algorithms.parallel;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Multi-threaded Parallel Search over student records using an ExecutorService thread pool.
 * Divides the dataset into contiguous chunks across available CPU cores.
 * Compares Sequential vs Parallel execution time, speedup, work, and span.
 */
public class ParallelSearch {

    public static class ParallelSearchResult {
        public Student foundStudent;
        public long seqTimeNs;
        public long parTimeNs;
        public double speedup;
        public int threadsUsed;
        public long work;
        public long span;
    }

    public static ParallelSearchResult search(List<Student> students, long targetId, int numThreads) {
        ParallelSearchResult result = new ParallelSearchResult();
        result.threadsUsed = numThreads;
        int n = students.size();

        // 1. Sequential execution baseline
        long seqStart = System.nanoTime();
        Student seqFound = null;
        for (int i = 0; i < n; i++) {
            if (students.get(i).getStudentId() == targetId) {
                seqFound = students.get(i);
                break;
            }
        }
        result.seqTimeNs = System.nanoTime() - seqStart;

        // 2. Parallel execution
        long parStart = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int chunkSize = Math.max(1, (n + numThreads - 1) / numThreads);
        AtomicBoolean foundFlag = new AtomicBoolean(false);
        BlockingQueue<Student> foundQueue = new LinkedBlockingQueue<>();

        List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < numThreads; t++) {
            final int startIdx = t * chunkSize;
            final int endIdx = Math.min(n, startIdx + chunkSize);

            if (startIdx >= n) break;

            futures.add(executor.submit(() -> {
                for (int i = startIdx; i < endIdx && !foundFlag.get(); i++) {
                    if (students.get(i).getStudentId() == targetId) {
                        foundFlag.set(true);
                        foundQueue.offer(students.get(i));
                        break;
                    }
                }
            }));
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception ignored) {}
        }
        executor.shutdown();

        result.parTimeNs = System.nanoTime() - parStart;
        result.foundStudent = foundQueue.poll();
        if (result.foundStudent == null) {
            result.foundStudent = seqFound;
        }

        result.speedup = (result.parTimeNs > 0) ? (double) result.seqTimeNs / result.parTimeNs : 1.0;
        result.work = n; // Total work
        result.span = n / numThreads; // Theoretical critical path span

        return result;
    }

    public static AlgorithmResult executeParallelSearch(List<Student> students, long targetId) {
        int threads = Runtime.getRuntime().availableProcessors();
        ParallelSearchResult res = search(students, targetId, threads);

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Dataset Size: %,d records | Active Processor Cores: %d", students.size(), res.threadsUsed));
        steps.add(String.format("Sequential Scan Execution Time: %.4f ms", res.seqTimeNs / 1_000_000.0));
        steps.add(String.format("Parallel Chunk Scan Execution Time: %.4f ms", res.parTimeNs / 1_000_000.0));
        steps.add(String.format("Empirical Speedup: %.2fx", res.speedup));
        steps.add(String.format("Work (T1): %,d operations | Span (T_inf): %,d operations | Parallelism: %.2f",
                res.work, res.span, (double) res.work / Math.max(1, res.span)));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Parallel Multi-Threaded Search");
        result.setCategory("Parallel Algorithms");
        result.setPurpose("Concurrent partitioning of student dataset search across CPU cores");
        result.setInputSize(students.size());
        result.setDataStructureUsed("ExecutorService Fixed Thread Pool & Sub-Array Slices");
        result.setOutput(res.foundStudent != null ? res.foundStudent.toString() : "Not Found");
        result.setExecutionTimeNs(res.parTimeNs);
        result.setComparisons(students.size());
        result.setIterations(students.size() / res.threadsUsed);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity(String.format("Work: O(n) | Span: O(n / P) where P=%d cores", res.threadsUsed));
        result.setSpaceComplexity("O(P) Thread Contexts");
        result.setExplanation(String.format("Parallel search completed across %d threads achieving %.2fx empirical speedup.", res.threadsUsed, res.speedup));
        result.setSteps(steps);

        return result;
    }
}
