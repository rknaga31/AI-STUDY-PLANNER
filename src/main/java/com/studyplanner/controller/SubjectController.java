package com.studyplanner.controller;

import com.studyplanner.model.Exam;
import com.studyplanner.model.Student;
import com.studyplanner.model.Subject;
import com.studyplanner.service.ExamService;
import com.studyplanner.service.SubjectService;
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
 * Controller for Subject Management and Exam Deadlines.
 */
public class SubjectController {

    private final SubjectService subjectService = new SubjectService();
    private final ExamService examService = new ExamService();

    private TableView<Subject> tableSubjects;
    private TableView<Exam> tableExams;
    private VBox mainContainer;

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

        // Title and Action Buttons Header
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label pageTitle = new Label("Subject & Exam Management");
        pageTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label pageSub = new Label("Configure course difficulty, topic readiness, and upcoming exam deadlines.");
        pageSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleBox.getChildren().addAll(pageTitle, pageSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAddSubject = new Button("+ Add New Subject");
        btnAddSubject.getStyleClass().add("btn-primary");
        btnAddSubject.setOnAction(e -> showAddSubjectDialog(current.getId()));

        Button btnAddExam = new Button("+ Add Exam Deadline");
        btnAddExam.getStyleClass().add("btn-secondary");
        btnAddExam.setOnAction(e -> showAddExamDialog(current.getId()));

        topBar.getChildren().addAll(titleBox, spacer, btnAddSubject, btnAddExam);

        // Subjects Section Card
        VBox subjectsCard = new VBox(12);
        subjectsCard.getStyleClass().add("card");

        Label lblSubCardTitle = new Label("Registered Academic Subjects");
        lblSubCardTitle.getStyleClass().add("card-title");

        tableSubjects = buildSubjectTable(current.getId());
        loadSubjectData(current.getId());

        subjectsCard.getChildren().addAll(lblSubCardTitle, tableSubjects);

        // Exams Section Card
        VBox examsCard = new VBox(12);
        examsCard.getStyleClass().add("card");

        Label lblExamCardTitle = new Label("Upcoming Exam Deadlines");
        lblExamCardTitle.getStyleClass().add("card-title");

        tableExams = buildExamTable(current.getId());
        loadExamData(current.getId());

        examsCard.getChildren().addAll(lblExamCardTitle, tableExams);

        mainContainer.getChildren().addAll(topBar, subjectsCard, examsCard);
    }

    private TableView<Subject> buildSubjectTable(int studentId) {
        TableView<Subject> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(240);

        TableColumn<Subject, String> colName = new TableColumn<>("Subject Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Subject, Label> colDiff = new TableColumn<>("Difficulty");
        colDiff.setCellValueFactory(data -> new SimpleObjectProperty<>(UIHelper.createDifficultyBadge(data.getValue().getDifficulty())));

        TableColumn<Subject, String> colTopics = new TableColumn<>("Topics Progress");
        colTopics.setCellValueFactory(data -> {
            Subject s = data.getValue();
            return new SimpleStringProperty(s.getCompletedTopics() + " / " + s.getTotalTopics() + " (" + String.format("%.0f%%", s.getPreparationPercentage()) + ")");
        });

        TableColumn<Subject, ProgressBar> colBar = new TableColumn<>("Progress Bar");
        colBar.setCellValueFactory(data -> {
            ProgressBar bar = new ProgressBar(data.getValue().getPreparationPercentage() / 100.0);
            bar.setMaxWidth(Double.MAX_VALUE);
            return new SimpleObjectProperty<>(bar);
        });

        TableColumn<Subject, String> colGrade = new TableColumn<>("Target Grade");
        colGrade.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTargetGrade()));
        colGrade.setPrefWidth(90);

