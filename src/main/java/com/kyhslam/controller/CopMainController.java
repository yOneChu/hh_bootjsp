package com.kyhslam.controller;

import com.kyhslam.service.BomAnomalyService;
import com.kyhslam.service.cop.CopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * BOM 이상감지(SENTINEL)
 *
 *   GET /cop                    -> /cop/main 으로 이동
 *   GET /cop/main               : 메인 화면 (하위 화면으로 연결되는 첫 화면)
 *   GET /cop2/main              : 임원 보고용 메인 화면 (간결한 버전)
 *   GET /cop/radar              : 6F 이상감지 관제 화면
 *   GET /cop/radar/anomalies    : 관제 화면 데이터 (BomAnomalyService, 지금은 데모)
 *   GET /cop/diff               : 4F 호기 BOM 비교 화면 (?a=기준호기&b=비교호기)
 *   GET /cop/dashboard          : COP 배치 변경 대시보드 (이전호기 ↔ 시뮬호기)
 *   GET /cop/dashboard/brand    : BOM 이상감지 (브랜드 · 기종 · 블럭별 일자 변동 추적)
 *   GET /cop/dashboard/brand/history : 위 화면 데이터 (?from=yyyyMMdd&to=yyyyMMdd, PLM_SIMUL_HOGIBOM 이력)
 *   GET /cop/trace              : 3F 이상 원인 추적 화면 (?id=이상ID | ?no=호기번호&part=품번)
 *   GET /cop/trace/sales        : 3F 영업사양 원인 추정 (BOM 비교 "원인 →", ?a=기준호기&no=비교호기&block=블록번호&part=품번)
 *   GET /cop/report             : 2F 이상 리포트 화면 (?period=week|month|quarter)
 *   GET /cop/rules              : 1F 감지 규칙 관리 화면 (?id=규칙ID)
 *   GET /cop/hogi               : B1 관리대상 호기 관리 화면
 *   GET /cop/hogi/list          : 관리대상 호기 조회 (PLM_SIMUL_HOGI)
 *   POST /cop/hogi/save         : 관리대상 호기 등록 · 수정 (mode=create|update)
 *   POST /cop/hogi/delete       : 관리대상 호기 삭제 (hogis=[호기, ...])
 *
 * 하위 화면
 *   /cop/bom                    : BOM 원사이클 자동화 (BomManageController)
 */
@Controller
@RequiredArgsConstructor
public class CopMainController {

    private final BomAnomalyService bomAnomalyService;
    private final CopService copService;

    @GetMapping("/cop")
    public String root() {
        return "redirect:/cop/main";
    }

    @GetMapping("/cop/main")
    public String main() {
        return "thymeleaf/cop/bomCopMain";
    }

    /** 임원 보고용 메인 화면 (bomCopMain 의 간결한 버전) */
    @GetMapping("/cop2/main")
    public String main2() {
        return "thymeleaf/cop2/copMain";
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

    /** COP 배치 변경 대시보드 (이전호기 ↔ 시뮬호기, 지금은 화면 안의 샘플 데이터 사용) */
    @GetMapping("/cop/dashboard")
    public String dashboard() {
        return "thymeleaf/cop/copDashboard";
    }

    /** COP 배치 변경 요약 (임원 · 경진대회 발표용, 데이터는 /cop/dashboard/brand/history 와 같음) */
    @GetMapping("/cop/dashboard/preview")
    public String dashboardPreview() {
        return "thymeleaf/cop/copDashboardPreView";
    }

    /**
     * BOM 변동 원인 추적 : 같은 호기 · 블럭의 두 저장 일자를 기준일 버전으로 다시 실행해 PID 버전 · 라인 차이를 찾는다
     * ?hogi=호기&date=yyyyMMdd(바뀐 날)&prev=yyyyMMdd(앞 저장일)&block=블럭 PID&pick=PICK (block · pick · prev 는 생략 가능)
     * 데이터 : /cop/dashboard/brand/history, /simulateup/calcBlockQty · simulatePid · simulatePidPick
     */
    @GetMapping("/cop/trace/cause")
    public String traceCause() {
        return "thymeleaf/cop/copTraceCause";
    }

    /** BOM 이상감지 : 브랜드 · 기종 · 블럭별 일자(BATCHDATE) 수량 · 자재 변동 추적 (PLM_SIMUL_HOGIBOM 이력) */
    @GetMapping("/cop/dashboard/brand")
    public String brandDashboard() {
        return "thymeleaf/cop/copBrandBomDashboard";
    }

    @GetMapping("/cop/dashboard/brand/history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> brandDashboardHistory(@RequestParam(required = false) String from,
                                                                     @RequestParam(required = false) String to) {
        try {
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(copService.findBomHistory(from, to));
        } catch (Exception e) {
            return hogiFail(e, "BOM 이력을 불러오지 못했습니다.");
        }
    }

    /** 3F 이상 원인 추적 (지금은 화면 안의 샘플 추적 데이터 사용) */
    @GetMapping("/cop/trace")
    public String trace() {
        return "thymeleaf/cop/bomCopTrace"; // 기존
        //return "thymeleaf/cop2/copTrace";
    }

    /** 3F 영업사양 원인 추정 (BOM 비교 "원인 →" : 블록 PID 를 두 호기로 실행해 영업사양 비교) */
    @GetMapping("/cop/trace/sales")
    public String traceSales() {
        //http://localhost:8070/cop/trace/sales?a=기준호기&no=비교호기&block=블록번호&part=품번
        return "thymeleaf/cop2/copTraceSales";
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

    /** B1 관리대상 호기 관리 (PLM_SIMUL_HOGI 조회 · 저장 · 삭제) */
    @GetMapping("/cop/hogi")
    public String hogi() {
        return "thymeleaf/cop/bomHogiManage";
    }

    @GetMapping("/cop/hogi/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> hogiList() {
        try {
            return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                    .body(Map.of("success", true, "rows", copService.findManagedHogi()));
        } catch (Exception e) {
            return hogiFail(e, "호기 목록을 불러오지 못했습니다.");
        }
    }

    @PostMapping("/cop/hogi/save")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> hogiSave(@RequestBody Map<String, String> req) {
        try {
            copService.saveManagedHogi(req);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return hogiFail(e, "호기를 저장하지 못했습니다.");
        }
    }

    @PostMapping("/cop/hogi/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> hogiDelete(@RequestBody Map<String, List<String>> req) {
        try {
            int count = copService.deleteManagedHogi(req.get("hogis"));
            return ResponseEntity.ok(Map.of("success", true, "count", count));
        } catch (Exception e) {
            return hogiFail(e, "호기를 삭제하지 못했습니다.");
        }
    }

    /** 입력 오류는 메시지를 그대로 400 으로, 그 밖의 오류는 로그만 남기고 500 으로 */
    private ResponseEntity<Map<String, Object>> hogiFail(Exception e, String message) {
        if (e instanceof IllegalArgumentException) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(Map.of("success", false, "message", message));
    }

    @GetMapping("/cop/radar/anomalies")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> anomalies() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(bomAnomalyService.scan());
    }
}
