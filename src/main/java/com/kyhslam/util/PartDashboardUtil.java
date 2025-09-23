package com.kyhslam.util;

import com.kyhslam.dto.PartInfoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class PartDashboardUtil {


    /**
     * PLM에 등록된 자재 개수 (엘리베이터만)
     * AND A.NATION != '2803457356' --중국법인 제외
     * FLAG: ACTIVE, INACTIVE
     * @param flag
     * @return
     */
    public static String findPLMPartSum(String flag) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        String allCount = "";

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                    SELECT
                        COUNT(A.md$number) AS ALLCNT
                    FROM normalpart$vf A, normalpart$id B
                    WHERE A.vf$ouid = B.id$last
                    AND A.NATION != '2803457356' --중국법인 제외
                    --AND A.PART_STATUS = '2466425004' --활성
                    AND A.MD$STATUS = 'RLS' --릴리즈
                    AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                """;

            //System.out.println("sql = " + sql);

            // 활성
            if (flag != null && "ACTIVE".equals(flag)) {
                sql += " AND A.PART_STATUS = '2466425004' ";
                //sql += " AND A.PART_STATUS = '" + productOID + "' ";
            }

            // 비활성
            if (flag != null && "INACTIVE".equals(flag)) {
                sql += " AND A.PART_STATUS = '2466425005' ";
                //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
            }

            // 폐기
            if (flag != null && "OSL".equals(flag)) {
                sql += " AND A.PART_STATUS = '2501081338' ";
                //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
            }

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, productOID);
            //pstmt.setString(2, partNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String ALLCNT = rs.getString("ALLCNT"); //제품번호
                allCount = ALLCNT;
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return allCount;
    }


    /**
     * @apiNote 자재 현황 - 자재조회(excel)
     * @param year
     * @param partNo
     * @param partName
     * @param active
     * @return
     */
    public static ArrayList<PartInfoDTO> findPLMPartV1(String year, String partNo, String partName, String active) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<PartInfoDTO> result = new ArrayList<>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                  with ouid as
                              ( select A.vf$ouid from NORMALPART$vf A, NORMALPART$id B
                                where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
                                --and ( md$number in ( '18900360G0700') )
                                 AND SUBSTR(A.MD$CDATE, 0, 4) IN( ? )
                              )
                  SELECT A.MD$NUMBER AS PARTNO,
                         A.MD$DESC AS PARTNAME,
                         A.G_L_CODE AS GL_CODE,
                         --A.MD$CDATE,
                         DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                         --CAD.MD$NUMBER AS CADNO,
                         --CAD.MD$DESC AS CADNAME,
                         --DECODE(CAD.MD$NUMBER, NULL, NULL, CAD.MD$NUMBER || ' ' || CAD.MD$DESC) AS CADDESC,
                         CODN(A.PART_STATUS) AS PART_STATUS,
                         COD(A.UOM) AS UOM,
                         A.VF$VERSION AS VERSION,
                         CODN(A.NATION) AS NATION,
                         COD(A.DESIGN_USE) AS DESIGN_USE,
                         COD(A.COST_USE) AS COST_USE,
                         CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                         A.BLOCKNO_NUMBER,
                         A.SPEC,
                         A.PART_SIZE AS PARTSIZE
                  FROM NORMALPART$VF A
                  --JOIN AUTOCAD_FILE$VF CAD ON CAD.VF$OUID = GETID(A.DRAWING_NO)
                  WHERE A.VF$OUID IN (SELECT * FROM OUID)
                  --AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) != '6'
                  --AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) != '5'
                  AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                """;


            //활성
            if (active != null && "ACTIVE".equals(active)) {
                sql += " AND A.PART_STATUS = '2466425004' ";
                //sql += " AND A.PART_STATUS = '" + productOID + "' ";
            }

            //비활성
            if (active != null && "INACTIVE".equals(active)) {
                sql += " AND A.PART_STATUS = '2466425005' ";
                //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
            }

            //폐기
            if (active != null && "OSL".equals(active)) {
                sql += " AND A.PART_STATUS = '2501081338' ";
                //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
            }


            //sql += " AND A.MD$NUMBER LIKE '10111175G010%' ";
            if(partNo != null && !"".equals(partNo)){
                partNo = partNo.toUpperCase();

                if (partNo.contains("*")) {
                    partNo = partNo.replace("*", "%");
                    sql += " AND A.MD$NUMBER LIKE '" + partNo + "' ";
                } else {
                    sql += " AND A.MD$NUMBER = '" + partNo + "' ";
                }
            }

            if(partName != null && !"".equals(partName)){
                partName = partName.toUpperCase();

                if (partName.contains("*")) {
                    partName = partName.replace("*", "%");
                    sql += " AND A.MD$DESC LIKE '" + partName + "' ";
                } else {
                    sql += " AND A.MD$DESC = '" + partName + "' ";
                }
            }

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, year);
            //pstmt.setString(1, productOID);

            System.out.println("sql.toString() = " + sql.toString());
            
            rs = pstmt.executeQuery();

            while(rs.next()) {
                String PARTNO = rs.getString("PARTNO"); //제품번호
                String glCode = rs.getString("GL_CODE");
                String PARTNAME = rs.getString("PARTNAME");

                String VERSION = rs.getString("VERSION");
                String PART_STATUS = rs.getString("PART_STATUS");
                String DESIGN_USE = rs.getString("DESIGN_USE");
                String COST_USE = rs.getString("COST_USE");
                String ORIGIN_DIV = rs.getString("ORIGIN_DIV");
                String BLOCKNO_NUMBER = rs.getString("BLOCKNO_NUMBER");
                String SPEC = rs.getString("SPEC");
                String UOM = rs.getString("UOM");
                String PARTSIZE = rs.getString("PARTSIZE");

                PartInfoDTO dto = new PartInfoDTO();
                dto.setPartNo(PARTNO);
                dto.setGlCode(glCode);
                dto.setPartName(PARTNAME);
                dto.setVersion(VERSION);
                dto.setStatus(PART_STATUS);
                dto.setDesign(DESIGN_USE);
                dto.setCost(COST_USE);
                dto.setBlockNo(BLOCKNO_NUMBER);
                dto.setSpec(SPEC);
                dto.setUom(UOM);
                dto.setOriginDiv(ORIGIN_DIV);
                dto.setPartSize(PARTSIZE);
                dto.setOriginDiv(ORIGIN_DIV);

                result.add(dto);

            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    /**
     * 많이 생성된 TOP10 자재 조회
     * @return
     */
    public static ArrayList<HashMap<String,String>> findTopBlockPart() {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<HashMap<String,String>> result = new ArrayList<HashMap<String,String>>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                   SELECT
                    A.BLOCKNO_NUMBER AS BLOCKNO,
                    (SELECT S.MD$DESC FROM BLOCKNO$SF S WHERE S.MD$NUMBER = A.BLOCKNO_NUMBER ) AS BLOCKNAME, 
                    COUNT(A.BLOCKNO_NUMBER) AS BLOCKNO_COUNT
                    FROM normalpart$vf A JOIN normalpart$id B
                    ON A.vf$ouid = B.id$last
                    WHERE A.NATION != '2803457356' --중국법인 제외
                        AND A.PART_STATUS = '2466425004' --활성
                        AND A.MD$STATUS = 'RLS' --릴리즈
                        AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                        AND SUBSTR(A.MD$MDATE, 0,4) IN('2025', '2024')
                    GROUP BY A.BLOCKNO_NUMBER
                    ORDER BY BLOCKNO_COUNT DESC
                """;

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, productOID);

            System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String BLOCKNO = rs.getString("BLOCKNO"); //제품번호
                String BLOCKNO_COUNT = rs.getString("BLOCKNO_COUNT");
                String BLOCKNAME = rs.getString("BLOCKNAME");

                HashMap<String,String> oMap = new HashMap<>();
                oMap.put("BLOCKNO", BLOCKNO);
                oMap.put("BLOCKNO_COUNT", BLOCKNO_COUNT);
                oMap.put("BLOCKNAME", BLOCKNAME);

                result.add(oMap);

            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }
}
