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

public class subae_LogicPercentage {

    public static void main(String[] args) {
        readExcel();
    }

    public static void readExcel() {
        StopWatch sw = new StopWatch();
        sw.start();

        //String fileName = "D:\\C자재번호 단가 없는거 (20250808).xlsx";
        String fileName = "C:\\excel\\SUBAE_202510.xlsx";

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;


        HashMap<String, ArrayList<ProductDto>> hogiKeyMap = new HashMap<>();


        try {
            // 1. 엑셀 파일 읽기
            fis = new FileInputStream(fileName);
            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();
            System.out.println("rowCnt = " + rowCnt);

            //110000
            for (int i =1; i < 1000; i++) {
                Row row = sheet.getRow(i);

                Cell cell01 = row.getCell(0); //제품번호
                String hogi = cell01.getStringCellValue();

                if(hogi == null || hogi.equals("")){
                    continue;
                } else {
                    hogi = hogi.trim();
                }

                //System.out.println((i) + " > hogi = " + hogi);
                String hogiVer = row.getCell(1).getStringCellValue();
                int pHogiVer = Integer.parseInt(hogiVer);
                String partNo = row.getCell(3).getStringCellValue();
                String qty = row.getCell(6).getStringCellValue();

                String cmt = "";
                cmt = row.getCell(10).getStringCellValue();
                String uCheck = row.getCell(11).getStringCellValue();
                System.out.println((i) + " > " + hogi + " : " + partNo);
                String modFlag = "";

                if(uCheck == null || "".equals(uCheck)){
                    continue;
                }

                if(cmt == null || "".equals(cmt)){
                    //continue;
                    cmt = "";
                }

                //이전버전의 제품 oid 찾기
                String curProductOID = "";
                if(!hogiVer.equals("0")) {

                    //ArrayList<ProductDto> productInfoList = SubaeCommonUtil.findProductOIDS(hogi);
                    ArrayList<ProductDto> productInfoList = new ArrayList<>();

                    //제품번호로 모든 버전의 제품 정보 가져오기
                    if(hogiKeyMap.containsKey(hogi)) {
                        //productInfoList = SubaeCommonUtil.findProductOIDS(hogi);
                        productInfoList = hogiKeyMap.get(hogi);
                    } else {
                        productInfoList = SubaeCommonUtil.findProductOIDS(hogi);
                        hogiKeyMap.put(hogi,productInfoList);

                    }

                    for (int j = 0; j < productInfoList.size(); j++) {
                        ProductDto productDto = productInfoList.get(j);
                        int dtoVer = Integer.parseInt(productDto.getProductVersion());
                        String dtoOid = productDto.getProductOid();

                        //현 엑셀의 버전보다 작으면 비교 수행
                        if(pHogiVer > dtoVer){

                            //ArrayList<ProductDto> bomList = ProductCommonUtil.findProductBOMWithOID(dtoOid);
                            ArrayList<ProductDto> bomList = ProductCommonUtil.findProductBOMWithOID_partNo(dtoOid, partNo.trim());

                            //0버전부터 비교 수행
                            for(int k=0; k < bomList.size(); k++) {
                                ProductDto partDto = bomList.get(k);

                                String vPartNo = partDto.getPartNo();
                                String vQty = partDto.getQty();

                                boolean diffFlag = false;

                                if (vPartNo.equals(partNo)) {
                                    String partCmt = "";
                                    partCmt = partDto.getCmt();

                                    if( !vQty.equals(qty) ) {
                                        //System.out.println("수량다름");
                                        continue;
                                    }

                                    if(partCmt != null) {
                                        partCmt = partCmt.trim();
                                    }
                                    //System.out.println(dtoVer + " > " + partCmt);

                                    //if(partCmt != null) {
                                    if (cmt.trim().equals(partCmt.trim())) {
                                        //System.out.println((i) + " > " + hogi + " n> 동일");
                                        //System.out.println(cmt.trim() + " > 동일 > " + vCmt.trim());
                                        //modFlag = "동일";
                                        //row.createCell(8).setCellValue(modFlag);
                                        //row.createCell(9).setCellValue(partCmt); // 이전주석
                                    } else {
                                        System.out.println(hogi + " > 다름 > ");
                                        System.out.println("현재 > " + cmt);
                                        System.out.println("이전 > " + partCmt);
                                        row.createCell(8).setCellValue("다름");
                                        row.createCell(9).setCellValue(partCmt); // 이전주석
                                        diffFlag = true;
                                    }
                                    //}
                                }

                                //if(diffFlag) continue;
                            }
                        }
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
