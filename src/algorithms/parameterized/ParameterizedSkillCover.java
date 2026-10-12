package algorithms.parameterized;

import model.AlgorithmResult;
import model.Student;
import java.util.*;

/**
 * Manual implementation of Parameterized Algorithms (Fixed Parameter Tractability - FPT).
 * Solves Parameterized Student Skill Coverage parameterized by budget K.
 * Demonstrates:
 * 1. Kernelization (Forced inclusion of unique skill-bearers, dominance reductions)
 * 2. Bounded Search Tree Branching
 */
public class ParameterizedSkillCover {

    public static class FPTResult {
        public boolean possible;
        public int parameterK;
        public int initialStudentCount;
        public int kernelStudentCount;
        public int kernelSkillCount;
        public List<Student> chosenStudents;
        public List<String> reductionSteps;

        public FPTResult(int k) {
            this.parameterK = k;
            this.chosenStudents = new ArrayList<>();
            this.reductionSteps = new ArrayList<>();
        }
    }

    public static FPTResult solve(List<Student> students, Set<String> targetSkills, int k) {
        FPTResult result = new FPTResult(k);
        result.initialStudentCount = students.size();

        // Working copy
        Map<String, Set<String>> studentSkills = new HashMap<>();
        Map<String, Student> studentMap = new HashMap<>();

        for (Student s : students) {
            String sId = "S_" + s.getStudentId();
            studentMap.put(sId, s);
            Set<String> sks = new HashSet<>();
            for (String sk : s.getSkills()) {
                if (targetSkills.contains(sk)) {
                    sks.add(sk.toLowerCase());
                }
            }
            if (!sks.isEmpty()) {
                studentSkills.put(sId, sks);
            }
        }

        Set<String> remainingSkills = new HashSet<>();
        for (String s : targetSkills) remainingSkills.add(s.toLowerCase());

        int remainingK = k;

        // --- Kernelization Rule 1: Forced Selection (Unique Skill Bearers) ---
        boolean changed = true;
        while (changed && !remainingSkills.isEmpty()) {
            changed = false;
            for (String skill : new ArrayList<>(remainingSkills)) {
                List<String> holders = new ArrayList<>();
                for (Map.Entry<String, Set<String>> entry : studentSkills.entrySet()) {
                    if (entry.getValue().contains(skill)) {
                        holders.add(entry.getKey());
                    }
                }

                if (holders.isEmpty()) {
                    result.reductionSteps.add(String.format("Kernel Rule: Skill '%s' is held by 0 students -> IMPOSSIBLE.", skill));
                    result.possible = false;
                    return result;
                } else if (holders.size() == 1) {
                    String forcedId = holders.get(0);
                    Student forcedStudent = studentMap.get(forcedId);
                    result.chosenStudents.add(forcedStudent);
                    result.reductionSteps.add(String.format("Kernelization Rule 1 (Forced Invariant): Student '%s' uniquely holds skill '%s' -> Forced pick.",
                            forcedStudent.getName(), skill));
                    remainingK--;
                    // Remove all skills covered by this forced student
                    remainingSkills.removeAll(studentSkills.get(forcedId));
                    studentSkills.remove(forcedId);
                    changed = true;
                    break;
                }
            }
        }

        if (remainingK < 0) {
            result.possible = false;
            return result;
        }

        // --- Kernelization Rule 2: Domination Reduction ---
        List<String> studentKeys = new ArrayList<>(studentSkills.keySet());
        for (int i = 0; i < studentKeys.size(); i++) {
            String s1 = studentKeys.get(i);
            if (!studentSkills.containsKey(s1)) continue;
            for (int j = 0; j < studentKeys.size(); j++) {
                if (i == j) continue;
                String s2 = studentKeys.get(j);
                if (!studentSkills.containsKey(s2)) continue;

                // If s2 covers a superset of s1, drop s1
                if (studentSkills.get(s2).containsAll(studentSkills.get(s1))) {
                    result.reductionSteps.add(String.format("Kernelization Rule 2 (Domination): Dropped dominated candidate %s in favor of %s.", s1, s2));
                    studentSkills.remove(s1);
                    break;
                }
            }
        }

        result.kernelStudentCount = studentSkills.size();
        result.kernelSkillCount = remainingSkills.size();

        // --- Phase 2: Bounded Search Tree Branching ---
        if (remainingSkills.isEmpty()) {
            result.possible = true;
            return result;
        }

        List<String> branchChoice = new ArrayList<>();
        boolean branchSuccess = boundedBranch(studentSkills, studentMap, remainingSkills, remainingK, branchChoice);

        if (branchSuccess) {
            for (String sId : branchChoice) {
                result.chosenStudents.add(studentMap.get(sId));
            }
            result.possible = true;
        } else {
            result.possible = false;
        }

        return result;
    }

