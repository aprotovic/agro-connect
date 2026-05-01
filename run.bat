@echo off
echo ======================================
echo    Starting Agro-Connect Server
echo ======================================
echo.

if not exist build (
    echo Build directory not found! Please run build.bat first.
    pause
    exit /b 1
)

echo Starting server on http://localhost:8080
echo Press Ctrl+C to stop the server
echo.

java -cp "build;lib/*" AgroConnectServer

pause
