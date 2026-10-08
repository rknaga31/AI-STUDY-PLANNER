# Backend Module — Services & Business Logic

The **Backend Module** contains the core business logic, validation rules, authentication mechanisms, and workflow orchestration for the AI-Based Intelligent Study Planner. It bridges the JavaFX frontend controllers with the Planning Engine and the Database DAO layer.

---

## 1. Responsibilities & Functional Scope

- **Student Authentication & Account Management**: Registering new students, credential validation, updating daily study targets, and maintaining session state.
- **Academic Subject Management**: Creating and updating courses, enforcing topic bounds, and synchronizing topic completions with progress records.
- **Exam Deadlines & Urgency Tracking**: Scheduling exam dates, computing countdown days, and validating exam weightages.
- **Study Plan Orchestration**: Interfacing with the heuristic Planning Engine, generating daily task allocations, persisting generated schedules, and triggering plan regenerations upon topic updates.
- **Progress Tracking & Study Log History**: Logging study sessions, incrementing completed topics, calculating syllabus readiness metrics, and computing cumulative study hours.
- **Business Rule Invariant Enforcement**: Centralized validation for input fields and academic constraints.

---

## 2. Packages Used

### Java Standard Library Packages
| Package | Primary Usage |
| :--- | :--- |
| `java.sql.SQLException` | Exception propagation and database error handling across DAO interactions. |
| `java.time.LocalDate` | Date operations for schedules, sessions, and exam deadlines. |
| `java.time.LocalDateTime` | Timestamps for session logs and entity creation. |
| `java.util.*` | Collections (`List`, `ArrayList`, `Map`, `HashMap`, `Collections`, `PriorityQueue`) for processing academic data. |
| `java.util.regex.Pattern` | Regular expression validation for email addresses. |

### Internal Project Packages
| Package | Usage |
| :--- | :--- |
| `com.studyplanner.dao.*` | PreparedStatement database interactions (`StudentDAO`, `SubjectDAO`, `ExamDAO`, `StudyTaskDAO`, `ProgressDAO`, `StudySessionDAO`). |
| `com.studyplanner.model.*` | Domain entities and DTOs (`Student`, `Subject`, `Exam`, `StudyTask`, `Progress`, `StudySession`, `PrioritizedSubject`). |
| `com.studyplanner.planning.*` | Planning engine modules (`ScheduleGenerator`, `PriorityCalculator`, `SchedulePlan`). |
| `com.studyplanner.util.*` | Input validation (`ValidationUtil`) and session tracking (`SessionContext`). |

---

## 3. Directory & File Breakdown

### Services (`src/main/java/com/studyplanner/service/`)
- **[`StudentService.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/StudentService.java)**
  - Handles user registration with username uniqueness checks and password length requirements.
  - Authenticates login credentials and populates `SessionContext`.
  - Manages student profile updates and target daily study hour preferences.
- **[`SubjectService.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/SubjectService.java)**
  - Implements CRUD operations for academic courses.
  - Automatically initializes and synchronizes linked `Progress` records when subjects or topic counts change.
  - Cascades deletions to ensure no orphaned progress or exam records remain.
- **[`ExamService.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/ExamService.java)**
  - Manages exam deadlines, weightages, and notes.
  - Filters and queries upcoming non-expired exams ordered chronologically.
- **[`PlanService.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/PlanService.java)**
  - Central orchestrator connecting the data layer with the AI `ScheduleGenerator` and `PriorityCalculator`.
  - Clears stale pending tasks for the target date and batch-inserts newly generated study tasks.
  - Implements `calculatePriorityOverview()` to populate explainable AI cards for the frontend.
  - Implements `regeneratePlanForStudent()` triggered after study sessions are completed.
- **[`ProgressService.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/ProgressService.java)**
  - Logs study session records with duration and notes.
  - Increments completed topics on subjects and recalculates the student's overall syllabus preparation percentage.
  - Updates study task status (`PENDING` $\rightarrow$ `COMPLETED`).

### Utilities & Context (`src/main/java/com/studyplanner/util/`)
- **[`ValidationUtil.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/util/ValidationUtil.java)**
  - Validates business rules across the entire application:
    - Subject difficulty: `1` to `5`
    - Target daily study hours: `0.5` to `16.0` hours
    - Completed topics: `0 <= completed <= totalTopics`
    - Study session duration: `1` to `720` minutes
    - Username and password length: $\ge 3$ characters
    - Email format verification via Regex pattern
- **[`SessionContext.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/util/SessionContext.java)**
  - Thread-safe Singleton managing the currently authenticated `Student` session context throughout GUI execution.

---

## 4. Architectural Interaction Flow

```
           [Frontend Controller]
                     │
                     ▼
             [Service Layer]
             ├── Validation (ValidationUtil)
             ├── Session State (SessionContext)
             ▼                      ▼
  [AI Planning Engine]     [DAO Layer (JDBC)]
  (PriorityQueue / Plan)    (PreparedStatement)
```
