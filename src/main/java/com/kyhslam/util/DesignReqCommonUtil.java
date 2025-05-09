package com.kyhslam.util;


import com.kyhslam.dto.DesignDashDTO;
import com.kyhslam.dto.DesignRequestDTO;
import com.kyhslam.service.DesignDashboardService;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class DesignReqCommonUtil {

    
    //신전산요청 조회 - 2025년 등록 건
    public static ArrayList<DesignRequestDTO> findDesignReqs() {

        ArrayList<DesignRequestDTO> result = new ArrayList<DesignRequestDTO>();
        
        HashMap<String, String> data = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@newplm-prod-db.cdqjoonblwol.ap-northeast-2.rds.amazonaws.com:1521/PLMPRD";
            String id = "hdel_default";
            String pass = "hdel_default";

            con = DriverManager.getConnection(url, id, pass);

            System.out.println("con = " + con);


            StringBuffer sql = new StringBuffer();

            sql.append(" SELECT A.MD$NUMBER AS REQNO,   "); // 요청번호
            sql.append(" A.MD$STATUS AS REQSTATUS, A.MD$DESC AS RDETAIL,  A.MD$USER AS CUSER, A.HOGI,   "); //

            sql.append(" (select U.MD$DESC FROM FUSER$SF U WHERE U.MD$NUMBER = A.MD$USER) AS CUSERNAME,   ");
            sql.append(" A.MANAGER AS MANAGER,   "); //
            //A.MANAGER AS 처리자,

            sql.append(" CODN(A.PRIORITY) AS WOSUN,   "); //우선순위
            sql.append(" A.DESIGNPART AS GUBUN_KEY,    "); //구분-전기,기게 KEY
            sql.append(" CODN(A.DESIGNPART) AS GUBUN,    "); //구분-전기,기게

            sql.append(" A.REQUESTTYPE AS WORKGUBUN_KEY,   "); //작업구분 Key
            sql.append(" CODN(A.REQUESTTYPE) AS WORKGUBUN,   "); //작업구분
            sql.append(" CODN(A.REQUESTCAUSE) AS REQUESTCAUSE,   "); // 요청사유
            sql.append(" A.REQUESTDETAIL,   "); // 요청내용
            sql.append(" CODN(A.COSTINFLUENCE) AS COSTINFLUENCE,   "); //원가영향도

            sql.append(" SUBSTR(A.MD$CDATE, 1, 6) AS CRE_MONTH,   ");
            sql.append(" SUBSTR(A.MD$MDATE, 1, 6) AS MOD_MONTH,   ");
            sql.append(" DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CRE_DATE,   ");
            sql.append(" DATEFORMAT(A.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS MOD_DATE   ");

            sql.append(" FROM NEWPLMDESIGNREQUEST$VF A   ");
            sql.append(" WHERE   ");
            sql.append(" SUBSTR(A.MD$CDATE, 1, 4) = '2025'   ");



            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, month);

            rs = pstmt.executeQuery();

            System.out.println("222");
            int totalCnt = 0;
            while (rs.next()) {

                //Row curRow = sheet.createRow(totalCnt);

                String REQNO = rs.getString("REQNO") == null ? "" : rs.getString("REQNO");
                String REQSTATUS = rs.getString("REQSTATUS") == null ? "" : rs.getString("REQSTATUS");
                String RDETAIL = rs.getString("RDETAIL") == null ? "" : rs.getString("RDETAIL");
                String CUSER = rs.getString("CUSER") == null ? "" : rs.getString("CUSER");
                String CUSERNAME = rs.getString("CUSERNAME") == null ? "" : rs.getString("CUSERNAME");
                String MANAGER = rs.getString("MANAGER") == null ? "" : rs.getString("MANAGER");

                String HOGI = rs.getString("HOGI") == null ? "" : rs.getString("HOGI");
                String WOSUN = rs.getString("WOSUN") == null ? "" : rs.getString("WOSUN"); //우선순위


                String GUBUN = rs.getString("GUBUN");
                String GUBUN_KEY = rs.getString("GUBUN_KEY") == null ? "" : rs.getString("GUBUN_KEY"); //구분key
                String WORKGUBUN = rs.getString("WORKGUBUN");

                String REQUESTDETAIL = rs.getString("REQUESTDETAIL") == null ? "" : rs.getString("REQUESTDETAIL"); //요청내용
                String REQUESTCAUSE = rs.getString("REQUESTCAUSE"); //요청사유
                String COSTINFLUENCE = rs.getString("COSTINFLUENCE"); //원가영향도

                String CRE_DATE = rs.getString("CRE_DATE") == null ? "" : rs.getString("CRE_DATE"); //등록일
                String MOD_DATE = rs.getString("MOD_DATE") == null ? "" : rs.getString("MOD_DATE"); //수정일
                
                
                //CRE_MONTH
                String CRE_MONTH = rs.getString("CRE_MONTH") == null ? "" : rs.getString("CRE_MONTH"); //등록월
                String MOD_MONTH = rs.getString("MOD_MONTH") == null ? "" : rs.getString("MOD_MONTH"); //수정월
                
                System.out.println(REQNO + " , " + REQSTATUS + ", " + HOGI + ", " + GUBUN + "," + WORKGUBUN);

                DesignRequestDTO d = new DesignRequestDTO();
                d.setReqNo(REQNO);
                d.setStatus(REQSTATUS);
                d.setcUser(CUSER);
                d.setcUserName(CUSERNAME);
                d.setManager(MANAGER);

                d.setWosun(WOSUN);
                d.setGubun(GUBUN);
                d.setWorkGubun(WORKGUBUN);
                d.setReqCause(REQUESTCAUSE);
                d.setReqDetail(REQUESTDETAIL);
                d.setCostInfluence(COSTINFLUENCE);

                d.setCreMon(CRE_MONTH);
                d.setModMon(MOD_MONTH);
                d.setCreDate(CRE_DATE);
                d.setModDate(MOD_DATE);

                result.add(d);

                totalCnt++;
            }

            System.out.println("totalCnt = " + totalCnt);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException ex) {
            }

            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException ex) {
            }

            try {
                if (con != null)
                    con.close();
            } catch (SQLException ex) {
            }
        }

        return result;
    }

    //기계-수배로직
    //2500087253-2805876119

    //기계-2805876103,기준정보변경
    //기계-2805876135,정합성검토
    //기계-2805876208,자재번호채번
    //기계-2805876215,신견적 자재번호채번
    //기계-2805876153,DUTY TABLE
    //기계-2805876169,1품1도 요청
    public static HashMap<String, Integer> getCountByType(String designTypeOID, String reqTypeOID) {
        //HashMap<String, Integer> data = DesignReqCommonUtil.getMonthDataCount(designTypeOID, reqTypeOID);
        HashMap<String, Integer> data = getMonthDataCount(designTypeOID, reqTypeOID);
        return data;
    }

    //reqType - 2805876119,수배로직
    //designType -2500087253,기계 / 2500087252,전기

    /**
     *
     * @param designTypeOID 구분 - 기계/전기
     * @param reqTypeOID 작업구분 - 수배로직, 1품1도 등..
     * @return
     */
    public static ArrayList<DesignRequestDTO> findDesignReqFromType(String designTypeOID, String reqTypeOID) {
        ArrayList<DesignRequestDTO> result = new ArrayList<DesignRequestDTO>();

        HashMap<String, String> data = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@newplm-prod-db.cdqjoonblwol.ap-northeast-2.rds.amazonaws.com:1521/PLMPRD";
            String id = "hdel_default";
            String pass = "hdel_default";

            con = DriverManager.getConnection(url, id, pass);

            System.out.println("con = " + con);


            StringBuffer sql = new StringBuffer();

            sql.append(" SELECT A.MD$NUMBER AS REQNO,   "); // 요청번호
            sql.append(" A.MD$STATUS AS REQSTATUS, A.MD$DESC AS RDETAIL,  A.MD$USER AS CUSER, A.HOGI,   "); //

            sql.append(" (select U.MD$DESC FROM FUSER$SF U WHERE U.MD$NUMBER = A.MD$USER) AS CUSERNAME,   ");
            sql.append(" A.MANAGER AS MANAGER,   "); //
            //A.MANAGER AS 처리자,

            sql.append(" CODN(A.PRIORITY) AS WOSUN,   "); //우선순위
            sql.append(" A.DESIGNPART AS GUBUN_KEY,    "); //구분-전기,기게 KEY
            sql.append(" CODN(A.DESIGNPART) AS GUBUN,    "); //구분-전기,기게

            sql.append(" A.REQUESTTYPE AS WORKGUBUN_KEY,   "); //작업구분 Key
            sql.append(" CODN(A.REQUESTTYPE) AS WORKGUBUN,   "); //작업구분
            sql.append(" CODN(A.REQUESTCAUSE) AS REQUESTCAUSE,   "); // 요청사유
            sql.append(" A.REQUESTDETAIL,   "); // 요청내용
            sql.append(" CODN(A.COSTINFLUENCE) AS COSTINFLUENCE,   "); //원가영향도

            sql.append(" SUBSTR(A.MD$CDATE, 1, 6) AS CRE_MONTH,   ");
            sql.append(" SUBSTR(A.MD$MDATE, 1, 6) AS MOD_MONTH,   ");
            sql.append(" DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CRE_DATE,   ");
            sql.append(" DATEFORMAT(A.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS MOD_DATE   ");

            sql.append(" FROM NEWPLMDESIGNREQUEST$VF A   ");
            sql.append(" WHERE   ");
            sql.append(" SUBSTR(A.MD$CDATE, 1, 4) = '2025'   ");

            sql.append(" AND A.DESIGNPART = ?   "); //기계 or 전기
            sql.append(" AND A.REQUESTTYPE = ?   "); //작업구분 : 수배로직, 1품1도 등


            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, designTypeOID);
            pstmt.setString(2, reqTypeOID);
            rs = pstmt.executeQuery();

            int totalCnt = 0;
            while (rs.next()) {

                //Row curRow = sheet.createRow(totalCnt);

                String REQNO = rs.getString("REQNO") == null ? "" : rs.getString("REQNO");
                String REQSTATUS = rs.getString("REQSTATUS") == null ? "" : rs.getString("REQSTATUS");
                String RDETAIL = rs.getString("RDETAIL") == null ? "" : rs.getString("RDETAIL");
                String CUSER = rs.getString("CUSER") == null ? "" : rs.getString("CUSER");
                String CUSERNAME = rs.getString("CUSERNAME") == null ? "" : rs.getString("CUSERNAME");
                String MANAGER = rs.getString("MANAGER") == null ? "" : rs.getString("MANAGER");

                String HOGI = rs.getString("HOGI") == null ? "" : rs.getString("HOGI");
                String WOSUN = rs.getString("WOSUN") == null ? "" : rs.getString("WOSUN"); //우선순위


                String GUBUN = rs.getString("GUBUN");
                String GUBUN_KEY = rs.getString("GUBUN_KEY") == null ? "" : rs.getString("GUBUN_KEY"); //구분key
                String WORKGUBUN = rs.getString("WORKGUBUN");

                String REQUESTDETAIL = rs.getString("REQUESTDETAIL") == null ? "" : rs.getString("REQUESTDETAIL"); //요청내용
                String REQUESTCAUSE = rs.getString("REQUESTCAUSE"); //요청사유
                String COSTINFLUENCE = rs.getString("COSTINFLUENCE"); //원가영향도

                String CRE_DATE = rs.getString("CRE_DATE") == null ? "" : rs.getString("CRE_DATE"); //등록일
                String MOD_DATE = rs.getString("MOD_DATE") == null ? "" : rs.getString("MOD_DATE"); //수정일


                //CRE_MONTH
                String CRE_MONTH = rs.getString("CRE_MONTH") == null ? "" : rs.getString("CRE_MONTH"); //등록월
                String MOD_MONTH = rs.getString("MOD_MONTH") == null ? "" : rs.getString("MOD_MONTH"); //수정월

                System.out.println(REQNO + " , " + REQSTATUS + ", " + HOGI + ", " + GUBUN + "," + WORKGUBUN);

                DesignRequestDTO d = new DesignRequestDTO();
                d.setReqNo(REQNO);
                d.setStatus(REQSTATUS);
                d.setcUser(CUSER);
                d.setcUserName(CUSERNAME);
                d.setManager(MANAGER);

                d.setWosun(WOSUN);
                d.setGubun(GUBUN);
                d.setWorkGubun(WORKGUBUN);
                d.setReqCause(REQUESTCAUSE);
                d.setReqDetail(REQUESTDETAIL);
                d.setCostInfluence(COSTINFLUENCE);

                d.setCreMon(CRE_MONTH);
                d.setModMon(MOD_MONTH);
                d.setCreDate(CRE_DATE);
                d.setModDate(MOD_DATE);

                result.add(d);

                totalCnt++;
            }

            System.out.println("totalCnt = " + totalCnt);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException ex) {
            }

            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException ex) {
            }

            try {
                if (con != null)
                    con.close();
            } catch (SQLException ex) {
            }
        }

        return result;
    }


    //월별 등록 건수
    //구분-작업구분
    public static HashMap<String, Integer> getMonthDataCount(String designTypeOID, String reqTypeOID) {


        HashMap<String, Integer> result = new HashMap<>();

        //2500087253 기계
        //2805876119 수배로직
        ArrayList<DesignRequestDTO> data = DesignReqCommonUtil.findDesignReqFromType(designTypeOID, reqTypeOID);

        int cre202501 = 0;
        int cre202502 = 0;
        int cre202503 = 0;
        int cre202504 = 0;
        int cre202505 = 0;
        int cre202506 = 0;
        int cre202507 = 0;
        int cre202508 = 0;
        int cre202509 = 0;
        int cre202510 = 0;
        int cre202511 = 0;
        int cre202512 = 0;

        int com202501 = 0;
        int com202502 = 0;
        int com202503 = 0;
        int com202504 = 0;
        int com202505 = 0;
        int com202506 = 0;
        int com202507 = 0;
        int com202508 = 0;
        int com202509 = 0;
        int com202510 = 0;
        int com202511 = 0;
        int com202512 = 0;

        try {

            for (int i = 0; i < data.size(); i++) {
                DesignRequestDTO dto = data.get(i);

                String status = dto.getStatus();
                String creDate = dto.getCreDate();
                String modDate = dto.getModDate();
                String modMonth = dto.getModMon();

                if("CRT".equals(status))  {

                    if("202501".equals(modMonth)) {
                        cre202501++;
                    } else if ("202502".equals(modMonth)) {
                        cre202502++;
                    } else if ("202503".equals(modMonth)) {
                        cre202503++;
                    } else if ("202504".equals(modMonth)) {
                        cre202504++;
                    } else if ("202505".equals(modMonth)) {
                        cre202505++;
                    } else if ("202506".equals(modMonth)) {
                        cre202506++;
                    } else if ("202507".equals(modMonth)) {
                        cre202507++;
                    } else if ("202508".equals(modMonth)) {
                        cre202508++;
                    } else if ("202509".equals(modMonth)) {
                        cre202509++;
                    } else if ("202510".equals(modMonth)) {
                        cre202510++;
                    } else if ("202511".equals(modMonth)) {
                        cre202511++;
                    } else if ("202512".equals(modMonth)) {
                        cre202512++;
                    }

                } else if("RLS".equals(status)){
                    if("202501".equals(modMonth)) {
                        com202501++;
                    } else if ("202502".equals(modMonth)) {
                        com202502++;
                    } else if ("202503".equals(modMonth)) {
                        com202503++;
                    } else if ("202504".equals(modMonth)) {
                        com202504++;
                    } else if ("202505".equals(modMonth)) {
                        com202505++;
                    } else if ("202506".equals(modMonth)) {
                        com202506++;
                    } else if ("202507".equals(modMonth)) {
                        com202507++;
                    } else if ("202508".equals(modMonth)) {
                        com202508++;
                    } else if ("202509".equals(modMonth)) {
                        com202509++;
                    } else if ("202510".equals(modMonth)) {
                        com202510++;
                    } else if ("202511".equals(modMonth)) {
                        com202511++;
                    } else if ("202512".equals(modMonth)) {
                        com202512++;
                    }
                }
            }


            result.put("cre202501", cre202501);
            result.put("cre202502", cre202502);
            result.put("cre202503", cre202503);
            result.put("cre202504", cre202504);
            result.put("cre202505", cre202505);
            result.put("cre202506", cre202506);
            result.put("cre202507", cre202507);
            result.put("cre202508", cre202508);
            result.put("cre202509", cre202509);
            result.put("cre202510", cre202510);
            result.put("cre202511", cre202511);
            result.put("cre202512", cre202512);

            result.put("com202501", com202501);
            result.put("com202502", com202502);
            result.put("com202503", com202503);
            result.put("com202504", com202504);
            result.put("com202505", com202505);
            result.put("com202506", com202506);
            result.put("com202507", com202507);
            result.put("com202508", com202508);
            result.put("com202509", com202509);
            result.put("com202510", com202510);
            result.put("com202511", com202511);
            result.put("com202512", com202512);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }



        return result;
    }


    //대시보드 현황 조회
    public static ArrayList<DesignDashDTO> getDashboard() {

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        ArrayList<DesignDashDTO> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;
        try {

            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT A.design_id, ");
            sql.append(" A.batch_date, ");
            sql.append(" A.design_type_name AS GUBUN, ");
            sql.append(" A.design_work_type_name AS GUBUNTYPE, ");
            sql.append(" A.cre202501, A.cre202502, A.CRE202503, A.CRE202504, A.CRE202505, A.CRE202506, ");
            sql.append(" A.CRE202507, A.CRE202508, A.CRE202509, A.CRE202510, A.CRE202511, A.CRE202512, ");

            sql.append(" A.COM202501, A.COM202502, A.COM202503, A.COM202504, A.COM202505, A.COM202506, ");
            sql.append(" A.COM202507, A.COM202508, A.COM202509, A.COM202510, A.COM202511, A.COM202512 ");

            //todayValue
            sql.append(" FROM design_request A ");
            sql.append(" WHERE A.BATCH_DATE = ? ");



            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, todayValue);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String batch_date = rs.getString("batch_date") == null ? "" : rs.getString("batch_date");
                String GUBUN = rs.getString("GUBUN") == null ? "" : rs.getString("GUBUN");
                String GUBUNTYPE = rs.getString("GUBUNTYPE") == null ? "" : rs.getString("GUBUNTYPE");

                String CRE202501 = rs.getString("cre202501") == null ? "" : rs.getString("cre202501");
                String CRE202502 = rs.getString("cre202502") == null ? "" : rs.getString("cre202502");
                String CRE202503 = rs.getString("CRE202503") == null ? "" : rs.getString("CRE202503");
                String CRE202504 = rs.getString("CRE202504") == null ? "" : rs.getString("CRE202504");
                String CRE202505 = rs.getString("CRE202504") == null ? "" : rs.getString("CRE202505");
                String CRE202506 = rs.getString("CRE202506") == null ? "" : rs.getString("CRE202506");
                String CRE202507 = rs.getString("CRE202507") == null ? "" : rs.getString("CRE202507");
                String CRE202508 = rs.getString("CRE202508") == null ? "" : rs.getString("CRE202508");
                String CRE202509 = rs.getString("CRE202509") == null ? "" : rs.getString("CRE202509");
                String CRE202510 = rs.getString("CRE202510") == null ? "" : rs.getString("CRE202510");
                String CRE202511 = rs.getString("CRE202511") == null ? "" : rs.getString("CRE202511");
                String CRE202512 = rs.getString("CRE202512") == null ? "" : rs.getString("CRE202512");

                String COM202501 = rs.getString("COM202501") == null ? "" : rs.getString("COM202501");
                String COM202502 = rs.getString("COM202502") == null ? "" : rs.getString("COM202502");
                String COM202503 = rs.getString("COM202503") == null ? "" : rs.getString("COM202503");
                String COM202504 = rs.getString("COM202504") == null ? "" : rs.getString("COM202504");
                String COM202505 = rs.getString("COM202505") == null ? "" : rs.getString("COM202505");
                String COM202506 = rs.getString("COM202506") == null ? "" : rs.getString("COM202506");
                String COM202507 = rs.getString("COM202507") == null ? "" : rs.getString("COM202507");
                String COM202508 = rs.getString("COM202508") == null ? "" : rs.getString("COM202508");
                String COM202509 = rs.getString("COM202509") == null ? "" : rs.getString("COM202509");
                String COM202510 = rs.getString("COM202510") == null ? "" : rs.getString("COM202510");
                String COM202511 = rs.getString("COM202511") == null ? "" : rs.getString("COM202511");
                String COM202512 = rs.getString("COM202512") == null ? "" : rs.getString("COM202512");


                DesignDashDTO dto = new DesignDashDTO();
                dto.setBatchDate(batch_date);
                dto.setGubun(GUBUN);
                dto.setGubunName(GUBUNTYPE);

                dto.setCre202501(CRE202501);
                dto.setCre202502(CRE202502);
                dto.setCre202503(CRE202503);
                dto.setCre202504(CRE202504);
                dto.setCre202505(CRE202505);
                dto.setCre202506(CRE202506);
                dto.setCre202507(CRE202507);
                dto.setCre202508(CRE202508);
                dto.setCre202509(CRE202509);
                dto.setCre202510(CRE202510);
                dto.setCre202511(CRE202511);
                dto.setCre202512(CRE202512);

                dto.setCom202501(COM202501);
                dto.setCom202502(COM202502);
                dto.setCom202503(COM202503);
                dto.setCom202504(COM202504);
                dto.setCom202505(COM202505);
                dto.setCom202506(COM202506);
                dto.setCom202507(COM202507);
                dto.setCom202508(COM202508);
                dto.setCom202509(COM202509);
                dto.setCom202510(COM202510);
                dto.setCom202511(COM202511);
                dto.setCom202512(COM202512);


                result.add(dto);

            } // end while



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }
}
