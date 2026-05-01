@echo off
echo ======================================
echo    Agro-Connect Build Script (Windows)
echo ======================================

if not exist build mkdir build

echo Compiling Java sources...
dir /s /b src\*.java > sources.txt
javac -d build -cp "lib/*" @sources.txt

if %errorlevel% neq 0 (
    echo Compilation failed!
    del sources.txt
    pause
    exit /b %errorlevel%
)

del sources.txt
echo Compilation successful.
echo.
echo ======================================
echo    Build Complete!
echo ======================================
pause
