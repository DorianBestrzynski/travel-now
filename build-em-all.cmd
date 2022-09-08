@echo off

set back=%cd%
for /d %%i in (*) do (
cd "%%i"
mvn package -DskipTests
)
cd %back%
pause
