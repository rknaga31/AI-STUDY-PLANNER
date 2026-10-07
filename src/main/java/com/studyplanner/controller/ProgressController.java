package com.studyplanner.controller;

import com.studyplanner.model.Progress;
import com.studyplanner.model.Student;
import com.studyplanner.model.StudySession;
import com.studyplanner.model.Subject;
import com.studyplanner.service.PlanService;
import com.studyplanner.service.ProgressService;
import com.studyplanner.service.SubjectService;
import com.studyplanner.ui.UIHelper;
import com.studyplanner.util.SessionContext;
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
 * Controller for Progress Tracking, Study Session Logging, and Plan Regeneration.
 */
public class ProgressController {

    private final SubjectService subjectService = new SubjectService();
    private final ProgressService progressService = new ProgressService();
    private final PlanService planService = new PlanService();

    private TableView<StudySession> tableSessions;
    private VBox subjectCardsContainer;
    private VBox mainContainer;
    private final Runnable navigateToPlanner;

    public ProgressController(Runnable navigateToPlanner) {
        this.navigateToPlanner = navigateToPlanner;
    }

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

        int studentId = current.getId();

        // Top Header & Action
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label pageTitle = new Label("Progress Tracking & Study Sessions");
        pageTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label pageSub = new Label("Record study sessions, update completed topics, and regenerate your study plan.");
        pageSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleBox.getChildren().addAll(pageTitle, pageSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnRegen = new Button("↻ Regenerate Plan With Latest Progress");
        btnRegen.getStyleClass().add("btn-primary");
        btnRegen.setOnAction(e -> {
            try {
                planService.regeneratePlanForStudent(studentId, current.getTargetDailyHours(), LocalDate.now());
                UIHelper.showInfoAlert("Plan Regenerated", "Study plan successfully regenerated based on latest progress and priorities!");
                if (navigateToPlanner != null) navigateToPlanner.run();
            } catch (Exception ex) {
                UIHelper.showErrorAlert("Error", ex.getMessage());
            }
        });

        topBar.getChildren().addAll(titleBox, spacer, btnRegen);

        // Quick Stats Row
        try {
            int totalMins = progressService.getTotalStudyMinutes(studentId);
            double prepPercent = progressService.getOverallPreparationPercentage(studentId);

            HBox statsRow = new HBox(16);
            VBox sc1 = UIHelper.createMetricCard("Total Study Time", String.format("%.1f hrs", totalMins / 60.0), totalMins + " minutes logged");
            VBox sc2 = UIHelper.createMetricCard("Overall Readiness", String.format("%.1f%%", prepPercent), "Syllabus coverage across all subjects");
            statsRow.getChildren().addAll(sc1, sc2);

            // Log Session & Update Topics Split
            HBox splitRow = new HBox(20);

            // Left Box: Log Study Session Form
            VBox logCard = new VBox(12);
            logCard.getStyleClass().add("card");
            logCard.setMinWidth(380);
            logCard.setPrefWidth(420);

            Label lblLogTitle = new Label("Log Completed Study Session");
            lblLogTitle.getStyleClass().add("card-title");

            List<Subject> subjects = subjectService.getSubjectsForStudent(studentId);
            if (subjects.isEmpty()) {
                Label noSub = new Label("Please add subjects first to log study sessions.");
                noSub.setStyle("-fx-text-fill: #94a3b8;");
                logCard.getChildren().addAll(lblLogTitle, noSub);
            } else {
                ComboBox<Subject> cbSubject = new ComboBox<>(FXCollections.observableArrayList(subjects));
                cbSubject.setValue(subjects.get(0));
                cbSubject.setMaxWidth(Double.MAX_VALUE);

                DatePicker dpDate = new DatePicker(LocalDate.now());
                dpDate.setMaxWidth(Double.MAX_VALUE);

                Spinner<Integer> spDuration = new Spinner<>(15, 480, 60, 15);
                spDuration.setEditable(true);
                spDuration.setMaxWidth(Double.MAX_VALUE);

                Spinner<Integer> spTopicsInc = new Spinner<>(0, 10, 1, 1);
                spTopicsInc.setEditable(true);
                spTopicsInc.setMaxWidth(Double.MAX_VALUE);

                TextField txtNotes = new TextField();
                txtNotes.setPromptText("e.g. Completed Chapter 3 on Dynamic Programming");

                Button btnSaveSession = new Button("Record Session & Advance Progress");
                btnSaveSession.getStyleClass().add("btn-success");
                btnSaveSession.setMaxWidth(Double.MAX_VALUE);
                btnSaveSession.setOnAction(e -> {
                    try {
                        Subject s = cbSubject.getValue();
                        progressService.logStudySession(
                                studentId,
                                s.getId(),
                                dpDate.getValue(),
                                spDuration.getValue(),
                                txtNotes.getText(),
                                spTopicsInc.getValue()
                        );
                        UIHelper.showInfoAlert("Success", "Study session recorded and progress updated!");
                        refreshView();
                    } catch (Exception ex) {
                        UIHelper.showErrorAlert("Error", ex.getMessage());
                    }
                });

                GridPane formGrid = new GridPane();
                formGrid.setHgap(10);
                formGrid.setVgap(10);

                formGrid.add(new Label("Subject:"), 0, 0);
                formGrid.add(cbSubject, 1, 0);
                formGrid.add(new Label("Session Date:"), 0, 1);
                formGrid.add(dpDate, 1, 1);
                formGrid.add(new Label("Duration (Minutes):"), 0, 2);
                formGrid.add(spDuration, 1, 2);
                formGrid.add(new Label("Topics Finished:"), 0, 3);
                formGrid.add(spTopicsInc, 1, 3);
                formGrid.add(new Label("Session Notes:"), 0, 4);
                formGrid.add(txtNotes, 1, 4);

                logCard.getChildren().addAll(lblLogTitle, formGrid, btnSaveSession);
            }

            // Right Box: Subject Progress Adjusters
            VBox trackerCard = new VBox(12);
            trackerCard.getStyleClass().add("card");
            HBox.setHgrow(trackerCard, Priority.ALWAYS);

            Label lblTrackTitle = new Label("Direct Subject Progress Tracker");
            lblTrackTitle.getStyleClass().add("card-title");

            subjectCardsContainer = new VBox(8);
            loadSubjectProgressItems(studentId, subjects);

            trackerCard.getChildren().addAll(lblTrackTitle, subjectCardsContainer);

            splitRow.getChildren().addAll(logCard, trackerCard);

            // Bottom Box: Study Sessions History Table
            VBox historyCard = new VBox(12);
            historyCard.getStyleClass().add("card");

            Label lblHistTitle = new Label("Logged Study Sessions History");
            lblHistTitle.getStyleClass().add("card-title");

            tableSessions = buildSessionsTable();
            loadSessionsData(studentId);

            historyCard.getChildren().addAll(lblHistTitle, tableSessions);

            mainContainer.getChildren().addAll(topBar, statsRow, splitRow, historyCard);

        } catch (Exception ex) {
            Label err = new Label("Failed to load progress view: " + ex.getMessage());
            err.setStyle("-fx-text-fill: #ef4444;");
            mainContainer.getChildren().add(err);
        }
    }

