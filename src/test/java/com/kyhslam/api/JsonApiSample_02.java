package com.kyhslam.api;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.domain.SubaeHogi;
import com.kyhslam.domain.SubaeHogiBOM;
import com.kyhslam.service.SubaeHogiService;
import com.kyhslam.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

@SpringBootTest
public class JsonApiSample_02 {

    @Autowired
    SubaeHogiService subaeHogiService;

    @Test
    public void findData() {
        StopWatch sw = new StopWatch();
        sw.start();

        LocalDate now = LocalDate.now();
        String todayVal = DateUtil.getTodayDate();

        try {
            String requestUrl = "https://plmpro.hdel.co.kr/jsp/help/getBomByBlockoptList.jsp?proNo=210317L07";

            String jsonResponse = getJson(requestUrl);
            System.out.println("응답 JSON:");
            System.out.println(jsonResponse);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            if (root.isArray()) {
                for (JsonNode node : root) {
                    String partNo = node.path("PART").asText();
                    String partName = node.path("PARTNAME").asText();
                    String hogi = node.path("MD$NUMBER").asText();
                    String hogiVersion = node.path("VF$VERSION").asText();
                    String UCHECK =  node.path("UCHECK").asText();
                    if(UCHECK == null || "".equals(UCHECK)){
                        UCHECK = "";
                    }
                    String CDATE = node.path("CDATE").asText();
                    String QTY = node.path("QTY").asText();

                    String BLOCKNO_NUMBER = node.path("BLOCKNO_NUMBER").asText();
                    String BLOCK_OPT = node.path("BLOCK_OPT").asText();


                    System.out.println("hogi = " + hogi);
                    System.out.println("hogiVersion = " + hogiVersion);
                    System.out.println("partNo = " + partNo);
                    System.out.println("QTY = " + QTY);


                    SubaeHogiBOM b = new  SubaeHogiBOM();
                    b.setHogi(hogi);
                    b.setHogiVersion(hogiVersion);
                    b.setPartNo(partNo);
                    b.setPartName(partName);
                    b.setQty(QTY);
                    b.setBlockNo(BLOCKNO_NUMBER);
                    b.setBlockOpt(BLOCK_OPT);
                    b.setCodate(CDATE);
                    b.setUcheck(UCHECK);

                    subaeHogiService.subaeHogiBOMSave(b);

                    System.out.println("--------");
                }
            }

        } catch (Exception e) {
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
