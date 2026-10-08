package com.studyplanner.model;

import java.time.LocalDateTime;

/**
 * Model representing an academic Subject.
 */
public class Subject {
    private int id;
    private int studentId;
    private String name;
    private int difficulty; // 1 to 5
    private int totalTopics;
    private int completedTopics;
    private String targetGrade;
    private LocalDateTime createdAt;

    public Subject() {
        this.difficulty = 3;
        this.totalTopics = 10;
        this.completedTopics = 0;
        this.targetGrade = "A";
    }

    public Subject(int studentId, String name, int difficulty, int totalTopics, int completedTopics, String targetGrade) {
        this.studentId = studentId;
        this.name = name;
        this.difficulty = Math.max(1, Math.min(5, difficulty));
        this.totalTopics = Math.max(1, totalTopics);
        this.completedTopics = Math.max(0, Math.min(totalTopics, completedTopics));
        this.targetGrade = targetGrade != null ? targetGrade : "A";
        this.createdAt = LocalDateTime.now();
    }

    public Subject(int id, int studentId, String name, int difficulty, int totalTopics, int completedTopics, String targetGrade, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.difficulty = Math.max(1, Math.min(5, difficulty));
        this.totalTopics = Math.max(1, totalTopics);
        this.completedTopics = Math.max(0, Math.min(totalTopics, completedTopics));
        this.targetGrade = targetGrade;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = Math.max(1, Math.min(5, difficulty));
    }

    public int getTotalTopics() {
        return totalTopics;
    }

    public void setTotalTopics(int totalTopics) {
        this.totalTopics = Math.max(1, totalTopics);
    }

    public int getCompletedTopics() {
        return completedTopics;
    }

    public void setCompletedTopics(int completedTopics) {
        this.completedTopics = Math.max(0, Math.min(this.totalTopics, completedTopics));
    }

    public String getTargetGrade() {
        return targetGrade;
    }

    public void setTargetGrade(String targetGrade) {
        this.targetGrade = targetGrade;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getIncompleteTopics() {
        return Math.max(0, totalTopics - completedTopics);
    }

    public double getPreparationPercentage() {
        if (totalTopics <= 0) return 0.0;
        return (double) completedTopics / totalTopics * 100.0;
    }

    public String getDifficultyLabel() {
        switch (difficulty) {
            case 1: return "Very Easy";
            case 2: return "Easy";
            case 3: return "Moderate";
            case 4: return "Hard";
            case 5: return "Very Hard";
            default: return "Moderate";
        }
    }

    @Override
    public String toString() {
        return name + " (" + getDifficultyLabel() + ")";
    }
}
