@echo off
setlocal enabledelayedexpansion

echo ====================================================
echo   🏋️ GYM MANAGEMENT SYSTEM - 1-CLICK LAUNCHER
echo ====================================================
echo.

:: Detect or set JAVA_HOME
if not defined JAVA_HOME (
    if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot" (
        set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
    ) else if exist "C:\Program Files\Java\jdk-21" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    ) else if exist "C:\Program Files\Java\jdk-17" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    )
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    echo [OK] Using JAVA_HOME: %JAVA_HOME%
) else (
    echo [INFO] Using System Default Java.
)

:: Check Maven
set "MVN_CMD=mvn"
if exist "temp_maven\apache-maven-3.9.6\bin\mvn.cmd" (
    set "MVN_CMD=temp_maven\apache-maven-3.9.6\bin\mvn.cmd"
)

echo [OK] Starting Spring Boot Application...
echo [INFO] Local Web URL: http://localhost:8080
echo.

:: Open browser automatically after 6 seconds in background
start /min cmd /c "timeout /t 6 /nobreak >nul && start http://localhost:8080"

:: Start the application
call "%MVN_CMD%" spring-boot:run

pause
