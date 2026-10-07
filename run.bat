@echo off
echo ========================================================
echo   Starting AI-Based Intelligent Study Planner (JavaFX)
echo ========================================================
java -jar "%~dp0target\ai-study-planner-1.0.0.jar"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with code %ERRORLEVEL%.
    pause
)
