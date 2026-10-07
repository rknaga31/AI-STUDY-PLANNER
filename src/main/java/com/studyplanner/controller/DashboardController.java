package com.studyplanner.controller;

import com.studyplanner.model.PrioritizedSubject;
import com.studyplanner.model.Student;
import com.studyplanner.model.StudyTask;
import com.studyplanner.model.Subject;
import com.studyplanner.model.TaskStatus;
import com.studyplanner.service.ExamService;
import com.studyplanner.service.PlanService;
import com.studyplanner.service.ProgressService;
import com.studyplanner.service.SubjectService;
import com.studyplanner.ui.UIHelper;
import com.studyplanner.util.SessionContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Dashboard overview screen.
 */
public class DashboardController {

    private final SubjectService subjectService = new SubjectService();
    private final ExamService examService = new ExamService();
    private final PlanService planService = new PlanService();
    private final ProgressService progressService = new ProgressService();

    private final Runnable navigateToSubjects;
    private final Runnable navigateToPlanner;

    private VBox mainContainer;

    public DashboardController(Runnable navigateToSubjects, Runnable navigateToPlanner) {
        this.navigateToSubjects = navigateToSubjects;
        this.navigateToPlanner = navigateToPlanner;
    }

    public Parent getView() {
        mainContainer = new VBox(24);
        mainContainer.setPadding(new Insets(24));

        refreshView();

        ScrollPane scroll = new ScrollPane(mainContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return scroll;
    }

    public void refreshView() {
        mainContainer.getChildren().clear();

        Student current = SessionContext.getInstance().getCurrentStudent();
        if (current == null) return;

        try {
            int studentId = current.getId();
            List<Subject> subjects = subjectService.getSubjectsForStudent(studentId);
            var upcomingExams = examService.getUpcomingExams(studentId);
            List<StudyTask> todayTasks = planService.getTasksForDate(studentId, LocalDate.now());
            double overallPrep = progressService.getOverallPreparationPercentage(studentId);

            double pendingHours = 0.0;
            for (StudyTask t : todayTasks) {
                if (t.getStatus() != TaskStatus.COMPLETED) {
                    pendingHours += t.getEstimatedHours();
                }
            }

            // Top Greeting Bar
            VBox greetingBox = new VBox(4);
            Label lblHello = new Label("Welcome, " + current.getFullName() + "!");
            lblHello.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
            Label lblSub = new Label("Here is your intelligent academic preparation summary for today.");
            lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
            greetingBox.getChildren().addAll(lblHello, lblSub);

            // Metrics Cards Row
            HBox metricsRow = new HBox(16);
            VBox c1 = UIHelper.createMetricCard("Total Subjects", String.valueOf(subjects.size()), "Registered courses");
            VBox c2 = UIHelper.createMetricCard("Upcoming Exams", String.valueOf(upcomingExams.size()), "Scheduled deadlines");
            VBox c3 = UIHelper.createMetricCard("Today's Tasks", String.format("%.1fh", pendingHours), todayTasks.size() + " scheduled tasks");
            VBox c4 = UIHelper.createMetricCard("Overall Preparation", String.format("%.1f%%", overallPrep), "Topic completion rate");
            metricsRow.getChildren().addAll(c1, c2, c3, c4);

            // Priority Spotlight & Action Section
            HBox contentSplit = new HBox(20);

            // Left side: Priority Queue Spotlight
            VBox priorityCard = new VBox(14);
            priorityCard.getStyleClass().add("card");
            HBox.setHgrow(priorityCard, Priority.ALWAYS);

            HBox pHeader = new HBox(10);
            pHeader.setAlignment(Pos.CENTER_LEFT);
            Label pTitle = new Label("Intelligent Priority Ranking (PriorityQueue)");
            pTitle.getStyleClass().add("card-title");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button btnGoPlanner = new Button("Open Planner");
            btnGoPlanner.getStyleClass().add("btn-secondary");
            btnGoPlanner.setOnAction(e -> {
                if (navigateToPlanner != null) navigateToPlanner.run();
            });
            pHeader.getChildren().addAll(pTitle, spacer, btnGoPlanner);

            VBox priorityList = new VBox(10);
            List<PrioritizedSubject> ranked = planService.calculatePriorityOverview(studentId);

            if (ranked.isEmpty()) {
                Label emptyLabel = new Label("No subjects available. Add subjects to see intelligent priority ranking.");
                emptyLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
                priorityList.getChildren().add(emptyLabel);
            } else {
                for (int i = 0; i < Math.min(3, ranked.size()); i++) {
                    PrioritizedSubject ps = ranked.get(i);
                    VBox itemBox = new VBox(6);
                    itemBox.setStyle("-fx-background-color: #f8fafc; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

                    HBox itemTop = new HBox(10);
                    itemTop.setAlignment(Pos.CENTER_LEFT);
                    Label rankNum = new Label("#" + (i + 1));
                    rankNum.setStyle("-fx-font-weight: bold; -fx-text-fill: #4f46e5; -fx-font-size: 14px;");

                    Label sName = new Label(ps.getSubjectName());
                    sName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0f172a;");

                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);

                    Label pBadge = UIHelper.createPriorityBadge(ps.getPriorityScore());
                    Label dBadge = UIHelper.createDifficultyBadge(ps.getDifficulty());

                    itemTop.getChildren().addAll(rankNum, sName, sp, dBadge, pBadge);

                    Label reasonLbl = new Label(ps.getReasonExplanation());
                    reasonLbl.getStyleClass().add("reason-text");
                    reasonLbl.setWrapText(true);

                    itemBox.getChildren().addAll(itemTop, reasonLbl);
                    priorityList.getChildren().add(itemBox);
                }
            }
            priorityCard.getChildren().addAll(pHeader, priorityList);

            // Right side: Today's Tasks
            VBox tasksCard = new VBox(14);
            tasksCard.getStyleClass().add("card");
            tasksCard.setMinWidth(360);
            tasksCard.setPrefWidth(380);

            Label tTitle = new Label("Today's Tasks (" + todayTasks.size() + ")");
            tTitle.getStyleClass().add("card-title");

            VBox tasksList = new VBox(8);
            if (todayTasks.isEmpty()) {
                VBox emptyBox = new VBox(10);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(20));
                Label emptyLbl = new Label("No study tasks scheduled for today.");
                emptyLbl.setStyle("-fx-text-fill: #94a3b8;");
                Button btnGen = new Button("Generate Today's Plan");
                btnGen.getStyleClass().add("btn-primary");
                btnGen.setOnAction(e -> {
                    try {
                        planService.generateAndSaveDailyPlan(studentId, current.getTargetDailyHours(), LocalDate.now());
                        refreshView();
                    } catch (Exception ex) {
                        UIHelper.showErrorAlert("Error", ex.getMessage());
                    }
                });
                emptyBox.getChildren().addAll(emptyLbl, btnGen);
                tasksList.getChildren().add(emptyBox);
            } else {
                for (StudyTask task : todayTasks) {
                    HBox taskRow = new HBox(10);
                    taskRow.setAlignment(Pos.CENTER_LEFT);
                    taskRow.setStyle("-fx-background-color: #f8fafc; -fx-padding: 8 12 8 12; -fx-background-radius: 6;");

                    CheckBox cb = new CheckBox();
                    cb.setSelected(task.getStatus() == TaskStatus.COMPLETED);
                    cb.setOnAction(e -> {
                        try {
                            if (cb.isSelected()) {
                                progressService.completeTask(task.getId());
                            } else {
                                new com.studyplanner.dao.StudyTaskDAO().updateStatus(task.getId(), TaskStatus.PENDING);
                            }
                            refreshView();
                        } catch (Exception ex) {
                            UIHelper.showErrorAlert("Error", ex.getMessage());
                        }
                    });

                    VBox tInfo = new VBox(2);
                    Label tName = new Label(task.getTitle());
                    tName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                    if (task.getStatus() == TaskStatus.COMPLETED) {
                        tName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #94a3b8; -fx-strikethrough: true;");
                    }

                    Label tHours = new Label(task.getEstimatedHours() + "h • Priority " + String.format("%.0f", task.getPriorityScore()));
                    tHours.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
                    tInfo.getChildren().addAll(tName, tHours);

                    taskRow.getChildren().addAll(cb, tInfo);
                    tasksList.getChildren().add(taskRow);
                }
            }
            tasksCard.getChildren().addAll(tTitle, tasksList);

            contentSplit.getChildren().addAll(priorityCard, tasksCard);

            mainContainer.getChildren().addAll(greetingBox, metricsRow, contentSplit);

        } catch (Exception ex) {
            Label err = new Label("Failed to load dashboard: " + ex.getMessage());
            err.setStyle("-fx-text-fill: #ef4444;");
            mainContainer.getChildren().add(err);
        }
    }
}
