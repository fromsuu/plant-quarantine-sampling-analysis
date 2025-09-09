package algorithms;

import java.util.Random;

/**
 * 난수 생성 + 보정 알고리즘 기반 샘플링
 * 
 * XOR 연산과 모듈러 연산을 통해 기본 난수의 편향을 보정하려고 시도하는
 * 샘플링 알고리즘입니다. 추가적인 수학적 변환을 통해 분포의 균등성을
 * 개선하고자 하는 실험적 접근법을 구현합니다.
 * 
 * 알고리즘 원리:
 * 1. 기본 난수 생성 (Random.nextInt())
 * 2. XOR 연산을 통한 비트 패턴 변경
 * 3. 모듈러 연산을 통한 범위 조정
 * 4. 추가 보정 함수 적용
 * 
 * 장점:
 * - 기본 난수의 편향 패턴 변경 시도
 * - 추가적인 무작위성 도입
 * - 구현이 비교적 간단
 * 
 * 단점:
 * - 수학적 증명되지 않은 보정 방법
 * - 실제 편향 감소 효과 불분명
 * - 추가 연산으로 인한 성능 저하
 * - 새로운 편향 패턴 도입 가능성
 * 
 * 적용 사례:
 * - 실험적 샘플링 연구
 * - 기본 난수 품질 개선 시도
 * - 특수한 분포 요구사항이 있는 경우
 * 
 * 주의사항:
 * 이 알고리즘은 실험적 성격이 강하며, 실제 편향 감소 효과에 대한
 * 수학적 보장은 없습니다. 프로덕션 환경에서는 신중한 검토가 필요합니다.
 * 
 * @author QIA Data Analysis Team
 * @version 1.0.0
 */
public class CorrectedRandomSampling implements SamplingAlgorithm {
    
    /** 난수 생성기 인스턴스 */
    private final Random random;
    
    /** 전체 그룹 수 */
    private static final int TOTAL_GROUPS = 33;
    
    /** XOR 보정용 마스크 값 */
    private static final int XOR_MASK = 0x5A5A5A5A;
    
    /** 모듈러 보정용 소수 */
    private static final int PRIME_MODULUS = 97;
    
    /**
     * 기본 생성자
     * 현재 시간을 시드로 사용하여 Random 인스턴스를 초기화합니다.
     */
    public CorrectedRandomSampling() {
        this.random = new Random();
    }
    
    /**
     * 시드 지정 생성자
     * 
     * @param seed 난수 생성기 초기화에 사용할 시드값
     */
    public CorrectedRandomSampling(long seed) {
        this.random = new Random(seed);
    }
    
    @Override
    public String getName() {
        return "난수 생성 + 보정";
    }
    
    @Override
    public String getDescription() {
        return "기본 난수 생성 후 XOR 연산과 모듈러 연산을 통해 편향을 보정하려고 " +
               "시도하는 실험적 알고리즘입니다. 추가적인 수학적 변환을 통해 " +
               "분포의 균등성을 개선하고자 하지만, 효과는 검증이 필요합니다.";
    }
    
    @Override
    public int generateSample(int groupStart) throws IllegalArgumentException {
        if (groupStart < 1 || groupStart > TOTAL_GROUPS) {
            throw new IllegalArgumentException(
                String.format("그룹 번호는 1부터 %d 사이여야 합니다. 입력값: %d", 
                            TOTAL_GROUPS, groupStart));
        }
        
        // 1단계: 기본 난수 생성
        int baseRandom = random.nextInt();
        
        // 2단계: XOR 연산을 통한 비트 패턴 변경
        int xorCorrected = baseRandom ^ XOR_MASK;
        
        // 3단계: 모듈러 연산을 통한 추가 보정
        int modularCorrected = Math.abs(xorCorrected) % PRIME_MODULUS;
        
        // 4단계: 추가 보정 함수 적용
        int finalCorrected = applyAdditionalCorrection(modularCorrected);
        
        // 5단계: 최종 범위 조정 (1부터 TOTAL_GROUPS까지)
        int result = (finalCorrected % TOTAL_GROUPS) + 1;
        
        return result;
    }
    
