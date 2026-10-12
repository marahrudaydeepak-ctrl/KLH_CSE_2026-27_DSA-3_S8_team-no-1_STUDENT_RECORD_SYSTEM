package model;

import java.io.Serializable;

public class AcademicActivity implements Serializable, Comparable<AcademicActivity> {
    private static final long serialVersionUID = 1L;

    private String activityId;
    private String title;
    private int startHour; // e.g. 9 for 9:00 AM
    private int endHour;   // e.g. 11 for 11:00 AM
    private int priorityWeight; // value/importance score

    public AcademicActivity() {}

    public AcademicActivity(String activityId, String title, int startHour, int endHour, int priorityWeight) {
        this.activityId = activityId;
        this.title = title;
        this.startHour = startHour;
        this.endHour = endHour;
        this.priorityWeight = priorityWeight;
    }

    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getStartHour() { return startHour; }
    public void setStartHour(int startHour) { this.startHour = startHour; }

    public int getEndHour() { return endHour; }
    public void setEndHour(int endHour) { this.endHour = endHour; }

    public int getPriorityWeight() { return priorityWeight; }
    public void setPriorityWeight(int priorityWeight) { this.priorityWeight = priorityWeight; }

    @Override
    public int compareTo(AcademicActivity o) {
        return Integer.compare(this.endHour, o.endHour);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-24s | Time: %02d:00 - %02d:00 | Weight: %d pts",
                activityId, title, startHour, endHour, priorityWeight);
    }
}
