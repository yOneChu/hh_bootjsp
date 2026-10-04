package com.kyhslam.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kyhslam.service.OneCycleFunc;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SuppressWarnings("unchecked")
@SpringBootTest
public class SimulateBlockClient_One {

    /**
     * BOM 시뮬레이터
     */

    private static final String SIMULATE_URL = "http://plmpro.hdel.co.kr/plmetc/bom/simulate/block";

    /** 결과 테이블 컬럼 : {헤더, JSON 필드명}  (전체 값이 비어있는 컬럼은 자동 생략) */
    private static final String[][] COLUMNS = {
            {"CHANGE",   "changeType"},
            {"BOM 품번",  "bomPartNo"},
            {"BOM 수량",  "bomQty"},
            {"BOM 비고",  "bomCmt"},
            {"SIM 품번",  "simulatePartNo"},
            {"SIM 수량",  "simulateQty"},
            {"GL CODE",  "simulateGlcode"},
            {"SPEC",     "simulateSpec"},
            {"SIZE",     "simulatePartSize"},
            {"SIM 비고",  "simulateCmt"},
    };
    private static final int MAX_CELL_WIDTH = 40;

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);   // 수량은 BigDecimal

    @Autowired
    private OneCycleFunc oneCycleFunc;

    @Test
    public void simulateBlock() {
        Map<String, Object> body = new HashMap<>();
        body.put("productNoList", Arrays.asList("TEST-632563"));
        //body.put("blockList", Arrays.asList("블록번호1", "블록번호2"));
        body.put("blockList", Arrays.asList("E321A"));
        body.put("blockOPTList", new ArrayList<>());   // 비워도 반드시 넣기

        // 0) 로그인 (application.properties 의 plm 계정, DESIGN 권한 사용자)
        OneCycleFunc.PlmSession session = oneCycleFunc.login();
        if (session == null) {
            log.error("##### 로그인 실패로 중단");
            return;
        }

        try {
            byte[] json = mapper.writeValueAsBytes(body);

            long start = System.currentTimeMillis();
            HttpURLConnection con = (HttpURLConnection) new URL(SIMULATE_URL).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Cookie", session.getCookieHeader());   // 로그인 세션 쿠키
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                os.write(json);
            }

            int code = con.getResponseCode();
            String responseBody = readBody(code < 400 ? con.getInputStream() : con.getErrorStream());
            long elapsed = System.currentTimeMillis() - start;

            report(body, code, elapsed, responseBody);
        } catch (Exception e) {
            log.error("##### 블록 시뮬레이션 호출 실패", e);
        }
    }

    @Test
    public void simulateBlock_Param_Test() {
        Map<String, Object> result = simulateBlock_Param(
                Arrays.asList("TEST-632563"),     // 호기
                Arrays.asList("E321A"),           // 블록
                new ArrayList<>());               // 블록옵션

        // 필요한 것만 꺼내서 사용
        if (!(Boolean) result.get("success")) {
            log.error("실패 : HTTP {} / {}", result.get("httpCode"), result.get("errorMessage"));
            return;
        }
        int totalCount = (Integer) result.get("totalCount");
        Map<String, Integer> changeTypeCount = (Map<String, Integer>) result.get("changeTypeCount");
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        List<Map<String, Object>> updates = ((Map<String, List<Map<String, Object>>>) result.get("byChangeType"))
                .getOrDefault("UPDATE", new ArrayList<>());

        log.info("총 {}건, 변경유형 {}, UPDATE {}건", totalCount, changeTypeCount, updates.size());
        for (Map<String, Object> row : rows) {
            log.info("{} | {} → {} | 수량 {} → {}", row.get("changeType"),
                    row.get("bomPartNo"), row.get("simulatePartNo"), row.get("bomQty"), row.get("simulateQty"));
        }
    }

    /**
     * 블록 시뮬레이션 호출 후 결과를 Map 으로 반환
     *
     * <pre>
     *  success         Boolean                          HTTP 2xx 이고 JSON 파싱 성공 여부
     *  httpCode        Integer                          HTTP 응답코드 (호출 자체 실패 시 -1)
     *  elapsedMs       Long                             소요시간(ms)
     *  errorMessage    String                           실패 사유 (성공 시 null)
     *  request         Map                              요청 body
     *  rawJson         String                           응답 원문
     *  totalCount      Integer                          결과 건수
     *  rows            List&lt;Map&gt;                        결과 행 (키 = SimulateBomVO 필드명 : productNo, blockNo, changeType,
     *                                                   bomPartNo, bomQty, bomCmt, simulatePartNo, simulateQty, simulateCmt ...)
     *  changeTypeCount Map&lt;String, Integer&gt;             변경유형별 건수  (SAME / UPDATE / ADD / DELETE / REPLACE)
     *  byChangeType    Map&lt;String, List&lt;Map&gt;&gt;           변경유형별 행
     *  byBlock         Map&lt;String, List&lt;Map&gt;&gt;           "호기/블록" 별 행  (ex. "TEST-632563/E321A")
     * </pre>
     * 수량(bomQty, simulateQty)은 BigDecimal
     */
    public Map<String, Object> simulateBlock_Param(List<String> productNoList, List<String> blockList, List<String> blockOPTList) {
        Map<String, Object> body = new HashMap<>();
        body.put("productNoList", productNoList);
        body.put("blockList", blockList);
        body.put("blockOPTList", blockOPTList == null ? new ArrayList<>() : blockOPTList);   // 비워도 반드시 넣기

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("httpCode", -1);
        result.put("elapsedMs", 0L);
        result.put("errorMessage", null);
        result.put("request", body);
        result.put("rawJson", null);
        result.put("totalCount", 0);
        result.put("rows", new ArrayList<Map<String, Object>>());
        result.put("changeTypeCount", new LinkedHashMap<String, Integer>());
        result.put("byChangeType", new LinkedHashMap<String, List<Map<String, Object>>>());
        result.put("byBlock", new LinkedHashMap<String, List<Map<String, Object>>>());

        // 0) 로그인 (application.properties 의 plm 계정, DESIGN 권한 사용자)
        OneCycleFunc.PlmSession session = oneCycleFunc.login();
        if (session == null) {
            log.error("##### 로그인 실패로 중단");
            result.put("errorMessage", "로그인 실패");
            return result;
        }

        try {
            byte[] json = mapper.writeValueAsBytes(body);

            long start = System.currentTimeMillis();
            HttpURLConnection con = (HttpURLConnection) new URL(SIMULATE_URL).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Cookie", session.getCookieHeader());   // 로그인 세션 쿠키
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                os.write(json);
            }

            int code = con.getResponseCode();
            String responseBody = readBody(code < 400 ? con.getInputStream() : con.getErrorStream());
            long elapsed = System.currentTimeMillis() - start;

            report(body, code, elapsed, responseBody);
            fillResult(result, code, elapsed, responseBody);
        } catch (Exception e) {
            log.error("##### 블록 시뮬레이션 호출 실패", e);
            result.put("errorMessage", e.toString());
        }
        return result;
    }

    private void fillResult(Map<String, Object> result, int code, long elapsed, String responseBody) {
        result.put("httpCode", code);
        result.put("elapsedMs", elapsed);
        result.put("rawJson", responseBody);

        JsonNode root = parseQuietly(responseBody);
        if (code >= 400 || root == null) {
            result.put("errorMessage", root == null ? "JSON 파싱 실패 : " + responseBody : root.toString());
            return;
        }

        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        Map<String, Integer> changeTypeCount = (Map<String, Integer>) result.get("changeTypeCount");
        Map<String, List<Map<String, Object>>> byChangeType = (Map<String, List<Map<String, Object>>>) result.get("byChangeType");
        Map<String, List<Map<String, Object>>> byBlock = (Map<String, List<Map<String, Object>>>) result.get("byBlock");

        for (JsonNode node : extractRows(root)) {
            Map<String, Object> row = mapper.convertValue(node, LinkedHashMap.class);
            rows.add(row);

            String changeType = text(node, "changeType").isEmpty() ? "-" : text(node, "changeType");
            changeTypeCount.merge(changeType, 1, Integer::sum);
            byChangeType.computeIfAbsent(changeType, k -> new ArrayList<>()).add(row);
            byBlock.computeIfAbsent(text(node, "productNo") + "/" + text(node, "blockNo"), k -> new ArrayList<>()).add(row);
        }
        result.put("totalCount", rows.size());
        result.put("success", true);
    }

    @Test
    public void simulateBlock_Origin() {
        Map<String, Object> body = new HashMap<>();
        body.put("productNoList", Arrays.asList("TEST-632563"));
        //body.put("blockList", Arrays.asList("블록번호1", "블록번호2"));
        body.put("blockList", Arrays.asList("E321A"));
        body.put("blockOPTList", new ArrayList<>());   // 비워도 반드시 넣기

        // 0) 로그인 (application.properties 의 plm 계정, DESIGN 권한 사용자)
        OneCycleFunc.PlmSession session = oneCycleFunc.login();
        if (session == null) {
            System.out.println("##### 로그인 실패로 중단");
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            byte[] json = mapper.writeValueAsBytes(body);

            URL url = new URL("http://plmpro.hdel.co.kr/plmetc/bom/simulate/block");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Cookie", session.getCookieHeader());   // 로그인 세션 쿠키
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                os.write(json);
            }

            int code = con.getResponseCode();
            InputStream is = code < 400 ? con.getInputStream() : con.getErrorStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                System.out.println(code + " : " + sb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------ 출력

    private void report(Map<String, Object> request, int code, long elapsed, String responseBody) throws Exception {
        StringBuilder out = new StringBuilder("\n");
        out.append(banner("BLOCK 시뮬레이션 결과"));
        out.append(String.format("  URL      : %s%n", SIMULATE_URL));
        out.append(String.format("  호기     : %s%n", request.get("productNoList")));
        out.append(String.format("  블록     : %s%n", request.get("blockList")));
        out.append(String.format("  블록옵션 : %s%n", request.get("blockOPTList")));
        out.append(String.format("  응답     : HTTP %d  (%,d ms)%n", code, elapsed));

        JsonNode root = parseQuietly(responseBody);
        if (code >= 400 || root == null) {
            out.append(banner("오류 응답"));
            out.append(root != null ? mapper.writeValueAsString(root) : responseBody).append('\n');
            log.error(out.toString());
            return;
        }

        List<JsonNode> rows = extractRows(root);
        Path saved = saveJson(root);

        // 요약 : 변경유형별 / 호기-블록별 건수
        Map<String, Integer> byChange = new LinkedHashMap<>();
        Map<String, List<JsonNode>> byBlock = new LinkedHashMap<>();
        for (JsonNode row : rows) {
            byChange.merge(text(row, "changeType").isEmpty() ? "-" : text(row, "changeType"), 1, Integer::sum);
            byBlock.computeIfAbsent(text(row, "productNo") + " / " + text(row, "blockNo"), k -> new ArrayList<>()).add(row);
        }

        out.append(banner("요약"));
        out.append(String.format("  총 건수  : %,d%n", rows.size()));
        if (!byChange.isEmpty()) {
            out.append("  변경유형 : ");
            byChange.forEach((k, v) -> out.append(k).append('=').append(v).append("  "));
            out.append('\n');
        }
        out.append(String.format("  원본JSON : %s%n", saved == null ? "(저장 실패)" : saved.toAbsolutePath()));

        // 호기/블록 단위 테이블
        for (Map.Entry<String, List<JsonNode>> e : byBlock.entrySet()) {
            out.append(banner("호기 / 블록 : " + e.getKey() + "  (" + e.getValue().size() + "건)"));
            out.append(table(e.getValue()));
        }
        if (rows.isEmpty()) {
            out.append("  (결과 없음)\n");
        }

        log.info(out.toString());
    }

    /** 응답이 배열이면 그대로, 객체로 감싸져 있으면 첫 번째 배열 필드를 결과로 사용 */
    private List<JsonNode> extractRows(JsonNode root) {
        JsonNode arr = root;
        if (!root.isArray()) {
            arr = null;
            for (Iterator<JsonNode> it = root.elements(); it.hasNext(); ) {
                JsonNode child = it.next();
                if (child.isArray()) { arr = child; break; }
            }
        }
        List<JsonNode> rows = new ArrayList<>();
        if (arr != null) arr.forEach(rows::add);
        return rows;
    }

    private String table(List<JsonNode> rows) {
        // 값이 하나라도 있는 컬럼만 사용
        List<String[]> cols = new ArrayList<>();
        cols.add(new String[]{"No", null});
        for (String[] c : COLUMNS) {
            for (JsonNode r : rows) {
                if (!text(r, c[1]).isEmpty()) { cols.add(c); break; }
            }
        }

        List<String[]> cells = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            String[] line = new String[cols.size()];
            line[0] = String.valueOf(i + 1);
            for (int c = 1; c < cols.size(); c++) {
                line[c] = truncate(text(rows.get(i), cols.get(c)[1]).replaceAll("\\s*\\R\\s*", " / "));
            }
            cells.add(line);
        }

        int[] width = new int[cols.size()];
        for (int c = 0; c < cols.size(); c++) {
            width[c] = displayWidth(cols.get(c)[0]);
            for (String[] line : cells) width[c] = Math.max(width[c], displayWidth(line[c]));
        }

        StringBuilder sb = new StringBuilder();
        String border = border(width);
        sb.append(border);
        String[] header = new String[cols.size()];
        for (int c = 0; c < cols.size(); c++) header[c] = cols.get(c)[0];
        sb.append(row(header, width, cols));
        sb.append(border);
        for (String[] line : cells) sb.append(row(line, width, cols));
        sb.append(border);
        return sb.toString();
    }

    private String border(int[] width) {
        StringBuilder sb = new StringBuilder("  +");
        for (int w : width) sb.append(repeat('-', w + 2)).append('+');
        return sb.append('\n').toString();
    }

    private String row(String[] values, int[] width, List<String[]> cols) {
        StringBuilder sb = new StringBuilder("  |");
        for (int c = 0; c < values.length; c++) {
            String field = cols.get(c)[1];
            boolean right = field == null || field.endsWith("Qty");   // No, 수량은 우측정렬
            int pad = width[c] - displayWidth(values[c]);
            sb.append(' ');
            if (right) sb.append(repeat(' ', pad)).append(values[c]);
            else sb.append(values[c]).append(repeat(' ', pad));
            sb.append(" |");
        }
        return sb.append('\n').toString();
    }

    private String banner(String title) {
        return "\n  ==== " + title + " " + repeat('=', Math.max(4, 70 - displayWidth(title))) + "\n";
    }

    // ------------------------------------------------------------------ 유틸

    private String readBody(InputStream is) throws Exception {
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString().trim();
        }
    }

    private JsonNode parseQuietly(String body) {
        try {
            return body.isEmpty() ? null : mapper.readTree(body);
        } catch (Exception e) {
            return null;
        }
    }

    /** 전체 응답은 target/simulate 에 pretty JSON 으로 저장 */
    private Path saveJson(JsonNode root) {
        try {
            Path dir = Paths.get("target", "simulate");
            Files.createDirectories(dir);
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path file = dir.resolve("block_" + ts + ".json");
            Files.write(file, mapper.writeValueAsBytes(root));
            return file;
        } catch (Exception e) {
            log.warn("결과 JSON 저장 실패 : {}", e.getMessage());
            return null;
        }
    }

    private String text(JsonNode row, String field) {
        JsonNode v = row.get(field);
        if (v == null || v.isNull()) return "";
        return v.isNumber() ? v.decimalValue().stripTrailingZeros().toPlainString() : v.asText();
    }

    private String truncate(String s) {
        if (displayWidth(s) <= MAX_CELL_WIDTH) return s;
        StringBuilder sb = new StringBuilder();
        int w = 0;
        for (int i = 0; i < s.length(); i++) {
            int cw = charWidth(s.charAt(i));
            if (w + cw > MAX_CELL_WIDTH - 2) break;
            sb.append(s.charAt(i));
            w += cw;
        }
        return sb.append("..").toString();
    }

    /** 한글 등 전각 문자는 콘솔에서 2칸을 차지하므로 정렬 시 보정 */
    private int displayWidth(String s) {
        int w = 0;
        for (int i = 0; i < s.length(); i++) w += charWidth(s.charAt(i));
        return w;
    }

    private int charWidth(char ch) {
        return (ch >= 0x1100 && ch <= 0x115F) || (ch >= 0x2E80 && ch <= 0xA4CF)
                || (ch >= 0xAC00 && ch <= 0xD7A3) || (ch >= 0xF900 && ch <= 0xFAFF)
                || (ch >= 0xFE30 && ch <= 0xFE4F) || (ch >= 0xFF00 && ch <= 0xFF60)
                || (ch >= 0xFFE0 && ch <= 0xFFE6) ? 2 : 1;
    }

    private String repeat(char ch, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(ch);
        return sb.toString();
    }
}
