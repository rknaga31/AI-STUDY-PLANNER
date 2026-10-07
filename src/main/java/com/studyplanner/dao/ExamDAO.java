package com.studyplanner.dao;

import com.studyplanner.database.DatabaseConnection;
import com.studyplanner.model.Exam;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Exam entity.
 */
public class ExamDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    public Exam create(Exam exam) throws SQLException {
        String sql = "INSERT INTO Exam (subject_id, exam_name, exam_date, weightage_percentage, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, exam.getSubjectId());
            ps.setString(2, exam.getExamName());
            ps.setDate(3, Date.valueOf(exam.getExamDate()));
            ps.setDouble(4, exam.getWeightagePercentage());
            ps.setString(5, exam.getNotes());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    exam.setId(rs.getInt(1));
                }
            }
            return exam;
        }
    }

    public Exam findById(int id) throws SQLException {
        String sql = "SELECT e.id, e.subject_id, e.exam_name, e.exam_date, e.weightage_percentage, e.notes, e.created_at, s.name as subject_name " +
                     "FROM Exam e LEFT JOIN Subject s ON e.subject_id = s.id WHERE e.id = ?";
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

    public List<Exam> findBySubjectId(int subjectId) throws SQLException {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT e.id, e.subject_id, e.exam_name, e.exam_date, e.weightage_percentage, e.notes, e.created_at, s.name as subject_name " +
                     "FROM Exam e LEFT JOIN Subject s ON e.subject_id = s.id WHERE e.subject_id = ? ORDER BY e.exam_date ASC";
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

    public List<Exam> findByStudentId(int studentId) throws SQLException {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT e.id, e.subject_id, e.exam_name, e.exam_date, e.weightage_percentage, e.notes, e.created_at, s.name as subject_name " +
                     "FROM Exam e JOIN Subject s ON e.subject_id = s.id WHERE s.student_id = ? ORDER BY e.exam_date ASC";
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

    public Exam findNearestUpcomingBySubjectId(int subjectId) throws SQLException {
        String sql = "SELECT e.id, e.subject_id, e.exam_name, e.exam_date, e.weightage_percentage, e.notes, e.created_at, s.name as subject_name " +
                     "FROM Exam e LEFT JOIN Subject s ON e.subject_id = s.id " +
                     "WHERE e.subject_id = ? AND e.exam_date >= ? ORDER BY e.exam_date ASC LIMIT 1";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean update(Exam exam) throws SQLException {
        String sql = "UPDATE Exam SET exam_name = ?, exam_date = ?, weightage_percentage = ?, notes = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, exam.getExamName());
            ps.setDate(2, Date.valueOf(exam.getExamDate()));
            ps.setDouble(3, exam.getWeightagePercentage());
            ps.setString(4, exam.getNotes());
            ps.setInt(5, exam.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Exam WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Exam mapRow(ResultSet rs) throws SQLException {
        Exam e = new Exam();
        e.setId(rs.getInt("id"));
        e.setSubjectId(rs.getInt("subject_id"));
        e.setExamName(rs.getString("exam_name"));
        Date d = rs.getDate("exam_date");
        if (d != null) {
            e.setExamDate(d.toLocalDate());
        }
        e.setWeightagePercentage(rs.getDouble("weightage_percentage"));
        e.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            e.setCreatedAt(ts.toLocalDateTime());
        }
        try {
            e.setSubjectName(rs.getString("subject_name"));
        } catch (SQLException ignored) {
        }
        return e;
    }
}
