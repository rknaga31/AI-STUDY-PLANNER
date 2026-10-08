package com.studyplanner.controller;

import com.studyplanner.model.PrioritizedSubject;
import com.studyplanner.model.Student;
import com.studyplanner.model.StudyTask;
import com.studyplanner.model.TaskStatus;
import com.studyplanner.planning.SchedulePlan;
import com.studyplanner.service.PlanService;
import com.studyplanner.service.ProgressService;
import com.studyplanner.ui.UIHelper;
import com.studyplanner.util.SessionContext;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for Intelligent Study Plan Generation, PriorityQueue analysis,
 * and rule-based explanations.
 */
public class PlannerController {

    private final PlanService planService = new PlanService();
    private final ProgressService progressService = new ProgressService();

    private TableView<StudyTask> tableTasks;
    private VBox priorityRankingContainer;
    private VBox mainContainer;
    private Spinner<Double> spHours;
    private Label lblPlanSummary;

    public Parent getView() {
        mainContainer = new VBox(20);
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

        // Header and Controls
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label pageTitle = new Label("AI-Based Study Planner");
        pageTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label pageSub = new Label("Rule-based intelligent scheduling with Java PriorityQueue and explainable rationale.");
        pageSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleBox.getChildren().addAll(pageTitle, pageSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Hours selector
        Label lblHoursPrompt = new Label("Available Study Hours Today:");
        lblHoursPrompt.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");

        spHours = new Spinner<>(0.5, 12.0, current.getTargetDailyHours(), 0.5);
        spHours.setEditable(true);
        spHours.setPrefWidth(85);

        Button btnGenerate = new Button("Generate Study Plan");
        btnGenerate.getStyleClass().add("btn-primary");
        btnGenerate.setOnAction(e -> handleGeneratePlan(current.getId()));

        Button btnRegen = new Button("↻ Regenerate Plan");
        btnRegen.getStyleClass().add("btn-secondary");
        btnRegen.setOnAction(e -> handleRegeneratePlan(current.getId()));

        topBar.getChildren().addAll(titleBox, spacer, lblHoursPrompt, spHours, btnGenerate, btnRegen);

        // Plan Summary Banner
        lblPlanSummary = new Label();
        lblPlanSummary.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #4338ca; -fx-background-color: #e0e7ff; -fx-padding: 8 16; -fx-background-radius: 6;");
        lblPlanSummary.setMaxWidth(Double.MAX_VALUE);
        lblPlanSummary.setVisible(false);
        lblPlanSummary.setManaged(false);

        // Generated Daily Schedule Tasks Table Card
        VBox scheduleCard = new VBox(12);
        scheduleCard.getStyleClass().add("card");

        HBox sHeader = new HBox(10);
        sHeader.setAlignment(Pos.CENTER_LEFT);
        Label sTitle = new Label("Today's Generated Schedule");
        sTitle.getStyleClass().add("card-title");
        sHeader.getChildren().add(sTitle);

        tableTasks = buildTasksTable();
        loadTaskData(current.getId());

        scheduleCard.getChildren().addAll(sHeader, tableTasks);

        // Why Prioritized / PriorityQueue Section Card
        VBox whyCard = new VBox(14);
        whyCard.getStyleClass().add("card");

        Label whyTitle = new Label("Why Are These Subjects Prioritized? (Rule Reasoning Engine)");
        whyTitle.getStyleClass().add("card-title");
        Label whySub = new Label("The planning engine processes subjects through a Java PriorityQueue based on Difficulty, Exam Urgency, Preparation %, and Incomplete Topics.");
        whySub.getStyleClass().add("card-subtitle");

        priorityRankingContainer = new VBox(10);
        loadPriorityExplanations(current.getId());

        whyCard.getChildren().addAll(whyTitle, whySub, priorityRankingContainer);

        mainContainer.getChildren().addAll(topBar, lblPlanSummary, scheduleCard, whyCard);
    }

    private TableView<StudyTask> buildTasksTable() {
        TableView<StudyTask> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(230);

        TableColumn<StudyTask, String> colSub = new TableColumn<>("Subject");
        colSub.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubjectName() != null ? data.getValue().getSubjectName() : "ID #" + data.getValue().getSubjectId()));
        colSub.setPrefWidth(120);

        TableColumn<StudyTask, String> colTask = new TableColumn<>("Task Description");
        colTask.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        colTask.setPrefWidth(240);

