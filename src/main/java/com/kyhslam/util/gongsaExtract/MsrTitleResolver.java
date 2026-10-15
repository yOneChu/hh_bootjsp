package com.kyhslam.util.gongsaExtract;

import com.kyhslam.util.PLMDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * MSR 다국어 문자열 조회
 * (원본: MSR.getStgrepString(id, substitute, opt) - 서버 메모리 캐시 대신 msrstgrep 테이블 직접 조회)
 */
public class MsrTitleResolver {

    private final Connection con;
    private final String locale;
    private boolean available = true;

    public MsrTitleResolver(Connection con, String locale) {
        this.con = con;
        this.locale = GongSaExtractParam.isEmpty(locale) ? "en" : locale;
    }

    /**
     * @param id         MSR ID
     * @param substitute MSR 값이 없을 때 사용할 문자열
     * @param opt        locale 이 en 일 때만 적용 (0:그대로, 1:대문자, 2:소문자, 3:단어 첫글자 대문자)
     */
    public String getStgrepString(String id, String substitute, int opt) {
        String stg = available ? selectStg(id) : null;
        String tmpStr = GongSaExtractParam.isEmpty(stg) ? substitute : stg;

        if (tmpStr == null || tmpStr.length() == 0 || !locale.equals("en")) {
            return tmpStr;
        }

        switch (opt) {
            case 1:
                return tmpStr.toUpperCase();
            case 2:
                return tmpStr.toLowerCase();
            case 3:
                StringBuffer sb = new StringBuffer();
                for (String token : tokenize(tmpStr, ' ')) {
                    if (GongSaExtractParam.isEmpty(token)) {
                        continue;
                    }
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(token.substring(0, 1).toUpperCase()).append(token.substring(1));
                }
                return sb.toString();
            default:
                return tmpStr;
        }
    }

    private String selectStg(String id) {
        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            stat = con.prepareStatement("SELECT stg FROM msrstgrep WHERE loc = ? AND id = ?");
            stat.setString(1, locale);
            stat.setString(2, id);
            rs = stat.executeQuery();
            return rs.next() ? rs.getString(1) : null;
        } catch (Exception e) {
            // 테이블 접근 불가 시 이후 조회는 생략하고 기본 타이틀 사용
            available = false;
            System.err.println("[WARN] msrstgrep 조회 실패, 기본 타이틀을 사용합니다. : " + e.getMessage());
            return null;
        } finally {
            PLMDBConnection.disconnect(null, stat, rs);
        }
    }

    /** 원본: Utils.tokenizeMessage() */
    private static List<String> tokenize(String message, char delimiter) {
        List<String> list = new ArrayList<String>();
        int pos1 = 0;
        int pos2 = 0;
        while (pos2 >= 0) {
            pos2 = message.indexOf(delimiter, pos1);
            if (pos2 >= 0) {
                list.add(message.substring(pos1, pos2).trim());
                pos1 = pos2 + 1;
            } else {
                list.add(message.substring(pos1).trim());
            }
        }
        return list;
    }
}
