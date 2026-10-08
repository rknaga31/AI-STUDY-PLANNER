# Planning Engine Package (`com.studyplanner.planning`)

This package contains the heuristic algorithm, priority calculation rules, Java `PriorityQueue` processing, and schedule generation logic.

## Modules & Classes
- **[`PriorityCalculator.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/planning/PriorityCalculator.java)**: Evaluates Difficulty, Urgency, Prep Need, and Incomplete Topics into a 0.0–10.0 score with explainable text.
- **[`ScheduleGenerator.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/planning/ScheduleGenerator.java)**: Uses a `PriorityQueue` to rank courses and allocates daily available study hours.
- **[`SchedulePlan.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/planning/SchedulePlan.java)**: Value object encapsulating generated tasks, summary, and total allocated hours.

## Packages Used
- `java.util.PriorityQueue`
- `java.util.List`, `ArrayList`, `Map`
- `java.time.LocalDate`
- `java.time.temporal.ChronoUnit`
- `com.studyplanner.model.*`
