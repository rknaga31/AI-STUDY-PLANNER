package com.studyplanner.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Model representing an upcoming Exam for a subject.
 */
public class Exam {
    private int id;
    private int subjectId;
    private String examName;
    private LocalDate examDate;
    private double weightagePercentage;
    private String notes;
    private LocalDateTime createdAt;

    // Transient helper field for display joins
    private String subjectName;

    public Exam() {
        this.weightagePercentage = 30.0;
        this.examDate = LocalDate.now().plusDays(7);
    }

    public Exam(int subjectId, String examName, LocalDate examDate, double weightagePercentage, String notes) {
        this.subjectId = subjectId;
        this.examName = examName;
        this.examDate = examDate;
        this.weightagePercentage = weightagePercentage;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public Exam(int id, int subjectId, String examName, LocalDate examDate, double weightagePercentage, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.subjectId = subjectId;
        this.examName = examName;
        this.examDate = examDate;
        this.weightagePercentage = weightagePercentage;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public double getWeightagePercentage() {
        return weightagePercentage;
    }

    public void setWeightagePercentage(double weightagePercentage) {
        this.weightagePercentage = weightagePercentage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public long getDaysRemaining(LocalDate fromDate) {
        if (examDate == null || fromDate == null) return Long.MAX_VALUE;
        return ChronoUnit.DAYS.between(fromDate, examDate);
    }

    public long getDaysRemaining() {
        return getDaysRemaining(LocalDate.now());
    }

    @Override
    public String toString() {
        return examName + " (" + examDate + ")";
    }
}
