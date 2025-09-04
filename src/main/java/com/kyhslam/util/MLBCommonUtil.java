package com.kyhslam.util;

import com.kyhslam.dto.PartInfoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MLBCommonUtil {


    /**
     * PartNo로 부품 OID 조회
     * @param partNo
     * @return
     */
    public static ArrayList<PartInfoDTO> findPartWithPartNo(String partNo) {
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
                        --AND SUBSTR(A.MD$CDATE, 0, 8) IN( ? )
                     )
                SELECT
                A.VF$OUID AS OID,
                A.MD$NUMBER AS PARTNO,
                A.MD$DESC AS PARTNAME,
                A.G_L_CODE AS GL_CODE,
                --A.MD$CDATE,
                --DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                CODN(A.PART_STATUS) AS PART_STATUS,
                COD(A.UOM) AS UOM,
                A.VF$VERSION AS VERSION,
                CODN(A.NATION) AS NATION,
                COD(A.DESIGN_USE) AS DESIGN_USE,
                COD(A.COST_USE) AS COST_USE,
                CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                A.BLOCKNO_NUMBER AS BLOCKNO,
                A.SPEC AS SPEC,
                A.PART_SIZE AS PARTSIZE
                --A.*
                FROM NORMALPART$VF A
                WHERE A.VF$OUID IN (SELECT * FROM OUID)
                AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                AND A.PART_STATUS = '2466425004'
                AND A.MD$NUMBER = ?
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo);
            //pstmt.setString(2, partName);
            //pstmt.setString(1, productOID);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String BLOCKNO = rs.getString("BLOCKNO");
                String SPEC = rs.getString("SPEC");
                String PARTSIZE = rs.getString("PARTSIZE");

                PartInfoDTO dto  = new PartInfoDTO();
                dto.setOid(OID);
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setDesign(PARTSIZE);
                dto.setBlockNo(BLOCKNO);
                dto.setSpec(SPEC);

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
     * @apiNote 년도로 등록된 활성 부품 oid 조회
     * @param year
     * @return
     */
    public static ArrayList<String> findPartWithYear(String year) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<String> result = new ArrayList<>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                  with ouid as
                     ( select A.vf$ouid from NORMALPART$vf A, NORMALPART$id B
                       where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
                       --and ( md$number in ( '18900360G0700') )
                        AND SUBSTR(A.MD$CDATE, 0, 8) IN( ? )
                     )
                SELECT
                A.VF$OUID AS OID,
                A.MD$NUMBER AS PARTNO,
                A.MD$DESC AS PARTNAME,
                A.G_L_CODE AS GL_CODE,
                --A.MD$CDATE,
                --DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
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
                --A.*
                FROM NORMALPART$VF A
                WHERE A.VF$OUID IN (SELECT * FROM OUID)
                AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                AND A.PART_STATUS = '2466425004'
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, year);
            //pstmt.setString(2, partName);
            //pstmt.setString(1, productOID);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");

                result.add(OID);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    //최신 부품 OID 조회
    public static ArrayList<String> searchPartOids(String year, String partName) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<String> result = new ArrayList<>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                  select A.vf$ouid AS OID from NORMALPART$vf A, NORMALPART$id B
                  where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
                   AND SUBSTR(A.MD$CDATE, 0, 4) IN( ? )
                   AND A.MD$DESC = 'CAR WALL ASSY'
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, year);
            //pstmt.setString(2, partName);
            //pstmt.setString(1, productOID);

            //System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");

                result.add(OID);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }



    //하위 조회
    public static void findDownLevel(String oid, ArrayList<PartInfoDTO> downPartList, PartInfoDTO parentDto) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<String> result = new ArrayList<>();


        String parentNo = parentDto.getPartNo();
        String parentPartName =  parentDto.getPartName();
        String parentSpec = parentDto.getSpec();
        String parentBlockNo = parentDto.getBlockNo();
        String parentGLCode = parentDto.getGlCode();



        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                  SELECT
                    TO_CHAR(LEVEL) AS P_LEVEL,
                       'partofpart$ac@' || lower(dectohex(BOM.SF$OUID)) AS OOID,
                       NP.MD$NUMBER AS PARTNO
                    , BOM.QTY AS QTY
                    , BOM.CMT
                    , BOM.PART_SPT
                    , CODN(NP.NATION)
                    , NP.COMPEN_PART
                    , DECODE(BOM.SERVICEFLAG, 'T', BOM.SERVICEFLAG, NULL)
                    , BOM.MD$SEQUENCE
                    , CODN(NP.UOM) UOM -- 단위
                    , CODN(NP.CLASSIFICATION_01)
                    , BLOCKNO_NUMBER AS BLOCKNO
                    , NP.MD$DESC AS PARTNAME
                    , NP.VF$VERSION 
                    , NP.G_L_CODE  
                    , NP.PART_SIZE AS PART_SIZE
                    , NP.SPEC AS SPEC
                    , CODN(NP.SPT)
                    , CODN(NP.ORIGIN_DIV) -- 내작외작
                    , CODN(NP.REVISION)
                    , CODN(NP.PART_STATUS)
                    , CODN(NP.ACTIVE_YN)
                    , NP.MD$STATUS
                       , DATEFORMAT(BOM.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE
                    , DATEFORMAT(BOM.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_DATE
                    , DECODE(B1.MD$NUMBER, NULL, NULL, B1.MD$NUMBER || ' ' || B1.MD$DESC) AS PARTNAMEV2
                    , DECODE(B2.MD$NUMBER, NULL, NULL, B2.MD$NUMBER || ' ' || B2.MD$DESC)
                    , DECODE(CAD.MD$NUMBER, NULL, NULL, CAD.MD$NUMBER || ' ' || CAD.MD$DESC)
                    , DECODE(NAME.MD$NUMBER, NULL, NULL, NAME.MD$NUMBER || ' ' || NAME.MD$DESC)
                    , U.MD$DESC
                    , U2.MD$DESC
                 FROM
                    PARTOFPART$AC BOM
                    INNER JOIN NORMALPART$VF NP ON 	BOM.AS$END2 = NP.VF$OUID
                 LEFT OUTER JOIN BLOCKNO$SF B1 ON B1.SF$OUID = GETID(NP.BLOCKNO)
                 LEFT OUTER JOIN BLOCKNO$SF B2 ON B2.SF$OUID = GETID(NP.UPPERBLOCKNO)
                 LEFT OUTER JOIN AUTOCAD_FILE$VF CAD ON CAD.VF$OUID = GETID(NP.DRAWING_NO)
                 LEFT OUTER JOIN FUSER$SF U ON U.MD$NUMBER = BOM.CUSER
                 LEFT OUTER JOIN FUSER$SF U2 ON U2.MD$NUMBER = NP.MD$USER
                 LEFT OUTER JOIN PARTNAME$SF NAME ON NAME.SF$OUID = GETID(NP.PARTNAME)
                 START WITH AS$END1 = ? -- 부품OID(VF$OUID)
                 CONNECT BY
                    PRIOR AS$END2 = AS$END1
                    ORDER SIBLINGS BY CAST(BOM.MD$SEQUENCE AS NUMBER DEFAULT 0 ON CONVERSION ERROR)
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, oid);
            //pstmt.setString(1, productOID);

            //System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                //String OID = rs.getString("OID"); //제품번호

                String PARTNO =  rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");
                String qty = rs.getString("QTY");
                String BLOCKNO = rs.getString("BLOCKNO");

                PartInfoDTO dto =  new PartInfoDTO();

                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setBlockNo(BLOCKNO);
                dto.setSpec(SPEC);
                dto.setPartSize(PART_SIZE);
                dto.setQty(qty);

                // BLOCKNO
                dto.setParentPartNo(parentNo);
                dto.setParentPartName(parentPartName);
                dto.setParentGLCode(parentGLCode);
                dto.setParentSpec(parentSpec);
                dto.setParentBlockNo(parentBlockNo);

                //D375A_LPSIZE
                //E331A_CE_002
                if (qty.equals("D375A_LPSIZE")) {
                    //data.add(PARTNO);
                    //System.out.println(PARTNO);
                }

                downPartList.add(dto);

                //result.add(OID);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
//        return result;
    }
}
