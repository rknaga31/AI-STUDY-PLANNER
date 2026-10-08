package com.studyplanner.model;

import java.time.LocalDateTime;

/**
 * Model representing progress status for a subject.
 */
public class Progress {
    private int id;
    private int studentId;
    private int subjectId;
    private int completedTopics;
    private int totalTopics;
    private double progressPercentage;
    private LocalDateTime lastUpdated;

    // Transient field for UI display
    private String subjectName;

    public Progress() {
        this.completedTopics = 0;
        this.totalTopics = 10;
        this.progressPercentage = 0.0;
        this.lastUpdated = LocalDateTime.now();
    }

    public Progress(int studentId, int subjectId, int completedTopics, int totalTopics) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.completedTopics = completedTopics;
        this.totalTopics = Math.max(1, totalTopics);
        this.progressPercentage = ((double) completedTopics / this.totalTopics) * 100.0;
        this.lastUpdated = LocalDateTime.now();
    }

    public Progress(int id, int studentId, int subjectId, int completedTopics, int totalTopics,
                    double progressPercentage, LocalDateTime lastUpdated) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.completedTopics = completedTopics;
        this.totalTopics = totalTopics;
        this.progressPercentage = progressPercentage;
        this.lastUpdated = lastUpdated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getCompletedTopics() {
        return completedTopics;
    }

    public void setCompletedTopics(int completedTopics) {
        this.completedTopics = completedTopics;
        recalculatePercentage();
    }

    public int getTotalTopics() {
        return totalTopics;
    }

    public void setTotalTopics(int totalTopics) {
        this.totalTopics = Math.max(1, totalTopics);
        recalculatePercentage();
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    private void recalculatePercentage() {
        if (totalTopics > 0) {
            this.progressPercentage = Math.min(100.0, ((double) completedTopics / totalTopics) * 100.0);
        } else {
            this.progressPercentage = 0.0;
        }
    }

    @Override
    public String toString() {
        return "Progress{" +
                "subjectId=" + subjectId +
                ", " + completedTopics + "/" + totalTopics +
                " (" + String.format("%.1f", progressPercentage) + "%)" +
                '}';
    }
}
