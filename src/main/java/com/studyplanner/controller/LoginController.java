package com.studyplanner.controller;

import com.studyplanner.database.DatabaseConnection;
import com.studyplanner.model.Student;
import com.studyplanner.service.StudentService;
import com.studyplanner.ui.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;

/**
 * Controller for User Authentication (Sign In and Account Registration).
 */
public class LoginController {

    private final StudentService studentService;
    private final Consumer<Student> onLoginSuccess;

    private boolean isRegisterMode = false;

    // Form fields
    private TextField txtUsername;
    private PasswordField txtPassword;
    private TextField txtFullName;
    private TextField txtEmail;
    private TextField txtTargetHours;
    private Label lblError;
    private Label lblTitle;
    private Label lblSubtitle;
    private Button btnSubmit;
    private Button btnToggle;
    private VBox registerFieldsBox;

    public LoginController(Consumer<Student> onLoginSuccess) {
        this.studentService = new StudentService();
        this.onLoginSuccess = onLoginSuccess;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a;");

        // Centered Auth Box
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setMaxWidth(420);
        card.setPadding(new Insets(32, 36, 32, 36));
        card.setAlignment(Pos.TOP_LEFT);

        // App Logo / Title
        Label brand = new Label("AI Study Planner");
        brand.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4f46e5;");

        lblTitle = new Label("Welcome back");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        lblSubtitle = new Label("Sign in to your intelligent study planner");
        lblSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        // Error message label
        lblError = new Label();
        lblError.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblError.setWrapText(true);
        lblError.setVisible(false);
        lblError.setManaged(false);

        // Form Fields
        Label lblUser = new Label("Username");
        lblUser.getStyleClass().add("input-label");
        txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");

        Label lblPass = new Label("Password");
        lblPass.getStyleClass().add("input-label");
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");

        // Registration-only fields
        registerFieldsBox = new VBox(12);
        registerFieldsBox.setVisible(false);
        registerFieldsBox.setManaged(false);

        Label lblName = new Label("Full Name");
        lblName.getStyleClass().add("input-label");
        txtFullName = new TextField();
        txtFullName.setPromptText("e.g. Naga Raghav");

        Label lblMail = new Label("Email Address");
        lblMail.getStyleClass().add("input-label");
        txtEmail = new TextField();
        txtEmail.setPromptText("e.g. student@university.edu");

        Label lblHours = new Label("Target Daily Study Hours");
        lblHours.getStyleClass().add("input-label");
        txtTargetHours = new TextField("4.0");
        txtTargetHours.setPromptText("e.g. 4.0");

        registerFieldsBox.getChildren().addAll(lblName, txtFullName, lblMail, txtEmail, lblHours, txtTargetHours);

        // Action Buttons
        btnSubmit = new Button("Sign In");
        btnSubmit.getStyleClass().add("btn-primary");
        btnSubmit.setMaxWidth(Double.MAX_VALUE);
        btnSubmit.setOnAction(e -> handleAuth());

        // Also submit on Enter key in password field
        txtPassword.setOnAction(e -> handleAuth());

        btnToggle = new Button("Don't have an account? Create one");
        btnToggle.setStyle("-fx-background-color: transparent; -fx-text-fill: #4f46e5; -fx-cursor: hand; -fx-font-size: 12px;");
        btnToggle.setOnAction(e -> toggleMode());

        // Quick Demo Accounts Fill for instant testing
        Button btnDemo = new Button("Quick Fill Demo Account");
        btnDemo.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 11px; -fx-cursor: hand; -fx-background-radius: 4;");
        btnDemo.setOnAction(e -> {
            txtUsername.setText("student1");
            txtPassword.setText("pass123");
        });

        // DB status badge at bottom of login
        String dbInfo = DatabaseConnection.getInstance().getCurrentDatabaseType();
        Label lblDbStatus = new Label("Storage: " + dbInfo);
        lblDbStatus.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        card.getChildren().addAll(
                brand, lblTitle, lblSubtitle,
                lblError,
                lblUser, txtUsername,
                lblPass, txtPassword,
                registerFieldsBox,
                btnSubmit,
                btnToggle,
                new Separator(),
                btnDemo,
                lblDbStatus
        );

        StackPane centerWrapper = new StackPane(card);
        centerWrapper.setPadding(new Insets(40));
        root.setCenter(centerWrapper);

        return root;
    }

    private void toggleMode() {
        isRegisterMode = !isRegisterMode;
        lblError.setVisible(false);
        lblError.setManaged(false);

        if (isRegisterMode) {
            lblTitle.setText("Create New Account");
            lblSubtitle.setText("Set up your profile to start intelligent planning");
            btnSubmit.setText("Create Account & Sign In");
            btnToggle.setText("Already have an account? Sign In");
            registerFieldsBox.setVisible(true);
            registerFieldsBox.setManaged(true);
        } else {
            lblTitle.setText("Welcome back");
            lblSubtitle.setText("Sign in to your intelligent study planner");
            btnSubmit.setText("Sign In");
            btnToggle.setText("Don't have an account? Create one");
            registerFieldsBox.setVisible(false);
            registerFieldsBox.setManaged(false);
        }
    }

    private void handleAuth() {
        lblError.setVisible(false);
        lblError.setManaged(false);

        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            if (isRegisterMode) {
                String fullName = txtFullName.getText();
                String email = txtEmail.getText();
                double targetHours = 4.0;
                try {
                    targetHours = Double.parseDouble(txtTargetHours.getText().trim());
                } catch (Exception ex) {
                    showError("Invalid target study hours format. Use e.g. 4.0");
                    return;
                }

                Student student = studentService.register(username, password, fullName, email, targetHours);
                if (onLoginSuccess != null) {
                    onLoginSuccess.accept(student);
                }
            } else {
                Student student = studentService.login(username, password);
                if (onLoginSuccess != null) {
                    onLoginSuccess.accept(student);
                }
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}
