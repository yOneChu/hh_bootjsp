package com.kyhslam.excel;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.MLBCommonUtil;
import com.kyhslam.util.PartCommonUtil;
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
import java.util.HashMap;

public class export_PartInfo {

    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();


        ArrayList<String> partNoList = new ArrayList<>();

        //String fileName = "d:\\excel\\BOM원복리스트_2차분_102대_20260421.xlsx";
        String fileName = "d:\\excel\\SPEC_LIST.xlsx";

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;


        try {



            // 1. 엑셀 파일 읽기
            fis = new FileInputStream(fileName);
            workbook = new XSSFWorkbook(fis);


            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            for (int i = 0; i < rowCnt; i++) {
                Row row = sheet.getRow(i);

                Cell cell0 = row.getCell(0);
                String partNo = cell0.getStringCellValue();

                PartInfoDTO partInfo = MLBCommonUtil.findPartOneWithPartNo(partNo);

                System.out.println((i) + " -> partInfo  " + partNo);
                String vPartSpec = partInfo.getSpec();
                String PART_STATUS = partInfo.getStatus();
                String BLOCKNO = partInfo.getBlockNo();
                String partName = partInfo.getPartName();

                row.createCell(1).setCellValue(partName);
                row.createCell(2).setCellValue(vPartSpec);
                row.createCell(3).setCellValue(PART_STATUS);
                row.createCell(4).setCellValue(BLOCKNO);
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
