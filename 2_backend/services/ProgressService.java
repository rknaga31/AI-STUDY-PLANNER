package com.studyplanner.service;

import com.studyplanner.dao.ProgressDAO;
import com.studyplanner.dao.StudySessionDAO;
import com.studyplanner.dao.StudyTaskDAO;
import com.studyplanner.dao.SubjectDAO;
import com.studyplanner.model.Progress;
import com.studyplanner.model.StudySession;
import com.studyplanner.model.Subject;
import com.studyplanner.model.TaskStatus;
import com.studyplanner.util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service managing student progress, logging study sessions, and task completion.
 */
public class ProgressService {

    private final ProgressDAO progressDAO;
    private final StudySessionDAO sessionDAO;
    private final SubjectDAO subjectDAO;
    private final StudyTaskDAO taskDAO;

    public ProgressService() {
        this.progressDAO = new ProgressDAO();
        this.sessionDAO = new StudySessionDAO();
        this.subjectDAO = new SubjectDAO();
        this.taskDAO = new StudyTaskDAO();
    }

    public ProgressService(ProgressDAO progressDAO, StudySessionDAO sessionDAO, SubjectDAO subjectDAO, StudyTaskDAO taskDAO) {
        this.progressDAO = progressDAO;
        this.sessionDAO = sessionDAO;
        this.subjectDAO = subjectDAO;
        this.taskDAO = taskDAO;
    }

    /**
     * Logs a study session, updates completed topics, and recalculates progress.
     */
    public StudySession logStudySession(int studentId, int subjectId, LocalDate date,
                                       int durationMinutes, String notes, int topicsIncrement) throws SQLException {
        ValidationUtil.validateStudySession(durationMinutes);

        StudySession session = new StudySession(studentId, subjectId, date, durationMinutes, notes != null ? notes.trim() : "");
        StudySession created = sessionDAO.create(session);

        if (topicsIncrement > 0) {
            Subject subject = subjectDAO.findById(subjectId);
            if (subject != null) {
                int newCompleted = Math.min(subject.getTotalTopics(), subject.getCompletedTopics() + topicsIncrement);
                subjectDAO.updateCompletedTopics(subjectId, newCompleted);

                Progress p = new Progress(studentId, subjectId, newCompleted, subject.getTotalTopics());
                progressDAO.saveOrUpdate(p);
            }
        }

        return created;
    }

    /**
     * Marks a study task as completed.
     */
    public boolean completeTask(int taskId) throws SQLException {
        return taskDAO.updateStatus(taskId, TaskStatus.COMPLETED);
    }

    /**
     * Updates topic counts directly and recalculates progress.
     */
    public boolean updateCompletedTopics(int studentId, int subjectId, int completedTopics) throws SQLException {
        Subject s = subjectDAO.findById(subjectId);
        if (s == null) return false;

        completedTopics = Math.max(0, Math.min(s.getTotalTopics(), completedTopics));
        subjectDAO.updateCompletedTopics(subjectId, completedTopics);

        Progress p = new Progress(studentId, subjectId, completedTopics, s.getTotalTopics());
        return progressDAO.saveOrUpdate(p);
    }

    public List<Progress> getStudentProgress(int studentId) throws SQLException {
        return progressDAO.findByStudentId(studentId);
    }

    public List<StudySession> getStudySessions(int studentId) throws SQLException {
        return sessionDAO.findByStudentId(studentId);
    }

    public int getTotalStudyMinutes(int studentId) throws SQLException {
        return sessionDAO.getTotalMinutesByStudent(studentId);
    }

    public double getOverallPreparationPercentage(int studentId) throws SQLException {
        List<Progress> list = progressDAO.findByStudentId(studentId);
        if (list == null || list.isEmpty()) return 0.0;

        int totalTopics = 0;
        int completedTopics = 0;
        for (Progress p : list) {
            totalTopics += p.getTotalTopics();
            completedTopics += p.getCompletedTopics();
        }

        if (totalTopics == 0) return 0.0;
        return ((double) completedTopics / totalTopics) * 100.0;
    }
}
