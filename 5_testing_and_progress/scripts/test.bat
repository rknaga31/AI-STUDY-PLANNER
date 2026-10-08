@echo off
echo ========================================================
echo   Running JUnit 5 Test Suite for AI Study Planner
echo ========================================================
call "%~dp0mvnw.cmd" test
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Test execution failed with exit code %ERRORLEVEL%.
) else (
    echo.
    echo [SUCCESS] All JUnit 5 unit and integration tests passed!
)
pause
