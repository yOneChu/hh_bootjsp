package com.kyhslam.controller.simulate;

import com.kyhslam.service.simulate.PIDSpecOverrideService;
import com.kyhslam.util.pidSimulatorUp.SimConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 영업사양 지정 PID 실행 (PID 의 SPEC 열 중 영업사양 코드만 값을 바꿔 실행. DB 미반영)
 *
 *   GET  /simulateup/specOverride          : 화면
 *   POST /simulateup/specOverride/extract  : { project, pid, version, floor, beforePid, includeSub } → 영업사양 목록
 *   POST /simulateup/specOverride/run      : { project, pid, version, floor, beforePid, overrides: {코드: 값} } → OUTPUT 비교
 *
 * version : "" 또는 없음 = 최신, "-1" = TEST, 숫자 = 지정 버전
 * 응답 실패 시 : { success: false, message }
 */
@Controller
@RequestMapping("/simulateup/specOverride")
@RequiredArgsConstructor
@Slf4j
public class PIDSpecOverrideController {

    private final PIDSpecOverrideService service;

    @GetMapping
    public String view() {
        //http://localhost:8070/simulateup/specOverride
        //http://localhost:8070/simulateup/specOverride?project=206663L01&pid=D375A
        return "thymeleaf/cop/bomCalculatorResult";
    }

    @Description("영업사양 추출 : 공사값으로 한 번 실행해 SPEC 열의 영업사양 코드만 뽑는다")
    @PostMapping("/extract")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> extract(@RequestBody Map<String, Object> req) {
        String project = str(req.get("project")), pid = str(req.get("pid"));
        log.info("specOverride extract project:{}, pid:{}, version:{}, floor:{}, beforePid:{}, includeSub:{}",
                project, pid, req.get("version"), req.get("floor"), req.get("beforePid"), req.get("includeSub"));
        try {
            return ResponseEntity.ok(service.extract(project, pid, str(req.get("floor")), version(req.get("version")),
                    beforePids(req.get("beforePid")), !Boolean.FALSE.equals(req.get("includeSub"))));
        } catch (Exception e) {
            log.error("specOverride extract error project:{}, pid:{}", project, pid, e);
            return ResponseEntity.ok(fail(e));
        }
    }

    @Description("지정값 실행 : 공사값 실행과 지정값 실행의 최종 OUTPUT 비교")
    @PostMapping("/run")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> run(@RequestBody Map<String, Object> req) {
        String project = str(req.get("project")), pid = str(req.get("pid"));
        Map<String, String> overrides = new LinkedHashMap<>();
        if (req.get("overrides") instanceof Map<?, ?> m) {
            for (Map.Entry<?, ?> e : m.entrySet()) {
                String code = str(e.getKey());
                if (code.isEmpty())
                    continue;
                overrides.put(code, e.getValue() == null ? "" : String.valueOf(e.getValue()).trim());
            }
        }
        log.info("specOverride run project:{}, pid:{}, version:{}, floor:{}, beforePid:{}, overrides:{}",
                project, pid, req.get("version"), req.get("floor"), req.get("beforePid"), overrides);
        try {
            return ResponseEntity.ok(service.run(project, pid, str(req.get("floor")), version(req.get("version")),
                    beforePids(req.get("beforePid")), overrides));
        } catch (Exception e) {
            log.error("specOverride run error project:{}, pid:{}", project, pid, e);
            return ResponseEntity.ok(fail(e));
        }
    }

    // ------------------------------------------------------------------ 내부

    private static Map<String, Object> fail(Exception e) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", false);
        res.put("message", e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        return res;
    }

    private static String str(Object v) {
        return v == null ? "" : String.valueOf(v).trim();
    }

    /** "" → null(최신), "-1" → TEST, 숫자 → 지정 버전 */
    private static Integer version(Object v) {
        String s = str(v);
        if (s.isEmpty())
            return null;
        if ("TEST".equalsIgnoreCase(s))
            return SimConsts.TEST_VERSION;
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("버전이 숫자가 아닙니다 : " + s);
        }
    }

    /** "a, b\nc" → "a\nb\nc" (PIDSimul 의 전처리 PID 형식 : 줄바꿈 구분). 없으면 null */
    private static String beforePids(Object v) {
        String s = str(v);
        if (s.isEmpty())
            return null;
        StringBuilder sb = new StringBuilder();
        for (String p : s.split("[,\\s]+")) {
            if (p.trim().isEmpty())
                continue;
            if (sb.length() > 0)
                sb.append('\n');
            sb.append(p.trim());
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}
