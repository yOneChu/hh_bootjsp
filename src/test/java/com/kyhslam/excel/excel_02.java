package com.kyhslam.excel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.service.SubaeService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class excel_02 {

    public static void main(String[] args) {
        StopWatch sw = new StopWatch();
        sw.start();

        //String fileName = "D:\\C자재번호 단가 없는거 (20250808).xlsx";
        String fileName = "D:\\Downloads\\11.xlsx";

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;


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


            for (int i = 8000; i < 12000; i++) {
                Row row = sheet.getRow(i);

                Cell cell03 = row.getCell(1);
                String hogi = cell03.getStringCellValue();

                System.out.println((i) + " >>> hogi = " + hogi);


                HashMap<String, String> calMap = pidExecute(hogi, "CAL_DESIGN_MAT", "", "", "");

                row.createCell(12).setCellValue(calMap.get("MATERIAL_T_WALL01"));

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


    public static HashMap<String, String> pidExecute(String hogi, String pid, String testVersion, String floor, String isfloor) {

        System.out.println("pidExecute ==============");
        HashMap<String, String> resultMap = new HashMap<>(); //mapper.readValue(jsonString, List.class);

        //https://plmpro.hdel.co.kr/plmetc/vault/pidExecute?hogi=208223L01&PID=EL_PB186A01&testVersion=on&isfloor&floor=

        String apiUrl = "https://plmpro.hdel.co.kr/plmetc/vault/pidExecute?";
        apiUrl += "hogi=" + hogi;
        apiUrl += "&PID=" + pid;
        apiUrl += "&testVersion=" + testVersion;
        apiUrl += "&isfloor=" + isfloor;
        apiUrl += "&floor=" + floor;


        try {
            // URL 객체 생성
            URL url = new URL(apiUrl);

            // HttpURLConnection 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // GET 방식 설정
            conn.setRequestMethod("GET");

            // 응답 타입 설정 (JSON, XML 등 필요에 맞게 변경 가능)
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            // 응답 데이터 읽기
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"))) {

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                // response.toString() → JSON 문자열
                String jsonString = response.toString();

                System.out.println(jsonString.toString());
                // ObjectMapper 생성
                ObjectMapper mapper = new ObjectMapper();


                // JSON → HashMap<String, String>
                /*HashMap<String, String> resultMap = mapper.readValue(
                        jsonString, new TypeReference<HashMap<String, String>>() {}
                );*/

                resultMap = mapper.readValue(
                        jsonString, new TypeReference<HashMap<String, String>>() {}
                );


                // HashMap 출력 예시
                /*for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }*/

                // 결과 출력
                //System.out.println("Response Data: " + response.toString());
            }

            // 연결 종료
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }
}
