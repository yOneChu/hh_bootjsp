package com.kyhslam.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PartDashboardUtil {


    /**
     * PLM에 등록된 자재 조회
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
                    with ouid as
                        ( select A.vf$ouid from NORMALPART$vf A, NORMALPART$id B
                          where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
                        )
                    SELECT 
                            COUNT(A.MD$NUMBER) AS ALLCNT
                    FROM NORMALPART$VF A
                    WHERE A.VF$OUID IN (SELECT * FROM OUID)
                    AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                """;

            //System.out.println("sql = " + sql);

            //활성
            if (flag != null && "ACTIVE".equals(flag)) {
                sql += " AND A.PART_STATUS = '2466425004' ";
                //sql += " AND A.PART_STATUS = '" + productOID + "' ";
            }

            //비활성
            if (flag != null && "INACTIVE".equals(flag)) {
                sql += " AND A.PART_STATUS = '2466425005' ";
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
}
