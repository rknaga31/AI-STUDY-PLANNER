# 🎨 ROLE 1: FRONTEND MODULE (JavaFX UI & Presentation)

**Team Member Contribution**: Role 1 — Frontend & UI/UX Developer  
**Core Responsibility**: Built the entire desktop user interface, interactive scenes, event handlers, modal dialogs, and styling.

---

## 1. What This Role Does in Simple Words
> *"Everything the student sees, clicks, types, and interacts with on the computer screen was designed and programmed in this module."*

This role created:
1. The **Authentication Screen**: Toggle between Student Login and Registration with real-time error alerts.
2. The **Main Application Shell**: Persistent dark sidebar navigation and header with live database connectivity status (`● MySQL Connected`).
3. The **Dashboard Overview**: 4 metric cards (Total Subjects, Upcoming Exams, Today's Tasks, Overall Syllabus Prep %) and a Priority Spotlight.
4. The **Course & Exam Manager**: Table views with color-coded difficulty badges (1 to 5), syllabus progress bars, and modal dialogs to register courses and upcoming exam deadlines.
5. The **AI Study Planner View**: Daily available study hours spinner, generated tasks table, and explainable AI cards (*"Why Prioritized?"*).
6. The **Progress Tracker**: Modal dialog to record completed study sessions (duration and topics covered) with real-time topic adjustment buttons (`+` and `-`).
7. The **Profile & Database Panel**: Student profile management and database engine status monitor.

---

## 2. Directory Structure of This Role
```
1_frontend/
├── README.md                  # This contribution documentation
├── controllers/               # JavaFX view controllers
│   ├── LoginController.java
│   ├── MainViewController.java
│   ├── DashboardController.java
│   ├── SubjectController.java
│   ├── PlannerController.java
│   ├── ProgressController.java
│   └── ProfileController.java
├── ui_components/             # Reusable UI widgets and helpers
│   └── UIHelper.java
├── styles/                    # Modern CSS theme
│   └── style.css
└── entrypoints/               # Application startup classes
    ├── Main.java
    └── AppLauncher.java
```

---

## 3. Technologies & Packages Used
- **Language**: Java 17+
- **GUI Framework**: JavaFX 21 (`javafx.controls`, `javafx.fxml`, `javafx.graphics`, `javafx.base`)
- **Key Controls**: `TableView`, `TableColumn`, `ProgressBar`, `Spinner`, `ComboBox`, `DatePicker`, `Dialog`, `BorderPane`, `GridPane`, `VBox`, `HBox`
- **Data Binding**: `ObservableList`, `SimpleStringProperty`, `SimpleObjectProperty`
- **Styling**: Vanilla CSS3 (`style.css` - slate/indigo palette, rounded cards, button hover states)

---

## 4. How to Demonstrate to Ma'am
1. Run the application via `run.bat`.
2. Demonstrate switching between **Sign In** and **Register**.
3. Log in with demo account (`Batman` / `pass123`).
4. Click through all sidebar tabs: **Dashboard** $\rightarrow$ **Subjects & Exams** $\rightarrow$ **Study Planner** $\rightarrow$ **Progress & Sessions** $\rightarrow$ **Profile & Database**.
5. Click **"+ Add New Subject"** or **"+ Add Exam Deadline"** to show the popup modal forms.
