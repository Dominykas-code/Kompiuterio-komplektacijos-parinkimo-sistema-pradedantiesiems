@echo off
cd /d "%~dp0"
call scripts\maven.cmd test
