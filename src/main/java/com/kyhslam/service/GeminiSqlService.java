package com.kyhslam.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kyhslam.util.ChatUtil;
import com.kyhslam.util.SQLCommonUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * DB 정의서(ChatUtil — 비표준 사양검토 / 전산화요청)를 시스템 프롬프트로 사용해
 * 자연어 질문 → SELECT SQL 을 Gemini 로 생성하고, SQLCommonUtil 로 DB 에서 직접 실행하는 서비스.
 * dataset 파라미터("nonstandard" | "designreq")로 대상 DB 를 선택한다.
 */
@Service
public class GeminiSqlService {


    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** dataset 식별자 → DB 정의서(시스템 프롬프트). 미지정/미지원 값은 비표준 사양검토로 처리 */
    private String resolveSchemaPrompt(String dataset) {
        return "designreq".equalsIgnoreCase(dataset)
                ? ChatUtil.getDesignReqDefinition()
                : ChatUtil.getNonStandardContent();
    }

    /** dataset 식별자 → 한국어 도메인명 (분석 리포트 페르소나용) */
    private String datasetName(String dataset) {
        return "designreq".equalsIgnoreCase(dataset) ? "전산화(설계)요청" : "비표준 사양검토";
    }

    /**
     * 자연어 질문을 받아 Gemini 로 SQL 을 생성한다.
     *
     * @param dataset 대상 DB ("nonstandard" | "designreq", null 이면 nonstandard)
     * @return {"sql": 생성된 SELECT문, "explanation": 쿼리 설명} 형태의 Map
     */
    public Map<String, String> generateSql(String question, String dataset) {

        String systemPrompt = resolveSchemaPrompt(dataset) + """

                [응답 형식]
                반드시 아래 JSON 형식으로만 응답하라. JSON 전체를 감싸는 마크다운 코드펜스 없이 순수 JSON만 출력.
                {"sql": "생성한 SELECT 쿼리 (한 줄 또는 줄바꿈 포함 가능)", "explanation": "쿼리에 대한 간단한 한국어 설명"}
                explanation 은 마크다운 형식으로 작성하라:
                - 조회 조건·집계 기준 등 핵심 키워드는 **굵게** 강조
                - 테이블명·컬럼명·코드값은 `인라인 코드` 로 표기
                - 조건이 여러 개면 붙임표(-) 목록으로 정리
                SQL 생성이 불가능하거나 질문이 모호하면 sql 은 빈 문자열로, explanation 에 확인 질문을 담아라.
                """;

        try {
            String modelText = callGemini(systemPrompt, question);
            JsonNode result = objectMapper.readTree(stripCodeFence(modelText));
            return Map.of(
                    "sql", result.path("sql").asText(""),
                    "explanation", result.path("explanation").asText("")
            );
        } catch (Exception e) {
            return Map.of(
                    "sql", "",
                    "explanation", "SQL 생성 중 오류가 발생했습니다: " + e.getMessage()
            );
        }
    }

    /** 분석 프롬프트에 넣을 쿼리 결과 최대 길이 (토큰 한도 보호) */
    private static final int MAX_RESULT_CHARS = 20_000;

