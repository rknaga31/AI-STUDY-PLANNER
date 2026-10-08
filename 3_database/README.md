# 🗄️ ROLE 3: DATABASE MODULE (MySQL, JDBC & Persistence)

**Team Member Contribution**: Role 3 — Database Engineer / DBA  
**Core Responsibility**: Relational database architecture, SQL schema design, dual-mode JDBC connection management (MySQL + SQLite fallback), and parameterized DAO layer.

---

## 1. What This Role Does in Simple Words
> *"Designs all relational tables, stores student data permanently in MySQL, ensures zero data loss, and provides instant fallback to local storage if MySQL is offline."*

This role created:
1. **Relational Schema (`schema.sql`)**: 6 fully normalized tables with Foreign Keys and `ON DELETE CASCADE`:
   - `Student`: Primary Key `id`, username, password, target daily hours, timestamp.
   - `Subject`: Foreign Key $\rightarrow$ `Student(id) ON DELETE CASCADE`.
   - `Exam`: Foreign Key $\rightarrow$ `Subject(id) ON DELETE CASCADE`.
   - `StudyTask`: Foreign Keys $\rightarrow$ `Student(id)` and `Subject(id)`.
   - `Progress`: Unique per `(student_id, subject_id)`, tracks completed topics, total topics, percentage.
   - `StudySession`: Foreign Keys $\rightarrow$ `Student(id)` and `Subject(id)`, logs study date, minutes, notes.
2. **Dual-Engine JDBC Manager (`DatabaseConnection`)**:
   - Primary: Connects to **MySQL 8.0+** (`localhost:3306/study_planner`) using credentials in `db.properties`.
   - Automatic Fallback: If MySQL is offline or credentials fail, it immediately boots an embedded **SQLite engine** (`study_planner.db`), ensuring the application never crashes.
   - Dynamic DDL: Automatically runs `schema.sql` on first boot to create all tables.
3. **Data Access Objects (DAO Layer)**:
   - Uses `java.sql.PreparedStatement` exclusively with `?` bind variables to eliminate SQL injection.
   - `StudentDAO`: User CRUD and authentication queries.
   - `SubjectDAO`: Subject CRUD with student-scoped filtering.
   - `ExamDAO`: Exam CRUD and date-ordered lookups.
   - `StudyTaskDAO`: Batch creation for generated study plans, status updates.
   - `ProgressDAO`: Upsert queries (`saveOrUpdate`) syncing syllabus completion.
   - `StudySessionDAO`: Logging study sessions and computing total hours.
4. **Demonstration Scripts**:
   - `show_mysql_tables.bat`: 1-click batch script that opens MySQL in terminal and shows all tables and rows to ma'am.
   - `view_database.bat`: Formatted ASCII table viewer for console presentations.

---

## 2. Directory Structure of This Role
```
3_database/
├── README.md                  # This contribution documentation
├── connection/                # JDBC connection manager & config
│   ├── DatabaseConnection.java
│   └── db.properties
├── dao/                       # PreparedStatement DAO layer
│   ├── StudentDAO.java
│   ├── SubjectDAO.java
│   ├── ExamDAO.java
│   ├── StudyTaskDAO.java
│   ├── ProgressDAO.java
│   └── StudySessionDAO.java
├── sql_schema/                # Relational DDL script
│   └── schema.sql
└── scripts/                   # Evaluation & inspection tools
    ├── show_mysql_tables.bat
    ├── view_database.bat
    └── DatabaseViewer.java
```

---

## 3. Technologies & Packages Used
- **DBMS**: MySQL 8.0+ (Production), SQLite 3.45 (Offline embedded engine)
- **JDBC Drivers**: `com.mysql.cj.jdbc.Driver`, `org.sqlite.JDBC`
- **Java Packages**: `java.sql.Connection`, `java.sql.DriverManager`, `java.sql.PreparedStatement`, `java.sql.ResultSet`, `java.sql.Statement`, `java.sql.SQLException`
- **Security**: Parameterized queries preventing SQL Injection.

---

## 4. How to Demonstrate to Ma'am
1. Double-click [**`show_mysql_tables.bat`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/3_database/scripts/show_mysql_tables.bat):
   - Connects to MySQL using password `RKNAGA`.
   - Displays `SHOW TABLES;` and all data records in `Student`, `Subject`, `Exam`, `StudyTask`, `Progress`.
   - Leaves you in the interactive `mysql>` prompt to run any custom SQL query requested.
2. Open the desktop app and point to the top-right header: **`● MySQL Connected`**.
3. Show [`schema.sql`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/3_database/sql_schema/schema.sql) to explain Foreign Keys, Primary Keys, and `ON DELETE CASCADE`.
