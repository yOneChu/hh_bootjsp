package com.kyhslam.controller;

import com.kyhslam.service.BomAnomalyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * BOM 이상감지(SENTINEL)
 *
 *   GET /cop                    -> /cop/main 으로 이동
 *   GET /cop/main               : 메인 화면 (하위 화면으로 연결되는 첫 화면)
 *   GET /cop/radar              : 6F 이상감지 관제 화면
 *   GET /cop/radar/anomalies    : 관제 화면 데이터 (BomAnomalyService, 지금은 데모)
 *   GET /cop/diff               : 4F 호기 BOM 비교 화면 (?a=기준호기&b=비교호기)
 *   GET /cop/trace              : 3F 이상 원인 추적 화면 (?id=이상ID | ?no=호기번호&part=품번)
 *   GET /cop/report             : 2F 이상 리포트 화면 (?period=week|month|quarter)
 *   GET /cop/rules              : 1F 감지 규칙 관리 화면 (?id=규칙ID)
 *
 * 하위 화면
 *   /cop/bom                    : BOM 원사이클 자동화 (BomManageController)
 */
@Controller
@RequiredArgsConstructor
public class CopMainController {

    private final BomAnomalyService bomAnomalyService;

    @GetMapping("/cop")
    public String root() {
        return "redirect:/cop/main";
    }

    @GetMapping("/cop/main")
    public String main() {
        return "thymeleaf/cop/bomCopMain";
    }

    @GetMapping("/cop/radar")
    public String radar() {
        return "thymeleaf/cop/bomRadar";
    }

    /** 4F 호기 BOM 비교 (지금은 화면 안의 샘플 데이터 사용) */
    @GetMapping("/cop/diff")
    public String diff() {
        return "thymeleaf/cop/bomCopDiff";
    }

    /** 3F 이상 원인 추적 (지금은 화면 안의 샘플 추적 데이터 사용) */
    @GetMapping("/cop/trace")
    public String trace() {
        return "thymeleaf/cop/bomCopTrace";
    }

    /** 2F 이상 리포트 (지금은 화면 안의 샘플 집계 사용) */
    @GetMapping("/cop/report")
    public String report() {
        return "thymeleaf/cop/bomCopReport";
    }

    /** 1F 감지 규칙 관리 (지금은 화면 안의 샘플 규칙 · 브라우저 저장) */
    @GetMapping("/cop/rules")
    public String rules() {
        return "thymeleaf/cop/bomCopRules";
    }

    @GetMapping("/cop/radar/anomalies")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> anomalies() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(bomAnomalyService.scan());
    }
}
