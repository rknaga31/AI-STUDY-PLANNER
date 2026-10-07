package com.studyplanner.controller;

import com.studyplanner.database.DatabaseConnection;
import com.studyplanner.model.Student;
import com.studyplanner.service.StudentService;
import com.studyplanner.ui.UIHelper;
import com.studyplanner.util.SessionContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Controller for Student Profile, Daily Goals, and MySQL Database Configuration.
 */
public class ProfileController {

    private final StudentService studentService = new StudentService();
    private final Runnable refreshHeaderCallback;

    private VBox mainContainer;
    private Label lblDbStatus;

    public ProfileController(Runnable refreshHeaderCallback) {
        this.refreshHeaderCallback = refreshHeaderCallback;
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

        // Title
        VBox titleBox = new VBox(4);
        Label pageTitle = new Label("Student Profile & Database Settings");
        pageTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label pageSub = new Label("Manage personal study preferences and MySQL persistent database connection.");
        pageSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        titleBox.getChildren().addAll(pageTitle, pageSub);

        HBox split = new HBox(20);

        // Left Card: Profile & Target Hours
        VBox profileCard = new VBox(14);
        profileCard.getStyleClass().add("card");
        profileCard.setMinWidth(380);
        profileCard.setPrefWidth(420);

        Label pTitle = new Label("Student Profile & Study Goals");
        pTitle.getStyleClass().add("card-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        TextField txtName = new TextField(current.getFullName());
        TextField txtEmail = new TextField(current.getEmail());
        TextField txtHours = new TextField(String.valueOf(current.getTargetDailyHours()));

        grid.add(new Label("Username:"), 0, 0);
        Label uLbl = new Label(current.getUsername());
        uLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #4338ca;");
        grid.add(uLbl, 1, 0);

        grid.add(new Label("Full Name:"), 0, 1);
        grid.add(txtName, 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(txtEmail, 1, 2);

        grid.add(new Label("Target Daily Hours:"), 0, 3);
        grid.add(txtHours, 1, 3);

        Button btnSaveProfile = new Button("Save Profile Changes");
        btnSaveProfile.getStyleClass().add("btn-primary");
        btnSaveProfile.setOnAction(e -> {
            try {
                double hours = Double.parseDouble(txtHours.getText().trim());
                current.setFullName(txtName.getText().trim());
                current.setEmail(txtEmail.getText().trim());
                current.setTargetDailyHours(hours);

                studentService.updateProfile(current);
                UIHelper.showInfoAlert("Success", "Profile updated successfully!");
                if (refreshHeaderCallback != null) refreshHeaderCallback.run();
                refreshView();
            } catch (Exception ex) {
                UIHelper.showErrorAlert("Error", ex.getMessage());
            }
        });

        profileCard.getChildren().addAll(pTitle, grid, btnSaveProfile);

        // Right Card: MySQL Connection Management
        VBox dbCard = new VBox(14);
        dbCard.getStyleClass().add("card");
        HBox.setHgrow(dbCard, Priority.ALWAYS);

        Label dbTitle = new Label("MySQL Database Connectivity (JDBC)");
        dbTitle.getStyleClass().add("card-title");

        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        lblDbStatus = new Label("Active Engine: " + dbConn.getCurrentDatabaseType() + " (" + dbConn.getConnectionStatusMessage() + ")");
        lblDbStatus.setWrapText(true);
        if (dbConn.isUsingMySQL()) {
            lblDbStatus.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold; -fx-background-color: #ecfdf5; -fx-padding: 8 12; -fx-background-radius: 6;");
        } else {
            lblDbStatus.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold; -fx-background-color: #eff6ff; -fx-padding: 8 12; -fx-background-radius: 6;");
        }

        GridPane dbGrid = new GridPane();
        dbGrid.setHgap(10);
        dbGrid.setVgap(10);

        TextField txtHost = new TextField("localhost");
        TextField txtPort = new TextField("3306");
        TextField txtDbName = new TextField("study_planner");
        TextField txtUser = new TextField("root");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Enter your MySQL root password");

        dbGrid.add(new Label("MySQL Host:"), 0, 0);
        dbGrid.add(txtHost, 1, 0);
        dbGrid.add(new Label("Port:"), 0, 1);
        dbGrid.add(txtPort, 1, 1);
        dbGrid.add(new Label("Database Name:"), 0, 2);
        dbGrid.add(txtDbName, 1, 2);
        dbGrid.add(new Label("Username:"), 0, 3);
        dbGrid.add(txtUser, 1, 3);
        dbGrid.add(new Label("Password:"), 0, 4);
        dbGrid.add(txtPass, 1, 4);

        Button btnConnectMySQL = new Button("Test & Connect to MySQL");
        btnConnectMySQL.getStyleClass().add("btn-primary");
        btnConnectMySQL.setOnAction(e -> {
            boolean ok = dbConn.switchAndTestMySQL(
                    txtHost.getText().trim(),
                    txtPort.getText().trim(),
                    txtDbName.getText().trim(),
                    txtUser.getText().trim(),
                    txtPass.getText()
            );
            if (ok) {
                UIHelper.showInfoAlert("MySQL Connected", "Successfully connected to MySQL database!");
            } else {
                UIHelper.showErrorAlert("MySQL Connection Failed", dbConn.getConnectionStatusMessage());
            }
            if (refreshHeaderCallback != null) refreshHeaderCallback.run();
            refreshView();
        });

        Label dbHelp = new Label("Note: If MySQL credentials are not provided or server is down, the system maintains complete functionality using local storage.");
        dbHelp.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        dbHelp.setWrapText(true);

        dbCard.getChildren().addAll(dbTitle, lblDbStatus, dbGrid, btnConnectMySQL, dbHelp);

        split.getChildren().addAll(profileCard, dbCard);

        mainContainer.getChildren().addAll(titleBox, split);
    }
}
