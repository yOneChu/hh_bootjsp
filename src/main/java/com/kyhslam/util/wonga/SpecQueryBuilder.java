package com.kyhslam.util.wonga;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dyna.plmetc.product.searchDesignedList#getSpecQuery 를 SvServer/MSR 없이 실행할 수 있도록 옮긴 클래스.
 * (_right.jsp 는 헤더를 컬럼명 그대로 출력하므로 MSR 타이틀 변환은 사용하지 않음)
 */
public class SpecQueryBuilder {

    public static final String ELV_PRODUCT_CLS = "2248993771";

    /**
     * 추출데이터(specList, 줄바꿈 구분) 에 해당하는 select 컬럼 구문을 만든다.
     * 예) " (select des from doscoditm where ouid=EL_ATYP) \"EL_ATYP\",  EL_AMAN \"EL_AMAN\", "
     */
    public static String getSpecQuery(Connection con, String productCls, String specList) throws Exception {

        List<String> specs = readLines(specList);
        if (specs.isEmpty())
            return "";

        StringBuilder inList = new StringBuilder();
        for (String spec : specs) {
            if (inList.length() > 0)
                inList.append(',');
            inList.append('\'').append(spec.trim()).append('\'');
        }

        Map<String, String> typeMap = new HashMap<String, String>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT UPPER(CODE) NAME, TIT, MSRTITLECODE, TYPE ");
        sql.append(" FROM hdel_system.dosfld ");
        sql.append(" WHERE DOSCLAS in (2248973239, " + productCls + ") AND UPPER(code) IN (" + inList.toString().toUpperCase() + ") ");

        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            stat = con.prepareStatement(sql.toString());
            rs = stat.executeQuery();
            while (rs.next()) {
                typeMap.put(rs.getString("NAME"), rs.getString("TYPE"));
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (stat != null) try { stat.close(); } catch (Exception e) {}
        }

        StringBuilder mainSQL = new StringBuilder();
        for (String spec : specs) {
            String name = spec.toUpperCase().trim();
            if (!typeMap.containsKey(name))
                continue;

            String type = typeMap.get(name);
            if ("24".equals(type) || "25".equals(type)) // 타입이 코드형태일 때
                mainSQL.append(" (select des from doscoditm where ouid=" + name + ") \"" + name + "\", ");
            else
                mainSQL.append(" " + name + " \"" + name + "\", ");
        }

        return mainSQL.toString();
    }

    private static List<String> readLines(String text) throws IOException {

        List<String> lines = new ArrayList<String>();
        if (text == null)
            return lines;

        LineNumberReader lnr = new LineNumberReader(new StringReader(text));
        String line;
        while ((line = lnr.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
}
