package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Detailed Academic Semester Grade Card / Marksheet domain model.
 * Contains subject-by-subject assessment scores, letter grades, credit points,
 * SGPA/CGPA calculations, and formal institutional marksheet rendering.
 */
public class GradeCard implements Serializable {
    private static final long serialVersionUID = 1L;

    private long studentId;
    private String rollNumber;
    private String studentName;
    private String department;
    private String course;
    private int semester;
    private int year;
    private String section;
    private String issueDate;

    private List<SubjectGrade> subjects;
    private int totalCreditsRegistered;
    private int totalCreditsEarned;
    private double totalCreditPoints;
    private double sgpa;
    private double cgpa;
    private String overallGrade;
    private String resultStatus;

    public GradeCard() {
        this.subjects = new ArrayList<>();
        this.issueDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    }

    public GradeCard(Student student, List<SubjectGrade> subjects) {
        this.studentId = student.getStudentId();
        this.rollNumber = student.getRollNumber();
        this.studentName = student.getName();
        this.department = student.getDepartment();
        this.course = student.getCourse();
        this.semester = student.getSemester();
        this.year = student.getYear();
        this.section = student.getSection();
        this.cgpa = student.getCgpa();
        this.issueDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        this.subjects = subjects != null ? new ArrayList<>(subjects) : new ArrayList<>();

        recalculate();
    }

    public void recalculate() {
        this.totalCreditsRegistered = 0;
        this.totalCreditsEarned = 0;
        this.totalCreditPoints = 0.0;
        boolean hasFailedSubject = false;

        for (SubjectGrade sg : subjects) {
            this.totalCreditsRegistered += sg.getCredits();
            if (!"FAIL".equalsIgnoreCase(sg.getStatus())) {
                this.totalCreditsEarned += sg.getCredits();
            } else {
                hasFailedSubject = true;
            }
            this.totalCreditPoints += sg.getCreditPoints();
        }

        if (this.totalCreditsRegistered > 0) {
            this.sgpa = Math.round((this.totalCreditPoints / this.totalCreditsRegistered) * 100.0) / 100.0;
        } else {
            this.sgpa = 0.0;
        }

        this.overallGrade = Student.calculateGrade(this.cgpa > 0 ? this.cgpa : this.sgpa);

        if (hasFailedSubject) {
            this.resultStatus = "FAIL (REAPPEAR REQUIRED)";
        } else if (this.sgpa >= 8.5) {
            this.resultStatus = "PASSED - FIRST CLASS WITH DISTINCTION";
        } else if (this.sgpa >= 6.5) {
            this.resultStatus = "PASSED - FIRST CLASS";
        } else if (this.sgpa >= 5.0) {
            this.resultStatus = "PASSED - SECOND CLASS";
        } else {
            this.resultStatus = "PASSED - PASS CLASS";
        }
    }

    public long getStudentId() { return studentId; }
    public String getRollNumber() { return rollNumber; }
    public String getStudentName() { return studentName; }
    public String getDepartment() { return department; }
    public String getCourse() { return course; }
    public int getSemester() { return semester; }
    public int getYear() { return year; }
    public String getSection() { return section; }
    public String getIssueDate() { return issueDate; }
    public List<SubjectGrade> getSubjects() { return subjects; }
    public int getTotalCreditsRegistered() { return totalCreditsRegistered; }
    public int getTotalCreditsEarned() { return totalCreditsEarned; }
    public double getTotalCreditPoints() { return totalCreditPoints; }
    public double getSgpa() { return sgpa; }
    public double getCgpa() { return cgpa; }
    public String getOverallGrade() { return overallGrade; }
    public String getResultStatus() { return resultStatus; }

    /**
     * Formats the grade card as a formal official academic marksheet document.
     */
    public String toFormattedMarksheet() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================================\n");
        sb.append("                            APEX INSTITUTE OF HIGHER TECHNOLOGY & SCIENCE                          \n");
        sb.append("                               AUTONOMOUS ACADEMIC EXAMINATION BOARD                               \n");
        sb.append("                                   SEMESTER GRADE REPORT / MARKSHEET                               \n");
        sb.append("====================================================================================================\n");
        sb.append(String.format(" Student Name : %-30s | Roll Number : %-15s | Section: %s\n", studentName, rollNumber, section));
        sb.append(String.format(" Student ID   : %-30d | Course/Prog : %-15s | Year: %d | Sem: %d\n", studentId, course, year, semester));
        sb.append(String.format(" Department   : %-30s | Date Issued : %-15s | Status: %s\n", department, issueDate, resultStatus));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" %-8s | %-32s | %-4s | %-5s | %-5s | %-5s | %-5s | %-4s | %-6s | %-4s\n",
                "CODE", "COURSE / SUBJECT TITLE", "CRD", "INT", "EXT", "TOT", "GRD", "GP", "CR-PTS", "RES"));
        sb.append("----------------------------------------------------------------------------------------------------\n");

        for (SubjectGrade sg : subjects) {
            sb.append(String.format(" %-8s | %-32s |  %2d  | %5.1f | %5.1f | %5.1f |  %-2s   |  %2d  | %6.1f | %-4s\n",
                    sg.getSubjectCode(),
                    sg.getSubjectName().length() > 32 ? sg.getSubjectName().substring(0, 32) : sg.getSubjectName(),
                    sg.getCredits(),
                    sg.getInternalMarks(),
                    sg.getExternalMarks(),
                    sg.getTotalMarks(),
                    sg.getLetterGrade(),
                    sg.getGradePoint(),
                    sg.getCreditPoints(),
                    sg.getStatus()));
        }

        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" TOTAL REGISTERED CREDITS : %-4d | CREDITS EARNED : %-4d | TOTAL CREDIT POINTS : %-6.1f\n",
                totalCreditsRegistered, totalCreditsEarned, totalCreditPoints));
        sb.append(String.format(" SEMESTER SGPA            : %-4.2f | CUMULATIVE CGPA: %-4.2f | FINAL GRADE        : %-4s\n",
                sgpa, cgpa, overallGrade));
        sb.append(String.format(" OVERALL SEMESTER RESULT  : %s\n", resultStatus));
        sb.append("====================================================================================================\n");
        sb.append(" GRADING SCALE: O (90-100)=10 | A+ (80-89)=9 | A (70-79)=8 | B+ (60-69)=7 | B (55-59)=6 | C (50-54)=5 | P (40-49)=4 | F (<40)=0\n");
        sb.append(" Minimum Passing Criteria: 40% in each subject & SGPA >= 4.00. This is a computer-verified grade report.\n");
        sb.append("\n");
        sb.append("        [Verified by: Faculty Advisor]            [Controller of Examinations]            [Dean - Academics]\n");
        sb.append("====================================================================================================\n");
        return sb.toString();
    }
}
