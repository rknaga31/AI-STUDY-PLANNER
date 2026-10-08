# Backend Services Package (`com.studyplanner.service`)

This package contains the application's business logic services, transaction orchestration, and validation checks.

## Modules & Classes
- **[`StudentService.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/StudentService.java)**: User registration, credential authentication, profile management.
- **[`SubjectService.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/SubjectService.java)**: Subject CRUD and progress synchronization.
- **[`ExamService.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/ExamService.java)**: Exam date scheduling and upcoming exam filtering.
- **[`PlanService.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/PlanService.java)**: Coordinates Planning Engine, task persistence, and plan regeneration.
- **[`ProgressService.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/service/ProgressService.java)**: Study session logging, topic increments, and syllabus readiness metrics.

## Packages Used
- `java.sql.SQLException`
- `java.time.LocalDate`
- `java.util.*`
- `com.studyplanner.dao.*`
- `com.studyplanner.model.*`
- `com.studyplanner.planning.*`
- `com.studyplanner.util.*`
