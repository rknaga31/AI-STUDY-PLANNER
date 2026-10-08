package com.studyplanner;

import com.studyplanner.controller.LoginController;
import com.studyplanner.controller.MainViewController;
import com.studyplanner.dao.ExamDAO;
import com.studyplanner.dao.StudentDAO;
import com.studyplanner.dao.SubjectDAO;
import com.studyplanner.database.DatabaseConnection;
import com.studyplanner.model.Exam;
import com.studyplanner.model.Student;
import com.studyplanner.model.Subject;
import com.studyplanner.service.PlanService;
import com.studyplanner.service.StudentService;
import com.studyplanner.util.SessionContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Main Application Entry Point for AI-Based Intelligent Study Planner.
 */
public class Main extends Application {

    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("AI-Based Intelligent Study Planner");
        this.primaryStage.setMinWidth(960);
        this.primaryStage.setMinHeight(640);

        // Pre-seed demo student and sample subjects if fresh database
        seedDemoDataIfEmpty();

        // Show Login Screen initially
        showLoginScreen();

        primaryStage.show();
    }

    public void showLoginScreen() {
        LoginController loginController = new LoginController(student -> {
            showMainScreen();
        });

        scene = new Scene(loginController.getView(), 1080, 720);
        applyStyles(scene);
        primaryStage.setScene(scene);
    }

    public void showMainScreen() {
        MainViewController mainViewController = new MainViewController(this::showLoginScreen);
        scene = new Scene(mainViewController.getView(), 1140, 740);
        applyStyles(scene);
        primaryStage.setScene(scene);
    }

    private void applyStyles(Scene scene) {
        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Could not load style.css: " + e.getMessage());
        }
    }

    /**
     * Seeds realistic academic sample data so the prototype can be inspected
     * and evaluated immediately with subjects, difficulty levels, and exams.
     */
    private void seedDemoDataIfEmpty() {
        try {
            StudentDAO studentDAO = new StudentDAO();
            // Migrate student1 to Batman if it was already seeded
            Student oldStudent = studentDAO.findByUsername("student1");
            if (oldStudent != null) {
                studentDAO.updateUsername(oldStudent.getId(), "Batman");
                oldStudent.setFullName("Batman (Bruce Wayne)");
                studentDAO.update(oldStudent);
                System.out.println("[Main] Existing demo student migrated to 'Batman'.");
            }

            Student existing = studentDAO.findByUsername("Batman");
            if (existing == null) {
                // Create sample student
                Student demoStudent = new Student("Batman", "pass123", "Batman (Bruce Wayne)", "batman@gotham.edu", 4.0);
                demoStudent = studentDAO.create(demoStudent);

                SubjectDAO subjectDAO = new SubjectDAO();
                ExamDAO examDAO = new ExamDAO();

                // 1. High Difficulty, Low Prep, Approaching Exam (Highest Priority!)
                Subject s1 = new Subject(demoStudent.getId(), "Java Programming & OOP", 4, 12, 3, "A");
                s1 = subjectDAO.create(s1);
                examDAO.create(new Exam(s1.getId(), "Java Midterm Theory Exam", LocalDate.now().plusDays(4), 35.0, "Chapters 1-6"));

                // 2. High Difficulty, Moderate Prep, Exam in 10 days
                Subject s2 = new Subject(demoStudent.getId(), "Data Structures & Algorithms", 5, 14, 6, "A");
                s2 = subjectDAO.create(s2);
                examDAO.create(new Exam(s2.getId(), "DSA Practical Lab Assessment", LocalDate.now().plusDays(10), 30.0, "Trees, Graphs & DP"));

                // 3. Moderate Difficulty, Higher Prep
                Subject s3 = new Subject(demoStudent.getId(), "Database Management Systems", 3, 10, 7, "A");
                s3 = subjectDAO.create(s3);
                examDAO.create(new Exam(s3.getId(), "DBMS Final Exam", LocalDate.now().plusDays(18), 40.0, "SQL, Normalization & ACID"));

                // 4. Low Difficulty, High Prep, No Urgent Exam
                Subject s4 = new Subject(demoStudent.getId(), "Software Engineering", 2, 8, 6, "B+");
                subjectDAO.create(s4);

                // Auto-generate initial plan for demo student
                PlanService planService = new PlanService();
                planService.generateAndSaveDailyPlan(demoStudent.getId(), demoStudent.getTargetDailyHours(), LocalDate.now());
                System.out.println("[Main] Demo student 'Batman' and academic curriculum successfully seeded.");
            }
        } catch (Exception e) {
            System.out.println("[Main] Seed check completed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
