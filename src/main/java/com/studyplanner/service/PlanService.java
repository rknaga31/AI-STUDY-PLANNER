package com.studyplanner.service;

import com.studyplanner.dao.ExamDAO;
import com.studyplanner.dao.StudyTaskDAO;
import com.studyplanner.dao.SubjectDAO;
import com.studyplanner.model.Exam;
import com.studyplanner.model.PrioritizedSubject;
import com.studyplanner.model.StudyTask;
import com.studyplanner.model.Subject;
import com.studyplanner.planning.PriorityCalculator;
import com.studyplanner.planning.ScheduleGenerator;
import com.studyplanner.planning.SchedulePlan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Service orchestrating the Planning Engine, database persistence, and plan regeneration.
 */
public class PlanService {

    private final SubjectDAO subjectDAO;
    private final ExamDAO examDAO;
    private final StudyTaskDAO taskDAO;
    private final ScheduleGenerator scheduleGenerator;
    private final PriorityCalculator priorityCalculator;

    public PlanService() {
        this.subjectDAO = new SubjectDAO();
        this.examDAO = new ExamDAO();
        this.taskDAO = new StudyTaskDAO();
        this.priorityCalculator = new PriorityCalculator();
        this.scheduleGenerator = new ScheduleGenerator(this.priorityCalculator);
    }

    public PlanService(SubjectDAO subjectDAO, ExamDAO examDAO, StudyTaskDAO taskDAO, ScheduleGenerator generator) {
        this.subjectDAO = subjectDAO;
        this.examDAO = examDAO;
        this.taskDAO = taskDAO;
        this.priorityCalculator = new PriorityCalculator();
        this.scheduleGenerator = generator != null ? generator : new ScheduleGenerator(this.priorityCalculator);
    }

    /**
     * Generates a daily schedule for a student and persists the tasks to the database.
     */
    public SchedulePlan generateAndSaveDailyPlan(int studentId, double targetHours, LocalDate date) throws SQLException {
        if (date == null) date = LocalDate.now();

        List<Subject> subjects = subjectDAO.findByStudentId(studentId);
        Map<Integer, List<Exam>> subjectExamsMap = buildSubjectExamsMap(studentId);

        SchedulePlan plan = scheduleGenerator.generateSchedule(studentId, subjects, subjectExamsMap, targetHours, date);

        // Clear existing non-completed tasks for this date before saving newly generated ones
        taskDAO.deleteScheduledForDate(studentId, date);

        if (!plan.getGeneratedTasks().isEmpty()) {
            taskDAO.createBatch(plan.getGeneratedTasks());
        }

        return plan;
    }

    /**
     * Regenerates the plan when progress changes or user requests re-planning.
     */
    public SchedulePlan regeneratePlanForStudent(int studentId, double targetHours, LocalDate date) throws SQLException {
        return generateAndSaveDailyPlan(studentId, targetHours, date);
    }

    /**
     * Calculates current priority ranking and rule-based explanations for all subjects
     * using the Planning Engine's PriorityQueue.
     */
    public List<PrioritizedSubject> calculatePriorityOverview(int studentId) throws SQLException {
        List<Subject> subjects = subjectDAO.findByStudentId(studentId);
        Map<Integer, List<Exam>> subjectExamsMap = buildSubjectExamsMap(studentId);

        PriorityQueue<PrioritizedSubject> queue = new PriorityQueue<>();
        for (Subject s : subjects) {
            List<Exam> exams = subjectExamsMap.getOrDefault(s.getId(), Collections.emptyList());
            PrioritizedSubject ps = priorityCalculator.calculate(s, exams, LocalDate.now());
            queue.offer(ps);
        }

        List<PrioritizedSubject> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            result.add(queue.poll());
        }
        return result;
    }

    public List<StudyTask> getTasksForStudent(int studentId) throws SQLException {
        return taskDAO.findByStudentId(studentId);
    }

    public List<StudyTask> getTasksForDate(int studentId, LocalDate date) throws SQLException {
        return taskDAO.findByStudentIdAndDate(studentId, date != null ? date : LocalDate.now());
    }

    public List<StudyTask> getPendingTasks(int studentId) throws SQLException {
        return taskDAO.findPendingTasks(studentId);
    }

    public boolean deleteTask(int taskId) throws SQLException {
        return taskDAO.delete(taskId);
    }

    private Map<Integer, List<Exam>> buildSubjectExamsMap(int studentId) throws SQLException {
        List<Exam> allExams = examDAO.findByStudentId(studentId);
        Map<Integer, List<Exam>> map = new HashMap<>();
        for (Exam e : allExams) {
            map.computeIfAbsent(e.getSubjectId(), k -> new ArrayList<>()).add(e);
        }
        return map;
    }
}
