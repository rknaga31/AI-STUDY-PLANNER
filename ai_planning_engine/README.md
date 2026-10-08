# AI Planning Engine — PriorityQueue & Rule-Based Scheduler

The **AI Planning Engine** is the intelligent algorithmic core of the application. It applies rule-based heuristic reasoning, a Java `java.util.PriorityQueue`, and proportional time allocation to dynamically rank courses, calculate daily study hours, and generate transparent explanations for students.

---

## 1. Responsibilities & Functional Scope

- **Multi-Factor Priority Calculation**: Evaluates each course against four weighted academic variables to assign a composite priority score between `0.0` and `10.0`.
- **Java PriorityQueue Processing**: Utilizes a min-heap configured `PriorityQueue<PrioritizedSubject>` (with descending natural comparator) to order and retrieve the most critical subjects in $O(\log n)$ time.
- **Explainable Rule Reasoning**: Generates human-readable explanations explaining *why* each course was assigned its specific rank and priority score.
- **Daily Schedule Generation**: Dynamically allocates available study hours across high-ranking subjects, applying minimum/maximum thresholds and focus break recommendations.

---

## 2. Prioritization Heuristic Formula

The composite priority score is calculated using the following weighted multi-factor rule:

$$\text{Priority Score} = (\text{Difficulty} \times 0.25) + (\text{Exam Urgency} \times 0.35) + (\text{Prep Need} \times 0.25) + (\text{Incomplete Topics} \times 0.15)$$

### Factor Breakdown
| Parameter | Weight | Normalized Range | Description |
| :--- | :---: | :---: | :--- |
| **Course Difficulty** | **25%** | $1.0 - 5.0 \rightarrow 0 - 10$ | Student-rated subject complexity (1 = Very Easy, 5 = Very Hard). |
| **Exam Urgency** | **35%** | $0.0 - 10.0$ | Proximity of nearest upcoming exam. Exam within 2 days = $10.0$; within 7 days = $8.0$; within 14 days = $6.0$; within 30 days = $4.0$; no exam = $1.0$. |
| **Preparation Need** | **25%** | $0.0 - 10.0$ | Inverse of syllabus completion: $(100 - \text{prep}\%) / 10.0$. Low readiness yields high urgency. |
| **Incomplete Topics** | **15%** | $0.0 - 10.0$ | Work remaining: $(\text{incompleteTopics} / \text{totalTopics}) \times 10.0$. |

---

## 3. Packages Used

### Java Standard Library Packages
| Package | Primary Usage |
| :--- | :--- |
| `java.util.PriorityQueue` | Heap data structure storing `PrioritizedSubject` objects sorted by descending priority score. |
| `java.util.List` / `ArrayList` | Sequential lists of subjects and generated daily tasks. |
| `java.util.Map` | Mapping subject IDs to lists of upcoming exams. |
| `java.util.Collections` | Safe handling of empty exam lists. |
| `java.time.LocalDate` | Anchor date for calculating countdown days to assessments. |
| `java.time.temporal.ChronoUnit` | Computing precise days between current date and exam dates. |

### Internal Project Packages
| Package | Usage |
| :--- | :--- |
| `com.studyplanner.model.Subject` | Source academic subject data (difficulty, completed topics, total topics). |
| `com.studyplanner.model.Exam` | Assessment metadata (date, weightage percentage, notes). |
| `com.studyplanner.model.PrioritizedSubject` | Comparable wrapper encapsulating calculated scores and rationales. |
| `com.studyplanner.model.StudyTask` | Concrete task entity persisted in the database. |
| `com.studyplanner.model.TaskStatus` | Task lifecycle state (`PENDING`, `COMPLETED`). |

---

## 4. Directory & File Breakdown

### Planning Engine Classes (`src/main/java/com/studyplanner/planning/`)
- **[`PriorityCalculator.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/planning/PriorityCalculator.java)**
  - Calculates component scores: `calcDifficultyScore()`, `calcExamUrgencyScore()`, `calcPrepNeedScore()`, and `calcTopicWeightScore()`.
  - Determines nearest exam deadlines and weights upcoming assessments by their grading percentages.
  - Generates transparent, formatted explanation strings:
    > *"Java Programming & OOP is HIGHLY prioritized because its difficulty is high (4/5), preparation is low (25.0% with 9 topics incomplete), and the exam 'Midterm Exam' is approaching in 4 days."*
- **[`ScheduleGenerator.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/planning/ScheduleGenerator.java)**
  - Instantiates the `PriorityQueue<PrioritizedSubject>`.
  - Distributes the student's daily target hours (e.g. 4.0 hours) across top-ranked subjects.
  - Implements safeguards: minimum 45 minutes per subject, maximum 3.5 hours per subject, rounded to 15-minute intervals.
  - Generates actionable task titles: `[Deep Work - High Priority] Intensive Study & Revision: Java Programming & OOP`.
- **[`SchedulePlan.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/planning/SchedulePlan.java)**
  - Container object encapsulating the generated list of `StudyTask` items, total hours allocated, target date, and executive summary string.

---

## 5. Architectural Interaction Flow

```
[Subject List & Exam Dates]
           │
           ▼
[PriorityCalculator]
  ├── Multi-factor heuristic scoring
  └── Explainable rationale generation
           │
           ▼
[java.util.PriorityQueue<PrioritizedSubject>]
  (Extracts highest priority subjects first)
           │
           ▼
[ScheduleGenerator]
  ├── Proportional hour allocation
  └── Minimum & maximum session bounds
           │
           ▼
[SchedulePlan -> StudyTask items]
```
