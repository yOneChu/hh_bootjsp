package com.kyhslam.util.pidTest;

import com.kyhslam.util.pidSimulatorUp.SimContext;
import com.kyhslam.util.pidSimulatorUp.SimVariant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PID 테스트용 실행 엔진 (SimVariant 확장)
 * PID 실행 오류를 errors 에 모아 결과(PidTestRow) 로 돌려준다.
 * saveErrorLog=true 일 때만 원본처럼 VARIANT_ERRORLOG 에도 기록한다.
 *
 * 주의) CALL 로 실행된 하위 PID 내부 오류는 SimVariant 가 직접 처리하므로 errors 에 담기지 않는다.
 *       (saveErrorLog=false 면 콘솔 [PID-ERROR] 로만 출력)
 */
public class PidTestVariant extends SimVariant {

    private final SimContext ctx;
    private final List<String> errors;

    public PidTestVariant(SimContext ctx, HashMap elvEnt, List<Map> floorMasterList, List<String> errors) {
        super(ctx, elvEnt, floorMasterList);
        this.ctx = ctx;
        this.errors = errors;
    }

    @Override
    public void saveErrorLog(String DOUID, String exceptionType, String msg) {
        errors.add("[" + exceptionType + "] DOUID=" + DOUID + " " + msg);
        if (ctx.isSaveErrorLog()) {
            super.saveErrorLog(DOUID, exceptionType, msg);
        }
    }
}
