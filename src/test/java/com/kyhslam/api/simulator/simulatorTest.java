package com.kyhslam.api.simulator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PID 시뮬레이터 기준일 실행 API 테스트
 *   POST /simulateup/simulatePidAsOf  (hogi, date, pid)
 *   사용법 : docs/PID시뮬레이터_기준일실행_API_사용법.md
 *
 * 실행 전 서버(BASE_URL)가 떠 있어야 한다.
 * 파라미터는 아래 상수를 바꾸거나, 실행 시 -Dhogi=... -Ddate=... -Dpid=... -DbaseUrl=... 로 넘긴다.
 */
public class simulatorTest {

    // ===== 테스트 파라미터 =====
    /** 공사번호(호기) */
    private static final String HOGI = System.getProperty("hogi", "N29738L01");
    /** 기준일 yyyyMMdd (yyyy-MM-dd 도 가능) */
    private static final String DATE = System.getProperty("date", "20261001");
    /** 실행할 PID */
    private static final String PID  = System.getProperty("pid", "EL_PA101A");

    /** 서버 주소 */
    private static final String BASE_URL = System.getProperty("baseUrl", "https://vault-in.hdel.co.kr/:8070");
    private static final String API_PATH = "/simulateup/simulatePidAsOf";

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws Exception {
        Map<String, Object> output = callAsOf(HOGI, DATE, PID);

        System.out.println("===== 최종 OUTPUT (" + output.size() + "건) =====");
        int width = output.keySet().stream().mapToInt(String::length).max().orElse(0);
        output.forEach((k, v) -> System.out.printf("%-" + width + "s : %s%n", k, v));
    }

    /**
     * API 호출 → 최종 OUTPUT (KEY → 값). 200 이 아니면 실패 처리한다.
     */
    private Map<String, Object> callAsOf(String hogi, String date, String pid) throws Exception {
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

        return mapper.readValue(response.body(), new TypeReference<LinkedHashMap<String, Object>>() {});
    }

    private static String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
