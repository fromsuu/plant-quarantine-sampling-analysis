# 🌱 Plant Quarantine Sampling Algorithm Analysis

A comprehensive statistical analysis tool for comparing random sampling algorithms used in plant quarantine operations, ensuring fair and reliable sampling procedures for national biosecurity.

## 🎯 Overview

This project evaluates **3 different sampling algorithms** to determine the most reliable method for plant quarantine sampling. Through rigorous statistical analysis, it identifies the optimal algorithm that ensures fairness and consistency in quarantine procedures.

## 🚀 Quick Start

### Prerequisites
- **Java 11+** (auto-detection included)
- **Internet connection** (for Maven dependencies)

### Run the Analysis

#### Option 1: PowerShell (Recommended)
```powershell
.\RUN_ANALYSIS.ps1
```

#### Option 2: Command Prompt
```cmd
.\run_analysis_EN.bat
```

#### Option 3: Direct Maven
```bash
.\mvnw.cmd exec:java -Dexec.mainClass="AlgorithmComparisonMain"
```

## 📊 Analysis Results

The tool generates comprehensive reports in both **text** and **Excel** formats:

- **Text Reports**: `results/*.txt`
- **Excel Reports**: `results/excel-outputs/*.xlsx`

### Key Metrics
- **Uniformity Score**: Measures how evenly samples are distributed
- **Stability Score**: Measures consistency across multiple runs  
- **Statistical Tests**: Chi-square goodness-of-fit test (α=0.05)

## 🔬 Algorithms Tested

1. **Standard Random Sampling** - `java.util.Random`
2. **Fisher-Yates Shuffle** - Mathematically proven unbiased shuffling
3. **Enhanced Random Sampling** - Custom algorithm with bias correction

## 📈 Sample Output

```
Analysis Results Summary
════════════════════════════════════════════════════════
Rank    Algorithm                  Uniformity    Stability    Grade
1       Fisher-Yates Shuffle       1.6512        0.1715       Excellent
2       Standard Random            1.7134        0.1882       Good  
3       Enhanced Random            2.0636        2.1400       Average
════════════════════════════════════════════════════════
Recommendation: Fisher-Yates Shuffle
```

## 🏗️ Project Structure

```
plant-quarantine-sampling-analysis/
├── RUN_ANALYSIS.ps1              # PowerShell runner (recommended)
├── run_analysis_EN.bat           # English batch runner
├── run_analysis.bat              # Korean batch runner
├── mvnw.cmd / mvnw               # Maven wrapper
├── src/main/java/
│   ├── AlgorithmComparisonMain.java
│   ├── algorithms/
│   │   ├── StandardRandomSampling.java
│   │   ├── FisherYatesSampling.java
│   │   └── CorrectedRandomSampling.java
│   └── analysis/
│       ├── StatisticalAnalyzer.java
│       ├── ComparisonRunner.java
│       └── ExcelExporter.java
└── results/                      # Generated reports (auto-created)
```

## 🛠️ Features

- **Multiple Algorithm Comparison**: Statistical comparison of 3 sampling methods
- **Comprehensive Analysis**: 5 iterations with 1,000 samples each for reliability
- **Dual Output Formats**: Both text and Excel reports with detailed statistics  
- **Cross-Platform**: Windows PowerShell and Command Prompt support
- **Self-Contained**: Includes Maven Wrapper, no external dependencies
- **Auto-Setup**: Automatic Java detection and environment configuration

## 📋 Analysis Modes

1. **Standard Analysis** (5 iterations, quick results)
2. **Detailed Analysis** (5 iterations, comprehensive reports)
3. **Custom Analysis** (user-defined iterations: 3-20)

## 🔧 Technical Details

- **Language**: Java 11+
- **Build Tool**: Maven 3.9.6 (via wrapper)
- **Dependencies**: Apache POI (Excel generation), JUnit (testing)
- **Statistical Methods**: Chi-square test, standard deviation analysis
- **Output Formats**: UTF-8 text files, Excel (.xlsx) with multiple sheets

## 📞 Troubleshooting

### Common Issues

**Java not found**:
- Use `.\RUN_ANALYSIS.ps1` for automatic Java detection
- Or download from: https://adoptium.net/temurin/releases/

**Compilation failed**:
- Check internet connection (downloads Maven dependencies)
- Verify Java version: `java -version`

**Permission denied**:
- Run PowerShell as Administrator
- Or use: `Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process`

## 📄 License

MIT License - Feel free to use and modify for research and commercial purposes.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add your algorithm in `src/main/java/algorithms/`
4. Implement the `SamplingAlgorithm` interface
5. Submit a pull request

---

**Plant Quarantine Data Analysis Team** | v1.0.0