package algorithms.parallel;

import algorithms.sorting.SortCriteria;
import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Parallel Merge Sort using Java's ForkJoinPool framework.
 * Recursively forks left and right subarray sorting into separate threads and joins them.
 * Measures Sequential vs Parallel runtime, speedup, work, and span.
 */
public class ParallelSort {

    private static final int THRESHOLD = 250; // Threshold to switch to sequential merge sort

    public static class ParallelSortTask extends RecursiveAction {
        private final Student[] array;
        private final Student[] temp;
        private final int left;
        private final int right;
        private final SortCriteria criteria;
        private final boolean ascending;

        public ParallelSortTask(Student[] array, Student[] temp, int left, int right,
                                SortCriteria criteria, boolean ascending) {
            this.array = array;
            this.temp = temp;
            this.left = left;
            this.right = right;
            this.criteria = criteria;
            this.ascending = ascending;
        }

        @Override
        protected void compute() {
            if (right - left <= THRESHOLD) {
                seqMergeSort(array, temp, left, right, criteria, ascending);
                return;
            }

            int mid = left + (right - left) / 2;
            ParallelSortTask leftTask = new ParallelSortTask(array, temp, left, mid, criteria, ascending);
            ParallelSortTask rightTask = new ParallelSortTask(array, temp, mid + 1, right, criteria, ascending);

            invokeAll(leftTask, rightTask);
            merge(array, temp, left, mid, right, criteria, ascending);
        }
    }

    private static void seqMergeSort(Student[] a, Student[] tmp, int l, int r,
                                     SortCriteria crit, boolean asc) {
        if (l < r) {
            int m = l + (r - l) / 2;
            seqMergeSort(a, tmp, l, m, crit, asc);
            seqMergeSort(a, tmp, m + 1, r, crit, asc);
            merge(a, tmp, l, m, r, crit, asc);
        }
    }

    private static void merge(Student[] a, Student[] tmp, int l, int m, int r,
                             SortCriteria crit, boolean asc) {
        for (int idx = l; idx <= r; idx++) {
            tmp[idx] = a[idx];
        }

        int i = l, j = m + 1, k = l;
        while (i <= m && j <= r) {
            if (SortCriteria.compareStudents(tmp[i], tmp[j], crit, asc) <= 0) {
                a[k++] = tmp[i++];
            } else {
                a[k++] = tmp[j++];
            }
        }
        while (i <= m) {
            a[k++] = tmp[i++];
        }
        while (j <= r) {
            a[k++] = tmp[j++];
        }
    }

    public static AlgorithmResult executeParallelSort(List<Student> students, SortCriteria criteria, boolean ascending) {
        int n = students.size();
        Student[] parArr = students.toArray(new Student[0]);
        Student[] parTmp = new Student[n];

        // 1. Sequential run
        Student[] seqArr = students.toArray(new Student[0]);
        Student[] seqTmp = new Student[n];
        long seqStart = System.nanoTime();
        seqMergeSort(seqArr, seqTmp, 0, n - 1, criteria, ascending);
        long seqDuration = System.nanoTime() - seqStart;

        // 2. Parallel run
        ForkJoinPool pool = new ForkJoinPool();
        long parStart = System.nanoTime();
        pool.invoke(new ParallelSortTask(parArr, parTmp, 0, n - 1, criteria, ascending));
        long parDuration = System.nanoTime() - parStart;
        pool.shutdown();

        double speedup = (parDuration > 0) ? (double) seqDuration / parDuration : 1.0;
        int p = Runtime.getRuntime().availableProcessors();
        long work = (long) (n * (Math.log(n + 1) / Math.log(2)));
        long span = (long) Math.pow(Math.log(n + 1) / Math.log(2), 2);

        List<Student> sortedList = new ArrayList<>();
        for (Student s : parArr) sortedList.add(s);

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Record Count: %,d | Field: %s (%s)", n, criteria.name(), ascending ? "ASC" : "DESC"));
        steps.add(String.format("Sequential MergeSort Execution Time: %.4f ms", seqDuration / 1_000_000.0));
        steps.add(String.format("Parallel ForkJoin MergeSort Execution Time: %.4f ms", parDuration / 1_000_000.0));
        steps.add(String.format("Empirical Speedup across %d worker threads: %.2fx", p, speedup));
        steps.add(String.format("Work T1: %,d operations | Span T_inf: %,d operations | Theoretical Max Parallelism: %.2f",
                work, span, (double) work / Math.max(1, span)));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Parallel ForkJoin MergeSort");
        result.setCategory("Parallel Algorithms");
        result.setPurpose(String.format("Concurrent divide-and-conquer student sorting across multi-core processors by %s", criteria.name()));
        result.setInputSize(n);
        result.setDataStructureUsed("ForkJoinPool RecursiveAction & Memory Buffers");
        result.setOutput(sortedList);
        result.setExecutionTimeNs(parDuration);
        result.setComparisons(work);
        result.setIterations(n);
        result.setSwaps(work);
        result.setRecursionCount(n);
        result.setTimeComplexity("Work: O(n log n) | Span: O(log² n)");
        result.setSpaceComplexity("O(n) Merge Buffers");
        result.setExplanation(String.format("ForkJoin parallel merge sort divided work over %d available CPU threads achieving %.2fx speedup.", p, speedup));
        result.setSteps(steps);

        return result;
    }
}
