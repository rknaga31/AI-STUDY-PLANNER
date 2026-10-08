package com.studyplanner.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Reusable UI component builder and alert dialog utilities.
 */
public class UIHelper {

    public static VBox createMetricCard(String title, String value, String subtitle) {
        VBox card = new VBox(6);
        card.getStyleClass().add("metric-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("metric-label");

        Label lblVal = new Label(value);
        lblVal.getStyleClass().add("metric-value");

        Label lblSub = new Label(subtitle);
        lblSub.getStyleClass().add("metric-sub");

        card.getChildren().addAll(lblTitle, lblVal, lblSub);
        return card;
    }

    public static Label createDifficultyBadge(int difficulty) {
        String label;
        String style;
        switch (difficulty) {
            case 5:
                label = "Level 5 (Very Hard)";
                style = "badge-diff-5";
                break;
            case 4:
                label = "Level 4 (Hard)";
                style = "badge-diff-4";
                break;
            case 3:
                label = "Level 3 (Moderate)";
                style = "badge-diff-3";
                break;
            case 2:
                label = "Level 2 (Easy)";
                style = "badge-diff-2";
                break;
            default:
                label = "Level 1 (Very Easy)";
                style = "badge-diff-1";
                break;
        }
        Label badge = new Label(label);
        badge.getStyleClass().add(style);
        return badge;
    }

    public static Label createPriorityBadge(double priorityScore) {
        String label;
        String style;
        if (priorityScore >= 70.0) {
            label = String.format("High (%.1f)", priorityScore);
            style = "badge-priority-high";
        } else if (priorityScore >= 45.0) {
            label = String.format("Medium (%.1f)", priorityScore);
            style = "badge-priority-med";
        } else {
            label = String.format("Normal (%.1f)", priorityScore);
            style = "badge-priority-normal";
        }
        Label badge = new Label(label);
        badge.getStyleClass().add(style);
        return badge;
    }

    public static VBox createReasonBox(String explanation) {
        VBox box = new VBox(4);
        box.getStyleClass().add("reason-box");

        Label heading = new Label("AI Priority Rule Reasoning:");
        heading.setStyle("-fx-font-weight: bold; -fx-text-fill: #4338ca; -fx-font-size: 11px;");

        Label reason = new Label(explanation);
        reason.getStyleClass().add("reason-text");
        reason.setWrapText(true);

        box.getChildren().addAll(heading, reason);
        return box;
    }

    public static void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean showConfirmAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
