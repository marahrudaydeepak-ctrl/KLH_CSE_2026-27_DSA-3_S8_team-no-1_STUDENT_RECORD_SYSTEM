package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Faculty implements Serializable {
    private static final long serialVersionUID = 1L;

    private String facultyId;
    private String name;
    private String department;
    private List<String> expertiseSubjects;
    private int maxLoad; // max subjects/courses can teach
    private int assignedLoad;

    public Faculty() {
        this.expertiseSubjects = new ArrayList<>();
    }

    public Faculty(String facultyId, String name, String department, List<String> expertiseSubjects, int maxLoad) {
        this.facultyId = facultyId;
        this.name = name;
        this.department = department;
        this.expertiseSubjects = expertiseSubjects != null ? new ArrayList<>(expertiseSubjects) : new ArrayList<>();
        this.maxLoad = maxLoad;
        this.assignedLoad = 0;
    }

    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public List<String> getExpertiseSubjects() { return expertiseSubjects; }
    public void setExpertiseSubjects(List<String> expertiseSubjects) { this.expertiseSubjects = expertiseSubjects != null ? expertiseSubjects : new ArrayList<>(); }

    public int getMaxLoad() { return maxLoad; }
    public void setMaxLoad(int maxLoad) { this.maxLoad = maxLoad; }

    public int getAssignedLoad() { return assignedLoad; }
    public void setAssignedLoad(int assignedLoad) { this.assignedLoad = assignedLoad; }

    @Override
    public String toString() {
        return String.format("[%s] Prof. %s (%s, Max Load: %d, Subjects: %s)", facultyId, name, department, maxLoad, String.join(", ", expertiseSubjects));
    }
}