    private static boolean boundedBranch(Map<String, Set<String>> studentSkills,
                                         Map<String, Student> studentMap,
                                         Set<String> remainingSkills,
                                         int k, List<String> chosen) {
        if (remainingSkills.isEmpty()) return true;
        if (k <= 0) return false;

        // Pick one uncovered skill
        String targetSkill = remainingSkills.iterator().next();

        // Branch over all students possessing this skill
        List<String> candidates = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : studentSkills.entrySet()) {
            if (entry.getValue().contains(targetSkill)) {
                candidates.add(entry.getKey());
            }
        }

        for (String candId : candidates) {
            chosen.add(candId);
            Set<String> nextSkills = new HashSet<>(remainingSkills);
            nextSkills.removeAll(studentSkills.get(candId));

            if (boundedBranch(studentSkills, studentMap, nextSkills, k - 1, chosen)) {
                return true;
            }
            chosen.remove(chosen.size() - 1); // backtrack
        }

        return false;
    }

    public static AlgorithmResult executeFPT(List<Student> students, Set<String> targetSkills, int k) {
        long start = System.nanoTime();
        FPTResult res = solve(students, targetSkills, k);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Target parameter K = %d students allowed. Target Skills: %s", k, targetSkills));
        steps.add(String.format("Step 2: Applied kernelization reduction rules (Forced inclusion & Domination pruning)."));
        steps.addAll(res.reductionSteps);
        steps.add(String.format("Step 3: Kernel Size reduced from %d to %d students, covering %d remaining skills.",
                res.initialStudentCount, res.kernelStudentCount, res.kernelSkillCount));
        steps.add(String.format("Step 4: Bounded Search Tree Exploration Result: %s", res.possible ? "FEASIBLE" : "INFEASIBLE"));
        if (res.possible) {
            for (Student s : res.chosenStudents) {
                steps.add(String.format("  Selected Student: [%d] %s -> %s", s.getStudentId(), s.getName(), s.getSkills()));
            }
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Parameterized FPT Skill Cover");
        result.setCategory("Parameterized Algorithms (FPT)");
        result.setPurpose(String.format("Determine if at most K=%d students can cover all required skills via Kernelization", k));
        result.setInputSize(students.size());
        result.setDataStructureUsed("Kernelized Candidate Map & Bounded Search Tree");
        result.setOutput(res.possible
                ? String.format("Possible with K=%d students! (%d selected)", k, res.chosenStudents.size())
                : String.format("Not Possible with K=%d students", k));
        result.setExecutionTimeNs(duration);
        result.setComparisons((long) res.initialStudentCount * targetSkills.size());
        result.setIterations(res.reductionSteps.size() + 1);
        result.setSwaps(0);
        result.setRecursionCount(res.chosenStudents.size());
        result.setTimeComplexity("O(2^k × |Kernel| + poly(n)) Fixed Parameter Tractable");
        result.setSpaceComplexity("O(|Kernel| + k)");
        result.setExplanation(String.format("Evaluated problem with parameter K=%d. Kernelization reduced instance to %d students. Verdict: %s.",
                k, res.kernelStudentCount, res.possible ? "Possible" : "Not Possible"));
        result.setSteps(steps);

        return result;
    }
}
