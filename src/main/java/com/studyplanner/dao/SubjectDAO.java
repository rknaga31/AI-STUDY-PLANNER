package com.studyplanner.dao;

import com.studyplanner.database.DatabaseConnection;
import com.studyplanner.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Subject entity.
 */
public class SubjectDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    public Subject create(Subject subject) throws SQLException {
        String sql = "INSERT INTO Subject (student_id, name, difficulty, total_topics, completed_topics, target_grade) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, subject.getStudentId());
            ps.setString(2, subject.getName());
            ps.setInt(3, subject.getDifficulty());
            ps.setInt(4, subject.getTotalTopics());
            ps.setInt(5, subject.getCompletedTopics());
            ps.setString(6, subject.getTargetGrade());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    subject.setId(rs.getInt(1));
                }
            }
            return subject;
        }
    }

    public Subject findById(int id) throws SQLException {
        String sql = "SELECT id, student_id, name, difficulty, total_topics, completed_topics, target_grade, created_at FROM Subject WHERE id = ?";
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

    public List<Subject> findByStudentId(int studentId) throws SQLException {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT id, student_id, name, difficulty, total_topics, completed_topics, target_grade, created_at FROM Subject WHERE student_id = ? ORDER BY difficulty DESC, name ASC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public boolean update(Subject subject) throws SQLException {
        String sql = "UPDATE Subject SET name = ?, difficulty = ?, total_topics = ?, completed_topics = ?, target_grade = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject.getName());
            ps.setInt(2, subject.getDifficulty());
            ps.setInt(3, subject.getTotalTopics());
            ps.setInt(4, subject.getCompletedTopics());
            ps.setString(5, subject.getTargetGrade());
            ps.setInt(6, subject.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateCompletedTopics(int subjectId, int completedTopics) throws SQLException {
        String sql = "UPDATE Subject SET completed_topics = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, completedTopics);
            ps.setInt(2, subjectId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Subject WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Subject mapRow(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setId(rs.getInt("id"));
        s.setStudentId(rs.getInt("student_id"));
        s.setName(rs.getString("name"));
        s.setDifficulty(rs.getInt("difficulty"));
        s.setTotalTopics(rs.getInt("total_topics"));
        s.setCompletedTopics(rs.getInt("completed_topics"));
        s.setTargetGrade(rs.getString("target_grade"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            s.setCreatedAt(ts.toLocalDateTime());
        }
        return s;
    }
}
