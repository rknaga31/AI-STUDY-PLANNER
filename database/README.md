# Database Module — JDBC, MySQL & SQLite Fallback

The **Database Module** manages persistent storage, relational schemas, parameterized queries, and connection lifecycles for the AI-Based Intelligent Study Planner. It supports **MySQL 8.0+** as the primary enterprise database, with an automatic, zero-configuration fallback to an **embedded SQLite** engine for offline execution and instant evaluation.

---

## 1. Responsibilities & Functional Scope

- **Dual-Engine Connection Management**: Automatically attempts connection to MySQL using credentials from `db.properties`; if MySQL is unreachable or offline, seamlessly initializes local SQLite storage (`study_planner.db`).
- **Automatic Schema Migration**: Parses and executes DDL scripts (`schema.sql`) on startup, creating tables, foreign keys, cascades, and indices.
- **DAO Abstraction Layer**: Exposes strongly typed CRUD operations using SQL `PreparedStatement` to eliminate SQL injection vulnerabilities.
- **Batch Processing**: Supports batch execution for daily generated study tasks.
- **Cascading Integrity**: Ensures deletions (e.g. deleting a subject) cleanly cascade to linked exams, progress records, and tasks.

---

## 2. Packages Used

### Java Standard Library Packages
| Package | Primary Usage |
| :--- | :--- |
| `java.sql.Connection` | Active database session interface. |
| `java.sql.DriverManager` | Driver resolution and connection factory. |
| `java.sql.PreparedStatement` | Parameterized SQL query and DML execution. |
| `java.sql.ResultSet` | Cursor for reading query result rows. |
| `java.sql.Statement` | DDL script execution for schema initialization. |
| `java.sql.SQLException` | Checked exceptions for SQL errors. |
| `java.io.InputStream` | Reading resource streams (`db.properties`, `schema.sql`). |
| `java.io.BufferedReader` | Line-by-line SQL script parsing. |
| `java.util.Properties` | Key-value configuration management for database credentials. |

### JDBC Drivers & Dependencies
| Library / Driver | Maven Artifact | Purpose |
| :--- | :--- | :--- |
| `com.mysql.cj.jdbc.Driver` | `com.mysql:mysql-connector-j:8.3.0` | Production MySQL 8.0+ JDBC connector. |
| `org.sqlite.JDBC` | `org.xerial:sqlite-jdbc:3.45.1.0` | Embedded SQLite engine for offline fallback. |

---

## 3. Directory & File Breakdown

### Database Connection (`src/main/java/com/studyplanner/database/`)
- **[`DatabaseConnection.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/database/DatabaseConnection.java)**
  - Thread-safe Singleton managing database connections.
  - Tries MySQL connection based on `db.properties` (`localhost:3306/study_planner`).
  - Automatically switches to SQLite (`jdbc:sqlite:study_planner.db`) upon network or credential failure.
  - Dynamically runs `executeSchemaInit()` to ensure all required tables exist.
  - Provides runtime query methods: `isUsingMySQL()` and `getCurrentDatabaseType()` displayed on the UI status bar.

### Data Access Objects (`src/main/java/com/studyplanner/dao/`)
- **[`StudentDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/StudentDAO.java)**
  - Student creation, profile updating, username lookup, and authentication queries.
- **[`SubjectDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/SubjectDAO.java)**
  - Subject CRUD, student-scoped retrieval, and direct completed-topic update queries.
- **[`ExamDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/ExamDAO.java)**
  - Exam creation, deletion, date-sorted retrieval, and joining with subject names.
- **[`StudyTaskDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/StudyTaskDAO.java)**
  - Batch insertion of daily tasks, task status updates (`PENDING` $\rightarrow$ `COMPLETED`), and date-based task queries.
- **[`ProgressDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/ProgressDAO.java)**
  - Upsert logic (`saveOrUpdate`) syncing topic completion percentages per student and subject.
- **[`StudySessionDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/StudySessionDAO.java)**
  - Logs study sessions, queries historical sessions, and computes total minutes studied per student.

### Resources & Configuration (`src/main/resources/`)
- **[`schema.sql`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/resources/sql/schema.sql)**
  - Standard relational SQL schema defining tables:
    - `Student`: `id`, `username`, `password`, `full_name`, `email`, `target_daily_hours`, `created_at`
    - `Subject`: `id`, `student_id` (FK $\rightarrow$ `Student(id) ON DELETE CASCADE`), `name`, `difficulty`, `total_topics`, `completed_topics`, `target_grade`
    - `Exam`: `id`, `subject_id` (FK $\rightarrow$ `Subject(id) ON DELETE CASCADE`), `exam_name`, `exam_date`, `weightage_percentage`, `notes`
    - `StudyTask`: `id`, `student_id`, `subject_id`, `title`, `estimated_hours`, `priority_score`, `task_date`, `status`
    - `Progress`: `id`, `student_id`, `subject_id`, `completed_topics`, `total_topics`, `percentage`
    - `StudySession`: `id`, `student_id`, `subject_id`, `session_date`, `duration_minutes`, `notes`
- **[`db.properties`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/resources/db.properties)**
  - Configuration properties: MySQL host, port, database name, user, password, and SQLite database URL.

---

## 4. Architectural Interaction Flow

```
              [Service Layer]
                     │
                     ▼
                 [DAO Layer]
    (StudentDAO, SubjectDAO, ExamDAO, etc.)
                     │
         PreparedStatement Queries
                     │
                     ▼
          [DatabaseConnection]
                     │
      ┌──────────────┴──────────────┐
      ▼                             ▼
[MySQL Server]             [SQLite Local DB]
(Primary DBMS)             (Automatic Fallback)
```
