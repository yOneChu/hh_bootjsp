package com.kyhslam.controller.simulate;

import com.kyhslam.service.simulate.PIDSimulPlaygroundService;
import com.kyhslam.util.simulPlayground.PlaygroundRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * PID 플레이그라운드 : DB 에서 불러온 PID 로직을 화면(Handsontable)에서 고쳐 바로 실행. DB 에는 저장하지 않는다.
 */
@Controller
@RequestMapping("/simulplay")
@RequiredArgsConstructor
@Slf4j
public class PIDSimulPlaygroundController {

    private final PIDSimulPlaygroundService playgroundService;

    /**
     * @apiNote PID 플레이그라운드 화면
     */
    @GetMapping("/pidSimulPlayGround")
    public String pidSimulPlayGroundView() {
        //http://localhost:8070/simulplay/pidSimulPlayGround
        //http://localhost:8070/simulplay/pidSimulPlayGround?project=206663L01&pid=D375A
        return "thymeleaf/pid/pidSimulPlayGround";
    }

    @Description("PID 버전 목록 (항목 {version, latest, regDate, isFloorSpec})")
    @GetMapping("/pidVersions")
    @ResponseBody
    public ResponseEntity<Object> pidVersions(@RequestParam String pid) {
        //http://localhost:8070/simulplay/pidVersions?pid=D375A
        try {
            return ResponseEntity.ok(playgroundService.getPidVersions(pid));
        } catch (Exception e) {
            log.error("pidVersions error pid:{}", pid, e);
            return error(e);
        }
    }

    @Description("편집할 PID 로직 (DB 원본 라인)")
    @GetMapping("/pidLogic")
    @ResponseBody
    public ResponseEntity<Object> pidLogic(@RequestParam String pid,
                                           @RequestParam(required = false) Integer version) {
        //http://localhost:8070/simulplay/pidLogic?pid=D375A
        //http://localhost:8070/simulplay/pidLogic?pid=D375A&version=-1
        try {
            return ResponseEntity.ok(playgroundService.loadLogic(pid, version));
        } catch (Exception e) {
            log.error("pidLogic error pid:{}, version:{}", pid, version, e);
            return error(e);
        }
    }

    @Description("수정본(rows, 없으면 DB 원본)으로 PID 실행 - DB 미저장")
    @PostMapping("/simulate")
    @ResponseBody
    public ResponseEntity<Object> simulate(@RequestBody PlaygroundRequest req) {
        log.info("playground simulate project:{}, pid:{}, version:{}, floor:{}, beforePid:{}, rows:{}",
                req.project, req.pid, req.version, req.floor, req.beforePid, req.rows == null ? "DB" : req.rows.size());
        try {
            return ResponseEntity.ok(playgroundService.simulate(req));
        } catch (Exception e) {
            log.error("playground simulate error project:{}, pid:{}", req.project, req.pid, e);
            return error(e);
        }
    }

    private static ResponseEntity<Object> error(Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }
}
