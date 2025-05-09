package com.kyhslam.jqpl.Excel;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class excelRead {

    public static void main(String[] args) {



        try {

            FileInputStream file = new FileInputStream(new File("JQPL-20250131.XLSX"));

            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            System.out.println("rowCnt = " + rowCnt);

            for (int i = 1; i < rowCnt; i++) {
                Row row = sheet.getRow(i);

                Cell cell05 = row.getCell(5);
                String mUser = cell05.getStringCellValue();

                Cell cell06 = row.getCell(6);
                String eUser = cell06.getStringCellValue();

                Cell cell08 = row.getCell(8); //JQPR NO
                String jqprNo = cell08.getStringCellValue();


                Cell cell09 = row.getCell(9); //접수일
                //String receptDate = cell09.getStringCellValue();
                Date cell09Date = cell09.getDateCellValue();
                //String a = new SimpleDateFormat("yyyy-mm-dd").format(receptDate);

                String receptDate = "";
                if (cell09Date != null) {
                    receptDate =new SimpleDateFormat("yyyy-MM-dd").format(cell09Date);
                }

                Cell cell10 = row.getCell(10); //관리번호
                String manageNo = cell10.getStringCellValue();

                Cell cell11 = row.getCell(11); //프로젝트명
                String pjtName = cell11.getStringCellValue();

                Cell cell12 = row.getCell(12); //문제자재명
                String problemPart = cell12.getStringCellValue();

                Cell cell13 = row.getCell(13); //호기
                String hogi = cell13.getStringCellValue();

                Cell cell15 = row.getCell(15); //작성자
                String creator = cell15.getStringCellValue();

                Cell cell26 = row.getCell(26); //JQPR 유형
                String jqprtType = cell26.getStringCellValue();

                Cell cell31 = row.getCell(31); //고장현상
                String problemStatus = cell31.getStringCellValue();

                Cell cell32 = row.getCell(32); //고장원인
                String problemCause = cell32.getStringCellValue();

                Cell cell33 = row.getCell(33); //분류코드
                String typeCode = cell33.getStringCellValue();

                Cell cell34 = row.getCell(34); //item분류명
                String itemType = cell34.getStringCellValue();

                Cell cell35 = row.getCell(35); //자재비
                String cost01 = Integer.toString((int)cell35.getNumericCellValue());

                Cell cell36 = row.getCell(36); //노무비
                String cost02 = Integer.toString((int)cell36.getNumericCellValue());

                Cell cell37 = row.getCell(37); //실패비용
                String failCost = Integer.toString((int)cell37.getNumericCellValue());





                Cell cell49 = row.getCell(49); //기타부서명
                String etcTeamName = cell49.getStringCellValue();

                Cell cell50 = row.getCell(50); //기타부서비
                String etcTeamCost = cell50.getStringCellValue();

                System.out.println(jqprNo + " > " + receptDate + " + " + cost02 + " - " + failCost);
            }

            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }
}
