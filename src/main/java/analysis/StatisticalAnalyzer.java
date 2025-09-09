package analysis;

import algorithms.SamplingAlgorithm;
import java.util.*;
import java.text.DecimalFormat;

/**
 * 샘플링 알고리즘의 통계적 성능을 분석하는 클래스
 * 
 * 이 클래스는 다양한 샘플링 알고리즘의 성능을 정량적으로 측정하고 비교합니다.
 * 주요 분석 지표로는 평균 균등성, 결과 안정성, 분포 균등성 등이 있습니다.
 * 
 * 분석 방법론:
 * - 5회 반복 실행을 통한 신뢰성 확보
 * - 표준편차 기반 균등성 평가
 * - 결과 간 변동성 측정
 * - 카이제곱 검정을 통한 균등성 검증
 * 
 * @author QIA Data Analysis Team
 * @version 1.0.0
 */
public class StatisticalAnalyzer {
    
    /** 전체 그룹 수 */
    public static final int TOTAL_GROUPS = 33;
    
    /** 기본 테스트 반복 횟수 */
    public static final int DEFAULT_ITERATIONS = 5;
    
    /** 각 테스트에서의 샘플 생성 횟수 */
    public static final int SAMPLES_PER_TEST = 1000;
    
    /** 소수점 포맷터 */
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");
    
    /**
     * 알고리즘 분석 결과를 저장하는 클래스
     */
    public static class AnalysisResult {
        private final String algorithmName;
        private final double[] standardDeviations;
        private final double averageUniformity;
        private final double resultStability;
        private final double chiSquareStatistic;
        private final boolean isUniform;
        private final long executionTimeNanos;
        
        public AnalysisResult(String algorithmName, double[] standardDeviations, 
                            double averageUniformity, double resultStability,
                            double chiSquareStatistic, boolean isUniform, 
                            long executionTimeNanos) {
            this.algorithmName = algorithmName;
            this.standardDeviations = Arrays.copyOf(standardDeviations, standardDeviations.length);
            this.averageUniformity = averageUniformity;
            this.resultStability = resultStability;
            this.chiSquareStatistic = chiSquareStatistic;
            this.isUniform = isUniform;
            this.executionTimeNanos = executionTimeNanos;
        }
        
        // Getters
        public String getAlgorithmName() { return algorithmName; }
        public double[] getStandardDeviations() { return Arrays.copyOf(standardDeviations, standardDeviations.length); }
        public double getAverageUniformity() { return averageUniformity; }
        public double getResultStability() { return resultStability; }
        public double getChiSquareStatistic() { return chiSquareStatistic; }
        public boolean isUniform() { return isUniform; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        
        @Override
        public String toString() {
            return String.format("[%s] 평균 균등성: %s, 결과 안정성: %s, 카이제곱: %s", 
                               algorithmName, 
                               DECIMAL_FORMAT.format(averageUniformity),
                               DECIMAL_FORMAT.format(resultStability),
                               DECIMAL_FORMAT.format(chiSquareStatistic));
        }
    }
    
