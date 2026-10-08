package com.studyplanner.planning;

import com.studyplanner.model.Exam;
import com.studyplanner.model.PrioritizedSubject;
import com.studyplanner.model.StudyTask;
import com.studyplanner.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlanningEngineTest {

    private PriorityCalculator calculator;
    private ScheduleGenerator generator;

    @BeforeEach
    void setUp() {
        calculator = new PriorityCalculator();
        generator = new ScheduleGenerator(calculator);
    }

    @Test
    @DisplayName("Should prioritize high difficulty, low preparation, and imminent exam")
    void testPriorityCalculationUrgency() {
        LocalDate today = LocalDate.now();

        // Urgent Subject: Java (Diff 4, 25% prep, exam in 3 days)
        Subject javaSub = new Subject(1, "Java", 4, 12, 3, "A");
        List<Exam> javaExams = List.of(new Exam(1, "Java Midterm", today.plusDays(3), 30.0, ""));
        PrioritizedSubject psJava = calculator.calculate(javaSub, javaExams, today);

        // Relaxed Subject: Web Tech (Diff 2, 80% prep, no urgent exam)
        Subject webSub = new Subject(1, "Web Tech", 2, 10, 8, "B");
        PrioritizedSubject psWeb = calculator.calculate(webSub, Collections.emptyList(), today);

        assertTrue(psJava.getPriorityScore() > psWeb.getPriorityScore(),
                "Java with upcoming exam and low prep should have higher priority score than Web Tech");
        assertTrue(psJava.getPriorityScore() >= 70.0, "Urgent Java subject score should be high");

        // Verify rule explanation contains reasoning components
        String explanation = psJava.getReasonExplanation();
        assertNotNull(explanation);
        assertTrue(explanation.contains("Java"), "Explanation should mention subject name");
        assertTrue(explanation.contains("difficulty is high"), "Explanation should mention difficulty");
        assertTrue(explanation.contains("low"), "Explanation should note low preparation");
        assertTrue(explanation.contains("approaching"), "Explanation should note approaching exam");
    }

    @Test
    @DisplayName("Java PriorityQueue should extract high-priority subjects first")
    void testPriorityQueueOrdering() {
        LocalDate today = LocalDate.now();

        Subject s1 = new Subject(1, "Math", 5, 10, 2, "A"); // High diff, low prep
        Subject s2 = new Subject(1, "History", 1, 10, 9, "A"); // Low diff, high prep
        Subject s3 = new Subject(1, "Physics", 4, 10, 5, "A"); // Med diff, med prep

        PrioritizedSubject ps1 = calculator.calculate(s1, List.of(new Exam(1, "Math Final", today.plusDays(2), 40.0, "")), today);
        PrioritizedSubject ps2 = calculator.calculate(s2, Collections.emptyList(), today);
        PrioritizedSubject ps3 = calculator.calculate(s3, List.of(new Exam(3, "Physics Quiz", today.plusDays(12), 20.0, "")), today);

        PriorityQueue<PrioritizedSubject> queue = new PriorityQueue<>();
        queue.offer(ps2);
        queue.offer(ps1);
        queue.offer(ps3);

        PrioritizedSubject first = queue.poll();
        PrioritizedSubject second = queue.poll();
        PrioritizedSubject third = queue.poll();

        assertEquals("Math", first.getSubjectName(), "Highest priority subject should be polled first");
        assertEquals("Physics", second.getSubjectName(), "Medium priority subject should be polled second");
        assertEquals("History", third.getSubjectName(), "Lowest priority subject should be polled last");
    }

    @Test
    @DisplayName("ScheduleGenerator should allocate hours within target bounds")
    void testScheduleGeneratorTimeAllocation() {
        LocalDate today = LocalDate.now();
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1, "Operating Systems", 4, 10, 2, "A"));
        subjects.add(new Subject(2, "Computer Networks", 3, 10, 5, "B"));

        Map<Integer, List<Exam>> examsMap = new HashMap<>();
        examsMap.put(1, List.of(new Exam(1, "OS Midterm", today.plusDays(4), 30.0, "")));

        double availableHours = 4.0;
        SchedulePlan plan = generator.generateSchedule(1, subjects, examsMap, availableHours, today);

        assertNotNull(plan);
        assertFalse(plan.getGeneratedTasks().isEmpty(), "Should generate study tasks");
        assertTrue(plan.getTotalAllocatedHours() > 0, "Allocated hours should be greater than 0");
        assertTrue(plan.getTotalAllocatedHours() <= availableHours + 1.0, "Allocated hours should be reasonably close to target");

        for (StudyTask task : plan.getGeneratedTasks()) {
            assertNotNull(task.getTitle());
            assertNotNull(task.getReasonExplanation());
            assertTrue(task.getEstimatedHours() > 0);
        }
    }
}
