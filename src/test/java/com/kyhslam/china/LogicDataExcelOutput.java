package com.kyhslam.china;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.ChinaCommonUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LogicDataExcelOutput {



    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();


        HashMap<String, HashMap<String, String>> blockMap = ChinaCommonUtil.initBlockNo("");

        //모든 블럭의 정보 셋팅
        String[] headers = {"PartNo", "PartName", "Version", "ENAME", "CNAME", "UOM", "BlockNo", "GLCode", "Part_Size", "SPEC"
                , "SPEC1", "CON1", "SPEC2", "CON2", "SPEC3", "CON3", "SPEC4", "CON4", "SPEC5", "CON5", "SPEC6", "CON6", "SPEC7", "CON7", "SPEC8", "CON8", "SPEC9", "CON9", "SPEC10", "CON10"
                , "SPEC11", "CON11", "SPEC12", "CON12", "SPEC13", "CON13", "SPEC14", "CON14", "SPEC15", "CON15", "SPEC16", "CON16", "SPEC17", "CON17", "SPEC18", "CON18", "SPEC19", "CON19", "SPEC20", "CON20"};


        //writeExcelFile(headers, );

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }


    //
    private static void writeExcelFile(String[] headers, ArrayList<PartInfoDTO> dataList, String year) {
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
        String[] titles = { "모 자재번호", "BlockNo", "모 자재명", "모 버전", "자 LEVEL", "자 자재번호", "자 버전", "BlockNo", "자재명", "SPEC", "SIZE", "QTY", "CMT" };

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

            //headerRow.createCell(0).setCellValue(dto.getParentLevel());
            headerRow.createCell(0).setCellValue(dto.getParentPartNo());
            headerRow.createCell(1).setCellValue(dto.getParentBlockNo());
            headerRow.createCell(2).setCellValue(dto.getParentPartName());
            headerRow.createCell(3).setCellValue(dto.getParentVersion());
            //headerRow.createCell(5).setCellValue(dto.getParentSize());


            headerRow.createCell(4).setCellValue(dto.getLevel());
            headerRow.createCell(5).setCellValue(dto.getPartNo());
            headerRow.createCell(6).setCellValue(dto.getVersion());
            headerRow.createCell(7).setCellValue(dto.getBlockNo());
            headerRow.createCell(8).setCellValue(dto.getPartName());
            headerRow.createCell(9).setCellValue(dto.getSpec());
            headerRow.createCell(10).setCellValue(dto.getPartSize());
            headerRow.createCell(11).setCellValue(dto.getQty());
            headerRow.createCell(12).setCellValue(dto.getCmt());
        }

        // 자동 열 너비 조정
        for (int i = 0; i < 12; i++) {
            sheet.autoSizeColumn(i);
        }

        String filePath = "C:\\excel\\D375A_QTY_";
        filePath += year + ".xlsx";


        // 파일 저장
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
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
