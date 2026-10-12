package model;

import java.io.Serializable;

public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subjectCode;
    private String subjectName;
    private int credits;
    private String requiredSkill;
    private int studentCapacity;

    public Subject() {}

    public Subject(String subjectCode, String subjectName, int credits, String requiredSkill, int studentCapacity) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.credits = credits;
        this.requiredSkill = requiredSkill;
        this.studentCapacity = studentCapacity;
    }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getRequiredSkill() { return requiredSkill; }
    public void setRequiredSkill(String requiredSkill) { this.requiredSkill = requiredSkill; }

    public int getStudentCapacity() { return studentCapacity; }
    public void setStudentCapacity(int studentCapacity) { this.studentCapacity = studentCapacity; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d Credits, Skill: %s)", subjectCode, subjectName, credits, requiredSkill);
    }
}
