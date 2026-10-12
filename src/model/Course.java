package model;

import java.io.Serializable;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseCode;
    private String courseName;
    private int capacity;
    private int credits;
    private String department;
    private int enrolledCount;

    public Course() {}

    public Course(String courseCode, String courseName, int capacity, int credits, String department) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.capacity = capacity;
        this.credits = credits;
        this.department = department;
        this.enrolledCount = 0;
    }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }

    @Override
    public String toString() {
        return String.format("%s: %s (Cap: %d, Credits: %d, Dept: %s)", courseCode, courseName, capacity, credits, department);
    }
}
