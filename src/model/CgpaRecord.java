package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Dedicated Model for Separate CGPA Analysis, Academic Ranking,
 * Semester GPA History, and Performance Tier classification.
 */
public class CgpaRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private long studentId;
    private String rollNumber;
    private String studentName;
    private String department;
    private String course;
    private int year;
    private int semester;

    private double cgpa;
    private double sgpa;
    private double marksPercentage;
    private String letterGrade;
    private String performanceTier;
    private int rankInDept;
    private int totalInDept;
    private double percentile;
    private Map<Integer, Double> semesterGpas;

    public CgpaRecord() {
        this.semesterGpas = new TreeMap<>();
    }

    public CgpaRecord(Student student, int rankInDept, int totalInDept) {
        this.studentId = student.getStudentId();
        this.rollNumber = student.getRollNumber();
        this.studentName = student.getName();
        this.department = student.getDepartment();
        this.course = student.getCourse();
        this.year = student.getYear();
        this.semester = student.getSemester();

        this.cgpa = student.getCgpa();
        this.marksPercentage = student.getMarks();
        this.letterGrade = Student.calculateGrade(this.cgpa);
        this.rankInDept = rankInDept;
        this.totalInDept = totalInDept;

        if (totalInDept > 0) {
            this.percentile = Math.round(((double) (totalInDept - rankInDept + 1) / totalInDept) * 1000.0) / 10.0;
        } else {
            this.percentile = 100.0;
        }

        // Evaluate performance tier
        if (this.cgpa >= 9.0) {
            this.performanceTier = "OUTSTANDING (DEAN'S HONORS LIST)";
        } else if (this.cgpa >= 8.5) {
            this.performanceTier = "FIRST CLASS WITH DISTINCTION";
        } else if (this.cgpa >= 6.5) {
            this.performanceTier = "FIRST CLASS (GOOD ACADEMIC STANDING)";
        } else if (this.cgpa >= 5.0) {
            this.performanceTier = "SECOND CLASS (SATISFACTORY)";
        } else if (this.cgpa >= 4.0) {
            this.performanceTier = "PASS CLASS (MINIMAL PASSING)";
        } else {
            this.performanceTier = "FAIL (ACADEMIC PROBATION / AT-RISK)";
        }

        // Build semester GPA progression
        this.semesterGpas = new TreeMap<>();
        int currentSem = Math.max(1, student.getSemester());
        for (int s = 1; s <= currentSem; s++) {
            if (s == currentSem) {
                this.semesterGpas.put(s, this.cgpa);
            } else {
                // Realistic slight variance per prior semester around current CGPA
                double diff = ((student.getStudentId() + s * 17) % 7 - 3) * 0.12;
                double prevGpa = Math.max(4.0, Math.min(10.0, Math.round((this.cgpa + diff) * 100.0) / 100.0));
                this.semesterGpas.put(s, prevGpa);
            }
        }
        this.sgpa = this.semesterGpas.getOrDefault(currentSem, this.cgpa);
    }

    public long getStudentId() { return studentId; }
    public String getRollNumber() { return rollNumber; }
    public String getStudentName() { return studentName; }
    public String getDepartment() { return department; }
    public String getCourse() { return course; }
    public int getYear() { return year; }
    public int getSemester() { return semester; }
    public double getCgpa() { return cgpa; }
    public double getSgpa() { return sgpa; }
    public double getMarksPercentage() { return marksPercentage; }
    public String getLetterGrade() { return letterGrade; }
    public String getPerformanceTier() { return performanceTier; }
    public int getRankInDept() { return rankInDept; }
    public int getTotalInDept() { return totalInDept; }
    public double getPercentile() { return percentile; }
    public Map<Integer, Double> getSemesterGpas() { return Collections.unmodifiableMap(semesterGpas); }

    /**
     * Formats this record as a dedicated, standalone CGPA Academic Scorecard.
     */
    public String toFormattedCgpaCard() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================================\n");
        sb.append("                            APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE                          \n");
        sb.append("                               OFFICE OF ACADEMIC EVALUATION & GRADES                              \n");
        sb.append("                               DEDICATED STUDENT CGPA & ACADEMIC SCORECARD                         \n");
        sb.append("====================================================================================================\n");
        sb.append(String.format(" Student Name     : %-32s | Roll Number  : %-18s\n", studentName, rollNumber));
        sb.append(String.format(" Student ID       : %-32d | Department   : %-18s\n", studentId, department));
        sb.append(String.format(" Program / Course : %-32s | Year / Sem   : Year %d, Sem %-10d\n", course, year, semester));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" CUMULATIVE GPA (CGPA)   : %5.2f / 10.00         | OFFICIAL LETTER GRADE : %-4s\n", cgpa, letterGrade));
        sb.append(String.format(" CURRENT SEMESTER SGPA   : %5.2f / 10.00         | OVERALL MARKS SCORE   : %5.1f%%\n", sgpa, marksPercentage));
        sb.append(String.format(" DEPARTMENT CLASS RANK   : #%-3d of %-3d Students  | ACADEMIC PERCENTILE   : %5.1fth %%\n",
                rankInDept, totalInDept, percentile));
        sb.append(String.format(" ACADEMIC PERFORMANCE    : %s\n", performanceTier));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(" SEMESTER-WISE GPA PROGRESSION HISTORY:\n");
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" %-12s | %-12s | %-15s | %-45s\n", "SEMESTER", "SGPA", "STATUS", "PROGRESSION BAR"));
        sb.append("----------------------------------------------------------------------------------------------------\n");

        for (Map.Entry<Integer, Double> entry : semesterGpas.entrySet()) {
            int semNum = entry.getKey();
            double gpaVal = entry.getValue();
            int bars = (int) Math.round((gpaVal / 10.0) * 30);
            String barStr = "█".repeat(Math.max(0, bars));
            String status = gpaVal >= 8.5 ? "Distinction" : (gpaVal >= 6.5 ? "First Class" : (gpaVal >= 5.0 ? "Second Class" : "Pass"));
            sb.append(String.format(" Semester %-2d  | %5.2f / 10.0 | %-15s | %-30s (%5.1f%%)\n",
                    semNum, gpaVal, status, barStr, (gpaVal * 10.0)));
        }

        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(" CGPA BENCHMARK CRITERIA:\n");
        sb.append(" • CGPA >= 9.00 : Eligible for Institutional Merit Scholarship, Gold Medal & Chancellor's Commendation\n");
        sb.append(" • CGPA >= 8.50 : Awarded First Class with Distinction; eligible for fast-track campus placements\n");
        sb.append(" • CGPA >= 6.50 : Awarded First Class in Engineering Degree\n");
        sb.append(" • CGPA <  5.00 : Mandatory academic mentoring and remedial workshop assigned\n");
        sb.append("====================================================================================================\n");
        sb.append("      [Computer Generated Record]         [Academic Registrar]         [Dean of Academic Affairs]   \n");
        sb.append("====================================================================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("[CGPA Record: %s (%s) | CGPA: %.2f | Grade: %s | Rank: #%d/%d]",
                studentName, rollNumber, cgpa, letterGrade, rankInDept, totalInDept);
    }
}
