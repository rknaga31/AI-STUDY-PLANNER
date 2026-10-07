package com.studyplanner.controller;

import com.studyplanner.database.DatabaseConnection;
import com.studyplanner.model.Student;
import com.studyplanner.util.SessionContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Main application shell containing Sidebar Navigation, Header Bar, and Content view switcher.
 */
public class MainViewController {

    private final Runnable onLogout;

    private BorderPane rootLayout;
    private Label lblHeaderTitle;
    private Label lblUserBadge;
    private Label lblDbBadge;

    private Button btnNavDashboard;
    private Button btnNavSubjects;
    private Button btnNavPlanner;
    private Button btnNavProgress;
    private Button btnNavProfile;

    // View Controllers
    private DashboardController dashboardController;
    private SubjectController subjectController;
    private PlannerController plannerController;
    private ProgressController progressController;
    private ProfileController profileController;

    private String currentNavKey = "dashboard";

    public MainViewController(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    public Parent getView() {
        rootLayout = new BorderPane();

        // 1. Sidebar
        VBox sidebar = buildSidebar();
        rootLayout.setLeft(sidebar);

        // 2. Header
        HBox header = buildHeader();
        rootLayout.setTop(header);

        // 3. Initialize Controllers
        dashboardController = new DashboardController(
                () -> navigateTo("subjects"),
                () -> navigateTo("planner")
        );
        subjectController = new SubjectController();
        plannerController = new PlannerController();
        progressController = new ProgressController(
                () -> navigateTo("planner")
        );
        profileController = new ProfileController(this::refreshHeader);

        // Initial view
        navigateTo("dashboard");

        return rootLayout;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");

        // Brand
        VBox brandBox = new VBox(2);
        brandBox.setPadding(new Insets(0, 0, 16, 0));
        Label brandTitle = new Label("AI Study Planner");
        brandTitle.getStyleClass().add("sidebar-brand-title");
        Label brandSub = new Label("Intelligent Academic Scheduler");
        brandSub.getStyleClass().add("sidebar-brand-sub");
        brandBox.getChildren().addAll(brandTitle, brandSub);

        // Navigation Buttons
        btnNavDashboard = createNavButton("Dashboard", "dashboard");
        btnNavSubjects = createNavButton("Subjects & Exams", "subjects");
        btnNavPlanner = createNavButton("Study Planner", "planner");
        btnNavProgress = createNavButton("Progress & Sessions", "progress");
        btnNavProfile = createNavButton("Profile & Database", "profile");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Sign Out");
        btnLogout.getStyleClass().add("nav-logout-button");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> {
            SessionContext.getInstance().logout();
            if (onLogout != null) onLogout.run();
        });

        sidebar.getChildren().addAll(
                brandBox,
                new Separator(),
                btnNavDashboard,
                btnNavSubjects,
                btnNavPlanner,
                btnNavProgress,
                btnNavProfile,
                spacer,
                btnLogout
        );

        return sidebar;
    }

    private Button createNavButton(String text, String key) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> navigateTo(key));
        return btn;
    }

    private HBox buildHeader() {
        HBox header = new HBox(16);
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER_LEFT);

        lblHeaderTitle = new Label("Dashboard");
        lblHeaderTitle.getStyleClass().add("header-page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblDbBadge = new Label();
        updateDbBadge();

        lblUserBadge = new Label();
        updateUserBadge();

        header.getChildren().addAll(lblHeaderTitle, spacer, lblDbBadge, lblUserBadge);
        return header;
    }

    private void updateDbBadge() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        lblDbBadge.getStyleClass().removeAll("header-db-badge-mysql", "header-db-badge-sqlite");
        if (db.isUsingMySQL()) {
            lblDbBadge.setText("● MySQL Connected");
            lblDbBadge.getStyleClass().add("header-db-badge-mysql");
        } else {
            lblDbBadge.setText("● Local DB Mode (SQLite)");
            lblDbBadge.getStyleClass().add("header-db-badge-sqlite");
        }
    }

    private void updateUserBadge() {
        Student current = SessionContext.getInstance().getCurrentStudent();
        if (current != null) {
            lblUserBadge.setText(current.getFullName() + " (" + current.getUsername() + ")");
            lblUserBadge.getStyleClass().add("header-user-badge");
        }
    }

    public void refreshHeader() {
        updateDbBadge();
        updateUserBadge();
    }

    public void navigateTo(String key) {
        this.currentNavKey = key;

        // Reset all button styles
        btnNavDashboard.getStyleClass().remove("nav-button-active");
        btnNavSubjects.getStyleClass().remove("nav-button-active");
        btnNavPlanner.getStyleClass().remove("nav-button-active");
        btnNavProgress.getStyleClass().remove("nav-button-active");
        btnNavProfile.getStyleClass().remove("nav-button-active");

        switch (key) {
            case "dashboard":
                btnNavDashboard.getStyleClass().add("nav-button-active");
                lblHeaderTitle.setText("Dashboard Overview");
                rootLayout.setCenter(dashboardController.getView());
                break;
            case "subjects":
                btnNavSubjects.getStyleClass().add("nav-button-active");
                lblHeaderTitle.setText("Subject & Exam Management");
                rootLayout.setCenter(subjectController.getView());
                break;
            case "planner":
                btnNavPlanner.getStyleClass().add("nav-button-active");
                lblHeaderTitle.setText("AI Study Planner & Priority Ranking");
                rootLayout.setCenter(plannerController.getView());
                break;
            case "progress":
                btnNavProgress.getStyleClass().add("nav-button-active");
                lblHeaderTitle.setText("Progress Tracker & Study Sessions");
                rootLayout.setCenter(progressController.getView());
                break;
            case "profile":
                btnNavProfile.getStyleClass().add("nav-button-active");
                lblHeaderTitle.setText("Profile & Database Settings");
                rootLayout.setCenter(profileController.getView());
                break;
        }
    }
}
