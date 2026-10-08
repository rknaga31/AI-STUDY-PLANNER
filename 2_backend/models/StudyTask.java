package com.studyplanner.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing an assigned/generated study task.
 */
public class StudyTask {
    private int id;
    private int studentId;
    private int subjectId;
    private String title;
    private double estimatedHours;
    private TaskStatus status;
    private double priorityScore;
    private LocalDate scheduledDate;
    private String reasonExplanation;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    // Transient fields for UI display
    private String subjectName;

    public StudyTask() {
        this.status = TaskStatus.PENDING;
        this.scheduledDate = LocalDate.now();
        this.estimatedHours = 1.0;
    }

    public StudyTask(int studentId, int subjectId, String title, double estimatedHours,
                     TaskStatus status, double priorityScore, LocalDate scheduledDate, String reasonExplanation) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.title = title;
        this.estimatedHours = estimatedHours;
        this.status = status != null ? status : TaskStatus.PENDING;
        this.priorityScore = priorityScore;
        this.scheduledDate = scheduledDate != null ? scheduledDate : LocalDate.now();
        this.reasonExplanation = reasonExplanation;
        this.createdAt = LocalDateTime.now();
    }

    public StudyTask(int id, int studentId, int subjectId, String title, double estimatedHours,
                     TaskStatus status, double priorityScore, LocalDate scheduledDate,
                     String reasonExplanation, LocalDateTime completedAt, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.title = title;
        this.estimatedHours = estimatedHours;
        this.status = status != null ? status : TaskStatus.PENDING;
        this.priorityScore = priorityScore;
        this.scheduledDate = scheduledDate;
        this.reasonExplanation = reasonExplanation;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        if (status == TaskStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        } else if (status != TaskStatus.COMPLETED) {
            this.completedAt = null;
        }
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getReasonExplanation() {
        return reasonExplanation;
    }

    public void setReasonExplanation(String reasonExplanation) {
        this.reasonExplanation = reasonExplanation;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return title + " (" + estimatedHours + "h) - " + status;
    }
}
