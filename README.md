# 🌱 식물검역 샘플링 알고리즘 성능 비교 분석

식물검역 업무에서 사용할 랜덤 샘플링 알고리즘의 신뢰성을 정량적으로 비교 분석하여 최적의 알고리즘을 제안하는 프로젝트입니다.

## 📋 프로젝트 개요

식물검역 업무에서 **공정하고 신뢰할 수 있는 샘플링**은 국가 생물 보안의 핵심 요소입니다. 기존의 단순한 랜덤 함수들은 의사난수 생성 특성으로 인해 예측 가능한 패턴이나 편향된 결과를 생성할 가능성이 있어, 본 프로젝트에서는 **3가지 주요 샘플링 알고리즘의 성능을 정량적으로 비교**하여 최적의 솔루션을 제안합니다.

---

## 🔍 분석 대상 알고리즘

### 1️⃣ 일반 랜덤 함수 (Standard Random)
**기술 구현**: `java.util.Random` 클래스 사용
- **알고리즘**: 선형 합동 생성기 (Linear Congruential Generator)
- **장점**: 
  - 빠른 실행 속도
  - 간단한 구현
  - JVM 최적화 혜택
- **단점**: 
  - 의사난수로 인한 편향 가능성
  - 예측 가능한 시드 기반 패턴
  - 암호학적으로 안전하지 않음

### 2️⃣ 피셔-예이츠 셔플 (Fisher-Yates Shuffle)
**기술 구현**: Donald Knuth의 현대적 구현 방식
- **알고리즘**: 수학적으로 증명된 무편향 셔플링
- **장점**: 
  - **완전한 무편향성 보장**
  - 모든 순열이 동일한 확률로 생성
  - 통계적으로 검증된 공정성
- **단점**: 
  - 추가 메모리 사용 (O(n))
  - 단순 난수보다 약간 느린 실행 속도

### 3️⃣ 난수 생성 + 보정 (Corrected Random)
**기술 구현**: XOR 연산과 모듈러 연산을 통한 보정 시도
- **알고리즘**: 기본 난수에 수학적 변환 적용
- **장점**: 
  - 기본 난수의 편향 패턴 변경 시도
  - 추가적인 무작위성 도입
- **단점**: 
  - 수학적으로 증명되지 않은 보정 방법
  - 실제 편향 감소 효과 불분명
  - 새로운 편향 패턴 도입 가능성

---

## 📊 분석 방법론

### 통계적 신뢰성 확보
- **5회 반복 실행**을 통한 결과의 일관성 검증
- 각 테스트마다 1,000회의 샘플 생성
- 총 15,000개의 데이터 포인트로 신뢰성 확보

### 핵심 평가 지표

#### 1. 평균 균등성 (Average Uniformity)
- **측정 방법**: 각 그룹별 선택 빈도의 표준편차 계산
- **의미**: 낮을수록 모든 그룹이 균등하게 선택됨을 의미
- **기준값**: 이론적 완전 균등 시 0에 수렴

#### 2. 결과 안정성 (Result Stability)
- **측정 방법**: 5회 반복 실행 결과의 표준편차
- **의미**: 낮을수록 알고리즘의 성능이 일관됨을 의미
- **중요성**: 실제 업무 환경에서의 예측 가능성

#### 3. 카이제곱 검정 (Chi-Square Test)
- **검정 조건**: α=0.05, 자유도=32
- **귀무가설**: 모든 그룹의 선택 확률이 동일
- **판정 기준**: 통계량 ≤ 50.892 시 균등성 인정

---

## 🏆 주요 분석 결과

| 알고리즘 | 평균 균등성 | 결과 안정성 | 종합 점수 | 순위 |
|---------|-------------|-------------|-----------|------|
| **피셔-예이츠 셔플** | **1.6512** | **0.1715** | **1.8227** | **🥇 1위** |
| 일반 랜덤 함수 | 1.7134 | 0.1882 | 1.9016 | 🥈 2위 |
| 난수 생성 + 보정 | 2.0636 | 2.1400 | 4.2036 | 🥉 3위 |

