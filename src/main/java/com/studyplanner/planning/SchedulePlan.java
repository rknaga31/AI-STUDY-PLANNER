package com.studyplanner.planning;

import com.studyplanner.model.PrioritizedSubject;
import com.studyplanner.model.StudyTask;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the generated study schedule for a date,
 * including assigned tasks, priority ranking, and allocation summary.
 */
public class SchedulePlan {
    private LocalDate planDate;
    private double targetHours;
    private double totalAllocatedHours;
    private List<PrioritizedSubject> prioritizedQueueOrder;
    private List<StudyTask> generatedTasks;
    private String summaryMessage;

    public SchedulePlan(LocalDate planDate, double targetHours) {
        this.planDate = planDate;
        this.targetHours = targetHours;
        this.prioritizedQueueOrder = new ArrayList<>();
        this.generatedTasks = new ArrayList<>();
    }

    public LocalDate getPlanDate() {
        return planDate;
    }

    public double getTargetHours() {
        return targetHours;
    }

    public double getTotalAllocatedHours() {
        return totalAllocatedHours;
    }

    public void setTotalAllocatedHours(double totalAllocatedHours) {
        this.totalAllocatedHours = totalAllocatedHours;
    }

    public List<PrioritizedSubject> getPrioritizedQueueOrder() {
        return prioritizedQueueOrder;
    }

    public void setPrioritizedQueueOrder(List<PrioritizedSubject> prioritizedQueueOrder) {
        this.prioritizedQueueOrder = prioritizedQueueOrder;
    }

    public List<StudyTask> getGeneratedTasks() {
        return generatedTasks;
    }

    public void setGeneratedTasks(List<StudyTask> generatedTasks) {
        this.generatedTasks = generatedTasks;
    }

    public String getSummaryMessage() {
        return summaryMessage;
    }

    public void setSummaryMessage(String summaryMessage) {
        this.summaryMessage = summaryMessage;
    }
}
