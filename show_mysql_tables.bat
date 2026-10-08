@echo off
color 0B
echo ================================================================================
echo               AI-BASED INTELLIGENT STUDY PLANNER - MYSQL DATABASE
echo ================================================================================
echo  Connecting to MySQL Server (localhost:3306) on database 'study_planner'...
echo ================================================================================
echo.

set "MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

if not exist "%MYSQL_PATH%" (
    echo [ERROR] Could not find mysql.exe at:
    echo %MYSQL_PATH%
    pause
    exit /b 1
)

echo >>> 1. LISTING ALL CREATED TABLES IN DATABASE 'study_planner':
echo --------------------------------------------------------------------------------
"%MYSQL_PATH%" -u root -pRKNAGA -e "USE study_planner; SHOW TABLES;"
echo.

echo >>> 2. STUDENT TABLE (DEMO USER & PROFILES):
echo --------------------------------------------------------------------------------
"%MYSQL_PATH%" -u root -pRKNAGA -e "USE study_planner; SELECT id, username, full_name, email, target_daily_hours FROM Student;"
echo.

echo >>> 3. SUBJECT TABLE (ACADEMIC COURSES & PROGRESS):
echo --------------------------------------------------------------------------------
"%MYSQL_PATH%" -u root -pRKNAGA -e "USE study_planner; SELECT id, name, difficulty, total_topics, completed_topics, target_grade FROM Subject;"
echo.

echo >>> 4. EXAM TABLE (UPCOMING EXAMS & WEIGHTAGES):
echo --------------------------------------------------------------------------------
"%MYSQL_PATH%" -u root -pRKNAGA -e "USE study_planner; SELECT id, exam_name, exam_date, weightage_percentage, notes FROM Exam;"
echo.

echo >>> 5. STUDYTASK TABLE (AI-GENERATED DAILY SCHEDULE):
echo --------------------------------------------------------------------------------
"%MYSQL_PATH%" -u root -pRKNAGA -e "USE study_planner; SELECT id, title, estimated_hours, priority_score, status, scheduled_date FROM StudyTask;"
echo.

echo >>> 6. PROGRESS TABLE (SYLLABUS COMPLETION STATUS):
echo --------------------------------------------------------------------------------
"%MYSQL_PATH%" -u root -pRKNAGA -e "USE study_planner; SELECT id, subject_id, completed_topics, total_topics, progress_percentage FROM Progress;"
echo.

echo ================================================================================
echo  Entering interactive MySQL session. Type your SQL queries or 'exit' to quit.
echo ================================================================================
echo.
"%MYSQL_PATH%" -u root -pRKNAGA study_planner

pause
