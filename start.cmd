@echo off
cd /d "%~dp0"
echo Paleidziamas PCBuilder... Atidaryk http://localhost:8080
call scripts\maven.cmd spring-boot:run
