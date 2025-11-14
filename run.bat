@echo off
REM Local run script using Java 17
REM Requires: Java 17 (JAVA_HOME17 set) and built JAR file

set CONFIG_FILE=%~1
if "%CONFIG_FILE%"=="" set CONFIG_FILE=config.xml

if not exist "%CONFIG_FILE%" (
    echo Error: Config file '%CONFIG_FILE%' not found
    exit /b 1
)

if not exist "target\csv-to-excel-converter-1.0.0.jar" (
    echo Error: JAR file not found. Please run build.bat first
    exit /b 1
)

if "%JAVA_HOME17%"=="" (
    echo Error: JAVA_HOME17 environment variable is not set
    echo Please set JAVA_HOME17 to your Java 17 installation path
    exit /b 1
)

if not exist "%JAVA_HOME17%\bin\java.exe" (
    echo Error: Java 17 not found at JAVA_HOME17=%JAVA_HOME17%
    exit /b 1
)

set JAVA_HOME=%JAVA_HOME17%
set PATH=%JAVA_HOME%\bin;%PATH%

echo Using Java 17 from: %JAVA_HOME17%
echo Running with config: %CONFIG_FILE%
echo.

"%JAVA_HOME%\bin\java.exe" -jar target\csv-to-excel-converter-1.0.0.jar "%CONFIG_FILE%"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Execution complete!
) else (
    echo.
    echo Execution failed!
    exit /b %ERRORLEVEL%
)

