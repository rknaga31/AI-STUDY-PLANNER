# ⚙️ ROLE 2: BACKEND MODULE (Services, Logic & Validation)

**Team Member Contribution**: Role 2 — Backend Developer  
**Core Responsibility**: Built the business logic services, data validation rules, authentication flow, and orchestration between frontend and database.

---

## 1. What This Role Does in Simple Words
> *"The brain of the application. It receives input from the screen, validates all the data against strict academic rules, processes calculations, and coordinates saving to the database."*

This role created:
1. **Input Validation (`ValidationUtil`)**: Enforces academic rules:
   - Course difficulty must be between 1 (Very Easy) and 5 (Very Hard).
   - Target daily study hours must be between 0.5 and 16.0 hours.
   - Completed topics cannot exceed total topics.
   - Study session durations must be positive and realistic (1 to 720 minutes).
   - Valid email formatting using regular expressions.
2. **Student Authentication (`StudentService`)**: Secure registration with uniqueness checks, password validation, and profile management.
3. **Session Context (`SessionContext`)**: Thread-safe Singleton maintaining the logged-in student's state across the app.
4. **Subject Synchronization (`SubjectService`)**: CRUD operations for courses that automatically create and synchronize linked syllabus progress records.
5. **Exam Urgency Service (`ExamService`)**: Computes days remaining to exams and filters non-expired deadlines.
6. **Plan Orchestrator (`PlanService`)**: Feeds course and exam data into the AI Planning Engine, stores daily task schedules, and triggers plan regeneration when study progress changes.
7. **Progress Service (`ProgressService`)**: Records study logs, increments completed syllabus units, and calculates overall readiness percentage.

---

## 2. Directory Structure of This Role
```
2_backend/
├── README.md                  # This contribution documentation
├── services/                  # Business logic services
│   ├── StudentService.java
│   ├── SubjectService.java
│   ├── ExamService.java
│   ├── PlanService.java
│   └── ProgressService.java
├── utilities/                 # Validators and session context
│   ├── ValidationUtil.java
│   └── SessionContext.java
└── models/                    # Data transfer objects & domain models
    ├── Student.java
    ├── Subject.java
    ├── Exam.java
    ├── StudyTask.java
    ├── Progress.java
    ├── StudySession.java
    └── TaskStatus.java
```

---

## 3. Technologies & Packages Used
- **Language**: Java 17+
- **Key Concepts**: Object-Oriented Design (OOP), Separation of Concerns (SoC), Singleton Pattern, Exception Handling (`IllegalArgumentException`, `SQLException`).
- **Standard Packages**: `java.util.*`, `java.time.LocalDate`, `java.time.LocalDateTime`, `java.util.regex.Pattern`.

---

## 4. How to Demonstrate to Ma'am
1. Show `ValidationUtil.java` to explain how bad data (invalid emails, negative topics, hours > 16) is rejected before reaching the database.
2. Show `PlanService.java` to explain how the backend coordinates with the AI Planning Engine to generate and save daily schedules.
3. Show `ProgressService.java` to demonstrate how completing a study session automatically updates syllabus completion percentages.
