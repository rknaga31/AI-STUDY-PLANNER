package com.studyplanner.dao;

import com.studyplanner.database.DatabaseConnection;
import com.studyplanner.model.Progress;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Progress entity.
 */
public class ProgressDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    public boolean saveOrUpdate(Progress progress) throws SQLException {
        Progress existing = findByStudentAndSubject(progress.getStudentId(), progress.getSubjectId());
        if (existing == null) {
            String sql = "INSERT INTO Progress (student_id, subject_id, completed_topics, total_topics, progress_percentage, last_updated) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, progress.getStudentId());
                ps.setInt(2, progress.getSubjectId());
                ps.setInt(3, progress.getCompletedTopics());
                ps.setInt(4, progress.getTotalTopics());
                ps.setDouble(5, progress.getProgressPercentage());
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            progress.setId(rs.getInt(1));
                        }
                    }
                    return true;
                }
                return false;
            }
        } else {
            String sql = "UPDATE Progress SET completed_topics = ?, total_topics = ?, progress_percentage = ?, last_updated = ? " +
                         "WHERE student_id = ? AND subject_id = ?";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, progress.getCompletedTopics());
                ps.setInt(2, progress.getTotalTopics());
                ps.setDouble(3, progress.getProgressPercentage());
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.setInt(5, progress.getStudentId());
                ps.setInt(6, progress.getSubjectId());
                progress.setId(existing.getId());
                return ps.executeUpdate() > 0;
            }
        }
    }

    public Progress findByStudentAndSubject(int studentId, int subjectId) throws SQLException {
        String sql = "SELECT p.*, s.name as subject_name FROM Progress p LEFT JOIN Subject s ON p.subject_id = s.id " +
                     "WHERE p.student_id = ? AND p.subject_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Progress> findByStudentId(int studentId) throws SQLException {
        List<Progress> list = new ArrayList<>();
        String sql = "SELECT p.*, s.name as subject_name FROM Progress p LEFT JOIN Subject s ON p.subject_id = s.id " +
                     "WHERE p.student_id = ? ORDER BY p.progress_percentage ASC";
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

    public boolean deleteBySubjectId(int subjectId) throws SQLException {
        String sql = "DELETE FROM Progress WHERE subject_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            return ps.executeUpdate() > 0;
        }
    }

    private Progress mapRow(ResultSet rs) throws SQLException {
        Progress p = new Progress();
        p.setId(rs.getInt("id"));
        p.setStudentId(rs.getInt("student_id"));
        p.setSubjectId(rs.getInt("subject_id"));
        p.setCompletedTopics(rs.getInt("completed_topics"));
        p.setTotalTopics(rs.getInt("total_topics"));
        p.setProgressPercentage(rs.getDouble("progress_percentage"));
        Timestamp ts = rs.getTimestamp("last_updated");
        if (ts != null) {
            p.setLastUpdated(ts.toLocalDateTime());
        }
        try {
            p.setSubjectName(rs.getString("subject_name"));
        } catch (SQLException ignored) {
        }
        return p;
    }
}
