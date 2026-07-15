package com.kyhslam.controller;

import com.kyhslam.service.GeminiSqlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * chatMain.html 에서 호출하는 데이터분석(자연어 → SQL) REST API.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatApiController {

    private final GeminiSqlService geminiSqlService;

    public ChatApiController(GeminiSqlService geminiSqlService) {
        this.geminiSqlService = geminiSqlService;
    }

    public record SqlRequest(String question, String dataset) {}

    public record ExecuteRequest(String sql) {}

    public record AnalyzeRequest(String question, String sql, String result, String dataset) {}

    /**
     * 자연어 질문 → Gemini SQL 생성. 응답: {"sql": "...", "explanation": "..."}
     * dataset: "nonstandard"(비표준 사양검토, 기본) | "designreq"(전산화요청)
     */
    @PostMapping("/sql")
    public ResponseEntity<Map<String, String>> generateSql(@RequestBody SqlRequest request) {
        if (request.question() == null || request.question().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("sql", "", "explanation", "질문을 입력해 주세요."));
        }
        return ResponseEntity.ok(geminiSqlService.generateSql(request.question(), request.dataset()));
    }

    /** 생성된 SQL 실행(SELECT 만 허용). 응답: executeQuery API 결과(JSON) */
    @PostMapping("/execute")
    public ResponseEntity<String> executeQuery(@RequestBody ExecuteRequest request) {
        try {
            return ResponseEntity.ok(geminiSqlService.executeQuery(request.sql()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"쿼리 실행 중 오류: " + e.getMessage() + "\"}");
        }
    }

    /**
     * 쿼리 실행 결과 → Gemini 인사이트 분석 리포트.
     * 응답: {"summary": "...", "keyFindings": [...], "insights": [...], "recommendations": [...]}
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeResult(@RequestBody AnalyzeRequest request) {
        if (request.result() == null || request.result().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "summary", "분석할 쿼리 결과가 없습니다.",
                    "dataTable", "",
                    "keyFindings", java.util.List.of(),
                    "insights", java.util.List.of(),
                    "recommendations", java.util.List.of()));
        }
        return ResponseEntity.ok(geminiSqlService.analyzeResult(
                request.question(), request.sql(), request.result(), request.dataset()));
    }
}
