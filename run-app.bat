@echo off
setlocal enabledelayedexpansion

echo ====================================================
echo   🏋️ GYM MANAGEMENT SYSTEM - 1-CLICK LAUNCHER
echo ====================================================
echo.

:: Detect or force valid JAVA_HOME
if not exist "%JAVA_HOME%\bin\javac.exe" (
    if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javac.exe" (
        set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
    ) else if exist "C:\Program Files\Java\jdk-21\bin\javac.exe" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    ) else if exist "C:\Program Files\Java\jdk-17\bin\javac.exe" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    )
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    echo [OK] Using JAVA_HOME: %JAVA_HOME%
) else (
    echo [INFO] Using System Default Java.
)

:: Load environment variables from .env if present
if exist ".env" (
    for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
        set "line=%%A"
        if not "!line:~0,1!"=="#" (
            if not "%%A"=="" (
                set "%%A=%%B"
            )
        )
    )
)

:: Check Maven
set "MVN_CMD=mvn"
if exist "temp_maven\apache-maven-3.9.6\bin\mvn.cmd" (
    set "MVN_CMD=%cd%\temp_maven\apache-maven-3.9.6\bin\mvn.cmd"
)

echo [OK] Starting Spring Boot Application...
echo [INFO] Local Web URL: http://localhost:8080
echo [INFO] Please wait while Spring Boot starts up...
echo.

:: Automatically open browser as soon as port 8080 is live
start /min powershell -NoProfile -ExecutionPolicy Bypass -Command "for ($i=0; $i -lt 45; $i++) { Start-Sleep -Seconds 1; try { $tcp = New-Object System.Net.Sockets.TcpClient; $tcp.Connect('127.0.0.1', 8080); if ($tcp.Connected) { $tcp.Close(); Start-Process 'http://localhost:8080'; break; } } catch {} }"

:: Start the application
call "%MVN_CMD%" spring-boot:run

pause
