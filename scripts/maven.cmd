@echo off
setlocal EnableDelayedExpansion
set "MAVEN_CMD="
for %%X in (mvn.cmd) do if not "%%~$PATH:X"=="" set "MAVEN_CMD=%%~$PATH:X"
if not defined MAVEN_CMD (
  for /d %%I in ("%ProgramFiles%\JetBrains\IntelliJ IDEA*") do (
    if exist "%%~fI\plugins\maven\lib\maven3\bin\mvn.cmd" set "MAVEN_CMD=%%~fI\plugins\maven\lib\maven3\bin\mvn.cmd"
  )
)
if not defined MAVEN_CMD (
  echo Maven nerastas PATH arba IntelliJ IDEA instaliacijos aplanke.
  echo Atidaryk backend\pom.xml per IntelliJ IDEA ir paleisk Spring Boot per Run.
  echo Arba idiek Apache Maven 3.9+ ir pridek ji i PATH.
  exit /b 1
)
cd /d "%~dp0..\backend"
call "%MAVEN_CMD%" %*
exit /b %ERRORLEVEL%
