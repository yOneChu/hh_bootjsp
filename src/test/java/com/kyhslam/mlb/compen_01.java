package com.kyhslam.mlb;

import com.kyhslam.util.ElvInfoCommonUtil;
import com.kyhslam.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class compen_01 {


    public static void main(String[] args) {

        //엑셀 읽기
        String filePath = "";
        filePath = "C:\\excel\\컴팬체인 PR수량 집계(251118) - 복사본.XLSX"; // 읽을 엑셀 파일 경로

        ArrayList<String> hogiList = new ArrayList<>();

        HashMap<String, String> dataMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            //Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 읽기
            Sheet sheet = workbook.getSheet("PR로우데이터");


            //sheet.getLastRowNum()
            int cnt = 0;
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                Row row = sheet.getRow(rowIndex);

                // 각 컬럼 값 읽기
                String reqNo = ExcelUtil.getCellValue(row.getCell(0));
                String wbs = ExcelUtil.getCellValue(row.getCell(11)); // wbs

                if(wbs != null && !"".equals(wbs)) {
                    if(wbs.contains("-P")) {
                        String hogi = wbs.replaceAll("-P", "");
                        hogiList.add(hogi.trim());
                    } else {
                        hogiList.add(wbs.trim());
                    }
                } else {
                    continue;
                }

                if (hogiList.size() == 300) {
                    ElvInfoCommonUtil.findElvInfoValue(hogiList, dataMap);
                    hogiList.clear();
                }
            }

            System.out.println("dataMap = " + dataMap.size());

            ElvInfoCommonUtil.findElvInfoValue(hogiList, dataMap);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(dataMap);


        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {


            //Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 읽기
            Sheet sheet = workbook.getSheet("PR로우데이터");


            //sheet.getLastRowNum()
            for (int rowIndex = 100; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                //System.out.println("excel row == " + rowIndex);

                Row row = sheet.getRow(rowIndex);
                //if (row == null) continue;

                // 각 컬럼 값 읽기
                String reqNo = ExcelUtil.getCellValue(row.getCell(0));
                //String compenBon = ExcelUtil.getCellValue(row.getCell(7)); // 본수
                String wbs = ExcelUtil.getCellValue(row.getCell(11)); // wbs

                if(wbs==null||wbs.equals("")){
                    continue;
                }

                String hogi = "";
                String EL_DCCAQ = "";

                if(wbs.contains("-P")) {
                    hogi = wbs.replaceAll("-P", "");
                    EL_DCCAQ = dataMap.get(hogi);

                    row.createCell(7).setCellValue(EL_DCCAQ);
                } else {
                    continue;
                }

                //System.out.println(rowIndex + " :: " + reqNo + ", " +  hogi + ", " + EL_DCCAQ);

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


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }
}