        TableColumn<Subject, HBox> colActions = new TableColumn<>("Actions");
        colActions.setCellValueFactory(data -> {
            Subject s = data.getValue();
            HBox box = new HBox(6);
            box.setAlignment(Pos.CENTER);

            Button btnEdit = new Button("Edit");
            btnEdit.getStyleClass().add("btn-secondary");
            btnEdit.setStyle("-fx-padding: 4 8; -fx-font-size: 11px;");
            btnEdit.setOnAction(e -> showEditSubjectDialog(s));

            Button btnDel = new Button("Delete");
            btnDel.getStyleClass().add("btn-danger");
            btnDel.setStyle("-fx-padding: 4 8; -fx-font-size: 11px;");
            btnDel.setOnAction(e -> {
                if (UIHelper.showConfirmAlert("Delete Subject", "Are you sure you want to delete '" + s.getName() + "' and its associated tasks/exams?")) {
                    try {
                        subjectService.deleteSubject(s.getId());
                        refreshView();
                    } catch (Exception ex) {
                        UIHelper.showErrorAlert("Error", ex.getMessage());
                    }
                }
            });

            box.getChildren().addAll(btnEdit, btnDel);
            return new SimpleObjectProperty<>(box);
        });

        table.getColumns().addAll(colName, colDiff, colTopics, colBar, colGrade, colActions);
        return table;
    }

    private TableView<Exam> buildExamTable(int studentId) {
        TableView<Exam> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);

        TableColumn<Exam, String> colSub = new TableColumn<>("Subject");
        colSub.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubjectName() != null ? data.getValue().getSubjectName() : "ID #" + data.getValue().getSubjectId()));

        TableColumn<Exam, String> colName = new TableColumn<>("Exam Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExamName()));

        TableColumn<Exam, String> colDate = new TableColumn<>("Exam Date");
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExamDate().toString()));

        TableColumn<Exam, String> colUrgency = new TableColumn<>("Days Remaining");
        colUrgency.setCellValueFactory(data -> {
            long days = data.getValue().getDaysRemaining();
            if (days < 0) return new SimpleStringProperty("Overdue");
            if (days == 0) return new SimpleStringProperty("TODAY");
            if (days == 1) return new SimpleStringProperty("Tomorrow (1 day)");
            return new SimpleStringProperty(days + " days remaining");
        });

        TableColumn<Exam, String> colWeight = new TableColumn<>("Weightage");
        colWeight.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.0f%%", data.getValue().getWeightagePercentage())));

        TableColumn<Exam, Button> colAction = new TableColumn<>("Action");
        colAction.setCellValueFactory(data -> {
            Exam exam = data.getValue();
            Button btnDel = new Button("Delete");
            btnDel.getStyleClass().add("btn-danger");
            btnDel.setStyle("-fx-padding: 4 8; -fx-font-size: 11px;");
            btnDel.setOnAction(e -> {
                if (UIHelper.showConfirmAlert("Delete Exam", "Are you sure you want to delete this exam deadline?")) {
                    try {
                        examService.deleteExam(exam.getId());
                        refreshView();
                    } catch (Exception ex) {
                        UIHelper.showErrorAlert("Error", ex.getMessage());
                    }
                }
            });
            return new SimpleObjectProperty<>(btnDel);
        });

        table.getColumns().addAll(colSub, colName, colDate, colUrgency, colWeight, colAction);
        return table;
    }

    private void loadSubjectData(int studentId) {
        try {
            List<Subject> list = subjectService.getSubjectsForStudent(studentId);
            tableSubjects.setItems(FXCollections.observableArrayList(list));
        } catch (Exception ex) {
            UIHelper.showErrorAlert("Error", "Could not load subjects: " + ex.getMessage());
        }
    }

    private void loadExamData(int studentId) {
        try {
            List<Exam> list = examService.getUpcomingExams(studentId);
            tableExams.setItems(FXCollections.observableArrayList(list));
        } catch (Exception ex) {
            UIHelper.showErrorAlert("Error", "Could not load exams: " + ex.getMessage());
        }
    }

    private void showAddSubjectDialog(int studentId) {
        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle("Add New Subject");
        dialog.setHeaderText("Register a course with difficulty and topic count.");

        ButtonType btnSaveType = new ButtonType("Add Subject", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSaveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField txtName = new TextField();
        txtName.setPromptText("e.g. Data Structures & Algorithms");

        ComboBox<Integer> cbDiff = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbDiff.setValue(3);

        Spinner<Integer> spTotal = new Spinner<>(1, 100, 10);
        spTotal.setEditable(true);

        Spinner<Integer> spDone = new Spinner<>(0, 100, 0);
        spDone.setEditable(true);

        TextField txtGrade = new TextField("A");

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(new Label("Difficulty (1-5):"), 0, 1);
        grid.add(cbDiff, 1, 1);
        grid.add(new Label("Total Topics/Units:"), 0, 2);
        grid.add(spTotal, 1, 2);
        grid.add(new Label("Completed Topics:"), 0, 3);
        grid.add(spDone, 1, 3);
        grid.add(new Label("Target Grade:"), 0, 4);
        grid.add(txtGrade, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSaveType) {
                try {
                    return subjectService.addSubject(
                            studentId,
                            txtName.getText(),
                            cbDiff.getValue(),
                            spTotal.getValue(),
                            spDone.getValue(),
                            txtGrade.getText()
                    );
                } catch (Exception ex) {
                    UIHelper.showErrorAlert("Error", ex.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(s -> refreshView());
    }

    private void showEditSubjectDialog(Subject subject) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Subject");
        dialog.setHeaderText("Update subject details and completion status.");

        ButtonType btnSaveType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSaveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField txtName = new TextField(subject.getName());
        ComboBox<Integer> cbDiff = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbDiff.setValue(subject.getDifficulty());

        Spinner<Integer> spTotal = new Spinner<>(1, 100, subject.getTotalTopics());
        spTotal.setEditable(true);

        Spinner<Integer> spDone = new Spinner<>(0, 100, subject.getCompletedTopics());
        spDone.setEditable(true);

        TextField txtGrade = new TextField(subject.getTargetGrade());

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(new Label("Difficulty (1-5):"), 0, 1);
        grid.add(cbDiff, 1, 1);
        grid.add(new Label("Total Topics:"), 0, 2);
        grid.add(spTotal, 1, 2);
        grid.add(new Label("Completed Topics:"), 0, 3);
        grid.add(spDone, 1, 3);
        grid.add(new Label("Target Grade:"), 0, 4);
        grid.add(txtGrade, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSaveType) {
                try {
                    subject.setName(txtName.getText());
                    subject.setDifficulty(cbDiff.getValue());
                    subject.setTotalTopics(spTotal.getValue());
                    subject.setCompletedTopics(spDone.getValue());
                    subject.setTargetGrade(txtGrade.getText());
                    return subjectService.updateSubject(subject);
                } catch (Exception ex) {
                    UIHelper.showErrorAlert("Error", ex.getMessage());
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait().ifPresent(ok -> {
            if (ok) refreshView();
        });
    }

    private void showAddExamDialog(int studentId) {
        try {
            List<Subject> subjects = subjectService.getSubjectsForStudent(studentId);
            if (subjects.isEmpty()) {
                UIHelper.showErrorAlert("No Subjects", "Please add at least one subject before scheduling an exam.");
                return;
            }

            Dialog<Exam> dialog = new Dialog<>();
            dialog.setTitle("Schedule Exam Deadline");
            dialog.setHeaderText("Set an exam deadline to inform the Intelligent Planning Engine.");

            ButtonType btnSaveType = new ButtonType("Add Exam", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(btnSaveType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(12);
            grid.setPadding(new Insets(20, 20, 10, 20));

            ComboBox<Subject> cbSubject = new ComboBox<>(FXCollections.observableArrayList(subjects));
            cbSubject.setValue(subjects.get(0));

            TextField txtExamName = new TextField();
            txtExamName.setPromptText("e.g. Midterm Examination");

            DatePicker dpDate = new DatePicker(LocalDate.now().plusDays(5));

            Spinner<Double> spWeight = new Spinner<>(5.0, 100.0, 30.0, 5.0);
            spWeight.setEditable(true);

            grid.add(new Label("Subject:"), 0, 0);
            grid.add(cbSubject, 1, 0);
            grid.add(new Label("Exam Name:"), 0, 1);
            grid.add(txtExamName, 1, 1);
            grid.add(new Label("Exam Date:"), 0, 2);
            grid.add(dpDate, 1, 2);
            grid.add(new Label("Weightage (%):"), 0, 3);
            grid.add(spWeight, 1, 3);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == btnSaveType) {
                    try {
                        return examService.addExam(
                                cbSubject.getValue().getId(),
                                txtExamName.getText(),
                                dpDate.getValue(),
                                spWeight.getValue(),
                                ""
                        );
                    } catch (Exception ex) {
                        UIHelper.showErrorAlert("Error", ex.getMessage());
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(e -> refreshView());

        } catch (Exception ex) {
            UIHelper.showErrorAlert("Error", ex.getMessage());
        }
    }
}
