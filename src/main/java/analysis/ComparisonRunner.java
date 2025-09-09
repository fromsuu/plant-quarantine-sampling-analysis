package analysis;

import algorithms.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 여러 샘플링 알고리즘의 성능을 비교 실행하는 클래스
 * 
 * 이 클래스는 다양한 샘플링 알고리즘들을 동일한 조건에서 테스트하고
 * 결과를 종합적으로 비교 분석합니다. 통계적 신뢰성을 위해 
 * 여러 번의 반복 테스트를 수행하고 결과를 정량화합니다.
 * 
 * 주요 기능:
 * - 다중 알고리즘 병렬 분석
 * - 통계적 유의성 검증
 * - 성능 순위 매김
 * - 상세 리포트 생성
 * 
 * @author QIA Data Analysis Team
 * @version 1.0.0
 */
public class ComparisonRunner {
    
    private final StatisticalAnalyzer analyzer;
    private final DateTimeFormatter dateFormatter;
    
    /**
     * 기본 생성자
     */
    public ComparisonRunner() {
        this.analyzer = new StatisticalAnalyzer();
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * 모든 기본 알고리즘에 대해 비교 분석을 수행합니다.
     * 
     * @param iterations 각 알고리즘당 반복 테스트 횟수
     * @return 비교 분석 결과 객체
     */
    public ComparisonResult runFullComparison(int iterations) {
        System.out.println("🚀 식물검역 샘플링 알고리즘 성능 비교 분석 시작");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.printf("📅 분석 일시: %s\n", LocalDateTime.now().format(dateFormatter));
        System.out.printf("🔄 테스트 반복 횟수: %d회\n", iterations);
        System.out.printf("📊 샘플 크기: %d개 그룹\n", StatisticalAnalyzer.TOTAL_GROUPS);
        System.out.println("════════════════════════════════════════════════════════\n");
        
        // 분석할 알고리즘들 초기화
        List<SamplingAlgorithm> algorithms = Arrays.asList(
            new StandardRandomSampling(),
            new FisherYatesSampling(),
            new CorrectedRandomSampling()
        );
        
        // 각 알고리즘 분석 수행
        List<StatisticalAnalyzer.AnalysisResult> results = new ArrayList<>();
        for (SamplingAlgorithm algorithm : algorithms) {
            StatisticalAnalyzer.AnalysisResult result = analyzer.analyzeAlgorithm(algorithm, iterations);
            results.add(result);
        }
        
        // 결과 순위 매김
        List<StatisticalAnalyzer.AnalysisResult> rankedResults = analyzer.rankResults(results);
        
        // 비교 결과 생성
        ComparisonResult comparisonResult = new ComparisonResult(
            LocalDateTime.now(),
            iterations,
            results,
            rankedResults
        );
        
        // 결과 출력
        printComparisonResults(comparisonResult);
        
        return comparisonResult;
    }
    
    /**
     * 기본 반복 횟수(5회)로 전체 비교 분석을 수행합니다.
     * 
     * @return 비교 분석 결과 객체
     */
    public ComparisonResult runFullComparison() {
        return runFullComparison(StatisticalAnalyzer.DEFAULT_ITERATIONS);
    }
    
    /**
     * 특정 알고리즘들에 대해서만 비교 분석을 수행합니다.
     * 
     * @param algorithms 분석할 알고리즘 리스트
     * @param iterations 반복 테스트 횟수
     * @return 비교 분석 결과 객체
     */
    public ComparisonResult runCustomComparison(List<SamplingAlgorithm> algorithms, int iterations) {
        System.out.println("🎯 맞춤형 알고리즘 비교 분석 시작");
        System.out.println("════════════════════════════════════════");
        System.out.printf("📅 분석 일시: %s\n", LocalDateTime.now().format(dateFormatter));
        System.out.printf("📊 분석 대상: %d개 알고리즘\n", algorithms.size());
        System.out.printf("🔄 반복 횟수: %d회\n", iterations);
        System.out.println("════════════════════════════════════════\n");
        
        List<StatisticalAnalyzer.AnalysisResult> results = new ArrayList<>();
        
        for (SamplingAlgorithm algorithm : algorithms) {
            StatisticalAnalyzer.AnalysisResult result = analyzer.analyzeAlgorithm(algorithm, iterations);
            results.add(result);
        }
        
        List<StatisticalAnalyzer.AnalysisResult> rankedResults = analyzer.rankResults(results);
        
        ComparisonResult comparisonResult = new ComparisonResult(
            LocalDateTime.now(),
            iterations,
            results,
            rankedResults
        );
        
        printComparisonResults(comparisonResult);
        
        return comparisonResult;
    }
    
    /**
     * 비교 결과를 콘솔에 출력합니다.
     * 
     * @param result 출력할 비교 결과
     */
    private void printComparisonResults(ComparisonResult result) {
        System.out.println("📊 분석 결과 요약");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.println(analyzer.generateSummaryTable(result.getAllResults()));
        
        System.out.println("\n🏆 최종 순위 및 평가");
        System.out.println("════════════════════════════════════════════════════════");
        
        List<StatisticalAnalyzer.AnalysisResult> ranked = result.getRankedResults();
        for (int i = 0; i < ranked.size(); i++) {
            StatisticalAnalyzer.AnalysisResult algorithmResult = ranked.get(i);
            String medal = getMedalIcon(i);
            String grade = getPerformanceGrade(algorithmResult);
            
            System.out.printf("%s %d위: %s\n", medal, i + 1, algorithmResult.getAlgorithmName());
            System.out.printf("     평균 균등성: %.4f | 결과 안정성: %.4f | 종합 평가: %s\n",
                            algorithmResult.getAverageUniformity(),
                            algorithmResult.getResultStability(),
                            grade);
            System.out.println();
        }
        
        // 최종 권장사항
        StatisticalAnalyzer.AnalysisResult winner = ranked.get(0);
        System.out.println("💡 권장사항");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.printf("✅ 최종 권장 알고리즘: %s\n", winner.getAlgorithmName());
        System.out.printf("📈 성능 우위: 평균 균등성 %.4f, 결과 안정성 %.4f\n",
                         winner.getAverageUniformity(), winner.getResultStability());
        System.out.println("🎯 식물검역 업무에서의 신뢰할 수 있는 랜덤 샘플링을 위해");
        System.out.printf("   '%s' 알고리즘 사용을 권장합니다.\n", winner.getAlgorithmName());
        System.out.println("════════════════════════════════════════════════════════\n");
    }
    
    /**
     * 순위에 따른 메달 아이콘을 반환합니다.
     * 
     * @param rank 순위 (0-based)
     * @return 메달 아이콘
     */
    private String getMedalIcon(int rank) {
        switch (rank) {
            case 0: return "🥇";
            case 1: return "🥈";
            case 2: return "🥉";
            default: return "📍";
        }
    }
    
    /**
     * 성능 결과에 따른 등급을 반환합니다.
     * 
     * @param result 분석 결과
     * @return 성능 등급
     */
    private String getPerformanceGrade(StatisticalAnalyzer.AnalysisResult result) {
        double totalScore = result.getAverageUniformity() + result.getResultStability();
        
        if (totalScore <= 2.0) {
            return "우수";
        } else if (totalScore <= 3.0) {
            return "양호";
        } else if (totalScore <= 4.0) {
            return "보통";
        } else {
            return "개선 필요";
        }
    }
    
    /**
     * 비교 분석 결과를 저장하는 클래스
     */
    public static class ComparisonResult {
        private final LocalDateTime analysisTime;
        private final int iterations;
        private final List<StatisticalAnalyzer.AnalysisResult> allResults;
        private final List<StatisticalAnalyzer.AnalysisResult> rankedResults;
        
        public ComparisonResult(LocalDateTime analysisTime, int iterations,
                               List<StatisticalAnalyzer.AnalysisResult> allResults,
                               List<StatisticalAnalyzer.AnalysisResult> rankedResults) {
            this.analysisTime = analysisTime;
            this.iterations = iterations;
            this.allResults = new ArrayList<>(allResults);
            this.rankedResults = new ArrayList<>(rankedResults);
        }
        
        // Getters
        public LocalDateTime getAnalysisTime() { return analysisTime; }
        public int getIterations() { return iterations; }
        public List<StatisticalAnalyzer.AnalysisResult> getAllResults() { 
            return new ArrayList<>(allResults); 
        }
        public List<StatisticalAnalyzer.AnalysisResult> getRankedResults() { 
            return new ArrayList<>(rankedResults); 
        }
        
        /**
         * 우승 알고리즘을 반환합니다.
         * 
         * @return 1위 알고리즘의 분석 결과
         */
        public StatisticalAnalyzer.AnalysisResult getWinningAlgorithm() {
            return rankedResults.isEmpty() ? null : rankedResults.get(0);
        }
        
        /**
         * 특정 알고리즘의 결과를 찾습니다.
         * 
         * @param algorithmName 찾을 알고리즘 이름
         * @return 해당 알고리즘의 분석 결과 (없으면 null)
         */
        public StatisticalAnalyzer.AnalysisResult findResultByName(String algorithmName) {
            return allResults.stream()
                           .filter(result -> result.getAlgorithmName().equals(algorithmName))
                           .findFirst()
                           .orElse(null);
        }
        
        /**
         * 분석 결과 요약을 반환합니다.
         * 
         * @return 요약 문자열
         */
        public String getSummary() {
            StatisticalAnalyzer.AnalysisResult winner = getWinningAlgorithm();
            if (winner == null) {
                return "분석 결과가 없습니다.";
            }
            
            return String.format("분석 완료 (%s) | 우승: %s | 총 %d개 알고리즘 비교",
                               analysisTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                               winner.getAlgorithmName(),
                               allResults.size());
        }
    }
}