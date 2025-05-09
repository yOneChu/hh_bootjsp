package com.kyhslam.util;

import com.kyhslam.dto.PartInfoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class InventorCommonUtil {


    /**
     * 설계복사화면에서 호기 입력 시 제품의 버전, 상태 조회
     *
     * @param productNo
     * @return
     */
    public static HashMap<String, String> findProductInfo(String productNo) {


        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap<String, String> result = new HashMap<String, String>();


        try {

            con = PLMDBConnection.getConnection();
            StringBuffer sql = new StringBuffer();

            sql.append(" with ouid as ");
            sql.append(" ( select A.vf$ouid from product$vf A, product$id B ");
            sql.append("  	where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip ");
            sql.append("  	and A.md$number in ( ? )	");
            //sql.append("  	and A.md$number in ( '" + productNo + "')	");
            sql.append(" ) ");
            sql.append(" SELECT b.md$Number AS HOGI, ");
            sql.append(" b.VF$VERSION AS VERSION,	");
            sql.append(" b.MD$STATUS AS STATUS	");
            sql.append(" FROM  product$vf b ");
            sql.append(" WHERE b.vf$ouid = (select * from ouid)  ");

            System.out.println(sql.toString());
            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, productNo);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String hogi = rs.getString("HOGI");
                String version = rs.getString("VERSION");
                String status = rs.getString("STATUS") == null ? "" : rs.getString("STATUS");

                result.put("HOGI", hogi);
                result.put("VERSION", version);
                result.put("STATUS", status);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    /**
     * 제품의 하위 BOM 중 수정된 자재만 조회
     * @param productNo
     * @return
     */
    public static ArrayList<PartInfoDTO> findModParts(String productNo) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList<PartInfoDTO> result = new ArrayList<>();

        try {

            con = PLMDBConnection.getConnection();
            StringBuffer sql = new StringBuffer();

            sql.append(" with ouid as ");
            sql.append(" ( select A.vf$ouid AS VFOID from product$vf A, product$id B ");
            sql.append("  	where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip ");
            sql.append("  	and A.md$number in ( ? )	");
            sql.append(" ) ");

            sql.append(" SELECT ");
            sql.append(" (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) AS PARENTNO, ");
            sql.append(" NP.MD$NUMBER AS PARTNO, ");
            sql.append(" cod(NP.NATION) AS NATION, ");
            sql.append(" NP.MD$DESC AS PARTNAME, ");
            sql.append(" NP.VF$VERSION AS VERSION, ");
            sql.append(" NVL(NP.G_L_CODE, '') AS GLCODE, ");
            sql.append(" NVL(NP.SPEC, '') AS SPEC, ");
            sql.append(" NVL(NP.PART_SIZE, '') AS PART_SIZE, ");
            sql.append(" (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNO, ");
            sql.append(" PE.QTY AS QTY, ");
            sql.append(" NVL(COD(NP.UOM), '') AS UOM, ");
            sql.append(" PE.CMT AS CMT, ");
            sql.append(" VP.UCHECK AS UCHECK , ");
            sql.append(" NVL(COD(NP.ORIGIN_DIV), '') AS DIV ");

            sql.append(" FROM  PARTOFEBOM PE ");
            sql.append(" INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID  ");
            sql.append(" LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID ");
            sql.append(" WHERE PE.PRODUCTOUID = (SELECT VFOID FROM ouid) ");
            sql.append(" AND VP.UCHECK = '1' ");
            sql.append(" ORDER BY TO_NUMBER(PE.SEQ) ");


            System.out.println(sql.toString());
            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, productNo);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION") == null ? "" : rs.getString("VERSION");

                String GLCODE = rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE");
                String SPEC = rs.getString("SPEC") == null ? "" : rs.getString("SPEC");
                String BLOCKNO = rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");

                String QTY = rs.getString("QTY") == null ? "" : rs.getString("QTY");
                String UOM = rs.getString("UOM") == null ? "" : rs.getString("UOM");

                String CMT = rs.getString("CMT") == null ? "" : rs.getString("CMT");

                PartInfoDTO partInfoDTO = new PartInfoDTO();
                partInfoDTO.setPartNo(PARTNO);
                partInfoDTO.setPartName(PARTNAME);
                partInfoDTO.setVersion(VERSION);
                partInfoDTO.setGlCode(GLCODE);
                partInfoDTO.setSpec(SPEC);
                partInfoDTO.setBlockNo(BLOCKNO);
                partInfoDTO.setQty(QTY);
                partInfoDTO.setUom(UOM);
                partInfoDTO.setCmt(CMT);

                result.add(partInfoDTO);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;

    }
}
