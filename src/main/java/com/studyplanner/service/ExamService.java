package com.studyplanner.service;

import com.studyplanner.dao.ExamDAO;
import com.studyplanner.model.Exam;
import com.studyplanner.util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service managing upcoming exams, exam dates, and weightages.
 */
public class ExamService {

    private final ExamDAO examDAO;

    public ExamService() {
        this.examDAO = new ExamDAO();
    }

    public ExamService(ExamDAO examDAO) {
        this.examDAO = examDAO;
    }

    public Exam addExam(int subjectId, String examName, LocalDate examDate, double weightage, String notes) throws SQLException {
        ValidationUtil.validateExam(examName, examDate, weightage);

        Exam exam = new Exam(subjectId, examName.trim(), examDate, weightage, notes != null ? notes.trim() : "");
        return examDAO.create(exam);
    }

    public List<Exam> getExamsForSubject(int subjectId) throws SQLException {
        return examDAO.findBySubjectId(subjectId);
    }

    public List<Exam> getExamsForStudent(int studentId) throws SQLException {
        return examDAO.findByStudentId(studentId);
    }

    public List<Exam> getUpcomingExams(int studentId) throws SQLException {
        List<Exam> all = examDAO.findByStudentId(studentId);
        List<Exam> upcoming = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Exam e : all) {
            if (e.getExamDate() != null && !e.getExamDate().isBefore(today)) {
                upcoming.add(e);
            }
        }
        return upcoming;
    }

    public boolean updateExam(Exam exam) throws SQLException {
        if (exam == null) return false;
        ValidationUtil.validateExam(exam.getExamName(), exam.getExamDate(), exam.getWeightagePercentage());
        return examDAO.update(exam);
    }

    public boolean deleteExam(int examId) throws SQLException {
        return examDAO.delete(examId);
    }
}
