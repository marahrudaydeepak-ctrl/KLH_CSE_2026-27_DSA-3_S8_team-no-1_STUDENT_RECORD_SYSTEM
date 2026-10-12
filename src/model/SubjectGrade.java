package model;

import java.io.Serializable;

/**
 * Represents the individual subject assessment result within a student's grade card / marksheet.
 */
public class SubjectGrade implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subjectCode;
    private String subjectName;
    private int credits;
    private double internalMarks; // Out of 30
    private double externalMarks; // Out of 70
    private double totalMarks;    // Out of 100
    private int gradePoint;       // 0 to 10
    private String letterGrade;   // O, A+, A, B+, B, C, P, F
    private double creditPoints;  // credits * gradePoint
    private String status;        // PASS / FAIL

    public SubjectGrade() {}

    public SubjectGrade(String subjectCode, String subjectName, int credits,
                        double internalMarks, double externalMarks) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.credits = credits;
        this.internalMarks = Math.round(internalMarks * 10.0) / 10.0;
        this.externalMarks = Math.round(externalMarks * 10.0) / 10.0;
        this.totalMarks = Math.round((this.internalMarks + this.externalMarks) * 10.0) / 10.0;
        evaluateGrade();
    }

    private void evaluateGrade() {
        if (totalMarks >= 90.0) {
            letterGrade = "O";
            gradePoint = 10;
            status = "PASS";
        } else if (totalMarks >= 80.0) {
            letterGrade = "A+";
            gradePoint = 9;
            status = "PASS";
        } else if (totalMarks >= 70.0) {
            letterGrade = "A";
            gradePoint = 8;
            status = "PASS";
        } else if (totalMarks >= 60.0) {
            letterGrade = "B+";
            gradePoint = 7;
            status = "PASS";
        } else if (totalMarks >= 55.0) {
            letterGrade = "B";
            gradePoint = 6;
            status = "PASS";
        } else if (totalMarks >= 50.0) {
            letterGrade = "C";
            gradePoint = 5;
            status = "PASS";
        } else if (totalMarks >= 40.0) {
            letterGrade = "P";
            gradePoint = 4;
            status = "PASS";
        } else {
            letterGrade = "F";
            gradePoint = 0;
            status = "FAIL";
        }
        this.creditPoints = this.credits * this.gradePoint;
    }

    // Getters and Setters
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { 
        this.credits = credits; 
        this.creditPoints = this.credits * this.gradePoint;
    }

    public double getInternalMarks() { return internalMarks; }
    public void setInternalMarks(double internalMarks) { 
        this.internalMarks = internalMarks; 
        this.totalMarks = this.internalMarks + this.externalMarks;
        evaluateGrade();
    }

    public double getExternalMarks() { return externalMarks; }
    public void setExternalMarks(double externalMarks) { 
        this.externalMarks = externalMarks; 
        this.totalMarks = this.internalMarks + this.externalMarks;
        evaluateGrade();
    }

    public double getTotalMarks() { return totalMarks; }
    public int getGradePoint() { return gradePoint; }
    public String getLetterGrade() { return letterGrade; }
    public double getCreditPoints() { return creditPoints; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("%-8s | %-32s | %2d | %5.1f | %5.1f | %5.1f | %-2s | %2d | %5.1f | %-4s",
                subjectCode,
                subjectName.length() > 32 ? subjectName.substring(0, 32) : subjectName,
                credits, internalMarks, externalMarks, totalMarks,
                letterGrade, gradePoint, creditPoints, status);
    }
}
