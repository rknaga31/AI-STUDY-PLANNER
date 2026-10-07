package com.studyplanner.dao;

import com.studyplanner.database.DatabaseConnection;
import com.studyplanner.model.StudyTask;
import com.studyplanner.model.TaskStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for StudyTask entity.
 */
public class StudyTaskDAO {

    private final DatabaseConnection db = DatabaseConnection.getInstance();

    public StudyTask create(StudyTask task) throws SQLException {
        String sql = "INSERT INTO StudyTask (student_id, subject_id, title, estimated_hours, status, priority_score, scheduled_date, reason_explanation) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, task.getStudentId());
            ps.setInt(2, task.getSubjectId());
            ps.setString(3, task.getTitle());
            ps.setDouble(4, task.getEstimatedHours());
            ps.setString(5, task.getStatus().name());
            ps.setDouble(6, task.getPriorityScore());
            ps.setDate(7, Date.valueOf(task.getScheduledDate()));
            ps.setString(8, task.getReasonExplanation());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getInt(1));
                }
            }
            return task;
        }
    }

    public void createBatch(List<StudyTask> tasks) throws SQLException {
        if (tasks == null || tasks.isEmpty()) return;
        String sql = "INSERT INTO StudyTask (student_id, subject_id, title, estimated_hours, status, priority_score, scheduled_date, reason_explanation) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (StudyTask task : tasks) {
                    ps.setInt(1, task.getStudentId());
                    ps.setInt(2, task.getSubjectId());
                    ps.setString(3, task.getTitle());
                    ps.setDouble(4, task.getEstimatedHours());
                    ps.setString(5, task.getStatus().name());
                    ps.setDouble(6, task.getPriorityScore());
                    ps.setDate(7, Date.valueOf(task.getScheduledDate()));
                    ps.setString(8, task.getReasonExplanation());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            task.setId(rs.getInt(1));
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public StudyTask findById(int id) throws SQLException {
        String sql = "SELECT t.*, s.name as subject_name FROM StudyTask t LEFT JOIN Subject s ON t.subject_id = s.id WHERE t.id = ?";
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

    public List<StudyTask> findByStudentId(int studentId) throws SQLException {
        List<StudyTask> list = new ArrayList<>();
        String sql = "SELECT t.*, s.name as subject_name FROM StudyTask t LEFT JOIN Subject s ON t.subject_id = s.id " +
                     "WHERE t.student_id = ? ORDER BY t.scheduled_date DESC, t.priority_score DESC";
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

    public List<StudyTask> findByStudentIdAndDate(int studentId, LocalDate date) throws SQLException {
        List<StudyTask> list = new ArrayList<>();
        String sql = "SELECT t.*, s.name as subject_name FROM StudyTask t LEFT JOIN Subject s ON t.subject_id = s.id " +
                     "WHERE t.student_id = ? AND t.scheduled_date = ? ORDER BY t.priority_score DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<StudyTask> findPendingTasks(int studentId) throws SQLException {
        List<StudyTask> list = new ArrayList<>();
        String sql = "SELECT t.*, s.name as subject_name FROM StudyTask t LEFT JOIN Subject s ON t.subject_id = s.id " +
                     "WHERE t.student_id = ? AND t.status != 'COMPLETED' ORDER BY t.scheduled_date ASC, t.priority_score DESC";
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

    public boolean update(StudyTask task) throws SQLException {
        String sql = "UPDATE StudyTask SET title = ?, estimated_hours = ?, status = ?, priority_score = ?, scheduled_date = ?, reason_explanation = ?, completed_at = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setDouble(2, task.getEstimatedHours());
            ps.setString(3, task.getStatus().name());
            ps.setDouble(4, task.getPriorityScore());
            ps.setDate(5, Date.valueOf(task.getScheduledDate()));
            ps.setString(6, task.getReasonExplanation());
            if (task.getCompletedAt() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(task.getCompletedAt()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }
            ps.setInt(8, task.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int taskId, TaskStatus status) throws SQLException {
        String sql = "UPDATE StudyTask SET status = ?, completed_at = ? WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            if (status == TaskStatus.COMPLETED) {
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            } else {
                ps.setNull(2, Types.TIMESTAMP);
            }
            ps.setInt(3, taskId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM StudyTask WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteScheduledForDate(int studentId, LocalDate date) throws SQLException {
        String sql = "DELETE FROM StudyTask WHERE student_id = ? AND scheduled_date = ? AND status != 'COMPLETED'";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, Date.valueOf(date));
            return ps.executeUpdate() >= 0;
        }
    }

    private StudyTask mapRow(ResultSet rs) throws SQLException {
        StudyTask task = new StudyTask();
        task.setId(rs.getInt("id"));
        task.setStudentId(rs.getInt("student_id"));
        task.setSubjectId(rs.getInt("subject_id"));
        task.setTitle(rs.getString("title"));
        task.setEstimatedHours(rs.getDouble("estimated_hours"));
        task.setStatus(TaskStatus.fromString(rs.getString("status")));
        task.setPriorityScore(rs.getDouble("priority_score"));
        Date d = rs.getDate("scheduled_date");
        if (d != null) {
            task.setScheduledDate(d.toLocalDate());
        }
        task.setReasonExplanation(rs.getString("reason_explanation"));
        Timestamp cat = rs.getTimestamp("completed_at");
        if (cat != null) {
            task.setCompletedAt(cat.toLocalDateTime());
        }
        Timestamp cr = rs.getTimestamp("created_at");
        if (cr != null) {
            task.setCreatedAt(cr.toLocalDateTime());
        }
        try {
            task.setSubjectName(rs.getString("subject_name"));
        } catch (SQLException ignored) {
        }
        return task;
    }
}
