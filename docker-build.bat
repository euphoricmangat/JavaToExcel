@echo off
REM Quick Docker build script for Windows PowerShell/CMD

docker run --rm -v "%CD%":/workspace -w /workspace maven:3.9-eclipse-temurin-17 mvn clean package

if %ERRORLEVEL% EQU 0 (
    echo Build complete! JAR file is in target/ directory
) else (
    echo Build failed!
    exit /b %ERRORLEVEL%
)

