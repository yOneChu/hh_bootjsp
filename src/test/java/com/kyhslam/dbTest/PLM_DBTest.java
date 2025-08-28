package com.kyhslam.dbTest;

import com.kyhslam.util.PLMDBConnection;

import java.sql.Connection;

public class PLM_DBTest {

    public static void main(String[] args) {

        Connection con = null;

        try {

            con = PLMDBConnection.getConnection();
            System.out.println("con = " + con);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, null, null);
        }

    }
}
