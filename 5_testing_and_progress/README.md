# 🧪 ROLE 5: TESTING & PROGRESS TRACKING MODULE (QA & Verification)

**Team Member Contribution**: Role 5 — QA, Test Automation & Progress Tracking Engineer  
**Core Responsibility**: Built the automated unit and integration test suites, verified algorithmic correctness, and designed the study session logging and syllabus progress tracking engine.

---

## 1. What This Role Does in Simple Words
> *"Makes sure the application is bug-free, verifies that the math and database work correctly under tests, and tracks how much syllabus a student actually completes every day."*

This role created:
1. **Automated Unit Testing (`PlanningEngineTest`)**:
   - Tests `PriorityCalculator` to verify urgency scores increase as exam dates approach.
   - Tests `PriorityQueue` ordering to ensure the most urgent subjects are always extracted first.
   - Tests `ScheduleGenerator` to verify total allocated study time matches the student's available daily hours.
2. **End-to-End Integration Testing (`ServiceWorkflowTest`)**:
   - Executes the complete user lifecycle in an automated test:
     $$\text{Register} \rightarrow \text{Add Course} \rightarrow \text{Schedule Exam} \rightarrow \text{Generate Daily Plan} \rightarrow \text{Log Study Session} \rightarrow \text{Regenerate Plan}$$
   - Validates that syllabus readiness percentage recalculates accurately after completed sessions.
3. **Progress Tracking Engine (`ProgressService`)**:
   - Records study session logs with duration in minutes, notes, and topics covered.
   - Calculates cumulative syllabus preparation percentages:
     $$\text{Readiness } \% = \frac{\sum \text{Completed Topics}}{\sum \text{Total Topics}} \times 100$$
4. **Test Runner Automation (`test.bat`)**:
   - 1-click execution script running all JUnit 5 test suites through Maven with human-readable success/failure reporting.

---

## 2. Directory Structure of This Role
```
5_testing_and_progress/
├── README.md                  # This contribution documentation
├── unit_tests/                # JUnit 5 algorithm tests
│   └── PlanningEngineTest.java
├── integration_tests/         # End-to-end workflow tests
│   └── ServiceWorkflowTest.java
├── progress_tracking/         # Progress business logic & models
│   ├── ProgressService.java
│   ├── Progress.java
│   └── StudySession.java
└── scripts/                   # Test runner script
    └── test.bat
```

---

## 3. Technologies & Packages Used
- **Testing Framework**: JUnit 5 Jupiter (`org.junit.jupiter.api.*`)
- **Annotations**: `@Test`, `@BeforeEach`
- **Assertions**: `assertEquals`, `assertTrue`, `assertNotNull`, `assertFalse`
- **Build Tool**: Apache Maven (`mvnw.cmd test`, `maven-surefire-plugin`)

---

## 4. How to Demonstrate to Ma'am
1. Double-click [**`test.bat`**](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/5_testing_and_progress/scripts/test.bat):
   - Shows the JUnit 5 test suite running in the terminal.
   - Demonstrates that **all 4 tests pass with 0 errors and 0 failures** (`BUILD SUCCESS`).
2. Show [`PlanningEngineTest.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/5_testing_and_progress/unit_tests/PlanningEngineTest.java) to explain how the priority calculations are mathematically tested.
3. Show [`ServiceWorkflowTest.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/5_testing_and_progress/integration_tests/ServiceWorkflowTest.java) to explain how the full database + service workflow is verified.
