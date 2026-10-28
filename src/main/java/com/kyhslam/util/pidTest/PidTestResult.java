package com.kyhslam.util.pidTest;

import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * PID Tester 실행 결과 (원본: PIDTester_right.jsp 화면)
 */
public class PidTestResult {

    private boolean success = true;
    private String message;

    private String pid;
    private String srcVersion;
    private String targetVersion;
    private PidInfo srcHeader;
    private PidInfo targetHeader;
    private long elapsedMs;

    private final List<String> specs = new ArrayList<String>();
    private final List<PidTestRow> rows = new ArrayList<PidTestRow>();

    /** 실행 자체가 실패한 결과 (PID 없음 등) */
    static PidTestResult fail(String pid, String message) {
        PidTestResult r = new PidTestResult();
        r.pid = pid;
        r.success = false;
        r.message = message;
        return r;
    }

    /** 원본 화면의 '기준 결과(최신:DB)' 헤더 */
    public String getSrcTitle() {
        return "기준 결과(" + PidTestParam.getVersionName(srcVersion) + ":" + (srcHeader == null ? "" : srcHeader.getMethod()) + ")";
    }

    /** 원본 화면의 '대상 결과(TEST:DB)' 헤더 */
    public String getTargetTitle() {
        return "대상 결과(" + PidTestParam.getVersionName(targetVersion) + ":" + (targetHeader == null ? "" : targetHeader.getMethod()) + ")";
    }

    public int getTotalCount() {
        return rows.size();
    }

    public int getMatchCount() {
        int cnt = 0;
        for (PidTestRow row : rows) {
            if (row.isMatch()) {
                cnt++;
            }
        }
        return cnt;
    }

    /** 실행은 됐지만 결과가 다른 건수 */
    public int getMismatchCount() {
        return getMismatchRows().size();
    }

    /** 실행은 됐지만 결과가 다른 행 (원본: 'False만 보기') */
    public List<PidTestRow> getMismatchRows() {
        List<PidTestRow> list = new ArrayList<PidTestRow>();
        for (PidTestRow row : rows) {
            if (PidTestRow.OK.equals(row.getStatus()) && !row.isMatch()) {
                list.add(row);
            }
        }
        return list;
    }

    /** 결과가 다른 현장번호 (원본: 'False호기만 복사') */
    public List<String> getMismatchProducts() {
        List<String> list = new ArrayList<String>();
        for (PidTestRow row : getMismatchRows()) {
            list.add(row.getProductNo());
        }
        return list;
    }

    /** OK 가 아닌 행 (NOT_FOUND / ERROR / TIMEOUT) */
    public List<PidTestRow> getFailedRows() {
        List<PidTestRow> list = new ArrayList<PidTestRow>();
        for (PidTestRow row : rows) {
            if (!PidTestRow.OK.equals(row.getStatus())) {
                list.add(row);
            }
        }
        return list;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public String getPid() {
        return pid;
    }

    void setPid(String pid) {
        this.pid = pid;
    }

    public String getSrcVersion() {
        return srcVersion;
    }

    void setSrcVersion(String srcVersion) {
        this.srcVersion = srcVersion;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public PidInfo getSrcHeader() {
        return srcHeader;
    }

    void setSrcHeader(PidInfo srcHeader) {
        this.srcHeader = srcHeader;
    }

    public PidInfo getTargetHeader() {
        return targetHeader;
    }

    void setTargetHeader(PidInfo targetHeader) {
        this.targetHeader = targetHeader;
    }

    public long getElapsedMs() {
        return elapsedMs;
    }

    void setElapsedMs(long elapsedMs) {
        this.elapsedMs = elapsedMs;
    }

    /** 결과 컬럼으로 보여줄 사용 SPEC 목록 */
    public List<String> getSpecs() {
        return specs;
    }

    public List<PidTestRow> getRows() {
        return rows;
    }
}
