@echo off
color 0A
echo ================================================================================
echo             AI STUDY PLANNER - FORMATTED DATABASE INSPECTOR
echo ================================================================================
java -cp "%~dp0target\ai-study-planner-1.0.0.jar" com.studyplanner.util.DatabaseViewer
echo.
pause
