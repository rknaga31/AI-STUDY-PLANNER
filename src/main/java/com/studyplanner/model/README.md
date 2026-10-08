# Domain Models Package (`com.studyplanner.model`)

This package defines the domain entities, comparable wrappers, and enums representing the core academic concepts.

## Modules & Classes
- **[`Student.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/Student.java)**: User profile, target study hours, credentials.
- **[`Subject.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/Subject.java)**: Academic course, difficulty rating, topic counts, target grade.
- **[`Exam.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/Exam.java)**: Assessment date, weightage percentage, days remaining.
- **[`StudyTask.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/StudyTask.java)**: Scheduled study task, estimated hours, priority score, status.
- **[`Progress.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/Progress.java)**: Topic completion tracking and percentage calculation.
- **[`StudySession.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/StudySession.java)**: Study history entry with duration and date.
- **[`PrioritizedSubject.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/PrioritizedSubject.java)**: DTO implementing `Comparable` for `PriorityQueue` sorting.
- **[`TaskStatus.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/TaskStatus.java)**: Lifecycle enum (`PENDING`, `IN_PROGRESS`, `COMPLETED`, `SKIPPED`).

## Packages Used
- `java.time.LocalDate`
- `java.time.LocalDateTime`
