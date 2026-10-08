# Data Access Objects Package (`com.studyplanner.dao`)

This package provides parameterized `PreparedStatement` CRUD operations for all entities in the database.

## Modules & Classes
- **[`StudentDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/StudentDAO.java)**: Student record insertion, profile updates, credential authentication.
- **[`SubjectDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/SubjectDAO.java)**: Subject CRUD and completed topics updates.
- **[`ExamDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/ExamDAO.java)**: Exam CRUD and date-ordered queries.
- **[`StudyTaskDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/StudyTaskDAO.java)**: Batch creation of daily schedule tasks, task status updates.
- **[`ProgressDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/ProgressDAO.java)**: Upsert logic (`saveOrUpdate`) syncing syllabus completion percentages.
- **[`StudySessionDAO.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/dao/StudySessionDAO.java)**: Study session logging and cumulative study time queries.

## Packages Used
- `java.sql.*` (`Connection`, `PreparedStatement`, `ResultSet`, `SQLException`, `Statement`)
- `java.time.LocalDate`
- `java.util.*`
- `com.studyplanner.database.DatabaseConnection`
- `com.studyplanner.model.*`
