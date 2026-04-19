package com.kyhslam.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.domain.SubaeHogi;
import com.kyhslam.service.SubaeHogiService;
import com.kyhslam.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.util.StopWatch;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

@SpringBootTest
public class SubaeHogiSaveTest {

    @Autowired
    SubaeHogiService subaeHogiService;

    @Test
    public void printDate() {

        String toDate = DateUtil.getTodayDateNoHyphen();
        System.out.println("toDate = " + toDate);
    }

    @Description("당일 설계완료 호기 조회")
    @Test
    public void saveTest() {
        StopWatch sw = new StopWatch();
        sw.start();

        LocalDate now = LocalDate.now();
        String todayVal = DateUtil.getTodayDate();

        //설계완료일
        //String requestUrl = "https://plmpro.hdel.co.kr/jsp/help/gethogilistByBlockopt.jsp?searchdate=20260401";

        try {

            for (int i = 1; i < 32; i++) {
                String requestUrl = "https://plmpro.hdel.co.kr/jsp/help/gethogilistByBlockopt.jsp?searchdate=202604";

                if(i < 10) {
                    requestUrl += "0" + (String.valueOf(i));
                } else {
                    requestUrl += (String.valueOf(i));
                }

                System.out.println("requestUrl = " + requestUrl);

                // 2초 대기
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }

                String jsonResponse = getJson(requestUrl);
                System.out.println("응답 JSON:");
                System.out.println(jsonResponse);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonResponse);
                System.out.println("root = " + root);


                if (root.isArray()) {
                    for (JsonNode node : root) {
                        String hogiNo = node.path("posid").asText();
                        String codat = node.path("codat").asText();

                        if (hogiNo.startsWith("Q")) {
                            continue;
                        }

                        if (hogiNo.length() > 15) {
                            continue;
                        }

                        System.out.println("hogiNo = " + hogiNo);
                        System.out.println("codat = " + codat);

                        SubaeHogi s = new  SubaeHogi();
                        s.setHogi(hogiNo);
                        s.setCodat(codat);
                        s.setBatchDate(todayVal);

                        subaeHogiService.subaeHogiSave(s);

                        System.out.println("--------");
                    }
                }
            }


        } catch (Exception e)  {
            e.printStackTrace();
        } finally {
            sw.stop();

            long millis = sw.getTotalTimeMillis();

            double seconds = millis / 1000.0;
            double minutes = seconds / 60.0;

            System.out.println("⏱ 수행 시간:");
            System.out.printf("   - %.3f 초%n", seconds);
            System.out.printf("   - %.3f 분%n", minutes);
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
