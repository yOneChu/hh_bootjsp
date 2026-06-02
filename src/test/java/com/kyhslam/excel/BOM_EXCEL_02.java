package com.kyhslam.excel;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
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

public class BOM_EXCEL_02 {

    /**
     * todo: 엑셀의 호기번호에 해당하는 모든 BOM 추출하여 다른시트에 생성하여 입력
     * @param args
     */

    public static void main(String[] args) {


        StopWatch sw = new StopWatch();
        sw.start();


        ArrayList<String> hogiList = new ArrayList<>();

        //String fileName = "d:\\excel\\BOM원복리스트_2차분_102대_20260421.xlsx";
        String fileName = "c:\\excel\\TEST.xlsx";

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;


        try {

            // 1. 엑셀 파일 읽기
            fis = new FileInputStream(fileName);
            workbook = new XSSFWorkbook(fis);


            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            for (int i = 1; i < rowCnt; i++) {
                Row row = sheet.getRow(i);

                Cell cell0 = row.getCell(1);
                String hogi = cell0.getStringCellValue();

                if( !hogiList.contains(hogi) ){
                    hogiList.add(hogi);
                    System.out.println("hogi = " + hogi);
                }
            }

            System.out.println("hogiList.size() = " + hogiList.size());

            writeBOM(workbook, hogiList);

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


    public static void writeBOM(Workbook workbook, ArrayList<String> hogiList) {

        try {

            //Sheet sheet = workbook.createSheet("BOM_List");
            Sheet sheet = workbook.createSheet("BOM_List");

            int rowCnt = 1;

            for(int i=0; i < hogiList.size(); i++){

                String hogi = hogiList.get(i);

                PartInfoDTO dto = ProductCommonUtil.findProductInfoAsBlockNo(hogi.trim(), "B182G03");


                String partNo = dto.getPartNo();
                String partName = dto.getPartName();
                String nation = dto.getNation();
                String blockNo = dto.getBlockNo();
                String spec =  dto.getSpec();
                String qty = dto.getQty();
                String ucheck = dto.getUCheck();
                String cmt =  dto.getCmt();



                Row row = sheet.createRow(rowCnt);
                row.createCell(1).setCellValue(hogi);
                row.createCell(2).setCellValue(partNo);
                row.createCell(3).setCellValue(partName);
                row.createCell(4).setCellValue(nation);
                row.createCell(5).setCellValue(blockNo);
                row.createCell(6).setCellValue(spec);
                row.createCell(7).setCellValue(qty);
                row.createCell(8).setCellValue(ucheck);
                row.createCell(9).setCellValue(cmt);

                rowCnt++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
