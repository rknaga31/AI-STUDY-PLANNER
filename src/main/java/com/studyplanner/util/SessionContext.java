package com.studyplanner.util;

import com.studyplanner.model.Student;

/**
 * Global Session Context holding the authenticated student information.
 */
public class SessionContext {

    private static SessionContext instance;
    private Student currentStudent;

    private SessionContext() {}

    public static synchronized SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student currentStudent) {
        this.currentStudent = currentStudent;
    }

    public boolean isLoggedIn() {
        return currentStudent != null;
    }

    public void logout() {
        this.currentStudent = null;
    }
}
