# Frontend UI Components Package (`com.studyplanner.ui`)

This package provides reusable UI components, layout helpers, badges, and modal dialog utilities for JavaFX.

## Modules & Classes
- **[`UIHelper.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/ui/UIHelper.java)**:
  - `createMetricCard(title, value, subtext)`: Formats dashboard KPI cards.
  - `createPriorityBadge(score)`: Color-codes priority scores (CRITICAL, HIGH, MODERATE, LOW).
  - `createDifficultyBadge(difficulty)`: Formats 1–5 difficulty badges.
  - `createReasonBox(explanation)`: Displays transparent explainable AI rationale.
  - `showConfirmAlert(...)` & `showErrorAlert(...)`: Standard modal dialogs.

## Packages Used
- `javafx.scene.control.*`
- `javafx.scene.layout.*`
- `javafx.geometry.*`
