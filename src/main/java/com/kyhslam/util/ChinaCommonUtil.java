package com.kyhslam.util;

import com.kyhslam.dto.HogiExportDTO;
import com.kyhslam.dto.PartInfoDTO;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class ChinaCommonUtil {


    public static Connection getConnection() {

        Connection con 			= null;


        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@100.100.29.32:1521:SHPLM";
            String id = "HES_DEFAULT";
            String pass = "HES_DEFAULT";

            con = DriverManager.getConnection(url, id, pass);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }


    public static void disconnect(Connection con, PreparedStatement pstmt, ResultSet rs) {

        try {
            if (rs != null)
                rs.close();
        }
        catch (SQLException ex) {}

        try {
            if (pstmt != null)
                pstmt.close();
        }
        catch (SQLException ex) {}

        try {
            if (con != null)
                con.close();
        }
        catch (SQLException ex) {}
    }


    public static HashMap<String, String> getPartInfoWithCN(String partNo) {

        partNo = "";

        //partNo = "C121P012084";
        HashMap<String, String> data = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            /*Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@100.100.29.32:1521:SHPLM";
            String id = "HES_DEFAULT";
            String pass = "HES_DEFAULT";*/

            //con = DriverManager.getConnection(url,id,pass);
            con = getConnection(); // 중국 Connection

            StringBuffer sql = new StringBuffer();

            sql.append(" with C as ");
            sql.append("  ( select V.vf$ouid AS OID from NORMALPART$VF V, NORMALPART$ID B ");
            sql.append("  where V.vf$identity = B.id$ouid and V.VF$OUID = B.ID$WIP ");
            sql.append("  and ( md$number in ( ? )  ) )");

            sql.append("  SELECT P.MD$NUMBER AS PARTNO, "); //파트NO
            sql.append("  P.MD$DESC AS DESCVAL, "); // 파트명
            sql.append("  P.ENAME AS ENAME, "); // EN명
            sql.append("  P.CNAME AS CNAME, "); // CN명
            sql.append("  NVL(P.G_L_CODE, '') AS GLCODE, ");
            sql.append("  NVL(P.SPEC, '') AS SPEC, ");
            sql.append("  NVL(COD(P.UOM), '') AS UOM, "); // 단위
            sql.append("  NVL(P.PART_SIZE, '') AS PART_SIZE, ");

            sql.append("  P.VF$VERSION AS PART_VERSION, ");
            sql.append("  NVL(COD(ORIGIN_DIV), '') DIV, "); // 최초구분

            sql.append("  NVL(COD(P.DISUSE_YN), '') AS DISAWAY, "); // 폐기여부
            //sql.append("  NVL(COD(P.DISUSE_YN), '') AS AA,  "); // -- 폐기여부

            sql.append("  p.MD$STATUS AS STATUS, "); // 상태

            sql.append("  (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNO, "); // BLOCKNO
            sql.append("  (SELECT S.MD$DESC FROM BLOCKNO$SF S WHERE S.SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNONAME  "); //BLOCK명

            sql.append("  FROM  NORMALPART$VF P ");
            sql.append("  WHERE P.VF$OUID = (SELECT OID FROM C) ");

            System.out.println("sql==" + sql.toString());

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {

                data.put("PARTNO", rs.getString("PARTNO"));
                data.put("DESCVAL", rs.getString("DESCVAL"));

                data.put("ENAME", rs.getString("ENAME"));
                data.put("CNAME", rs.getString("CNAME"));

                data.put("GLCODE", rs.getString("GLCODE"));

                data.put("SPEC", rs.getString("SPEC"));


                String partSize = rs.getString("PART_SIZE") == null ? "" : rs.getString("PART_SIZE");
                if(partSize != null) {
                    //partSize = "'" + partSize + "'";
/*                    if(partSize.contains("≤")) {
                        partSize = partSize.replaceAll("<", "&lt;=");
                    }

                    if(partSize.contains("<")) {
                        partSize = partSize.replaceAll("<", "&lt;");
                    }*/

                    if(partSize.contains("<")) {
                        partSize = partSize.replaceAll("<", "&lt;");
                    }

                    if(partSize.contains(">")) {
                        partSize = partSize.replaceAll(">", "&gt;");
                    }

                }
                data.put("PART_SIZE", partSize);

                data.put("UOM", rs.getString("UOM"));
                data.put("PART_VERSION", rs.getString("PART_VERSION"));
                data.put("DISAWAY", rs.getString("DISAWAY")); // 폐기여부

                data.put("STATUS", rs.getString("STATUS")); //  상태
                data.put("DIV", rs.getString("DIV")); // 최초구분

                data.put("BLOCKNO", rs.getString("BLOCKNO"));
                data.put("BLOCKNONAME", rs.getString("BLOCKNONAME"));

                //resultList.add(data);
            }



            System.out.println(data);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //DynaUtils.close(rs, pstmt, con);
            disconnect(con,  pstmt, rs);
        }

        return data;
    }

    //파일명으로 전체 검색
    public static ArrayList<HashMap<String, String>> getPartALLInfo(String partNo) {

        //partNo = "C121P012084";
        ArrayList<HashMap<String, String>>  result = new ArrayList<HashMap<String, String>>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@100.100.29.32:1521:SHPLM";
            String id = "HES_DEFAULT";
            String pass = "HES_DEFAULT";

            con = DriverManager.getConnection(url, id, pass);

            StringBuffer sql = new StringBuffer();


            sql.append(" with C as ");
            sql.append("  ( select V.vf$ouid AS OID from NORMALPART$VF V, NORMALPART$ID B ");
            sql.append("  where V.vf$identity = B.id$ouid and V.VF$OUID = B.ID$WIP ");
            sql.append("  and ( md$number in ( ? )  ) )");

            sql.append("  SELECT P.MD$NUMBER AS PARTNO, "); //파트NO
            sql.append("  P.MD$DESC AS DESCVAL, "); // 파트명
            sql.append("  P.ENAME AS ENAME, "); // EN명
            sql.append("  P.CNAME AS CNAME, "); // CN명
            sql.append("  NVL(P.G_L_CODE, '') AS GLCODE, ");
            sql.append("  NVL(P.SPEC, '') AS SPEC, ");
            sql.append("  NVL(COD(P.UOM), '') AS UOM, "); // 단위
            sql.append("  NVL(P.PART_SIZE, '') AS PART_SIZE, ");

            sql.append("  P.VF$VERSION AS PART_VERSION, ");
            sql.append("  NVL(COD(ORIGIN_DIV), '') DIV, "); // 최초구분

            sql.append("  NVL(COD(P.DISUSE_YN), '') AS DISAWAY, "); // 폐기여부
            //sql.append("  NVL(COD(P.DISUSE_YN), '') AS AA,  "); // -- 폐기여부

            sql.append("  p.MD$STATUS, "); // 상태

            sql.append("  (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNO, "); // BLOCKNO
            sql.append("  (SELECT S.MD$DESC FROM BLOCKNO$SF S WHERE S.SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNONAME  "); //BLOCK명

            sql.append("  FROM  NORMALPART$VF P ");
            sql.append("  WHERE P.VF$OUID = (SELECT OID FROM C) ");

            System.out.println("sql==" + sql.toString());

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                HashMap<String, String> data = new HashMap<>();

                data.put("PARTNO", rs.getString("PARTNO"));
                data.put("DESCVAL", rs.getString("DESCVAL"));
                data.put("GLCODE", rs.getString("GLCODE"));

                data.put("SPEC", rs.getString("SPEC"));

                String partSize = rs.getString("PART_SIZE") == null ? "" : rs.getString("PART_SIZE");
                if(partSize != null ) {
                    partSize = "(" + partSize + ")";
                }
                data.put("PART_SIZE", partSize);

                data.put("UOM", rs.getString("UOM"));
                data.put("PART_VERSION", rs.getString("PART_VERSION"));
                data.put("DISAWAY", rs.getString("DISAWAY"));

                data.put("BLOCKNO", rs.getString("BLOCKNO"));
                data.put("BLOCKNONAME", rs.getString("BLOCKNONAME"));

                result.add(data);
            }

            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //DynaUtils.close(rs, pstmt, con);
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


    //중국자재 조회
    public static ArrayList<PartInfoDTO> findChinaParts() {


        ArrayList<PartInfoDTO> list = new ArrayList<>();


        //오늘일자 출력
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedTodayDate = today.format(formatter);
        //System.out.println(formattedTodayDate);


        HashMap<String, String> data = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            con = getConnection();

            StringBuffer sql = new StringBuffer();

            //with C as
            sql.append("  with C as ");
            sql.append("  ( select V.vf$ouid AS OID from NORMALPART$VF V, NORMALPART$ID B   where V.vf$identity = B.id$ouid and V.VF$OUID = B.ID$WIP ) ");
            sql.append("  SELECT P.MD$NUMBER AS PARTNO, ");
            sql.append("  SUBSTR(P.MD$MDATE, 0,8) AS MDATE, "); //수정일 YYYYMMDD
            sql.append("  P.VF$VERSION AS PART_VERSION, ");
            sql.append(" P.MD$DESC AS DESCVAL, ");
            sql.append(" P.ENAME AS ENAME,  ");
            sql.append(" P.CNAME AS CNAME,   NVL(P.G_L_CODE, '') AS GLCODE, ");
            sql.append(" NVL(P.SPEC, '') AS SPEC,  ");
            sql.append(" NVL(COD(P.UOM), '') AS UOM,  ");
            sql.append(" NVL(P.PART_SIZE, '') AS PART_SIZE,  ");
            sql.append(" P.MD$CDATE as creDate,  "); //등록일
            sql.append(" P.MD$MDATE as modDate,  "); //수정일
            sql.append(" NVL(COD(ORIGIN_DIV), '') DIV,   NVL(COD(P.DISUSE_YN), '') AS DISAWAY,  ");
            sql.append(" P.MD$STATUS AS STATUS,  ");
            sql.append(" (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNO,  ");
            sql.append(" (SELECT S.MD$DESC FROM BLOCKNO$SF S WHERE S.SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNONAME  ");

            sql.append("  FROM  NORMALPART$VF P ");
            sql.append(" WHERE P.VF$OUID in (SELECT OID FROM C) ");
            sql.append("  AND SUBSTR(P.MD$MDATE, 0,4) = '2025' ");

            //formattedTodayDate
            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, formattedTodayDate);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                PartInfoDTO dto = new PartInfoDTO();
                dto.setPartNo(rs.getString("PARTNO") == null ? "" : rs.getString("PARTNO"));
                dto.setVersion(rs.getString("PART_VERSION") ==  null ? "" : rs.getString("PART_VERSION"));
                dto.setMod(rs.getString("MDATE") == null ? "" : rs.getString("MDATE"));
                dto.setCreDate(rs.getString("creDate") == null ? "" : rs.getString("creDate"));
                dto.setModDate(rs.getString("modDate") == null ? "" : rs.getString("modDate"));
                dto.setDesc(rs.getString("DESCVAL") == null ? "" : rs.getString("DESCVAL"));
                dto.setEName(rs.getString("ENAME") == null ? "" : rs.getString("ENAME"));
                dto.setCName(rs.getString("CNAME") == null ? "" : rs.getString("CNAME"));
                dto.setGlCode(rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE"));
                dto.setSpec(rs.getString("SPEC") == null ? "" : rs.getString("SPEC"));
                dto.setUom(rs.getString("UOM") == null ? "" : rs.getString("UOM"));
                dto.setPartSize(rs.getString("PART_SIZE") == null ? "" : rs.getString("PART_SIZE"));
                dto.setStatus(rs.getString("STATUS") == null ? "" : rs.getString("STATUS"));
                dto.setBlockNo(rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO"));
                dto.setBlockName(rs.getString("BLOCKNONAME") == null ? "" : rs.getString("BLOCKNONAME"));

                dto.setDiv(rs.getString("DIV") == null ? "" : rs.getString("DIV")); // 최초구분
                dto.setDisAway(rs.getString("DISAWAY") == null ? "" : rs.getString("DISAWAY")); // 폐기여부

                //System.out.println(dto.getPartNo() + " > " + dto.getVersion());

                list.add(dto);
            }

            System.out.println("list.size() = " + list.size());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect(con,  pstmt, rs);
        }

        return list;
    }



    //금일 중국pdm에서 릴리즈되 자재 조회

    /**
     * 금일 법인PDM(중국)에서 릴리즈 된 자재 조회
     */
    public static ArrayList<PartInfoDTO> findReleasedParts() {


        ArrayList<PartInfoDTO> list = new ArrayList<>();


        //오늘일자 출력
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedTodayDate = today.format(formatter);
        //System.out.println(formattedTodayDate);


        HashMap<String, String> data = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            con = getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append("  SELECT P.MD$NUMBER AS PARTNO, ");
            sql.append("  SUBSTR(P.MD$MDATE, 0,8) AS MDATE, "); //수정일 YYYYMMDD
            sql.append("  P.VF$VERSION AS PART_VERSION, ");
            sql.append(" P.MD$DESC AS DESCVAL, ");
            sql.append(" P.ENAME AS ENAME,  ");
            sql.append(" P.CNAME AS CNAME,   NVL(P.G_L_CODE, '') AS GLCODE, ");
            sql.append(" NVL(P.SPEC, '') AS SPEC,  ");
            sql.append(" NVL(COD(P.UOM), '') AS UOM,  ");
            sql.append(" NVL(P.PART_SIZE, '') AS PART_SIZE,  ");
            sql.append(" P.MD$CDATE as creDate,  "); //등록일
            sql.append(" P.MD$MDATE as modDate,  "); //수정일
            sql.append(" NVL(COD(ORIGIN_DIV), '') DIV,   NVL(COD(P.DISUSE_YN), '') AS DISAWAY,  ");
            sql.append(" P.MD$STATUS AS STATUS,  ");
            sql.append(" (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNO,  ");
            sql.append(" (SELECT S.MD$DESC FROM BLOCKNO$SF S WHERE S.SF$OUID =  DECODE(BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(BLOCKNO, 12))))) BLOCKNONAME  ");

            sql.append("  FROM  NORMALPART$VF P ");
            sql.append("  WHERE SUBSTR(P.MD$MDATE, 0,8) = ? ");
            sql.append("  AND SUBSTR(P.MD$MDATE, 0,4) = '2025' and P.MD$STATUS = 'RLS'  ");
            sql.append("  AND P.VF$VERSION != '0'  ");

            //formattedTodayDate
            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, formattedTodayDate);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                PartInfoDTO dto = new PartInfoDTO();
                dto.setPartNo(rs.getString("PARTNO") == null ? "" : rs.getString("PARTNO"));
                dto.setVersion(rs.getString("PART_VERSION") ==  null ? "" : rs.getString("PART_VERSION"));
                dto.setMod(rs.getString("MDATE") == null ? "" : rs.getString("MDATE"));
                dto.setCreDate(rs.getString("creDate") == null ? "" : rs.getString("creDate"));
                dto.setModDate(rs.getString("modDate") == null ? "" : rs.getString("modDate"));
                dto.setDesc(rs.getString("DESCVAL") == null ? "" : rs.getString("DESCVAL"));
                dto.setEName(rs.getString("ENAME") == null ? "" : rs.getString("ENAME"));
                dto.setCName(rs.getString("CNAME") == null ? "" : rs.getString("CNAME"));
                dto.setGlCode(rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE"));
                dto.setSpec(rs.getString("SPEC") == null ? "" : rs.getString("SPEC"));
                dto.setUom(rs.getString("UOM") == null ? "" : rs.getString("UOM"));
                dto.setPartSize(rs.getString("PART_SIZE") == null ? "" : rs.getString("PART_SIZE"));
                dto.setStatus(rs.getString("STATUS") == null ? "" : rs.getString("STATUS"));
                dto.setBlockNo(rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO"));
                dto.setBlockName(rs.getString("BLOCKNONAME") == null ? "" : rs.getString("BLOCKNONAME"));

                dto.setDiv(rs.getString("DIV") == null ? "" : rs.getString("DIV")); // 최초구분
                dto.setDisAway(rs.getString("DISAWAY") == null ? "" : rs.getString("DISAWAY")); // 폐기여부

                System.out.println(dto.getPartNo() + " > " + dto.getVersion());

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect(con,  pstmt, rs);
        }

        return list;
    }


    //데이터 저장
    public static void chinaPartSave(PartInfoDTO dto) {

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" INSERT INTO CHINA_PART(BATCH_DATE, PARTNO, ENAME, CNAME, PARTSIZE, CDESC, SPEC, MDATE, CDATE, UOM, BLOCKNO, BLOCKNAME, DISWAY, DIV, VERSION ) ");
            sql.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");


            //pstmt = con.prepareStatement(sql.toString());
            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, todayValue);
            pstmt.setString(2, dto.getPartNo());
            pstmt.setString(3, dto.getEName());
            pstmt.setString(4, dto.getCName());
            pstmt.setString(5, dto.getPartSize());
            pstmt.setString(6, dto.getDesc());
            pstmt.setString(7, dto.getSpec());
            pstmt.setString(8, dto.getModDate());
            pstmt.setString(9, dto.getCreDate());
            pstmt.setString(10, dto.getUom());
            pstmt.setString(11, dto.getBlockNo());
            pstmt.setString(12, dto.getBlockName());
            pstmt.setString(13, dto.getDisAway());
            pstmt.setString(14, dto.getDiv());
            pstmt.setString(15, dto.getVersion());

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
    }


    /**
     * 배치로 저장된 법인자재들 조회
     * @return
     */
    public static ArrayList<PartInfoDTO> getSaveData(String batchDate) {

        ArrayList<PartInfoDTO> list = new ArrayList<>();

        //오늘일자 출력
        //LocalDate now = LocalDate.now();
        //String todayValue = now.toString();


        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append("  SELECT  ");
            sql.append("  BATCH_DATE, ENAME, CNAME, PARTSIZE, ");
            sql.append("  CDESC, SPEC, MDATE, CDATE, UOM, BLOCKNO, BLOCKNAME, DISWAY, DIV ");
            sql.append("  FROM CHINA_PART ");
            //sql.append("  WHERE BATCH_DATE = ? ");

            if ( batchDate != null && !"".equals(batchDate)) {
                sql.append("  WHERE BATCH_DATE = '" + batchDate + "'");
            }

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, todayValue);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                PartInfoDTO dto = new PartInfoDTO();
                dto.setPartNo(rs.getString("PARTNO") == null ? "" : rs.getString("PARTNO"));
                dto.setVersion(rs.getString("PART_VERSION") ==  null ? "" : rs.getString("PART_VERSION"));
                dto.setMod(rs.getString("MDATE") == null ? "" : rs.getString("MDATE"));
                dto.setCreDate(rs.getString("creDate") == null ? "" : rs.getString("creDate"));
                dto.setModDate(rs.getString("modDate") == null ? "" : rs.getString("modDate"));
                dto.setDesc(rs.getString("DESCVAL") == null ? "" : rs.getString("DESCVAL"));
                dto.setEName(rs.getString("ENAME") == null ? "" : rs.getString("ENAME"));
                dto.setCName(rs.getString("CNAME") == null ? "" : rs.getString("CNAME"));
                dto.setGlCode(rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE"));
                dto.setSpec(rs.getString("SPEC") == null ? "" : rs.getString("SPEC"));
                dto.setUom(rs.getString("UOM") == null ? "" : rs.getString("UOM"));
                dto.setPartSize(rs.getString("PART_SIZE") == null ? "" : rs.getString("PART_SIZE"));
                dto.setStatus(rs.getString("STATUS") == null ? "" : rs.getString("STATUS"));
                dto.setBlockNo(rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO"));
                dto.setBlockName(rs.getString("BLOCKNONAME") == null ? "" : rs.getString("BLOCKNONAME"));

                dto.setDiv(rs.getString("DIV") == null ? "" : rs.getString("DIV")); // 최초구분
                dto.setDisAway(rs.getString("DISAWAY") == null ? "" : rs.getString("DISAWAY")); // 폐기여부

                //ystem.out.println(dto.getPartNo() + " , " + dto.getVersion());

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con,  pstmt, rs);
        }

        return list;
    }

    //금일 기준으로 -1일전 배치된 데이터 조회
    public static void getChinaPartsWithToday() {

        //오늘일자 출력
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate date = now.minusDays(1); // 1일 전날짜 출력
        String todayMinus = date.format(formatter);

        try {

            //전날 배치 실행 된 법인자재 조회
            ArrayList<PartInfoDTO> datas = getSaveData(todayMinus);
            for (PartInfoDTO dto : datas) {

            }
            //활성
            //릴리즈
            //중국법인

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }


    /**
     * 중국자재번호와 매칭되는 한국자재 있는지 찾기 함수를 수행하기 위한 Query 생성
     * @param chinaList
     * @param dataList
     */
    public static void matchingParts(ArrayList<PartInfoDTO> chinaList, ArrayList<PartInfoDTO> dataList) {

        try {
            String appendVal = "";
            int cnt = 0;

            for (int i = 0; i < chinaList.size(); i++) {
                String partNo = chinaList.get(i).getPartNo();
                appendVal += ("'" + partNo + "',");
                cnt++;

                if(cnt == 300) {
                    appendVal = appendVal.substring(0, appendVal.length() - 1);

                    //조회해서 dataList에 넣기
                    findMatchingPart(appendVal, dataList);
                    cnt = 0;
                    appendVal = "";
                }
            }

            if(cnt > 0) {
                if(appendVal != null && !appendVal.equals("")) {
                    appendVal = appendVal.substring(0, appendVal.length() - 1);
                    findMatchingPart(appendVal, dataList);
                }
            }

        } catch (Exception e) {
          e.printStackTrace();
        }
    }


    /**
     * 중국자재번호와 매칭되는 한국자재 있는지 찾기
     * 조건(국가:중국, 상태:릴리즈, 활성자재)
     * @param appendVal
     * @param dataList
     */
    public static void findMatchingPart(String appendVal, ArrayList<PartInfoDTO> dataList) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = PLMDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" with OUID as ");
            sql.append(" ( select A.vf$ouid from NORMALPART$vf A, NORMALPART$id B ");
            sql.append(" where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip )  ");

            sql.append(" SELECT A.MD$NUMBER AS PARTNO, SUBSTR(A.MD$MDATE, 0, 8) AS MOD_DATE, A.MD$DESC AS PARTNAME, ");
            sql.append(" A.VF$OUID AS OID, A.SPEC AS SPEC, A.BLOCKNO_NUMBER AS BLOCKNO, ");
            sql.append(" A.PART_SIZE AS PARTSIZE, ");
            sql.append(" A.G_L_CODE AS GLCODE, ");
            sql.append(" CODN(A.ORIGIN_DIV) AS ORIGINDIV, A.VF$VERSION AS VERSION, ");
            sql.append(" CODN(A.PART_STATUS) AS PART_STATUS, ");
            sql.append(" CODN(a.NATION) AS NATION, ");
            sql.append(" A.NATION AS NATIONKEY, A.MD$STATUS AS STATUS");

            sql.append(" FROM NORMALPART$VF A ");
            sql.append(" WHERE A.VF$OUID IN (SELECT * FROM OUID) ");
            sql.append(" AND A.PART_STATUS = '2466425004' "); //활성
            sql.append(" and A.NATION != '2803457397' "); //국가(2803457397:한국, 2803457356:중국)

            sql.append(" AND A.MD$STATUS = 'RLS' ");

            sql.append(" AND A.MD$NUMBER IN ( ");

            sql.append(appendVal);

            sql.append(" ) ");

            stmt = con.prepareStatement(sql.toString());
            rs = stmt.executeQuery();

            System.out.println("findMatchingPart.toString() = " + sql.toString());

            while(rs.next()) {
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String MOD_DATE = rs.getString("MOD_DATE");
                String BLOCKNO = rs.getString("BLOCKNO");
                String SPEC = rs.getString("SPEC");
                String PARTSIZE = rs.getString("PARTSIZE");
                String GLCODE = rs.getString("GLCODE");
                String VERSION = rs.getString("VERSION");
                String ORIGINDIV = rs.getString("ORIGINDIV");
                String NATION = rs.getString("NATION");
                String STATUS = rs.getString("STATUS");

                PartInfoDTO dto = new PartInfoDTO();
                dto.setPartNo(PARTNO);
                dto.setStatus(STATUS);
                dto.setBlockNo(BLOCKNO);
                dto.setSpec(SPEC);
                dto.setPartName(PARTNAME);
                dto.setModDate(MOD_DATE);
                dto.setPartSize(PARTSIZE);
                dto.setNation(NATION);
                dto.setVersion(VERSION);
                dto.setGlCode(GLCODE);

                dataList.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
    }

}
