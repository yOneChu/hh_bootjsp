package com.kyhslam.service;


import com.kyhslam.domain.DesignRequest;
import com.kyhslam.domain.DesignRequestData;
import com.kyhslam.dto.DesignDashDTO;
import com.kyhslam.dto.DesignRequestDTO;
import com.kyhslam.repository.DesignReqRepository;
import com.kyhslam.util.VaultDBConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DesignDashboardService {

    private final DesignReqRepository repository;


    public void designReqProcess() {


        //기계-2500087253
        //기계-수배-2805876119
        getCountByType("2500087253", "2805876119", "기계", "수배로직");

        //기계-2805876103,기준정보변경
        getCountByType("2500087253", "2805876103", "기계", "기준정보변경");

        //기계-2805876135,정합성검토
        getCountByType("2500087253", "2805876135", "기계", "정합성검토");

        //기계-2805876208,자재번호채번
        getCountByType("2500087253", "2805876208", "기계", "자재번호채번");

        //기계-2805876215,신견적_자재번호채번
        getCountByType("2500087253", "2805876215", "기계", "신견적_자재번호채번");

        //기계-2805876153,DUTY TABLE
        getCountByType("2500087253", "2805876153", "기계", "DUTY_TABLE");

        //기계-2805876169,1품1도 요청
        getCountByType("2500087253", "2805876169", "기계", "1품1도_요청");


        //전기-2500087252
        //전기-2805876119,수배로직
        getCountByType("2500087252", "2805876119", "전기", "수배로직");

        //전기-2805876103,기준정보변경
        getCountByType("2500087252", "2805876103", "전기", "기준정보변경");

        //전기-2805876208,자재번호채번
        getCountByType("2500087252", "2805876208", "전기", "자재번호채번");

        //전기-2805876135,정합성검토
        getCountByType("2500087252", "2805876135", "전기", "정합성검토");

        //전기-2805876215,신견적_자재번호채번
        getCountByType("2500087252", "2805876215", "전기", "신견적_자재번호채번");

        //전기-2805876153,DUTY_TABLE
        getCountByType("2500087252", "2805876153", "전기", "DUTY_TABLE");

    }










    public HashMap<String, Integer> getCountByType(String designTypeOID, String reqTypeOID,String type, String workType) {
        //HashMap<String, Integer> data = DesignReqCommonUtil.getMonthDataCount(designTypeOID, reqTypeOID);
        HashMap<String, Integer> data = getMonthDataCount(designTypeOID, reqTypeOID, type, workType);
        return data;
    }


    @Transactional
    public HashMap<String, Integer> getMonthDataCount(String designTypeOID, String reqTypeOID, String type, String workType) {
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        HashMap<String, Integer> result = new HashMap<>();

        //2500087253 기계
        //2805876119 수배로직
        //ArrayList<DesignRequestDTO> data = DesignReqCommonUtil.findDesignReqFromType(designTypeOID, reqTypeOID);
        ArrayList<DesignRequestDTO> data = findDesignReqFromType(designTypeOID, reqTypeOID);

        //findDesignReqFromType
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

                if ("CRT".equals(status)) {

                    if ("202501".equals(modMonth)) {
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

                } else if ("RLS".equals(status)) {
                    if ("202501".equals(modMonth)) {
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


            DesignRequest req = new DesignRequest();

            req.setDesignTypeName(type);
            req.setDesignWorkTypeName(workType);

            req.setBatchDate(todayValue);
            req.setCre202501(cre202501);
            req.setCre202502(cre202502);
            req.setCre202503(cre202503);
            req.setCre202504(cre202504);
            req.setCre202505(cre202505);
            req.setCre202506(cre202506);
            req.setCre202507(cre202507);
            req.setCre202508(cre202508);
            req.setCre202509(cre202509);
            req.setCre202510(cre202510);
            req.setCre202511(cre202511);
            req.setCre202512(cre202512);

            req.setCom202501(com202501);
            req.setCom202502(com202502);
            req.setCom202503(com202503);
            req.setCom202504(com202504);
            req.setCom202505(com202505);
            req.setCom202506(com202506);
            req.setCom202507(com202507);
            req.setCom202508(com202508);
            req.setCom202509(com202509);
            req.setCom202510(com202510);
            req.setCom202511(com202511);
            req.setCom202512(com202512);

            repository.reqSave(req);

            /*result.put("cre202501", cre202501);
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
            result.put("com202512", com202512);*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return result;
    }



    /**
     *
     * @param designTypeOID 구분 - 기계/전기
     * @param reqTypeOID 작업구분 - 수배로직, 1품1도 등..
     * @return
     */
    public ArrayList<DesignRequestDTO> findDesignReqFromType(String designTypeOID, String reqTypeOID) {
        ArrayList<DesignRequestDTO> result = new ArrayList<DesignRequestDTO>();

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

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




                DesignRequestData dd = new DesignRequestData();
                dd.setBatchDate(todayValue);
                dd.setReqNo(REQNO);
                dd.setHogi(HOGI);
                dd.setStatus(REQSTATUS);

                dd.setGubun(GUBUN);
                dd.setGubun(GUBUN_KEY);

                dd.setWorkGubun(WORKGUBUN);

                dd.setCUser(CUSER);
                dd.setCUserName(CUSERNAME);

                dd.setManager(MANAGER);
                dd.setReqCause(REQUESTCAUSE);
                dd.setReqDetail(REQUESTDETAIL);
                dd.setCostInfluence(COSTINFLUENCE);
                dd.setCreMon(CRE_MONTH);
                dd.setModMon(MOD_MONTH);
                dd.setCreDate(CRE_DATE);
                dd.setModDate(MOD_DATE);

                repository.reqDataSave(dd);

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

}
