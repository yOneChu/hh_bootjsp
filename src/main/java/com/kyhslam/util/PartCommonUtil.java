package com.kyhslam.util;

import com.kyhslam.dto.HogiExportDTO;
import com.kyhslam.dto.ProductDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PartCommonUtil {

    //호기의 출하예정일 추출
    public static String findExportDate(String hogi, String blockNo) {

        System.out.println("PartCommonUtil findExportDate start ==-" + hogi + " > " + blockNo);

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String returnValue = "";
        String startBlockNo = blockNo.substring(0,1);

        try {
            /*Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@HDEL-SRM-PROD-DB-A.CU93NQJHPJCN.AP-NORTHEAST-2.RDS.AMAZONAWS.COM:1521/ORCL";
            String id = "COMPRD";
            String pass = "COMPRD01";*/

            con = CommonDBConnection.getConnection();
            //con = DriverManager.getConnection(url,id,pass);

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT ");
            sql.append(" DISTINCT A.POSID AS WBS, ");
            sql.append(" A.MANDT,   ");
            sql.append(" A.PSPID AS 프로젝트정의, "); // 프로젝트정의
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'A' AND B.POSID = A.POSID) AS SHIP_A,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'B' AND B.POSID = A.POSID) AS SHIP_B,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'C' AND B.POSID = A.POSID) AS SHIP_C,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'D' AND B.POSID = A.POSID) AS SHIP_D,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'E' AND B.POSID = A.POSID) AS SHIP_E,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'F' AND B.POSID = A.POSID) AS SHIP_F   ");

            sql.append(" FROM ZPPT027 A ");
            sql.append(" WHERE A.ILDAT IS NOT NULL ");
            sql.append(" AND A.ACTIV = '05' "); // ACTIVE 구분 (5:출하, 07:준공)
            sql.append(" AND A.GUBUN = '01' ");
            sql.append(" AND A.POSID = ? ");

            //System.out.println("sql==" + sql.toString());

            if(con == null) {
                con = CommonDBConnection.getConnection();
            }

            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, hogi.trim());
            rs = stmt.executeQuery();

            while(rs.next()) {

                String SHIP_A = rs.getString("SHIP_A");
                String SHIP_B = rs.getString("SHIP_B");
                String SHIP_C = rs.getString("SHIP_C");
                String SHIP_D = rs.getString("SHIP_D");
                String SHIP_E = rs.getString("SHIP_E");
                String SHIP_F = rs.getString("SHIP_F");

                //System.out.println(SHIP_B + " > " + SHIP_C);

                switch (startBlockNo) {
                    case "A":
                        returnValue = SHIP_A;
                        break;
                    case "B":
                        returnValue = SHIP_B;
                        break;
                    case "C":
                        returnValue = SHIP_C;
                        break;
                    case "D":
                        returnValue = SHIP_D;
                        break;
                    case "E":
                        returnValue = SHIP_E;
                        break;
                    case "F":
                        returnValue = SHIP_F;
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonDBConnection.disconnect(con, stmt, rs);
        }
        //System.out.println("PartCommonUtil findExportDate END ==-");
        return returnValue;
    }

    //여러개로 출하예정일 조회
    //출하예정일을 여러개의 호기로 조회하는 로직
    //public static String findExportDateV2(String hogi, String blockNo) {
    public static HashMap<String, HogiExportDTO> findExportDateV2(ArrayList data) {


        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        HashMap<String, HogiExportDTO> resultMap = new HashMap<>();
        try {


            con = CommonDBConnection.getConnection();
            //con = DriverManager.getConnection(url,id,pass);

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT ");
            sql.append(" DISTINCT A.POSID AS WBS, ");
            sql.append(" A.MANDT,   ");
            sql.append(" A.PSPID AS 프로젝트정의, "); // 프로젝트정의
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'A' AND B.POSID = A.POSID) AS SHIP_A,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'B' AND B.POSID = A.POSID) AS SHIP_B,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'C' AND B.POSID = A.POSID) AS SHIP_C,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'D' AND B.POSID = A.POSID) AS SHIP_D,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'E' AND B.POSID = A.POSID) AS SHIP_E,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'F' AND B.POSID = A.POSID) AS SHIP_F   ");

            sql.append(" FROM ZPPT027 A ");
            sql.append(" WHERE A.ILDAT IS NOT NULL ");
            sql.append(" AND A.ACTIV = '05' "); // ACTIVE 구분 (5:출하, 07:준공)
            sql.append(" AND A.GUBUN = '01' ");
            //sql.append(" AND A.POSID = ? ");

            sql.append(" AND A.POSID IN ( ");

            String appendVal = "";

            if(data.size() > 700) {
                for(int i=0; i < 700; i++) {
                    ArrayList row = (ArrayList) data.get(i);
                    String hogi = (String) row.get(1);
                    appendVal += (" '" + hogi + "',");
                }




            } else {
                for(int i=0; i < data.size(); i++) {
                    ArrayList row = (ArrayList) data.get(i);
                    String hogi = (String) row.get(1);
                    appendVal += (" '" + hogi + "',");
                }
            }
            appendVal = appendVal.substring(0, appendVal.length() - 1);


            sql.append(appendVal);
            sql.append(" ) ");

            stmt = con.prepareStatement(sql.toString());

            rs = stmt.executeQuery();

            while(rs.next()) {

                String WBS = rs.getString("WBS"); //호기
                String SHIP_A = rs.getString("SHIP_A");
                String SHIP_B = rs.getString("SHIP_B");
                String SHIP_C = rs.getString("SHIP_C");
                String SHIP_D = rs.getString("SHIP_D");
                String SHIP_E = rs.getString("SHIP_E");
                String SHIP_F = rs.getString("SHIP_F");

                HogiExportDTO dto = new HogiExportDTO();
                dto.setHogi(WBS);
                dto.setSHIP_A(SHIP_A);
                dto.setSHIP_B(SHIP_B);
                dto.setSHIP_C(SHIP_C);
                dto.setSHIP_D(SHIP_D);
                dto.setSHIP_E(SHIP_E);
                dto.setSHIP_F(SHIP_F);

                resultMap.put(WBS.trim(), dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonDBConnection.disconnect(con, stmt, rs);
        }
        System.out.println("PartCommonUtil findExportDateV2 END ==-" );
        System.out.println("resultMap = " + resultMap);
        return resultMap;
    }


    //원가절감 대상 데이터의 출하예정일을 700개씩 모아서 조회하는 부분
    public static HashMap<String, HogiExportDTO> findExportDateV3(ArrayList data) {


        HashMap<String, HogiExportDTO> resultMap = new HashMap<>();
        try {

            String appendVal = "";

            int cnt = 0;
            for(int i=0; i < data.size(); i++) {
                ArrayList row = (ArrayList) data.get(i);
                String hogi = (String) row.get(1);
                appendVal += (" '" + hogi + "',");

                cnt++;

                if(cnt == 700) {
                    appendVal = appendVal.substring(0, appendVal.length() - 1);
                    findExportDateV4(appendVal, resultMap);
                    appendVal = "";
                    cnt = 0;
                }
            }

            if (cnt > 0) {
                if(appendVal != null && !appendVal.equals("")) {
                    appendVal = appendVal.substring(0, appendVal.length() - 1);
                    findExportDateV4(appendVal, resultMap);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }


    /**
     * 출하예정일 조회
     * @param appendVal
     * @param resultMap
     */
    public static void findExportDateV4(String appendVal, HashMap<String, HogiExportDTO> resultMap) {


        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        //HashMap<String, HogiExportDTO> resultMap = new HashMap<>();
        try {


            con = CommonDBConnection.getConnection();
            //con = DriverManager.getConnection(url,id,pass);

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT ");
            sql.append(" DISTINCT A.POSID AS WBS, ");
            sql.append(" A.MANDT,   ");
            sql.append(" A.PSPID AS 프로젝트정의, "); // 프로젝트정의
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'A' AND B.POSID = A.POSID) AS SHIP_A,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'B' AND B.POSID = A.POSID) AS SHIP_B,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'C' AND B.POSID = A.POSID) AS SHIP_C,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'D' AND B.POSID = A.POSID) AS SHIP_D,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'E' AND B.POSID = A.POSID) AS SHIP_E,   ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'F' AND B.POSID = A.POSID) AS SHIP_F   ");

            sql.append(" FROM ZPPT027 A ");
            sql.append(" WHERE A.ILDAT IS NOT NULL ");
            sql.append(" AND A.ACTIV = '05' "); // ACTIVE 구분 (5:출하, 07:준공)
            sql.append(" AND A.GUBUN = '01' ");
            //sql.append(" AND A.POSID = ? ");

            sql.append(" AND A.POSID IN ( ");

            sql.append(appendVal);

            sql.append(" ) ");

            stmt = con.prepareStatement(sql.toString());

            rs = stmt.executeQuery();

            while(rs.next()) {

                String WBS = rs.getString("WBS"); //호기
                String SHIP_A = rs.getString("SHIP_A");
                String SHIP_B = rs.getString("SHIP_B");
                String SHIP_C = rs.getString("SHIP_C");
                String SHIP_D = rs.getString("SHIP_D");
                String SHIP_E = rs.getString("SHIP_E");
                String SHIP_F = rs.getString("SHIP_F");

                HogiExportDTO dto = new HogiExportDTO();
                dto.setHogi(WBS);
                dto.setSHIP_A(SHIP_A);
                dto.setSHIP_B(SHIP_B);
                dto.setSHIP_C(SHIP_C);
                dto.setSHIP_D(SHIP_D);
                dto.setSHIP_E(SHIP_E);
                dto.setSHIP_F(SHIP_F);

                resultMap.put(WBS.trim(), dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonDBConnection.disconnect(con, stmt, rs);
        }
    }


    //기종 리스트
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

    /**
     * 품번으로 최신버전의 부품 조회
     * @param partNo
     * @return
     */
    public static HashMap<String, String> findOneFromPartNo(String partNo) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        HashMap<String, String> result = new HashMap<String, String>();

        try {

            //con = DBconnectionInfo.getPDM_DBConnection();
            con = PLMDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();

            sql.append("with C as");
            sql.append(" ( ");
            sql.append(" select V.vf$ouid AS OID from NORMALPART$VF V, NORMALPART$ID B ");
            sql.append(" where V.vf$identity = B.id$ouid and V.VF$OUID = B.ID$WIP ");
            sql.append(" and ( ");
            sql.append("      md$number in ( ? ) ");
            sql.append("     )");
            sql.append(" )");

            sql.append(" SELECT P.MD$NUMBER AS PARTNO, ");
            sql.append(" P.MD$DESC AS DESCVAL, NVL(P.G_L_CODE, '') GLCODE, ");
            sql.append(" NVL(P.SPEC, '') SPEC, ");
            sql.append(" NVL(P.PART_SIZE, '') PART_SIZE, ");
            sql.append(" NVL(COD(P.UOM), '') UOM, ");
            //sql.append(" P.BLOCKNO_NUMBER AS BLOCKNO ");

            sql.append(" MD$STATUS AS STATUS, ");
            sql.append(" CODN(A.PART_STATUS) AS PART_STATUS, "); // 활성
            sql.append(" CODN(a.NATION), "); //중국법인
            sql.append(" (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNO, ");
            sql.append(" (SELECT S.MD$DESC FROM BLOCKNO$SF S WHERE S.SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNONAME ");
            //(SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNO,

            sql.append("  FROM  NORMALPART$VF P  ");
            sql.append("  WHERE P.VF$OUID = (SELECT OID FROM C) ");

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String PARTNO = rs.getString("PARTNO");
                String DESCVAL = rs.getString("DESCVAL");
                String GLCODE = rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE");
                String SPEC = rs.getString("SPEC") == null ? "" : rs.getString("SPEC");
                String PART_SIZE   = rs.getString("PART_SIZE") == null ? "" : rs.getString("PART_SIZE");

                String NATION   = rs.getString("NATION");
                String PART_STATUS   = rs.getString("PART_STATUS");
                String UOM   = rs.getString("UOM");
                String STATUS   = rs.getString("STATUS");

                String BLOCKNO   = rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
                String BLOCKNONAME = rs.getString("BLOCKNONAME") == null ? "" : rs.getString("BLOCKNONAME");


                result.put("PARTNO", PARTNO);
                result.put("DESCVAL", DESCVAL);
                result.put("GLCODE", GLCODE);

                result.put("SPEC", SPEC);
                result.put("PART_SIZE", PART_SIZE);
                result.put("PART_STATUS", PART_STATUS);
                result.put("UOM", UOM); // 단위
                result.put("STATUS", STATUS);
                result.put("NATION", NATION);
                result.put("BLOCKNO", BLOCKNO);
                result.put("BLOCKNONAME", BLOCKNONAME);

                //System.out.println(PARTNO + " - " + BLOCKNO + " - " + DESCVAL + " , " + GLCODE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    /**
     * @category 품목별 공정진행 현황 : SAP (ZPPR030)
     * @param hogiNumber
     * @return
     */
    public static ArrayList<HashMap<String, String>> searchProductIngStatus(String hogiNumber) {

        //HashMap<String, String> data = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;


        String whereXp = "";

        hogiNumber = hogiNumber.trim().toUpperCase();

        if(hogiNumber.contains(";")) {
            String[] arr = hogiNumber.split(";");
            System.out.println("arr = " + Arrays.toString(arr));

            for(int i=0; i < arr.length; i++) {
                //productList.add(arr[i].trim());
                String a = arr[i].trim();
                whereXp += "'" + a + "',";

            }

            whereXp = whereXp.substring(0, whereXp.length()-1);
        }




        try {
            /*Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@HDEL-SRM-PROD-DB-A.CU93NQJHPJCN.AP-NORTHEAST-2.RDS.AMAZONAWS.COM:1521/ORCL";
            String id = "COMPRD";
            String pass = "COMPRD01";*/

            //con = DriverManager.getConnection(url,id,pass);
            con = CommonDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT  ");
            sql.append(" A.POSID,  ");
            sql.append(" A.PSTYPE,  ");
            sql.append(" A.CONTEXT,  ");
            sql.append(" A.SPEC,  ");
            sql.append(" A.MKINDS,  ");

            sql.append(" A.SHIPDAT1,  "); // 기계실 생산_출하일자
            sql.append(" A.SHIPDAT2,  "); // 구조물 생산_출하일자
            sql.append(" A.SHIPDAT3,  "); // 출입구 생산_출할일자
            sql.append(" A.SHIPDAT4,  "); // DOOR 생산_출하일자
            sql.append(" A.SHIPDAT5,  "); // CAGE 생산_출하일자
            sql.append(" A.SHIPDAT6  "); // 바닥재 생산_출하일자

            sql.append(" FROM ZMMT025 A  ");
            sql.append(" WHERE  ");
            sql.append(" A.PSTYPE = '02' ");
            //sql.append(" AND A.POSID IN ('203687L13', '203687L14', '203687L35', '203687L41', '203687L42')  ");
            //sql.append(" AND A.POSID = ?  ");

            sql.append(" AND A.POSID IN (" + whereXp + ")");


            //System.out.println("sql==" + sql.toString());

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, hogiNumber);

            rs = pstmt.executeQuery();

            while(rs.next()) {

                HashMap<String, String> data = new HashMap<String, String>();
                data.put("POSID", rs.getString("POSID"));
                data.put("PSTYPE", rs.getString("PSTYPE"));
                data.put("CONTEXT", rs.getString("CONTEXT"));
                data.put("SPEC", rs.getString("SPEC"));
                data.put("MKINDS", rs.getString("MKINDS"));

                data.put("SHIPDAT1", rs.getString("SHIPDAT1"));
                data.put("SHIPDAT2", rs.getString("SHIPDAT2"));
                data.put("SHIPDAT3", rs.getString("SHIPDAT3"));
                data.put("SHIPDAT4", rs.getString("SHIPDAT4"));
                data.put("SHIPDAT5", rs.getString("SHIPDAT5"));
                data.put("SHIPDAT6", rs.getString("SHIPDAT6"));

                //System.out.println("data = " + data);

                resultList.add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CommonDBConnection.disconnect(con, pstmt, rs);
        }

        return resultList;
    }

    /**
     * 출하예정일 (ZPPT027)
     * @param hogiNumber
     * @return
     */
    public static ArrayList<HashMap<String, String>> getExportDate(String hogiNumber) {

        //HashMap<String, String> data = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;


        String whereXP = "";
        hogiNumber = hogiNumber.trim().toUpperCase();

        if(hogiNumber.contains(";")) {
            String[] arr = hogiNumber.split(";");
            System.out.println("arr = " + Arrays.toString(arr));

            for(int i=0; i < arr.length; i++) {
                String a = arr[i].trim();
                whereXP += "'" + a + "',";
            }

            whereXP = whereXP.substring(0, whereXP.length()-1);
        }

        try {
            /*Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@HDEL-SRM-PROD-DB-A.CU93NQJHPJCN.AP-NORTHEAST-2.RDS.AMAZONAWS.COM:1521/ORCL";
            String id = "COMPRD";
            String pass = "COMPRD01";

            con = DriverManager.getConnection(url,id,pass);*/

            con = CommonDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT ");
            sql.append(" DISTINCT A.POSID AS WBS, A.MANDT, A.PSPID AS PJTNAME, ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'A' AND B.POSID = A.POSID) AS SHIP_A, ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'B' AND B.POSID = A.POSID) AS SHIP_B, ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'C' AND B.POSID = A.POSID) AS SHIP_C, ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'D' AND B.POSID = A.POSID) AS SHIP_D, ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'E' AND B.POSID = A.POSID) AS SHIP_E, ");
            sql.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'F' AND B.POSID = A.POSID) AS SHIP_F, ");


            sql.append(" ( ");
            sql.append(" SELECT MIN(TO_NUMBER(B.ILDAT)) FROM ZPPT027 B ");
            sql.append(" WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'A' AND B.PSPID = A.PSPID ");
            sql.append(" ) AS SHIP_MIN_A, ");

            sql.append(" ( ");
            sql.append(" SELECT MIN(TO_NUMBER(B.ILDAT)) FROM ZPPT027 B ");
            sql.append(" WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'B' AND B.PSPID = A.PSPID ");
            sql.append(" ) AS SHIP_MIN_B, ");

            sql.append(" ( ");
            sql.append(" SELECT MIN(TO_NUMBER(B.ILDAT)) FROM ZPPT027 B ");
            sql.append(" WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'C' AND B.PSPID = A.PSPID ");
            sql.append(" ) AS SHIP_MIN_C, ");

            sql.append(" ( ");
            sql.append(" SELECT MIN(TO_NUMBER(B.ILDAT)) FROM ZPPT027 B ");
            sql.append(" WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'D' AND B.PSPID = A.PSPID ");
            sql.append(" ) AS SHIP_MIN_D, ");

            sql.append(" ( ");
            sql.append(" SELECT MIN(TO_NUMBER(B.ILDAT)) FROM ZPPT027 B ");
            sql.append(" WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'E' AND B.PSPID = A.PSPID ");
            sql.append(" ) AS SHIP_MIN_E, ");

            sql.append(" ( ");
            sql.append(" SELECT MIN(TO_NUMBER(B.ILDAT)) FROM ZPPT027 B ");
            sql.append(" WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = 'F' AND B.PSPID = A.PSPID ");
            sql.append(" ) AS SHIP_MIN_F");



            sql.append(" FROM ZPPT027 A ");
            sql.append(" WHERE A.ILDAT IS NOT NULL ");
            sql.append(" AND A.ACTIV = '05' ");   //-- ACTIVE 구분 (5:출하, 07:준공)
            sql.append(" AND A.GUBUN = '01' ");  //-- 예정일

            sql.append(" AND A.POSID IN ( " + whereXP + " )");



            System.out.println("sql==" + sql.toString());

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, startDate);

            rs = pstmt.executeQuery();

            //pstmt = con.prepareStatement(sql.toString()); pstmt.setString(1, partNo);
            //rs = pstmt.executeQuery();


            while(rs.next()) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("WBS", rs.getString("WBS"));
                data.put("MANDT", rs.getString("MANDT"));
                data.put("PJTNAME", rs.getString("PJTNAME"));

                data.put("SHIP_A", rs.getString("SHIP_A"));
                data.put("SHIP_B", rs.getString("SHIP_B"));
                data.put("SHIP_C", rs.getString("SHIP_C"));
                data.put("SHIP_D", rs.getString("SHIP_D"));
                data.put("SHIP_E", rs.getString("SHIP_E"));
                data.put("SHIP_F", rs.getString("SHIP_F"));

                data.put("SHIP_MIN_A", rs.getString("SHIP_MIN_A"));
                data.put("SHIP_MIN_B", rs.getString("SHIP_MIN_B"));
                data.put("SHIP_MIN_C", rs.getString("SHIP_MIN_C"));
                data.put("SHIP_MIN_D", rs.getString("SHIP_MIN_D"));
                data.put("SHIP_MIN_E", rs.getString("SHIP_MIN_E"));
                data.put("SHIP_MIN_F", rs.getString("SHIP_MIN_F"));

                resultList.add(data);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //DynaUtils.close(rs, pstmt, con);
            CommonDBConnection.disconnect(con, pstmt, rs);
        }

        return resultList;
    }
    
    /**
     * 제품의 최신 1레벨 조회
     * @param productNo
     * @return
     */
    public static ArrayList<ProductDto> findProductInfo(String productNo) {
        System.out.println("PartCommonUtil findProductInfo start ==-" + productNo );

        ArrayList<ProductDto> list = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {


            con = PLMDBConnection.getConnection();

            String sql = """
                with ouid as
                                          ( select vf$ouid AS VFOID from product$vf, product$id
                                           	where vf$identity = id$ouid and vf$ouid = id$wip
                                           	and (
                                              	  md$number = ?
                                              	)
                                          )
                                         SELECT
                                         			 PE.ASSOOUID ASSOOUID
                                         			 , PE.PRODUCTOUID PRODUCTOUID
                                         			 , PE.PARTOUID PARTOUID
                                         			 , PE.SEQ
                                         			 , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) PARENTNO
                                         			 , (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = PE.PARTOUID) CADCNT
                                         			 , NP.MD$NUMBER AS PARTNO
                                         			 , cod(NP.NATION) AS NATION
                                         			 , NP.compen_part AS COMPEN_PART
                                         			 , NP.MD$DESC AS PARTNAME
                                         			 , NP.VF$VERSION AS VERSION
                                         			 , NVL(NP.G_L_CODE, '') AS GLCODE
                                         			 , NVL(NP.SPEC, '') AS SPEC
                                         			 , NVL(NP.PART_SIZE, '') AS PART_SIZE
                                         			 , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO
                                         			 , (SELECT NVL(LOSSRATE, '') FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) LOSSRATE
                                         			 , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCK_OPT
                                         			 , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) UPPERBLOCKNO
                                         			 , NVL(COD(NP.UOM), '') AS UOM
                                         			 , PE.QTY
                                         			 , VP.WORK_QTY
                                         			 , PE.CMT
                                         			 , VP.WORK_CMT
                                         			 , PE.COLOR
                                         			 , VP.WORK_COLOR
                                         			 , NVL(CODN(NP.ORIGIN_DIV), '') DIV
                                         			 , NVL(PE.MBOM, '') MBOM
                                         			 , NVL(COD(NP.PART_MBOM), '') PART_MBOM
                                         			 , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) USERNAME
                                         			 , NP.MD$USER USERID
                                         			 , NP.OLD_CODE
                                         			 , NP.OLD_CODE2
                                         			 , NP.OLD_CODE3
                                         			 , COD(NP.SPT) SPT
                                         			 , COD(NP.PARTMPCHECK) PARTMPCHECK
                                         			 , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = PE.CUSER) CUSERNAME
                                         			 , PE.CUSER CUSERID
                                         			 , 1 LEV
                                         			 , 'F' ISLEAF
                                         			 , VP.UCHECK
                                         			 , VP.MCHECK
                                         			 , NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION
                                         			 , PE.CDATE
                                         			 , VP.MDATE
                                         			 , VP.user5
                                         			 , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) HASCHILD
                                                FROM
                                             PARTOFEBOM PE
                                            INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                                            LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                                            WHERE
                                             PE.PRODUCTOUID = (SELECT VFOID FROM ouid)
                                            ORDER BY TO_NUMBER(PE.SEQ)
        """;


            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productNo);
            rs = stmt.executeQuery();

            while(rs.next()) {

                //PRODUCTOUID
                String PRODUCTOUID = rs.getString("PRODUCTOUID");
                String PARENTNO = rs.getString("PARENTNO");
                String PARTOUID = rs.getString("PARTOUID");
                String SEQ = rs.getString("SEQ");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION");

                String BLOCKNO = rs.getString("BLOCKNO");
                String BLOCK_OPT = rs.getString("BLOCK_OPT"); //내작외작
                String GLCODE = rs.getString("GLCODE");

                String NATION = rs.getString("NATION");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");

                String QTY = rs.getString("QTY");
                String CMT = rs.getString("CMT");
                String UCHECK = rs.getString("UCHECK");
                String USERNAME = rs.getString("USERNAME");
                String USERID = rs.getString("USERID");

                ProductDto dto = new ProductDto();
                dto.setProductOid(PRODUCTOUID);
                dto.setProductNo(PARENTNO);

                dto.setSeq(SEQ);
                dto.setPartNo(PARTNO);
                dto.setPartNoOID(PARTOUID);
                dto.setPartName(PARTNAME);
                dto.setVersion(VERSION);
                dto.setBlockNo(BLOCKNO);
                dto.setBlock_opt(BLOCK_OPT);
                dto.setGlCode(GLCODE);

                dto.setNation(NATION);
                dto.setSpec(SPEC);
                dto.setPart_size(PART_SIZE);
                dto.setQty(QTY);
                dto.setCmt(CMT);
                dto.setUcheck(UCHECK);
                dto.setUsername(USERNAME);
                dto.setUserId(USERID);

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
        return list;
    }

}
