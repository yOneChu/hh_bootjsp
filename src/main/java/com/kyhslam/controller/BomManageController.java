package com.kyhslam.controller;

import com.kyhslam.service.OneCycleFunc;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

//COP

/**
 * PLM 원사이클 화면 (BOM 자동화)
 *
 * 사용자가 본인 사번/비번으로 PLM 에 로그인하면, 발급된 PLM 세션(JSESSIONID)을 HttpSession 에 보관하고
 * 이후 단계 호출은 모두 그 세션으로 수행한다. (비밀번호는 저장하지 않는다)
 *
 *   GET  /cop/bom                 : 화면
 *   GET  /cop/bom/oneCycle?hogi=  : 호기별 원사이클 실행 팝업 (관리대상 호기 화면에서 연다)
 *   POST /cop/bom/login           : { userid, pwd }                -> PLM 로그인
 *   POST /cop/bom/autoLogin       :                                -> 설정(plm.user-id / plm.password) 계정으로 PLM 로그인
 *   POST /cop/bom/logout         :                                -> PLM 세션 폐기
 *   GET  /cop/bom/me              :                                -> 로그인 상태
 *   POST /cop/bom/resolve         : { productNo }                  -> 호기번호 -> 영업사양 ouid
 *   POST /cop/bom/wip             : { ouid }                       -> WIP 생성
 *   POST /cop/bom/equal           : { ouid }                       -> 동일정보 만들기 (신규 ouid / 호기번호)
 *   POST /cop/bom/attr            : { ouid, attrs: {코드: 값} }    -> 속성값 변경 + 변경 후 값
 *   POST /cop/bom/attrValues      : { ouid, codes: [코드] }        -> 현재 속성값 조회
 *   POST /cop/bom/jongsoksung     : { ouid }                       -> 종속사양 산출
 *   POST /cop/bom/bom             : { ouid, c, m, f, 1, 2, 3 }     -> BOM 계산 + 호기 BOM 저장(PLM_SIMUL_HOGIBOM) + 배치 날짜 갱신(PLM_SIMUL_HOGI)
 *
 * 응답 공통 : { success, message, elapsedMillis, ... }
 * PLM 로그인이 안 되어 있으면 401 + { success:false, needLogin:true }
 */
@Controller
@RequestMapping("/cop/bom")
@RequiredArgsConstructor
@Slf4j
public class BomManageController {

    private static final String SESSION_KEY = "COP_BOM_PLM_SESSION";
    private static final String USER_KEY = "COP_BOM_PLM_USER";

    /** 영업사양 ouid 형식 (elv_info$vf@16진수) */
    private static final Pattern OUID_PATTERN = Pattern.compile("^elv_info\\$vf@[0-9a-fA-F]+$");

