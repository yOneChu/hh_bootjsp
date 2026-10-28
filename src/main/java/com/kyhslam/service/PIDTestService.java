package com.kyhslam.service;

import com.kyhslam.util.pidSimulatorUp.SimDb;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;
import com.kyhslam.util.pidTest.PidTestParam;
import com.kyhslam.util.pidTest.PidTestResult;
import com.kyhslam.util.pidTest.PidTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PID Tester 화면 (thymeleaf/pidTest/pidTest.html) 서비스
 * 같은 PID 의 기준 버전과 대상 버전을 여러 현장(호기)에 실행해서 결과가 같은지 비교한다. (util.pidTest)
 */
@Slf4j
@Service
public class PIDTestService {

    /**
     * PID 테스트 실행
     * @param paramMap 화면 입력값 (KEY 는 pidTestMain 주석 참고 : PID, srcVersion, targetVersion, productList ...)
     * @return 화면 응답 (pidTest.html 의 result 형식)
     */
    public Map<String, Object> run(Map<String, ?> paramMap) throws Exception {
        PidTestParam param = PidTestParam.from(paramMap);
        log.info("PID 테스트 시작 : {}", param);

        try (SimDb db = SimDb.open()) {
            PidTestResult result = new PidTestService().run(db, param);
            log.info("PID 테스트 종료 : pid={}, 총 {}건 / 일치 {}건 / 불일치 {}건 ({}ms)", result.getPid(),
                    result.getTotalCount(), result.getMatchCount(), result.getMismatchCount(), result.getElapsedMs());
            return toResponse(db, result);
        }
    }

    private Map<String, Object> toResponse(SimDb db, PidTestResult result) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", result.isSuccess());
        res.put("message", result.getMessage());
        res.put("pid", result.getPid());
        if (!result.isSuccess())
            return res;

        res.put("srcTitle", result.getSrcTitle());
        res.put("targetTitle", result.getTargetTitle());
        res.put("elapsedMs", result.getElapsedMs());
        res.put("srcHeader", toHeader(db, result.getSrcHeader()));
        res.put("targetHeader", toHeader(db, result.getTargetHeader()));
        res.put("totalCount", result.getTotalCount());
        res.put("matchCount", result.getMatchCount());
        res.put("mismatchCount", result.getMismatchCount());
        res.put("mismatchProducts", result.getMismatchProducts());
        res.put("specs", result.getSpecs());
        res.put("rows", result.getRows());
        return res;
    }

    /** PID 버전 정보 (PidInfo 에 없는 등록일/등록자/비고는 variant_h 에서 조회) */
    private Map<String, Object> toHeader(SimDb db, PidInfo pid) {
        if (pid == null)
            return null;

        Map<String, Object> h = new LinkedHashMap<>();
        h.put("pid", pid.getPid());
        h.put("version", pid.getVersion());
        h.put("method", pid.getMethod());
        h.put("name", pid.getName());

        try {
            Map<String, String> row = db.queryForFirst(
                    " select to_char(reg_date, 'YYYY-MM-DD HH24:MI') REG_DATE, userid, remarks from variant_h "
                    + " where pid = ? and version = ? order by reg_date desc ", pid.getPid(), pid.getVersion());
            if (row != null) {
                h.put("regDate", row.get("REG_DATE"));
                h.put("userId", row.get("USERID"));
                h.put("remarks", row.get("REMARKS"));
            }
        } catch (Exception e) {
            log.warn("PID 헤더 부가정보 조회 실패 : {} ({})", pid.getPid(), e.getMessage());
        }
        return h;
    }
}
