package analysis;

import algorithms.SamplingAlgorithm;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 분석 결과를 Excel 파일로 내보내는 클래스
 * 
 * Apache POI 라이브러리를 사용하여 분석 결과를 구조화된 Excel 파일로 생성합니다.
 * 여러 시트를 통해 요약, 상세 데이터, 차트 등을 제공합니다.
 * 
 * 생성되는 시트:
 * 1. 요약 (Summary) - 전체 분석 결과 요약
 * 2. 상세 데이터 (Detailed Data) - 각 알고리즘별 상세 수치
 * 3. 비교 분석 (Comparison) - 알고리즘 간 성능 비교
 * 4. 통계 검정 (Statistical Tests) - 카이제곱 검정 등 통계 결과
 * 
 * @author QIA Data Analysis Team
 * @version 1.0.0
 */
public class ExcelExporter {
    
    /** Excel 파일 저장 디렉토리 */
    private static final String EXCEL_DIR = "results/excel-outputs";
    
    /** 날짜 형식 포맷터 */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * 비교 분석 결과를 Excel 파일로 내보냅니다.
     * 
     * @param comparisonResult 내보낼 비교 분석 결과
     * @param filename 저장할 파일명 (확장자 제외)
     * @return 생성된 Excel 파일의 전체 경로
     * @throws IOException 파일 생성 중 오류 발생 시
     */
    public String exportToExcel(ComparisonRunner.ComparisonResult comparisonResult, String filename) throws IOException {
        // Excel 파일 저장 디렉토리 생성
        java.io.File dir = new java.io.File(EXCEL_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 파일명 생성
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMATTER);
        String fullFilename = String.format("%s/%s_%s.xlsx", EXCEL_DIR, filename, timestamp);
        
        // Excel 워크북 생성
        try (Workbook workbook = new XSSFWorkbook()) {
            // 스타일 생성
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            
            // 시트들 생성
            createSummarySheet(workbook, comparisonResult, headerStyle, dataStyle, numberStyle, titleStyle);
            createDetailedDataSheet(workbook, comparisonResult, headerStyle, dataStyle, numberStyle);
            createComparisonSheet(workbook, comparisonResult, headerStyle, dataStyle, numberStyle);
            createStatisticalTestSheet(workbook, comparisonResult, headerStyle, dataStyle, numberStyle);
            
            // 파일 저장
            try (FileOutputStream fileOut = new FileOutputStream(fullFilename)) {
                workbook.write(fileOut);
            }
        }
        
        return fullFilename;
    }
    
