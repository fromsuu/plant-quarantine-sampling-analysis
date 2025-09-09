@echo off
setlocal enabledelayedexpansion

echo ========================================================
echo   Plant Quarantine Sampling Algorithm Analysis Tool   
echo ========================================================
echo.

REM Check if in project root directory
if not exist "pom.xml" (
    echo [ERROR] pom.xml not found. Please run from project root directory.
    pause
    exit /b 1
)

REM Check Java installation with multiple detection methods
echo [INFO] Checking Java environment...

REM Try direct java command first
java -version >nul 2>&1
if not errorlevel 1 (
    echo [SUCCESS] Java is installed and accessible.
    java -version 2>&1 | findstr "version"
    goto CHECK_MAVEN
)

REM Check JAVA_HOME if direct command failed
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        echo [SUCCESS] Java found via JAVA_HOME: %JAVA_HOME%
        "%JAVA_HOME%\bin\java.exe" -version 2>&1 | findstr "version"
        REM Add JAVA_HOME to PATH for this session
        set PATH=%JAVA_HOME%\bin;%PATH%
        goto CHECK_MAVEN
    )
)

REM Search common installation paths
echo [INFO] Searching for Java in common installation paths...
set JAVA_FOUND=0

for /d %%i in ("C:\Program Files\Java\jdk*") do (
    if exist "%%i\bin\java.exe" (
        echo [SUCCESS] Found Java at: %%i
        "%%i\bin\java.exe" -version 2>&1 | findstr "version"
        set PATH=%%i\bin;%PATH%
        set JAVA_FOUND=1
        goto CHECK_MAVEN
    )
)

for /d %%i in ("C:\Program Files\OpenJDK\jdk*") do (
    if exist "%%i\bin\java.exe" (
        echo [SUCCESS] Found OpenJDK at: %%i
        "%%i\bin\java.exe" -version 2>&1 | findstr "version"
        set PATH=%%i\bin;%PATH%
        set JAVA_FOUND=1
        goto CHECK_MAVEN
    )
)

for /d %%i in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
    if exist "%%i\bin\java.exe" (
        echo [SUCCESS] Found Eclipse Adoptium JDK at: %%i
        "%%i\bin\java.exe" -version 2>&1 | findstr "version"
        set PATH=%%i\bin;%PATH%
        set JAVA_FOUND=1
        goto CHECK_MAVEN
    )
)

REM If still not found, show installation options
if %JAVA_FOUND% equ 0 (
    echo [ERROR] Java is not installed or not in PATH.
    echo.
    echo [PROMPT] Try automatic installation?
    echo.
    echo    1. Try Auto Install (run setup_java.bat)
    echo    2. Manual Installation Guide
    echo    3. Exit Program
    echo.
    set /p choice="Select option (1-3): "
    
    if "!choice!"=="1" (
        echo.
        echo [INFO] Starting Java auto-installation...
        call setup_java.bat
        if errorlevel 1 (
            echo [ERROR] Auto-installation failed.
            goto MANUAL_INSTALL_GUIDE
        )
        echo [SUCCESS] Java installation completed. Restarting...
        echo.
        goto RESTART_CHECK
    ) else if "!choice!"=="2" (
        goto MANUAL_INSTALL_GUIDE
    ) else (
        echo [INFO] Exiting program.
        pause
        exit /b 1
    )
)

:CHECK_MAVEN

:RESTART_CHECK
REM Re-check Java after installation
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java still not recognized after installation.
    echo [INFO] Please restart system or open new command prompt.
    pause
    exit /b 1
)

echo.

REM Check Maven - use Maven Wrapper if not available
echo [INFO] Checking build tools...
mvn --version >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Maven not installed. Using Maven Wrapper.
    set MAVEN_CMD=mvnw.cmd
    
    REM Check if Maven Wrapper files exist
    if not exist "mvnw.cmd" (
        echo [ERROR] Maven Wrapper files not found.
        echo [INFO] Downloading Maven Wrapper...
        
        REM Download Maven Wrapper jar
        if not exist ".mvn\wrapper" mkdir .mvn\wrapper
        
        echo [INFO] Downloading Maven Wrapper...
        powershell -Command "try { Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '.mvn\wrapper\maven-wrapper.jar' -ErrorAction Stop; Write-Host '[SUCCESS] Maven Wrapper downloaded' } catch { Write-Host '[ERROR] Download failed:' $_.Exception.Message; exit 1 }"
        
        if errorlevel 1 (
            echo [ERROR] Maven Wrapper download failed.
            echo [INFO] Please check internet connection and try again.
            pause
            exit /b 1
        )
    )
    
    echo [SUCCESS] Using Maven Wrapper.
) else (
    echo [SUCCESS] Maven is installed.
    set MAVEN_CMD=mvn
    mvn --version 2>&1 | findstr "Apache Maven"
)

echo.

REM Check dependencies and compile
echo [INFO] Compiling project...
echo       (First run may take time for dependency downloads)
echo.

%MAVEN_CMD% clean compile -q
if errorlevel 1 (
    echo [ERROR] Compilation failed.
    echo.
    echo [INFO] Checking detailed error information...
    %MAVEN_CMD% clean compile
    echo.
    echo [GUIDE] Troubleshooting:
    echo    1. Check internet connection (for dependency downloads)
    echo    2. Check proxy settings
    echo    3. Verify Java version (JDK 11+ required)
    echo.
    pause
    exit /b 1
)

echo [SUCCESS] Compilation completed!
echo.

REM Create result directories
echo [INFO] Preparing result directories...
if not exist "results" mkdir results
if not exist "results\excel-outputs" mkdir "results\excel-outputs"
echo [SUCCESS] Directories ready!
echo.

REM Run the program
echo [INFO] Starting the analysis program...
echo ========================================================
echo.
%MAVEN_CMD% exec:java -Dexec.mainClass="AlgorithmComparisonMain" -q

REM Check execution result
if errorlevel 1 (
    echo.
    echo [ERROR] Program execution failed.
    pause
    exit /b 1
)

REM Completion message
echo.
echo ========================================================
echo [SUCCESS] Analysis completed successfully!
echo ========================================================
echo.
echo [INFO] Generated Files:
if exist "results\*.txt" (
    echo    Text Reports: results\ directory
)
if exist "results\excel-outputs\*.xlsx" (
    echo    Excel Reports: results\excel-outputs\ directory
)
echo.
echo [INFO] Please check the result files for analysis results!
echo.

REM Option to open results folder
set /p OPEN_FOLDER="Open results folder? (Y/n): "
if /i "!OPEN_FOLDER!" neq "n" if /i "!OPEN_FOLDER!" neq "no" (
    start explorer "results"
)

pause
goto END

:MANUAL_INSTALL_GUIDE
echo.
echo [GUIDE] Manual Java Installation
echo ========================================
echo.
echo [ERROR] Auto-installation failed. Please install manually.
echo.
echo Download Link:
echo    https://adoptium.net/temurin/releases/
echo.
echo Steps After Installation:
echo    1. Set JAVA_HOME environment variable to JDK installation path
echo    2. Add %%JAVA_HOME%%\bin to PATH
echo.
echo Detailed Installation Guide:
echo    https://docs.oracle.com/en/java/javase/17/install/
echo.
pause
exit /b 1

:END