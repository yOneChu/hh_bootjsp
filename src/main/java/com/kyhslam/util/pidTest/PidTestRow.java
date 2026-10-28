package com.kyhslam.util.pidTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 현장(호기) 1건의 비교 결과 (원본: HogiTester.execute() 가 출력하던 <tr> 한 줄)
 */
public class PidTestRow {

    public static final String OK = "OK";
    /** 현장번호로 공사정보를 찾지 못함 (원본은 결과에서 제외) */
    public static final String NOT_FOUND = "NOT_FOUND";
    /** 실행 중 예외 (원본은 로그만 남기고 결과에서 제외) */
    public static final String ERROR = "ERROR";
    /** timeoutSec 초과 */
    public static final String TIMEOUT = "TIMEOUT";

    private int no;
    private String productNo;
    private String status = OK;
    private String srcResult = "*";
    private String targetResult = "*";
    private long elapsedMs;
    private String message;
    private final Map<String, String> specValues = new LinkedHashMap<String, String>();
    private final List<String> errors = new ArrayList<String>();

    public PidTestRow(int no, String productNo) {
        this.no = no;
        this.productNo = productNo;
    }

    /** 기준 결과와 대상 결과가 같은지 (OK 가 아니면 false) */
    public boolean isMatch() {
        return OK.equals(status) && srcResult.equals(targetResult);
    }

    public int getNo() {
        return no;
    }

    public String getProductNo() {
        return productNo;
    }

    public String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    public String getSrcResult() {
        return srcResult;
    }

    void setSrcResult(String srcResult) {
        this.srcResult = srcResult;
    }

    public String getTargetResult() {
        return targetResult;
    }

    void setTargetResult(String targetResult) {
        this.targetResult = targetResult;
    }

    /** 대상 버전 실행 시간 (원본과 동일하게 대상 버전만 측정) */
    public long getElapsedMs() {
        return elapsedMs;
    }

    void setElapsedMs(long elapsedMs) {
        this.elapsedMs = elapsedMs;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    /** 사용 SPEC -> 값 (대상 버전 결과 우선, 없으면 영업사양 값) */
    public Map<String, String> getSpecValues() {
        return specValues;
    }

    /** PID 로직 실행 중 발생한 오류 (원본은 VARIANT_ERRORLOG 에만 기록) */
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return no + ". " + productNo + " [" + status + "] match=" + isMatch() + " src=" + oneLine(srcResult)
                + " target=" + oneLine(targetResult) + " " + elapsedMs + "ms" + (message == null ? "" : " msg=" + message);
    }

    static String oneLine(String value) {
        return value == null ? null : value.replace("\n", " | ");
    }
}
