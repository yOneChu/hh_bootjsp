package com.kyhslam.util;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DTMUtil {

    public static void closeStatementSafely (Statement stat)
    {
        if (stat != null)
        {
            try
            {
                stat.close () ;
            }
            catch (Exception e)
            {
                // do nothing.
            }
            stat = null ;
        }
    }

    public static void closeConnectionSafely (Connection con)
    {
        if (con != null)
        {
            try
            {
                con.close () ;
            }
            catch (Exception e)
            {
                // do nothing.
            }
            con = null ;
        }
    }

    public static void closePooledConnectionSafely (PooledConnection xc)
    {
        if (xc != null)
        {
            try
            {
                xc.close () ;
            }
            catch (Exception e)
            {
                // do nothing.
            }
            xc = null ;
        }
    }

    public static void closeSafely (Statement stat, Connection con, PooledConnection xc)
    {
        if (stat != null)
        {
            try
            {
                stat.close () ;
            }
            catch (Exception e)
            {
                // do nothing.
            }
            stat = null ;
        }

        if (con != null)
        {
            try
            {
                con.close () ;
            }
            catch (Exception e)
            {
                // do nothing.
            }
            con = null ;
        }

        if (xc != null)
        {
            try
            {
                xc.close () ;
            }
            catch (Exception e)
            {
                // do nothing.
            }
            xc = null ;
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                // do nothing.
            }
            rs = null;
        }
    }

    public static void sqlExceptionHelper (Connection con, SQLException e) throws Exception {
        if (con != null) {
            try {
                con.rollback ();
            } catch (SQLException se) {
                // do nothing.
            }
        }
        if (e != null) {
            e.printStackTrace () ;
            throw new Exception (e.toString ()) ;
        }
    }

    /**
     * SQLException외의 Exception발생시 Connection RollBack후 Exception처리하는 Method
     * @param con 		 Connection
     * @param e           		Exception
     * @throws Exception
     */
    public static void commonExceptionHelper (Connection con, Exception e) throws Exception {
        if (con != null) {
            try {
                con.rollback ();
            } catch (SQLException se) {
                // do nothing.
            }
        }
        if (e != null) {
            e.printStackTrace ();
            if (e instanceof Exception) throw (Exception)e;
            else throw new Exception (e.toString ());
        }
    }
}