    /**
     * 쿼리 실행 결과를 Gemini 로 분석해 상세 인사이트 리포트를 생성한다.
     *
     * @param question   사용자의 원래 자연어 질문
     * @param sql        실행된 SELECT 쿼리
     * @param resultJson executeQuery 가 반환한 조회 결과(JSON 문자열)
     * @param dataset    대상 DB ("nonstandard" | "designreq", null 이면 nonstandard)
     * @return {"summary": 핵심 요약, "keyFindings": [주요 발견], "insights": [인사이트], "recommendations": [제언]}
     */
    public Map<String, Object> analyzeResult(String question, String sql, String resultJson, String dataset) {

        String systemPrompt = """
                너는 %s 데이터를 다루는 시니어 데이터 분석가다.
                사용자의 질문, 실행된 SQL, 그리고 그 실행 결과(JSON)를 근거로
                의사결정에 도움이 되는 상세한 한국어 분석 리포트를 작성하라.

                [작성 지침]
                - 결과 데이터에 실제로 존재하는 수치·항목만 근거로 사용하고, 없는 사실을 지어내지 마라.
                - 총 건수, 비중, 최댓값/최솟값, 집중 구간, 눈에 띄는 패턴이나 이상치를 구체적인 수치와 함께 짚어라.
                - 특정 개인에 대한 해석·평가는 절대 금지한다. 결과에 담당자·사용자 등 인물 정보가 포함되어 있더라도, "특정 담당자의 업무가 많다/적다", "업무가 특정 인원에게 몰렸다·편중됐다",
                  "처리 속도가 느리다·지연됐다" 등 개인의 업무량·성과·역량으로 해석될 수 있는 표현을 summary, keyFindings, insights, recommendations 어디에도 쓰지 마라.
                  분석과 시사점은 개인이 아닌 작업구분·유형·기간·프로세스 관점으로만 도출하라.
                - 결과가 0건이면 그 사실과 가능한 원인(조회 조건, 기간 등)을 설명하라.
                - 결과가 용량 제한으로 일부만 제공된 경우, 제공된 범위 내 분석임을 summary 에 명시하라.
                - 모든 텍스트는 마크다운 인라인 서식을 활용하라: 핵심 수치·건수·비율은 **굵게**,
                  컬럼명·상태코드 등 데이터 항목은 `인라인 코드` 로 강조해 임원 보고서 수준의 가독성을 확보하라.
                - 1,000 이상의 모든 수치는 천 단위 콤마로 표기하라(예: **7,082건**, **1,914**). dataTable 안의 수치도 동일하게 적용하라.
                - 결과가 집계·순위·목록 등 표로 정리하기 좋은 형태면 dataTable 에 핵심 데이터를 GFM 마크다운 표로 요약하라(최대 10행, 원본 나열이 아닌 비중·순위 등 의미 있는 재구성).
                - 합계·비율 열을 추가하면 좋다. 표가 어울리지 않으면 빈 문자열로 남겨라.
                - 표의 컬럼 수는 예시에 제한받지 말고 결과 데이터의 의미 있는 컬럼을 모두 반영하라. (단,최대 6열까지만 표시할 것)

                [응답 형식]
                반드시 아래 JSON 형식으로만 응답하라. JSON 전체를 감싸는 마크다운 코드펜스 없이 순수 JSON만 출력.
                {
                  "summary": "핵심 요약 (2~3문장)",
                  "dataTable": "GFM 마크다운표. 커럼 수와 구성은 데이터에 맞게 자유롭게 정하라. 단 최대 6열까지만. 예: | 항목 | 건수 | 비중 | 리드타임 | 처럼 필요 컬럼 모두 포함 \\n|---|---|---|---|\\n| ... | **12** | 34.3%% | 형태의 GFM 마크다운 표 (없으면 \\"\\")",
                  "keyFindings": ["구체적 수치를 포함한 주요 발견", "..."],
                  "insights": ["발견으로부터 도출한 해석과 시사점", "..."],
                  "recommendations": ["후속 분석 제안이나 실무적 제언", "..."]
                }
                """.formatted(datasetName(dataset));

        //"dataTable": "| 항목 | 건수 | 비중 |\n|---|---|---|\n| ... | **12** | 34.3%% | 형태의 GFM 마크다운 표 (없으면 \"\")",

        String result = resultJson == null ? "" : resultJson;
        boolean truncated = result.length() > MAX_RESULT_CHARS;
        if (truncated) {
            result = result.substring(0, MAX_RESULT_CHARS);
        }

        String userText = "[사용자 질문]\n" + question
                + "\n\n[실행된 SQL]\n" + sql
                + "\n\n[쿼리 실행 결과" + (truncated ? " — 용량 제한으로 앞부분만 제공됨" : "") + "]\n" + result;

        try {
            String modelText = callGemini(systemPrompt, userText);
            JsonNode report = objectMapper.readTree(stripCodeFence(modelText));
            return Map.of(
                    "summary", report.path("summary").asText(""),
                    "dataTable", report.path("dataTable").asText(""),
                    "keyFindings", toStringList(report.path("keyFindings")),
                    "insights", toStringList(report.path("insights")),
                    "recommendations", toStringList(report.path("recommendations"))
            );
        } catch (Exception e) {
            return Map.of(
                    "summary", "분석 리포트 생성 중 오류가 발생했습니다: " + e.getMessage(),
                    "dataTable", "",
                    "keyFindings", List.of(),
                    "insights", List.of(),
                    "recommendations", List.of()
            );
        }
    }

    /** Gemini generateContent 호출 후 모델 출력 텍스트(candidates[0].content.parts[0].text)를 반환 */
    private String callGemini(String systemPrompt, String userText) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.putObject("system_instruction")
                .putArray("parts").addObject().put("text", systemPrompt);
        body.putArray("contents").addObject()
                .put("role", "user")
                .putArray("parts").addObject().put("text", userText);
        ObjectNode genConfig = body.putObject("generationConfig");
        genConfig.put("temperature", 0.1);
        genConfig.put("responseMimeType", "application/json");

        String response = restClient.post()
                .uri(GEMINI_URL)
                .header("x-goog-api-key", "KEKEKE")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body.toString())
                .retrieve()
                .body(String.class);

        JsonNode root = objectMapper.readTree(response);
        return root.path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text").asText("");
    }

    private List<String> toStringList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(item -> list.add(item.asText()));
        }
        return list;
    }

    /**
     * 생성된 SQL 을 SQLCommonUtil 로 DB 에서 직접 실행한다. SELECT 문만 허용.
     *
     * @return 조회 결과 행 목록을 직렬화한 JSON 배열 문자열
     */
    public String executeQuery(String sql) throws Exception {
        validateSelectOnly(sql);

        List<Map<String, Object>> rows = SQLCommonUtil.executeQuery(sql);
        return objectMapper.writeValueAsString(rows);
    }

    /** INSERT/UPDATE/DELETE 등 데이터·구조 변경 SQL 차단 */
    private void validateSelectOnly(String sql) {
        String normalized = sql == null ? "" : sql.trim().toUpperCase(Locale.ROOT);
        if (!normalized.startsWith("SELECT")) {
            throw new IllegalArgumentException("SELECT 문만 실행할 수 있습니다.");
        }
        for (String banned : new String[]{"INSERT", "UPDATE", "DELETE", "DROP", "ALTER", "TRUNCATE", "MERGE"}) {
            if (normalized.matches(".*\\b" + banned + "\\b.*")) {
                throw new IllegalArgumentException("허용되지 않는 키워드가 포함되어 있습니다: " + banned);
            }
        }
    }

    /** 모델이 ```json ... ``` 코드펜스로 감싸 응답한 경우 제거 */
    private String stripCodeFence(String text) {
        String t = text.trim();
        if (t.startsWith("```")) {
            t = t.replaceFirst("^```[a-zA-Z]*\\s*", "").replaceFirst("\\s*```$", "");
        }
        return t;
    }
}
