package com.kyhslam.util.cop;

import com.kyhslam.dto.BomDTO;
import com.kyhslam.util.VaultDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CopUtil {


    // cop 배치를 수행할  오늘 호기 조회
    public static ArrayList<HashMap<String, String>> findHogi() {

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        // CONVERT(..., 112) 는 yyyyMMdd 형식이므로 같은 형식으로 비교
        String todayValue = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        todayValue = "20261030";

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


    // 호기의 1레벨 BOM 저장. 기존 BOM 을 지우고 다시 넣는다.
    // 한 건이라도 실패하면 삭제까지 모두 되돌린다. 저장된 건수를 돌려준다
    public static int replaceHogiBom(String hogi, List<BomDTO> bomList, String batchDate) throws Exception {

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = connect();
            con.setAutoCommit(false);

            int deleted;
            try (PreparedStatement delete = con.prepareStatement("DELETE FROM PLM_SIMUL_HOGIBOM WHERE HOGI = ?")) {
                delete.setString(1, hogi);
                deleted = delete.executeUpdate();
            }

            String sql = """
                    INSERT INTO PLM_SIMUL_HOGIBOM
                        (HOGI, PARTNO, PARTNAME, QTY, BLOCKNO, BLOCK_OPT, UCHECK, CMT, UOM, CREDATE, BATCHDATE)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)
                    """;

            pstmt = con.prepareStatement(sql);

            for (BomDTO bom : bomList) {
                pstmt.setString(1, hogi);
                pstmt.setString(2, bom.getPartNo());
                pstmt.setNString(3, bom.getPartName());
                pstmt.setNString(4, bom.getQty());
                pstmt.setNString(5, bom.getBlockNo());
                pstmt.setNString(6, bom.getBlockopt());
                pstmt.setNString(7, bom.getUcheck());
                pstmt.setNString(8, bom.getCmt());
                pstmt.setNString(9, bom.getUom());
                pstmt.setNString(10, batchDate);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            con.commit();

            System.out.println("[PLM_SIMUL_HOGIBOM 저장] hogi = " + hogi + ", 삭제 = " + deleted
                    + ", 저장 = " + bomList.size() + ", batchDate = " + batchDate);
            return bomList.size();

        } catch (Exception e) {
            if (con != null) {
                con.rollback();
            }
            throw e;
        } finally {
            VaultDBConnection.disconnect(con, pstmt, null);
        }
    }


    // 일자별 수량 계산 결과 이력 저장 (화면 : 일자별 수량 계산 비교 > 이력 저장)
    // 같은 호기 / 블럭 / BATCHDATE 의 기존 이력은 지우고 다시 넣는다 (다른 일자 이력은 그대로 쌓인다).
    // 한 건이라도 실패하면 모두 되돌린다. 저장된 건수를 돌려준다
    public static int saveHogiBomHistory(String hogi, String blockNo,
                                         List<com.kyhslam.service.simulate.PIDBlockQtyCompareService.HistoryColumn> cols) throws Exception {

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = connect();
            checkHistoryLength(con, hogi, blockNo, cols);
            con.setAutoCommit(false);

            // HOGI + BLOCKNO + BATCHDATE 는 한 벌만 유지 : 기존 이력을 지우고 다시 넣는다.
            // HOLDLOCK : 커밋까지 해당 키 범위를 잠가, 동시에 저장해도 같은 키로 두 벌이 쌓이지 않게 한다
            try (PreparedStatement delete = con.prepareStatement(
                    "DELETE FROM PLM_SIMUL_HOGIBOM WITH (HOLDLOCK) WHERE HOGI = ? AND BLOCKNO = ? AND BATCHDATE = ?")) {
                for (String batchDate : cols.stream().map(c -> c.batchDate).distinct().toList()) {
                    delete.setString(1, hogi);
                    delete.setNString(2, blockNo);
                    delete.setNString(3, batchDate);
                    delete.addBatch();
                }
                delete.executeBatch();
            }

            String sql = """
                    INSERT INTO PLM_SIMUL_HOGIBOM
                        (HOGI, BLOCKNO, BRAND, SPEED, ACAPA, PICK, PARTNO, QTY, CMT, CREDATE, BATCHDATE)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)
                    """;

            pstmt = con.prepareStatement(sql);

            int count = 0;
            for (var col : cols) {
                for (var item : col.items) {
                    pstmt.setString(1, hogi);
                    pstmt.setNString(2, blockNo);
                    pstmt.setNString(3, col.brand);
                    pstmt.setNString(4, col.speed);
                    pstmt.setNString(5, col.acapa);
                    pstmt.setNString(6, item.pick);
                    pstmt.setString(7, item.partNo);
                    pstmt.setNString(8, item.qty);
                    pstmt.setNString(9, item.cmt);
                    pstmt.setNString(10, col.batchDate);
                    pstmt.addBatch();
                    count++;
                }
            }

            pstmt.executeBatch();
            con.commit();

            System.out.println("[PLM_SIMUL_HOGIBOM 이력 저장] hogi = " + hogi + ", blockNo = " + blockNo
                    + ", 일자 = " + cols.size() + ", 저장 = " + count);
            return count;

        } catch (Exception e) {
            if (con != null) {
                con.rollback();
            }
            throw e;
        } finally {
            VaultDBConnection.disconnect(con, pstmt, null);
        }
    }


    // 이력 저장 전 컬럼 길이 검사 : 넘치는 값이 있으면 "문자열이나 이진 데이터는 잘립니다" 대신 컬럼 / 값 / 길이를 알려준다
    private static void checkHistoryLength(Connection con, String hogi, String blockNo,
                                           List<com.kyhslam.service.simulate.PIDBlockQtyCompareService.HistoryColumn> cols) throws SQLException {
        Map<String, Integer> size = new HashMap<>();
        try (ResultSet rs = con.getMetaData().getColumns(null, null, "PLM_SIMUL_HOGIBOM", null)) {
            while (rs.next())
                size.put(rs.getString("COLUMN_NAME").toUpperCase(), rs.getInt("COLUMN_SIZE"));
        }
        if (size.isEmpty())
            return;   // 메타데이터를 못 읽으면 DB 오류에 맡긴다

        List<String> over = new ArrayList<>();
        java.util.function.BiConsumer<String, String> check = (col, v) -> {
            Integer max = size.get(col);
            if (v != null && max != null && max > 0 && v.length() > max) {
                String msg = col + "(최대 " + max + "자) ← " + v.length() + "자 : " + (v.length() > 40 ? v.substring(0, 40) + "…" : v);
                if (!over.contains(msg) && over.size() < 5)
                    over.add(msg);
            }
        };
        check.accept("HOGI", hogi);
        check.accept("BLOCKNO", blockNo);
        for (var col : cols) {
            check.accept("BATCHDATE", col.batchDate);
            check.accept("BRAND", col.brand);
            check.accept("SPEED", col.speed);
            check.accept("ACAPA", col.acapa);
            for (var item : col.items) {
                check.accept("PICK", item.pick);
                check.accept("PARTNO", item.partNo);
                check.accept("QTY", item.qty);
                check.accept("CMT", item.cmt);
            }
        }
        if (!over.isEmpty()) {
            System.out.println("[PLM_SIMUL_HOGIBOM 이력 저장] 컬럼 길이 초과 : " + over);
            throw new IllegalArgumentException("컬럼 길이보다 긴 값이 있어 저장하지 못했습니다. " + String.join(" / ", over));
        }
    }


    // 관리대상 호기 전체 조회 (관리 화면에서 실패를 알 수 있도록 예외를 던진다)
    public static ArrayList<HashMap<String, String>> findManagedHogi() throws Exception {

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = connect();

            String sql = """
                    SELECT HOGI, BEFOREHOGI, BRAND, ACAPA, SPEED, BATCHDATE,
                           CONVERT(VARCHAR(19), CREDATE, 120) AS CREDATE
                    FROM PLM_SIMUL_HOGI
                    ORDER BY CREDATE DESC, HOGI
                    """;

            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                HashMap<String, String> data = new HashMap<>();
                for (String column : List.of("HOGI", "BEFOREHOGI", "BRAND", "ACAPA", "SPEED", "BATCHDATE", "CREDATE")) {
                    String value = rs.getString(column);
                    data.put(column, value == null ? "" : value);
                }
                result.add(data);
            }

        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    // 관리대상 호기 등록. 이미 있는 호기면 false (덮어쓰지 않는다)
    public static boolean insertManagedHogi(Map<String, String> hogi) throws Exception {

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = connect();

            String sql = """
                    INSERT INTO PLM_SIMUL_HOGI (BEFOREHOGI, BRAND, ACAPA, SPEED, BATCHDATE, HOGI, CREDATE)
                    VALUES (?, ?, ?, ?, ?, ?, GETDATE())
                    """;

            pstmt = con.prepareStatement(sql);
            bindHogi(pstmt, hogi);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // 2627 : PRIMARY KEY 위반
            if (e.getErrorCode() == 2627) {
                return false;
            }
            throw e;
        } finally {
            VaultDBConnection.disconnect(con, pstmt, null);
        }
    }


    // 관리대상 호기 수정. 호기(PK) 와 생성날짜는 바꾸지 않는다. 대상이 없으면 false
    public static boolean updateManagedHogi(Map<String, String> hogi) throws Exception {

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = connect();

            String sql = """
                    UPDATE PLM_SIMUL_HOGI
                    SET BEFOREHOGI = ?, BRAND = ?, ACAPA = ?, SPEED = ?, BATCHDATE = ?
                    WHERE HOGI = ?
                    """;

            pstmt = con.prepareStatement(sql);
            bindHogi(pstmt, hogi);
            return pstmt.executeUpdate() > 0;

        } finally {
            VaultDBConnection.disconnect(con, pstmt, null);
        }
    }


    // 관리대상 호기의 배치 날짜만 갱신한다. 등록되지 않은 호기면 아무것도 바꾸지 않고 false
    public static boolean updateManagedHogiBatchDate(String hogi, String batchDate) throws Exception {

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = connect();

            pstmt = con.prepareStatement("UPDATE PLM_SIMUL_HOGI SET BATCHDATE = ? WHERE HOGI = ?");
            pstmt.setNString(1, batchDate);
            pstmt.setString(2, hogi);
            return pstmt.executeUpdate() > 0;

        } finally {
            VaultDBConnection.disconnect(con, pstmt, null);
        }
    }


    // 관리대상 호기 삭제. 한 건이라도 실패하면 모두 되돌린다. 삭제된 건수를 돌려준다
    public static int deleteManagedHogi(List<String> hogis) throws Exception {

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = connect();
            con.setAutoCommit(false);

            pstmt = con.prepareStatement("DELETE FROM PLM_SIMUL_HOGI WHERE HOGI = ?");

            int count = 0;
            for (String hogi : hogis) {
                pstmt.setString(1, hogi);
                count += pstmt.executeUpdate();
            }

            con.commit();
            return count;

        } catch (Exception e) {
            if (con != null) {
                con.rollback();
            }
            throw e;
        } finally {
            VaultDBConnection.disconnect(con, pstmt, null);
        }
    }


    // 등록 · 수정 공통 바인딩 (HOGI 가 마지막 파라미터)
    private static void bindHogi(PreparedStatement pstmt, Map<String, String> hogi) throws SQLException {
        pstmt.setString(1, hogi.get("BEFOREHOGI"));
        pstmt.setNString(2, hogi.get("BRAND"));
        pstmt.setNString(3, hogi.get("ACAPA"));
        pstmt.setNString(4, hogi.get("SPEED"));
        pstmt.setNString(5, hogi.get("BATCHDATE"));
        pstmt.setString(6, hogi.get("HOGI"));
    }


    private static Connection connect() {
        Connection con = VaultDBConnection.getConnection();
        if (con == null) {
            throw new IllegalStateException("DB 연결 실패");
        }
        return con;
    }

}
