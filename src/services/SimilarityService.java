package services;

import algorithms.dynamicprogramming.LCS;
import algorithms.string.DamerauLevenshtein;
import algorithms.string.LevenshteinDistance;
import model.AlgorithmResult;
import model.Student;
import java.util.*;

/**
 * Service for detecting duplicate student profiles and computing holistic
 * pairwise student profile similarity using Edit Distance, LCS, and Set Jaccard metrics.
 */
public class SimilarityService {

    public static class DuplicateReport {
        public Student studentA;
        public Student studentB;
        public boolean isDuplicate;
        public String reason;
        public double nameSimilarity;
        public int editDistance;

        @Override
        public String toString() {
            return String.format("Duplicate Flag: %b | Reason: %s | Name Similarity: %.1f%% (Dist: %d)\n  Record A: ID %d [%s] - %s\n  Record B: ID %d [%s] - %s",
                    isDuplicate, reason, nameSimilarity, editDistance,
                    studentA.getStudentId(), studentA.getRollNumber(), studentA.getName(),
                    studentB.getStudentId(), studentB.getRollNumber(), studentB.getName());
        }
    }

    public static class ProfileSimilarityReport {
        public Student studentA;
        public Student studentB;
        public List<String> commonSkills;
        public List<String> commonProjects;
        public double overallSimilarityPercentage;
        public double skillJaccardSimilarity;
        public double academicCloseness;
        public int lcsSkillLength;
        public List<String> lcsSkills;
    }

    /**
     * Scans for duplicates across all student records using ID, Roll, Email, and Fuzzy Name matching.
     */
    public static List<DuplicateReport> findDuplicates(List<Student> students) {
        List<DuplicateReport> reports = new ArrayList<>();
        int n = students.size();

        for (int i = 0; i < n; i++) {
            Student a = students.get(i);
            for (int j = i + 1; j < n; j++) {
                Student b = students.get(j);

                DuplicateReport rep = checkPairDuplicates(a, b);
                if (rep.isDuplicate) {
                    reports.add(rep);
                }
            }
        }
        return reports;
    }

    public static DuplicateReport checkPairDuplicates(Student a, Student b) {
        DuplicateReport rep = new DuplicateReport();
        rep.studentA = a;
        rep.studentB = b;

        // Exact ID duplicate
        if (a.getStudentId() == b.getStudentId()) {
            rep.isDuplicate = true;
            rep.reason = "Duplicate Student ID (" + a.getStudentId() + ")";
            rep.nameSimilarity = 100.0;
            return rep;
        }

        // Exact Roll Number duplicate
        if (a.getRollNumber().equalsIgnoreCase(b.getRollNumber())) {
            rep.isDuplicate = true;
            rep.reason = "Identical Roll Number (" + a.getRollNumber() + ")";
            rep.nameSimilarity = 100.0;
            return rep;
        }

        // Exact Email duplicate
        if (a.getEmail().equalsIgnoreCase(b.getEmail())) {
            rep.isDuplicate = true;
            rep.reason = "Duplicate Email Address (" + a.getEmail() + ")";
            rep.nameSimilarity = 100.0;
            return rep;
        }

        // Fuzzy Name Matching via Damerau-Levenshtein
        DamerauLevenshtein.DLResult dl = DamerauLevenshtein.compute(a.getName(), b.getName());
        rep.editDistance = dl.distance;
        rep.nameSimilarity = dl.similarityPercentage;

        // If high name similarity (>= 85%) and either phone match or department match
        if (dl.similarityPercentage >= 85.0) {
            if (a.getPhone().equals(b.getPhone()) || (a.getDepartment().equalsIgnoreCase(b.getDepartment()) && Math.abs(a.getStudentId() - b.getStudentId()) <= 5)) {
                rep.isDuplicate = true;
                rep.reason = String.format("High Name Similarity (%.1f%%) with identical contact/cohort metadata", dl.similarityPercentage);
            }
        }

        return rep;
    }

