package com.kyhslam.util;

import java.sql.*;

public class VaultDBConnection {



    //VAULT DB Connection
    public static Connection getConnection() {

        Connection con 			= null;


        try {

            String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=PLMPRDIF;encrypt=false;";
            String id = "SA";
            String pw = "AutodeskVault@26200";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            con = DriverManager.getConnection(url,id,pw);

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
