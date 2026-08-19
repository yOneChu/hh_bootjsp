package com.kyhslam.util;

import com.kyhslam.dto.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class ElvInfoCommonUtil {

    public static ArrayList<String> getTypeList() {
        ArrayList<String> result = new ArrayList<>();

        result.add("GTLX_R");
        result.add("WBHS_(HSVF)");
        result.add("LXVF1");
        result.add("WBSS2_(SSVF)");
        result.add("SSVF7");
        result.add("GTSS_E");
        result.add("WBST_SE");
        result.add("WBST1");
        result.add("WBLX1_(LXVF)");
        result.add("WBSS_SE");
        result.add("GTLX_E");
        result.add("WBSS");
        result.add("HSVF");
        result.add("GTSS_R");
        result.add("LXVF");
        result.add("STVF7");
        result.add("LXVF7");
        result.add("WBHS_(SSVF)");
        result.add("STVF5");
        result.add("WBST1_(STVF)");
        result.add("SSVF");
        result.add("STVF");
        result.add("GTSS");
        result.add("GTLX");
        result.add("WBLX_SE");
        result.add("SUVF");
        result.add("WBSS1_(SSVF)");

        return result;
    }


    //집계된거 대시보드 조회
    public static ArrayList<ELVInfoDashDTO> getDashbard() {
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        ArrayList<ELVInfoDashDTO> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;
        try {

            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT  ");
            sql.append(" A.batch_date, A.elv_type, ");
            sql.append(" A.total_cnt, ");
            sql.append(" A.dis202401, A.dis202402, A.dis202403, A.dis202404, A.dis202405, A.dis202406, ");
            sql.append(" A.dis202407, A.dis202408, A.dis202409, A.dis202410, A.dis202411, A.dis202412 ");

            //todayValue
            sql.append(" FROM elvinfo_dash A ");
            //sql.append(" WHERE A.BATCH_DATE = ? ");



            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, todayValue);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String batch_date = rs.getString("batch_date") == null ? "" : rs.getString("batch_date");
                String elv_type = rs.getString("elv_type") == null ? "" : rs.getString("elv_type");
                String total_cnt = rs.getString("total_cnt") == null ? "" : rs.getString("total_cnt");

                String dis202401 = rs.getString("dis202401") == null ? "" : rs.getString("dis202401");
                String dis202402 = rs.getString("dis202402") == null ? "" : rs.getString("dis202402");
                String dis202403 = rs.getString("dis202403") == null ? "" : rs.getString("dis202403");
                String dis202404 = rs.getString("dis202404") == null ? "" : rs.getString("dis202404");
                String dis202405 = rs.getString("dis202404") == null ? "" : rs.getString("dis202405");
                String dis202406 = rs.getString("dis202406") == null ? "" : rs.getString("dis202406");
                String dis202407 = rs.getString("dis202407") == null ? "" : rs.getString("dis202407");
                String dis202408 = rs.getString("dis202408") == null ? "" : rs.getString("dis202408");
                String dis202409 = rs.getString("dis202409") == null ? "" : rs.getString("dis202409");
                String dis202410 = rs.getString("dis202410") == null ? "" : rs.getString("dis202410");
                String dis202411 = rs.getString("dis202411") == null ? "" : rs.getString("dis202411");
                String dis202412 = rs.getString("dis202412") == null ? "" : rs.getString("dis202412");




                ELVInfoDashDTO dto = new ELVInfoDashDTO();
                dto.setBatchDate(batch_date);
                dto.setElvType(elv_type);
                dto.setTotalCnt(total_cnt);
                dto.setDis202401(dis202401);
                dto.setDis202402(dis202402);
                dto.setDis202403(dis202403);
                dto.setDis202404(dis202404);
                dto.setDis202405(dis202405);
                dto.setDis202406(dis202406);
                dto.setDis202407(dis202407);
                dto.setDis202408(dis202408);
                dto.setDis202409(dis202409);
                dto.setDis202410(dis202410);
                dto.setDis202411(dis202411);
                dto.setDis202412(dis202412);



                result.add(dto);

            } // end while



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    public static HashMap<String, String> getMonthDashboard(String type) {

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> data = new HashMap<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            con = VaultDBConnection.getConnection();

            //con = DriverManager.getConnection(url, id, pass);

            System.out.println("con = " + con);


            StringBuffer sql = new StringBuffer();

            sql.append(" SELECT    ");

            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202401' AND A.atyp = ?) AS C202401,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202402' AND A.atyp = ?) AS C202402,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202403' AND A.atyp = ?) AS C202403,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202404' AND A.atyp = ?) AS C202404,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202405' AND A.atyp = ?) AS C202405,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202406' AND A.atyp = ?) AS C202406,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202407' AND A.atyp = ?) AS C202407,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202408' AND A.atyp = ?) AS C202408,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202409' AND A.atyp = ?) AS C202409,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202410' AND A.atyp = ?) AS C202410,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202411' AND A.atyp = ?) AS C202411,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202412' AND A.atyp = ?) AS C202412   ");


            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, type);
            pstmt.setString(2, type);
            pstmt.setString(3, type);
            pstmt.setString(4, type);
            pstmt.setString(5, type);
            pstmt.setString(6, type);
            pstmt.setString(7, type);
            pstmt.setString(8, type);
            pstmt.setString(9, type);
            pstmt.setString(10, type);
            pstmt.setString(11, type);
            pstmt.setString(12, type);

            rs = pstmt.executeQuery();

            int totalCnt = 0;
            while (rs.next()) {

                String C202401 = rs.getString("C202401") == null ? "" : rs.getString("C202401");
                String C202402 = rs.getString("C202402") == null ? "" : rs.getString("C202402");
                String C202403 = rs.getString("C202403") == null ? "" : rs.getString("C202403");
                String C202404 = rs.getString("C202404") == null ? "" : rs.getString("C202404");
                String C202405 = rs.getString("C202405") == null ? "" : rs.getString("C202405");
                String C202406 = rs.getString("C202406") == null ? "" : rs.getString("C202406");
                String C202407 = rs.getString("C202407") == null ? "" : rs.getString("C202407");
                String C202408 = rs.getString("C202408") == null ? "" : rs.getString("C202408");
                String C202409 = rs.getString("C202409") == null ? "" : rs.getString("C202409");
                String C202410 = rs.getString("C202410") == null ? "" : rs.getString("C202410");
                String C202411 = rs.getString("C202411") == null ? "" : rs.getString("C202411");
                String C202412 = rs.getString("C202412") == null ? "" : rs.getString("C202412");

                //HashMap<String, String> data = new HashMap<>();

                data.put("202401", C202401);
                data.put("202402", C202402);
                data.put("202403", C202403);
                data.put("202404", C202404);
                data.put("202405", C202405);
                data.put("202406", C202406);
                data.put("202407", C202407);
                data.put("202408", C202408);
                data.put("202409", C202409);
                data.put("202410", C202410);
                data.put("202411", C202411);
                data.put("202412", C202412);


            }

            System.out.println("data = " + data);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

        return data;
    }

    /**
     * 특정호기의 영업사양 값 추출
     * @param hogi
     * @return
     */
    public static ArrayList<HashMap<String, String>> getSalesInfo(String hogi) {

        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();

        String apiUrl = "https://plmpro.hdel.co.kr/jsp/help/salesInfoFromProductViewJson.jsp?productNumber=";
        apiUrl +=  hogi;


        try {
            // URL 객체 생성
            URL url = new URL(apiUrl);

            // HttpURLConnection 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // GET 방식 설정
            conn.setRequestMethod("GET");

            // 응답 타입 설정 (JSON, XML 등 필요에 맞게 변경 가능)
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            conn.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            StringBuilder response = new StringBuilder();

            // 응답 데이터 읽기
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"))) {

                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            // 연결 종료
            conn.disconnect();

            // JSON 파싱 및 ArrayList<HashMap<String, String>> 변환
            //ArrayList<HashMap<String, String>> resultList = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("SPEC_VALUE", obj.optString("SPEC_VALUE")); // 특성명
                map.put("SPEC_CODE", obj.optString("SPEC_CODE")); // 특성코드
                map.put("VALUE", obj.optString("VALUE")); // 특성값
                //map.put("TYPE", obj.optString("TYPE")); // tab명

                String vType =  obj.optString("TYPE");


                if( !"3D_MODEL".equals(vType) ) {
                    resultList.add(map);
                }

            }

            // 결과 출력
            /*for (HashMap<String, String> map : resultList) {
                System.out.println(map);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }


    /**
     * @apiNote 영업사양 값 추출
     * @param
     * @return
     */
    public static void findElvInfoValue(ArrayList<String> hogiList, HashMap<String, String> result) {

        //HashMap<String, String> result = new HashMap<>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            con = PLMDBConnection.getConnection();

            String sql = """
                    SELECT V.MD$DESC, V.MD$NUMBER AS HOGI,
                           CONCAT('elv_info$vf@', LOWER(DECTOHEX(V.vf$ouid))) OUID,   -- 영업사양 객체
                           CODN(V.EL_ABRAND), -- 브랜드
                           v.EL_DCCAQ AS EL_DCCAQ, -- compen bon
                           CODN(V.EL_ATYP), -- 기종
                           CODN (V.EL_ASPD), -- 속도
                           CODN (V.EL_ACAPA), --용량
                           V.EL_ZTEXT_B, --가내 특기사항
                           V.EL_ZTEXT_C, --승장 특기사항
                           V.EL_ZTEXT_D, --옵션 특기사항
                           V.EL_ZTEXT_E, --L/O 특기사항
                           V.EL_ZERR_M3_1, --기계 에러 메시지
                           V.EL_ZERR_E3_1, --전기 에러 메시지
                           V.EL_ZERR_M5_1, --기계 미품목,
                           V.EL_ZERR_E5_1, --전기 미품목
                           V.EL_ZERR_C_1, --공통 에러 메시지
                           V.EL_ZERR_A_1, --자동 입력 오류
                           V.MD$USER,
                           V.MD$CDATE
                           --CODN(V.EL_ETM)
                           --V.*
                    FROM ELV_INFO$VF V, ELV_INFO$ID A
                    WHERE
                        V.vf$identity = A.id$ouid and V.vf$ouid = A.id$wip
                        --AND SUBSTR(V.MD$CDATE, 0, 4) = '2025'
                    --AND CODN(V.EL_ATYP) LIKE '%WBLX%'
                    --AND V.MD$NUMBER = '204861L04';
                    AND V.MD$NUMBER IN (
                    """;

            String whereXp = "";
            for (int i = 0; i < hogiList.size(); i++) {
                String hogi = hogiList.get(i);
                whereXp += "'" + hogi + "',";
            }

            // Remove trailing comma if present
            if (whereXp.endsWith(",")) {
                whereXp = whereXp.substring(0, whereXp.length() - 1);
            }
            
            System.out.println("whereXp = " + whereXp);

            sql += whereXp;

            sql += ")";



            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, hogi);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                String HOGI =  rs.getString("HOGI");
                String EL_DCCAQ = rs.getString("EL_DCCAQ") == null ? "" : rs.getString("EL_DCCAQ");
                result.put(HOGI, EL_DCCAQ);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        //return result;
    }

    //기종 추출
    public static ArrayList<CodeDTO> findDosCode(String type) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList<CodeDTO> result = new ArrayList<>();

        try {
            con = PLMDBConnection.getConnection();

            String sql = """
                SELECT D.NAME, D.DES AS CODE , C.NAME AS TYPE_NAME
                --, D.*, C.*
                FROM doscoditm D, DOSCOD C
                WHERE D.DOSCOD= C.OUID
                --AND C.NAME = '기종'
                AND C.NAME = ?
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, type);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String NAME =  rs.getString("NAME"); // 설명
                String CODE = rs.getString("CODE") == null ? "" : rs.getString("CODE");
                String TYPE_NAME =  rs.getString("TYPE_NAME") == null ? "" : rs.getString("TYPE_NAME");

                CodeDTO dto = new CodeDTO();
                dto.setName(NAME);
                dto.setCode(CODE);
                dto.setTypeName(TYPE_NAME);

                result.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    /**
     * 사양코드로 관련 공통코드 리스트 조회
     * @param type
     * @return
     */
    public static ArrayList<CodeDTO> findCodeList(String type) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList<CodeDTO> result = new ArrayList<>();

        try {
            con = PLMDBConnection.getConnection();

            String sql = """
                    SELECT 
                    a.name AS CODENAME, 
                    a.TIT AS KNAME, 
                    c.NAME AS SPECNAME, 
                    c.DES AS SPECVALUE, 
                    c.ouid, 
                    b.name AS USENAME
                                  FROM HDEL_SYSTEM.DOSFLD A LEFT JOIN HDEL_SYSTEM.DOSCOD B ON A.TYPECLAS  = B.OUID
                                  LEFT JOIN DOSCODITM C ON C.DOSCOD = B.OUID
                                  WHERE 1 = 1
                                    AND A.DOSCLAS = 2248993771
                                    --AND A.NAME = 'EL_ABRAND'
                                  AND A.NAME = ?
                                    --AND A.TIT = '브랜드'
                                  ORDER BY A.name, C.CODITM
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, type);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String NAME =  rs.getString("SPECNAME"); // 설명
                String CODE = rs.getString("SPECVALUE") == null ? "" : rs.getString("SPECVALUE");
                String TYPE_NAME =  rs.getString("SPECVALUE") == null ? "" : rs.getString("SPECVALUE");

                CodeDTO dto = new CodeDTO();
                dto.setName(NAME);
                dto.setCode(CODE);
                dto.setTypeName(TYPE_NAME);

                result.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }

    //ELV 조회 및 분석
    public static ArrayList<ElvInfoDTO> findElvSearch(ElvWhere whereCond) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        String year = whereCond.getYear();

        String vNumber = whereCond.getHogi();
        String vEL_ACAPA =  whereCond.getEL_ACAPA();
        String vEL_ABRAND = whereCond.getEL_ABRAND();
        String vEL_ASPSCD = whereCond.getEL_ASPSCD();
        String vEL_ATYP =  whereCond.getEL_ATYP();
        String vEL_ASPD = whereCond.getEL_ASPD();
        String vEL_ERPW = whereCond.getEL_ERPW();
        String vEL_AOPEN = whereCond.getEL_AOPEN();
        String vEL_ETM = whereCond.getEL_ETM();
        String vEL_COB = whereCond.getEL_COB();
        String vEL_DLATT = whereCond.getEL_DLATT();

        ArrayList<ElvInfoDTO> dataList = new ArrayList<ElvInfoDTO>();

        try {
            con = PLMDBConnection.getConnection();

            String sql = """        
                     SELECT V.MD$DESC, 
                     V.MD$NUMBER AS PRODUCTNO,
                     V.VF$OUID AS OID,
                     COD(V.EL_AOPEN) AS EL_AOPEN,
                     V.MD$STATUS AS STATUS,
                     CODN(v.EL_AUSE) AS EL_AUSE, -- 용도
                     CODN(V.EL_ETM) AS EL_ETM, --권상기
                     V.EL_ECWBUFBH, --CWT BUFFER BLOCKING 높이 
                     V.EL_ECCH, --CAR 높이; CH 
                     V.EL_ECBG, --CAR:BG 
                     V.EL_ECEE, --CAR 무게중심;EE 
                     V.EL_ECJJ, --도어폭;JJ 
                     V.EL_ERPW,
                     COD(V.EL_ECWRL) AS EL_ECWRL, --CWT RAIL(K) 
                     COD(V.EL_ETM) AS EL_ETM, --권상기 
                     V.EL_ECWBG AS EL_ECWBG, --CWT; BG 
                     V.EL_ECWW AS EL_ECWW, --CWT;폭 
                     COD(V.EL_ECSF) AS EL_ECSF, --CAR; SAFETY 
                     COD(V.EL_ASPC) AS EL_ASPC, --시방서 
                     COD(V.EL_ASPCD) AS EL_ASPCD, -- 시방서 DEVIATION 여부 
                     COD(V.EL_BCL) AS EL_BCL, -- 천장종류 
                     V.EL_AMAN AS EL_AMAN, --인승 
                     COD(V.EL_ASPSCD) AS EL_ASPSCD, --생산거점(설계) 
                     CONCAT('elv_info$vf@', LOWER(DECTOHEX(V.vf$ouid))) OUID,   -- 영업사양 객체 
                     CODN(V.EL_ABRAND) AS EL_ABRAND, -- 브랜드 
                     CODN(V.EL_ATYP) AS EL_ATYP, -- 기종 
                     CODN (V.EL_ASPD) AS EL_ASPD, -- 속도 
                     CODN (V.EL_ACAPA) AS EL_ACAPA, --용량 
                     V.EL_ZTEXT_B, --가내 특기사항 
                     V.EL_ZTEXT_C, --승장 특기사항 
                     V.EL_ZTEXT_D, --옵션 특기사항 
                     V.EL_ZTEXT_E, --L/O 특기사항 
                     V.EL_ZERR_M3_1, --기계 에러 메시지 
                     V.EL_ZERR_E3_1, --전기 에러 메시지 
                     V.EL_ZERR_M5_1, --기계 미품목, 
                     V.EL_ZERR_E5_1, --전기 미품목 
                     V.EL_ZERR_C_1, --공통 에러 메시지 
                     V.EL_ZERR_A_1, --자동 입력 오류 
                     V.MD$USER, 
                     V.MD$CDATE, 
                     CODN(V.EL_ETM) 
                     --,V.* 
              FROM ELV_INFO$VF V, ELV_INFO$ID A 
              WHERE 
                  V.vf$identity = A.id$ouid and V.vf$ouid = A.id$wip 
                  AND V.MD$NUMBER NOT LIKE 'Q%'
                  AND V.MD$NUMBER NOT LIKE '%TEST%'
                """;

            //vNumber
            if(vNumber != null && !"".equals(vNumber) && !"-".equals(vNumber)) {
                if (vNumber.contains("*")) {
                    vNumber = vNumber.replace("*", "%");
                    sql += " AND V.MD$NUMBER LIKE '" + vNumber + "' ";
                } else {
                    sql += " AND V.MD$NUMBER = '" + vNumber + "' ";
                }
            }

            if(vEL_DLATT != null && !"".equals(vEL_DLATT) && !"-".equals(vEL_DLATT)) {
                sql += " AND COD(V.EL_DLATT) = '" + vEL_DLATT + "' ";
            }

            if(year != null && !"".equals(year) && !"-".equals(year)) {
                sql += " AND SUBSTR(V.MD$CDATE, 0, 4) = '" + year + "' ";
            }

            if(vEL_ASPD != null && !"".equals(vEL_ASPD) && !"-".equals(vEL_ASPD)) {
                if (vEL_ASPD.contains("*")) {
                    vEL_ASPD = vEL_ASPD.replace("*", "%");
                    sql += " AND CODN(V.EL_ASPD) LIKE '" + vEL_ASPD + "' ";
                } else {
                    sql += " AND CODN(V.EL_ASPD) = '" + vEL_ASPD + "' ";
                }
            }

            if(vEL_ETM != null && !"".equals(vEL_ETM) && !"-".equals(vEL_ETM)) {
                if (vEL_ETM.contains("*")) {
                    vEL_ETM = vEL_ETM.replace("*", "%");
                    //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
                    sql += " AND CODN(V.EL_ETM) LIKE '" + vEL_ETM + "' ";
                } else {
                    sql += " AND CODN(V.EL_ETM) = '" + vEL_ETM + "' ";
                }
            }

            if(vEL_ERPW != null && !"".equals(vEL_ERPW) && !"-".equals(vEL_ERPW)) {
                if (vEL_ERPW.contains("*")) {
                    vEL_ERPW = vEL_ERPW.replace("*", "%");
                    //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
                    sql += " AND CODN(V.EL_ERPW) LIKE '" + vEL_ERPW + "' ";
                } else {
                    sql += " AND CODN(V.EL_ERPW) = '" + vEL_ERPW + "' ";
                }
            }

            //EL_AOPEN 열림방식
            if(vEL_AOPEN != null && !"".equals(vEL_AOPEN) && !"-".equals(vEL_AOPEN)) {
                if (vEL_AOPEN.contains("*")) {
                    vEL_AOPEN = vEL_AOPEN.replace("*", "%");
                    //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
                    sql += " AND CODN(V.EL_AOPEN) LIKE '" + vEL_AOPEN + "' ";
                } else {
                    sql += " AND CODN(V.EL_AOPEN) = '" + vEL_AOPEN + "' ";
                }
            }

            //brand
            if(vEL_ABRAND != null && !"".equals(vEL_ABRAND) && !"-".equals(vEL_ABRAND)) {
                if (vEL_ABRAND.contains("*")) {
                    vEL_ABRAND = vEL_ABRAND.replace("*", "%");
                    sql += " AND COD(V.EL_ABRAND) LIKE '" + vEL_ABRAND + "' ";
                } else {
                    sql += " AND COD(V.EL_ABRAND) = '" + vEL_ABRAND + "' ";
                }
            }

            if(vEL_COB != null && !"".equals(vEL_COB) && !"-".equals(vEL_COB)) {
                sql += " AND COD(V.EL_COB) = '" + vEL_COB + "' ";
            }

            if(vEL_ACAPA != null && !"".equals(vEL_ACAPA) && !"-".equals(vEL_ACAPA)) {
                sql += " AND COD(V.EL_ACAPA) = '" + vEL_ACAPA + "' ";
            }

            //생산거점(설계)
            if(vEL_ASPSCD != null && !"".equals(vEL_ASPSCD) && !"-".equals(vEL_ASPSCD)) {
                if (vEL_ASPSCD.contains("*")) {
                    vEL_ASPSCD = vEL_ASPSCD.replace("*", "%");
                    sql += " AND COD(V.EL_ASPSCD) LIKE '" + vEL_ASPSCD + "' ";
                } else {
                    sql += " AND COD(V.EL_ASPSCD) = '" + vEL_ASPSCD + "' ";
                }
            }

            //기종
            if(vEL_ATYP != null && !"".equals(vEL_ATYP) && !"-".equals(vEL_ATYP)) {
                if (vEL_ATYP.contains("*")) {
                    vEL_ATYP = vEL_ATYP.replace("*", "%");
                    sql += " AND CODN(V.EL_ATYP) LIKE '" + vEL_ATYP + "' ";
                } else {
                    sql += " AND CODN(V.EL_ATYP) = '" + vEL_ATYP + "' ";
                }
            }


            System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, productOID);
            //pstmt.setString(2, partNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String PRODUCTNO = rs.getString("PRODUCTNO"); //제품번호
                String OID = rs.getString("OID");
                String STATUS = rs.getString("STATUS");
                String EL_AOPEN = rs.getString("EL_AOPEN") == null ? "" : rs.getString("EL_AOPEN");
                String EL_ECWBUFBH = rs.getString("EL_ECWBUFBH") == null ? "" : rs.getString("EL_ECWBUFBH");

                String EL_ECCH = rs.getString("EL_ECCH") == null ? "" : rs.getString("EL_ECCH");
                String EL_ECBG = rs.getString("EL_ECBG") == null ? "" : rs.getString("EL_ECBG");
                String EL_ECEE = rs.getString("EL_ECEE") == null ? "" : rs.getString("EL_ECEE");
                String EL_ECJJ = rs.getString("EL_ECJJ") == null ? "" : rs.getString("EL_ECJJ");
                String EL_ERPW = rs.getString("EL_ERPW") == null ? "" : rs.getString("EL_ERPW");

                String EL_ECWRL =  rs.getString("EL_ECWRL") == null ? "" : rs.getString("EL_ECWRL");
                String EL_ETM =  rs.getString("EL_ETM") == null ? "" : rs.getString("EL_ETM");
                String EL_ECWBG = rs.getString("EL_ECWBG") == null ? "" : rs.getString("EL_ECWBG");
                String EL_ECWW = rs.getString("EL_ECWW") == null ? "" : rs.getString("EL_ECWW");
                String EL_ECSF = rs.getString("EL_ECSF") == null ? "" : rs.getString("EL_ECSF");
                String EL_ASPC =  rs.getString("EL_ASPC") == null ? "" : rs.getString("EL_ASPC");
                String EL_ASPCD =  rs.getString("EL_ASPCD") == null ? "" : rs.getString("EL_ASPCD");
                String EL_BCL =  rs.getString("EL_BCL") == null ? "" : rs.getString("EL_BCL");

                String EL_AMAN = rs.getString("EL_AMAN") == null ? "" : rs.getString("EL_AMAN");
                String EL_ASPSCD = rs.getString("EL_ASPSCD") == null ? "" : rs.getString("EL_ASPSCD");
                String EL_ABRAND = rs.getString("EL_ABRAND") == null ? "" : rs.getString("EL_ABRAND");
                String EL_ATYP = rs.getString("EL_ATYP") == null ? "" : rs.getString("EL_ATYP");
                String EL_ASPD = rs.getString("EL_ASPD") == null ? "" : rs.getString("EL_ASPD");
                String EL_ACAPA = rs.getString("EL_ACAPA") == null ? "" : rs.getString("EL_ACAPA");
                String EL_AUSE =  rs.getString("EL_AUSE") == null ? "" : rs.getString("EL_AUSE");

                ElvInfoDTO dto = new ElvInfoDTO();
                dto.setPRODUCTNO(PRODUCTNO); //제품번호
                dto.setProductoid(OID);
                dto.setStatus(STATUS);
                dto.setEL_AOPEN(EL_AOPEN);
                dto.setEL_ECWBUFBH(EL_ECWBUFBH);
                dto.setEL_ATYP(EL_ATYP);
                dto.setEL_ECCH(EL_ECCH);
                dto.setEL_ECBG(EL_ECBG);
                dto.setEL_ECEE(EL_ECEE);
                dto.setEL_ECJJ(EL_ECJJ);
                dto.setEL_ERPW(EL_ERPW);

                dto.setEL_ECWRL(EL_ECWRL);
                dto.setEL_ETM(EL_ETM);
                dto.setEL_ECWBG(EL_ECWBG);
                dto.setEL_ECWW(EL_ECWW);
                dto.setEL_ECSF(EL_ECSF);
                dto.setEL_ASPC(EL_ASPC);
                dto.setEL_ASPCD(EL_ASPCD);
                dto.setEL_BCL(EL_BCL);
                dto.setEL_AMAN(EL_AMAN);
                dto.setEL_ASPSCD(EL_ASPSCD);
                dto.setEL_ABRAND(EL_ABRAND);
                dto.setEL_ATYP(EL_ATYP);
                dto.setEL_ASPD(EL_ASPD);
                dto.setEL_ACAPA(EL_ACAPA);
                dto.setEL_AUSE(EL_AUSE);

                dataList.add(dto);
            } //end while

            System.out.println("dataList.size() = " + dataList.size());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return dataList;
    }


    //API를 통한 영업사양 추출
    public static ArrayList<ElvInfoDTO> findElvSearchInfo(String hogi) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<ElvInfoDTO> dataList = new ArrayList<ElvInfoDTO>();

        try {
            con = PLMDBConnection.getConnection();

            String sql = """        
                     SELECT V.MD$DESC, 
                     V.MD$NUMBER AS PRODUCTNO,
                     V.VF$OUID AS OID,
                     COD(V.EL_AOPEN) AS EL_AOPEN,
                     V.MD$STATUS AS STATUS,
                     CODN(v.EL_AUSE) AS EL_AUSE, -- 용도
                     CODN(V.EL_ETM) AS EL_ETM, --권상기
                     V.EL_ECWBUFBH, --CWT BUFFER BLOCKING 높이 
                     V.EL_ECCH, --CAR 높이; CH 
                     V.EL_ECBG, --CAR:BG 
                     V.EL_ECEE, --CAR 무게중심;EE 
                     V.EL_ECJJ, --도어폭;JJ 
                     V.EL_ERPW,
                     COD(V.EL_ECWRL) AS EL_ECWRL, --CWT RAIL(K) 
                     COD(V.EL_ETM) AS EL_ETM, --권상기 
                     V.EL_ECWBG AS EL_ECWBG, --CWT; BG 
                     V.EL_ECWW AS EL_ECWW, --CWT;폭 
                     COD(V.EL_ECSF) AS EL_ECSF, --CAR; SAFETY 
                     COD(V.EL_ASPC) AS EL_ASPC, --시방서 
                     COD(V.EL_ASPCD) AS EL_ASPCD, -- 시방서 DEVIATION 여부 
                     COD(V.EL_BCL) AS EL_BCL, -- 천장종류 
                     V.EL_AMAN AS EL_AMAN, --인승 
                     COD(V.EL_ASPSCD) AS EL_ASPSCD, --생산거점(설계) 
                     CONCAT('elv_info$vf@', LOWER(DECTOHEX(V.vf$ouid))) OUID,   -- 영업사양 객체 
                     CODN(V.EL_ABRAND) AS EL_ABRAND, -- 브랜드 
                     CODN(V.EL_ATYP) AS EL_ATYP, -- 기종 
                     CODN (V.EL_ASPD) AS EL_ASPD, -- 속도 
                     CODN (V.EL_ACAPA) AS EL_ACAPA, --용량 
                     V.EL_ZTEXT_B, --가내 특기사항 
                     V.EL_ZTEXT_C, --승장 특기사항 
                     V.EL_ZTEXT_D, --옵션 특기사항 
                     V.EL_ZTEXT_E, --L/O 특기사항 
                     V.EL_ZERR_M3_1, --기계 에러 메시지 
                     V.EL_ZERR_E3_1, --전기 에러 메시지 
                     V.EL_ZERR_M5_1, --기계 미품목, 
                     V.EL_ZERR_E5_1, --전기 미품목 
                     V.EL_ZERR_C_1, --공통 에러 메시지 
                     V.EL_ZERR_A_1, --자동 입력 오류 
                     V.MD$USER, 
                     V.MD$CDATE, 
                     CODN(V.EL_ETM) 
                     --,V.* 
              FROM ELV_INFO$VF V, ELV_INFO$ID A 
              WHERE 
                  V.vf$identity = A.id$ouid and V.vf$ouid = A.id$wip 
                  --AND V.MD$NUMBER NOT LIKE 'Q%'
                  --AND V.MD$NUMBER NOT LIKE '%TEST%'
                  AND V.MD$NUMBER = ?
                """;

            System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, hogi);
            //pstmt.setString(2, partNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String PRODUCTNO = rs.getString("PRODUCTNO"); //제품번호
                String OID = rs.getString("OID");
                String STATUS = rs.getString("STATUS");
                String EL_AOPEN = rs.getString("EL_AOPEN") == null ? "" : rs.getString("EL_AOPEN");
                String EL_ECWBUFBH = rs.getString("EL_ECWBUFBH") == null ? "" : rs.getString("EL_ECWBUFBH");

                String EL_ECCH = rs.getString("EL_ECCH") == null ? "" : rs.getString("EL_ECCH");
                String EL_ECBG = rs.getString("EL_ECBG") == null ? "" : rs.getString("EL_ECBG");
                String EL_ECEE = rs.getString("EL_ECEE") == null ? "" : rs.getString("EL_ECEE");
                String EL_ECJJ = rs.getString("EL_ECJJ") == null ? "" : rs.getString("EL_ECJJ");
                String EL_ERPW = rs.getString("EL_ERPW") == null ? "" : rs.getString("EL_ERPW");

                String EL_ECWRL =  rs.getString("EL_ECWRL") == null ? "" : rs.getString("EL_ECWRL");
                String EL_ETM =  rs.getString("EL_ETM") == null ? "" : rs.getString("EL_ETM");
                String EL_ECWBG = rs.getString("EL_ECWBG") == null ? "" : rs.getString("EL_ECWBG");
                String EL_ECWW = rs.getString("EL_ECWW") == null ? "" : rs.getString("EL_ECWW");
                String EL_ECSF = rs.getString("EL_ECSF") == null ? "" : rs.getString("EL_ECSF");
                String EL_ASPC =  rs.getString("EL_ASPC") == null ? "" : rs.getString("EL_ASPC");
                String EL_ASPCD =  rs.getString("EL_ASPCD") == null ? "" : rs.getString("EL_ASPCD");
                String EL_BCL =  rs.getString("EL_BCL") == null ? "" : rs.getString("EL_BCL");

                String EL_AMAN = rs.getString("EL_AMAN") == null ? "" : rs.getString("EL_AMAN");
                String EL_ASPSCD = rs.getString("EL_ASPSCD") == null ? "" : rs.getString("EL_ASPSCD");
                String EL_ABRAND = rs.getString("EL_ABRAND") == null ? "" : rs.getString("EL_ABRAND");
                String EL_ATYP = rs.getString("EL_ATYP") == null ? "" : rs.getString("EL_ATYP");
                String EL_ASPD = rs.getString("EL_ASPD") == null ? "" : rs.getString("EL_ASPD");
                String EL_ACAPA = rs.getString("EL_ACAPA") == null ? "" : rs.getString("EL_ACAPA");
                String EL_AUSE =  rs.getString("EL_AUSE") == null ? "" : rs.getString("EL_AUSE");

                ElvInfoDTO dto = new ElvInfoDTO();
                dto.setPRODUCTNO(PRODUCTNO); //제품번호
                dto.setProductoid(OID);
                dto.setStatus(STATUS);
                dto.setEL_AOPEN(EL_AOPEN);
                dto.setEL_ECWBUFBH(EL_ECWBUFBH);
                dto.setEL_ATYP(EL_ATYP);
                dto.setEL_ECCH(EL_ECCH);
                dto.setEL_ECBG(EL_ECBG);
                dto.setEL_ECEE(EL_ECEE);
                dto.setEL_ECJJ(EL_ECJJ);
                dto.setEL_ERPW(EL_ERPW);

                dto.setEL_ECWRL(EL_ECWRL);
                dto.setEL_ETM(EL_ETM);
                dto.setEL_ECWBG(EL_ECWBG);
                dto.setEL_ECWW(EL_ECWW);
                dto.setEL_ECSF(EL_ECSF);
                dto.setEL_ASPC(EL_ASPC);
                dto.setEL_ASPCD(EL_ASPCD);
                dto.setEL_BCL(EL_BCL);
                dto.setEL_AMAN(EL_AMAN);
                dto.setEL_ASPSCD(EL_ASPSCD);
                dto.setEL_ABRAND(EL_ABRAND);
                dto.setEL_ATYP(EL_ATYP);
                dto.setEL_ASPD(EL_ASPD);
                dto.setEL_ACAPA(EL_ACAPA);
                dto.setEL_AUSE(EL_AUSE);

                dataList.add(dto);
            } //end while

            System.out.println("dataList.size() = " + dataList.size());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return dataList;
    }

    /**
     * 영업사양 값 추출 개선
     * @param searchMdNumber
     * @return
     */
    public static ArrayList<HashMap<String, String>> findElvSearchInfoV2(String searchMdNumber) {
        //Connection con = null;
        //PreparedStatement pstmt = null;
        //ResultSet rs = null;

        //ArrayList<ElvInfoDTO> dataList = new ArrayList<ElvInfoDTO>();
        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();


        String query = """
                SELECT V.*
                FROM ELV_INFO$VF V, ELV_INFO$ID A 
                  WHERE 
                      V.vf$identity = A.id$ouid and V.vf$ouid = A.id$wip 
                      --AND V.MD$NUMBER NOT LIKE 'Q%'
                      --AND V.MD$NUMBER NOT LIKE '%TEST%'
                      AND V.MD$NUMBER = ?
            """;


        /*try (Connection conn = PLMDBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query) ) {*/
        try (Connection conn = PLMDBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {


            // 5. 쿼리의 '?' 위치에 파라미터 값 바인딩 (첫 번째 '?' 이므로 인덱스 1)
            // 주석 처리된 부분의 조건(LIKE 'Q%')을 볼 때 문자열로 추정되어 setString 사용
            pstmt.setString(1, searchMdNumber);

            // 6. 쿼리 실행 후 ResultSet은 내부에 중첩 try-with-resources로 감싸기
            try (ResultSet rs = pstmt.executeQuery()) {

                // --- [여기서부터 컬럼명 동적 추출 및 데이터 출력 로직] ---

               /* ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                System.out.println("========================================= 조회 결과 =========================================");

                // 컬럼명 먼저 쫙 출력
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rsmd.getColumnLabel(i) + "\t|\t");
                }
                System.out.println("\n---------------------------------------------------------------------------------------------");

                // 실제 로우(Row) 데이터 출력
                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    for (int i = 1; i <= columnCount; i++) {
                        // DB 컬럼 타입이 숫자든 날짜든 콘솔 출력을 위해 일괄 getString() 호출
                        System.out.print(rs.getString(i) + "\t|\t");
                    }
                    System.out.println(); // 한 행이 끝나면 줄바꿈
                }

                if (!hasData) {
                    System.out.println("조건에 맞는 데이터가 존재하지 않습니다.");
                }
                System.out.println("=============================================================================================");
*/
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                // 🌟 핵심 로직: ResultSet을 순회하며 Map으로 변환
                while (rs.next()) {
                    // 컬럼 순서를 유지하기 위해 LinkedHashMap 사용
                    HashMap<String, String> rowMap = new LinkedHashMap<>();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rsmd.getColumnLabel(i); // Key: 컬럼명
                        String columnValue = rs.getString(i) == null ? "" : rs.getString(i);       // Value: 데이터

                        // Map에 데이터 적재 (만약 DB 값이 NULL이면 columnValue도 null이 들어감)
                        rowMap.put(columnName, columnValue);
                    }

                    // 완성된 1줄(Row)의 Map을 List에 추가
                    resultList.add(rowMap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    //영업 사양 값 매칭
    /**
     * @apiNote 영업 사양 값 매칭
     * @param ouid
     * @return
     */
    public static String findCodeValue(String ouid) {

        String result = "";

        String query = """
                select NAME, DES, MSRTITLECODE from doscoditm
                WHERE OUID = ?
                """;


        try (Connection conn = PLMDBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, ouid);


            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    // 컬럼명으로 데이터 추출 (데이터 타입에 맞춰 getString, getInt 등 사용)
                    String name = rs.getString("NAME");
                    String tit = rs.getString("DES");
                    String msrTitleCode = rs.getString("MSRTITLECODE");

                    // 추출한 데이터를 Map에 담기
                    //Map<String, String> row = new HashMap<>();
                    //row.put("NAME", name);
                    //row.put("DES", tit);
                    //row.put("MSRTITLECODE", msrTitleCode);

                    //msrtitlecode NULL이면 DES(COD)을 값으로 한다.
                    //msrtitlecode 값이 있으면 name(CODN)을 값으로 한다.
                    result = tit;
                    if(msrTitleCode != null && !msrTitleCode.isEmpty()) {
                        result = name;
                    }

                    // 디버깅용 출력
                    //System.out.println("NAME: " + name + ", TIT: " + tit + ", MSRTITLECODE: " + msrTitleCode);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static boolean isNumeric(String str) {
        // null 이거나 빈 문자열이면 false 반환
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 정규표현식: 오직 0~9의 숫자로만 이루어져 있는지 확인
        // "^[0-9]+$" 또는 "\\d+" 를 사용할 수 있습니다.
        return str.matches("^[0-9]+$");
    }

}
