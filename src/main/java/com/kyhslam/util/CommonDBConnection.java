package com.kyhslam.util;

import java.sql.*;

public class CommonDBConnection {

    //공통DB
    public static Connection getConnection() {

        Connection con 			= null;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@HDEL-SRM-PROD-DB-A.CU93NQJHPJCN.AP-NORTHEAST-2.RDS.AMAZONAWS.COM:1521/ORCL";
            String id = "COMPRD";
            String pass = "COMPRD01";

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
