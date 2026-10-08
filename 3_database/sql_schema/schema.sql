-- ====================================================================
-- AI-Based Intelligent Study Planner - Database Schema
-- DBMS: MySQL (Compatible with Standard SQL / SQLite fallback)
-- ====================================================================

CREATE TABLE IF NOT EXISTS Student (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    target_daily_hours DOUBLE DEFAULT 4.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Subject (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    difficulty INT NOT NULL, -- 1 (Very Easy) to 5 (Very Hard)
    total_topics INT NOT NULL DEFAULT 10,
    completed_topics INT NOT NULL DEFAULT 0,
    target_grade VARCHAR(10) DEFAULT 'A',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subject_student FOREIGN KEY (student_id) REFERENCES Student(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Exam (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_id INT NOT NULL,
    exam_name VARCHAR(100) NOT NULL,
    exam_date DATE NOT NULL,
    weightage_percentage DOUBLE DEFAULT 30.0,
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exam_subject FOREIGN KEY (subject_id) REFERENCES Subject(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS StudyTask (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    estimated_hours DOUBLE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, COMPLETED
    priority_score DOUBLE DEFAULT 0.0,
    scheduled_date DATE NOT NULL,
    reason_explanation TEXT,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_student FOREIGN KEY (student_id) REFERENCES Student(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_subject FOREIGN KEY (subject_id) REFERENCES Subject(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    completed_topics INT NOT NULL DEFAULT 0,
    total_topics INT NOT NULL DEFAULT 10,
    progress_percentage DOUBLE NOT NULL DEFAULT 0.0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_progress_student FOREIGN KEY (student_id) REFERENCES Student(id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_subject FOREIGN KEY (subject_id) REFERENCES Subject(id) ON DELETE CASCADE,
    CONSTRAINT uq_progress_student_subject UNIQUE (student_id, subject_id)
);

CREATE TABLE IF NOT EXISTS StudySession (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    session_date DATE NOT NULL,
    duration_minutes INT NOT NULL,
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_student FOREIGN KEY (student_id) REFERENCES Student(id) ON DELETE CASCADE,
    CONSTRAINT fk_session_subject FOREIGN KEY (subject_id) REFERENCES Subject(id) ON DELETE CASCADE
);
