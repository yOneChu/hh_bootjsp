package com.kyhslam.service;

import com.kyhslam.util.PLMDBConnection;
import com.kyhslam.util.gongsaExtract.GongSaExtractParam;
import com.kyhslam.util.gongsaExtract.GongSaExtractResult;
import com.kyhslam.util.gongsaExtract.MsrTitleResolver;
import com.kyhslam.util.gongsaExtract.SpecConditionBuilder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GongSaExtractService {

    private static final String CLS_ELV = "2248993771";       // 일반 엘리베이터
    private static final String CLS_SHIP_ELV = "2706224418";  // 선박용 엘리베이터

    /**
     * HashMap 조건으로 공사번호 추출 수행
     * (원본: GongSaExtractMapMain.execute() 의 조회 부분)
     */
    public GongSaExtractResult search(Map<String, String> paramMap) throws Exception {
        GongSaExtractParam param = toParam(paramMap);
        System.out.println("검색조건 : " + param);
        return search(param);
    }

    /** HashMap -> GongSaExtractParam 변환 및 검증 */
    public GongSaExtractParam toParam(Map<String, String> paramMap) {
        if (paramMap == null) {
            throw new IllegalArgumentException("파라미터가 없습니다.");
        }

        GongSaExtractParam param = new GongSaExtractParam();

        String tableType = getParam(paramMap, "tableType");
        if (!GongSaExtractParam.isEmpty(tableType)) {
            param.setTableType(tableType.trim());
        }
        param.setStartDate(getParam(paramMap, "startdate"));
        param.setEndDate(getParam(paramMap, "enddate"));

        String locale = getParam(paramMap, "locale");
        if (!GongSaExtractParam.isEmpty(locale)) {
            param.setLocale(locale.trim());
        }

        for (int i = 0; i < GongSaExtractParam.MAX_SPEC_COUNT; i++) {
            String spec = getParam(paramMap, "spec" + i);
            if (!GongSaExtractParam.isEmpty(spec)) {
                param.addSpecCondition(spec, getParam(paramMap, "cond" + i));
            }
        }

        param.validate();
        return param;
    }

    /** 대소문자 구분 없이 KEY 조회 (startdate / startDate / STARTDATE 모두 허용) */
    public static String getParam(Map<String, String> paramMap, String key) {
        String value = paramMap.get(key);
        if (value != null) {
            return value;
        }
        for (Map.Entry<String, String> e : paramMap.entrySet()) {
            if (e.getKey() != null && e.getKey().equalsIgnoreCase(key)) {
                return e.getValue();
            }
        }
        return null;
    }

    public GongSaExtractResult search(GongSaExtractParam param) throws Exception {
        param.validate();

        Connection con = PLMDBConnection.getConnection();
        if (con == null) {
            throw new IllegalStateException("DB 연결에 실패했습니다.");
        }

        try {
            return search(con, param);
        } finally {
            PLMDBConnection.disconnect(con, null, null);
        }
    }

    public GongSaExtractResult search(Connection con, GongSaExtractParam param) throws Exception {
        GongSaExtractResult result = new GongSaExtractResult();

        String tableType = param.getTableType();
        String clsCode = "shipelv".equals(tableType) ? CLS_SHIP_ELV : CLS_ELV;

        List<String> specCodes = new ArrayList<String>();
        for (GongSaExtractParam.SpecCondition sc : param.getSpecConditions()) {
            if (sc.hasSpec()) {
                specCodes.add(sc.getSpec());
            }
        }

        StringBuffer sql = new StringBuffer();
        sql.append("  SELECT *  FROM ( \n");
        sql.append(" SELECT DISTINCT A.MD$NUMBER AS 공사번호, \n");
        sql.append(getSpecQuery(con, clsCode, specCodes, param.getLocale(), result.getSpecTitleMap()));
        sql.append("  A.MD$DESC AS 수주명, \n");
        sql.append("  A.MANAGER_M AS 기계담당자, \n");
        sql.append("  A.MANAGER_E AS 전기담당자, \n");
        sql.append("  NVL(C.MD$DESC,A.MD$USER) AS 등록자, \n");
        sql.append(" TO_DATE(A.MD$CDATE ,'YYYYMMDDHH24MISS') AS 등록일 \n");
        sql.append("FROM " + tableType + "_INFO$VF A, " + tableType + "_INFO$ID B, FUSER$SF C    \n");
        sql.append("WHERE 1=1 \n");
        sql.append("AND A.VF$OUID=B.ID$WIP ");
        sql.append("AND A.MD$USER = C.MD$NUMBER(+) ");
        sql.append("AND A.MD$NUMBER NOT LIKE 'TEST%' ");
        sql.append(" AND A.MD$CDATE >= ? ");
        sql.append(" AND A.MD$CDATE <= ? ");
        sql.append("ORDER BY A.MD$NUMBER DESC \n ");
        sql.append(")A ");
        sql.append("WHERE 1=1 \n");

        for (GongSaExtractParam.SpecCondition sc : param.getSpecConditions()) {
            if (!sc.hasCond()) {
                continue;
            }
            String s = SpecConditionBuilder.build(sc.getSpec(), sc.getCond());
            if (s != null) {
                sql.append("    AND  " + s + " \n");
            }
        }

        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            stat = con.prepareStatement(sql.toString());
            stat.setString(1, param.getStartDate().trim().replaceAll("-", ""));
            // MD$CDATE 는 yyyyMMddHHmmss 이므로 종료일 당일 등록건까지 포함되도록 235959 를 붙임
            String endDate = param.getEndDate().trim().replaceAll("-", "");
            if (endDate.length() == 8) {
                endDate += "235959";
            }
            stat.setString(2, endDate);
            rs = stat.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                result.getHead().add(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                List<String> row = new ArrayList<String>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                result.getData().add(row);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] SQL :\n" + sql);
            throw e;
        } finally {
            PLMDBConnection.disconnect(null, stat, rs);
        }

        return result;
    }

    /**
     * 영업사양 컬럼 SELECT 절 생성 및 타이틀 맵 작성
     * (원본: productNumExt.getSpecQuery())
     */
    private String getSpecQuery(Connection con, String productCls, List<String> specCodes,
                                String locale, Map<String, String> specTitleMap) throws Exception {
        if (specCodes.isEmpty()) {
            return "";
        }

        StringBuffer specs = new StringBuffer();
        for (String spec : specCodes) {
            specs.append(",'").append(spec).append("'");
        }
        String specIn = specs.substring(1);

        Map<String, Map<String, String>> fieldMap = new HashMap<String, Map<String, String>>();

        StringBuffer sql = new StringBuffer();
        sql.append(" select upper(CODE) NAME, TIT, MSRTITLECODE, TYPE ");
        sql.append(" from hdel_system.dosfld ");
        sql.append(" where dosclas=" + productCls + " and code in (" + specIn.toLowerCase() + "," + specIn.toUpperCase() + " ) ");

        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            stat = con.prepareStatement(sql.toString());
            rs = stat.executeQuery();
            while (rs.next()) {
                Map<String, String> row = new HashMap<String, String>();
                row.put("NAME", rs.getString("NAME"));
                row.put("TIT", rs.getString("TIT"));
                row.put("MSRTITLECODE", rs.getString("MSRTITLECODE"));
                row.put("TYPE", rs.getString("TYPE"));
                fieldMap.put(rs.getString("NAME"), row);
            }
        } finally {
            PLMDBConnection.disconnect(null, stat, rs);
        }

        MsrTitleResolver msr = new MsrTitleResolver(con, locale);
        StringBuffer mainSQL = new StringBuffer();

        for (String spec : specCodes) {
            Map<String, String> row = fieldMap.get(spec.toUpperCase());
            if (row == null) {
                System.err.println("[WARN] 존재하지 않는 특성 CODE 입니다. : " + spec);
                continue;
            }

            String name = row.get("NAME");
            String title = row.get("TIT");
            String msrTitleCode = row.get("MSRTITLECODE");
            String type = row.get("TYPE");

            if (!GongSaExtractParam.isEmpty(msrTitleCode)) {
                title = msr.getStgrepString(msrTitleCode, title, 3);
            }
            if (GongSaExtractParam.isEmpty(title)) {
                title = name;
            }
            specTitleMap.put(name, title);

            if ("24".equals(type) || "25".equals(type)) {
                mainSQL.append(" (select des from doscoditm where ouid=" + name + ") \"" + name + "\", ");
            } else {
                mainSQL.append(" " + name + " \"" + name + "\", ");
            }
        }

        return mainSQL.toString();
    }

}
