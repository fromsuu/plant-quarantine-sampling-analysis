package algorithms;

/**
 * 식물검역 샘플링 알고리즘 인터페이스
 * 모든 샘플링 알고리즘은 이 인터페이스를 구현해야 합니다.
 * 
 * @author QIA Data Analysis Team
 * @version 1.0.0
 */
public interface SamplingAlgorithm {
    
    /**
     * 알고리즘 이름을 반환합니다.
     * @return 알고리즘의 고유 식별 이름
     */
    String getName();
    
    /**
     * 알고리즘의 상세 설명을 반환합니다.
     * @return 알고리즘 작동 원리와 특징에 대한 설명
     */
    String getDescription();
    
    /**
     * 지정된 그룹에서 시작하여 랜덤 샘플을 생성합니다.
     * 
     * @param groupStart 시작 그룹 번호 (1부터 시작)
     * @return 생성된 샘플 그룹 번호
     * @throws IllegalArgumentException 그룹 번호가 유효하지 않은 경우
     */
    int generateSample(int groupStart) throws IllegalArgumentException;
    
    /**
     * 알고리즘의 시간 복잡도를 반환합니다.
     * @return 시간 복잡도 (예: "O(1)", "O(n)" 등)
     */
    default String getTimeComplexity() {
        return "O(1)";
    }
    
    /**
     * 알고리즘이 암호학적으로 안전한지 여부를 반환합니다.
     * @return 암호학적 안전성 여부
     */
    default boolean isCryptographicallySecure() {
        return false;
    }
}