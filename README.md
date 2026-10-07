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

## 3. Team Contributions & Module Breakdown

### 1. FRONTEND — JavaFX
- **Authentication**: `LoginController` with toggling between Sign In and Registration.
- **Dashboard**: `DashboardController` featuring 4 metric cards, Today's Task checklist, and live PriorityQueue spotlight.
- **Subject Management**: `SubjectController` displaying difficulty badges, syllabus completion progress bars, and modal dialogs to add/edit subjects and exam deadlines.
- **Study Plan**: `PlannerController` with daily available hours input, interactive schedule table, and "Why Prioritized?" rule explanations.
- **Progress Tracking**: `ProgressController` with study session logger, direct topic adjustments, and session history table.
- **Navigation**: `MainViewController` with a persistent dark sidebar navigation and header bar displaying live DB connection status.

### 2. BACKEND — Java
- **Services**:
  - `StudentService`: Input validation, authentication, and profile updates.
  - `SubjectService`: Subject CRUD and automatic progress synchronization.
  - `ExamService`: Exam deadline management and nearest exam lookup.
  - `PlanService`: Orchestrates planning engine, persists generated daily tasks, and handles plan regeneration.
  - `ProgressService`: Session logging, topic incrementation, and overall syllabus readiness calculation.
- **Validation**: `ValidationUtil` enforces business rules (difficulty 1–5, email format, study hour bounds, positive topics).

### 3. DATABASE / DBMS — MySQL + JDBC
- **Schema (`src/main/resources/sql/schema.sql`)**:
  - `Student`: Primary Key `id`, username, password, target daily hours.
  - `Subject`: Foreign Key `student_id` $\rightarrow$ `Student(id) ON DELETE CASCADE`.
  - `Exam`: Foreign Key `subject_id` $\rightarrow$ `Subject(id) ON DELETE CASCADE`.
  - `StudyTask`: Foreign Keys $\rightarrow$ `Student(id)` and `Subject(id)`.
  - `Progress`: Tracks completed topics, total topics, percentage, unique per (student, subject).
  - `StudySession`: Logs session dates, duration in minutes, notes, and topics covered.
- **DAOs**: Parameterized `PreparedStatement` operations in `StudentDAO`, `SubjectDAO`, `ExamDAO`, `StudyTaskDAO`, `ProgressDAO`, and `StudySessionDAO`.
- **Connectivity**: `DatabaseConnection` reads from `db.properties`. If MySQL credentials are not yet configured or server is unreachable, it seamlessly operates on an embedded SQLite engine so the prototype can be tested immediately, and allows connecting to MySQL at any time via the UI Settings screen.

### 4. PLANNING ENGINE — Java
- **Rule-Based Prioritization**:
  $$\text{Priority Score} = (\text{Difficulty} \times 0.25) + (\text{Urgency} \times 0.35) + (\text{Prep Need} \times 0.25) + (\text{Incomplete Topics} \times 0.15)$$
- **Java PriorityQueue**:
  `PriorityCalculator` calculates scores and inserts subjects into `java.util.PriorityQueue<PrioritizedSubject>` which extracts the most critical subjects first.
- **Transparent Rule Reasoning**:
  Generates explainable rationale for why each subject is prioritized:
  > *"Java Programming & OOP is HIGHLY prioritized because its difficulty is high (4/5), preparation is low (25.0% with 9 topics incomplete), and the exam 'Java Midterm Theory Exam' is approaching in 4 days."*
- **Daily Schedule Generator**:
  `ScheduleGenerator` allocates available hours across high-priority subjects and outputs structured `StudyTask` items.

### 5. PROGRESS + TESTING — Java
- **Progress Updates & Plan Regeneration**:
  When a study session is logged or topics are completed, `PlanService.regeneratePlanForStudent()` re-ranks subjects and updates the day's study tasks.
- **Automated Tests**:
  - `PlanningEngineTest`: Validates PriorityCalculator urgency scoring, queue ordering, and time allocation.
  - `ServiceWorkflowTest`: Validates end-to-end flow: Registration $\rightarrow$ Subject & Exam creation $\rightarrow$ Plan generation $\rightarrow$ Progress update $\rightarrow$ Plan regeneration.

---

## 4. How to Run the Application

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

## 5. How to Run Unit & Integration Tests

Run the test batch file:
```cmd
test.bat
```
Or via Maven:
```cmd
.\mvnw.cmd test
```

---

## 6. Demo Account Credentials

A sample academic profile is pre-seeded on first run:
- **Username**: `student1`
- **Password**: `pass123`
- Pre-populated with courses: *Java Programming & OOP*, *Data Structures & Algorithms*, *Database Management Systems*, and *Software Engineering*, along with upcoming exam dates.
