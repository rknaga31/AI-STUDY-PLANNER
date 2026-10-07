package com.studyplanner.planning;

import com.studyplanner.model.Exam;
import com.studyplanner.model.PrioritizedSubject;
import com.studyplanner.model.Subject;

import java.time.LocalDate;
import java.util.List;

/**
 * Rule-Based Intelligent Priority Calculation Engine.
 *
 * Evaluates academic subjects across 4 weighted dimensions:
 * 1. Difficulty Level (1 to 5)
 * 2. Exam Urgency (Days remaining until nearest exam)
 * 3. Preparation Percentage (Inverse: lower prep = higher priority)
 * 4. Incomplete Topics Count
 *
 * Generates transparent, human-readable explanations of prioritization decisions.
 */
public class PriorityCalculator {

    // Weight coefficients (Sum = 1.0)
    public static final double WEIGHT_DIFFICULTY = 0.25;
    public static final double WEIGHT_EXAM_URGENCY = 0.35;
    public static final double WEIGHT_PREPARATION_NEED = 0.25;
    public static final double WEIGHT_INCOMPLETE_TOPICS = 0.15;

    /**
     * Calculates the priority score and builds a PrioritizedSubject instance with explanation.
     *
     * @param subject       The academic subject
     * @param upcomingExams List of exams associated with this subject
     * @param referenceDate Reference date (usually LocalDate.now())
     * @return PrioritizedSubject populated with metrics and explanation
     */
    public PrioritizedSubject calculate(Subject subject, List<Exam> upcomingExams, LocalDate referenceDate) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }
        if (referenceDate == null) {
            referenceDate = LocalDate.now();
        }

        PrioritizedSubject ps = new PrioritizedSubject(subject);

        // 1. Difficulty Score (0 - 100)
        double difficultyScore = (subject.getDifficulty() / 5.0) * 100.0;

        // 2. Exam Urgency Score (0 - 100) & Nearest Exam determination
        Exam nearestExam = findNearestExam(upcomingExams, referenceDate);
        double examUrgencyScore;
        long daysUntilExam = Long.MAX_VALUE;
        String examName = null;

        if (nearestExam != null) {
            daysUntilExam = nearestExam.getDaysRemaining(referenceDate);
            examName = nearestExam.getExamName();
            ps.setDaysUntilExam(daysUntilExam);
            ps.setNextExamName(examName + " (" + nearestExam.getExamDate() + ")");

            if (daysUntilExam <= 0) {
                examUrgencyScore = 100.0; // Exam today or overdue
            } else if (daysUntilExam <= 2) {
                examUrgencyScore = 95.0;
            } else if (daysUntilExam <= 5) {
                examUrgencyScore = 85.0;
            } else if (daysUntilExam <= 10) {
                examUrgencyScore = 70.0;
            } else if (daysUntilExam <= 20) {
                examUrgencyScore = 45.0;
            } else if (daysUntilExam <= 35) {
                examUrgencyScore = 25.0;
            } else {
                examUrgencyScore = 15.0;
            }
        } else {
            // Baseline urgency when no exam is set
            examUrgencyScore = 10.0;
            ps.setDaysUntilExam(Long.MAX_VALUE);
            ps.setNextExamName("No upcoming exam scheduled");
        }
        ps.setExamUrgencyScore(examUrgencyScore);

        // 3. Preparation Need Score (0 - 100)
        double prepPercent = subject.getPreparationPercentage();
        double prepNeedScore = Math.max(0.0, 100.0 - prepPercent);
        ps.setPreparationPercentage(prepPercent);

        // 4. Incomplete Topics Ratio Score (0 - 100)
        int incomplete = subject.getIncompleteTopics();
        int total = Math.max(1, subject.getTotalTopics());
        double incompleteRatioScore = ((double) incomplete / total) * 100.0;
        ps.setIncompleteTopics(incomplete);

        // Composite Priority Score
        double compositeScore = (difficultyScore * WEIGHT_DIFFICULTY)
                + (examUrgencyScore * WEIGHT_EXAM_URGENCY)
                + (prepNeedScore * WEIGHT_PREPARATION_NEED)
                + (incompleteRatioScore * WEIGHT_INCOMPLETE_TOPICS);

        // Bound between 0.0 and 100.0
        double finalScore = Math.max(0.0, Math.min(100.0, compositeScore));
        ps.setPriorityScore(Math.round(finalScore * 10.0) / 10.0);

        // Generate Human-Readable Reasoning
        String explanation = generateExplanation(subject, prepPercent, incomplete, nearestExam, daysUntilExam, examName, finalScore);
        ps.setReasonExplanation(explanation);

        return ps;
    }

    private Exam findNearestExam(List<Exam> exams, LocalDate referenceDate) {
        if (exams == null || exams.isEmpty()) return null;
        Exam nearest = null;
        long minDays = Long.MAX_VALUE;

        for (Exam e : exams) {
            long days = e.getDaysRemaining(referenceDate);
            if (days >= 0 && days < minDays) {
                minDays = days;
                nearest = e;
            }
        }
        return nearest;
    }

    private String generateExplanation(Subject subject, double prepPercent, int incompleteTopics,
                                       Exam nearestExam, long daysUntilExam, String examName, double finalScore) {
        StringBuilder sb = new StringBuilder();
        sb.append(subject.getName()).append(" is ");

        if (finalScore >= 70.0) {
            sb.append("HIGHLY prioritized");
        } else if (finalScore >= 45.0) {
            sb.append("moderately prioritized");
        } else {
            sb.append("assigned normal priority");
        }
        sb.append(" because ");

        // Difficulty clause
        int diff = subject.getDifficulty();
        if (diff >= 4) {
            sb.append("its difficulty is high (").append(diff).append("/5)");
        } else if (diff == 3) {
            sb.append("its difficulty is moderate (3/5)");
        } else {
            sb.append("its difficulty is light (").append(diff).append("/5)");
        }

        // Preparation clause
        sb.append(", preparation is ");
        if (prepPercent < 35.0) {
            sb.append("low (").append(String.format("%.1f%%", prepPercent))
              .append(" with ").append(incompleteTopics).append(" topics incomplete)");
        } else if (prepPercent < 75.0) {
            sb.append("in progress (").append(String.format("%.1f%%", prepPercent))
              .append(" with ").append(incompleteTopics).append(" topics remaining)");
        } else {
            sb.append("strong (").append(String.format("%.1f%%", prepPercent)).append(")");
        }

        // Exam clause
        if (nearestExam != null) {
            sb.append(", and ");
            if (daysUntilExam <= 0) {
                sb.append("the exam '").append(examName).append("' is scheduled for TODAY!");
            } else if (daysUntilExam == 1) {
                sb.append("the exam '").append(examName).append("' is TOMORROW!");
            } else if (daysUntilExam <= 7) {
                sb.append("the exam '").append(examName).append("' is approaching in ")
                  .append(daysUntilExam).append(" days.");
            } else {
                sb.append("the exam '").append(examName).append("' is in ")
                  .append(daysUntilExam).append(" days.");
            }
        } else {
            sb.append(", with no urgent exam scheduled.");
        }

        return sb.toString();
    }
}
