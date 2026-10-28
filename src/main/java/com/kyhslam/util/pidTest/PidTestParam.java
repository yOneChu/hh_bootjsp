package com.kyhslam.util.pidTest;

import com.kyhslam.util.pidSimulatorUp.SimConsts;
import com.kyhslam.util.pidSimulatorUp.SimUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * PID Tester 실행 조건 (원본: PIDTester_left.jsp 의 form)
 */
public class PidTestParam {

    /** 최신 버전 (원본 화면 값 그대로 'lastest') */
    public static final String LATEST = SimConsts.LASTEST;
    /** TEST 버전 */
    public static final String TEST = String.valueOf(SimConsts.TEST_VERSION);

    private String pid;
    private String srcVersion = LATEST;
    private String targetVersion = TEST;
    private final List<String> productList = new ArrayList<String>();
    private final List<String> specList = new ArrayList<String>();
    private boolean getMore = false;
    private int timeoutSec = 30;
    private boolean stopOnTimeout = true;
    private boolean saveErrorLog = false;
    private boolean codeFieldDisplay = false;
    private int maxCount = 0;

    /** HashMap -> PidTestParam 변환 및 검증 (KEY 는 대소문자 구분 안 함) */
    public static PidTestParam from(Map<String, ?> paramMap) {
        if (paramMap == null) {
            throw new IllegalArgumentException("파라미터가 없습니다.");
        }

        PidTestParam p = new PidTestParam();
        p.pid = trim(get(paramMap, "PID"));
        p.srcVersion = normalizeVersion(get(paramMap, "srcVersion"), LATEST);
        p.targetVersion = normalizeVersion(get(paramMap, "targetVersion"), TEST);
        p.productList.addAll(splitLines(get(paramMap, "productList"), true));
        p.specList.addAll(splitLines(get(paramMap, "specList"), false));
        p.getMore = toBoolean(get(paramMap, "getMore"), false);
        p.timeoutSec = toInt(get(paramMap, "timeoutSec"), 30, "timeoutSec");
        p.stopOnTimeout = toBoolean(get(paramMap, "stopOnTimeout"), true);
        p.saveErrorLog = toBoolean(get(paramMap, "saveErrorLog"), false);
        p.codeFieldDisplay = toBoolean(get(paramMap, "codeFieldDisplay"), false);
        p.maxCount = toInt(get(paramMap, "maxCount"), 0, "maxCount");

        p.validate();
        return p;
    }

    public void validate() {
        if (!SimUtil.hasText(pid)) {
            throw new IllegalArgumentException("PID 는 필수입니다.");
        }
        if (timeoutSec <= 0) {
            throw new IllegalArgumentException("timeoutSec 는 1 이상이어야 합니다. : " + timeoutSec);
        }
    }

    /** 최신/lastest/latest -> lastest, TEST/test/-1 -> -1, 숫자 -> 숫자 */
    static String normalizeVersion(String value, String defaultValue) {
        if (!SimUtil.hasText(value)) {
            return defaultValue;
        }
        String v = value.trim();
        if (v.equalsIgnoreCase(LATEST) || v.equalsIgnoreCase("latest") || v.equals("최신")) {
            return LATEST;
        }
        if (v.equalsIgnoreCase("test") || v.equals(TEST)) {
            return TEST;
        }
        try {
            return String.valueOf(Integer.parseInt(v));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("버전 값이 올바르지 않습니다. (lastest / test / 숫자) : " + value);
        }
    }

    /** 버전 값 -> SimVariant 실행 버전 (최신이면 null, TEST 면 -1) */
    static Integer toVersionNumber(String version) {
        return LATEST.equals(version) ? null : Integer.valueOf(version);
    }

    /**
     * 대소문자 구분 없이 KEY 조회 (PID / pid / Pid 모두 허용)
     * 값이 Collection / 배열이면 줄바꿈으로 이어 붙인 문자열로 돌려준다.
     */
    public static String get(Map<String, ?> paramMap, String key) {
        Object value = paramMap.get(key);
        if (value == null) {
            for (Map.Entry<String, ?> e : paramMap.entrySet()) {
                if (e.getKey() != null && e.getKey().equalsIgnoreCase(key)) {
                    value = e.getValue();
                    break;
                }
            }
        }
        if (value == null) {
            return null;
        }
        if (value instanceof Object[]) {
            value = Arrays.asList((Object[]) value);
        }
        if (value instanceof Collection) {
            StringBuffer sb = new StringBuffer();
            for (Object v : (Collection<?>) value) {
                if (v == null) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(v.toString());
            }
            return sb.toString();
        }
        return value.toString();
    }

    /** 원본: PIDTester_right.jsp.getVersionName() */
    public static String getVersionName(String version) {
        if (LATEST.equals(version)) {
            return "최신";
        } else if (TEST.equals(version)) {
            return "TEST";
        }
        return version;
    }

    /** 줄바꿈 구분 (현장번호는 콤마/공백도 구분자로 허용) */
    private static List<String> splitLines(String value, boolean allowComma) {
        List<String> list = new ArrayList<String>();
        if (value == null) {
            return list;
        }
        String[] arr = value.split(allowComma ? "[\\r\\n,\\s]+" : "\\r?\\n");
        for (String s : arr) {
            String t = allowComma ? s.trim() : s;
            if (t.trim().length() > 0 && !list.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    private static boolean toBoolean(String value, boolean defaultValue) {
        if (!SimUtil.hasText(value)) {
            return defaultValue;
        }
        String v = value.trim();
        return v.equalsIgnoreCase("Y") || v.equalsIgnoreCase("true") || v.equalsIgnoreCase("on") || v.equals("1");
    }

    private static int toInt(String value, int defaultValue, String name) {
        if (!SimUtil.hasText(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " 는 숫자여야 합니다. : " + value);
        }
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    public String getPid() {
        return pid;
    }

    public String getSrcVersion() {
        return srcVersion;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public List<String> getProductList() {
        return productList;
    }

    public List<String> getSpecList() {
        return specList;
    }

    public boolean isGetMore() {
        return getMore;
    }

    public int getTimeoutSec() {
        return timeoutSec;
    }

    public boolean isStopOnTimeout() {
        return stopOnTimeout;
    }

    public boolean isSaveErrorLog() {
        return saveErrorLog;
    }

    public boolean isCodeFieldDisplay() {
        return codeFieldDisplay;
    }

    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public String toString() {
        return "PidTestParam [pid=" + pid + ", srcVersion=" + getVersionName(srcVersion) + ", targetVersion=" + getVersionName(targetVersion)
                + ", productList=" + (productList.isEmpty() ? "(자동조회)" : productList.size() + "건") + ", specList=" + specList
                + ", getMore=" + getMore + ", timeoutSec=" + timeoutSec + ", stopOnTimeout=" + stopOnTimeout
                + ", saveErrorLog=" + saveErrorLog + ", maxCount=" + maxCount + "]";
    }
}
