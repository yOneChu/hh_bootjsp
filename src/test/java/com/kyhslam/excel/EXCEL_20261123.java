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
 * Block No. 별 영업사양 코드 추출
 * - A열(0) 의 BlockNo 로 PID 'EL_P' + BlockNo 의 최신 버전 로직을 조회
 * - 로직 SPEC1~30 열의 특성코드 중 영업사양 코드(dosfld 특성코드 사전에 있는 것)만 골라
 *   C열(2) 부터 가로로 순서대로 기록 (로직 등장 순, 중복 제거)
 */
public class EXCEL_20261123 {



    // ===== 테스트 파라미터 =====
    /** 공사번호(호기) */
    private static final String HOGI = System.getProperty("hogi", "N29738L01");
    /** 기준일 yyyyMMdd (yyyy-MM-dd 도 가능) */
    private static final String DATE = System.getProperty("date", "20261001");
    /** 실행할 PID */
    private static final String PID  = System.getProperty("pid", "EL_PA101A");

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

            for (int i = 1; i <= 4; i++) {

                Row row = sheet.getRow(i);

                //209718L02

                // 1. 빈 행이거나, 필터/수동 숨김 처리된 행이면 건너뛰기
                if (row == null || row.getZeroHeight()) {
                    //continue;
                }

                Cell cell0 = row.getCell(1); //호기번호
                String hogi = cell0.getStringCellValue();

                //Cell cell5 = row.getCell(10); //CAR_WT
                //String partNo = cell5.getStringCellValue();

                System.out.println("hogi = " + hogi + " , " + row.getCell(2).getStringCellValue());


                String XC_CAR_WT = "";
                String XC_FLOOR_WT = "";
                String XC_AIRCON_WT = "";

                Map<String, Object> output = callAsOf(hogi.trim(), "20261123", "CALC_CAR_WT");
                System.out.println("===== 최종 OUTPUT (" + output.size() + "건) =====");





                //row.createCell(8).setCellValue(pMap.get("GUBUN")); //XC_CAR_WT


                //XC_BALANCE_WT

                //XC_COM_WT
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
