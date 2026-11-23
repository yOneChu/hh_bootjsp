package com.kyhslam.excel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.util.pidSimulatorUp.SimDb;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository;
import com.kyhslam.util.pidSimulatorUp.SimVariantMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PID 'CALC_CAR_WT' 시뮬을 돌려서 엑셀에 입력된 호기들의 카자중에 사용되는 변수값 추출하는 기능
 */
public class EXCEL_20261123 {

    /** 서버 주소 */
    private static final String BASE_URL = System.getProperty("baseUrl", "https://vault-in.hdel.co.kr:8070");
    private static final String API_PATH = "/simulateup/simulatePidAsOf";


    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();

        String fileName = "c:\\excel\\test.xlsx";
        // 원본 보호를 위해 결과는 별도 파일로 저장
        //String outFileName = fileName.replace(".xlsx", "_result.xlsx");

        Workbook workbook = null;

        try (SimDb db = SimDb.open()) {

            // 1. 엑셀 파일 읽기
            try (FileInputStream fis = new FileInputStream(fileName)) {
                workbook = new XSSFWorkbook(fis);
            }

            Sheet sheet = workbook.getSheet("Sheet2");

            System.out.println("sheet = " + sheet);


            int lastRow = sheet.getLastRowNum();

            for (int i = 300; i <= lastRow; i++) {

                Row row = sheet.getRow(i);

                //209718L02

                // 1. 빈 행이거나, 필터/수동 숨김 처리된 행이면 건너뛰기
                if (row == null || row.getZeroHeight()) {
                    continue;
                }

                Cell cell0 = row.getCell(1); //호기번호
                String hogi = cell0.getStringCellValue();

                //Cell cell5 = row.getCell(10); //CAR_WT
                //String partNo = cell5.getStringCellValue();

                //System.out.println("hogi = " + hogi + " , " + row.getCell(2).getStringCellValue());


                String XC_CAR_WT = "";
                String XC_FLOOR_WT = "";
                String XC_AIRCON_WT = "";
                String XC_BALANCE_WT = "";
                String XC_COM_WT = "";
                String XC_PAD_WT = "";
                String XC_ADD_WT = "";
                String XC_WALL_WT = "";
                String XC_CEILING_WT = "";

                String XC_EMG_WT = "";
                String XC_THRU_WT = "";
                String XC_OB_WT = "";
                String XC_CS_WT = "";
                String XC_DIS_WT = "";
                String XC_BWCAD_WT = "";


                Map<String, Object> output = callAsOf(hogi.trim(), "20261123", "CALC_CAR_WT");
                System.out.println("===== 최종 OUTPUT (" + output + "건) =====");


                row.createCell(8).setCellValue((String)output.get("XC_CAR_WT")); //XC_CAR_WT
                row.createCell(9).setCellValue((String)output.get("XC_FLOOR_WT")); //XC_CAR_WT
                row.createCell(10).setCellValue((String)output.get("XC_AIRCON_WT")); //XC_CAR_WT
                row.createCell(11).setCellValue((String)output.get("XC_BALANCE_WT")); //XC_CAR_WT
                row.createCell(12).setCellValue((String)output.get("XC_COM_WT")); //XC_CAR_WT
                row.createCell(13).setCellValue((String)output.get("XC_PAD_WT")); //XC_PAD_WT
                row.createCell(14).setCellValue((String)output.get("XC_ADD_WT")); //XC_CAR_WT
                row.createCell(15).setCellValue((String)output.get("XC_WALL_WT")); //XC_CAR_WT
                row.createCell(16).setCellValue((String)output.get("XC_CEILING_WT")); //XC_CAR_WT

                row.createCell(17).setCellValue((String)output.get("XC_EMG_WT"));
                row.createCell(18).setCellValue((String)output.get("XC_THRU_WT"));
                row.createCell(19).setCellValue((String)output.get("XC_OB_WT"));
                row.createCell(20).setCellValue((String)output.get("XC_CS_WT"));
                row.createCell(21).setCellValue((String)output.get("XC_DIS_WT"));
                row.createCell(22).setCellValue((String)output.get("XC_BWCAD_WT"));
            }

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) workbook.close();
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



    /**
     * API 호출 → 최종 OUTPUT (KEY → 값). 200 이 아니면 실패 처리한다.
     */
    private static Map<String, Object> callAsOf(String hogi, String date, String pid) throws Exception {
        String form = "hogi=" + enc(hogi) + "&date=" + enc(date) + "&pid=" + enc(pid);

        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + API_PATH))
                .timeout(Duration.ofMinutes(5))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        System.out.println("POST " + BASE_URL + API_PATH + "  (" + form + ")");
        long start = System.currentTimeMillis();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        System.out.println("Response Code : " + response.statusCode()
                + " (" + (System.currentTimeMillis() - start) + "ms)");

        if (response.statusCode() != 200)
            System.out.println("Response Body : " + response.body());
        assertEquals(200, response.statusCode(), "API 호출 실패 : " + response.body());

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(response.body(), new TypeReference<LinkedHashMap<String, Object>>() {});
    }

    private static String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
