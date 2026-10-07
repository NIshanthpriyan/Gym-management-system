@echo off
echo ====================================================
echo Starting Local MongoDB Server on Port 27017...
echo Database Storage Path: data\db
echo ====================================================

if not exist "data\db" mkdir "data\db"

for /r "temp_mongodb" %%f in (mongod.exe) do (
    set MONGOD_PATH=%%f
    goto :found
)

echo mongod.exe not found in temp_mongodb. Looking in system PATH...
set MONGOD_PATH=mongod.exe

:found
echo Running: "%MONGOD_PATH%" --dbpath "data\db" --port 27017
"%MONGOD_PATH%" --dbpath "data\db" --port 27017
pause
