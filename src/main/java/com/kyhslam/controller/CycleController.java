package com.kyhslam.controller;

import com.kyhslam.service.OneCycleFunc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PLM 원사이클 API.
 *
 * 모든 요청은 프로젝트호기번호(productNo)를 받아 영업사양 ouid 로 변환한 뒤 처리한다.
 *
 * - POST /cycle/oneCycle?productNo=xxxxx      : 로그인 -> WIP 생성 -> 종속사양 산출
 * - POST /cycle/makeWip?productNo=xxxxx       : WIP 생성만
 * - POST /cycle/jongsoksung?productNo=xxxxx   : 종속사양 산출만
 * - GET  /cycle/loginCheck                    : PLM 로그인 확인
 */
@RestController()
@RequestMapping("/cycle")
@RequiredArgsConstructor
@Slf4j
public class CycleController {

    private final OneCycleFunc oneCycleFunc;

    @Description("원사이클 - 로그인 후 WIP 생성, 종속사양 산출")
    @PostMapping("/oneCycle")
    public ResponseEntity<OneCycleFunc.OneCycleResult> oneCycle(@RequestParam String productNo) {
        //http://localhost:8070/cycle/oneCycle?productNo=xxxxx

        if (isBlank(productNo)) {
            return ResponseEntity.badRequest().build();
        }

        log.info("원사이클 요청. productNo = {}", productNo);
        OneCycleFunc.OneCycleResult result = oneCycleFunc.runOneCycle(productNo);
        log.info("원사이클 결과. {}", result);

        return ResponseEntity.ok(result);
    }

    @Description("WIP 생성")
    @PostMapping("/makeWip")
    public ResponseEntity<Map<String, Object>> makeWip(@RequestParam String productNo) {
        //http://localhost:8070/cycle/makeWip?productNo=xxxxx

        if (isBlank(productNo)) {
            return ResponseEntity.badRequest().build();
        }

        String vfOuid = OneCycleFunc.toObjectOuid(productNo);
        if (isBlank(vfOuid)) {
            return ResponseEntity.ok(result(productNo, vfOuid, false, "영업사양(WIP)을 찾지 못했습니다."));
        }

        OneCycleFunc.PlmSession session = oneCycleFunc.login();
        if (session == null) {
            return ResponseEntity.ok(result(productNo, vfOuid, false, "PLM 로그인 실패"));
        }

        String message = oneCycleFunc.makeWip(session, vfOuid);
        return ResponseEntity.ok(result(productNo, vfOuid, true, message));
    }

    @Description("종속사양 산출")
    @PostMapping("/jongsoksung")
    public ResponseEntity<Map<String, Object>> jongsoksung(@RequestParam String productNo) {
        //http://localhost:8070/cycle/jongsoksung?productNo=xxxxx

        if (isBlank(productNo)) {
            return ResponseEntity.badRequest().build();
        }

        String vfOuid = OneCycleFunc.toObjectOuid(productNo);
        if (isBlank(vfOuid)) {
            return ResponseEntity.ok(result(productNo, vfOuid, false, "영업사양(WIP)을 찾지 못했습니다."));
        }

        OneCycleFunc.PlmSession session = oneCycleFunc.login();
        if (session == null) {
            return ResponseEntity.ok(result(productNo, vfOuid, false, "PLM 로그인 실패"));
        }

        String message = oneCycleFunc.executeJongsoksung(session, vfOuid);
        return ResponseEntity.ok(result(productNo, vfOuid, message != null, message));
    }

    @Description("PLM 로그인 확인")
    @GetMapping("/loginCheck")
    public ResponseEntity<Map<String, Object>> loginCheck() {
        //http://localhost:8070/cycle/loginCheck

        boolean loginSuccess = oneCycleFunc.login() != null;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", loginSuccess);
        body.put("message", loginSuccess ? "PLM 로그인 성공" : "PLM 로그인 실패");

        return ResponseEntity.ok(body);
    }

    private Map<String, Object> result(String productNo, String vfOuid, boolean success, String message) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", success);
        body.put("productNo", productNo);
        body.put("objectOuid", vfOuid);
        body.put("message", message);

        return body;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
