import analysis.ComparisonRunner;
import analysis.StatisticalAnalyzer;
import analysis.ExcelExporter;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * 식물검역 샘플링 알고리즘 비교 분석 프로그램의 메인 클래스
 * 
 * 이 프로그램은 식물검역 업무에서 사용되는 다양한 랜덤 샘플링 알고리즘의
 * 성능을 비교 분석하여 최적의 알고리즘을 제안합니다.
 * 
 * 주요 기능:
 * - 3가지 샘플링 알고리즘 성능 비교
 * - 5회 반복 실행을 통한 통계적 신뢰성 확보
 * - 평균 균등성 및 결과 안정성 분석
 * - 상세 분석 리포트 생성
 * - 결과를 텍스트 파일로 자동 저장
 * 
 * 분석 대상 알고리즘:
 * 1. 일반 랜덤 함수 (java.util.Random)
 * 2. 피셔-예이츠 셔플 (Fisher-Yates Shuffle)
 * 3. 난수 생성 + 보정 (XOR, Modular 연산)
 * 
 * @author QIA Data Analysis Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class AlgorithmComparisonMain {
    
    /** 프로그램 제목 */
    private static final String PROGRAM_TITLE = "식물검역 샘플링 알고리즘 성능 비교 분석 프로그램";
    
    /** 프로그램 버전 */
    private static final String VERSION = "v1.0.0";
    
    /** 결과 파일 저장 디렉토리 */
    private static final String RESULTS_DIR = "results";
    
    /** 콘솔 입력을 위한 Scanner */
    private static final Scanner scanner = new Scanner(System.in);
    
    /**
     * 프로그램의 메인 엔트리 포인트
     * 
     * @param args 명령행 인수 (현재는 사용하지 않음)
     */
    public static void main(String[] args) {
        try {
            // 프로그램 시작 안내
            printWelcomeMessage();
            
            // 메뉴 시스템 실행
            runMenuSystem();
            
        } catch (Exception e) {
            System.err.println("❌ 프로그램 실행 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
            System.out.println("\n👋 프로그램을 종료합니다. 감사합니다!");
        }
    }
    
    /**
     * 프로그램 시작 시 환영 메시지를 출력합니다.
     */
    private static void printWelcomeMessage() {
        System.out.println("🌱═══════════════════════════════════════════════════════════🌱");
        System.out.println("🔬 " + PROGRAM_TITLE);
        System.out.println("   " + VERSION + " | Korea Plant Quarantine Data Analysis Team");
        System.out.println("🌱═══════════════════════════════════════════════════════════🌱");
        System.out.println();
        System.out.println("📋 프로그램 목적:");
        System.out.println("   식물검역 업무에서 사용할 랜덤 샘플링 알고리즘의 신뢰성을 분석하고");
        System.out.println("   통계적 근거를 바탕으로 최적의 알고리즘을 제안합니다.");
        System.out.println();
        System.out.println("🔍 분석 대상:");
        System.out.println("   1️⃣  일반 랜덤 함수 (java.util.Random)");
        System.out.println("   2️⃣  피셔-예이츠 셔플 (Fisher-Yates Shuffle)");
        System.out.println("   3️⃣  난수 생성 + 보정 (XOR, Modular 연산)");
        System.out.println();
        System.out.println("📊 분석 방법:");
        System.out.println("   • 5회 반복 실행으로 통계적 신뢰성 확보");
        System.out.println("   • 평균 균등성 및 결과 안정성 측정");
        System.out.println("   • 카이제곱 검정을 통한 균등성 검증");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
    }
    
    /**
     * 메뉴 시스템을 실행합니다.
     */
    private static void runMenuSystem() {
        while (true) {
            printMainMenu();
            
            System.out.print("선택하세요 (1-4): ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    runStandardAnalysis();
                    break;
                case "2":
                    runDetailedAnalysis();
                    break;
                case "3":
                    runCustomAnalysis();
                    break;
                case "4":
                    return; // 프로그램 종료
                default:
                    System.out.println("❌ 잘못된 선택입니다. 1-4 사이의 숫자를 입력해주세요.\n");
                    break;
            }
        }
    }
    
    /**
     * 메인 메뉴를 출력합니다.
     */
    private static void printMainMenu() {
        System.out.println("📋 메인 메뉴");
        System.out.println("─────────────────────────────────────────");
        System.out.println("1️⃣  표준 분석 실행 (5회 반복, 빠른 분석)");
        System.out.println("2️⃣  상세 분석 실행 (5회 반복, 상세 리포트)");
        System.out.println("3️⃣  맞춤형 분석 실행 (반복 횟수 설정)");
        System.out.println("4️⃣  프로그램 종료");
        System.out.println("─────────────────────────────────────────");
    }
    
    /**
     * 표준 분석을 실행합니다.
     * 5회 반복으로 빠른 분석을 수행하고 요약 결과만 출력합니다.
     */
    private static void runStandardAnalysis() {
        System.out.println("\n🚀 표준 분석을 시작합니다...\n");
        
        ComparisonRunner runner = new ComparisonRunner();
        ComparisonRunner.ComparisonResult result = runner.runFullComparison();
        
        // 결과를 파일로 저장
        String filename = saveResultsToFile(result, "standard_analysis");
        System.out.printf("💾 분석 결과가 '%s' 파일에 저장되었습니다.\n", filename);
        
        // Excel 파일로도 저장
        try {
            ExcelExporter excelExporter = new ExcelExporter();
            String excelFilename = excelExporter.exportToExcel(result, "standard_analysis");
            System.out.printf("📊 Excel 결과가 '%s' 파일에 저장되었습니다.\n", excelFilename);
        } catch (IOException e) {
            System.err.printf("⚠️ Excel 파일 저장 중 오류: %s\n", e.getMessage());
        }
        
        waitForUserInput();
    }
    
    /**
     * 상세 분석을 실행합니다.
     * 5회 반복으로 분석하고 각 알고리즘의 상세 리포트를 출력합니다.
     */
    private static void runDetailedAnalysis() {
        System.out.println("\n🔍 상세 분석을 시작합니다...\n");
        
        ComparisonRunner runner = new ComparisonRunner();
        StatisticalAnalyzer analyzer = new StatisticalAnalyzer();
        ComparisonRunner.ComparisonResult result = runner.runFullComparison();
        
        // 각 알고리즘의 상세 리포트 출력
        System.out.println("\n📄 각 알고리즘 상세 분석 리포트");
        System.out.println("════════════════════════════════════════════════════════");
        
        for (StatisticalAnalyzer.AnalysisResult algorithmResult : result.getAllResults()) {
            System.out.println(analyzer.generateDetailedReport(algorithmResult));
        }
        
        // 결과를 파일로 저장
        String filename = saveDetailedResultsToFile(result, analyzer, "detailed_analysis");
        System.out.printf("💾 상세 분석 결과가 '%s' 파일에 저장되었습니다.\n", filename);
        
        // Excel 파일로도 저장
        try {
            ExcelExporter excelExporter = new ExcelExporter();
            String excelFilename = excelExporter.exportToExcel(result, "detailed_analysis");
            System.out.printf("📊 Excel 결과가 '%s' 파일에 저장되었습니다.\n", excelFilename);
        } catch (IOException e) {
            System.err.printf("⚠️ Excel 파일 저장 중 오류: %s\n", e.getMessage());
        }
        
        waitForUserInput();
    }
    
    /**
     * 맞춤형 분석을 실행합니다.
     * 사용자가 반복 횟수를 설정할 수 있습니다.
     */
    private static void runCustomAnalysis() {
        System.out.println("\n⚙️ 맞춤형 분석 설정");
        System.out.println("─────────────────────────────────────────");
        
        int iterations = getIterationsFromUser();
        
        System.out.printf("\n🎯 %d회 반복으로 맞춤형 분석을 시작합니다...\n\n", iterations);
        
        ComparisonRunner runner = new ComparisonRunner();
        ComparisonRunner.ComparisonResult result = runner.runFullComparison(iterations);
        
        // 결과를 파일로 저장
        String filename = saveResultsToFile(result, String.format("custom_analysis_%d_iterations", iterations));
        System.out.printf("💾 맞춤형 분석 결과가 '%s' 파일에 저장되었습니다.\n", filename);
        
        // Excel 파일로도 저장
        try {
            ExcelExporter excelExporter = new ExcelExporter();
            String excelFilename = excelExporter.exportToExcel(result, String.format("custom_analysis_%d_iterations", iterations));
            System.out.printf("📊 Excel 결과가 '%s' 파일에 저장되었습니다.\n", excelFilename);
        } catch (IOException e) {
            System.err.printf("⚠️ Excel 파일 저장 중 오류: %s\n", e.getMessage());
        }
        
        waitForUserInput();
    }
    
    /**
     * 사용자로부터 반복 횟수를 입력받습니다.
     * 
     * @return 유효한 반복 횟수
     */
    private static int getIterationsFromUser() {
        while (true) {
            System.out.print("반복 횟수를 입력하세요 (3-20, 권장: 5): ");
            String input = scanner.nextLine().trim();
            
            try {
                int iterations = Integer.parseInt(input);
                if (iterations >= 3 && iterations <= 20) {
                    return iterations;
                } else {
                    System.out.println("❌ 반복 횟수는 3-20 사이여야 합니다.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ 올바른 숫자를 입력해주세요.");
            }
        }
    }
    
    /**
     * 분석 결과를 텍스트 파일로 저장합니다.
     * 
     * @param result 저장할 비교 결과
     * @param prefix 파일명 접두사
     * @return 생성된 파일명
     */
    private static String saveResultsToFile(ComparisonRunner.ComparisonResult result, String prefix) {
        // 결과 디렉토리 생성
        java.io.File dir = new java.io.File(RESULTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 파일명 생성 (타임스탬프 포함)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("%s/%s_%s.txt", RESULTS_DIR, prefix, timestamp);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false))) {
            // 헤더 작성
            writer.println("═══════════════════════════════════════════════════════════════");
            writer.println(PROGRAM_TITLE + " - 분석 결과 리포트");
            writer.println("버전: " + VERSION);
            writer.println("분석 일시: " + result.getAnalysisTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("반복 횟수: " + result.getIterations() + "회");
            writer.println("═══════════════════════════════════════════════════════════════");
            writer.println();
            
            // 결과 요약 작성
            StatisticalAnalyzer analyzer = new StatisticalAnalyzer();
            writer.println("📊 분석 결과 요약");
            writer.println("════════════════════════════════════════════════════════");
            writer.print(analyzer.generateSummaryTable(result.getAllResults()));
            writer.println();
            
            // 순위 및 평가 작성
            writer.println("🏆 최종 순위 및 평가");
            writer.println("════════════════════════════════════════════════════════");
            
            for (int i = 0; i < result.getRankedResults().size(); i++) {
                StatisticalAnalyzer.AnalysisResult algorithmResult = result.getRankedResults().get(i);
                writer.printf("%d위: %s\n", i + 1, algorithmResult.getAlgorithmName());
                writer.printf("     평균 균등성: %.4f | 결과 안정성: %.4f\n",
                             algorithmResult.getAverageUniformity(),
                             algorithmResult.getResultStability());
                writer.println();
            }
            
            // 권장사항 작성
            StatisticalAnalyzer.AnalysisResult winner = result.getWinningAlgorithm();
            if (winner != null) {
                writer.println("💡 권장사항");
                writer.println("════════════════════════════════════════════════════════");
                writer.printf("✅ 최종 권장 알고리즘: %s\n", winner.getAlgorithmName());
                writer.printf("📈 성능 우위: 평균 균등성 %.4f, 결과 안정성 %.4f\n",
                             winner.getAverageUniformity(), winner.getResultStability());
                writer.println("🎯 식물검역 업무에서의 신뢰할 수 있는 랜덤 샘플링을 위해");
                writer.printf("   '%s' 알고리즘 사용을 권장합니다.\n", winner.getAlgorithmName());
            }
            
            writer.println("════════════════════════════════════════════════════════");
            writer.println("리포트 생성 완료: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
        } catch (IOException e) {
            System.err.println("❌ 파일 저장 중 오류가 발생했습니다: " + e.getMessage());
            return "저장 실패";
        }
        
        return filename;
    }
    
    /**
     * 상세 분석 결과를 텍스트 파일로 저장합니다.
     * 
     * @param result 저장할 비교 결과
     * @param analyzer 분석기 인스턴스
     * @param prefix 파일명 접두사
     * @return 생성된 파일명
     */
    private static String saveDetailedResultsToFile(ComparisonRunner.ComparisonResult result, 
                                                   StatisticalAnalyzer analyzer, String prefix) {
        String basicFilename = saveResultsToFile(result, prefix);
        
        // 상세 리포트 추가를 위한 새 파일명
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String detailedFilename = String.format("%s/%s_detailed_%s.txt", RESULTS_DIR, prefix, timestamp);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(detailedFilename, false))) {
            // 기본 결과 포함
            writer.println("═══════════════════════════════════════════════════════════════");
            writer.println(PROGRAM_TITLE + " - 상세 분석 결과 리포트");
            writer.println("버전: " + VERSION);
            writer.println("분석 일시: " + result.getAnalysisTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("반복 횟수: " + result.getIterations() + "회");
            writer.println("═══════════════════════════════════════════════════════════════");
            writer.println();
            
            // 각 알고리즘의 상세 리포트
            for (StatisticalAnalyzer.AnalysisResult algorithmResult : result.getAllResults()) {
                writer.print(analyzer.generateDetailedReport(algorithmResult));
                writer.println();
            }
            
            writer.println("════════════════════════════════════════════════════════");
            writer.println("상세 리포트 생성 완료: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
        } catch (IOException e) {
            System.err.println("❌ 상세 파일 저장 중 오류가 발생했습니다: " + e.getMessage());
            return basicFilename; // 기본 파일이라도 반환
        }
        
        return detailedFilename;
    }
    
    /**
     * 사용자가 키를 누를 때까지 대기합니다.
     */
    private static void waitForUserInput() {
        System.out.println("\n⏩ 계속하려면 Enter 키를 누르세요...");
        scanner.nextLine();
        System.out.println();
    }
}