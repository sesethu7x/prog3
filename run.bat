@echo off
setlocal
cd /d "%~dp0"
mvn exec:java "-Dexec.mainClass=app.App"
if errorlevel 1 pause
