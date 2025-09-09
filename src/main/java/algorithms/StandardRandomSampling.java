package algorithms;

import java.util.Random;

/**
 * 일반 랜덤 함수 기반 샘플링 알고리즘
 * 
 * Java의 기본 java.util.Random 클래스를 사용하여 샘플을 생성합니다.
 * 선형 합동 생성기(Linear Congruential Generator)를 기반으로 하며,
 * 빠른 실행 속도를 제공하지만 예측 가능한 패턴이 발생할 수 있습니다.
 * 
 * 장점:
 * - 빠른 실행 속도
 * - 간단한 구현
 * - JVM 최적화 혜택
 * 
 * 단점:
 * - 의사난수 생성으로 인한 편향 가능성
 * - 예측 가능한 시드 기반 패턴
 * - 암호학적으로 안전하지 않음
 * 
 * 적용 사례:
 * - 일반적인 통계 샘플링
 * - 성능이 중요한 대량 데이터 처리
 * - 단순한 무작위 선택이 필요한 경우
 * 
 * @author QIA Data Analysis Team
 * @version 1.0.0
 */
public class StandardRandomSampling implements SamplingAlgorithm {
    
    /** 난수 생성기 인스턴스 */
    private final Random random;
    
    /** 전체 그룹 수 */
    private static final int TOTAL_GROUPS = 33;
    
    /**
     * 기본 생성자
     * 현재 시간을 시드로 사용하여 Random 인스턴스를 초기화합니다.
     */
    public StandardRandomSampling() {
        this.random = new Random();
    }
    
    /**
     * 시드 지정 생성자
     * 
     * @param seed 난수 생성기 초기화에 사용할 시드값
     */
    public StandardRandomSampling(long seed) {
        this.random = new Random(seed);
    }
    
    @Override
    public String getName() {
        return "일반 랜덤 함수";
    }
    
    @Override
    public String getDescription() {
        return "Java 표준 Random 클래스를 사용한 기본 난수 생성 알고리즘입니다. " +
               "선형 합동 생성기를 기반으로 하며 빠른 성능을 제공하지만 " +
               "의사난수 특성으로 인한 편향이 발생할 수 있습니다.";
    }
    
    @Override
    public int generateSample(int groupStart) throws IllegalArgumentException {
        if (groupStart < 1 || groupStart > TOTAL_GROUPS) {
            throw new IllegalArgumentException(
                String.format("그룹 번호는 1부터 %d 사이여야 합니다. 입력값: %d", 
                            TOTAL_GROUPS, groupStart));
        }
        
        // groupStart를 포함한 전체 범위에서 랜덤 선택
        int randomValue = random.nextInt(TOTAL_GROUPS) + 1;
        return randomValue;
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(1)";
    }
    
    @Override
    public boolean isCryptographicallySecure() {
        return false;
    }
    
    /**
     * 알고리즘의 품질 지표를 반환합니다.
     * 
     * @return 알고리즘 품질 점수 (1-10, 높을수록 좋음)
     */
    public int getQualityScore() {
        return 6; // 보통 수준의 품질
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