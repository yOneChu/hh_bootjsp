package com.kyhslam.mlb;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.MLBCommonUtil;
import com.kyhslam.util.ProductCommonUtil;
import com.kyhslam.util.SubaeCommonUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class find_ProductDownBOM {


    /**
     * 제품번호로 celing assy의 하위 bom 추출
     * 모-자 / 자-자
     * @param args
     */
    
    public static void main(String[] args) {


        String filePath = "D:\\Downloads\\ISOLATION ASSY 추가 현장 LIST 1.xlsx";

        ArrayList<PartInfoDTO> rowList = new ArrayList<>();

        ArrayList<String> partNoList = new ArrayList<>();

        HashMap<String, String> productNameMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트

            int rowCnt = sheet.getPhysicalNumberOfRows();

            System.out.println("rowCnt = " + rowCnt);



            for (Row row : sheet) {
                Cell cell00 = row.getCell(0); // 호기번호
                Cell cell03 = row.getCell(3); // PARTNO
                if (cell03 != null) {
                    String value00 = getCellValueAsString(cell00);
                    String value01 = getCellValueAsString(cell03);
                    //System.out.println(value);
                    productNameMap.put(value00, value01);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        //1.제품의 oid 찾기
        //SubaeCommonUtil.findProductALLInfo
        for (Map.Entry<String, String> entry : productNameMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value);

            ArrayList<ProductDto> productBOM = ProductCommonUtil.findProductInfo(key);
            System.out.println("productBOM = " + productBOM.size());

            // 제품의 1레벨 BOM조회
            for(int i=0; i < productBOM.size();i++){
                ProductDto parentDto = productBOM.get(i);
                String partNo = parentDto.getPartNo();
                String partOID = parentDto.getPartNoOID();
                //System.out.println("partNo = " + partNo);


                // 일치 (CELLING ASSY 찾기)
                if(partNo.equals(value)){

                    //CEILING ASSY
                    PartInfoDTO cilingPartAssy =  new PartInfoDTO();

                    cilingPartAssy.setParentPartNo(parentDto.getPartNo());
                    cilingPartAssy.setParentBlockNo(parentDto.getBlockNo());
                    cilingPartAssy.setParentPartName(parentDto.getPartName());
                    cilingPartAssy.setParentSize(parentDto.getPart_size());

                    //2. 하위추출
                    ArrayList<PartInfoDTO> part_DownBOM= MLBCommonUtil.findDownLevelBOM(partOID);

                    PartInfoDTO downPartDto = new PartInfoDTO();

                    // ASSY의 하위가 있다.
                    if (part_DownBOM != null && part_DownBOM.size() > 0) {
                        for (int j = 0; j < part_DownBOM.size(); j++) {
                            PartInfoDTO vDto = part_DownBOM.get(j);

                            String vLevel =  vDto.getLevel();
                            String vPartNo = vDto.getPartNo();
                            String vPartName = vDto.getPartName();
                            String vBlockNo = vDto.getBlockNo();
                            String vQty = vDto.getQty();
                            String vGLCODE = vDto.getGlCode();
                            String vCmt = vDto.getCmt();
                            String vSize = vDto.getPartSize();
                            String vSpec = vDto.getSpec();


                            if("1".equals(vLevel)){

                                // 상위부품 백업
                                downPartDto.setParentPartNo(vPartNo);
                                downPartDto.setParentPartName(vPartName);
                                downPartDto.setParentBlockNo(vBlockNo);
                                downPartDto.setParentGLCode(vGLCODE);
                                downPartDto.setParentQty(vQty);
                                downPartDto.setParentCmt(vCmt);
                                downPartDto.setParentSize(vSize);
                                downPartDto.setParentSpec(vDto.getSpec());

                                cilingPartAssy.setPartNo(vPartNo);
                                cilingPartAssy.setBlockNo(vBlockNo);
                                cilingPartAssy.setPartName(vPartName);
                                cilingPartAssy.setSpec(vSpec);
                                cilingPartAssy.setPartSize(vSize);
                                cilingPartAssy.setQty(vQty);
                                cilingPartAssy.setCmt(vCmt);


                                rowList.add(cilingPartAssy);
                                System.out.println(cilingPartAssy.getPartNo() + " -> " + vLevel + "::" + vPartNo);
                            } else {

                                //2레벨
                                /*downPartDto.setPartNo(vPartNo);
                                downPartDto.setPartName(vPartName);
                                downPartDto.setBlockNo(vBlockNo);
                                downPartDto.setGlCode(vGLCODE);
                                downPartDto.setQty(vQty);
                                downPartDto.setCmt(vCmt);
                                downPartDto.setPartSize(vSize);
                                downPartDto.setSpec(vDto.getSpec());0

                                rowList.add(downPartDto);
                                System.out.println(downPartDto.getParentPartNo() + " -> " + vLevel + "::" + vPartNo);*/
                            }
                        }
                    }

                }


            }


        }



        writeExcelFile(rowList);


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
        //headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
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
        try (FileOutputStream fileOut = new FileOutputStream("C:\\excel\\AssyFile22_20251105.xlsx")) {
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
