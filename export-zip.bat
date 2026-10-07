@echo off
setlocal

echo ====================================================
echo   📦 PACKAGING GYM MANAGEMENT SYSTEM TO ZIP
echo ====================================================
echo.
echo Packaging project files...

powershell -NoProfile -ExecutionPolicy Bypass -Command "$exclude = 'target', 'temp_maven', 'temp_mongodb', 'temp_mongodb.zip', '.git', '.vscode', 'Gym-Management-System-Source.zip'; $items = Get-ChildItem | Where-Object { $exclude -notcontains $_.Name }; Compress-Archive -Path $items -DestinationPath 'Gym-Management-System-Source.zip' -Force; Write-Host '[SUCCESS] Zip archive created successfully!'"

echo.
echo ====================================================
echo   ✅ DONE! Your ZIP file is ready in this folder:
echo   %cd%\Gym-Management-System-Source.zip
echo ====================================================
echo.
