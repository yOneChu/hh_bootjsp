package com.kyhslam.excel;

import com.kyhslam.util.PIDCommonUtil;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class excel_01 {

    /**
     * 법인자재에 대해서 단가 미책정된거 수배로직에 있나 검사
     * @param args
     */
    public static void main(String[] args) {


        String fileName = "D:\\C자재번호 단가 없는거 (20250808).xlsx";


        //FIELD = VAL

        ArrayList<String> cPartNoList = new ArrayList<>();

        try {

            FileInputStream file = new FileInputStream(new File(fileName));

            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            System.out.println("rowCnt = " + rowCnt);


            for (int i = 0; i < rowCnt; i++) {
                Row row = sheet.getRow(i);

                Cell cell03 = row.getCell(3);
                String partNo = cell03.getStringCellValue();

                if(partNo != null && partNo.length()>0){
                    cPartNoList.add(partNo.trim());
                }


                Cell cell05 = row.getCell(5);
                String partName = cell05.getStringCellValue();

                Cell cell08 = row.getCell(8);
                String dwgNo = cell08.getStringCellValue();

                //System.out.println(partNo + " > " +  partName + " > " + dwgNo);


            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }


        for (int i = 0; i < cPartNoList.size(); i++) {
            String pid = cPartNoList.get(i);
            String FIELD = "VAL";


            ArrayList<HashMap<String, String>> result = new ArrayList<>();
            result = PIDCommonUtil.findPIDDetail(pid, FIELD, "LIKE", "", "", "", "", "", "", "", "");

            if (result.size() > 0) {
                System.out.println(pid + " > " + result.size());
            }

        }


        System.out.println(" ---------------------- END ---------------------- ");
    }
}