    /** 특성코드 형식 (영문/숫자/_/$) */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_$]{1,60}$");

    private final OneCycleFunc oneCycleFunc;

    @GetMapping
    public String view() {
        return "thymeleaf/cop/bomManage";
    }

    @GetMapping("/oneCycle")
    public String oneCycle() {
        return "thymeleaf/cop/bomOneCycle";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> req, HttpSession httpSession) {

        String userid = trim(req.get("userid"));
        String pwd = req.get("pwd");
        if (userid.isEmpty() || pwd == null || pwd.isEmpty()) {
            return ResponseEntity.badRequest().body(body(false, "사번과 비밀번호를 입력하세요.", 0));
        }

        long start = System.currentTimeMillis();
        OneCycleFunc.PlmSession plmSession = oneCycleFunc.login(userid, pwd);
        return loginResult(plmSession, userid, "PLM 로그인에 실패했습니다. 사번/비밀번호를 확인하세요.", start, httpSession);
    }

    /** 원사이클 팝업용 : 화면에서 비밀번호를 받지 않고 서버 설정의 계정으로 로그인한다. */
    @PostMapping("/autoLogin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> autoLogin(HttpSession httpSession) {
        long start = System.currentTimeMillis();
        OneCycleFunc.PlmSession plmSession = oneCycleFunc.login();
        return loginResult(plmSession, oneCycleFunc.getPlmUserId(),
                "PLM 자동 로그인에 실패했습니다. 서버의 plm.user-id / plm.password 설정을 확인하세요.", start, httpSession);
    }

    private ResponseEntity<Map<String, Object>> loginResult(OneCycleFunc.PlmSession plmSession, String userid,
                                                            String failMessage, long start, HttpSession httpSession) {
        long elapsed = System.currentTimeMillis() - start;

        if (plmSession == null) {
            httpSession.removeAttribute(SESSION_KEY);
            httpSession.removeAttribute(USER_KEY);
            return ResponseEntity.ok(body(false, failMessage, elapsed));
        }

        httpSession.setAttribute(SESSION_KEY, plmSession);
        httpSession.setAttribute(USER_KEY, userid);
        log.info("[BOM 자동화] PLM 로그인. userid = {}", userid);

        Map<String, Object> res = body(true, "PLM 로그인 성공", elapsed);
        res.put("userid", userid);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/logout")
    @ResponseBody
    public Map<String, Object> logout(HttpSession httpSession) {
        httpSession.removeAttribute(SESSION_KEY);
        httpSession.removeAttribute(USER_KEY);
        return body(true, "로그아웃 되었습니다.", 0);
    }

    @GetMapping("/me")
    @ResponseBody
    public Map<String, Object> me(HttpSession httpSession) {
        Object user = httpSession.getAttribute(USER_KEY);
        boolean loggedIn = user != null && httpSession.getAttribute(SESSION_KEY) != null;
        Map<String, Object> res = body(loggedIn, loggedIn ? "로그인 상태" : "로그인 필요", 0);
        res.put("userid", loggedIn ? user : null);
        return res;
    }

    @PostMapping("/resolve")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resolve(@RequestBody Map<String, String> req) {

        String productNo = trim(req.get("productNo"));
        if (productNo.isEmpty()) {
            return ResponseEntity.badRequest().body(body(false, "프로젝트호기번호를 입력하세요.", 0));
        }

        long start = System.currentTimeMillis();
        String ouid = OneCycleFunc.toObjectOuid(productNo);
        boolean found = ouid != null && !ouid.isBlank();

        Map<String, Object> res = body(found,
                found ? "영업사양(WIP) 확인" : "프로젝트호기번호에 해당하는 영업사양(WIP)을 찾지 못했습니다.",
                System.currentTimeMillis() - start);
        res.put("productNo", productNo);
        res.put("ouid", found ? ouid : null);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/wip")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> wip(@RequestBody Map<String, Object> req, HttpSession httpSession) {
        return run(req, httpSession, (session, ouid, res) -> {
            String message = oneCycleFunc.makeWip(session, ouid);
            // WIP 생성은 이미 WIP 인 경우 오류 메시지가 오지만 다음 단계 진행에는 문제가 없다.
            res.put("success", message != null && !message.startsWith("세션이 만료"));
            res.put("message", message);
        });
    }

    @PostMapping("/equal")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> equal(@RequestBody Map<String, Object> req, HttpSession httpSession) {
        return run(req, httpSession, (session, ouid, res) -> {
            String newOuid = oneCycleFunc.makeEqualInfo(session, ouid);
            if (newOuid == null) {
                res.put("success", false);
                res.put("message", "동일정보 생성에 실패했습니다.");
                return;
            }

            // 채번이 늦게 보일 수 있어 잠시 재시도
            String newNo = "";
            for (int i = 0; i < 3 && newNo.isBlank(); i++) {
                newNo = OneCycleFunc.findProductNo(newOuid);
                if (newNo.isBlank()) sleep(2000);
            }

            res.put("success", true);
            res.put("newOuid", newOuid);
            res.put("newProductNo", newNo);
            res.put("message", newNo.isBlank() ? "동일정보 생성 완료 (신규 호기번호 조회 지연)" : "동일정보 생성 완료 → " + newNo);
        });
    }

    @PostMapping("/attr")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> attr(@RequestBody Map<String, Object> req, HttpSession httpSession) {

        Map<String, Object> attrs = new LinkedHashMap<>();
        if (req.get("attrs") instanceof Map<?, ?> m) {
            for (Map.Entry<?, ?> e : m.entrySet()) {
                String code = trim(String.valueOf(e.getKey()));
                if (!CODE_PATTERN.matcher(code).matches()) {
                    return ResponseEntity.badRequest().body(body(false, "특성코드 형식이 올바르지 않습니다 : " + code, 0));
                }
                attrs.put(code, e.getValue() == null ? "" : String.valueOf(e.getValue()).trim());
            }
        }
        if (attrs.isEmpty()) {
            return ResponseEntity.badRequest().body(body(false, "변경할 속성을 입력하세요.", 0));
        }

        return run(req, httpSession, (session, ouid, res) -> {
            String message = oneCycleFunc.changeAttr(session, ouid, attrs);
            if (message == null) {
                res.put("success", false);
                res.put("message", "속성정보 변경에 실패했습니다.");
                return;
            }

            Map<String, String> current = oneCycleFunc.getAttrValues(session, ouid, attrs.keySet());
            boolean verified = true;
            for (Map.Entry<String, Object> e : attrs.entrySet()) {
                // 빈 값(공백)으로 보낸 항목은 PLM 이 null 로 돌려줄 수 있으므로 null 을 "" 로 보고 비교한다.
                String now = current.get(e.getKey());
                if (!String.valueOf(e.getValue()).equals(now == null ? "" : now)) verified = false;
            }

            res.put("success", true);
            res.put("message", message);
            res.put("requested", attrs);
            res.put("current", current);
            res.put("verified", verified);
        });
    }

    @PostMapping("/attrValues")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> attrValues(@RequestBody Map<String, Object> req, HttpSession httpSession) {

        List<String> codes = new ArrayList<>();
        if (req.get("codes") instanceof List<?> list) {
            for (Object o : list) {
                String code = trim(String.valueOf(o));
                if (CODE_PATTERN.matcher(code).matches()) codes.add(code);
            }
        }
        if (codes.isEmpty()) {
            return ResponseEntity.badRequest().body(body(false, "조회할 특성코드를 입력하세요.", 0));
        }

        return run(req, httpSession, (session, ouid, res) -> {
            Map<String, String> current = oneCycleFunc.getAttrValues(session, ouid, codes);
            res.put("success", !current.isEmpty());
            res.put("message", current.isEmpty() ? "속성값을 조회하지 못했습니다." : "현재 속성값 조회 완료");
            res.put("current", current);
        });
    }

    @PostMapping("/jongsoksung")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> jongsoksung(@RequestBody Map<String, Object> req, HttpSession httpSession) {
        return run(req, httpSession, (session, ouid, res) -> {
            String message = oneCycleFunc.executeJongsoksung(session, ouid);
            res.put("success", message != null);
            res.put("message", message != null ? message : "종속사양 산출 응답을 확인하지 못했습니다.");
        });
    }

    @PostMapping("/bom")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bom(@RequestBody Map<String, Object> req, HttpSession httpSession) {

        List<String> parts = new ArrayList<>();
        for (String part : List.of(OneCycleFunc.BOM_C, OneCycleFunc.BOM_M, OneCycleFunc.BOM_F,
                OneCycleFunc.BOM_1, OneCycleFunc.BOM_2, OneCycleFunc.BOM_3)) {
            if (flag(req.get(part))) {
                parts.add(part);
            }
        }
        if (parts.isEmpty()) {
            return ResponseEntity.badRequest().body(body(false, "전개 옵션을 하나 이상 선택하세요.", 0));
        }

        return run(req, httpSession, (session, ouid, res) -> {
            String message = oneCycleFunc.bomCalStart(session, ouid, parts.toArray(String[]::new));
            res.put("success", message != null);
            res.put("message", message != null ? message : "BOM 계산 응답을 확인하지 못했습니다.");

            // 호기 BOM 저장 + 배치 날짜 갱신 (결과는 BOM 계산 성공 여부에 영향을 주지 않는다)
            if (message != null) {
                String productNo = OneCycleFunc.findProductNo(ouid);
                res.put("bomSaveMessage", oneCycleFunc.saveHogiBom(productNo));
                oneCycleFunc.updateHogiBatchDate(productNo);
            }
        });
    }


    // ================================================================
    // 공통
    // ================================================================

    @FunctionalInterface
    private interface Step {
        void run(OneCycleFunc.PlmSession session, String ouid, Map<String, Object> res);
    }

    /** 로그인 / ouid 확인 후 단계를 수행하고 수행 시간을 붙여 응답한다. */
    private ResponseEntity<Map<String, Object>> run(Map<String, Object> req, HttpSession httpSession, Step step) {

        Object attr = httpSession.getAttribute(SESSION_KEY);
        if (!(attr instanceof OneCycleFunc.PlmSession session)) {
            Map<String, Object> res = body(false, "PLM 로그인이 필요합니다.", 0);
            res.put("needLogin", true);
            return ResponseEntity.status(401).body(res);
        }

        String ouid = trim(req.get("ouid") == null ? null : String.valueOf(req.get("ouid")));
        if (!OUID_PATTERN.matcher(ouid).matches()) {
            return ResponseEntity.badRequest().body(body(false, "대상 호기를 먼저 확인하세요.", 0));
        }

        long start = System.currentTimeMillis();
        Map<String, Object> res = body(false, null, 0);
        res.put("ouid", ouid);
        try {
            step.run(session, ouid, res);
        } catch (Exception e) {
            log.error("[BOM 자동화] 처리 오류. ouid = {}", ouid, e);
            res.put("success", false);
            res.put("message", "처리 중 오류 : " + e.getMessage());
        }
        res.put("elapsedMillis", System.currentTimeMillis() - start);

        // PLM 세션이 끊긴 경우 화면에서 다시 로그인하도록 알린다.
        Object message = res.get("message");
        if (message != null && String.valueOf(message).startsWith("세션이 만료")) {
            httpSession.removeAttribute(SESSION_KEY);
            res.put("needLogin", true);
        }
        return ResponseEntity.ok(res);
    }

    private Map<String, Object> body(boolean success, String message, long elapsed) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", success);
        res.put("message", message);
        res.put("elapsedMillis", elapsed);
        return res;
    }

    private boolean flag(Object v) {
        return v instanceof Boolean b ? b : v != null && "true".equalsIgnoreCase(String.valueOf(v));
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
