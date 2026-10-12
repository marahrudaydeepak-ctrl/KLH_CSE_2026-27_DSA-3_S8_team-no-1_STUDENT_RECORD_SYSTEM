package algorithms.approximation;

import model.AlgorithmResult;
import model.Student;
import java.util.*;

/**
 * Manual implementation of Approximation Algorithms:
 * 1. 2-Approximation for Minimum Vertex Cover via Maximal Matching
 * 2. Greedy Set Cover Approximation for Student Skill Coverage
 * Application: Selects a minimum cardinality cohort of students that collectively
 * satisfies all prerequisite project skills.
 */
public class VertexCoverApproximation {

    public static class SkillCoverResult {
        public List<Student> selectedStudents;
        public Set<String> coveredSkills;
        public Set<String> requiredSkills;
        public double theoreticalApproximationRatio;
        public int optimalLowerBound;

        public SkillCoverResult() {
            this.selectedStudents = new ArrayList<>();
            this.coveredSkills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            this.requiredSkills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        }
    }

    /**
     * Greedy Set Cover approximation algorithm for Student Skill Coverage.
     * Guaranteed approximation ratio: H(|U|) = ln |U| + 1.
     */
    public static SkillCoverResult coverSkills(List<Student> students, Set<String> targetSkills) {
        SkillCoverResult result = new SkillCoverResult();
        result.requiredSkills.addAll(targetSkills);

        Set<String> uncovered = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        uncovered.addAll(targetSkills);

        List<Student> candidates = new ArrayList<>(students);

        while (!uncovered.isEmpty() && !candidates.isEmpty()) {
            Student bestStudent = null;
            int maxNewSkillsCovered = 0;

            for (Student s : candidates) {
                int count = 0;
                for (String sk : s.getSkills()) {
                    if (uncovered.contains(sk)) {
                        count++;
                    }
                }
                if (count > maxNewSkillsCovered) {
                    maxNewSkillsCovered = count;
                    bestStudent = s;
                }
            }

            if (bestStudent == null || maxNewSkillsCovered == 0) {
                break; // No more skills can be covered
            }

            result.selectedStudents.add(bestStudent);
            candidates.remove(bestStudent);
            for (String sk : bestStudent.getSkills()) {
                if (uncovered.contains(sk)) {
                    result.coveredSkills.add(sk);
                    uncovered.remove(sk);
                }
            }
        }

        int m = targetSkills.size();
        double harmonic = 0.0;
        for (int i = 1; i <= m; i++) harmonic += 1.0 / i;
        result.theoreticalApproximationRatio = harmonic; // H(m)
        result.optimalLowerBound = (int) Math.ceil((double) result.selectedStudents.size() / harmonic);

        return result;
    }

    /**
     * 2-Approximation for Vertex Cover using Maximal Matching.
     */
    public static AlgorithmResult executeVertexCover2Approx(int n, boolean[][] adjMatrix) {
        long start = System.nanoTime();
        boolean[] inCover = new boolean[n];
        boolean[][] visitedEdges = new boolean[n][n];
        List<int[]> matchingEdges = new ArrayList<>();
        List<String> steps = new ArrayList<>();

        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (adjMatrix[u][v] && !inCover[u] && !inCover[v]) {
                    // Pick edge (u, v) into maximal matching
                    matchingEdges.add(new int[]{u, v});
                    inCover[u] = true;
                    inCover[v] = true;
                    steps.add(String.format("Picked edge (%d, %d) -> Added both endpoints to cover.", u, v));
                }
            }
        }

        long duration = System.nanoTime() - start;
        List<Integer> coverNodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (inCover[i]) coverNodes.add(i);
        }

        steps.add(String.format("Maximal Matching Edges: %d | Cover Size = 2 × |M| = %d",
                matchingEdges.size(), coverNodes.size()));
        steps.add("Approximation Ratio: Guaranteed at most 2.0 × OPT.");

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Vertex Cover 2-Approximation (Maximal Matching)");
        result.setCategory("Approximation Algorithms");
        result.setPurpose("Polynomial-time 2-factor approximation for Minimum Vertex Cover");
        result.setInputSize(n);
        result.setDataStructureUsed("Maximal Matching Edge List & Boolean Cover Array");
        result.setOutput(String.format("Cover Size: %d nodes: %s", coverNodes.size(), coverNodes));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) n * n / 2);
        result.setIterations(n);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(V + E) Linear Time");
        result.setSpaceComplexity("O(V)");
        result.setExplanation(String.format("Greedy maximal matching selected %d edges yielding a 2-approximate cover of size %d.",
                matchingEdges.size(), coverNodes.size()));
        result.setSteps(steps);

        return result;
    }

    public static AlgorithmResult executeSkillCoverage(List<Student> students, Set<String> targetSkills) {
        long start = System.nanoTime();
        SkillCoverResult res = coverSkills(students, targetSkills);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Required project skills (%d): %s", targetSkills.size(), targetSkills));
        steps.add(String.format("Step 2: Available candidate students pool: %d records.", students.size()));
        steps.add(String.format("Step 3: Applied Greedy Set-Cover heuristic selecting students maximizing marginal uncovered skills."));
        for (Student s : res.selectedStudents) {
            steps.add(String.format("  Selected Student: [%d] %s -> Skills: %s",
                    s.getStudentId(), s.getName(), s.getSkills()));
        }
        steps.add(String.format("Step 4: All covered skills (%d/%d): %s",
                res.coveredSkills.size(), targetSkills.size(), res.coveredSkills));
        steps.add(String.format("Step 5: Theoretical approximation bound: H(|Skills|) = %.2f × OPT.",
                res.theoreticalApproximationRatio));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Skill Coverage (Greedy Set Cover Approximation)");
        result.setCategory("Approximation Algorithms");
        result.setPurpose("Assemble minimal student project cohort covering all prerequisite skills");
        result.setInputSize(students.size());
        result.setDataStructureUsed("Greedy Set Cover Hash Sets");
        result.setOutput(String.format("%d Students cover %d/%d skills",
                res.selectedStudents.size(), res.coveredSkills.size(), targetSkills.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) students.size() * targetSkills.size());
        result.setIterations(res.selectedStudents.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(|Students| × |Skills|)");
        result.setSpaceComplexity("O(|Skills|)");
        result.setExplanation(String.format("Selected %d students to achieve skill coverage. Bound: H(n)=%.2f.",
                res.selectedStudents.size(), res.theoreticalApproximationRatio));
        result.setSteps(steps);

        return result;
    }
}
