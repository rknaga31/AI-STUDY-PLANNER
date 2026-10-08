# Frontend Module — JavaFX GUI

The **Frontend Module** delivers the presentation tier of the AI-Based Intelligent Study Planner. It is built natively with **JavaFX 21**, providing a responsive desktop interface designed around modern UX principles, including a persistent sidebar, cards, interactive tables, progress bars, and modal dialogs.

---

## 1. Responsibilities & Functional Scope

- **User Authentication**: Secure Sign-in and Registration views with interactive toggle.
- **Dashboard Overview**: Metric cards summarizing registered subjects, upcoming exams, today's study hours, and syllabus readiness, alongside a Priority Spotlight.
- **Subject & Exam Management**: Dynamic table views with visual difficulty badges, syllabus completion progress bars, and modal dialogs to register courses and upcoming exam deadlines.
- **AI Study Planner**: Real-time study schedule generation with available hours input, interactive task completion checklist, and explainable AI rationale cards.
- **Progress Tracking & Study Sessions**: Logging completed study sessions, adjusting syllabus topics, and reviewing historical study logs.
- **Profile & Database Settings**: User profile updates and live database connection testing (MySQL / SQLite toggle).

---

## 2. Packages Used

### JavaFX Standard Packages (`org.openjfx`)
| Package | Primary Usage |
| :--- | :--- |
| `javafx.application.Application` | Application lifecycle, launch, and stage management. |
| `javafx.stage.Stage` | Desktop window stage configuration (dimensions, titles, icons). |
| `javafx.scene.Scene` | Scene graph container for views and stylesheet registration. |
| `javafx.scene.Parent` | Base class for hierarchical scene graph nodes. |
| `javafx.scene.layout.*` | Layout containers (`BorderPane`, `VBox`, `HBox`, `GridPane`, `Region`, `Priority`). |
| `javafx.scene.control.*` | Interactive UI controls (`Button`, `Label`, `TextField`, `PasswordField`, `TableView`, `TableColumn`, `ProgressBar`, `Spinner`, `ComboBox`, `DatePicker`, `Dialog`, `ScrollPane`, `Separator`). |
| `javafx.geometry.*` | View alignment and padding (`Insets`, `Pos`). |
| `javafx.collections.*` | Observable collections (`FXCollections.observableArrayList`) for reactive tables and dropdowns. |
| `javafx.beans.property.*` | Table cell data binding (`SimpleStringProperty`, `SimpleObjectProperty`). |

### Internal Project Packages
| Package | Usage |
| :--- | :--- |
| `com.studyplanner.service.*` | Invocations to backend services (`StudentService`, `SubjectService`, `ExamService`, `PlanService`, `ProgressService`). |
| `com.studyplanner.model.*` | Data binding with domain entities (`Student`, `Subject`, `Exam`, `StudyTask`, `PrioritizedSubject`). |
| `com.studyplanner.util.*` | Session state (`SessionContext`) and input validation. |
| `com.studyplanner.database.*` | Live DB status verification via `DatabaseConnection`. |

---

## 3. Directory & File Breakdown

### Entry Point & Launchers
- **[`Main.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/Main.java)**
  - Initializes the primary JavaFX stage (`Stage`).
  - Pre-seeds demo curriculum data if the database is unpopulated.
  - Switches between Login and Main Dashboard scenes.
  - Applies global CSS stylesheet (`/css/style.css`).
- **[`AppLauncher.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/AppLauncher.java)**
  - Standalone bootstrap class that redirects to `Main.main()`.
  - Bypasses module encapsulation checks when launching from a shaded JAR.

### View Controllers (`src/main/java/com/studyplanner/controller/`)
- **[`MainViewController.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/MainViewController.java)**
  - Main application shell containing the persistent dark sidebar and header.
  - Displays dynamic live database status badges (`MySQL Connected` / `Local DB Mode`).
  - Implements tab switching between Dashboard, Subjects, Planner, Progress, and Profile.
- **[`LoginController.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/LoginController.java)**
  - Authentication screen supporting seamless switching between Sign In and Registration.
  - Validates user input before invoking `StudentService`.
- **[`DashboardController.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/DashboardController.java)**
  - Executive summary featuring 4 key metric cards: Total Subjects, Upcoming Exams, Today's Pending Hours, and Overall Readiness.
  - Features the **Priority Queue Spotlight** and Today's Task quick checklist.
- **[`SubjectController.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/SubjectController.java)**
  - Manages course curriculum and exam deadlines.
  - Renders difficulty level badges (1 = Very Easy to 5 = Very Hard) and syllabus progress bars.
  - Modal dialogs for adding/editing courses and scheduling exam dates with weightages.
- **[`PlannerController.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/PlannerController.java)**
  - Interface for the AI Planning Engine.
  - Accepts available study hours for today and triggers schedule generation.
  - Displays generated study tasks and explainable reasoning cards detailing *why* each subject was prioritized.
- **[`ProgressController.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/ProgressController.java)**
  - Interactive syllabus progress monitor.
  - Modal study session logger allowing students to record minutes studied and topics completed.
  - Direct quick-adjustment buttons (`+` and `-`) for updating topic completion in real-time.
- **[`ProfileController.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/ProfileController.java)**
  - User profile management (editing name, email, target daily hours).
  - Database connectivity panel displaying live engine status and connection parameters.

### UI Utilities & Styling
- **[`UIHelper.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/ui/UIHelper.java)**
  - Helper functions for creating styled metric cards, colored priority badges, difficulty tags, and alert popups.
- **[`style.css`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/resources/css/style.css)**
  - Unified stylesheet defining dark sidebar styling, modern indigo/slate palette, glassmorphic cards, rounded buttons, and hover transitions.

---

## 4. Architectural Interaction Flow

```
[User Action in JavaFX View]
           │
           ▼
[ViewController (Validation & View State)]
           │
           ▼
[Backend Service Layer (Business Logic & Transactions)]
           │
           ▼
[Observable Collection / DTO Update]
           │
           ▼
[JavaFX TableView / Cards UI Re-render]
```
