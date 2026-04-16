package com.kyhslam.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.domain.PartPlanC;
import com.kyhslam.domain.PlanCDash;
import com.kyhslam.domain.SubaeHogi;
import com.kyhslam.domain.SubaeHogiBOM;
import com.kyhslam.repository.SubaeHogiRepository;
import com.kyhslam.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service("SubaeHogiService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubaeHogiService {
    private final SubaeHogiRepository repository;

    @Transactional
    public void subaeHogiSave(SubaeHogi subae) {
        repository.subaeHogisave(subae);
    }


    @Transactional
    public void subaeHogiBOMSave(SubaeHogiBOM subae) {
        repository.subaeHogiBOMsave(subae);
    }




    public List<SubaeHogi> findSubaeHogi(String batchDate) {
        List<SubaeHogi> list = repository.findSubaeHogi(batchDate);
        return list;
    }

    public List<SubaeHogi> findSubaeHogiAsCodat(String codat) {
        List<SubaeHogi> list = repository.findSubaeHogiAsCodat(codat);
        return list;
    }

    public List<SubaeHogi> findSubaeHogiLikeCodat(String codat) {
        List<SubaeHogi> list = repository.findSubaeHogiLikeCodat(codat);
        return list;
    }


    @Scheduled(cron = "0 30 22 * * *")
    public void subaeHogiInsertBatch() {
        StopWatch sw = new StopWatch();
        sw.start();

        String toDate = DateUtil.getTodayDateNoHyphen();
        String requestUrl = "https://plmpro.hdel.co.kr/jsp/help/gethogilistByBlockopt.jsp?searchdate=";
        try {

            requestUrl+= toDate;

            String jsonResponse = getJson(requestUrl);
            System.out.println("응답 JSON:");
            System.out.println(jsonResponse);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            //LocalDate now = LocalDate.now();
            String todayVal = DateUtil.getTodayDate();

            if (root.isArray()) {
                for (JsonNode node : root) {
                    String hogiNo = node.path("posid").asText();
                    String codat = node.path("codat").asText();

                    System.out.println("hogiNo = " + hogiNo);
                    System.out.println("codat = " + codat);

                    SubaeHogi s = new  SubaeHogi();
                    s.setHogi(hogiNo);
                    s.setCodat(codat);
                    s.setBatchDate(todayVal);

                    subaeHogiSave(s);

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
