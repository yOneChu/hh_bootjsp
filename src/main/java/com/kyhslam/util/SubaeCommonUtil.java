package com.kyhslam.util;

import com.kyhslam.dto.PartInfoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SubaeCommonUtil {



    //PLM에서 중국부품 조회
    public static ArrayList<PartInfoDTO> findOneFromPartNo(PartInfoDTO param) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<PartInfoDTO> result = new ArrayList<PartInfoDTO>();

        try {

            //con = DBconnectionInfo.getPDM_DBConnection();
            con = PLMDBConnection.getConnection();

            String sql = """
                    SELECT A.md$number AS PARTNO,
                           A.md$desc AS PARTNAME,
                           A.BLOCKNO_NUMBER AS BLOCKNO, A.spec AS SPEC, A.g_l_code AS GL_CODE,
                           A.PART_SIZE,
                           A.nation,
                           CODN(A.NATION) AS NATION_KO, 
                           cod(A.uom) AS UOM,
                           CODN(A.origin_div) AS origin_div, --외주
                           cod(A.spt) AS spt,
                           --A.PART_STATUS,
                           --CODN(A.PART_STATUS) AS ,
                           CODN(A.part_status) part_status,
                           A.old_code, A.old_code2, A.old_code3, old_code4
                    FROM normalpart$vf A, normalpart$id B
                    WHERE A.vf$ouid = B.id$last
                      AND LENGTH (A.md$number)=11 AND A.NATION = 2803457356
                    """;

            boolean paramFlag = false;

            String pPartNo = param.getPartNo();
            String pBlockNo = param.getBlockNo();
            String pPartName = param.getPartName();
            String pSpec = param.getSpec();
            String div = param.getDiv();
            String status = param.getStatus();


            String whereXp = "";

            if (pPartNo != null && !"".equals(pPartNo)) {
                sql += "AND A.md$number like '%" + pPartNo + "%'";
                paramFlag = true;
            }

            if (pBlockNo != null && !"".equals(pBlockNo)) {
                sql += "AND A.BLOCKNO_NUMBER like '%" + pBlockNo + "%'";
                paramFlag = true;
            }

            if (pPartName != null && !"".equals(pPartName)) {
                sql += "AND A.md$desc like '%" + pPartName + "%'";
                paramFlag = true;
            }

            if (div != null && !"".equals(div)) {
                sql += "AND CODN(A.origin_div) like '%" + div + "%'";
            }

            if (status != null && !"".equals(status)) {
                sql += "AND CODN(A.part_status) like '%" + status + "%'";
            }



            System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, partNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String GLCODE = rs.getString("GL_CODE") == null ? "" : rs.getString("GL_CODE");
                String SPEC = rs.getString("SPEC") == null ? "" : rs.getString("SPEC");
                String PART_SIZE   = rs.getString("PART_SIZE") == null ? "" : rs.getString("PART_SIZE");

                String NATION_KO   = rs.getString("NATION_KO");
                String PART_STATUS   = rs.getString("PART_STATUS");
                String UOM   = rs.getString("UOM");

                String BLOCKNO   = rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");

                PartInfoDTO dto = new PartInfoDTO();
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setGlCode(GLCODE);
                dto.setSpec(SPEC);
                dto.setPartSize(PART_SIZE);
                dto.setUom(UOM);
                dto.setBlockNo(BLOCKNO);

                result.add(dto);



                //System.out.println(PARTNO + " - " + BLOCKNO + " - " + DESCVAL + " , " + GLCODE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }
}
