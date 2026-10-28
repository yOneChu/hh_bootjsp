package com.kyhslam.util.pidTest;

import com.kyhslam.util.pidSimulatorUp.SimDb;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;
import com.kyhslam.util.pidSimulatorUp.SimSpecLoader;
import com.kyhslam.util.pidSimulatorUp.SimUtil;
import com.kyhslam.util.pidSimulatorUp.SimVariantMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 현장(호기) 1건에 대해 기준/대상 버전 PID 를 실행해서 비교 (원본: dyna.plmetc.variant.HogiTester)
 * 원본은 결과를 JspWriter 에 <tr> 로 출력했지만 여기서는 PidTestRow 에 담는다.
 *
 * 공사정보 로드와 PID 실행은 PID 시뮬레이터(pidSimulatorUp) 와 같은 SimSpecLoader / SimVariant 를 사용한다.
 */
public class HogiTester implements Callable<PidTestRow> {

    private final SimDb db;
    private final SimPidRepository pidRepository;
    private final PidTestParam param;
    private final PidInfo pidSrc;
    private final PidInfo pidTarget;
    private final List<String> specs;
    private final PidTestRow row;

    public HogiTester(SimDb db, SimPidRepository pidRepository, PidTestParam param, PidInfo pidSrc, PidInfo pidTarget,
                      List<String> specs, PidTestRow row) {
        this.db = db;
        this.pidRepository = pidRepository;
        this.param = param;
        this.pidSrc = pidSrc;
        this.pidTarget = pidTarget;
        this.specs = specs;
        this.row = row;
    }

    @Override
    public PidTestRow call() {
        try {
            // 타임아웃으로 남은 스레드와 캐시를 공유하지 않도록 호기마다 새로 만든다. (PID 캐시는 공유)
            PidTestContext ctx = new PidTestContext(db, pidRepository, param.isSaveErrorLog(), param.isCodeFieldDisplay());
            SimSpecLoader loader = ctx.getSpecLoader();

            Map<String, String> project = loader.findProject(row.getProductNo());
            SimSpecLoader.SpecObject elv = project == null ? null : loader.load(project.get("IOUID"));
            if (elv == null) {
                row.setStatus(PidTestRow.NOT_FOUND);
                row.setMessage("공사정보가 없습니다.");
                return row;
            }
            execute(ctx, elv);
        } catch (Throwable e) {
            row.setStatus(PidTestRow.ERROR);
            row.setMessage(e.getClass().getSimpleName() + " : " + e.getMessage());
        }
        return row;
    }

    /** 비교 실행 (원본: HogiTester.execute()) */
    private void execute(PidTestContext ctx, SimSpecLoader.SpecObject elv) throws Exception {
        HashMap elvEnt = elv.dataMap;
        String pidNo = pidSrc.getPid();
        List<String> errors = new ArrayList<String>();

        PidTestVariant variant = new PidTestVariant(ctx, elvEnt, loadFloors(ctx, elv), errors);

        // pidSrc 실행
        SimVariantMap variantResultSrc = run(variant, param.getSrcVersion());
        row.setSrcResult(getResultFromVariant(ctx, pidNo, variantResultSrc));

        // pidTarget 실행
        long start = System.currentTimeMillis();
        SimVariantMap variantResultTarget = run(variant, param.getTargetVersion());
        row.setTargetResult(getResultFromVariant(ctx, pidNo, variantResultTarget));
        row.setElapsedMs(System.currentTimeMillis() - start);

        for (String spec : specs) {
            String localVal;
            if (spec.contains("[$") && spec.contains("$]")) {
                try {
                    localVal = SimUtil.calculate(convertCmt(spec, elvEnt));
                } catch (Exception e) {
                    localVal = "수식 에러";
                }
            } else {
                localVal = SimUtil.NVL(variantResultTarget.get(spec), SimUtil.NVL(elvEnt.get(spec), ""));
            }
            row.getSpecValues().put(spec, localVal);
        }

        row.getErrors().addAll(errors);
    }

    /** 원본과 같이 최상위 실행은 디버그 모드 (최신이면 캐시된 최신 버전, 그 외는 지정 버전) */
    private SimVariantMap run(PidTestVariant variant, String version) throws Exception {
        Integer v = PidTestParam.toVersionNumber(version);
        if (v == null) {
            return variant.calcVariantPID(pidSrc.getPid(), null, null, 1, true);
        }
        return variant.calcVariantPID(pidSrc.getPid(), v, null, true);
    }

    /** METHOD=JAVA PID 용 층 정보 (PIDSimul 과 동일 - 실패하면 층 정보 없이 진행) */
    private static List<Map> loadFloors(PidTestContext ctx, SimSpecLoader.SpecObject elv) {
        try {
            List<HashMap> floors = ctx.getSpecLoader().loadFloors(elv.ouid, elv.dataMap);
            return floors == null ? null : new ArrayList<Map>(floors);
        } catch (Exception e) {
            return null;
        }
    }

    /** 원본: HogiTester.getResultFromVarint() */
    private static String getResultFromVariant(PidTestContext ctx, String pidNo, SimVariantMap result) throws Exception {
        String res;
        if (pidNo.contains("Q_")) {
            res = SimUtil.NVL(result.get("QTY"), "NULL");
        } else if (pidNo.contains("C_")) {
            res = SimUtil.NVL(result.get("CMT"), "NULL");
        } else if (ctx.isPidPattern(pidNo)) {
            res = "";
            if (result.containsKey("QTY")) {
                res += "QTY=" + SimUtil.NVL(result.get("QTY"), "NULL") + "\n";
            }
            if (result.containsKey("CMT")) {
                res += "CMT=" + SimUtil.NVL(result.get("CMT"), "NULL");
            }
        } else {
            res = result.getOUTPUTMap().toString();
        }

        if (res.endsWith("\n")) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }

    /** {변수} 를 영업사양 값으로 치환 (원본: PhantomBiz.convertCmt(cmt, false) - 오류 시 변환된 데까지 반환) */
    private static String convertCmt(String cmt, Map elvEnt) {
        int nCount = 0;
        try {
            while (cmt.indexOf("{") != -1 && nCount < 50) {
                int nStart = cmt.indexOf("{");
                int nEnd = cmt.indexOf("}");
                String elVar = cmt.substring(nStart + 1, nEnd);
                cmt = cmt.replace("{" + elVar + "}", SimUtil.NVL(elvEnt.get(elVar), ""));
                nCount++;
            }
            cmt = cmt.replace("&#160;", "");
        } catch (Exception e) {
            // 원본도 로그만 남기고 변환된 데까지 반환
        }
        return cmt;
    }
}
