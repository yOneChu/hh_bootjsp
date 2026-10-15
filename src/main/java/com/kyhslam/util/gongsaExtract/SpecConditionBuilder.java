package com.kyhslam.util.gongsaExtract;

/**
 * 영업사양 값 CODE 입력 문법을 SQL 조건절로 변환
 * (원본: productNumExt.getExt(), getPartialStOp ~ getPartialStOp3)
 *
 * 값 CODE 문법
 *   N        : 값이 N 또는 NULL
 *   !N       : 값이 N 과 NULL 이 아닌 것
 *   ,A,B     : REGEXP_LIKE(A|B)
 *   !A,B     : NULL 이거나 REGEXP_LIKE(A|B) 가 아닌 것
 *   >10 / <10: 숫자만 추출하여 비교 (',' 는 AND 로 연결)
 *   ?A?      : REGEXP_LIKE
 *   =A       : 값이 A 와 같음
 *   *        : NULL
 *   그 외     : 값이 입력값(',' 제거)과 같음
 */
public class SpecConditionBuilder {

    private SpecConditionBuilder() {
    }

    /**
     * @return SQL 조건절, 만들 수 없으면 null
     */
    public static String build(String spec, String cond) {
        if (cond == null || cond.length() == 0) {
            return null;
        }

        // N 인 경우 = N 또는 Null
        if (cond.equals("N")) {
            return "(" + spec + " IS NULL OR " + spec + " = '" + cond + "')";
        }

        // !N 인 경우 = N 과 Null 값 제외
        if (cond.equals("!N")) {
            return "(" + spec + " IS NOT NULL AND " + spec + " != 'N')";
        }

        String columnCond = null;

        switch (cond.charAt(0)) {
            case ',': {
                String a = getPartialStOp1(spec, cond.substring(1));
                if (a != null) {
                    columnCond = "REGEXP_LIKE(" + spec + ", '" + a + "')";
                }
                break;
            }
            case '!': {
                String b = getPartialStOp1(spec, cond.substring(1));
                if (b != null) {
                    // 제외 조건은 NULL 값까지 포함해야함
                    columnCond = "(" + spec + " IS NULL OR NOT REGEXP_LIKE(" + spec + ", '" + b + "'))";
                }
                break;
            }
            case '>':
            case '<': {
                String c = getPartialStOp3(spec, cond);
                if (c != null) {
                    columnCond = "REGEXP_REPLACE(" + spec + ", '[^0-9]')" + c;    // 숫자만 빼서 비교
                }
                break;
            }
            case '?': {
                String e = getPartialStOp(spec, cond);
                if (e != null) {
                    columnCond = "REGEXP_LIKE(" + spec + ", '" + e + "')";
                }
                break;
            }
            case '=':
                columnCond = " " + spec + " = '" + cond.replaceAll("=", "") + "' ";
                break;
            case '*':
                columnCond = " " + spec + " IS NULL ";
                break;
            default: {
                String f = getPartialStOp2(spec, cond);
                if (f == null) {
                    break;
                }
                if (f.startsWith("&")) {
                    columnCond = " " + spec + " = '" + f.replaceAll("&", "") + "' ";
                } else {
                    columnCond = " " + spec + " '" + f + "' ";
                }
                break;
            }
        }

        return columnCond;
    }

    /** 원본 getPartialStOp 그대로 ('?' 구간 토글, ',' -> '|') */
    static String getPartialStOp(String spec, String cond) {
        StringBuffer sb = new StringBuffer();
        String temp = null;
        boolean isStart = true;

        for (int j = 0; j < cond.length(); j++) {
            char token = cond.charAt(j);
            switch (token) {
                case '?':
                    if (isStart) {
                        isStart = false;
                    } else {
                        temp = sb.toString();
                        isStart = true;
                    }
                    break;
                case ',':
                    sb.append("|");
                    break;
                default:
                    sb.append(token);
                    isStart = false;
                    temp = sb.toString();
            }
        }
        return temp;
    }

    /** 원본 getPartialStOp1 그대로 ('(', ')', '?' 구간 토글, ',' -> '|') */
    static String getPartialStOp1(String spec, String cond) {
        StringBuffer sb = new StringBuffer();
        String temp = null;
        boolean isStart = true;

        for (int j = 0; j < cond.length(); j++) {
            char token = cond.charAt(j);
            switch (token) {
                case '(':
                case ')':
                case '?':
                    if (isStart) {
                        isStart = false;
                    } else {
                        temp = sb.toString();
                        isStart = true;
                    }
                    break;
                case ',':
                    sb.append("|");
                    break;
                default:
                    sb.append(token);
                    isStart = false;
                    temp = sb.toString();
            }
        }
        return temp;
    }

    /**
     * ',' 는 제거하고 맨 앞에 '&' 를 붙임
     * (원본 로직상 첫 문자에서 항상 '&' 가 붙으므로 결과는 "= '값'" 비교가 됨)
     */
    static String getPartialStOp2(String spec, String cond) {
        StringBuffer sb = new StringBuffer();
        String temp = null;

        for (int j = 0; j < cond.length(); j++) {
            char token = cond.charAt(j);
            if (token == ',') {
                continue;
            }
            sb.append(token);
            if (j == 0) {
                sb.insert(0, "&");
            }
            temp = sb.toString();
        }
        return temp;
    }

    /** ',' 는 " AND {spec} " 로 변환 (ex: >10,<20 -> >10 AND spec <20) */
    static String getPartialStOp3(String spec, String cond) {
        StringBuffer sb = new StringBuffer();
        String temp = null;

        for (int j = 0; j < cond.length(); j++) {
            char token = cond.charAt(j);
            if (token == ',') {
                sb.append(" AND ").append(spec).append(" ");
            } else {
                sb.append(token);
                temp = sb.toString();
            }
        }
        return temp;
    }
}
