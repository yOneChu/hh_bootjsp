package com.kyhslam.util.cop;

import com.kyhslam.util.VaultDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class CopUtil {


    // cop 배치를 수행할  오늘 호기 조회
    public static ArrayList<HashMap<String, String>> findHogi() {

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        // CONVERT(..., 112) 는 yyyyMMdd 형식이므로 같은 형식으로 비교
        String todayValue = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = VaultDBConnection.getConnection();

            String sql = """
                    SELECT A.HOGI, A.BRAND, A.ACAPA, A.SPEED
                    FROM PLM_SIMUL_HOGI A
                    WHERE 
                    CONVERT(VARCHAR(8), A.CREDATE, 112) = ?
                    """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, todayValue);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String HOGI = rs.getString("HOGI") == null ? "" : rs.getString("HOGI");
                String BRAND = rs.getString("BRAND") == null ? "" : rs.getString("BRAND");
                String SPEED = rs.getString("SPEED") == null ? "" : rs.getString("SPEED");
                String ACAPA = rs.getString("ACAPA") == null ? "" : rs.getString("ACAPA");

                HashMap<String, String> data = new HashMap<>();
                data.put("HOGI", HOGI);
                data.put("BRAND", BRAND);
                data.put("SPEED", SPEED);
                data.put("ACAPA", ACAPA);

                result.add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    // 동일정보로 생성된 호기 저장 (beforeHogi : 동일정보 원본 호기)
    public static void insertSimulHogi(String hogi, String beforeHogi, String brand, String speed, String acapa) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = VaultDBConnection.getConnection();

            String sql = """
                    INSERT INTO PLM_SIMUL_HOGI (HOGI, BEFOREHOGI, BRAND, SPEED, ACAPA, CREDATE)
                    VALUES (?, ?, ?, ?, ?, GETDATE())
                    """;

            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, hogi);
            pstmt.setString(2, beforeHogi);
            pstmt.setString(3, brand);
            pstmt.setString(4, speed);
            pstmt.setString(5, acapa);

            pstmt.executeUpdate();

            System.out.println("[PLM_SIMUL_HOGI 저장] hogi = " + hogi + ", beforeHogi = " + beforeHogi
                    + ", brand = " + brand + ", speed = " + speed + ", acapa = " + acapa);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
    }

}
