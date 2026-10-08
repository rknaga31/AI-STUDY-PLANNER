@echo off
echo ========================================================
echo   Starting AI-Based Intelligent Study Planner (JavaFX)
echo ========================================================

set "JAR_PATH=%~dp0target\ai-study-planner-1.0.0.jar"

if not exist "%JAR_PATH%" (
    echo Build artifact not found. Building project with Maven...
    call "%~dp0mvnw.cmd" clean package -DskipTests
    if %ERRORLEVEL% NEQ 0 (
        echo [ERROR] Build failed! Please review Maven output above.
        pause
        exit /b %ERRORLEVEL%
    )
)

echo Launching JavaFX Application...
java --enable-native-access=ALL-UNNAMED -jar "%JAR_PATH%"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with code %ERRORLEVEL%.
    pause
)
