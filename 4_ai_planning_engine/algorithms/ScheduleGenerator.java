package com.studyplanner.planning;

import com.studyplanner.model.Exam;
import com.studyplanner.model.PrioritizedSubject;
import com.studyplanner.model.StudyTask;
import com.studyplanner.model.Subject;
import com.studyplanner.model.TaskStatus;

import java.time.LocalDate;
import java.util.*;

/**
 * Intelligent Daily Schedule Generator.
 *
 * Utilizes Java's PriorityQueue to extract and process high-priority subjects first,
 * then allocates the student's available daily hours proportionally to maximize
 * academic impact and exam readiness.
 */
public class ScheduleGenerator {

    private final PriorityCalculator priorityCalculator;

    public ScheduleGenerator() {
        this.priorityCalculator = new PriorityCalculator();
    }

    public ScheduleGenerator(PriorityCalculator priorityCalculator) {
        this.priorityCalculator = priorityCalculator;
    }

    /**
     * Generates a daily study schedule based on available study hours.
     *
     * @param studentId           ID of the student
     * @param subjects            List of all subjects registered by the student
     * @param subjectExamsMap     Map of subject ID to upcoming exams
     * @param availableDailyHours Available hours to study today (e.g. 4.0)
     * @param targetDate          Date for the schedule (e.g. LocalDate.now())
     * @return SchedulePlan containing prioritized queue and generated study tasks
     */
    public SchedulePlan generateSchedule(int studentId,
                                         List<Subject> subjects,
                                         Map<Integer, List<Exam>> subjectExamsMap,
                                         double availableDailyHours,
                                         LocalDate targetDate) {

        if (targetDate == null) {
            targetDate = LocalDate.now();
        }

        // Clamp available daily hours to realistic range [0.5, 16.0]
        double hoursToAllocate = Math.max(0.5, Math.min(16.0, availableDailyHours));
        SchedulePlan plan = new SchedulePlan(targetDate, hoursToAllocate);

        if (subjects == null || subjects.isEmpty()) {
            plan.setSummaryMessage("No subjects registered. Please add subjects to generate a study schedule.");
            return plan;
        }

        // 1. Calculate Priority Metrics for each subject
        List<PrioritizedSubject> evaluatedList = new ArrayList<>();
        for (Subject subject : subjects) {
            List<Exam> exams = subjectExamsMap != null ? subjectExamsMap.get(subject.getId()) : Collections.emptyList();
            PrioritizedSubject ps = priorityCalculator.calculate(subject, exams, targetDate);
            evaluatedList.add(ps);
        }

        // 2. Insert into Java PriorityQueue to process high-priority subjects first
        // PrioritizedSubject implements Comparable<PrioritizedSubject> (descending by priorityScore)
        PriorityQueue<PrioritizedSubject> priorityQueue = new PriorityQueue<>(evaluatedList);

        // 3. Extract in strict priority order
        List<PrioritizedSubject> orderedList = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            orderedList.add(priorityQueue.poll());
        }
        plan.setPrioritizedQueueOrder(orderedList);

        // 4. Determine subjects that need active study vs fully prepared
        List<PrioritizedSubject> activeSubjects = new ArrayList<>();
        for (PrioritizedSubject ps : orderedList) {
            // Give preference to subjects that still have incomplete topics or upcoming exams
            if (ps.getIncompleteTopics() > 0 || ps.getDaysUntilExam() <= 7) {
                activeSubjects.add(ps);
            }
        }

        // If all subjects are 100% completed, include all for review/revision
        if (activeSubjects.isEmpty()) {
            activeSubjects.addAll(orderedList);
        }

        // 5. Allocate available hours proportionally
        // Limit number of subjects per day (e.g. max 3-4 subjects per day to prevent context switching)
        int maxSubjectsPerDay = Math.min(activeSubjects.size(), Math.max(2, (int) Math.ceil(hoursToAllocate / 1.5)));
        List<PrioritizedSubject> selectedSubjects = activeSubjects.subList(0, maxSubjectsPerDay);

        double totalSelectedScore = 0.0;
        for (PrioritizedSubject ps : selectedSubjects) {
            totalSelectedScore += Math.max(10.0, ps.getPriorityScore());
        }

        List<StudyTask> generatedTasks = new ArrayList<>();
        double remainingHours = hoursToAllocate;

        for (int i = 0; i < selectedSubjects.size(); i++) {
            PrioritizedSubject ps = selectedSubjects.get(i);
            double allocatedTime;

            if (i == selectedSubjects.size() - 1) {
                // Assign all remaining time to the last subject (bounded)
                allocatedTime = Math.round(remainingHours * 2.0) / 2.0; // round to nearest 0.5h
                if (allocatedTime <= 0) allocatedTime = 0.5;
            } else {
                double rawTime = (Math.max(10.0, ps.getPriorityScore()) / totalSelectedScore) * hoursToAllocate;
                allocatedTime = Math.round(rawTime * 2.0) / 2.0; // round to nearest 0.5h
                // Min 0.5h, Max 3.0h
                allocatedTime = Math.max(0.5, Math.min(3.0, allocatedTime));
                remainingHours -= allocatedTime;
                if (remainingHours <= 0) remainingHours = 0.5;
            }

            ps.setRecommendedDailyHours(allocatedTime);

            // Construct task title based on topics and urgency
            String taskTitle;
            if (ps.getIncompleteTopics() > 0) {
                taskTitle = String.format("Focus Study & Topic Coverage: %s", ps.getSubjectName());
            } else {
                taskTitle = String.format("Exam Prep & Comprehensive Review: %s", ps.getSubjectName());
            }

            StudyTask task = new StudyTask(
                    studentId,
                    ps.getSubject().getId(),
                    taskTitle,
                    allocatedTime,
                    TaskStatus.PENDING,
                    ps.getPriorityScore(),
                    targetDate,
                    ps.getReasonExplanation()
            );
            task.setSubjectName(ps.getSubjectName());
            generatedTasks.add(task);
        }

        double totalAllocated = 0.0;
        for (StudyTask t : generatedTasks) {
            totalAllocated += t.getEstimatedHours();
        }

        plan.setGeneratedTasks(generatedTasks);
        plan.setTotalAllocatedHours(totalAllocated);
        plan.setSummaryMessage(String.format(
                "Successfully generated %d priority study tasks totaling %.1f hours based on your target schedule.",
                generatedTasks.size(), totalAllocated
        ));

        return plan;
    }
}
