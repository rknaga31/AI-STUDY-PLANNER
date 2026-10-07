package com.studyplanner.model;

/**
 * Represents the status of a scheduled study task.
 */
public enum TaskStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TaskStatus fromString(String text) {
        if (text == null) return PENDING;
        for (TaskStatus status : TaskStatus.values()) {
            if (status.name().equalsIgnoreCase(text) || status.displayName.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return PENDING;
    }
}
