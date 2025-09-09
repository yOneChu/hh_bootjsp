package com.kyhslam.mlb;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.MLBCommonUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class findAssyPart_03 {


    /**
     * 재훈이꺼
     * 1. 엑셀의 partno 읽기
     * 2. partNo로 oid 찾기
     * 3. oid로 하위 자재 챚아서 모 > 자 로 출력
     * @param args
     */
    
    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();

        //ArrayList<PartInfoDTO> dtoList = new ArrayList<>();

        // 1레벨 부품 OID 조회
        // 조건에 AND A.BLOCKNO_NUMBER = 'D375A' 추가해야됨
        ArrayList<PartInfoDTO> dtoList = MLBCommonUtil.findPartWithYear_V2("2025");

        System.out.println("dtoList.size() -------- " + dtoList.size());

        ArrayList<PartInfoDTO> resultList = new ArrayList<>();

        // 부품하위 애들 검사
        for(int i=0; i < dtoList.size(); i++){
            PartInfoDTO parentDto = dtoList.get(i);
            String oid = parentDto.getOid();

            // 하위레벨 조회
            //MLBCommonUtil.findDownLevel(oid, resultList, parentDto);
            //MLBCommonUtil.findDownLevelQTY_CE(oid, resultList, parentDto);

        }

        System.out.println("---- writeExcel Run -----");

        //writeExcelFile(resultList);


        sw.stop();


        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }


    private static String getCellValueAsString(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    //엑셀 추출
    private static void writeExcelFile(ArrayList<PartInfoDTO> dataList) {
        Workbook workbook = new XSSFWorkbook();

        // 시트 생성
        Sheet sheet = workbook.createSheet("결과");


        // 스타일
        CellStyle headerStyle = workbook.createCellStyle();

        // 배경색 (연한 회색)
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 테두리 설정
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // 정렬 설정 (가운데 정렬)
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 폰트 설정 (굵은 글씨 + 크기 조절)
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setFontName("맑은 고딕");
        headerStyle.setFont(headerFont);

        Row header = sheet.createRow(0);
        String[] titles = { "모 자재번호", "BlockNo", "자재명", "SIZE", "자 자재번호", "BlockNo", "자재명", "SPEC", "SIZE", "QTY", "CMT"
        };
        for (int i = 0; i < titles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        //CellRangeAddress(시작행, 끝행, 시작열, 끝열)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));


        for(int i=0; i < dataList.size(); i++){
            PartInfoDTO dto = dataList.get(i);

            Row headerRow = sheet.createRow((i+1));

            headerRow.createCell(0).setCellValue(dto.getParentPartNo());
            headerRow.createCell(1).setCellValue(dto.getParentBlockNo());
            headerRow.createCell(2).setCellValue(dto.getParentPartName());
            headerRow.createCell(3).setCellValue(dto.getParentSize());


            headerRow.createCell(4).setCellValue(dto.getPartNo());
            headerRow.createCell(5).setCellValue(dto.getBlockNo());
            headerRow.createCell(6).setCellValue(dto.getPartName());
            headerRow.createCell(7).setCellValue(dto.getSpec());
            headerRow.createCell(8).setCellValue(dto.getPartSize());
            headerRow.createCell(9).setCellValue(dto.getQty());
            headerRow.createCell(10).setCellValue(dto.getCmt());
        }



        // 자동 열 너비 조정
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }

        // 파일 저장
        try (FileOutputStream fileOut = new FileOutputStream("C:\\excel\\AssyFile_20250909.xlsx")) {
            workbook.write(fileOut);
            System.out.println("Excel 파일 생성 완료!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 리소스 해제
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(" ---------- end ----------- ");

    }




}
