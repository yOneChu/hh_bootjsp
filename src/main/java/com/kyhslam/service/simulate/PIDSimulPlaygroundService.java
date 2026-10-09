package com.kyhslam.service.simulate;

import com.kyhslam.util.simulPlayground.PlaygroundResult;
import com.kyhslam.util.pidSimulatorUp.SimDb;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository;
import com.kyhslam.util.simulPlayground.PlaygroundDb;
import com.kyhslam.util.simulPlayground.PlaygroundLogic;
import com.kyhslam.util.simulPlayground.PlaygroundRequest;
import com.kyhslam.util.simulPlayground.PlaygroundSimul;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * PID 플레이그라운드 : DB 에서 불러온 PID 로직을 화면에서 고쳐 바로 실행 (DB 에는 절대 저장하지 않음).
 * 모든 DB 접근은 읽기 전용 연결(PlaygroundDb) 로 한다.
 */
@Slf4j
@Service
public class PIDSimulPlaygroundService {

    /** PID 버전 목록 {version, latest, regDate, isFloorSpec} (TEST 먼저, 그 다음 최신순) */
    public List<Map<String, Object>> getPidVersions(String pid) throws Exception {
        try (SimDb db = PlaygroundDb.openReadOnly()) {
            return new SimPidRepository().getVersions(db, pid.trim());
        }
    }

    /**
     * 편집 화면에 불러올 PID 로직 (DB 원본)
     * @param version null : 최신, -1 : TEST, 그 외 : 지정 버전
     */
    public PlaygroundLogic loadLogic(String pid, Integer version) throws Exception {
        try (SimDb db = PlaygroundDb.openReadOnly()) {
            return PlaygroundSimul.loadLogic(db, pid, version);
        }
    }

    /** 수정본(없으면 DB 원본)으로 실행 */
    public PlaygroundResult simulate(PlaygroundRequest req) throws Exception {
        long start = System.currentTimeMillis();
        PlaygroundResult result;
        try (SimDb db = PlaygroundDb.openReadOnly()) {
            result = PlaygroundSimul.run(db, req);
        }
        log.info("[PIDPlayground] {} / {} v{}{} 완료 : {}ms, {}행", req.project, req.pid, result.runVersion,
                req.rows == null ? " [DB 원본]" : " [수정본 " + req.rows.size() + "라인]",
                System.currentTimeMillis() - start, result.lines.size());
        return result;
    }
}
