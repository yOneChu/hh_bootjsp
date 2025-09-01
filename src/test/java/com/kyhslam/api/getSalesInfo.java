package com.kyhslam.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class getSalesInfo {

    public static void main(String[] args) {

        String apiUrl = "https://plmpro.hdel.co.kr/jsp/help/salesInfoFromProductViewJson.jsp?productNumber=204201L11";


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

            StringBuilder response = new StringBuilder();

            // 응답 데이터 읽기
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"))) {

                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            // 연결 종료
            conn.disconnect();

            // JSON 파싱 및 ArrayList<HashMap<String, String>> 변환
            ArrayList<HashMap<String, String>> resultList = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("SPEC_VALUE", obj.optString("SPEC_VALUE")); // 특성명
                map.put("SPEC_CODE", obj.optString("SPEC_CODE")); // 특성코드
                map.put("VALUE", obj.optString("VALUE")); // 특성값
                map.put("TYPE", obj.optString("TYPE")); // tab명

                resultList.add(map);
            }

            // 결과 출력
            for (HashMap<String, String> map : resultList) {
                System.out.println(map);
            }




        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
