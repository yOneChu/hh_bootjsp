package com.kyhslam.util;

import java.sql.*;

public class PLMDBConnection {

    public static Connection getConnection() {

        Connection con 			= null;


        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@newplm-prod-db.cdqjoonblwol.ap-northeast-2.rds.amazonaws.com:1521/PLMPRD";
            String id = "hdel_default";
            String pass = "hdel_default";

            con = DriverManager.getConnection(url, id, pass);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }


    public static void disconnect(Connection con, PreparedStatement pstmt, ResultSet rs) {

        try {
            if (rs != null)
                rs.close();
        }
        catch (SQLException ex) {}

        try {
            if (pstmt != null)
                pstmt.close();
        }
        catch (SQLException ex) {}

        try {
            if (con != null)
                con.close();
        }
        catch (SQLException ex) {}
    }
}
