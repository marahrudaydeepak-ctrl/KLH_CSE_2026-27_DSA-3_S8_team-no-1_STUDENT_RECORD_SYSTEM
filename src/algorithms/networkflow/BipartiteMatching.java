package algorithms.networkflow;

import model.*;
import java.util.*;

/**
 * Manual implementation of Maximum Bipartite Matching and Capacitated Allocation.
 * Powers:
 * 1. Course Allocation (Students -> Courses under capacity limits)
 * 2. Project Allocation (Students -> Projects based on required skills & preferences)
 * 3. Faculty-Subject Allocation (Faculty -> Subjects based on expertise & max teaching load)
 */
public class BipartiteMatching {

    public static class AllocationResult {
        public String allocationType;
        public Map<String, List<String>> assignments; // Target -> List of assigned Entities
        public int totalMatches;
        public List<String> matchLogs;

        public AllocationResult(String allocationType) {
            this.allocationType = allocationType;
            this.assignments = new LinkedHashMap<>();
            this.matchLogs = new ArrayList<>();
        }
    }

    /**
     * Standard Maximum Bipartite Matching using Augmenting Path DFS algorithm.
     */
    public static Map<String, String> maxBipartiteMatching(List<String> leftEntities,
                                                           Map<String, List<String>> candidateMap) {
        Map<String, String> rightMatch = new HashMap<>(); // rightEntity -> leftEntity

        for (String left : leftEntities) {
            Set<String> visited = new HashSet<>();
            augment(left, candidateMap, rightMatch, visited);
        }

        // Invert to left -> right
        Map<String, String> leftMatch = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : rightMatch.entrySet()) {
            leftMatch.put(entry.getValue(), entry.getKey());
        }
        return leftMatch;
    }

    private static boolean augment(String u, Map<String, List<String>> candidateMap,
                                   Map<String, String> rightMatch, Set<String> visited) {
        List<String> neighbors = candidateMap.getOrDefault(u, Collections.emptyList());
        for (String v : neighbors) {
            if (!visited.contains(v)) {
                visited.add(v);

                if (!rightMatch.containsKey(v) || augment(rightMatch.get(v), candidateMap, rightMatch, visited)) {
                    rightMatch.put(v, u);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Solves Course Allocation using Network Flow with capacities.
     * Source -> Students (cap 1) -> Courses (cap courseCapacity) -> Sink
     */
    public static AlgorithmResult allocateCourses(List<Student> students, List<Course> courses) {
        long start = System.nanoTime();
        Map<String, Map<String, Integer>> network = new HashMap<>();
        String source = "SRC";
        String sink = "SNK";
        network.put(source, new HashMap<>());

        // Source -> Students
        for (Student s : students) {
            String sKey = "S_" + s.getStudentId();
            network.get(source).put(sKey, 1);
            network.put(sKey, new HashMap<>());

            // Connect student to compatible department courses
            for (Course c : courses) {
                if (c.getDepartment().equalsIgnoreCase(s.getDepartment()) || c.getDepartment().equals("GEN")) {
                    network.get(sKey).put("C_" + c.getCourseCode(), 1);
                }
            }
        }

        // Courses -> Sink with capacities
        for (Course c : courses) {
            String cKey = "C_" + c.getCourseCode();
            network.putIfAbsent(cKey, new HashMap<>());
            network.get(cKey).put(sink, c.getCapacity());
        }

        Dinic.DinicResult dinicRes = Dinic.computeMaxFlow(network, source, sink);
        long duration = System.nanoTime() - start;

        // Extract assignments from residual graph
        Map<String, List<String>> courseAssignments = new LinkedHashMap<>();
        for (Course c : courses) {
            courseAssignments.put(c.getCourseName() + " (" + c.getCourseCode() + ")", new ArrayList<>());
        }

        int[][] rGraph = dinicRes.residualGraph;
        for (Student s : students) {
            String sKey = "S_" + s.getStudentId();
            int sIdx = dinicRes.nodeIndices.get(sKey);
            for (Course c : courses) {
                String cKey = "C_" + c.getCourseCode();
                if (dinicRes.nodeIndices.containsKey(cKey)) {
                    int cIdx = dinicRes.nodeIndices.get(cKey);
                    // In residual graph, forward capacity reduced means flow pushed
                    if (rGraph[cIdx][sIdx] > 0) {
                        String key = c.getCourseName() + " (" + c.getCourseCode() + ")";
                        courseAssignments.get(key).add(String.format("%s (ID: %d)", s.getName(), s.getStudentId()));
                    }
                }
            }
        }

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Formulated Course Allocation Network for %d students across %d courses.",
                students.size(), courses.size()));
        steps.add("Step 2: Applied Dinic's Maximum Flow algorithm to assign students respecting course capacity constraints.");
        steps.add(String.format("Step 3: Successfully assigned %d students to courses (Flow = %d).",
                dinicRes.maxFlow, dinicRes.maxFlow));
        for (Map.Entry<String, List<String>> entry : courseAssignments.entrySet()) {
            steps.add(String.format("  Course [%s]: %d enrolled -> %s",
                    entry.getKey(), entry.getValue().size(), entry.getValue()));
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Course Allocation (Network Flow / Bipartite)");
        result.setCategory("Network Flow / Matching");
        result.setPurpose("Maximize feasible student-to-course assignments respecting section capacities");
        result.setInputSize(students.size() + courses.size());
        result.setDataStructureUsed("Flow Network with Course Capacities");
        result.setOutput(String.format("%d / %d students successfully assigned to courses", dinicRes.maxFlow, students.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(dinicRes.phases * 10L);
        result.setIterations(dinicRes.phases);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(V² × E)");
        result.setSpaceComplexity("O(V + E)");
        result.setExplanation(String.format("Maximum bipartite flow matched %d students to eligible courses without exceeding room caps.", dinicRes.maxFlow));
        result.setSteps(steps);

        return result;
    }

    /**
     * Solves Project Allocation based on Student Skills.
     */
    public static AlgorithmResult allocateProjects(List<Student> students, List<Project> projects) {
        long start = System.nanoTime();
        Map<String, Map<String, Integer>> network = new HashMap<>();
        String source = "SRC";
        String sink = "SNK";
        network.put(source, new HashMap<>());

        for (Student s : students) {
            String sKey = "S_" + s.getStudentId();
            network.get(source).put(sKey, 1);
            network.put(sKey, new HashMap<>());

            for (Project p : projects) {
                // Check skill match
                boolean qualified = false;
                for (String reqSkill : p.getRequiredSkills()) {
                    for (String studSkill : s.getSkills()) {
                        if (studSkill.equalsIgnoreCase(reqSkill)) {
                            qualified = true;
                            break;
                        }
                    }
                    if (qualified) break;
                }
                if (qualified) {
                    network.get(sKey).put("P_" + p.getProjectId(), 1);
                }
            }
        }

        for (Project p : projects) {
            String pKey = "P_" + p.getProjectId();
            network.putIfAbsent(pKey, new HashMap<>());
            network.get(pKey).put(sink, p.getMaxStudentsAllowed());
        }

        Dinic.DinicResult dinicRes = Dinic.computeMaxFlow(network, source, sink);
        long duration = System.nanoTime() - start;

        Map<String, List<String>> projectAssignments = new LinkedHashMap<>();
        for (Project p : projects) {
            projectAssignments.put(p.getTitle() + " (" + p.getProjectId() + ")", new ArrayList<>());
        }

        int[][] rGraph = dinicRes.residualGraph;
        for (Student s : students) {
            String sKey = "S_" + s.getStudentId();
            int sIdx = dinicRes.nodeIndices.get(sKey);
            for (Project p : projects) {
                String pKey = "P_" + p.getProjectId();
                if (dinicRes.nodeIndices.containsKey(pKey)) {
                    int pIdx = dinicRes.nodeIndices.get(pKey);
                    if (rGraph[pIdx][sIdx] > 0) {
                        String key = p.getTitle() + " (" + p.getProjectId() + ")";
                        projectAssignments.get(key).add(String.format("%s (ID: %d)", s.getName(), s.getStudentId()));
                    }
                }
            }
        }

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Matched %d students with %d capstone projects based on verified skill sets.",
                students.size(), projects.size()));
        steps.add(String.format("Step 2: Total feasible project assignments = %d.", dinicRes.maxFlow));
        for (Map.Entry<String, List<String>> entry : projectAssignments.entrySet()) {
            steps.add(String.format("  Project [%s]: %d members -> %s",
                    entry.getKey(), entry.getValue().size(), entry.getValue()));
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Project Allocation (Skill-based Bipartite Matching)");
        result.setCategory("Network Flow / Matching");
        result.setPurpose("Match students to academic capstone projects based on competency prerequisites");
        result.setInputSize(students.size() + projects.size());
        result.setDataStructureUsed("Skill Bipartite Network with Quotas");
        result.setOutput(String.format("%d students allocated to %d projects", dinicRes.maxFlow, projects.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(dinicRes.phases * 10L);
        result.setIterations(dinicRes.phases);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(V² × E)");
        result.setSpaceComplexity("O(V + E)");
        result.setExplanation(String.format("Allocated %d student team members to research projects based on technical skill compatibility.", dinicRes.maxFlow));
        result.setSteps(steps);

        return result;
    }

    /**
     * Solves Faculty to Subject allocation based on expertise.
     */
    public static AlgorithmResult allocateFacultySubjects(List<Faculty> faculties, List<Subject> subjects) {
        long start = System.nanoTime();
        Map<String, Map<String, Integer>> network = new HashMap<>();
        String source = "SRC";
        String sink = "SNK";
        network.put(source, new HashMap<>());

        for (Faculty f : faculties) {
            String fKey = "F_" + f.getFacultyId();
            network.get(source).put(fKey, f.getMaxLoad());
            network.put(fKey, new HashMap<>());

            for (Subject sub : subjects) {
                for (String exp : f.getExpertiseSubjects()) {
                    if (exp.equalsIgnoreCase(sub.getSubjectCode()) || exp.equalsIgnoreCase(sub.getRequiredSkill())) {
                        network.get(fKey).put("SUB_" + sub.getSubjectCode(), 1);
                        break;
                    }
                }
            }
        }

        for (Subject sub : subjects) {
            String subKey = "SUB_" + sub.getSubjectCode();
            network.putIfAbsent(subKey, new HashMap<>());
            network.get(subKey).put(sink, 1); // 1 faculty per subject course
        }

        Dinic.DinicResult dinicRes = Dinic.computeMaxFlow(network, source, sink);
        long duration = System.nanoTime() - start;

        Map<String, String> assignments = new LinkedHashMap<>();
        int[][] rGraph = dinicRes.residualGraph;

        for (Faculty f : faculties) {
            String fKey = "F_" + f.getFacultyId();
            int fIdx = dinicRes.nodeIndices.get(fKey);
            for (Subject sub : subjects) {
                String subKey = "SUB_" + sub.getSubjectCode();
                if (dinicRes.nodeIndices.containsKey(subKey)) {
                    int sIdx = dinicRes.nodeIndices.get(subKey);
                    if (rGraph[sIdx][fIdx] > 0) {
                        assignments.put(sub.getSubjectName() + " (" + sub.getSubjectCode() + ")",
                                f.getName() + " (" + f.getDepartment() + ")");
                    }
                }
            }
        }

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Step 1: Analyzed %d faculty members and %d academic subjects.", faculties.size(), subjects.size()));
        steps.add(String.format("Step 2: Successfully scheduled %d / %d subjects with specialized faculty.",
                dinicRes.maxFlow, subjects.size()));
        for (Map.Entry<String, String> entry : assignments.entrySet()) {
            steps.add(String.format("  Subject [%s] assigned to: Prof. %s", entry.getKey(), entry.getValue()));
        }

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Faculty-Subject Allocation (Bipartite Flow)");
        result.setCategory("Network Flow / Matching");
        result.setPurpose("Assign qualified faculty to academic subjects respecting teaching loads");
        result.setInputSize(faculties.size() + subjects.size());
        result.setDataStructureUsed("Expertise Bipartite Flow Network");
        result.setOutput(String.format("%d / %d subjects assigned", dinicRes.maxFlow, subjects.size()));
        result.setExecutionTimeNs(duration);
        result.setComparisons(dinicRes.phases * 10L);
        result.setIterations(dinicRes.phases);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(V² × E)");
        result.setSpaceComplexity("O(V + E)");
        result.setExplanation(String.format("Assigned %d subjects to specialized faculty members without exceeding maximum teaching credits.", dinicRes.maxFlow));
        result.setSteps(steps);

        return result;
    }
}
