# Utilities & Session Package (`com.studyplanner.util`)

This package provides business rule validation and session context management across the application.

## Modules & Classes
- **[`ValidationUtil.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/util/ValidationUtil.java)**: Central validation for inputs (difficulty 1–5, email format, study hours 0.5–16, topic counts, exam weightage).
- **[`SessionContext.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/util/SessionContext.java)**: Thread-safe Singleton storing the currently logged-in student.

## Packages Used
- `java.time.LocalDate`
- `java.util.regex.Pattern`
- `com.studyplanner.model.Student`