    private void loadSubjectProgressItems(int studentId, List<Subject> subjects) {
        subjectCardsContainer.getChildren().clear();
        if (subjects == null || subjects.isEmpty()) {
            Label empty = new Label("No subjects to track. Add subjects first.");
            empty.setStyle("-fx-text-fill: #94a3b8;");
            subjectCardsContainer.getChildren().add(empty);
            return;
        }

        for (Subject s : subjects) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #f8fafc; -fx-padding: 8 12; -fx-background-radius: 6; -fx-border-color: #e2e8f0;");

            VBox info = new VBox(2);
            info.setMinWidth(140);
            Label name = new Label(s.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            Label diff = new Label(s.getDifficultyLabel());
            diff.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            info.getChildren().addAll(name, diff);

            ProgressBar bar = new ProgressBar(s.getPreparationPercentage() / 100.0);
            bar.setPrefWidth(120);

            Label pct = new Label(String.format("%.0f%%", s.getPreparationPercentage()));
            pct.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #4338ca;");
            pct.setMinWidth(45);

            Spinner<Integer> spDone = new Spinner<>(0, s.getTotalTopics(), s.getCompletedTopics(), 1);
            spDone.setPrefWidth(70);

            Label lblTotal = new Label("/ " + s.getTotalTopics());
            lblTotal.setStyle("-fx-text-fill: #64748b;");

            Button btnUpdate = new Button("Save");
            btnUpdate.getStyleClass().add("btn-secondary");
            btnUpdate.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");
            btnUpdate.setOnAction(e -> {
                try {
                    progressService.updateCompletedTopics(studentId, s.getId(), spDone.getValue());
                    UIHelper.showInfoAlert("Updated", "Updated topics for " + s.getName());
                    refreshView();
                } catch (Exception ex) {
                    UIHelper.showErrorAlert("Error", ex.getMessage());
                }
            });

            row.getChildren().addAll(info, bar, pct, spDone, lblTotal, btnUpdate);
            subjectCardsContainer.getChildren().add(row);
        }
    }

    private TableView<StudySession> buildSessionsTable() {
        TableView<StudySession> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(180);

        TableColumn<StudySession, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSessionDate().toString()));

        TableColumn<StudySession, String> colSub = new TableColumn<>("Subject");
        colSub.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubjectName() != null ? data.getValue().getSubjectName() : "ID #" + data.getValue().getSubjectId()));

        TableColumn<StudySession, String> colDur = new TableColumn<>("Duration");
        colDur.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDurationMinutes() + " mins (" + String.format("%.1fh", data.getValue().getDurationHours()) + ")"));

        TableColumn<StudySession, String> colNotes = new TableColumn<>("Notes / Topics Covered");
        colNotes.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNotes() != null ? data.getValue().getNotes() : ""));

        table.getColumns().addAll(colDate, colSub, colDur, colNotes);
        return table;
    }

    private void loadSessionsData(int studentId) {
        try {
            List<StudySession> list = progressService.getStudySessions(studentId);
            tableSessions.setItems(FXCollections.observableArrayList(list));
        } catch (Exception ex) {
            UIHelper.showErrorAlert("Error", "Could not load sessions: " + ex.getMessage());
        }
    }
}
