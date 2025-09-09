package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 피셔-예이츠 셔플 알고리즘 기반 샘플링
 * 
 * Ronald Fisher와 Frank Yates가 개발한 무편향 셔플 알고리즘을 사용하여
 * 수학적으로 증명된 공정한 샘플링을 제공합니다.
 * 현대적 구현은 Donald Knuth의 개선된 버전을 따릅니다.
 * 
 * 알고리즘 원리:
 * 1. 전체 그룹 리스트를 생성
 * 2. 리스트의 끝부터 시작하여 무작위 위치와 교환
 * 3. 교환 범위를 점진적으로 줄여가며 반복
 * 4. 셔플된 리스트에서 첫 번째 원소를 선택
 * 
 * 장점:
 * - 수학적으로 완전한 무편향성 보장
 * - 모든 순열이 동일한 확률로 생성
 * - 통계적으로 검증된 공정성
 * - 일정한 실행 시간
 * 
 * 단점:
 * - 추가 메모리 사용 (O(n))
 * - 단순 난수보다 약간 느린 실행 속도
 * - 구현 복잡도 증가
 * 
 * 적용 사례:
 * - 공정성이 중요한 추첨 시스템
 * - 통계적 샘플링 연구
 * - 게임의 카드 셔플
 * - 임상시험 대상자 무작위 배정
 * 
 * @author QIA Data Analysis Team
 * @version 1.0.0
 */
public class FisherYatesSampling implements SamplingAlgorithm {
    
    /** 난수 생성기 인스턴스 */
    private final Random random;
    
    /** 전체 그룹 수 */
    private static final int TOTAL_GROUPS = 33;
    
    /**
     * 기본 생성자
     * 현재 시간을 시드로 사용하여 Random 인스턴스를 초기화합니다.
     */
    public FisherYatesSampling() {
        this.random = new Random();
    }
    
    /**
     * 시드 지정 생성자
     * 
     * @param seed 난수 생성기 초기화에 사용할 시드값
     */
    public FisherYatesSampling(long seed) {
        this.random = new Random(seed);
    }
    
    @Override
    public String getName() {
        return "피셔-예이츠 셔플";
    }
    
    @Override
    public String getDescription() {
        return "Fisher-Yates 셔플 알고리즘을 사용한 무편향 샘플링입니다. " +
               "수학적으로 증명된 완전한 공정성을 보장하며, 모든 가능한 순열이 " +
               "동일한 확률로 생성됩니다. 통계적 신뢰성이 가장 높은 방법입니다.";
    }
    
    @Override
    public int generateSample(int groupStart) throws IllegalArgumentException {
        if (groupStart < 1 || groupStart > TOTAL_GROUPS) {
            throw new IllegalArgumentException(
                String.format("그룹 번호는 1부터 %d 사이여야 합니다. 입력값: %d", 
                            TOTAL_GROUPS, groupStart));
        }
        
        // 전체 그룹 번호 리스트 생성 (1부터 33까지)
        List<Integer> groups = new ArrayList<>();
        for (int i = 1; i <= TOTAL_GROUPS; i++) {
            groups.add(i);
        }
        
        // 피셔-예이츠 셔플 알고리즘 적용
        shuffleFisherYates(groups);
        
        // 셔플된 리스트의 첫 번째 원소 반환
        return groups.get(0);
    }
    
    /**
     * 피셔-예이츠 셔플 알고리즘 구현
     * 현대적 버전 (Knuth shuffle)을 사용합니다.
     * 
     * @param list 셔플할 리스트
     */
    private void shuffleFisherYates(List<Integer> list) {
        int size = list.size();
        
        // 리스트의 끝부터 시작하여 역순으로 진행
        for (int i = size - 1; i > 0; i--) {
            // 0부터 i까지의 범위에서 무작위 인덱스 선택
            int randomIndex = random.nextInt(i + 1);
            
            // 현재 위치(i)와 선택된 무작위 위치(randomIndex) 교환
            Collections.swap(list, i, randomIndex);
        }
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n)";
    }
    
    @Override
    public boolean isCryptographicallySecure() {
        return false; // Random 클래스 사용으로 인해 암호학적으로 안전하지 않음
    }
    
    /**
     * 알고리즘의 품질 지표를 반환합니다.
     * 
     * @return 알고리즘 품질 점수 (1-10, 높을수록 좋음)
     */
    public int getQualityScore() {
        return 10; // 최고 수준의 품질 (수학적 증명된 공정성)
    }
    
    /**
     * 셔플의 편향성을 검증하는 통계적 테스트를 수행합니다.
     * 
     * @param iterations 테스트 반복 횟수
     * @return 편향성 점수 (0에 가까울수록 공정함)
     */
    public double calculateBiasScore(int iterations) {
        int[] frequency = new int[TOTAL_GROUPS + 1]; // 1-based indexing
        
        for (int i = 0; i < iterations; i++) {
            int sample = generateSample(1);
            frequency[sample]++;
        }
        
        // 카이제곱 통계량 계산
        double expectedFrequency = (double) iterations / TOTAL_GROUPS;
        double chiSquare = 0.0;
        
        for (int i = 1; i <= TOTAL_GROUPS; i++) {
            double diff = frequency[i] - expectedFrequency;
            chiSquare += (diff * diff) / expectedFrequency;
        }
        
        return chiSquare;
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