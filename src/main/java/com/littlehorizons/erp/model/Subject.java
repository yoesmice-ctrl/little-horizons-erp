package com.littlehorizons.erp.model;

public class Subject {
    private int subjectId;
    private String subjectCode;
    private String subjectName;
    private String description;
    private int gradeId;
    private int weeklyHours;
    private boolean active;

    public Subject() {}

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getGradeId() { return gradeId; }
    public void setGradeId(int gradeId) { this.gradeId = gradeId; }
    public int getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(int weeklyHours) { this.weeklyHours = weeklyHours; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
