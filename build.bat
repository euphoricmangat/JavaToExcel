@echo off
REM Local build script using Maven and Java 17
REM Requires: Maven 3.6+ and Java 17 (JAVA_HOME17 set)

if "%JAVA_HOME17%"=="" (
    echo Error: JAVA_HOME17 environment variable is not set
    echo Please set JAVA_HOME17 to your Java 17 installation path
    exit /b 1
)

if not exist "%JAVA_HOME17%\bin\java.exe" (
    echo Error: Java 17 not found at JAVA_HOME17=%JAVA_HOME17%
    exit /b 1
)

echo Using Java 17 from: %JAVA_HOME17%
echo Using Maven: 
mvn --version

set JAVA_HOME=%JAVA_HOME17%
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo Building project...
call mvn clean package

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build complete! JAR file is in target/ directory
) else (
    echo.
    echo Build failed!
    exit /b %ERRORLEVEL%
)

