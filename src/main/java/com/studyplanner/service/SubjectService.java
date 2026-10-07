package com.studyplanner.service;

import com.studyplanner.dao.ProgressDAO;
import com.studyplanner.dao.SubjectDAO;
import com.studyplanner.model.Progress;
import com.studyplanner.model.Subject;
import com.studyplanner.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Service managing subjects and synchronizing with progress tracking.
 */
public class SubjectService {

    private final SubjectDAO subjectDAO;
    private final ProgressDAO progressDAO;

    public SubjectService() {
        this.subjectDAO = new SubjectDAO();
        this.progressDAO = new ProgressDAO();
    }

    public SubjectService(SubjectDAO subjectDAO, ProgressDAO progressDAO) {
        this.subjectDAO = subjectDAO;
        this.progressDAO = progressDAO;
    }

    public Subject addSubject(int studentId, String name, int difficulty, int totalTopics, int completedTopics, String targetGrade) throws SQLException {
        ValidationUtil.validateSubject(name, difficulty, totalTopics, completedTopics);

        Subject subject = new Subject(studentId, name.trim(), difficulty, totalTopics, completedTopics, targetGrade);
        Subject created = subjectDAO.create(subject);

        // Sync with Progress record
        Progress progress = new Progress(studentId, created.getId(), completedTopics, totalTopics);
        progressDAO.saveOrUpdate(progress);

        return created;
    }

    public List<Subject> getSubjectsForStudent(int studentId) throws SQLException {
        return subjectDAO.findByStudentId(studentId);
    }

    public Subject getSubjectById(int subjectId) throws SQLException {
        return subjectDAO.findById(subjectId);
    }

    public boolean updateSubject(Subject subject) throws SQLException {
        if (subject == null) return false;
        ValidationUtil.validateSubject(subject.getName(), subject.getDifficulty(), subject.getTotalTopics(), subject.getCompletedTopics());

        boolean ok = subjectDAO.update(subject);
        if (ok) {
            // Sync with progress
            Progress p = new Progress(subject.getStudentId(), subject.getId(), subject.getCompletedTopics(), subject.getTotalTopics());
            progressDAO.saveOrUpdate(p);
        }
        return ok;
    }

    public boolean updateCompletedTopics(int subjectId, int completedTopics) throws SQLException {
        Subject subject = subjectDAO.findById(subjectId);
        if (subject == null) return false;

        completedTopics = Math.max(0, Math.min(subject.getTotalTopics(), completedTopics));
        boolean ok = subjectDAO.updateCompletedTopics(subjectId, completedTopics);
        if (ok) {
            Progress p = new Progress(subject.getStudentId(), subjectId, completedTopics, subject.getTotalTopics());
            progressDAO.saveOrUpdate(p);
        }
        return ok;
    }

    public boolean deleteSubject(int subjectId) throws SQLException {
        progressDAO.deleteBySubjectId(subjectId);
        return subjectDAO.delete(subjectId);
    }
}
