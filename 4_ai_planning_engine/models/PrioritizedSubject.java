package com.studyplanner.model;

/**
 * Encapsulates a subject with its calculated priority metrics,
 * scheduling recommendations, and rule-based explanations.
 */
public class PrioritizedSubject implements Comparable<PrioritizedSubject> {
    private Subject subject;
    private double priorityScore;
    private double examUrgencyScore;
    private long daysUntilExam;
    private String nextExamName;
    private double preparationPercentage;
    private int incompleteTopics;
    private double recommendedDailyHours;
    private String reasonExplanation;

    public PrioritizedSubject(Subject subject) {
        this.subject = subject;
        this.daysUntilExam = Long.MAX_VALUE;
        this.nextExamName = "No exam scheduled";
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public double getExamUrgencyScore() {
        return examUrgencyScore;
    }

    public void setExamUrgencyScore(double examUrgencyScore) {
        this.examUrgencyScore = examUrgencyScore;
    }

    public long getDaysUntilExam() {
        return daysUntilExam;
    }

    public void setDaysUntilExam(long daysUntilExam) {
        this.daysUntilExam = daysUntilExam;
    }

    public String getNextExamName() {
        return nextExamName;
    }

    public void setNextExamName(String nextExamName) {
        this.nextExamName = nextExamName;
    }

    public double getPreparationPercentage() {
        return preparationPercentage;
    }

    public void setPreparationPercentage(double preparationPercentage) {
        this.preparationPercentage = preparationPercentage;
    }

    public int getIncompleteTopics() {
        return incompleteTopics;
    }

    public void setIncompleteTopics(int incompleteTopics) {
        this.incompleteTopics = incompleteTopics;
    }

    public double getRecommendedDailyHours() {
        return recommendedDailyHours;
    }

    public void setRecommendedDailyHours(double recommendedDailyHours) {
        this.recommendedDailyHours = recommendedDailyHours;
    }

    public String getReasonExplanation() {
        return reasonExplanation;
    }

    public void setReasonExplanation(String reasonExplanation) {
        this.reasonExplanation = reasonExplanation;
    }

    public String getSubjectName() {
        return subject != null ? subject.getName() : "Unknown";
    }

    public int getDifficulty() {
        return subject != null ? subject.getDifficulty() : 1;
    }

    /**
     * Orders descending by priorityScore so that java.util.PriorityQueue or sorting
     * processes highest-priority subjects first.
     */
    @Override
    public int compareTo(PrioritizedSubject other) {
        if (other == null) return -1;
        return Double.compare(other.priorityScore, this.priorityScore);
    }

    @Override
    public String toString() {
        return String.format("%s [Priority: %.1f] - %s",
                getSubjectName(), priorityScore, reasonExplanation);
    }
}
