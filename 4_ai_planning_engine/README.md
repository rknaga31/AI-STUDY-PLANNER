# 🧠 ROLE 4: AI PLANNING ENGINE (PriorityQueue & Heuristic Scheduler)

**Team Member Contribution**: Role 4 — AI & Planning Algorithm Engineer  
**Core Responsibility**: Built the core algorithmic heuristic model, Java `PriorityQueue` ranking system, time allocation logic, and transparent rule-based reasoning engine.

---

## 1. What This Role Does in Simple Words
> *"Builds the smart math formula and algorithm that figures out which subject the student needs to study first today, how many hours to spend on each, and explains WHY in plain English."*

This role created:
1. **Multi-Factor Heuristic Formula (`PriorityCalculator`)**:
   $$\text{Priority Score} = (\text{Difficulty} \times 0.25) + (\text{Exam Urgency} \times 0.35) + (\text{Prep Need} \times 0.25) + (\text{Incomplete Topics} \times 0.15)$$
   - **Difficulty (25%)**: Subject complexity (1 = Very Easy to 5 = Very Hard).
   - **Exam Urgency (35%)**: Proximity to nearest exam ($<2$ days = $10.0$, $<7$ days = $8.0$, $<14$ days = $6.0$, $<30$ days = $4.0$).
   - **Preparation Need (25%)**: Inverse of syllabus readiness ($(100 - \text{prep}\%) / 10.0$).
   - **Incomplete Topics (15%)**: Unfinished units ratio ($(\text{incomplete} / \text{total}) \times 10.0$).
2. **Java `PriorityQueue` Ranking Engine**:
   - Uses `java.util.PriorityQueue<PrioritizedSubject>` to sort and extract courses in descending priority in $O(\log n)$ time.
   - `PrioritizedSubject` implements `Comparable<PrioritizedSubject>` to enforce strict natural ordering.
3. **Daily Schedule Allocator (`ScheduleGenerator`)**:
   - Takes available hours (e.g., 4.0 hours) and allocates study intervals proportionally across top-ranking subjects.
   - Enforces safeguards: minimum 45 minutes per subject, maximum 3.5 hours per subject, rounded to 15-minute blocks.
   - Embeds focus break recommendations (e.g. 10-minute break between heavy sessions).
4. **Transparent Rule-Based Reasoning Engine**:
   - Generates human-readable rationales displayed on the user interface:
     > *"Java Programming & OOP is HIGHLY prioritized because its difficulty is high (4/5), preparation is low (25.0% with 9 topics incomplete), and the exam 'Midterm Exam' is approaching in 4 days."*

---

## 2. Directory Structure of This Role
```
4_ai_planning_engine/
├── README.md                  # This contribution documentation
├── algorithms/                # Algorithmic engine classes
│   ├── PriorityCalculator.java
│   ├── ScheduleGenerator.java
│   └── SchedulePlan.java
└── models/                    # Comparable priority wrapper
    └── PrioritizedSubject.java
```

---

## 3. Technologies & Packages Used
- **Language**: Java 17+
- **Data Structures**: `java.util.PriorityQueue`, `java.util.List`, `java.util.Map`
- **Interfaces**: `java.lang.Comparable<PrioritizedSubject>`
- **Date Arithmetic**: `java.time.LocalDate`, `java.time.temporal.ChronoUnit`
- **Algorithms**: Heuristic multi-criteria decision modeling, proportional resource scheduling.

---

## 4. How to Demonstrate to Ma'am
1. Open the application and navigate to the **"Study Planner"** tab.
2. Change the available study hours slider/spinner (e.g. from 4.0 to 6.0 hours) and click **"Generate Study Plan"**.
3. Show the **"Why Are These Subjects Prioritized?"** cards: explain how the AI formula calculated the exact score for Java vs DBMS vs Software Engineering.
4. Show [`PriorityCalculator.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/4_ai_planning_engine/algorithms/PriorityCalculator.java) to review the weighted formula code.
