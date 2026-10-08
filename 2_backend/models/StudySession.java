package com.studyplanner.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing a logged study session.
 */
public class StudySession {
    private int id;
    private int studentId;
    private int subjectId;
    private LocalDate sessionDate;
    private int durationMinutes;
    private String notes;
    private LocalDateTime createdAt;

    // Transient field for UI display
    private String subjectName;

    public StudySession() {
        this.sessionDate = LocalDate.now();
        this.durationMinutes = 60;
    }

    public StudySession(int studentId, int subjectId, LocalDate sessionDate, int durationMinutes, String notes) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.sessionDate = sessionDate != null ? sessionDate : LocalDate.now();
        this.durationMinutes = durationMinutes;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public StudySession(int id, int studentId, int subjectId, LocalDate sessionDate, int durationMinutes, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.sessionDate = sessionDate;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
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

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
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

    public double getDurationHours() {
        return durationMinutes / 60.0;
    }

    @Override
    public String toString() {
        return "StudySession{" +
                "id=" + id +
                ", subjectId=" + subjectId +
                ", date=" + sessionDate +
                ", duration=" + durationMinutes + " mins" +
                '}';
    }
}