    /**
     * 요약 시트를 생성합니다.
     */
    private void createSummarySheet(Workbook workbook, ComparisonRunner.ComparisonResult result,
                                  CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle, CellStyle titleStyle) {
        Sheet sheet = workbook.createSheet("요약 (Summary)");
        int rowNum = 0;
        
        // 제목
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("식물검역 샘플링 알고리즘 성능 비교 분석 결과");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));
        
        rowNum++; // 빈 행
        
        // 분석 정보
        Row infoRow1 = sheet.createRow(rowNum++);
        infoRow1.createCell(0).setCellValue("분석 일시:");
        infoRow1.createCell(1).setCellValue(result.getAnalysisTime().format(DATE_FORMATTER));
        
        Row infoRow2 = sheet.createRow(rowNum++);
        infoRow2.createCell(0).setCellValue("반복 횟수:");
        Cell iterCell = infoRow2.createCell(1);
        iterCell.setCellValue(result.getIterations());
        iterCell.setCellStyle(numberStyle);
        
        Row infoRow3 = sheet.createRow(rowNum++);
        infoRow3.createCell(0).setCellValue("분석 대상:");
        infoRow3.createCell(1).setCellValue(result.getAllResults().size() + "개 알고리즘");
        
        rowNum++; // 빈 행
        
        // 결과 테이블 헤더
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"순위", "알고리즘", "평균 균등성", "결과 안정성", "종합 점수", "성능 평가"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 결과 데이터
        List<StatisticalAnalyzer.AnalysisResult> rankedResults = result.getRankedResults();
        for (int i = 0; i < rankedResults.size(); i++) {
            StatisticalAnalyzer.AnalysisResult algorithmResult = rankedResults.get(i);
            Row dataRow = sheet.createRow(rowNum++);
            
            // 순위
            dataRow.createCell(0).setCellValue(i + 1);
            
            // 알고리즘명
            dataRow.createCell(1).setCellValue(algorithmResult.getAlgorithmName());
            
            // 평균 균등성
            Cell uniformityCell = dataRow.createCell(2);
            uniformityCell.setCellValue(algorithmResult.getAverageUniformity());
            uniformityCell.setCellStyle(numberStyle);
            
            // 결과 안정성
            Cell stabilityCell = dataRow.createCell(3);
            stabilityCell.setCellValue(algorithmResult.getResultStability());
            stabilityCell.setCellStyle(numberStyle);
            
            // 종합 점수
            Cell totalScoreCell = dataRow.createCell(4);
            double totalScore = algorithmResult.getAverageUniformity() + algorithmResult.getResultStability();
            totalScoreCell.setCellValue(totalScore);
            totalScoreCell.setCellStyle(numberStyle);
            
            // 성능 평가
            String grade = getPerformanceGrade(totalScore);
            dataRow.createCell(5).setCellValue(grade);
        }
        
        // 컬럼 너비 자동 조정
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // 권장사항 추가
        rowNum += 2;
        Row recommendRow = sheet.createRow(rowNum++);
        recommendRow.createCell(0).setCellValue("권장 알고리즘:");
        StatisticalAnalyzer.AnalysisResult winner = result.getWinningAlgorithm();
        if (winner != null) {
            Cell winnerCell = recommendRow.createCell(1);
            winnerCell.setCellValue(winner.getAlgorithmName());
            winnerCell.setCellStyle(headerStyle);
        }
    }
    
    /**
     * 상세 데이터 시트를 생성합니다.
     */
    private void createDetailedDataSheet(Workbook workbook, ComparisonRunner.ComparisonResult result,
                                       CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle) {
        Sheet sheet = workbook.createSheet("상세 데이터 (Detailed Data)");
        int rowNum = 0;
        
        for (StatisticalAnalyzer.AnalysisResult algorithmResult : result.getAllResults()) {
            // 알고리즘명
            Row algNameRow = sheet.createRow(rowNum++);
            Cell algNameCell = algNameRow.createCell(0);
            algNameCell.setCellValue(algorithmResult.getAlgorithmName() + " 상세 분석");
            algNameCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 0, 3));
            
            // 기본 통계
            Row avgUniformityRow = sheet.createRow(rowNum++);
            avgUniformityRow.createCell(0).setCellValue("평균 균등성:");
            Cell avgUniformityCell = avgUniformityRow.createCell(1);
            avgUniformityCell.setCellValue(algorithmResult.getAverageUniformity());
            avgUniformityCell.setCellStyle(numberStyle);
            
            Row stabilityRow = sheet.createRow(rowNum++);
            stabilityRow.createCell(0).setCellValue("결과 안정성:");
            Cell stabilityCell = stabilityRow.createCell(1);
            stabilityCell.setCellValue(algorithmResult.getResultStability());
            stabilityCell.setCellStyle(numberStyle);
            
            Row chiSquareRow = sheet.createRow(rowNum++);
            chiSquareRow.createCell(0).setCellValue("카이제곱 통계량:");
            Cell chiSquareCell = chiSquareRow.createCell(1);
            chiSquareCell.setCellValue(algorithmResult.getChiSquareStatistic());
            chiSquareCell.setCellStyle(numberStyle);
            
            Row uniformTestRow = sheet.createRow(rowNum++);
            uniformTestRow.createCell(0).setCellValue("균등성 검정:");
            uniformTestRow.createCell(1).setCellValue(algorithmResult.isUniform() ? "통과" : "실패");
            
            Row executionTimeRow = sheet.createRow(rowNum++);
            executionTimeRow.createCell(0).setCellValue("실행 시간 (초):");
            Cell executionTimeCell = executionTimeRow.createCell(1);
            executionTimeCell.setCellValue(algorithmResult.getExecutionTimeNanos() / 1_000_000_000.0);
            executionTimeCell.setCellStyle(numberStyle);
            
            // 각 테스트별 표준편차
            rowNum++;
            Row testHeaderRow = sheet.createRow(rowNum++);
            testHeaderRow.createCell(0).setCellValue("테스트 차수");
            testHeaderRow.createCell(1).setCellValue("표준편차");
            testHeaderRow.getCell(0).setCellStyle(headerStyle);
            testHeaderRow.getCell(1).setCellStyle(headerStyle);
            
            double[] stdevs = algorithmResult.getStandardDeviations();
            for (int i = 0; i < stdevs.length; i++) {
                Row testRow = sheet.createRow(rowNum++);
                testRow.createCell(0).setCellValue("테스트 " + (i + 1));
                Cell stdevCell = testRow.createCell(1);
                stdevCell.setCellValue(stdevs[i]);
                stdevCell.setCellStyle(numberStyle);
            }
            
            rowNum += 2; // 알고리즘 간 구분을 위한 빈 행
        }
        
        // 컬럼 너비 자동 조정
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * 비교 분석 시트를 생성합니다.
     */
    private void createComparisonSheet(Workbook workbook, ComparisonRunner.ComparisonResult result,
                                     CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle) {
        Sheet sheet = workbook.createSheet("비교 분석 (Comparison)");
        int rowNum = 0;
        
        // 제목
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue("알고리즘 성능 비교 분석");
        titleRow.getCell(0).setCellStyle(headerStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));
        
        rowNum++; // 빈 행
        
        // 최고 성능 알고리즘과의 비교
        StatisticalAnalyzer.AnalysisResult winner = result.getWinningAlgorithm();
        if (winner != null) {
            Row bestRow = sheet.createRow(rowNum++);
            bestRow.createCell(0).setCellValue("최고 성능:");
            bestRow.createCell(1).setCellValue(winner.getAlgorithmName());
            bestRow.getCell(1).setCellStyle(headerStyle);
            
            rowNum++; // 빈 행
            
            // 다른 알고리즘들과의 성능 차이
            Row compHeaderRow = sheet.createRow(rowNum++);
            String[] compHeaders = {"알고리즘", "균등성 차이 (%)", "안정성 차이 (%)", "종합 점수 차이"};
            for (int i = 0; i < compHeaders.length; i++) {
                Cell cell = compHeaderRow.createCell(i);
                cell.setCellValue(compHeaders[i]);
                cell.setCellStyle(headerStyle);
            }
            
            for (StatisticalAnalyzer.AnalysisResult algorithmResult : result.getAllResults()) {
                if (!algorithmResult.getAlgorithmName().equals(winner.getAlgorithmName())) {
                    Row compRow = sheet.createRow(rowNum++);
                    
                    compRow.createCell(0).setCellValue(algorithmResult.getAlgorithmName());
                    
                    // 균등성 차이 (%)
                    double uniformityDiff = ((algorithmResult.getAverageUniformity() - winner.getAverageUniformity()) / winner.getAverageUniformity()) * 100;
                    Cell uniformityDiffCell = compRow.createCell(1);
                    uniformityDiffCell.setCellValue(uniformityDiff);
                    uniformityDiffCell.setCellStyle(numberStyle);
                    
                    // 안정성 차이 (%)
                    double stabilityDiff = ((algorithmResult.getResultStability() - winner.getResultStability()) / winner.getResultStability()) * 100;
                    Cell stabilityDiffCell = compRow.createCell(2);
                    stabilityDiffCell.setCellValue(stabilityDiff);
                    stabilityDiffCell.setCellStyle(numberStyle);
                    
                    // 종합 점수 차이
                    double winnerTotal = winner.getAverageUniformity() + winner.getResultStability();
                    double currentTotal = algorithmResult.getAverageUniformity() + algorithmResult.getResultStability();
                    Cell totalDiffCell = compRow.createCell(3);
                    totalDiffCell.setCellValue(currentTotal - winnerTotal);
                    totalDiffCell.setCellStyle(numberStyle);
                }
            }
        }
        
        // 컬럼 너비 자동 조정
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * 통계 검정 시트를 생성합니다.
     */
    private void createStatisticalTestSheet(Workbook workbook, ComparisonRunner.ComparisonResult result,
                                          CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle) {
        Sheet sheet = workbook.createSheet("통계 검정 (Statistical Tests)");
        int rowNum = 0;
        
        // 제목
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue("카이제곱 균등성 검정 결과");
        titleRow.getCell(0).setCellStyle(headerStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));
        
        rowNum++; // 빈 행
        
        // 검정 조건
        Row conditionRow1 = sheet.createRow(rowNum++);
        conditionRow1.createCell(0).setCellValue("유의수준 (α):");
        conditionRow1.createCell(1).setCellValue("0.05");
        
        Row conditionRow2 = sheet.createRow(rowNum++);
        conditionRow2.createCell(0).setCellValue("자유도:");
        conditionRow2.createCell(1).setCellValue("32");
        
        Row conditionRow3 = sheet.createRow(rowNum++);
        conditionRow3.createCell(0).setCellValue("임계값:");
        conditionRow3.createCell(1).setCellValue("50.892");
        
        rowNum++; // 빈 행
        
        // 검정 결과 테이블
        Row testHeaderRow = sheet.createRow(rowNum++);
        String[] testHeaders = {"알고리즘", "카이제곱 통계량", "임계값", "검정 결과"};
        for (int i = 0; i < testHeaders.length; i++) {
            Cell cell = testHeaderRow.createCell(i);
            cell.setCellValue(testHeaders[i]);
            cell.setCellStyle(headerStyle);
        }
        
        double criticalValue = 50.892;
        for (StatisticalAnalyzer.AnalysisResult algorithmResult : result.getAllResults()) {
            Row testRow = sheet.createRow(rowNum++);
            
            testRow.createCell(0).setCellValue(algorithmResult.getAlgorithmName());
            
            Cell chiSquareCell = testRow.createCell(1);
            chiSquareCell.setCellValue(algorithmResult.getChiSquareStatistic());
            chiSquareCell.setCellStyle(numberStyle);
            
            Cell criticalCell = testRow.createCell(2);
            criticalCell.setCellValue(criticalValue);
            criticalCell.setCellStyle(numberStyle);
            
            String testResult = algorithmResult.getChiSquareStatistic() <= criticalValue ? "균등성 인정" : "균등성 기각";
            testRow.createCell(3).setCellValue(testResult);
        }
        
        // 컬럼 너비 자동 조정
        for (int i = 0; i < testHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * 헤더 스타일을 생성합니다.
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        
        return style;
    }
    
    /**
     * 데이터 스타일을 생성합니다.
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    /**
     * 숫자 데이터 스타일을 생성합니다.
     */
    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.0000"));
        return style;
    }
    
    /**
     * 제목 스타일을 생성합니다.
     */
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }
    
    /**
     * 성능 점수에 따른 등급을 반환합니다.
     */
    private String getPerformanceGrade(double totalScore) {
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
}