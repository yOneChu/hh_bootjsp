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
                           A.BLOCKNO_NUMBER AS BLOCKNO, 
                           NVL((SELECT BLOCKNO$SF.MD$DESC FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(A.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(A.BLOCKNO, 12))))), '-') AS BLOCKNO_NAME,
                           A.spec AS SPEC, 
                           A.g_l_code AS GL_CODE,
                           A.PART_SIZE,
                           A.nation,
                           A.VF$VERSION AS VERSION,
                           CODN(A.NATION) AS NATION_KO, 
                           cod(A.uom) AS UOM,
                           CODN(A.origin_div) AS origin_div, --외주
                           cod(A.spt) AS spt,
                           A.MD$STATUS AS PART_STATUS,
                           --A.PART_STATUS,
                           --CODN(A.PART_STATUS)  ,
                           CODN(A.part_status) AS ACTIVE,
                           A.old_code, A.old_code2, A.old_code3, old_code4
                    FROM normalpart$vf A, normalpart$id B
                    WHERE A.vf$ouid = B.id$last
                      AND LENGTH (A.md$number)=11 AND A.NATION = 2803457356
                    """;

            String pPartNo = param.getPartNo();
            String pBlockNo = param.getBlockNo();
            String pPartName = param.getPartName();
            String pSpec = param.getSpec();
            String div = param.getDiv();
            String status = param.getStatus();

            if (pPartNo != null && !"".equals(pPartNo)) {
                sql += "AND A.md$number like '%" + pPartNo + "%'";
            }

            if (pBlockNo != null && !"".equals(pBlockNo)) {
                sql += " AND A.BLOCKNO_NUMBER like '%" + pBlockNo + "%'";
            }

            if (pPartName != null && !"".equals(pPartName)) {
                sql += " AND A.md$desc like '%" + pPartName + "%'";
            }

            if (div != null && !"".equals(div)) {
                sql += " AND CODN(A.origin_div) like '%" + div + "%'";
            }

            if (status != null && !"".equals(status)) {
                sql += " AND CODN(A.part_status) like '%" + status + "%'";
            }

            if (pSpec != null && !"".equals(pSpec)) {
                sql += " AND A.SPEC like '%" + pSpec + "%'";
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
                String ACTIVE   = rs.getString("ACTIVE");

                String BLOCKNO   = rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
                String BLOCKNO_NAME   = rs.getString("BLOCKNO_NAME") == null ? "" : rs.getString("BLOCKNO_NAME");

                PartInfoDTO dto = new PartInfoDTO();
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setGlCode(GLCODE);
                dto.setSpec(SPEC);
                dto.setPartSize(PART_SIZE);
                dto.setUom(UOM);
                dto.setBlockNo(BLOCKNO);
                dto.setBlockName(BLOCKNO_NAME);
                dto.setStatus(PART_STATUS);
                dto.setActive(ACTIVE);

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
