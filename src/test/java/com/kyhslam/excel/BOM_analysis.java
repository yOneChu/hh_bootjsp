package com.kyhslam.excel;

import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.ExcelUtil;
import com.kyhslam.util.ProductCommonUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class BOM_analysis {

    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();

        ArrayList<String> hogiList = new ArrayList<>();

        String fileName = "d:\\excel\\18910471_분석.xlsx";

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;

        try {
            // 1. 엑셀 파일 읽기
            fis = new FileInputStream(fileName);
            workbook = new XSSFWorkbook(fis);


            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            for (int i = 2; i < 100; i++) {
                Row row = sheet.getRow(i);

                Cell cell0 = row.getCell(0); //호기번호
                String hogi = cell0.getStringCellValue();

                String hogiVersion = ExcelUtil.getCellValue(row.getCell(1)); //호기버전
                ArrayList<ProductDto> bomList = ProductCommonUtil.findProductInfo(hogi, hogiVersion);

                for (int j = 0; j < bomList.size(); j++) {
                    ProductDto dto = bomList.get(j);
                    String vPartNo = dto.getPartNo();
                    String vPartName = dto.getPartName();
                    String vQty = dto.getQty();
                    String vBlockOpt = dto.getBlockopt();
                    String vBlockNo = dto.getBlockNo();
                    String vCmt = dto.getCmt();

                    if (vBlockNo.equals("B189C01")) {
                        row.createCell(24).setCellValue(vPartNo);
                        row.createCell(25).setCellValue(vPartName);
                        row.createCell(26).setCellValue(vQty);
                        row.createCell(27).setCellValue(vCmt);
                    }
                }

            }



            fis.close();
            fos = new FileOutputStream(fileName);
            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) workbook.close();
                if (fos != null) fos.close();
                if (fis != null) fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }
}