    /**
     * Evaluates comprehensive profile similarity between two students using LCS and Jaccard metrics.
     */
    public static AlgorithmResult compareProfiles(Student a, Student b) {
        long start = System.nanoTime();

        // 1. Common Skills (Set intersection)
        Set<String> skillsA = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        skillsA.addAll(a.getSkills());
        Set<String> skillsB = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        skillsB.addAll(b.getSkills());

        Set<String> commonSkills = new TreeSet<>(skillsA);
        commonSkills.retainAll(skillsB);

        Set<String> unionSkills = new TreeSet<>(skillsA);
        unionSkills.addAll(skillsB);

        double jaccardSkills = unionSkills.isEmpty() ? 0.0 : ((double) commonSkills.size() / unionSkills.size()) * 100.0;

        // 2. Common Projects
        Set<String> projA = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        projA.addAll(a.getProjects());
        Set<String> projB = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        projB.addAll(b.getProjects());

        Set<String> commonProjects = new TreeSet<>(projA);
        commonProjects.retainAll(projB);

        // 3. LCS of Skill Lists
        LCS.LCSResult lcsRes = LCS.compute(a.getSkills(), b.getSkills());

        // 4. Academic Closeness (CGPA difference)
        double cgpaDiff = Math.abs(a.getCgpa() - b.getCgpa());
        double academicCloseness = Math.max(0.0, 100.0 - (cgpaDiff * 15.0));

        // 5. Department match bonus
        double deptBonus = a.getDepartment().equalsIgnoreCase(b.getDepartment()) ? 10.0 : 0.0;

        // Overall Weighted Similarity
        double overallScore = (jaccardSkills * 0.40) + (academicCloseness * 0.30) + (deptBonus) + (Math.min(100.0, commonProjects.size() * 30.0) * 0.20);
        overallScore = Math.min(100.0, overallScore);

        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Student A: ID %d - %s (%s, CGPA: %.2f)", a.getStudentId(), a.getName(), a.getDepartment(), a.getCgpa()));
        steps.add(String.format("Student B: ID %d - %s (%s, CGPA: %.2f)", b.getStudentId(), b.getName(), b.getDepartment(), b.getCgpa()));
        steps.add(String.format("Common Skills (%d): %s", commonSkills.size(), commonSkills));
        steps.add(String.format("Skill Jaccard Similarity: %.2f%%", jaccardSkills));
        steps.add(String.format("Longest Common Subsequence of Skill Path (%d items): %s", lcsRes.lcsLength, lcsRes.lcsSequence));
        steps.add(String.format("Academic Closeness (|CGPA diff| = %.2f): %.2f%%", cgpaDiff, academicCloseness));
        steps.add(String.format("Total Multi-Dimensional Profile Similarity: %.2f%%", overallScore));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Student Profile Similarity (LCS + Jaccard + Edit Distance)");
        result.setCategory("Student Analytics / Dynamic Programming");
        result.setPurpose("Multi-factor similarity comparison of skills, projects, and academic trajectories");
        result.setInputSize(2);
        result.setDataStructureUsed("HashSets & LCS Dynamic Programming Matrix");
        result.setOutput(String.format("Similarity: %.2f%% | Common Skills: %d | LCS Skills: %d", overallScore, commonSkills.size(), lcsRes.lcsLength));
        result.setExecutionTimeNs(duration);
        result.setComparisons(skillsA.size() * skillsB.size());
        result.setIterations(skillsA.size() + skillsB.size());
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(|Skills_A| × |Skills_B|)");
        result.setSpaceComplexity("O(|Skills_A| × |Skills_B|)");
        result.setExplanation(String.format("Calculated similarity between '%s' and '%s'. Final compatibility score: %.2f%%.", a.getName(), b.getName(), overallScore));
        result.setSteps(steps);

        return result;
    }
}
