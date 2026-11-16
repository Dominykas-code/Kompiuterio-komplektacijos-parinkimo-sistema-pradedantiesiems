@echo off
setlocal
cd /d "%~dp0.."
if not exist .core-build mkdir .core-build
javac -encoding UTF-8 -d .core-build backend\src\main\java\lt\pcbuilder\domain\*.java backend\src\test\java\lt\pcbuilder\domain\CoreSelfTest.java
if errorlevel 1 exit /b 1
java -cp .core-build lt.pcbuilder.domain.CoreSelfTest
