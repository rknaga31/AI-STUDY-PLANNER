package com.studyplanner.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Validation utilities for application inputs and business invariants.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static void validateStudent(String username, String password, String fullName, String email, double targetHours) {
        if (username == null || username.trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long.");
        }
        if (password == null || password.trim().length() < 3) {
            throw new IllegalArgumentException("Password must be at least 3 characters long.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (targetHours <= 0 || targetHours > 16) {
            throw new IllegalArgumentException("Target daily study hours must be between 0.5 and 16 hours.");
        }
    }

    public static void validateSubject(String name, int difficulty, int totalTopics, int completedTopics) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be empty.");
        }
        if (difficulty < 1 || difficulty > 5) {
            throw new IllegalArgumentException("Difficulty must be between 1 (Very Easy) and 5 (Very Hard).");
        }
        if (totalTopics < 1) {
            throw new IllegalArgumentException("Total topics must be at least 1.");
        }
        if (completedTopics < 0 || completedTopics > totalTopics) {
            throw new IllegalArgumentException("Completed topics must be between 0 and total topics (" + totalTopics + ").");
        }
    }

    public static void validateExam(String examName, LocalDate examDate, double weightage) {
        if (examName == null || examName.trim().isEmpty()) {
            throw new IllegalArgumentException("Exam name cannot be empty.");
        }
        if (examDate == null) {
            throw new IllegalArgumentException("Exam date is required.");
        }
        if (weightage < 0 || weightage > 100) {
            throw new IllegalArgumentException("Weightage must be between 0% and 100%.");
        }
    }

    public static void validateStudySession(int durationMinutes) {
        if (durationMinutes <= 0 || durationMinutes > 720) {
            throw new IllegalArgumentException("Study session duration must be between 1 and 720 minutes.");
        }
    }
}
