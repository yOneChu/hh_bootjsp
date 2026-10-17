package com.kyhslam.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * BOM 이상감지 (SENTINEL 6F 관제 화면 데이터)
 *
 * 지금은 발표용 데모 데이터를 만든다. (demo = true)
 * 실제 탐지 로직이 준비되면 {@link #scan()} 만 바꾸면 된다. 화면(cop/bomRadar.html)은 아래 응답 형식만 본다.
 *
 * 응답 형식
 * {
 *   demo, generatedAt, monitored,               // 데모 여부 / 생성 시각 / 감시 호기 수
 *   trend: [14일 탐지 건수],
 *   anomalies: [{
 *     id, productNo, site, type(QTY|MISSING|SPEC|DUP), severity(0~100), status(NEW|ACK|DONE|IGNORED),
 *     detectedAt, partNo, partName, message, suggestion, peers(비교한 동일 사양 호기 수),
 *     evidence: { kind:"dist", unit, actual, expected, values:[동일 사양 호기 값] }
 *             | { kind:"pair", leftLabel, leftValue, rightLabel, rightValue }
 *   }]
 * }
 */
@Service
public class BomAnomalyService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String[][] PARTS = {
            {"P-2210-04", "가이드레일"}, {"P-3318-11", "도어행거"}, {"P-1102-07", "브라켓"},
            {"P-4420-02", "와이어로프"}, {"P-5001-15", "권상기"}, {"P-6130-09", "제어반 케이블"},
            {"P-2740-03", "카 프레임"}, {"P-2805-12", "균형추"}, {"P-3902-01", "도어 인터록"},
            {"P-7011-06", "조속기"}, {"P-1450-21", "앵커볼트"}, {"P-6620-04", "리미트 스위치"}
    };

    private static final String[][] SPECS = {
            {"EL_ETM", "권상기 타입"}, {"EL_DOOR", "도어 방식"}, {"EL_SPD", "정격 속도"}, {"EL_CAP", "적재 하중"}
    };

    public Map<String, Object> scan() {

        Random rnd = new Random(20261017L);     // 매번 같은 데모 결과
        LocalDateTime now = LocalDateTime.now();
        String[] types = {"QTY", "MISSING", "SPEC", "DUP"};

        List<Map<String, Object>> anomalies = new ArrayList<>();
        for (int i = 0; i < 42; i++) {

            // 첫 건은 발표 시연용 대표 사례 (수량 급증 ×12)
            String type = i == 0 ? "QTY" : types[weighted(rnd, new int[]{40, 25, 20, 15})];
            String[] part = PARTS[rnd.nextInt(PARTS.length)];
            int severity = clamp((int) Math.round(35 + rnd.nextGaussian() * 22 + (type.equals("MISSING") ? 12 : 0)), 8, 98);
            String productNo = (i == 0 ? "TEST-632560" : "H" + (1 + rnd.nextInt(4)) + "-" + (10000 + rnd.nextInt(89999)));

            Map<String, Object> a = new LinkedHashMap<>();
            a.put("id", "A" + (1000 + i));
            a.put("productNo", productNo);
            a.put("site", "샘플현장 " + (char) ('A' + rnd.nextInt(8)) + "-" + (1 + rnd.nextInt(40)));
            a.put("type", type);
            a.put("severity", i == 0 ? 99 : severity);
            a.put("status", i < 30 ? "NEW" : i < 36 ? "ACK" : "DONE");
            a.put("detectedAt", now.minusMinutes(i * 37L + rnd.nextInt(30)).format(TS));
            a.put("partNo", part[0]);
            a.put("partName", part[1]);
            int peers = 12 + rnd.nextInt(30);
            a.put("peers", peers);

            switch (type) {
                case "QTY" -> {
                    int expected = 2 + rnd.nextInt(10);
                    double factor = i == 0 ? 12 : 1.8 + rnd.nextDouble() * 6;
                    int actual = (int) Math.round(expected * factor);
                    a.put("message", part[1] + " 수량 " + actual + "EA — 동일 사양 평균 " + expected + "EA 의 " + String.format("%.1f", (double) actual / expected) + "배");
                    a.put("suggestion", "수량 산출 로직(PID)과 속성값을 확인한 뒤 BOM 을 다시 계산하세요.");
                    a.put("evidence", dist("EA", actual, expected, peerValues(rnd, expected, peers)));
                }
                case "MISSING" -> {
                    int expected = 4 + rnd.nextInt(14);
                    a.put("message", part[1] + " 하위 자재 0건 — 동일 사양은 평균 " + expected + "건");
                    a.put("suggestion", "종속사양 산출이 끝까지 수행됐는지 확인하고 원사이클을 다시 실행하세요.");
                    a.put("evidence", dist("건", 0, expected, peerValues(rnd, expected, peers)));
                }
                case "SPEC" -> {
                    String[] spec = SPECS[rnd.nextInt(SPECS.length)];
                    String specVal = String.valueOf((char) ('A' + rnd.nextInt(3)));
                    String partVal = String.valueOf((char) ('D' + rnd.nextInt(3)));
                    a.put("message", spec[1] + " " + spec[0] + "=" + specVal + " 인데 " + part[1] + " " + partVal + " 타입이 전개됨");
                    a.put("suggestion", "영업사양 " + spec[0] + " 값과 자재 매핑 규칙을 대조해 보세요.");
                    a.put("evidence", pair("영업사양 " + spec[0], specVal + " 타입", "전개된 자재", part[1] + " " + partVal + " 타입"));
                }
                default -> {
                    String path1 = "카 > 도어 > " + part[1];
                    String path2 = "승강장 > 도어 > " + part[1];
                    a.put("message", part[0] + " " + part[1] + " 가 두 경로로 중복 전개됨");
                    a.put("suggestion", "두 전개 경로 중 하나의 수배 조건이 잘못되지 않았는지 확인하세요.");
                    a.put("evidence", pair("경로 1", path1, "경로 2", path2));
                }
            }
            anomalies.add(a);
        }

        List<Integer> trend = new ArrayList<>();
        for (int d = 0; d < 14; d++) {
            trend.add(clamp((int) Math.round(18 + Math.sin(d / 2.0) * 6 + rnd.nextGaussian() * 4 + (d > 10 ? 6 : 0)), 3, 60));
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("demo", true);
        res.put("generatedAt", now.format(TS));
        res.put("monitored", 1248);
        res.put("trend", trend);
        res.put("anomalies", anomalies);
        return res;
    }

    private Map<String, Object> dist(String unit, int actual, int expected, List<Integer> values) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("kind", "dist");
        e.put("unit", unit);
        e.put("actual", actual);
        e.put("expected", expected);
        e.put("values", values);
        return e;
    }

    private Map<String, Object> pair(String leftLabel, String leftValue, String rightLabel, String rightValue) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("kind", "pair");
        e.put("leftLabel", leftLabel);
        e.put("leftValue", leftValue);
        e.put("rightLabel", rightLabel);
        e.put("rightValue", rightValue);
        return e;
    }

    /** 동일 사양 호기들의 값 (기대값 근처에 몰리도록) */
    private List<Integer> peerValues(Random rnd, int expected, int n) {
        List<Integer> v = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            v.add(Math.max(0, (int) Math.round(expected + rnd.nextGaussian() * Math.max(1, expected * 0.15))));
        }
        return v;
    }

    private int weighted(Random rnd, int[] weights) {
        int sum = 0;
        for (int w : weights) sum += w;
        int r = rnd.nextInt(sum);
        for (int i = 0; i < weights.length; i++) {
            r -= weights[i];
            if (r < 0) return i;
        }
        return weights.length - 1;
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
