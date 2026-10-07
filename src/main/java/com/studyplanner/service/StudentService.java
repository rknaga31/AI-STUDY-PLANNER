package com.studyplanner.service;

import com.studyplanner.dao.StudentDAO;
import com.studyplanner.model.Student;
import com.studyplanner.util.SessionContext;
import com.studyplanner.util.ValidationUtil;

import java.sql.SQLException;

/**
 * Service handling student account authentication, profile management, and settings.
 */
public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public Student register(String username, String password, String fullName, String email, double targetHours) throws Exception {
        ValidationUtil.validateStudent(username, password, fullName, email, targetHours);

        Student existing = studentDAO.findByUsername(username.trim());
        if (existing != null) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken. Please choose another.");
        }

        Student student = new Student(username.trim(), password.trim(), fullName.trim(), email != null ? email.trim() : "", targetHours);
        Student created = studentDAO.create(student);
        SessionContext.getInstance().setCurrentStudent(created);
        return created;
    }

    public Student login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password are required.");
        }

        Student student = studentDAO.authenticate(username.trim(), password.trim());
        if (student == null) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        SessionContext.getInstance().setCurrentStudent(student);
        return student;
    }

    public boolean updateProfile(Student student) throws SQLException {
        if (student == null) return false;
        ValidationUtil.validateStudent(student.getUsername(), student.getPassword(), student.getFullName(), student.getEmail(), student.getTargetDailyHours());
        boolean ok = studentDAO.update(student);
        if (ok) {
            SessionContext.getInstance().setCurrentStudent(student);
        }
        return ok;
    }

    public boolean updateTargetHours(int studentId, double hours) throws SQLException {
        if (hours <= 0 || hours > 16) {
            throw new IllegalArgumentException("Target daily hours must be between 0.5 and 16.");
        }
        boolean ok = studentDAO.updateDailyHours(studentId, hours);
        Student current = SessionContext.getInstance().getCurrentStudent();
        if (ok && current != null && current.getId() == studentId) {
            current.setTargetDailyHours(hours);
        }
        return ok;
    }

    public Student getStudent(int studentId) throws SQLException {
        return studentDAO.findById(studentId);
    }
}
