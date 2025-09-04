package com.kyhslam.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class getFloorInfo_API {


    /**
     * 층별 로직 정보
     * 모든 층의 사양값 가져옴
     * @param args
     */
    public static void main(String[] args) {

        String apiUrl = "";

        //로컬
        //apiUrl = "http://localhost/plmetc/vault/getFloorInfo?prodNum=208618L17";

        //운영
        apiUrl = "https://plmpro.hdel.co.kr/plmetc/vault/getFloorInfo?prodNum=208618L17";

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

                // ObjectMapper 생성
                ObjectMapper mapper = new ObjectMapper();

                // JSON 배열이므로 List<Map>으로 변환
                List<Map<String, Object>> list = mapper.readValue(jsonString, List.class);

                // 확인
                for (Map<String, Object> item : list) {
                    System.out.println(item);
                }

                // 결과 출력
                //System.out.println("Response Data: " + response.toString());
            }

            // 연결 종료
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
