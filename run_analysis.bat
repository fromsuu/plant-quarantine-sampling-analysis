@echo off
setlocal enabledelayedexpansion

echo 🌱 식물검역 샘플링 알고리즘 분석 프로그램 시작 🌱
echo ═══════════════════════════════════════════════════════
echo.

REM 현재 디렉토리가 프로젝트 루트인지 확인
if not exist "pom.xml" (
    echo ❌ 오류: pom.xml 파일이 없습니다. 프로젝트 루트 디렉토리에서 실행해주세요.
    pause
    exit /b 1
)

REM Java 설치 확인 (더 친화적인 방식)
echo 🔍 Java 환경을 확인합니다...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java가 설치되지 않았거나 PATH에 없습니다.
    echo.
    echo 🚀 자동 설치를 시도하시겠습니까?
    echo.
    echo 1️⃣  자동 설치 시도 (setup_java.bat 실행)
    echo 2️⃣  수동 설치 안내
    echo 3️⃣  프로그램 종료
    echo.
    set /p choice="선택하세요 (1-3): "
    
    if "!choice!"=="1" (
        echo.
        echo 🔧 Java 자동 설치를 시작합니다...
        call setup_java.bat
        if errorlevel 1 (
            echo ❌ 자동 설치에 실패했습니다.
            goto MANUAL_INSTALL_GUIDE
        )
        echo ✅ Java 설치가 완료되었습니다. 다시 시작합니다...
        echo.
        goto RESTART_CHECK
    ) else if "!choice!"=="2" (
        goto MANUAL_INSTALL_GUIDE
    ) else (
        echo 👋 프로그램을 종료합니다.
        pause
        exit /b 1
    )
) else (
    echo ✅ Java가 설치되어 있습니다.
    java -version | head -1
)

:RESTART_CHECK
REM Java 재확인
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java 설치 후에도 인식되지 않습니다.
    echo 🔄 시스템을 재시작하거나 새 명령 프롬프트를 여세요.
    pause
    exit /b 1
)

echo.

REM Maven 확인 - 없으면 Maven Wrapper 사용
echo 🔍 빌드 도구를 확인합니다...
mvn --version >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Maven이 설치되지 않았습니다. Maven Wrapper를 사용합니다.
    set MAVEN_CMD=mvnw.cmd
    
    REM Maven Wrapper 파일이 있는지 확인
    if not exist "mvnw.cmd" (
        echo ❌ Maven Wrapper 파일이 없습니다.
        echo 🔧 Maven Wrapper를 다운로드합니다...
        
        REM Maven Wrapper jar 다운로드
        if not exist ".mvn\wrapper" mkdir .mvn\wrapper
        
        echo 📦 Maven Wrapper 다운로드 중...
        powershell -Command "try { Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '.mvn\wrapper\maven-wrapper.jar' -ErrorAction Stop; Write-Host '✅ Maven Wrapper 다운로드 완료' } catch { Write-Host '❌ 다운로드 실패:' $_.Exception.Message; exit 1 }"
        
        if errorlevel 1 (
            echo ❌ Maven Wrapper 다운로드에 실패했습니다.
            echo 📋 인터넷 연결을 확인하고 다시 시도해주세요.
            pause
            exit /b 1
        )
    )
    
    echo ✅ Maven Wrapper를 사용합니다.
) else (
    echo ✅ Maven이 설치되어 있습니다.
    set MAVEN_CMD=mvn
    mvn --version | head -1
)

echo.

REM 의존성 확인 및 컴파일
echo 🔄 프로젝트를 컴파일합니다...
echo    (처음 실행 시 의존성 다운로드로 시간이 걸릴 수 있습니다)
echo.

%MAVEN_CMD% clean compile -q
if errorlevel 1 (
    echo ❌ 컴파일에 실패했습니다.
    echo.
    echo 🔍 상세 오류 정보를 확인합니다...
    %MAVEN_CMD% clean compile
    echo.
    echo 📋 문제 해결 방법:
    echo    1. 인터넷 연결 확인 (의존성 다운로드 필요)
    echo    2. 프록시 설정 확인
    echo    3. Java 버전 확인 (JDK 11 이상 필요)
    echo.
    pause
    exit /b 1
)

echo ✅ 컴파일 완료!
echo.

REM 결과 디렉토리 생성
echo 📁 결과 디렉토리를 준비합니다...
if not exist "results" mkdir results
if not exist "results\excel-outputs" mkdir "results\excel-outputs"
echo ✅ 디렉토리 준비 완료!
echo.

REM 프로그램 실행
echo 🚀 프로그램을 시작합니다...
echo ═══════════════════════════════════════════════════════
echo.
%MAVEN_CMD% exec:java -Dexec.mainClass="AlgorithmComparisonMain" -q

REM 실행 결과 확인
if errorlevel 1 (
    echo.
    echo ❌ 프로그램 실행 중 오류가 발생했습니다.
    pause
    exit /b 1
)

REM 실행 완료 메시지
echo.
echo ✨ 분석이 완료되었습니다! ✨
echo ═══════════════════════════════════════════════════════
echo.
echo 📄 생성된 파일:
if exist "results\*.txt" (
    echo    📊 텍스트 리포트: results\ 디렉토리
)
if exist "results\excel-outputs\*.xlsx" (
    echo    📊 Excel 리포트: results\excel-outputs\ 디렉토리
)
echo.
echo 🔍 결과 파일을 열어서 분석 결과를 확인하세요!
echo.

REM 결과 디렉토리 열기 옵션
set /p OPEN_FOLDER="결과 폴더를 열겠습니까? (Y/n): "
if /i "!OPEN_FOLDER!" neq "n" if /i "!OPEN_FOLDER!" neq "no" (
    start explorer "results"
)

pause
goto END

:MANUAL_INSTALL_GUIDE
echo.
echo 📋 Java 수동 설치 가이드
echo ═══════════════════════════════════════════════════════
echo.
echo 🔗 OpenJDK 17 다운로드:
echo    https://adoptium.net/temurin/releases/
echo.
echo 📝 설치 후 해야 할 작업:
echo    1. JAVA_HOME 환경변수 설정 (예: C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot)
echo    2. PATH에 %%JAVA_HOME%%\bin 추가
echo    3. 명령 프롬프트를 재시작
echo.
echo 💡 환경변수 설정 방법:
echo    1. 제어판 → 시스템 → 고급 시스템 설정
echo    2. 환경 변수 클릭
echo    3. 시스템 변수에서 새로 만들기
echo       - 변수 이름: JAVA_HOME
echo       - 변수 값: Java 설치 경로
echo    4. Path 변수 편집하여 %%JAVA_HOME%%\bin 추가
echo.
pause

:END