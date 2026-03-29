package com.kyhslam.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class JsonApiSample_01 {

    public static void main(String[] args) {

        //설계완료일
        String requestUrl = "https://plmpro.hdel.co.kr/jsp/help/gethogilistByBlockopt.jsp?searchdate=20231129";

        try {
            String jsonResponse = getJson(requestUrl);
            System.out.println("응답 JSON:");
            System.out.println(jsonResponse);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            System.out.println("root = " + root);

/*
            String result = root.path("result").asText();
            int count = root.path("count").asInt();

            System.out.println("result = " + result);
            System.out.println("count = " + count);
*/

            if (root.isArray()) {
                for (JsonNode node : root) {
                    String hogiNo = node.path("posid").asText();
                    String codat = node.path("codat").asText();

                    System.out.println("hogiNo = " + hogiNo);
                    System.out.println("codat = " + codat);
                    System.out.println("--------");
                }
            }


            /*//파싱
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


            objectMapper.readValue(jsonResponse, ApiResponseVO.class);*/



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String getJson(String requestUrl) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("HTTP 요청 실패. status=" + response.statusCode()
                    + ", body=" + response.body());
        }

        return response.body();
    }

}