    /**
     * 지정된 알고리즘에 대해 포괄적인 통계 분석을 수행합니다.
     * 
     * @param algorithm 분석할 샘플링 알고리즘
     * @param iterations 반복 테스트 횟수 (기본값: 5)
     * @return 분석 결과 객체
     */
    public AnalysisResult analyzeAlgorithm(SamplingAlgorithm algorithm, int iterations) {
        long startTime = System.nanoTime();
        
        System.out.printf("📊 [%s] 분석 시작...\n", algorithm.getName());
        
        // 각 반복에서의 표준편차 저장
        double[] standardDeviations = new double[iterations];
        
        for (int i = 0; i < iterations; i++) {
            System.out.printf("   테스트 %d/%d 실행 중...\n", i + 1, iterations);
            
            // 각 그룹별 선택 빈도 계산
            int[] frequency = new int[TOTAL_GROUPS + 1]; // 1-based indexing
            
            for (int j = 0; j < SAMPLES_PER_TEST; j++) {
                int sample = algorithm.generateSample(1);
                frequency[sample]++;
            }
            
            // 표준편차 계산
            standardDeviations[i] = calculateStandardDeviation(frequency, SAMPLES_PER_TEST);
        }
        
        // 평균 균등성 계산 (표준편차들의 평균)
        double averageUniformity = calculateMean(standardDeviations);
        
        // 결과 안정성 계산 (표준편차들의 표준편차)
        double resultStability = calculateStandardDeviation(standardDeviations);
        
        // 카이제곱 검정을 위한 추가 테스트
        double chiSquareStatistic = performChiSquareTest(algorithm, SAMPLES_PER_TEST * 2);
        
        // 균등성 판정 (카이제곱 임계값 기준)
        double criticalValue = 50.892; // α=0.05, df=32에서의 임계값
        boolean isUniform = chiSquareStatistic <= criticalValue;
        
        long executionTime = System.nanoTime() - startTime;
        
        System.out.printf("✅ [%s] 분석 완료 (%.2f초)\n\n", 
                         algorithm.getName(), executionTime / 1_000_000_000.0);
        
        return new AnalysisResult(algorithm.getName(), standardDeviations, 
                                averageUniformity, resultStability, 
                                chiSquareStatistic, isUniform, executionTime);
    }
    
    /**
     * 기본 반복 횟수(5회)로 알고리즘을 분석합니다.
     * 
     * @param algorithm 분석할 샘플링 알고리즘
     * @return 분석 결과 객체
     */
    public AnalysisResult analyzeAlgorithm(SamplingAlgorithm algorithm) {
        return analyzeAlgorithm(algorithm, DEFAULT_ITERATIONS);
    }
    
    /**
     * 빈도 배열에서 표준편차를 계산합니다.
     * 
     * @param frequency 각 그룹의 선택 빈도
     * @param totalSamples 전체 샘플 수
     * @return 표준편차 값
     */
    private double calculateStandardDeviation(int[] frequency, int totalSamples) {
        double expectedFrequency = (double) totalSamples / TOTAL_GROUPS;
        double sumSquaredDiff = 0.0;
        
        for (int i = 1; i <= TOTAL_GROUPS; i++) {
            double diff = frequency[i] - expectedFrequency;
            sumSquaredDiff += diff * diff;
        }
        
        return Math.sqrt(sumSquaredDiff / TOTAL_GROUPS);
    }
    
    /**
     * 배열의 표준편차를 계산합니다.
     * 
     * @param values 값들의 배열
     * @return 표준편차 값
     */
    private double calculateStandardDeviation(double[] values) {
        double mean = calculateMean(values);
        double sumSquaredDiff = 0.0;
        
        for (double value : values) {
            double diff = value - mean;
            sumSquaredDiff += diff * diff;
        }
        
        return Math.sqrt(sumSquaredDiff / values.length);
    }
    
    /**
     * 배열의 평균값을 계산합니다.
     * 
     * @param values 값들의 배열
     * @return 평균값
     */
    private double calculateMean(double[] values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }
    
    /**
     * 카이제곱 검정을 수행합니다.
     * 
     * @param algorithm 테스트할 알고리즘
     * @param sampleSize 샘플 크기
     * @return 카이제곱 통계량
     */
    private double performChiSquareTest(SamplingAlgorithm algorithm, int sampleSize) {
        int[] observedFrequency = new int[TOTAL_GROUPS + 1]; // 1-based indexing
        
        // 샘플 생성 및 빈도 계산
        for (int i = 0; i < sampleSize; i++) {
            int sample = algorithm.generateSample(1);
            observedFrequency[sample]++;
        }
        
        // 카이제곱 통계량 계산
        double expectedFrequency = (double) sampleSize / TOTAL_GROUPS;
        double chiSquare = 0.0;
        
        for (int i = 1; i <= TOTAL_GROUPS; i++) {
            double diff = observedFrequency[i] - expectedFrequency;
            chiSquare += (diff * diff) / expectedFrequency;
        }
        
        return chiSquare;
    }
    
