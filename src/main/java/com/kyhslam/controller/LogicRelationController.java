package com.kyhslam.controller;

import com.kyhslam.service.LogicRelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PID 관계 분석 (로직 Viewer 의 Maptify 버튼으로 여는 화면)
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class LogicRelationController {

    private final LogicRelationService logicRelationService;

    /** 화면 : /subae/logicRelation?pid=EL_PB126A01 */
    @GetMapping("/subae/logicRelation")
    public String logicRelationView() {
        return "thymeleaf/logicViewRelation";
    }

    /** 화면 : /subae/pidLogicRelation?pid=EL_PB126A01 (PID 관계 분석 워크벤치. 데이터는 아래 두 API 를 같이 쓴다) */
    @GetMapping("/subae/pidLogicRelation")
    public String pidLogicRelationView() {
        return "thymeleaf/pid/pidLogicRelation";
    }

    /** 기준 PID 의 상위 / 하위 호출 관계 */
    @GetMapping("/subae/logicRelationData")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logicRelationData(@RequestParam String pid,
                                                                 @RequestParam(defaultValue = "false") boolean refresh) {
        try {
            return ResponseEntity.ok(logicRelationService.relation(pid, refresh));
        } catch (Exception e) {
            return error(e);
        }
    }

    /** 한 PID 의 상세 (직접 호출 관계, 쓰는 사양, 결과 KEY) */
    @GetMapping("/subae/logicRelationDetail")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logicRelationDetail(@RequestParam String pid) {
        try {
            return ResponseEntity.ok(logicRelationService.detail(pid));
        } catch (Exception e) {
            return error(e);
        }
    }

    /** 사양 출처 요청 : 기준 PID 와 그 PID 로직에서 쓰는 사양 CODE 들 (화면에서 고친 로직 기준으로 보낸다) */
    public record SpecOriginRequest(String pid, List<String> codes, Boolean refresh) {}

    /** 사양마다 영업사양(특성코드 마스터)인지, 어떤 PID 가 KEY 로 선언하는지 (상위 / 하위 / 무관) */
    @PostMapping("/subae/specOrigin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> specOrigin(@RequestBody SpecOriginRequest req) {
        try {
            List<String> codes = req.codes() == null ? List.of() : req.codes();
            if (codes.size() > 3000) codes = codes.subList(0, 3000);
            return ResponseEntity.ok(logicRelationService.specOrigin(req.pid(), codes, Boolean.TRUE.equals(req.refresh())));
        } catch (Exception e) {
            return error(e);
        }
    }

    private static ResponseEntity<Map<String, Object>> error(Exception e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
