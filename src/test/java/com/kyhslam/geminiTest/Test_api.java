package com.kyhslam.geminiTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Test_api {

    public static void main(String[] args) {
        // 1. 발급받은 제미나이 API 키 입력
        String kkk = "YOUR_GEMINI_API_KEY";

        // 2. 사용할 모델 지정 (가장 범용적이고 빠른 gemini-2.5-flash 추천)
        String model = "gemini-2.5-flash";
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + kkk;

        // 3. 요청 바디 데이터 구성 (JSON)
        String jsonPayload = """
                {
                    "contents": [{
                        "parts": [{
                            "text": "Java에서 제미나이 API 호출에 성공했어! 칭찬의 한마디 남겨줘."
                        }]
                    }]
                }
                """;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // 4. API 호출 및 응답 받기
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("=== HTTP 상태 코드 ===");
            System.out.println(response.statusCode());
            System.out.println("\n=== 응답 데이터 (JSON) ===");
            System.out.println(response.body());

        } catch (Exception e) {
            System.err.println("API 호출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
