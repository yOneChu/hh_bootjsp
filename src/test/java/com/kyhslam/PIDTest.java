package com.kyhslam;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StopWatch;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PIDTest {


    public static String getQueryResult(String authToken, String userId, String password) {
        String apiUrl = "https://plmpro.hdel.co.kr/plmetc/plmdb/api/json";
        HttpsURLConnection conn = null;
        BufferedReader br = null;

        try {
            // URL 연결 설정
            URL url = new URL(apiUrl);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("authToken", authToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);


            // 요청 데이터 (userid, password)
            Map<String, String> data = new HashMap<>();
            data.put("userId", "2035570");
            data.put("password", "gel1375a!");

            // 요청 데이터(JSON 변환 후 전송)
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = mapper.writeValueAsString(data);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // 응답 읽기
            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                response.append(line);
            }

            // JSON 문자열 그대로 반환 (혹은 Map으로 변환 가능)
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (br != null) try { br.close(); } catch (IOException ignore) {}
            if (conn != null) conn.disconnect();
        }
    }

    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();


        String authToken = "YOUR_AUTH_TOKEN";
        String userId = "testuser";
        String password = "testpass";

        String result = getQueryResult(authToken, userId, password);
        System.out.println("API Result: " + result);




        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);

    }
}
