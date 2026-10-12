package algorithms.dynamicprogramming;

import model.AcademicActivity;
import model.AlgorithmResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manual implementation of Weighted Interval Scheduling using Dynamic Programming.
 * Application: Maximizes total academic priority/weight of scheduled assignments,
 * exams, project milestones, and student consultations without time overlaps.
 */
public class WeightedIntervalScheduling {

    public static class ScheduleResult {
        public List<AcademicActivity> scheduledActivities;
        public int totalWeight;
        public int[] opt;
        public int[] p;

        public ScheduleResult() {
            this.scheduledActivities = new ArrayList<>();
        }
    }

    public static ScheduleResult solve(List<AcademicActivity> inputActivities) {
        List<AcademicActivity> activities = new ArrayList<>(inputActivities);
        // Sort by finish time
        Collections.sort(activities);
        int n = activities.size();

        // p[i] = latest non-overlapping interval before interval i (1-indexed)
        int[] p = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            p[i] = findLatestNonOverlapping(activities, i);
        }

        int[] opt = new int[n + 1];
        opt[0] = 0;

        for (int i = 1; i <= n; i++) {
            int includeWeight = activities.get(i - 1).getPriorityWeight() + opt[p[i]];
            int excludeWeight = opt[i - 1];
            opt[i] = Math.max(includeWeight, excludeWeight);
        }

        // Backtrack to find chosen activities
        ScheduleResult res = new ScheduleResult();
        res.totalWeight = opt[n];
        res.opt = opt;
        res.p = p;

        int curr = n;
        while (curr > 0) {
            int includeWeight = activities.get(curr - 1).getPriorityWeight() + opt[p[curr]];
            if (includeWeight >= opt[curr - 1]) {
                res.scheduledActivities.add(0, activities.get(curr - 1));
                curr = p[curr];
            } else {
                curr = curr - 1;
            }
        }

        return res;
    }

    private static int findLatestNonOverlapping(List<AcademicActivity> list, int index) {
        int targetStart = list.get(index - 1).getStartHour();
        int low = 0;
        int high = index - 2;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (list.get(mid).getEndHour() <= targetStart) {
                if (mid + 1 <= high && list.get(mid + 1).getEndHour() <= targetStart) {
                    low = mid + 1;
                } else {
                    return mid + 1; // 1-indexed
                }
            } else {
                high = mid - 1;
            }
        }
        return 0;
    }

    public static AlgorithmResult executeScheduling(List<AcademicActivity> activities) {
        long start = System.nanoTime();
        ScheduleResult res = solve(activities);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Input %d academic activities with varying priorities and time windows.", activities.size()));
        steps.add("Step 2: Sorted activities in ascending order of end times.");
        steps.add("Step 3: Computed predecessor array p[i] using binary search over finish times.");
        steps.add("Step 4: Evaluated Bellman equation OPT(i) = max(OPT(i-1), v_i + OPT(p(i))).");
        steps.add(String.format("Step 5: Identified %d mutually compatible activities yielding max weight %d pts.",
                res.scheduledActivities.size(), res.totalWeight));
        for (AcademicActivity act : res.scheduledActivities) {
            steps.add("  Chosen: " + act.toString());
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Weighted Interval Scheduling (DP)");
        result.setCategory("Dynamic Programming");
        result.setPurpose("Maximize total academic priority for non-overlapping student activities");
        result.setInputSize(activities.size());
        result.setDataStructureUsed("1D DP Table & Predecessor Pointer Array");
        result.setOutput(String.format("Optimal Weight: %d pts | %d Activities Selected", res.totalWeight, res.scheduledActivities.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) activities.size() * (long) (Math.log(activities.size() + 1) / Math.log(2)));
        result.setIterations(activities.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(n log n) due to sorting and binary search predecessor lookups");
        result.setSpaceComplexity("O(n)");
        result.setExplanation(String.format("Resolved overlapping conflicts to achieve maximum priority score %d with %d activities.", res.totalWeight, res.scheduledActivities.size()));
        result.setSteps(steps);

        return result;
    }
}
