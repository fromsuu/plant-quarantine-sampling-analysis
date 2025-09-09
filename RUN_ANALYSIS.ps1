# Plant Quarantine Sampling Algorithm Analysis Tool - PowerShell Version
# This script handles Java path detection issues in PowerShell

Write-Host ""
Write-Host "========================================================"
Write-Host "  Plant Quarantine Sampling Algorithm Analysis Tool   "
Write-Host "========================================================"
Write-Host ""

# Function to find Java installation
function Find-JavaInstallation {
    Write-Host "[INFO] Searching for Java installation..."
    
    # Method 1: Try direct java command
    try {
        $javaVersion = java -version 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[SUCCESS] Java found via PATH"
            Write-Host $javaVersion[0]
            return "java"
        }
    }
    catch {
        Write-Host "[INFO] Java not found in PATH, searching manually..."
    }
    
    # Method 2: Check JAVA_HOME
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
        Write-Host "[SUCCESS] Java found via JAVA_HOME: $env:JAVA_HOME"
        $javaPath = "$env:JAVA_HOME\bin\java.exe"
        $version = & $javaPath -version 2>&1
        Write-Host $version[0]
        return $javaPath
    }
    
    # Method 3: Search common installation paths
    $commonPaths = @(
        "C:\jdk-*\bin\java.exe",
        "C:\Program Files\Java\jdk*\bin\java.exe",
        "C:\Program Files\OpenJDK\jdk*\bin\java.exe", 
        "C:\Program Files\Eclipse Adoptium\jdk*\bin\java.exe",
        "C:\Program Files (x86)\Java\jdk*\bin\java.exe"
    )
    
    foreach ($pathPattern in $commonPaths) {
        $javaExes = Get-ChildItem -Path $pathPattern -ErrorAction SilentlyContinue
        if ($javaExes) {
            $javaPath = $javaExes[0].FullName
            Write-Host "[SUCCESS] Found Java at: $javaPath"
            
            try {
                $version = & $javaPath -version 2>&1
                Write-Host $version[0]
                return $javaPath
            }
            catch {
                Write-Host "[WARNING] Java executable found but failed to run: $javaPath"
            }
        }
    }
    
    return $null
}

# Function to find Maven or Maven Wrapper
function Find-MavenTool {
    Write-Host "[INFO] Checking for build tools..."
    
    # Try Maven first
    try {
        $mvnVersion = mvn --version 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[SUCCESS] Maven is installed"
            Write-Host ($mvnVersion | Select-String "Apache Maven")[0]
            return "mvn"
        }
    }
    catch {
        Write-Host "[INFO] Maven not found, checking Maven Wrapper..."
    }
    
    # Check for Maven Wrapper
    if (Test-Path "mvnw.cmd") {
        Write-Host "[SUCCESS] Maven Wrapper found"
        return ".\mvnw.cmd"
    }
    
    Write-Host "[WARNING] Neither Maven nor Maven Wrapper found"
    return $null
}

# Check if we're in the project root
if (!(Test-Path "pom.xml")) {
    Write-Host "[ERROR] pom.xml not found. Please run from project root directory."
    Read-Host "Press Enter to exit"
    exit 1
}

