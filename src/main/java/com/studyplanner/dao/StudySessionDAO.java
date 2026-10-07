package com.studyplanner.dao;

import com.studyplanner.database.DatabaseConnection;
import com.studyplanner.model.StudySession;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for StudySession entity.
 */
public class StudySessionDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    public StudySession create(StudySession session) throws SQLException {
        String sql = "INSERT INTO StudySession (student_id, subject_id, session_date, duration_minutes, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, session.getStudentId());
            ps.setInt(2, session.getSubjectId());
            ps.setDate(3, Date.valueOf(session.getSessionDate()));
            ps.setInt(4, session.getDurationMinutes());
            ps.setString(5, session.getNotes());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    session.setId(rs.getInt(1));
                }
            }
            return session;
        }
    }

    public List<StudySession> findByStudentId(int studentId) throws SQLException {
        List<StudySession> list = new ArrayList<>();
        String sql = "SELECT ss.*, s.name as subject_name FROM StudySession ss LEFT JOIN Subject s ON ss.subject_id = s.id " +
                     "WHERE ss.student_id = ? ORDER BY ss.session_date DESC, ss.created_at DESC";
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

    public List<StudySession> findBySubjectId(int subjectId) throws SQLException {
        List<StudySession> list = new ArrayList<>();
        String sql = "SELECT ss.*, s.name as subject_name FROM StudySession ss LEFT JOIN Subject s ON ss.subject_id = s.id " +
                     "WHERE ss.subject_id = ? ORDER BY ss.session_date DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public int getTotalMinutesByStudent(int studentId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(duration_minutes), 0) FROM StudySession WHERE student_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int getTotalMinutesBySubject(int subjectId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(duration_minutes), 0) FROM StudySession WHERE subject_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private StudySession mapRow(ResultSet rs) throws SQLException {
        StudySession s = new StudySession();
        s.setId(rs.getInt("id"));
        s.setStudentId(rs.getInt("student_id"));
        s.setSubjectId(rs.getInt("subject_id"));
        Date d = rs.getDate("session_date");
        if (d != null) {
            s.setSessionDate(d.toLocalDate());
        }
        s.setDurationMinutes(rs.getInt("duration_minutes"));
        s.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            s.setCreatedAt(ts.toLocalDateTime());
        }
        try {
            s.setSubjectName(rs.getString("subject_name"));
        } catch (SQLException ignored) {
        }
        return s;
    }
}
