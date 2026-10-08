package com.studyplanner.service;

import com.studyplanner.model.*;
import com.studyplanner.planning.SchedulePlan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceWorkflowTest {

    @Test
    @DisplayName("End-to-End Service Workflow: Registration -> Add Subject & Exam -> Generate Plan -> Study & Update Progress -> Regenerate Plan")
    void testEndToEndWorkflow() throws Exception {
        StudentService studentService = new StudentService();
        SubjectService subjectService = new SubjectService();
        ExamService examService = new ExamService();
        PlanService planService = new PlanService();
        ProgressService progressService = new ProgressService();

        String uniqueUser = "testuser_" + System.currentTimeMillis();

        // 1. Register & Login
        Student student = studentService.register(uniqueUser, "pass123", "Test Student", "test@domain.com", 3.5);
        assertNotNull(student);
        assertTrue(student.getId() > 0);

        Student loggedIn = studentService.login(uniqueUser, "pass123");
        assertEquals(student.getId(), loggedIn.getId());

        // 2. Add Subjects
        Subject s1 = subjectService.addSubject(student.getId(), "Object Oriented Java", 4, 10, 2, "A");
        assertNotNull(s1);
        assertEquals(2, s1.getCompletedTopics());
        assertEquals(10, s1.getTotalTopics());

        Subject s2 = subjectService.addSubject(student.getId(), "Cloud Computing", 2, 8, 5, "B");
        assertNotNull(s2);

        // 3. Add Exam for Subject 1 (Approaching in 3 days)
        Exam exam = examService.addExam(s1.getId(), "Java Midterm Test", LocalDate.now().plusDays(3), 30.0, "Units 1-3");
        assertNotNull(exam);
        assertTrue(exam.getId() > 0);

        // 4. Generate Initial Plan
        SchedulePlan plan = planService.generateAndSaveDailyPlan(student.getId(), student.getTargetDailyHours(), LocalDate.now());
        assertNotNull(plan);
        assertFalse(plan.getGeneratedTasks().isEmpty());

        // Java should be the top priority subject
        PrioritizedSubject topSub = plan.getPrioritizedQueueOrder().get(0);
        assertEquals("Object Oriented Java", topSub.getSubjectName());
        assertTrue(topSub.getReasonExplanation().contains("Object Oriented Java"));

        // 5. Study & Log Session (advancing 2 topics in Java)
        StudySession session = progressService.logStudySession(
                student.getId(),
                s1.getId(),
                LocalDate.now(),
                90,
                "Covered Inheritance and Polymorphism",
                2 // increment completed topics by 2
        );
        assertNotNull(session);
        assertEquals(90, session.getDurationMinutes());

        // Verify updated topic count
        Subject updatedS1 = subjectService.getSubjectById(s1.getId());
        assertEquals(4, updatedS1.getCompletedTopics(), "Completed topics should have increased from 2 to 4");
        assertEquals(40.0, updatedS1.getPreparationPercentage(), 0.01);

        // Complete first task in today's schedule
        StudyTask firstTask = plan.getGeneratedTasks().get(0);
        boolean taskDone = progressService.completeTask(firstTask.getId());
        assertTrue(taskDone);

        // 6. Regenerate Plan when progress changes
        SchedulePlan regeneratedPlan = planService.regeneratePlanForStudent(student.getId(), student.getTargetDailyHours(), LocalDate.now());
        assertNotNull(regeneratedPlan);
        assertNotNull(regeneratedPlan.getSummaryMessage());

        // Verify progress stats
        int totalMinutes = progressService.getTotalStudyMinutes(student.getId());
        assertEquals(90, totalMinutes);

        double overallPrep = progressService.getOverallPreparationPercentage(student.getId());
        assertTrue(overallPrep > 0, "Overall preparation percentage should be positive");
    }
}
