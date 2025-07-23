package com.kyhslam.util;

import com.kyhslam.dto.PartInfoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MLBCommonUtil {


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
                String OID = rs.getString("OID"); //제품번호

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
    public static void findDownLevel(String oid, ArrayList<String> data) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<String> result = new ArrayList<>();

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
                    , BLOCKNO_NUMBER
                    , NP.MD$DESC
                    , NP.VF$VERSION 
                    , NP.G_L_CODE  
                    , NP.PART_SIZE
                    , NP.SPEC
                    , CODN(NP.SPT)
                    , CODN(NP.ORIGIN_DIV) -- 내작외작
                    , CODN(NP.REVISION)
                    , CODN(NP.PART_STATUS)
                    , CODN(NP.ACTIVE_YN)
                    , NP.MD$STATUS
                       , DATEFORMAT(BOM.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE
                    , DATEFORMAT(BOM.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_DATE
                    , DECODE(B1.MD$NUMBER, NULL, NULL, B1.MD$NUMBER || ' ' || B1.MD$DESC)
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
                String qty = rs.getString("QTY");

                if (qty.equals("E321A_39")) {
                    data.add(PARTNO);
                    System.out.println(PARTNO);
                }


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
