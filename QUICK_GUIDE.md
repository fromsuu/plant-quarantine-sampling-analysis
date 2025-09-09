# 🌱 Plant Quarantine Sampling Algorithm Analysis Tool

## 🚀 Quick Start (Recommended)

### One-Click Execution
```
Double-click QUICK_START.bat → Auto setup and run
```

This is the easiest way! It handles all setup automatically.

## 📋 Available Files

### Main Execution Files
- **`QUICK_START.bat`** - One-click setup and run (Most Recommended)
- **`run_analysis_EN.bat`** - English version analysis runner  
- **`run_analysis.bat`** - Original runner (Korean text, may have encoding issues)
- **`setup_java.bat`** - Java auto-installer (requires admin privileges)

### Execution Order (if needed)
1. `QUICK_START.bat` (solves all problems automatically)
2. Or `setup_java.bat` → `run_analysis_EN.bat` in sequence

## 🔧 System Requirements

### Auto-installation included
- **OS**: Windows 10/11
- **Internet**: Required (for Java and dependency downloads)
- **Admin Rights**: Required for Java auto-installation

### Manual installation
- **Java**: JDK 11+ (OpenJDK 17 recommended)
- **Maven**: 3.6+ (or use Maven Wrapper)

## 📊 Generated Result Files

### Text Reports
```
results/
├── standard_analysis_20250909_143022.txt
├── detailed_analysis_20250909_143022.txt
└── custom_analysis_10_iterations_20250909_143022.txt
```

### Excel Reports
```
results/excel-outputs/
├── standard_analysis_20250909_143022.xlsx
├── detailed_analysis_20250909_143022.xlsx
└── custom_analysis_10_iterations_20250909_143022.xlsx
```

## 🔍 Excel File Contents

Each Excel file contains 4 sheets:

1. **Summary** - Overall analysis results and rankings
2. **Detailed Data** - Detailed statistics for each algorithm
3. **Comparison** - Performance differences between algorithms
4. **Statistical Tests** - Chi-square test results

## ❓ Troubleshooting

### Encoding Issues (Korean text garbled)
- Use **`QUICK_START.bat`** (English interface)
- Or use **`run_analysis_EN.bat`** (English version)

### Java Installation Issues
```bash
# Manual installation link
https://adoptium.net/temurin/releases/
```

**Environment Variables Setup (Windows 10/11)**:
1. `Win + R` → type `sysdm.cpl`
2. "Advanced" tab → "Environment Variables"
3. Add `JAVA_HOME` in System Variables
4. Add `%JAVA_HOME%\bin` to `Path`

### Maven Issues
- Maven Wrapper handles this automatically
- Check your internet connection

### Permission Issues
```powershell
# Run PowerShell as Administrator
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Proxy Environment
```bash
# If Maven proxy setup needed
# Add to .mvn/jvm.config file:
-Dhttp.proxyHost=proxy.company.com -Dhttp.proxyPort=8080
-Dhttps.proxyHost=proxy.company.com -Dhttps.proxyPort=8080
```

## 🎯 Analysis Modes

### 1. Standard Analysis
- 5 iterations
- Basic performance comparison
- Quick results

### 2. Detailed Analysis
- 5 iterations  
- Detailed report for each algorithm
- Statistical details included

### 3. Custom Analysis
- 3-20 iterations (user configurable)
- Adjustable precision
- Research-grade data generation

## 📈 Result Interpretation

### Performance Metrics
- **Average Uniformity**: Lower is better (perfect uniformity = 0)
- **Result Stability**: Lower is better (consistent performance)
- **Chi-square Statistic**: ≤ 50.892 indicates uniformity

### Performance Grades
- **Excellent**: Total score ≤ 2.0
- **Good**: Total score ≤ 3.0
- **Average**: Total score ≤ 4.0  
- **Needs Improvement**: Total score > 4.0

## 🔬 Analyzed Algorithms

1. **Standard Random** (java.util.Random)
2. **Fisher-Yates Shuffle** (Fisher-Yates Shuffle)
3. **Enhanced Random** (XOR, Modular operations)

## 📞 Support

If issues occur, check:

1. **Internet connection** status
2. Run as **Administrator**
3. Temporarily disable **antivirus** real-time scanning
4. Check **Windows firewall** settings

---

🌱 **Korea Plant Quarantine Data Analysis Team** | v1.0.0