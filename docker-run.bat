@echo off
REM Quick Docker run script for Windows PowerShell/CMD

set CONFIG_FILE=%~1
if "%CONFIG_FILE%"=="" set CONFIG_FILE=config.xml

if not exist "%CONFIG_FILE%" (
    echo Error: Config file '%CONFIG_FILE%' not found
    exit /b 1
)

docker run --rm -v "%CD%":/workspace -w /workspace maven:3.9-eclipse-temurin-17 java -jar target/csv-to-excel-converter-1.0.0.jar "%CONFIG_FILE%"