    /**
     * 추가 보정 함수를 적용합니다.
     * 선형 합동식과 비트 회전을 조합하여 추가적인 무작위성을 도입합니다.
     * 
     * @param value 보정할 값
     * @return 보정된 값
     */
    private int applyAdditionalCorrection(int value) {
        // 선형 합동식 적용: (a * x + c) mod m
        int linearCongruent = (31 * value + 17) % 101;
        
        // 비트 회전 (좌로 3비트 회전)
        int rotated = Integer.rotateLeft(linearCongruent, 3);
        
        // 현재 시간의 나노초를 이용한 추가 엔트로피
        long nanoTime = System.nanoTime();
        int timeEntropy = (int) (nanoTime % Integer.MAX_VALUE);
        
        // 최종 보정값 계산
        return Math.abs(rotated ^ timeEntropy);
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(1)";
    }
    
    @Override
    public boolean isCryptographicallySecure() {
        return false; // 예측 가능한 보정 알고리즘 사용
    }
    
    /**
     * 알고리즘의 품질 지표를 반환합니다.
     * 
     * @return 알고리즘 품질 점수 (1-10, 높을수록 좋음)
     */
    public int getQualityScore() {
        return 4; // 실험적 성격으로 인한 낮은 품질 점수
    }
    
    /**
     * 보정 효과를 측정하기 위한 테스트를 수행합니다.
     * 
     * @param iterations 테스트 반복 횟수
     * @return 보정 전후의 편향 비교 결과
     */
    public CorrectionEffectResult measureCorrectionEffect(int iterations) {
        int[] frequency = new int[TOTAL_GROUPS + 1]; // 1-based indexing
        int[] baseFrequency = new int[TOTAL_GROUPS + 1];
        
        Random baseRandom = new Random(random.nextLong());
        
        for (int i = 0; i < iterations; i++) {
            // 보정된 샘플
            int correctedSample = generateSample(1);
            frequency[correctedSample]++;
            
            // 기본 랜덤 샘플 (비교용)
            int baseSample = (baseRandom.nextInt(TOTAL_GROUPS)) + 1;
            baseFrequency[baseSample]++;
        }
        
        double correctedVariance = calculateVariance(frequency, iterations);
        double baseVariance = calculateVariance(baseFrequency, iterations);
        
        return new CorrectionEffectResult(baseVariance, correctedVariance, 
                                        correctedVariance < baseVariance);
    }
    
    /**
     * 빈도 배열의 분산을 계산합니다.
     * 
     * @param frequency 빈도 배열
     * @param total 전체 샘플 수
     * @return 분산값
     */
    private double calculateVariance(int[] frequency, int total) {
        double expectedFrequency = (double) total / TOTAL_GROUPS;
        double sumSquaredDiff = 0.0;
        
        for (int i = 1; i <= TOTAL_GROUPS; i++) {
            double diff = frequency[i] - expectedFrequency;
            sumSquaredDiff += diff * diff;
        }
        
        return sumSquaredDiff / TOTAL_GROUPS;
    }
    
    /**
     * 보정 효과 측정 결과를 담는 내부 클래스
     */
    public static class CorrectionEffectResult {
        public final double baseVariance;
        public final double correctedVariance;
        public final boolean isImproved;
        
        public CorrectionEffectResult(double baseVariance, double correctedVariance, 
                                    boolean isImproved) {
            this.baseVariance = baseVariance;
            this.correctedVariance = correctedVariance;
            this.isImproved = isImproved;
        }
        
        @Override
        public String toString() {
            return String.format("기본 분산: %.4f, 보정 분산: %.4f, 개선됨: %s", 
                               baseVariance, correctedVariance, 
                               isImproved ? "예" : "아니오");
        }
    }
    
    /**
     * 현재 Random 인스턴스의 시드를 재설정합니다.
     * 
     * @param seed 새로운 시드값
     */
    public void reseed(long seed) {
        random.setSeed(seed);
    }
}