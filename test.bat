@echo off
echo ========================================================
echo   Running JUnit 5 Test Suite for AI Study Planner
echo ========================================================
call "%~dp0mvnw.cmd" test
pause
