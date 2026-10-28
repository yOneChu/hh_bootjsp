package com.kyhslam.util.pidTest;

import com.kyhslam.util.pidSimulatorUp.SimDb;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * PID Tester 실행 (원본: PIDTester_right.jsp)
 *
 * 1. 기준/대상 버전 PID 헤더 조회
 * 2. 결과에 보여줄 사용 SPEC 목록 = 대상 버전 사용 SPEC + specList
 * 3. 현장번호가 없으면 테스트하기 적합한 현장번호 자동 조회 (원본: searchProjectList.jsp)
 * 4. 현장별로 HogiTester 실행 (현장당 timeoutSec 제한)
 */
public class PidTestService {

    public PidTestResult run(PidTestParam param) throws Exception {
        param.validate();

        try (SimDb db = SimDb.open()) {
            return run(db, param);
        }
    }

    public PidTestResult run(SimDb db, PidTestParam param) throws Exception {
        long startTime = System.currentTimeMillis();
        String pid = param.getPid();

        PidDao dao = new PidDao(db);
        SimPidRepository pidRepository = new SimPidRepository();

        // 원본(Variant.getPIDInfo)은 게시된(VARIANT_ID 에 있는) PID 만 실행 가능
        if (pidRepository.getLastPid(db, pid) == null) {
            return PidTestResult.fail(pid, "해당 버전 혹은 PID가 검색되지 않습니다. (게시된 PID 없음)");
        }

        PidInfo pidSrc = findPid(db, pidRepository, pid, param.getSrcVersion());
        PidInfo pidTarget = findPid(db, pidRepository, pid, param.getTargetVersion());
        if (pidSrc == null || pidTarget == null) {
            return PidTestResult.fail(pid, "해당 버전 혹은 PID가 검색되지 않습니다. (기준:"
                    + PidTestParam.getVersionName(param.getSrcVersion()) + (pidSrc == null ? " 없음" : "")
                    + ", 대상:" + PidTestParam.getVersionName(param.getTargetVersion()) + (pidTarget == null ? " 없음" : "") + ")");
        }

        PidTestResult result = new PidTestResult();
        result.setPid(pid);
        result.setSrcVersion(param.getSrcVersion());
        result.setTargetVersion(param.getTargetVersion());
        result.setSrcHeader(pidSrc);
        result.setTargetHeader(pidTarget);

        // 사용 SPEC 목록
        List<String> specs = result.getSpecs();
        specs.addAll(dao.getUsingSpecs(pid, pidTarget.getVersion()));
        for (String spec : param.getSpecList()) {
            if (!specs.contains(spec)) {
                specs.add(spec);
            }
        }

        // 현장번호
        List<String> products = new ArrayList<String>(param.getProductList());
        if (products.isEmpty()) {
            products.addAll(searchProjects(dao, specs, param.isGetMore()));
            System.out.println("현장번호 자동 조회 : " + products.size() + "건");
        }
        if (param.getMaxCount() > 0 && products.size() > param.getMaxCount()) {
            products = new ArrayList<String>(products.subList(0, param.getMaxCount()));
        }
        if (products.isEmpty()) {
            result.setMessage("테스트할 현장번호가 없습니다.");
            result.setElapsedMs(System.currentTimeMillis() - startTime);
            return result;
        }

        System.out.println("PID : " + pid + ", 현장 총 " + products.size() + "건, " + result.getSrcTitle() + " vs " + result.getTargetTitle());

        ExecutorService es = Executors.newCachedThreadPool(new DaemonThreadFactory());
        try {
            for (int i = 0; i < products.size(); i++) {
                PidTestRow row = new PidTestRow(i + 1, products.get(i));
                result.getRows().add(row);

                Future<PidTestRow> future = es.submit(new HogiTester(db, pidRepository, param, pidSrc, pidTarget, specs, row));
                try {
                    future.get(param.getTimeoutSec(), TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    future.cancel(true);
                    row.setStatus(PidTestRow.TIMEOUT);
                    row.setMessage(param.getTimeoutSec() + "초 안에 끝나지 않았습니다. 해당 데이터는 PID 시뮬레이터로 단독 실행 부탁드립니다.");

                    if (param.isStopOnTimeout()) {
                        result.setMessage(row.getProductNo() + " : 실행 중 타임아웃으로 전체 종료 되었습니다.");
                        break;
                    }
                }
                System.out.println(row);
            }
        } finally {
            es.shutdownNow();
        }

        result.setElapsedMs(System.currentTimeMillis() - startTime);
        return result;
    }

    private static PidInfo findPid(SimDb db, SimPidRepository pidRepository, String pid, String version) throws Exception {
        Integer v = PidTestParam.toVersionNumber(version);
        return v == null ? pidRepository.getLastPid(db, pid) : pidRepository.getPid(db, pid, v);
    }

    /** 원본: searchProjectList.jsp - SPEC 중 공사정보 컬럼인 것만 사용 (없는 컬럼이면 SQL 오류가 나므로) */
    private static List<String> searchProjects(PidDao dao, List<String> specs, boolean getMore) throws Exception {
        Set<String> elvColumns = dao.getElvColumns();
        List<String> columnSpecs = new ArrayList<String>();
        for (String spec : specs) {
            if (spec.matches("^[A-Za-z0-9_$#]+$") && elvColumns.contains(spec.toUpperCase())) {
                columnSpecs.add(spec);
            }
        }
        return dao.searchProjectList(columnSpecs, getMore);
    }

    /** 타임아웃으로 멈춘 스레드가 프로그램 종료를 막지 않도록 데몬 스레드 사용 */
    private static class DaemonThreadFactory implements ThreadFactory {
        private int seq = 0;

        @Override
        public synchronized Thread newThread(Runnable r) {
            Thread t = new Thread(r, "pid-test-" + (++seq));
            t.setDaemon(true);
            return t;
        }
    }
}
