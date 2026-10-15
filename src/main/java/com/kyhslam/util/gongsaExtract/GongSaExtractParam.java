package com.kyhslam.util.gongsaExtract;

import java.util.ArrayList;
import java.util.List;

/**
 * 공사번호 추출 검색 조건
 * (원본: productNumExt_left.jsp 입력 항목)
 */
public class GongSaExtractParam {

    /** 화면에서 입력 가능한 영업사양 조건 최대 개수 */
    public static final int MAX_SPEC_COUNT = 10;

    /** elv : 엘리베이터, shipelv : 선박용 엘리베이터 */
    private String tableType = "elv";

    /** yyyy-MM-dd 또는 yyyyMMdd */
    private String startDate;
    private String endDate;

    /** MSR 다국어 타이틀 조회 locale (ko / en ...) */
    private String locale = "ko";

    /** 영업사양 (특성 CODE / 값 CODE) */
    private final List<SpecCondition> specConditions = new ArrayList<SpecCondition>();

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<SpecCondition> getSpecConditions() {
        return specConditions;
    }

    public void addSpecCondition(String spec, String cond) {
        if (specConditions.size() >= MAX_SPEC_COUNT) {
            throw new IllegalArgumentException("영업사양 조건은 최대 " + MAX_SPEC_COUNT + "개까지 입력 가능합니다.");
        }
        specConditions.add(new SpecCondition(spec, cond));
    }

    /** 필수값 / 형식 검증 (원본: productNumExt_left.jsp searchList()) */
    public void validate() {
        if (!"elv".equals(tableType) && !"shipelv".equals(tableType)) {
            throw new IllegalArgumentException("tableType 은 elv 또는 shipelv 만 가능합니다. : " + tableType);
        }
        if (isEmpty(startDate)) {
            throw new IllegalArgumentException("START 날짜 는 필수입니다.");
        }
        if (isEmpty(endDate)) {
            throw new IllegalArgumentException("END 날짜 는 필수입니다.");
        }
        if (startDate.length() < 8 || endDate.length() < 8) {
            throw new IllegalArgumentException("Invalid date format (ex: yyyy-mm-dd or yyyymmdd)");
        }
    }

    static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    @Override
    public String toString() {
        return "tableType=" + tableType + ", startDate=" + startDate + ", endDate=" + endDate
                + ", locale=" + locale + ", specs=" + specConditions;
    }

    /** 영업사양 1건 (특성 CODE / 값 CODE) */
    public static class SpecCondition {
        private final String spec;
        private final String cond;

        public SpecCondition(String spec, String cond) {
            this.spec = spec == null ? null : spec.trim();
            this.cond = cond == null ? null : cond.trim();
        }

        public String getSpec() {
            return spec;
        }

        public String getCond() {
            return cond;
        }

        public boolean hasSpec() {
            return !isEmpty(spec);
        }

        /** 특성 CODE 와 값 CODE 가 모두 있어야 WHERE 조건으로 사용됨 */
        public boolean hasCond() {
            return hasSpec() && !isEmpty(cond);
        }

        @Override
        public String toString() {
            return spec + "=" + (cond == null ? "" : cond);
        }
    }
}
