# AI-Based Intelligent Study Planner

A modular Java desktop application that calculates academic priorities and generates personalized daily study schedules using rule-based reasoning, a Java `PriorityQueue`, JDBC connectivity, and MySQL persistent storage.

---

## 1. System Architecture

```
   JavaFX Desktop GUI (Controls, Cards & Views)
          ↓
   Controllers (Login, Dashboard, Subject, Planner, Progress, Profile)
          ↓
   Service / Backend (Validation, Session Context, Business Logic)
          ↓
   Planning Engine (PriorityCalculator, PriorityQueue, ScheduleGenerator)
          ↓
   DAO Layer (PreparedStatement CRUD operations)
          ↓
   JDBC Connection Manager (DatabaseConnection)
          ↓
   MySQL Persistent Storage (with automatic SQLite fallback)
```

---

## 2. Tech Stack

- **Core Language**: Java (JDK 17+)
- **Frontend GUI**: JavaFX 21 (Rich controls, TableViews, Cards, CSS theme)
- **Database Connectivity**: JDBC (`PreparedStatement`, Connection Management)
- **DBMS**: MySQL 8.0+ (with SQLite local fallback if MySQL is offline)
- **Build Tool**: Apache Maven (Wrapper included: `mvnw.cmd`)
- **Testing**: JUnit 5

---

## 3. Team Contribution Folders (5 Separate Roles)

To cleanly demonstrate individual contributions, the codebase provides **5 separate dedicated folders** arranged by function:

| Team Role | Dedicated Folder & Documentation | Core Contents | Key Responsibilities |
| :--- | :--- | :--- | :--- |
| **Role 1: Frontend** | [**`1_frontend/README.md`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/1_frontend/README.md) | `controllers/`, `ui_components/`, `styles/`, `entrypoints/` | JavaFX views, dashboard cards, navigation sidebar, modals, CSS styling. |
| **Role 2: Backend** | [**`2_backend/README.md`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/2_backend/README.md) | `services/`, `utilities/`, `models/` | Business logic, authentication, input validation, session context. |
| **Role 3: Database** | [**`3_database/README.md`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/3_database/README.md) | `connection/`, `dao/`, `sql_schema/`, `scripts/` | MySQL schema, JDBC connection manager, SQLite fallback, DAOs. |
| **Role 4: AI Engine** | [**`4_ai_planning_engine/README.md`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/4_ai_planning_engine/README.md) | `algorithms/`, `models/` | Multi-factor heuristic formula, `PriorityQueue`, time allocator. |
| **Role 5: Testing & QA**| [**`5_testing_and_progress/README.md`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/5_testing_and_progress/README.md) | `unit_tests/`, `integration_tests/`, `progress_tracking/`, `scripts/` | Automated JUnit 5 tests, study session logger, readiness metrics. |

---

## 4. Team Roles & Responsibilities (5 Roles Explained Simply)

This project was built cooperatively across 5 functional specializations. Here is what each role does in simple, clear terms:

---

### 🎨 Role 1: Frontend Developer (UI & User Experience)
> *"Builds everything the student sees, clicks, and interacts with on the desktop screen."*

