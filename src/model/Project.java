package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    private String projectId;
    private String title;
    private int timeRequiredHours;
    private int academicValue; // score 10 - 100
    private List<String> requiredSkills;
    private int maxStudentsAllowed;
    private int assignedCount;

    public Project() {
        this.requiredSkills = new ArrayList<>();
    }

    public Project(String projectId, String title, int timeRequiredHours, int academicValue,
                   List<String> requiredSkills, int maxStudentsAllowed) {
        this.projectId = projectId;
        this.title = title;
        this.timeRequiredHours = timeRequiredHours;
        this.academicValue = academicValue;
        this.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>();
        this.maxStudentsAllowed = maxStudentsAllowed;
        this.assignedCount = 0;
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getTimeRequiredHours() { return timeRequiredHours; }
    public void setTimeRequiredHours(int timeRequiredHours) { this.timeRequiredHours = timeRequiredHours; }

    public int getAcademicValue() { return academicValue; }
    public void setAcademicValue(int academicValue) { this.academicValue = academicValue; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>(); }

    public int getMaxStudentsAllowed() { return maxStudentsAllowed; }
    public void setMaxStudentsAllowed(int maxStudentsAllowed) { this.maxStudentsAllowed = maxStudentsAllowed; }

    public int getAssignedCount() { return assignedCount; }
    public void setAssignedCount(int assignedCount) { this.assignedCount = assignedCount; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Time: %dh | Value: %d pts | Skills: %s (Cap: %d)",
                projectId, title, timeRequiredHours, academicValue, String.join(", ", requiredSkills), maxStudentsAllowed);
    }
}
