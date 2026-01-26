package com.kyhslam.excel;

import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.ProductCommonUtil;
import com.kyhslam.util.SubaeCommonUtil;
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

public class 엑셀읽고수정 {


    public static void main(String[] args) {

        readExcel();

    }


    public static void readExcel() {
        StopWatch sw = new StopWatch();
        sw.start();

        //String fileName = "D:\\C자재번호 단가 없는거 (20250808).xlsx";
        String fileName = "C:\\excel\\SUBAE_202512.xlsx";

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;


        HashMap<String, String> hogiKeyMap = new HashMap<>();


        try {
            // 1. 엑셀 파일 읽기
            //FileInputStream  fis = new FileInputStream(fileName);


            //FileInputStream file = new FileInputStream(new File(fileName));

            //Workbook workbook = WorkbookFactory.create(file);


            // 1. 엑셀 파일 읽기
            fis = new FileInputStream(fileName);
            workbook = new XSSFWorkbook(fis);


            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            System.out.println("rowCnt = " + rowCnt);


            for (int i = 90000; i < 140000; i++) {
                Row row = sheet.getRow(i);

                Cell cell01 = row.getCell(0); //제품번호
                String hogi = cell01.getStringCellValue();

                //System.out.println((i) + " > hogi = " + hogi);
                String hogiVer = row.getCell(1).getStringCellValue();
                int pHogiVer = Integer.parseInt(hogiVer);
                String partNo = row.getCell(3).getStringCellValue();
                String cmt = row.getCell(10).getStringCellValue();
                String uCheck = row.getCell(11).getStringCellValue();

                String modFlag = "";

                if(uCheck == null || "".equals(uCheck)){
                    continue;
                }

                if(cmt == null || "".equals(cmt)){
                    continue;
                }


                String findKey = "";

                //이전버전의 제품 oid 찾기
                String curProductOID = "";
                if(!hogiVer.equals("0")) {
                    int beforeVersion = Integer.parseInt(hogiVer) - 1;

                    findKey = hogi + beforeVersion;

                    if (hogiKeyMap.containsKey(findKey)) {

                        curProductOID = hogiKeyMap.get(findKey);

                    } else {
                        //제품번호로 모든 버전의 제품 정보 가져오기
                        ArrayList<ProductDto> productInfoList = SubaeCommonUtil.findProductOIDS(hogi);

                        for (int j = 0; j < productInfoList.size(); j++) {
                            ProductDto dto = productInfoList.get(j);
                            int dtoVer = Integer.parseInt(dto.getProductVersion());


                            if(beforeVersion == dtoVer) {
                                curProductOID = dto.getProductOid();

                                hogiKeyMap.put((hogi + beforeVersion), curProductOID);
                            }
                        }
                    }



                }

                //제품 oid의 하위 bom 조회
                if(curProductOID != null && !curProductOID.equals("")) {

                    //if(hogiKeyMap.contai)

                    ArrayList<ProductDto> bomList = ProductCommonUtil.findProductBOMWithOID(curProductOID);

                    for(int k=0; k < bomList.size(); k++) {
                        ProductDto bDto = bomList.get(k);

                        String vPartNo = bDto.getPartNo();

                        if (vPartNo.equals(partNo)) {
                            String vCmt = bDto.getCmt();
                            //System.out.println(vCmt);

                            if (cmt.trim().equals(vCmt.trim())) {
                                //System.out.println((i) + " > " + hogi + " > 동일");
                                //System.out.println(cmt.trim() + " > 동일 > " + vCmt.trim());
                                modFlag = "동일";
                                row.createCell(8).setCellValue(modFlag);
                                row.createCell(9).setCellValue(vCmt); // 이전주석
                            } else {
                                //System.out.println(hogi + " > 다름 > " + vCmt);
                                row.createCell(8).setCellValue(modFlag);
                                row.createCell(9).setCellValue(vCmt); // 이전주석
                            }

                        }



                    }

                }





            }
            fis.close();
            fos = new FileOutputStream(fileName);
            workbook.write(fos);
            //fos = new FileOutputStream(filePath);
            //workbook.write(fos);
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
