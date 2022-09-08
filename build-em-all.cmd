@echo off

for /d %%i in (*) do (
cd "%%i"
mvn package -DskipTests
cd ..
)
pause
