package com.studyplanner.model;

import java.time.LocalDateTime;

/**
 * Model representing a Student user in the system.
 */
public class Student {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private double targetDailyHours;
    private LocalDateTime createdAt;

    public Student() {
        this.targetDailyHours = 4.0;
    }

    public Student(String username, String password, String fullName, String email, double targetDailyHours) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.targetDailyHours = targetDailyHours > 0 ? targetDailyHours : 4.0;
        this.createdAt = LocalDateTime.now();
    }

    public Student(int id, String username, String password, String fullName, String email, double targetDailyHours, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.targetDailyHours = targetDailyHours;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getTargetDailyHours() {
        return targetDailyHours;
    }

    public void setTargetDailyHours(double targetDailyHours) {
        this.targetDailyHours = targetDailyHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", targetDailyHours=" + targetDailyHours +
                '}';
    }
}