    /**
     * 여러 분석 결과를 비교하여 순위를 매깁니다.
     * 
     * @param results 비교할 분석 결과들
     * @return 순위가 매겨진 결과 리스트 (1위가 첫 번째)
     */
    public List<AnalysisResult> rankResults(List<AnalysisResult> results) {
        List<AnalysisResult> sortedResults = new ArrayList<>(results);
        
        // 평균 균등성(낮을수록 좋음)과 결과 안정성(낮을수록 좋음)을 종합하여 정렬
        sortedResults.sort((r1, r2) -> {
            double score1 = r1.getAverageUniformity() + r1.getResultStability();
            double score2 = r2.getAverageUniformity() + r2.getResultStability();
            return Double.compare(score1, score2);
        });
        
        return sortedResults;
    }
    
    /**
     * 분석 결과의 상세 리포트를 생성합니다.
     * 
     * @param result 분석 결과
     * @return 포맷된 리포트 문자열
     */
    public String generateDetailedReport(AnalysisResult result) {
        StringBuilder report = new StringBuilder();
        
        report.append("═══════════════════════════════════════\n");
        report.append(String.format("📋 %s 상세 분석 리포트\n", result.getAlgorithmName()));
        report.append("═══════════════════════════════════════\n");
        
        report.append(String.format("🎯 평균 균등성: %s\n", 
                                  DECIMAL_FORMAT.format(result.getAverageUniformity())));
        report.append(String.format("📊 결과 안정성: %s\n", 
                                  DECIMAL_FORMAT.format(result.getResultStability())));
        report.append(String.format("🔍 카이제곱 통계량: %s\n", 
                                  DECIMAL_FORMAT.format(result.getChiSquareStatistic())));
        report.append(String.format("✅ 균등성 검정: %s\n", 
                                  result.isUniform() ? "통과" : "실패"));
        report.append(String.format("⏱️  실행 시간: %.2f초\n", 
                                  result.getExecutionTimeNanos() / 1_000_000_000.0));
        
        report.append("\n📈 각 테스트별 표준편차:\n");
        double[] stdevs = result.getStandardDeviations();
        for (int i = 0; i < stdevs.length; i++) {
            report.append(String.format("   테스트 %d: %s\n", 
                                      i + 1, DECIMAL_FORMAT.format(stdevs[i])));
        }
        
        return report.toString();
    }
    
    /**
     * 알고리즘별 성능 요약 테이블을 생성합니다.
     * 
     * @param results 분석 결과들
     * @return 포맷된 요약 테이블 문자열
     */
    public String generateSummaryTable(List<AnalysisResult> results) {
        StringBuilder table = new StringBuilder();
        
        table.append("┌─────────────────────────┬──────────────┬──────────────┬──────────────┐\n");
        table.append("│        알고리즘          │  평균 균등성  │  결과 안정성  │   종합 점수   │\n");
        table.append("├─────────────────────────┼──────────────┼──────────────┼──────────────┤\n");
        
        List<AnalysisResult> rankedResults = rankResults(results);
        
        for (int i = 0; i < rankedResults.size(); i++) {
            AnalysisResult result = rankedResults.get(i);
            double totalScore = result.getAverageUniformity() + result.getResultStability();
            String rank = (i == 0) ? " ⭐ (1위)" : String.format(" (%d위)", i + 1);
            
            table.append(String.format("│ %-23s │ %12s │ %12s │ %11s%s │\n",
                                     result.getAlgorithmName(),
                                     DECIMAL_FORMAT.format(result.getAverageUniformity()),
                                     DECIMAL_FORMAT.format(result.getResultStability()),
                                     DECIMAL_FORMAT.format(totalScore),
                                     rank));
        }
        
        table.append("└─────────────────────────┴──────────────┴──────────────┴──────────────┘\n");
        
        return table.toString();
    }
}