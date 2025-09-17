package com.kyhslam.mlb;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.PLMDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class find_PM_Request {


    // COMPEN CHAIN : B189A 자재 조회
    // COMPEN CHAIN 들어간 호기의 변경 이력

    public static void main(String[] args) {


        // 1.COMPEN CHAIN 자재 찾기

        ArrayList<PartInfoDTO> compenList = findPLMPartWithBlock("2025", "B189A");

        System.out.println("compenList = " + compenList.size());



        // 2.그 자재 사용중인 모든 버전의 제품 찾기




    }


    // B189A 전체년도 자재 조회
    public static ArrayList<PartInfoDTO> findPLMPartWithBlock(String year, String block) {
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
                                -- AND SUBSTR(A.MD$CDATE, 0, 4) IN( ? )
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
                  --AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                  AND A.PART_STATUS = '2466425004' -- 활성
                """;



            //block
            if (block != null && !"".equals(block)) {
                sql += " AND A.BLOCKNO_NUMBER = '" + block.trim().toUpperCase() + "'";
                //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
            }

            //sql += " AND A.MD$NUMBER LIKE '10111175G010%' ";



            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, year);
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
}
