package com.studyplanner.dao;

import com.studyplanner.database.DatabaseConnection;
import com.studyplanner.model.Student;

import java.sql.*;

/**
 * Data Access Object for Student entity.
 */
public class StudentDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    public Student create(Student student) throws SQLException {
        String sql = "INSERT INTO Student (username, password, full_name, email, target_daily_hours) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getPassword());
            ps.setString(3, student.getFullName());
            ps.setString(4, student.getEmail());
            ps.setDouble(5, student.getTargetDailyHours());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }
            return student;
        }
    }

    public Student authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, target_daily_hours, created_at FROM Student WHERE username = ? AND password = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Student findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, target_daily_hours, created_at FROM Student WHERE username = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Student findById(int id) throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, target_daily_hours, created_at FROM Student WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE Student SET full_name = ?, email = ?, target_daily_hours = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getEmail());
            ps.setDouble(3, student.getTargetDailyHours());
            ps.setInt(4, student.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateDailyHours(int studentId, double hours) throws SQLException {
        String sql = "UPDATE Student SET target_daily_hours = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, hours);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setUsername(rs.getString("username"));
        student.setPassword(rs.getString("password"));
        student.setFullName(rs.getString("full_name"));
        student.setEmail(rs.getString("email"));
        student.setTargetDailyHours(rs.getDouble("target_daily_hours"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            student.setCreatedAt(ts.toLocalDateTime());
        }
        return student;
    }
}