# Find Java
$javaCommand = Find-JavaInstallation
if (!$javaCommand) {
    Write-Host "[ERROR] Java not found!"
    Write-Host ""
    Write-Host "[GUIDE] Please install Java:"
    Write-Host "  1. Download from: https://adoptium.net/temurin/releases/"
    Write-Host "  2. Install OpenJDK 17 or later"
    Write-Host "  3. Set JAVA_HOME environment variable"
    Write-Host "  4. Add JAVA_HOME\bin to PATH"
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

# Find Maven tool
$mavenCommand = Find-MavenTool
if (!$mavenCommand) {
    Write-Host "[INFO] Downloading Maven Wrapper..."
    
    # Create wrapper directory
    New-Item -ItemType Directory -Path ".mvn\wrapper" -Force | Out-Null
    
    try {
        # Download Maven Wrapper jar
        $wrapperUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
        Invoke-WebRequest -Uri $wrapperUrl -OutFile ".mvn\wrapper\maven-wrapper.jar"
        
        Write-Host "[SUCCESS] Maven Wrapper downloaded"
        $mavenCommand = ".\mvnw.cmd"
    }
    catch {
        Write-Host "[ERROR] Failed to download Maven Wrapper: $($_.Exception.Message)"
        Read-Host "Press Enter to exit"
        exit 1
    }
}

Write-Host ""

# Set up environment for Java if using specific path
if ($javaCommand -ne "java") {
    $javaDir = Split-Path $javaCommand
    $env:PATH = "$javaDir;$env:PATH"
    $javaHome = Split-Path $javaDir
    $env:JAVA_HOME = $javaHome
    Write-Host "[INFO] Set JAVA_HOME to: $javaHome"
    Write-Host "[INFO] Added to PATH: $javaDir"
}

# Create results directories
Write-Host "[INFO] Preparing result directories..."
New-Item -ItemType Directory -Path "results" -Force | Out-Null
New-Item -ItemType Directory -Path "results\excel-outputs" -Force | Out-Null
Write-Host "[SUCCESS] Directories ready!"
Write-Host ""

# Compile project
Write-Host "[INFO] Compiling project..."
Write-Host "       (First run may take time for dependency downloads)"
Write-Host ""

try {
    if ($mavenCommand -eq "mvn") {
        & mvn clean compile -q
    } else {
        & cmd /c $mavenCommand clean compile -q
    }
    
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed"
    }
    
    Write-Host "[SUCCESS] Compilation completed!"
}
catch {
    Write-Host "[ERROR] Compilation failed!"
    Write-Host ""
    Write-Host "[INFO] Trying with verbose output for debugging..."
    
    if ($mavenCommand -eq "mvn") {
        & mvn clean compile
    } else {
        & cmd /c $mavenCommand clean compile
    }
    
    Write-Host ""
    Write-Host "[GUIDE] Troubleshooting:"
    Write-Host "  1. Check internet connection (for dependency downloads)"
    Write-Host "  2. Check proxy settings"
    Write-Host "  3. Verify Java version (JDK 11+ required)"
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""

# Run the analysis program
Write-Host "[INFO] Starting the analysis program..."
Write-Host "========================================================"
Write-Host ""

try {
    if ($mavenCommand -eq "mvn") {
        & mvn exec:java -Dexec.mainClass="AlgorithmComparisonMain" -q
    } else {
        & cmd /c $mavenCommand exec:java -Dexec.mainClass="AlgorithmComparisonMain" -q
    }
    
    if ($LASTEXITCODE -ne 0) {
        throw "Program execution failed"
    }
}
catch {
    Write-Host ""
    Write-Host "[ERROR] Program execution failed!"
    Read-Host "Press Enter to exit"
    exit 1
}

# Completion message
Write-Host ""
Write-Host "========================================================"
Write-Host "[SUCCESS] Analysis completed successfully!"
Write-Host "========================================================"
Write-Host ""
Write-Host "[INFO] Generated Files:"

if (Test-Path "results\*.txt") {
    Write-Host "  📄 Text Reports: results\ directory"
}

if (Test-Path "results\excel-outputs\*.xlsx") {
    Write-Host "  📊 Excel Reports: results\excel-outputs\ directory"
}

Write-Host ""
Write-Host "[INFO] Please check the result files for analysis results!"
Write-Host ""

# Option to open results folder
$openFolder = Read-Host "Open results folder? (Y/n)"
if ($openFolder -ne "n" -and $openFolder -ne "no") {
    Start-Process explorer "results"
}

Write-Host ""
Write-Host "[INFO] Analysis completed. You can run this script again anytime."
Read-Host "Press Enter to exit"