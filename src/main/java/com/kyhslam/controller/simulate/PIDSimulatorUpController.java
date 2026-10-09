package com.kyhslam.controller.simulate;

import com.kyhslam.service.simulate.PIDSimulatorUpService;
import com.kyhslam.util.pidSimulatorUp.PIDSimulResult;
import com.kyhslam.util.pidSimulatorUp.SimConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/simulateup")
@RequiredArgsConstructor
@Slf4j
public class PIDSimulatorUpController {

    private final PIDSimulatorUpService pidSimulatorUpService;

    /**
     * @apiNote PID 시뮬레이터 화면 (PLM DB 직접 실행)
     * @return
     */
    @GetMapping("/pidSimulatorUp")
    @CrossOrigin
    public String pidSimulatorUpView() {
        //http://localhost:8070/simulateup/pidSimulatorUp
        //http://localhost:8070/simulateup/pidSimulatorUp?project=206663L01&pid=D375A&version=-1
        return "thymeleaf/pid/pidSimulatorUp";
    }

    @Description("PID 시뮬레이션 (PIDSimulator 화면의 [실행] : 행별 조건/결과, 최종 결과값, 최종 영업사양)")
    @GetMapping("/simulatePid")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<Object> simulatePid(@RequestParam String project,
                                              @RequestParam String pid,
                                              @RequestParam(required = false) String floor,
                                              @RequestParam(required = false) Integer version,
                                              @RequestParam(defaultValue = "false") boolean test,
                                              @RequestParam(required = false) String beforePid,
                                              @RequestParam(required = false) String subVersions) {
        //http://localhost:8070/simulateup/simulatePid?project=206663L01&pid=D375A
        //http://localhost:8070/simulateup/simulatePid?project=206663L01&pid=D375A&version=3
        //http://localhost:8070/simulateup/simulatePid?project=206663L01&pid=D375A&version=-1&floor=1F&beforePid=EL_P001,EL_P002
        //http://localhost:8070/simulateup/simulatePid?project=206663L01&pid=D375A&subVersions=EL_P001:3,EL_P002:-1
        log.info("simulatePid project:{}, pid:{}, floor:{}, version:{}, test:{}, beforePid:{}, subVersions:{}",
                project, pid, floor, version, test, beforePid, subVersions);

        try {
            // 버전을 고르면 그 버전, 아니면 테스트 토글(-1), 둘 다 없으면 최신(null)
            Integer runVersion = version != null ? version : (test ? (Integer) SimConsts.TEST_VERSION : null);
            PIDSimulResult result = pidSimulatorUpService.simulate(project, pid, floor, runVersion, splitParam(beforePid),
                    parseSubVersions(subVersions));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("simulatePid error project:{}, pid:{}", project, pid, e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @Description("PID 버전 목록 (TEST 버전 먼저, 그 다음 최신순 / 항목 {version, latest})")
    @GetMapping("/pidVersions")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<Object> pidVersions(@RequestParam String pid) {
        //http://localhost:8070/simulateup/pidVersions?pid=D375A
        try {
            return ResponseEntity.ok(pidSimulatorUpService.getPidVersions(pid));
        } catch (Exception e) {
            log.error("pidVersions error pid:{}", pid, e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /** "EL_P001:3,EL_P002:-1" → {EL_P001=3, EL_P002=-1} (형식이 틀리면 오류) */
    private static Map<String, Integer> parseSubVersions(String value) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String item : splitParam(value)) {
            int i = item.lastIndexOf(':');
            if (i <= 0 || i == item.length() - 1)
                throw new IllegalArgumentException("하위 PID 버전 형식이 잘못되었습니다 (PID:버전) : " + item);
            try {
                map.put(item.substring(0, i).trim(), Integer.valueOf(item.substring(i + 1).trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("하위 PID 버전이 숫자가 아닙니다 : " + item);
            }
        }
        return map;
    }

    /** "a, b,c" → [a, b, c] (빈 값 제외) */
    private static List<String> splitParam(String value) {
        List<String> list = new ArrayList<>();
        if (value == null)
            return list;
        for (String v : value.split("[,\\s]+")) {
            if (!v.trim().isEmpty())
                list.add(v.trim());
        }
        return list;
    }
}
