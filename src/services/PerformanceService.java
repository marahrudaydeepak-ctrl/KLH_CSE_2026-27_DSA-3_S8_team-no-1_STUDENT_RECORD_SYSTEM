package services;

import algorithms.sorting.MergeSort;
import algorithms.sorting.SortCriteria;
import model.AlgorithmResult;
import model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for calculating comprehensive academic performance metrics,
 * identifying at-risk cohorts, and generating multi-criteria student rankings.
 */
public class PerformanceService {

    public static class PerformanceSummary {
        public int totalStudents;
        public double avgMarks;
        public double avgCgpa;
        public double avgAttendance;
        public double maxMarks;
        public double minMarks;
        public Student topStudent;
        public List<Student> studentsBelowAttendance; // Attendance < 75%
        public List<Student> studentsAtAcademicRisk;   // CGPA < 5.0 or Marks < 40%
        public List<Student> topPerformers;           // CGPA >= 9.0

        public PerformanceSummary() {
            this.studentsBelowAttendance = new ArrayList<>();
            this.studentsAtAcademicRisk = new ArrayList<>();
            this.topPerformers = new ArrayList<>();
        }
    }

    public static class RankedStudent {
        public int rank;
        public Student student;
        public double score;

        public RankedStudent(int rank, Student student, double score) {
            this.rank = rank;
            this.student = student;
            this.score = score;
        }

        @Override
        public String toString() {
            return String.format("Rank #%-3d | ID: %-5d | %-20s | %-4s | Marks: %5.1f%% | Attd: %5.1f%% | CGPA: %4.2f (Grade: %-2s)",
                    rank, student.getStudentId(), student.getName(), student.getDepartment(),
                    student.getMarks(), student.getAttendance(), student.getCgpa(), student.getGrade());
        }
    }

    public static PerformanceSummary computeSummary(List<Student> students) {
        PerformanceSummary sum = new PerformanceSummary();
        sum.totalStudents = students.size();
        if (students.isEmpty()) return sum;

        double totMarks = 0;
        double totCgpa = 0;
        double totAtt = 0;
        sum.maxMarks = Double.NEGATIVE_INFINITY;
        sum.minMarks = Double.POSITIVE_INFINITY;

        for (Student s : students) {
            totMarks += s.getMarks();
            totCgpa += s.getCgpa();
            totAtt += s.getAttendance();

            if (s.getMarks() > sum.maxMarks) {
                sum.maxMarks = s.getMarks();
                sum.topStudent = s;
            }
            if (s.getMarks() < sum.minMarks) {
                sum.minMarks = s.getMarks();
            }

            if (s.getAttendance() < 75.0) {
                sum.studentsBelowAttendance.add(s);
            }
            if (s.getCgpa() < 5.0 || s.getMarks() < 40.0) {
                sum.studentsAtAcademicRisk.add(s);
            }
            if (s.getCgpa() >= 9.0) {
                sum.topPerformers.add(s);
            }
        }

        sum.avgMarks = totMarks / students.size();
        sum.avgCgpa = totCgpa / students.size();
        sum.avgAttendance = totAtt / students.size();
        return sum;
    }

    /**
     * Ranks students using manual MergeSort by selected criteria.
     */
    public static List<RankedStudent> rankStudents(List<Student> students, SortCriteria criteria) {
        // Run manual MergeSort in descending order
        AlgorithmResult sortRes = MergeSort.sort(students, criteria, false);
        @SuppressWarnings("unchecked")
        List<Student> sorted = (List<Student>) sortRes.getOutput();

        List<RankedStudent> ranked = new ArrayList<>();
        int rank = 1;
        for (Student s : sorted) {
            double val = 0.0;
            switch (criteria) {
                case CGPA: val = s.getCgpa(); break;
                case MARKS: val = s.getMarks(); break;
                case ATTENDANCE: val = s.getAttendance(); break;
                default: val = s.getCgpa(); break;
            }
            ranked.add(new RankedStudent(rank++, s, val));
        }
        return ranked;
    }

    /**
     * Ranks students by Combined Performance Score:
     * Combined Score = (CGPA * 5.0) + (Marks * 0.3) + (Attendance * 0.2) -> Max 100
     */
    public static List<RankedStudent> rankByCombinedScore(List<Student> students) {
        List<Student> list = new ArrayList<>(students);

        // In-place manual insertion sort by custom combined score descending
        for (int i = 1; i < list.size(); i++) {
            Student key = list.get(i);
            double keyScore = computeCombinedScore(key);
            int j = i - 1;

            while (j >= 0 && computeCombinedScore(list.get(j)) < keyScore) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }

        List<RankedStudent> ranked = new ArrayList<>();
        int r = 1;
        for (Student s : list) {
            ranked.add(new RankedStudent(r++, s, computeCombinedScore(s)));
        }
        return ranked;
    }

    public static double computeCombinedScore(Student s) {
        return (s.getCgpa() * 5.0) + (s.getMarks() * 0.3) + (s.getAttendance() * 0.2);
    }
}
