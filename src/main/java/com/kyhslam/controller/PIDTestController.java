package com.kyhslam.controller;

import com.kyhslam.service.PIDTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/pidTest")
@RequiredArgsConstructor
@Slf4j
public class PIDTestController {

    private final PIDTestService pidTestService;

    /**
     * @apiNote PID Tester 화면 (기준 버전 vs 대상 버전 결과 비교)
     * @return
     */
    @GetMapping("/pidTest")
    public String pidTestView() {
        //http://localhost:8070/pidTest/pidTest
        return "thymeleaf/pidTest/pidTest";
    }

    @Description("PID 테스트 실행 (body : pidTest.html getTestParam() 의 JSON)")
    @PostMapping("/run")
    @ResponseBody
    public ResponseEntity<Object> run(@RequestBody(required = false) Map<String, Object> paramMap) {
        log.info("pidTest run param:{}", paramMap);

        try {
            return ResponseEntity.ok(pidTestService.run(paramMap == null ? new HashMap<>() : paramMap));
        } catch (IllegalArgumentException e) {
            // 입력값 오류 (PID 누락, 버전 형식 등)
            return ResponseEntity.badRequest().body(fail(e.getMessage()));
        } catch (Exception e) {
            log.error("pidTest run error param:{}", paramMap, e);
            return ResponseEntity.internalServerError().body(fail(e.getClass().getSimpleName() + " : " + e.getMessage()));
        }
    }

    private static Map<String, Object> fail(String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }
}