### 📈 성능 차이 분석
- **피셔-예이츠**: 일반 랜덤 대비 **4.7% 더 균등**, **9.7% 더 안정적**
- **보정 알고리즘**: 예상과 달리 성능이 가장 낮음, 추가 보정이 오히려 편향 증가

---

## 💡 결론 및 제안

### 🎯 결론: **피셔-예이츠 셔플** 권장

수학적으로 증명된 완전한 무편향성을 보장하며, 평균 균등성과 결과 안정성 모든 면에서 최우수 성능을 보여 식물검역 업무에 최적입니다.

---

## 🚀 프로그램 실행 방법

### 환경 요구사항
- **Java**: JDK 11 이상 (자동 설치 지원)
- **Maven**: Maven Wrapper 포함 (별도 설치 불필요)
- **메모리**: 최소 512MB 힙 공간

### 실행 방법 (권장 순서)

#### 1️⃣ PowerShell 실행 (가장 권장)
```powershell
.\RUN_ANALYSIS.ps1
```
- Java 경로 자동 탐지 및 설정
- Maven Wrapper 자동 다운로드
- 모든 환경 문제 자동 해결

#### 2️⃣ 영어 배치 파일 실행
```cmd
.\run_analysis_EN.bat
```
- 한글 깨짐 없는 영어 인터페이스
- Java 자동 설치 옵션 제공

#### 3️⃣ 기본 배치 파일 실행
```cmd
.\run_analysis.bat
```
- 한국어 인터페이스 (일부 환경에서 깨짐 가능)

#### 4️⃣ Maven 직접 실행
```bash
# Maven이 설치된 경우
mvn exec:java -Dexec.mainClass="AlgorithmComparisonMain"

# Maven Wrapper 사용
.\mvnw.cmd exec:java -Dexec.mainClass="AlgorithmComparisonMain"
```

### 실행 옵션
- **1번**: 표준 분석 (5회 반복, 빠른 결과)
- **2번**: 상세 분석 (5회 반복, 상세 리포트) 
- **3번**: 맞춤형 분석 (사용자 설정 반복)

### 결과 파일
- `results/` - 텍스트 분석 결과
- `results/excel-outputs/` - Excel 분석 결과

---

## 📁 프로젝트 구조

```
plant-quarantine-sampling-analysis/
├── README.md                      # 프로젝트 설명서
├── QUICK_GUIDE.md                 # 영어 실행 가이드
├── pom.xml                        # Maven 설정 파일
│
├── RUN_ANALYSIS.ps1               # PowerShell 실행기 (권장)
├── run_analysis_EN.bat            # 영어 배치 실행기
├── run_analysis.bat               # 한국어 배치 실행기
│
├── mvnw.cmd / mvnw                # Maven Wrapper
├── .mvn/wrapper/                  # Maven Wrapper 설정
│
├── src/main/java/
│   ├── AlgorithmComparisonMain.java
│   ├── algorithms/
│   │   ├── SamplingAlgorithm.java
│   │   ├── StandardRandomSampling.java
│   │   ├── FisherYatesSampling.java
│   │   └── CorrectedRandomSampling.java
│   └── analysis/
│       ├── StatisticalAnalyzer.java
│       ├── ComparisonRunner.java
│       └── ExcelExporter.java
│
├── SIMULATION_RESULTS.md          # 시뮬레이션 결과 문서
├── VERIFICATION.md                # 검증 결과 문서
└── results/                       # 실행 결과 디렉토리 (자동 생성)
    ├── *.txt                      # 텍스트 리포트
    └── excel-outputs/             # Excel 리포트
        └── *.xlsx
```

## 🔧 기술적 특징

- **객체지향 설계**: 인터페이스 기반 확장 가능한 구조
- **통계 분석**: 5회 반복 실행으로 신뢰성 확보
- **다중 출력**: 텍스트 + Excel 형식 지원
- **한국어 UI**: 직관적인 메뉴 시스템

---

*Plant Quarantine Sampling Algorithm Performance Analysis v1.0.0*