package com.kyhslam.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * 부품공용화에 사용하는 공통함수 모음
 */
public class PartPublicCommonUtil {



    /**
     * partNo로 자재의 원가절감액 가져오기
     * @param partNo
     * @param partType
     * @return
     */
    public static int getPrice(String partNo, String partType) {
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;

        int partPrice = 0;

        try {
            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT A.PARTNO, A.PARTTYPE, A.PARTPRICE ");
            sql.append(" FROM PUBLICPART_LIST A ");
            sql.append(" WHERE A.PARTNO = ? ");
            //sql.append(" WHERE A.PARTNO = '28011629G10SA' ");

            //pstmt = con.prepareStatement(sql.toString());
            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1,partNo);
            //pstmt.setString(2,partType);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String getPartPrice = rs.getString("PARTPRICE") == null ? "" : rs.getString("PARTPRICE");
                if(getPartPrice != null && !"".equals(getPartPrice)) {
                    partPrice = Integer.parseInt(getPartPrice);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

        return partPrice;
    }


    /**
     * 출하예정일 금액 대시보드에서 OPB 정보 뽑기
     * @param todayVal
     * @param partType
     * @return
     */
    public static HashMap<String, Integer> getMonthValue(String todayVal, String partType) {
        System.out.println("getMonthValue == " + todayVal + " , " + partType);
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;

        HashMap<String, Integer> data = new HashMap<>();

        try {
            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" select A.batch_date, A.part_type, A.price202405, A.price202406, ");
            sql.append(" A.price202407, A.price202408, A.price202408, A.price202409, A.price202410, A.price202411, A.price202412, ");
            sql.append(" A.price202501, A.price202502, A.price202503, A.price202504, A.price202505, A.price202506, A.price202507, ");
            sql.append(" A.price202508, A.price202509, A.price202510, A.price202511, A.price202512, A.price_etc ");
            sql.append(" from dash_exportprice A ");
            sql.append(" WHERE A.batch_date = ? ");
            sql.append(" AND A.part_type = ? ");
            //sql.append(" WHERE A.PARTNO = '28011629G10SA' ");



            //pstmt = con.prepareStatement(sql.toString());
            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, todayVal);
            pstmt.setString(2, partType);

            //pstmt.setString(2,partType);

            //System.out.println("sql = " + sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {

                int m202405 = Integer.parseInt(rs.getString("price202405") == null ? "0" : rs.getString("price202405") );
                int m202406 = Integer.parseInt(rs.getString("price202406") == null ? "0" : rs.getString("price202406") );
                int m202407 = Integer.parseInt(rs.getString("price202407") == null ? "0" : rs.getString("price202407") );
                int m202408 = Integer.parseInt(rs.getString("price202408") == null ? "0" : rs.getString("price202408") );
                int m202409 = Integer.parseInt(rs.getString("price202409") == null ? "0" : rs.getString("price202409") );
                int m202410 = Integer.parseInt(rs.getString("price202410") == null ? "0" : rs.getString("price202410") );
                int m202411 = Integer.parseInt(rs.getString("price202411") == null ? "0" : rs.getString("price202411") );
                int m202412 = Integer.parseInt(rs.getString("price202412") == null ? "0" : rs.getString("price202412") );

                int m202501 = Integer.parseInt(rs.getString("price202501") == null ? "0" : rs.getString("price202501") );
                int m202502 = Integer.parseInt(rs.getString("price202502") == null ? "0" : rs.getString("price202502") );
                int m202503 = Integer.parseInt(rs.getString("price202503") == null ? "0" : rs.getString("price202503") );
                int m202504 = Integer.parseInt(rs.getString("price202504") == null ? "0" : rs.getString("price202504") );
                int m202505 = Integer.parseInt(rs.getString("price202505") == null ? "0" : rs.getString("price202505") );
                int m202506 = Integer.parseInt(rs.getString("price202506") == null ? "0" : rs.getString("price202506") );
                int m202507 = Integer.parseInt(rs.getString("price202507") == null ? "0" : rs.getString("price202507") );
                int m202508 = Integer.parseInt(rs.getString("price202508") == null ? "0" : rs.getString("price202508") );
                int m202509 = Integer.parseInt(rs.getString("price202509") == null ? "0" : rs.getString("price202509") );
                int m202510 = Integer.parseInt(rs.getString("price202510") == null ? "0" : rs.getString("price202510") );
                int m202511 = Integer.parseInt(rs.getString("price202511") == null ? "0" : rs.getString("price202511") );
                int m202512 = Integer.parseInt(rs.getString("price202512") == null ? "0" : rs.getString("price202512") );
                int etc = Integer.parseInt(rs.getString("price_etc") == null ? "0" : rs.getString("price_etc") );


                data.put("m202405", m202405);
                data.put("m202406", m202406);
                data.put("m202407", m202407);
                data.put("m202408", m202408);
                data.put("m202409", m202409);
                data.put("m202410", m202410);
                data.put("m202411", m202411);
                data.put("m202412", m202412);

                data.put("m202501", m202501);
                data.put("m202502", m202502);
                data.put("m202503", m202503);
                data.put("m202504", m202504);
                data.put("m202505", m202505);
                data.put("m202506", m202506);
                data.put("m202507", m202507);
                data.put("m202508", m202508);
                data.put("m202509", m202509);
                data.put("m202510", m202510);
                data.put("m202511", m202511);
                data.put("m202512", m202512);
                data.put("etc", etc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
        //System.out.println("eeeee -- " + data);
        return data;
    }
}
