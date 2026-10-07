@echo off
setlocal

echo ====================================================
echo   📦 PACKAGING GYM MANAGEMENT SYSTEM TO ZIP
echo ====================================================
echo.
echo Packaging project files (excluding heavy cache/temp files)...

set "OUTPUT_ZIP=Gym-Management-System-Source.zip"

powershell -NoProfile -Command ^
  "$exclude = @('target', 'temp_maven', 'temp_mongodb', 'temp_mongodb.zip', '.git', '.vscode', 'Gym-Management-System-Source.zip'); " ^
  "$items = Get-ChildItem -Path . | Where-Object { $exclude -notcontains $_.Name }; " ^
  "if (Test-Path '%OUTPUT_ZIP%') { Remove-Item '%OUTPUT_ZIP%' -Force }; " ^
  "Compress-Archive -Path $items.FullName -DestinationPath '%OUTPUT_ZIP%' -Force; " ^
  "Write-Host '[SUCCESS] Project successfully packaged to: %OUTPUT_ZIP%'"

echo.
echo ====================================================
echo   ✅ DONE! Your ZIP file is ready in this folder:
echo   %cd%\%OUTPUT_ZIP%
echo ====================================================
echo.
pause
