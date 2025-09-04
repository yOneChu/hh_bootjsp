package com.kyhslam.mlb;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.MLBCommonUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class findAssyPart_02 {


    /**
     * 선우꺼
     * 1. 엑셀의 partno 읽기
     * 2. partNo로 oid 찾기
     * 3. oid로 하위 자재 챚아서 모 > 자 로 출력
     * @param args
     */
    
    public static void main(String[] args) {


        StopWatch sw = new StopWatch();
        sw.start();


        String filePath = "C:\\Users\\Administrator\\Downloads\\강판류 도어 전수 조사(비방화).xlsx"; // 읽을 파일 경로

        ArrayList<String> partNoList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(7); // 첫 번째 시트

            int rowCnt = sheet.getPhysicalNumberOfRows();

            System.out.println("rowCnt = " + rowCnt);

            for (Row row : sheet) {
                Cell cell = row.getCell(0); // A열(첫 번째 열)
                if (cell != null) {
                    String value = getCellValueAsString(cell);
                    //System.out.println(value);
                    partNoList.add(value);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        ArrayList<PartInfoDTO> dtoList = new ArrayList<>();

        // 1레벨 부품 OID 조회
        for (int i = 0; i < 100; i++) {
            String partNo = partNoList.get(i);

            ArrayList<PartInfoDTO> rList = MLBCommonUtil.findPartWithPartNo(partNo.trim());

            for (PartInfoDTO dd : rList) {
                //System.out.println(dd);
                dtoList.add(dd);
            }
        }

        // 부품하위 애들 검사
        for(int i=0; i < dtoList.size(); i++){
            PartInfoDTO dto = dtoList.get(i);
            String oid = dto.getOid();
            String parentPartNo = dto.getPartNo();
            String parentPartName = dto.getPartName();

            ArrayList<PartInfoDTO> data = new  ArrayList<>();

            MLBCommonUtil.findDownLevel(oid, data);

            if (data.size() > 0) {
                for (PartInfoDTO datum : data) {
                    System.out.println(parentPartNo + " > " + datum.getPartNo() + " > " +  datum.getPartName());
                }
            }


        }



/*

        // partNo로 부품 oid 조회
        ArrayList<String> oids = MLBCommonUtil.findPartWithPartNo("20250903");

        System.out.println("oids = " + oids.size());


        ArrayList<String> data =  new ArrayList<>();

        for (int i = 0; i < oids.size(); i++) {
            System.out.println(i + " = " + oids.get(i));
            String oid = oids.get(i);
            MLBCommonUtil.findDownLevel(oid, data);
        }
*/

            sw.stop();


        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }


    private static String getCellValueAsString(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }
}
