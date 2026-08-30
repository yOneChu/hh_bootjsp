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
import java.util.HashMap;

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
        String fileName = "c:\\excel\\호기리스트_DATA.xlsx";

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

                Cell cell0 = row.getCell(0);
                String hogi = cell0.getStringCellValue();


                Cell cell3 = row.getCell(3);
                String getPartNo = cell3.getStringCellValue();

                if (hogi.isEmpty()) {
                    break;
                }

                //HashMap<String, String> dto = ProductCommonUtil.getProductInfo(hogi.trim());

                ArrayList<ProductDto> dtoList = ProductCommonUtil.findProductInfo(hogi.trim());

                for(int k=0; k < dtoList.size(); k++) {
                    ProductDto dto = dtoList.get(k);

                    String partNo = dto.getPartNo();

                    if(getPartNo.trim().equals(partNo)) {
                        row.createCell(7).setCellValue(dto.getUsername());
                        row.createCell(8).setCellValue(dto.getProductCreDate());
                        row.createCell(9).setCellValue(dto.getProductModDate());
                    }
                }

//
  //              hogiList.add(hogi);
    //            System.out.println("hogi = " + hogi);

            }

            //System.out.println("hogiList.size() = " + hogiList.size());

            //writeBOM(workbook,sheet, hogiList);

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


    public static void writeBOM(Workbook workbook, Sheet sheet, ArrayList<String> hogiList) {

        try {

            //Sheet sheet = workbook.createSheet("BOM_List");

            int rowCnt = 1;

            for(int i=0; i < hogiList.size(); i++){

                String hogi = hogiList.get(i);

                //PartInfoDTO dto = ProductCommonUtil.findProductInfoAsBlockNo(hogi.trim(), "B182G03");

                ArrayList<ProductDto> dtoList = ProductCommonUtil.findProductInfo(hogi.trim());

                for (int k = 0; k < dtoList.size(); k++) {
                    ProductDto dto = dtoList.get(i);

                    String PCREATOR = dto.getProductCreator();
                    String MDATE = dto.getProductModDate();
                    String CDATE = dto.getProductCreDate();

                }
                rowCnt++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
