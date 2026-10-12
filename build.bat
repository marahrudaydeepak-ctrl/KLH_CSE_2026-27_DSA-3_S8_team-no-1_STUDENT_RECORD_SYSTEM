@echo off
echo ====================================================================
echo  BUILDING INTELLIGENT STUDENT RECORD MANAGEMENT SYSTEM (JAVA DSA)
echo ====================================================================
if not exist "bin" mkdir bin
if not exist "data" mkdir data
javac -encoding UTF-8 -d bin src\model\*.java src\datastructures\*.java src\algorithms\searching\*.java src\algorithms\sorting\*.java src\algorithms\string\*.java src\algorithms\dynamicprogramming\*.java src\algorithms\graph\*.java src\algorithms\networkflow\*.java src\algorithms\npcomplete\*.java src\algorithms\approximation\*.java src\algorithms\parameterized\*.java src\algorithms\randomized\*.java src\algorithms\parallel\*.java src\file\*.java src\services\*.java src\ui\*.java src\test\*.java src\Main.java
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Build succeeded with 0 errors! Classes compiled to bin\
) else (
    echo [ERROR] Build failed. Please verify Java JDK is installed.
)
pause
