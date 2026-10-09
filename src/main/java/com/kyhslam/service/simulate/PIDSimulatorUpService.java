package com.kyhslam.service.simulate;

import com.kyhslam.util.pidSimulatorUp.PIDSimul;
import com.kyhslam.util.pidSimulatorUp.PIDSimulResult;
import com.kyhslam.util.pidSimulatorUp.SimConsts;
import com.kyhslam.util.pidSimulatorUp.SimDb;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.HdelBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * PID 시뮬레이터 (PIDSimulator 화면의 [실행] - Project 모드, util.pidSimulatorUp.PIDSimul 운영 버전)
 *
 * 설정 (선택)
 *   pid.simul.errorlog=true   variant_errorlog 에 PID 오류 저장 (기본 false : 로그 출력만)
 */
@Slf4j
@Service
public class PIDSimulatorUpService {

    @Value("${pid.simul.errorlog:false}")
    private boolean saveErrorLog;

    /**
     * PID 를 공사정보로 디버그 실행한다.
     * @param project     공사번호 ex) 206663L01, 또는 공사정보 ouid ex) elv_info$vf@a810066d
     * @param pid         실행할 PID ex) D375A
     * @param floor       층별 PID 일 때 적용할 층 (FLOOR_NAME). 층별 PID 가 아니면 무시
     * @param testVersion true 면 TEST 버전, false 면 최신 버전
     * @param beforePids  전처리 PID 목록 (null 가능)
     */
    public PIDSimulResult simulate(String project, String pid, String floor, boolean testVersion, List<String> beforePids) throws Exception {
        return simulate(project, pid, floor, testVersion ? (Integer) SimConsts.TEST_VERSION : null, beforePids);
    }

    /**
     * PID 를 공사정보로 지정 버전 디버그 실행한다.
     * @param version null : 최신 버전, -1 : TEST 버전, 그 외 : 지정 버전 (CALL 하는 하위 PID 는 항상 최신 버전)
     */
    public PIDSimulResult simulate(String project, String pid, String floor, Integer version, List<String> beforePids) throws Exception {
        return simulate(project, pid, floor, version, beforePids, null);
    }

    /**
     * PID 를 공사정보로 지정 버전 디버그 실행한다. CALL 하위 PID 도 버전을 지정할 수 있다.
     * @param version     null : 최신 버전, -1 : TEST 버전, 그 외 : 지정 버전
     * @param subVersions CALL 하위 PID → 버전 (-1 : TEST). 지정하지 않은 하위 PID 는 최신 버전
     */
    public PIDSimulResult simulate(String project, String pid, String floor, Integer version, List<String> beforePids,
                                   Map<String, Integer> subVersions) throws Exception {
        if (project == null || project.trim().isEmpty())
            throw new HdelBusinessException("공사번호(project) 가 없습니다.");
        if (pid == null || pid.trim().isEmpty())
            throw new HdelBusinessException("PID 가 없습니다.");

        String before = (beforePids == null || beforePids.isEmpty()) ? null : String.join("\n", beforePids);

        long start = System.currentTimeMillis();
        PIDSimulResult result;
        try (SimDb db = SimDb.open()) {
            result = PIDSimul.run(db, project.trim(), pid.trim(), floor, version, before, subVersions, saveErrorLog);
        }
        log.info("[PIDSimul] {} / {} v{}{}{}{} 완료 : {}ms, {}행, 하위 PID {}개", project, pid, result.runVersion,
                result.testVersion ? " [TEST]" : (version == null ? " [최신]" : ""),
                result.floor == null ? "" : " floor=" + result.floor,
                subVersions == null || subVersions.isEmpty() ? "" : " 하위 버전지정=" + subVersions,
                System.currentTimeMillis() - start, result.lines.size(), result.subPids.size());
        for (String w : result.warnings)
            log.warn("[PIDSimul] {} / {} : {}", project, pid, w);
        return result;
    }

    /**
     * PID 의 버전 목록 (TEST 버전 먼저, 그 다음 최신순)
     * @return 항목 {version, latest}
     */
    public List<Map<String, Object>> getPidVersions(String pid) throws Exception {
        try (SimDb db = SimDb.open()) {
            return PIDSimul.findVersions(db, pid);
        }
    }
}
