package com.kyhslam.service;

import com.kyhslam.util.PIDCommonUtil;
import com.kyhslam.util.PLMDBConnection;
import com.kyhslam.util.VaultDBConnection;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class PIDService {


    //월~금 18시20분 수행
    @Scheduled(cron = "0 20 18 * * *")
    public void scheduleProcess() {

        PIDCommonUtil p = new PIDCommonUtil();

        //PID 총 라인수
        p.insert_Type01();

        //PID 개수
        p.insert_Type02();

        //PID에 연결된 각 라인 수
        //p.insert_Type03();

        System.out.println(" ------- end -------");

    }

    // 쿼리수정해서 PLM에서 실시간 조회로 수정
    //금 18시40분 수행
    //@Scheduled(cron = "0 40 18 * * 5")
    public void scheduleProcessV2() {

        PIDCommonUtil p = new PIDCommonUtil();

        //PID에 연결된 각각의 라인 수 저장
        //p.insert_Type03();

        System.out.println(" ------- end -------");
    }

    //PID_TYPE01
    //PID 총 라인수
    public ArrayList<HashMap<String, String>> findType01() {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;
        try {

            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT BATCH_DATE, PID_COUNT  ");
            sql.append(" FROM PID_TYPE01 ");
            sql.append(" order by BATCH_DATE DESC ");

            pstmt = con.prepareStatement(sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String BATCH_DATE = rs.getString("BATCH_DATE") == null ? "" : rs.getString("BATCH_DATE");
                String PID_COUNT = rs.getString("PID_COUNT") == null ? "" : rs.getString("PID_COUNT");

                HashMap<String, String> d = new HashMap<>();
                d.put("DATE", BATCH_DATE);
                d.put("COUNT", PID_COUNT);

                System.out.println(BATCH_DATE + " > " + PID_COUNT);

                result.add(d);
            } // end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    //PID 개수
    public ArrayList<HashMap<String, String>> findType02() {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;
        try {

            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT BATCH_DATE, PID_ALL_COUNT  ");
            sql.append(" FROM PID_TYPE02 ");
            sql.append(" order by BATCH_DATE ASC ");

            pstmt = con.prepareStatement(sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String BATCH_DATE = rs.getString("BATCH_DATE") == null ? "" : rs.getString("BATCH_DATE");
                String PID_COUNT = rs.getString("PID_ALL_COUNT") == null ? "" : rs.getString("PID_ALL_COUNT");

                HashMap<String, String> d = new HashMap<>();
                d.put("DATE", BATCH_DATE);
                d.put("COUNT", PID_COUNT);

                result.add(d);
            } // end while
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    //PID에 연결된 각 라인 수
    public ArrayList<HashMap<String, String>> findType03() {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;
        try {

            //con = VaultDBConnection.getConnection();
            con = PLMDBConnection.getConnection();

            /*String sql = """
                    WITH RankedPID AS (
                                SELECT
                                BATCH_DATE,
                                PID,
                                PID_COUNT,
                                ROW_NUMBER() OVER (PARTITION BY PID ORDER BY BATCH_DATE DESC) AS rn
                                FROM PID_TYPE03
                        )
                        SELECT TOP 200 BATCH_DATE, PID, PID_COUNT
                        FROM RankedPID
                        WHERE rn = 1
                        ORDER BY PID_COUNT DESC
                    """;*/

            String sql = """
                    SELECT
                        PID,
                        line_count AS PID_COUNT
                    FROM (
                        SELECT
                            h.PID,
                            COUNT(*) AS line_count
                        FROM
                            variant_d d
                        JOIN
                            variant_h h ON h.HOUID = d.HOUID
                        JOIN
                            variant_id id ON h.HOUID = id.LAST_HOUID
                        GROUP BY
                            h.PID
                        ORDER BY
                            COUNT(*) DESC
                    )
                    WHERE ROWNUM <= 50
                    """;


            pstmt = con.prepareStatement(sql.toString());

            rs = pstmt.executeQuery();

            LocalDate now = LocalDate.now();
            String todayValue = now.toString();

            while(rs.next()) {
                //String BATCH_DATE = rs.getString("BATCH_DATE") == null ? "" : rs.getString("BATCH_DATE");
                String PID = rs.getString("PID") == null ? "" : rs.getString("PID");
                String PID_COUNT = rs.getString("PID_COUNT") == null ? "" : rs.getString("PID_COUNT");

                HashMap<String, String> d = new HashMap<>();


                //d.put("DATE", BATCH_DATE);
                d.put("DATE", todayValue);
                d.put("PID", PID);
                d.put("COUNT", PID_COUNT);

                result.add(d);
            } // end while
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }
}
