# Models & Entities Module — Domain Data Model

The **Models & Entities Module** provides the domain representations, data transfer objects (DTOs), and enumerations shared across all layers of the application—from database rows up to JavaFX table views and planning algorithms.

---

## 1. Responsibilities & Functional Scope

- **Object-Relational Mapping (ORM)**: Encapsulates database tables (`Student`, `Subject`, `Exam`, `StudyTask`, `Progress`, `StudySession`) into strongly-typed Java objects.
- **Data Encapsulation & Validation**: Provides accessor/mutator methods with internal range clamps (e.g. difficulty between 1 and 5, completed topics $\le$ total topics).
- **Comparable Sorting**: Implements `Comparable<PrioritizedSubject>` to enable native sorting in `java.util.PriorityQueue`.
- **State Representation**: Enforces valid lifecycle states for daily study tasks via `TaskStatus`.

---

## 2. Packages Used

### Java Standard Library Packages
| Package | Primary Usage |
| :--- | :--- |
| `java.time.LocalDate` | Dates for exams, tasks, and study sessions without timezone ambiguity. |
| `java.time.LocalDateTime` | High-precision audit timestamps (`createdAt`). |
| `java.lang.Comparable` | Natural ordering contract implemented by `PrioritizedSubject`. |

---

## 3. Directory & File Breakdown

### Entities (`src/main/java/com/studyplanner/model/`)
- **[`Student.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/Student.java)**
  - Represents the authenticated user profile.
  - Fields: `id`, `username`, `password`, `fullName`, `email`, `targetDailyHours`, `createdAt`.
- **[`Subject.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/Subject.java)**
  - Represents an academic course.
  - Fields: `id`, `studentId`, `name`, `difficulty` (1 to 5), `totalTopics`, `completedTopics`, `targetGrade`, `createdAt`.
  - Helper methods: `getIncompleteTopics()`, `getPreparationPercentage()`, `getDifficultyLabel()`.
- **[`Exam.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/Exam.java)**
  - Represents an upcoming evaluation or assessment deadline.
  - Fields: `id`, `subjectId`, `examName`, `examDate`, `weightagePercentage`, `notes`, `subjectName`.
  - Helper methods: `getDaysRemaining()`, `isUrgent()`.
- **[`StudyTask.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/StudyTask.java)**
  - Represents an actionable, generated daily study item.
  - Fields: `id`, `studentId`, `subjectId`, `title`, `estimatedHours`, `priorityScore`, `taskDate`, `status`, `subjectName`.
- **[`Progress.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/Progress.java)**
  - Tracks syllabus completion metrics per course.
  - Fields: `id`, `studentId`, `subjectId`, `completedTopics`, `totalTopics`, `percentage`, `subjectName`.
- **[`StudySession.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/StudySession.java)**
  - Historical log of completed study sessions.
  - Fields: `id`, `studentId`, `subjectId`, `sessionDate`, `durationMinutes`, `notes`, `subjectName`.
- **[`PrioritizedSubject.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/PrioritizedSubject.java)**
  - DTO encapsulating subject urgency, calculated priority score, and rule-based explanations.
  - Implements `Comparable<PrioritizedSubject>` to sort descending by priority score inside Java's `PriorityQueue`.
- **[`TaskStatus.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/model/TaskStatus.java)**
  - Enum representing task completion status: `PENDING`, `IN_PROGRESS`, `COMPLETED`, `SKIPPED`.

---

## 4. Entity Relationship Diagram (ERD)

```
┌────────────────┐       1:N       ┌────────────────┐
│    Student     │ ─────────────── │    Subject     │
└────────────────┘                 └────────────────┘
        │                                   │
        │ 1:N                               │ 1:N
        ▼                                   ▼
┌────────────────┐                 ┌────────────────┐
│   StudyTask    │                 │      Exam      │
└────────────────┘                 └────────────────┘
        │                                   │
        │ 1:N                               │ 1:N
        ▼                                   ▼
┌────────────────┐                 ┌────────────────┐
│  StudySession  │                 │    Progress    │
└────────────────┘                 └────────────────┘
```