        TableColumn<StudyTask, String> colHours = new TableColumn<>("Allocated Time");
        colHours.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstimatedHours() + " hours"));
        colHours.setPrefWidth(100);

        TableColumn<StudyTask, Label> colPri = new TableColumn<>("Priority Score");
        colPri.setCellValueFactory(data -> new SimpleObjectProperty<>(UIHelper.createPriorityBadge(data.getValue().getPriorityScore())));
        colPri.setPrefWidth(110);

        TableColumn<StudyTask, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().getDisplayName()));
        colStatus.setPrefWidth(90);

        TableColumn<StudyTask, Button> colAction = new TableColumn<>("Action");
        colAction.setCellValueFactory(data -> {
            StudyTask task = data.getValue();
            Button btn = new Button();
            if (task.getStatus() == TaskStatus.COMPLETED) {
                btn.setText("Completed ✓");
                btn.setStyle("-fx-background-color: #ecfdf5; -fx-text-fill: #059669; -fx-font-size: 11px;");
                btn.setDisable(true);
            } else {
                btn.setText("Mark Done");
                btn.getStyleClass().add("btn-success");
                btn.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");
                btn.setOnAction(e -> {
                    try {
                        progressService.completeTask(task.getId());
                        refreshView();
                    } catch (Exception ex) {
                        UIHelper.showErrorAlert("Error", ex.getMessage());
                    }
                });
            }
            return new SimpleObjectProperty<>(btn);
        });
        colAction.setPrefWidth(110);

        table.getColumns().addAll(colSub, colTask, colHours, colPri, colStatus, colAction);
        return table;
    }

    private void loadTaskData(int studentId) {
        try {
            List<StudyTask> tasks = planService.getTasksForDate(studentId, LocalDate.now());
            tableTasks.setItems(FXCollections.observableArrayList(tasks));
        } catch (Exception ex) {
            UIHelper.showErrorAlert("Error", "Could not load tasks: " + ex.getMessage());
        }
    }

    private void loadPriorityExplanations(int studentId) {
        priorityRankingContainer.getChildren().clear();
        try {
            List<PrioritizedSubject> ranked = planService.calculatePriorityOverview(studentId);
            if (ranked.isEmpty()) {
                Label empty = new Label("No subjects to prioritize. Add subjects in the Subject Management tab.");
                empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
                priorityRankingContainer.getChildren().add(empty);
                return;
            }

            for (int i = 0; i < ranked.size(); i++) {
                PrioritizedSubject ps = ranked.get(i);
                VBox box = new VBox(6);
                box.setStyle("-fx-background-color: #f8fafc; -fx-padding: 12 16; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-width: 1;");

                HBox top = new HBox(10);
                top.setAlignment(Pos.CENTER_LEFT);

                Label rankBadge = new Label("Rank #" + (i + 1));
                rankBadge.setStyle("-fx-font-weight: bold; -fx-text-fill: #4338ca; -fx-background-color: #e0e7ff; -fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 11px;");

                Label name = new Label(ps.getSubjectName());
                name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0f172a;");

                Region r = new Region();
                HBox.setHgrow(r, Priority.ALWAYS);

                Label badgePri = UIHelper.createPriorityBadge(ps.getPriorityScore());
                Label badgeDiff = UIHelper.createDifficultyBadge(ps.getDifficulty());

                top.getChildren().addAll(rankBadge, name, r, badgeDiff, badgePri);

                // Detailed reasoning box
                VBox reasonBox = UIHelper.createReasonBox(ps.getReasonExplanation());

                box.getChildren().addAll(top, reasonBox);
                priorityRankingContainer.getChildren().add(box);
            }
        } catch (Exception ex) {
            Label err = new Label("Failed to calculate priorities: " + ex.getMessage());
            err.setStyle("-fx-text-fill: #ef4444;");
            priorityRankingContainer.getChildren().add(err);
        }
    }

    private void handleGeneratePlan(int studentId) {
        double hours = spHours.getValue();
        try {
            SchedulePlan plan = planService.generateAndSaveDailyPlan(studentId, hours, LocalDate.now());
            lblPlanSummary.setText(plan.getSummaryMessage());
            lblPlanSummary.setVisible(true);
            lblPlanSummary.setManaged(true);
            loadTaskData(studentId);
            loadPriorityExplanations(studentId);
        } catch (Exception ex) {
            UIHelper.showErrorAlert("Planning Error", ex.getMessage());
        }
    }

    private void handleRegeneratePlan(int studentId) {
        double hours = spHours.getValue();
        try {
            SchedulePlan plan = planService.regeneratePlanForStudent(studentId, hours, LocalDate.now());
            lblPlanSummary.setText("Plan Regenerated: " + plan.getSummaryMessage());
            lblPlanSummary.setVisible(true);
            lblPlanSummary.setManaged(true);
            loadTaskData(studentId);
            loadPriorityExplanations(studentId);
        } catch (Exception ex) {
            UIHelper.showErrorAlert("Regeneration Error", ex.getMessage());
        }
    }
}
