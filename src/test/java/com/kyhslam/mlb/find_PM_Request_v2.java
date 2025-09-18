package com.kyhslam.mlb;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.PLMDBConnection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class find_PM_Request_v2 {


    // COMPEN CHAIN : B189A 자재 조회
    // COMPEN CHAIN 들어간 호기의 변경 이력

    public static void main(String[] args) {

        String filePath = "C:\\excel\\COMPEN_CHAIN_2024-2025.xlsx"; // 읽을 엑셀 파일 경로


        LinkedHashMap<String, ArrayList<ProductDto>> dataMap = new LinkedHashMap<>();
        //key: 호기번호|자재번호


        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 읽기

            // 첫 번째 행(헤더)은 건너뛴다고 가정 (row 0은 헤더)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            //for (int rowIndex = 1; rowIndex <= 2000; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                // 각 컬럼 값 읽기
                String productNo = getCellValue(row.getCell(0));
                String productVersion   = getCellValue(row.getCell(1));
                String productCreDate   = getCellValue(row.getCell(2));
                String partNo = getCellValue(row.getCell(3));
                String partName   = getCellValue(row.getCell(4));
                String partVersion = getCellValue(row.getCell(5));
                String uCheck = getCellValue(row.getCell(6));
                String blockNo = getCellValue(row.getCell(7));
                String qty = getCellValue(row.getCell(8));
                String spec = getCellValue(row.getCell(9));
                String size = getCellValue(row.getCell(10));
                String cmt = getCellValue(row.getCell(11));

                ProductDto dto = new ProductDto();
                dto.setProductNo(productNo);
                dto.setProductVersion(productVersion);
                dto.setProductCreDate(productCreDate);

                dto.setPartNo(partNo);
                dto.setPartName(partName);
                dto.setVersion(partVersion);
                dto.setUcheck(uCheck);
                dto.setBlockNo(blockNo);
                dto.setQty(qty);
                dto.setSpec(spec);
                dto.setPart_size(size);
                dto.setCmt(cmt);


                // 출력 (또는 DTO에 매핑 가능)
                System.out.printf("제품번호=%s, 버전=%s, 등록일=%s, 자재번호=%s, 버전=%s%n",
                        productNo, productVersion, productCreDate, partNo, partName);


                String key = productNo + "|" + partNo;

                if(dataMap.containsKey(key)){
                    ArrayList<ProductDto> list = dataMap.get(key);
                    list.add(dto);

                } else {
                    ArrayList<ProductDto> list = new ArrayList<>();
                    list.add(dto);

                    dataMap.put(key, list);
                }
            }


            System.out.println(" ---------------------- ");

            LinkedHashMap<String, ProductDto> resultMap = new LinkedHashMap<>();

            dataMap.keySet().forEach(key -> {
                //System.out.println(key + " - " + dataMap.get(key).size() + " > " + dataMap.get(key));

                ArrayList<ProductDto> list = dataMap.get(key);
                for(int i=0; i <  list.size(); i++){
                    ProductDto dto = list.get(i);
                    String qty = dto.getQty();
                    String cmt = dto.getCmt();

                    if(resultMap.containsKey(key)){

                        ProductDto saveDto = resultMap.get(key);

                        // div 수량
                        if( !saveDto.getQty().equals(qty)){
                            String div = saveDto.getDiv();
                            saveDto.setQty(qty);
                            saveDto.setDiv(div + " > " +  qty);
                        }

                        // uom 주석
                        if( !saveDto.getCmt().equals(cmt)){
                            String uom = saveDto.getUom();
                            saveDto.setCmt(cmt);
                            saveDto.setUom(uom + " > " +  cmt);
                        }

                        resultMap.put(key, saveDto);

                    } else {
                        dto.setDiv(qty);
                        dto.setUom(cmt);
                        resultMap.put(key, dto);
                    }
                }

            });


            System.out.println(" ================== result ===============");
            resultMap.keySet().forEach(key -> {
                ProductDto dto = resultMap.get(key);

                System.out.println(key + " >>>> " + dto.getProductNo() + " : " + dto.getPartNo() + " --- " + dto.getDiv() + " || " + dto.getUom());
            });


            writeExcelFile(resultMap);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 셀 값을 문자열로 변환하는 유틸 함수
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 날짜 형식인 경우
                    return cell.getDateCellValue().toString();
                } else {
                    // 숫자인 경우
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }



    private static void writeExcelFile(LinkedHashMap<String, ProductDto> resultMap) {

        //LinkedHashMap<String, ProductDto> resultMap = new LinkedHashMap<>();


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


        // 셀 스타일 생성
        CellStyle yelloStyle = workbook.createCellStyle();

        // 배경색 지정
        yelloStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yelloStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);



        Row header = sheet.createRow(0);
        String[] titles = { "제품번호", "버전", "등록일", "자재번호", "자재명", "버전", "수정여부", "BlockNo", "QTY", "SPEC", "SIZE", "CMT"
        };
        for (int i = 0; i < titles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        //CellRangeAddress(시작행, 끝행, 시작열, 끝열)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));

        int cnt = 1;
        Iterator<String> keys = resultMap.keySet().iterator();
        while ( keys.hasNext() ) {
            String key = keys.next();
            System.out.println("방법3) key : " + key +" / value : " + resultMap.get(key));

            ProductDto dto = resultMap.get(key);


            String qtyVal = dto.getDiv();
            String cmtVal = dto.getUom();

            if(qtyVal.contains(">") || cmtVal.contains(">")){
                Row headerRow = sheet.createRow(cnt);
                //Cell cell = headerRow.createCell(0);
                //cell.setCellValue(dto.getProductNo());
                headerRow.createCell(0).setCellValue(dto.getProductNo());
                headerRow.createCell(1).setCellValue(Integer.parseInt(dto.getProductVersion()));
                headerRow.createCell(2).setCellValue(dto.getProductCreDate());

                headerRow.createCell(3).setCellValue(dto.getPartNo());
                headerRow.createCell(4).setCellValue(dto.getPartName());
                headerRow.createCell(5).setCellValue(Integer.parseInt(dto.getVersion()));
                headerRow.createCell(6).setCellValue(dto.getUcheck());
                headerRow.createCell(7).setCellValue(dto.getBlockNo());

                headerRow.createCell(8).setCellValue(dto.getDiv());
                headerRow.createCell(9).setCellValue(dto.getUom());

                cnt++;
            }

        }





        // 자동 열 너비 조정
        for (int i = 0; i < 12; i++) {
            sheet.autoSizeColumn(i);
        }

        // 파일 저장
        try (FileOutputStream fileOut = new FileOutputStream("C:\\excel\\COMPEN_CHAIN_Summary.xlsx")) {
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