- **Documentation**: [**`frontend/README.md`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/frontend/README.md)
- **Folder / Files**: `src/main/java/com/studyplanner/controller/`, `src/main/java/com/studyplanner/ui/`, `src/main/resources/css/style.css`
- **Key Classes**: [`LoginController`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/LoginController.java), [`DashboardController`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/DashboardController.java), [`SubjectController`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/SubjectController.java), [`PlannerController`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/PlannerController.java), [`ProgressController`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/ProgressController.java), [`MainViewController`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/controller/MainViewController.java), [`UIHelper`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/ui/UIHelper.java).
- **Technologies Used**: JavaFX 21 (`TableView`, `GridPane`, `Dialog`, `ProgressBar`, `Spinner`), CSS3 theme (glassmorphism, slate/indigo styling).
- **What They Delivered**:
  1. Login & Registration toggle screen with instant field validation.
  2. Persistent dark sidebar navigation and header bar showing live database connectivity.
  3. Dashboard with 4 metric cards (Total Subjects, Upcoming Exams, Today's Hours, Syllabus %) and a Priority Spotlight.
  4. Subject management table with color-coded difficulty badges (1-5), syllabus progress bars, and Add/Edit popup dialogs.
  5. Interactive study planner view with daily hours spinner and checklist tasks.

---

### ⚙️ Role 2: Backend Developer (Business Logic & Validation)
> *"The brain of the app that processes student data, enforces academic rules, and connects views to the database."*

- **Documentation**: [**`backend/README.md`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/backend/README.md)
- **Folder / Files**: `src/main/java/com/studyplanner/service/`, `src/main/java/com/studyplanner/util/`
- **Key Classes**: [`StudentService`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/StudentService.java), [`SubjectService`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/SubjectService.java), [`ExamService`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/ExamService.java), [`PlanService`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/PlanService.java), [`ProgressService`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/ProgressService.java), [`ValidationUtil`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/util/ValidationUtil.java), [`SessionContext`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/util/SessionContext.java).
- **Technologies Used**: Core Java (JDK 17+), Regex, Singleton Pattern, Transaction Management.
- **What They Delivered**:
  1. Input validation enforcing business rules (difficulty 1–5, study hours 0.5–16, email format, non-empty courses).
  2. Student authentication, credential verification, and thread-safe session tracking.
  3. Automatic data synchronization (when subjects are created or topics are completed, progress is updated in real time).
  4. Workflow coordination between user requests, the AI Planning Engine, and persistent storage.

---

### 🗄️ Role 3: Database Engineer (MySQL & JDBC Persistence)
> *"Designs relational tables, stores all academic records permanently, and guarantees zero data loss."*

- **Documentation**: [**`database/README.md`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/database/README.md)
- **Folder / Files**: `src/main/java/com/studyplanner/database/`, `src/main/java/com/studyplanner/dao/`, `src/main/resources/sql/schema.sql`, `src/main/resources/db.properties`
- **Key Classes**: [`DatabaseConnection`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/database/DatabaseConnection.java), [`StudentDAO`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/StudentDAO.java), [`SubjectDAO`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/SubjectDAO.java), [`ExamDAO`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/ExamDAO.java), [`StudyTaskDAO`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/StudyTaskDAO.java), [`ProgressDAO`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/ProgressDAO.java), [`StudySessionDAO`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/StudySessionDAO.java).
- **Technologies Used**: MySQL 8.0, SQLite, JDBC (`PreparedStatement`, `Connection`, `ResultSet`), SQL DDL/DML.
- **What They Delivered**:
  1. Designed the relational schema with 6 tables (`Student`, `Subject`, `Exam`, `StudyTask`, `Progress`, `StudySession`) with Foreign Keys and `ON DELETE CASCADE`.
  2. Built the dual-engine connection manager that connects to MySQL, and automatically falls back to local SQLite if offline.
  3. Wrote parameterized `PreparedStatement` DAOs for all entities, preventing SQL injection.
  4. Created 1-click database inspector scripts ([`show_mysql_tables.bat`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/show_mysql_tables.bat), [`view_database.bat`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/view_database.bat)) to present tables to evaluators.

---

### 🧠 Role 4: AI & Planning Algorithm Engineer (PriorityQueue & Scheduling)
> *"Builds the intelligent heuristic formula that calculates which subject the student should study first today."*

- **Documentation**: [**`ai_planning_engine/README.md`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/ai_planning_engine/README.md)
- **Folder / Files**: `src/main/java/com/studyplanner/planning/`
- **Key Classes**: [`PriorityCalculator`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/planning/PriorityCalculator.java), [`ScheduleGenerator`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/planning/ScheduleGenerator.java), [`SchedulePlan`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/planning/SchedulePlan.java), [`PrioritizedSubject`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/PrioritizedSubject.java).
- **Technologies Used**: `java.util.PriorityQueue`, Multi-Factor Heuristic Modeling, `Comparable` interface.
- **What They Delivered**:
  1. Invented the 4-factor composite priority heuristic formula:
     $$\text{Priority Score} = (\text{Difficulty} \times 0.25) + (\text{Exam Urgency} \times 0.35) + (\text{Prep Need} \times 0.25) + (\text{Incomplete Topics} \times 0.15)$$
  2. Used a Java `PriorityQueue<PrioritizedSubject>` to automatically pull highest-urgency courses first in $O(\log n)$ time.
  3. Built the daily time allocator that splits available hours across courses with min/max caps and break reminders.
  4. Generated human-readable rule explanations (*"Why Prioritized?"*) so the student understands exactly why an assignment is scheduled.

---

### 🧪 Role 5: Testing & Progress Tracking Engineer (QA & Verification)
> *"Tests the whole application for bugs, verifies calculation accuracy, and logs study sessions."*

- **Documentation**: [**`models_and_entities/README.md`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/models_and_entities/README.md)
- **Folder / Files**: `src/test/java/com/studyplanner/`, `test.bat`
- **Key Classes**: [`PlanningEngineTest`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/test/java/com/studyplanner/planning/PlanningEngineTest.java), [`ServiceWorkflowTest`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/test/java/com/studyplanner/service/ServiceWorkflowTest.java), [`ProgressService`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/ProgressService.java), [`StudySession`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/StudySession.java).
- **Technologies Used**: JUnit 5 (`@Test`, `Assertions`), Automated regression testing, Batch automation.
- **What They Delivered**:
  1. Automated test suite verifying priority score weights, queue ordering, and schedule duration bounds.
  2. End-to-end integration test validating: Registration $\rightarrow$ Subject & Exam creation $\rightarrow$ Plan generation $\rightarrow$ Progress update $\rightarrow$ Plan regeneration.
  3. Study session logging system tracking duration in minutes, notes, and topic increments.
  4. Syllabus progress calculation engine tracking cumulative readiness percentages.


---

## 5. How to Run the Application

### Option A: Quick Launch (Batch File)
Double-click or run:
```cmd
run.bat
```

### Option B: Maven JavaFX Run
```cmd
.\mvnw.cmd javafx:run
```

### Option C: Run Executable JAR
```cmd
java -jar target\ai-study-planner-1.0.0.jar
```

---

## 6. How to Run Unit & Integration Tests

Run the test batch file:
```cmd
test.bat
```
Or via Maven:
```cmd
.\mvnw.cmd test
```

---

## 7. Demo Account Credentials
 
 A sample academic profile is pre-seeded on first run:
- **Username**: `Batman`
- **Password**: `pass123`
- **Full Name**: `Batman (Bruce Wayne)`
- Pre-populated with courses: *Java Programming & OOP*, *Data Structures & Algorithms*, *Database Management Systems*, and *Software Engineering*, along with upcoming exam dates.
