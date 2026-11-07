package com.kyhslam.controller.simulate;

import com.kyhslam.service.simulate.PIDBlockQtyCompareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 일자별 블럭 수량 계산 비교 (기준일마다 블럭 PID 수량 계산 + 일자별 영업사양 지정. DB 미반영)
 *
 *   GET  /simulateup/blockQtyCompare          : 화면
 *   POST /simulateup/blockQtyCompare/extract  : { project, pid, floor, beforePid, pickCmt, dates: ["yyyy-MM-dd" | ""] }
 *   POST /simulateup/blockQtyCompare/run      : { project, pid, floor, beforePid, pickCmt, columns: [{ date, overrides: {코드: 값} }] }
 *   pickCmt : true 면 PICK 마다 BLOCKNO$SF 의 주석 PID(CMTn) 실행 결과를 PickRun.cmt 에 넣는다
 *
 * 일자는 최대 5개, "" 은 최신 버전. 실패 시 { success: false, message }
 */
@Controller
@RequestMapping("/simulateup/blockQtyCompare")
@RequiredArgsConstructor
@Slf4j
public class PIDBlockQtyCompareController {

    private final PIDBlockQtyCompareService service;

    @GetMapping
    public String view() {
        //http://localhost:8070/simulateup/blockQtyCompare
        //http://localhost:8070/simulateup/blockQtyCompare?project=206663L01&pid=B181B&dates=2026-09-01,2026-10-01
        return "thymeleaf/cop/bomBlockCalculator";
    }

    @Description("일자별 수량 계산에 쓰이는 영업사양 추출")
    @PostMapping("/extract")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> extract(@RequestBody Map<String, Object> req) {
        String project = str(req.get("project")), pid = str(req.get("pid")).toUpperCase();
        List<String> dates = new ArrayList<>();
        if (req.get("dates") instanceof List<?> list)
            for (Object o : list) dates.add(date(o));
        log.info("blockQtyCompare extract project:{}, pid:{}, floor:{}, beforePid:{}, dates:{}", project, pid, req.get("floor"), req.get("beforePid"), dates);
        try {
            return ResponseEntity.ok(service.extract(project, pid, str(req.get("floor")), beforePids(req.get("beforePid")), dates,
                    Boolean.TRUE.equals(req.get("pickCmt"))));
        } catch (Exception e) {
            log.error("blockQtyCompare extract error project:{}, pid:{}", project, pid, e);
            return ResponseEntity.ok(fail(e));
        }
    }

    @Description("일자별 수량 계산 (일자마다 영업사양 지정값 적용)")
    @PostMapping("/run")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> run(@RequestBody Map<String, Object> req) {
        String project = str(req.get("project")), pid = str(req.get("pid")).toUpperCase();
        List<PIDBlockQtyCompareService.Column> cols = new ArrayList<>();
        if (req.get("columns") instanceof List<?> list) {
            for (Object o : list) {
                if (!(o instanceof Map<?, ?> m))
                    continue;
                PIDBlockQtyCompareService.Column c = new PIDBlockQtyCompareService.Column();
                c.date = date(m.get("date"));
                if (m.get("overrides") instanceof Map<?, ?> ov) {
                    for (Map.Entry<?, ?> e : ov.entrySet()) {
                        String code = str(e.getKey());
                        if (!code.isEmpty())
                            c.overrides.put(code, e.getValue() == null ? "" : String.valueOf(e.getValue()).trim());
                    }
                }
                cols.add(c);
            }
        }
        log.info("blockQtyCompare run project:{}, pid:{}, floor:{}, beforePid:{}, columns:{}", project, pid, req.get("floor"), req.get("beforePid"), cols.size());
        try {
            return ResponseEntity.ok(service.run(project, pid, str(req.get("floor")), beforePids(req.get("beforePid")), cols,
                    Boolean.TRUE.equals(req.get("pickCmt"))));
        } catch (Exception e) {
            log.error("blockQtyCompare run error project:{}, pid:{}", project, pid, e);
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

    /** "" → null (최신 버전). 형식 검사는 기준일 저장소(SimPidRepositoryAsOf)가 한다 */
    private static String date(Object v) {
        String s = str(v);
        return s.isEmpty() ? null : s;
    }

    /** "a, b\nc" → "a\nb\nc" (전처리 PID 형식 : 줄바꿈 구분, 대문자). 없으면 null */
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
            sb.append(p.trim().toUpperCase());
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}
