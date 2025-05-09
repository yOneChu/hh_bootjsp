package com.kyhslam.util;

import com.kyhslam.dto.DesignRequestDTO;
import com.kyhslam.dto.ELVInfoDashDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class ElvInfoCommonUtil {

    public static ArrayList<String> getTypeList() {
        ArrayList<String> result = new ArrayList<>();

        result.add("GTLX_R");
        result.add("WBHS_(HSVF)");
        result.add("LXVF1");
        result.add("WBSS2_(SSVF)");
        result.add("SSVF7");
        result.add("GTSS_E");
        result.add("WBST_SE");
        result.add("WBST1");
        result.add("WBLX1_(LXVF)");
        result.add("WBSS_SE");
        result.add("GTLX_E");
        result.add("WBSS");
        result.add("HSVF");
        result.add("GTSS_R");
        result.add("LXVF");
        result.add("STVF7");
        result.add("LXVF7");
        result.add("WBHS_(SSVF)");
        result.add("STVF5");
        result.add("WBST1_(STVF)");
        result.add("SSVF");
        result.add("STVF");
        result.add("GTSS");
        result.add("GTLX");
        result.add("WBLX_SE");
        result.add("SUVF");
        result.add("WBSS1_(SSVF)");

        return result;
    }


    //집계된거 대시보드 조회
    public static ArrayList<ELVInfoDashDTO> getDashbard() {
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        ArrayList<ELVInfoDashDTO> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;
        try {

            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT  ");
            sql.append(" A.batch_date, A.elv_type, ");
            sql.append(" A.total_cnt, ");
            sql.append(" A.dis202401, A.dis202402, A.dis202403, A.dis202404, A.dis202405, A.dis202406, ");
            sql.append(" A.dis202407, A.dis202408, A.dis202409, A.dis202410, A.dis202411, A.dis202412 ");

            //todayValue
            sql.append(" FROM elvinfo_dash A ");
            //sql.append(" WHERE A.BATCH_DATE = ? ");



            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, todayValue);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String batch_date = rs.getString("batch_date") == null ? "" : rs.getString("batch_date");
                String elv_type = rs.getString("elv_type") == null ? "" : rs.getString("elv_type");
                String total_cnt = rs.getString("total_cnt") == null ? "" : rs.getString("total_cnt");

                String dis202401 = rs.getString("dis202401") == null ? "" : rs.getString("dis202401");
                String dis202402 = rs.getString("dis202402") == null ? "" : rs.getString("dis202402");
                String dis202403 = rs.getString("dis202403") == null ? "" : rs.getString("dis202403");
                String dis202404 = rs.getString("dis202404") == null ? "" : rs.getString("dis202404");
                String dis202405 = rs.getString("dis202404") == null ? "" : rs.getString("dis202405");
                String dis202406 = rs.getString("dis202406") == null ? "" : rs.getString("dis202406");
                String dis202407 = rs.getString("dis202407") == null ? "" : rs.getString("dis202407");
                String dis202408 = rs.getString("dis202408") == null ? "" : rs.getString("dis202408");
                String dis202409 = rs.getString("dis202409") == null ? "" : rs.getString("dis202409");
                String dis202410 = rs.getString("dis202410") == null ? "" : rs.getString("dis202410");
                String dis202411 = rs.getString("dis202411") == null ? "" : rs.getString("dis202411");
                String dis202412 = rs.getString("dis202412") == null ? "" : rs.getString("dis202412");




                ELVInfoDashDTO dto = new ELVInfoDashDTO();
                dto.setBatchDate(batch_date);
                dto.setElvType(elv_type);
                dto.setTotalCnt(total_cnt);
                dto.setDis202401(dis202401);
                dto.setDis202402(dis202402);
                dto.setDis202403(dis202403);
                dto.setDis202404(dis202404);
                dto.setDis202405(dis202405);
                dto.setDis202406(dis202406);
                dto.setDis202407(dis202407);
                dto.setDis202408(dis202408);
                dto.setDis202409(dis202409);
                dto.setDis202410(dis202410);
                dto.setDis202411(dis202411);
                dto.setDis202412(dis202412);



                result.add(dto);

            } // end while



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    public static HashMap<String, String> getMonthDashboard(String type) {

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> data = new HashMap<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            con = VaultDBConnection.getConnection();

            //con = DriverManager.getConnection(url, id, pass);

            System.out.println("con = " + con);


            StringBuffer sql = new StringBuffer();

            sql.append(" SELECT    ");

            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202401' AND A.atyp = ?) AS C202401,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202402' AND A.atyp = ?) AS C202402,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202403' AND A.atyp = ?) AS C202403,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202404' AND A.atyp = ?) AS C202404,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202405' AND A.atyp = ?) AS C202405,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202406' AND A.atyp = ?) AS C202406,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202407' AND A.atyp = ?) AS C202407,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202408' AND A.atyp = ?) AS C202408,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202409' AND A.atyp = ?) AS C202409,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202410' AND A.atyp = ?) AS C202410,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202411' AND A.atyp = ?) AS C202411,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202412' AND A.atyp = ?) AS C202412   ");


            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, type);
            pstmt.setString(2, type);
            pstmt.setString(3, type);
            pstmt.setString(4, type);
            pstmt.setString(5, type);
            pstmt.setString(6, type);
            pstmt.setString(7, type);
            pstmt.setString(8, type);
            pstmt.setString(9, type);
            pstmt.setString(10, type);
            pstmt.setString(11, type);
            pstmt.setString(12, type);

            rs = pstmt.executeQuery();

            int totalCnt = 0;
            while (rs.next()) {

                String C202401 = rs.getString("C202401") == null ? "" : rs.getString("C202401");
                String C202402 = rs.getString("C202402") == null ? "" : rs.getString("C202402");
                String C202403 = rs.getString("C202403") == null ? "" : rs.getString("C202403");
                String C202404 = rs.getString("C202404") == null ? "" : rs.getString("C202404");
                String C202405 = rs.getString("C202405") == null ? "" : rs.getString("C202405");
                String C202406 = rs.getString("C202406") == null ? "" : rs.getString("C202406");
                String C202407 = rs.getString("C202407") == null ? "" : rs.getString("C202407");
                String C202408 = rs.getString("C202408") == null ? "" : rs.getString("C202408");
                String C202409 = rs.getString("C202409") == null ? "" : rs.getString("C202409");
                String C202410 = rs.getString("C202410") == null ? "" : rs.getString("C202410");
                String C202411 = rs.getString("C202411") == null ? "" : rs.getString("C202411");
                String C202412 = rs.getString("C202412") == null ? "" : rs.getString("C202412");

                //HashMap<String, String> data = new HashMap<>();

                data.put("202401", C202401);
                data.put("202402", C202402);
                data.put("202403", C202403);
                data.put("202404", C202404);
                data.put("202405", C202405);
                data.put("202406", C202406);
                data.put("202407", C202407);
                data.put("202408", C202408);
                data.put("202409", C202409);
                data.put("202410", C202410);
                data.put("202411", C202411);
                data.put("202412", C202412);


            }

            System.out.println("data = " + data);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

        return data;
    }
}
