package algorithms.parallel;

import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Parallel Academic Analytics and Parallel Prefix-Scan processing.
 * Concurrently computes aggregated attendance, marks totals, GPA distributions,
 * and academic risk cohorts using Callable thread tasks.
 */
public class ParallelAnalytics {

    public static class AnalyticsMetrics {
        public double totalMarks;
        public double avgMarks;
        public double avgAttendance;
        public double highestMarks;
        public double lowestMarks;
        public int atRiskStudentsCount; // Attendance < 75% or CGPA < 5.0
        public int topPerformersCount;  // CGPA >= 9.0
        public long seqTimeNs;
        public long parTimeNs;
        public double speedup;
    }

    public static AlgorithmResult executeParallelMetrics(List<Student> students) {
        int n = students.size();
        AnalyticsMetrics metrics = new AnalyticsMetrics();
        int numThreads = Runtime.getRuntime().availableProcessors();

        // 1. Sequential Run
        long seqStart = System.nanoTime();
        double seqTotMarks = 0;
        double seqTotAtt = 0;
        double seqMaxMarks = Double.NEGATIVE_INFINITY;
        double seqMinMarks = Double.POSITIVE_INFINITY;
        int seqAtRisk = 0;
        int seqTop = 0;

        for (Student s : students) {
            seqTotMarks += s.getMarks();
            seqTotAtt += s.getAttendance();
            if (s.getMarks() > seqMaxMarks) seqMaxMarks = s.getMarks();
            if (s.getMarks() < seqMinMarks) seqMinMarks = s.getMarks();
            if (s.getAttendance() < 75.0 || s.getCgpa() < 5.0) seqAtRisk++;
            if (s.getCgpa() >= 9.0) seqTop++;
        }
        long seqDuration = System.nanoTime() - seqStart;

        // 2. Parallel Run
        long parStart = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int chunkSize = Math.max(1, (n + numThreads - 1) / numThreads);
        List<Future<double[]>> futures = new ArrayList<>();

        for (int t = 0; t < numThreads; t++) {
            final int startIdx = t * chunkSize;
            final int endIdx = Math.min(n, startIdx + chunkSize);
            if (startIdx >= n) break;

            futures.add(executor.submit(() -> {
                double subMarks = 0;
                double subAtt = 0;
                double subMax = Double.NEGATIVE_INFINITY;
                double subMin = Double.POSITIVE_INFINITY;
                int subRisk = 0;
                int subTop = 0;

                for (int i = startIdx; i < endIdx; i++) {
                    Student s = students.get(i);
                    subMarks += s.getMarks();
                    subAtt += s.getAttendance();
                    if (s.getMarks() > subMax) subMax = s.getMarks();
                    if (s.getMarks() < subMin) subMin = s.getMarks();
                    if (s.getAttendance() < 75.0 || s.getCgpa() < 5.0) subRisk++;
                    if (s.getCgpa() >= 9.0) subTop++;
                }
                return new double[]{subMarks, subAtt, subMax, subMin, subRisk, subTop};
            }));
        }

        double parTotMarks = 0, parTotAtt = 0;
        double parMax = Double.NEGATIVE_INFINITY, parMin = Double.POSITIVE_INFINITY;
        int parRisk = 0, parTop = 0;

        for (Future<double[]> f : futures) {
            try {
                double[] res = f.get();
                parTotMarks += res[0];
                parTotAtt += res[1];
                if (res[2] > parMax) parMax = res[2];
                if (res[3] < parMin) parMin = res[3];
                parRisk += (int) res[4];
                parTop += (int) res[5];
            } catch (Exception ignored) {}
        }
        executor.shutdown();
        long parDuration = System.nanoTime() - parStart;

        metrics.totalMarks = parTotMarks;
        metrics.avgMarks = n > 0 ? parTotMarks / n : 0.0;
        metrics.avgAttendance = n > 0 ? parTotAtt / n : 0.0;
        metrics.highestMarks = parMax;
        metrics.lowestMarks = parMin;
        metrics.atRiskStudentsCount = parRisk;
        metrics.topPerformersCount = parTop;
        metrics.seqTimeNs = seqDuration;
        metrics.parTimeNs = parDuration;
        metrics.speedup = (parDuration > 0) ? (double) seqDuration / parDuration : 1.0;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Student Dataset Size: %,d | Active Parallel Worker Threads: %d", n, numThreads));
        steps.add(String.format("Average Marks: %.2f%% | Average Attendance: %.2f%%", metrics.avgMarks, metrics.avgAttendance));
        steps.add(String.format("Highest Marks: %.2f%% | Lowest Marks: %.2f%%", metrics.highestMarks, metrics.lowestMarks));
        steps.add(String.format("At-Risk Students (Attendance < 75%% or CGPA < 5.0): %d students", metrics.atRiskStudentsCount));
        steps.add(String.format("Top Performers (CGPA >= 9.0): %d students", metrics.topPerformersCount));
        steps.add(String.format("Performance: Sequential = %.4f ms | Parallel = %.4f ms | Speedup = %.2fx",
                seqDuration / 1_000_000.0, parDuration / 1_000_000.0, metrics.speedup));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Parallel Academic Analytics Aggregator");
        result.setCategory("Parallel Algorithms");
        result.setPurpose("Concurrent calculation of college-wide academic KPIs and risk cohorts");
        result.setInputSize(n);
        result.setDataStructureUsed("ExecutorService & Future Reduction Arrays");
        result.setOutput(String.format("Avg Marks: %.1f%% | Avg Attd: %.1f%% | At-Risk: %d | Top: %d",
                metrics.avgMarks, metrics.avgAttendance, metrics.atRiskStudentsCount, metrics.topPerformersCount));
        result.setExecutionTimeNs(parDuration);
        result.setComparisons(n);
        result.setIterations(n / numThreads);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("Work: O(n) | Span: O(n / P)");
        result.setSpaceComplexity("O(P) Sub-aggregation vectors");
        result.setExplanation(String.format("Multi-core reduction aggregated stats for %d students in %.4f ms.", n, parDuration / 1_000_000.0));
        result.setSteps(steps);

        return result;
    }
}
