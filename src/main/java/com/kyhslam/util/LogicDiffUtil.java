package com.kyhslam.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 로직(variant_d) 두 버전 비교 유틸.
 *
 * 기존 방식은 "한 행의 전체 셀 내용"을 통째로 키로 사용해서, 셀 하나만 바뀌어도
 * 그 행 전체가 (과거)삭제 + (최신)추가 로 잡혔다. 어떤 행이 어디로 옮겨갔는지,
 * 어느 셀이 바뀌었는지 알 수 없었다.
 *
 * 새 방식:
 *  1) ADDR(주소)를 행의 식별자로 사용해 과거/최신 행을 매칭한다.
 *     - ADDR 이 같으면 같은 행으로 보고, 셀 단위로 비교한다. (위치가 바뀌어도 매칭됨)
 *  2) ADDR 이 비었거나 매칭이 안 된 행은 "전체 내용"으로 한번 더 매칭한다.(이동된 동일행 구제)
 *  3) 최종적으로 각 행을 EQUAL / MODIFIED / ADDED / DELETED 로 분류하고,
 *     MODIFIED 의 경우 어떤 셀이 바뀌었는지(changed=true) 표시한다.
 */
public class LogicDiffUtil {

    /** 비교 대상 컬럼 순서 (헤더/셀 렌더링 순서와 동일하게 유지) */
    public static List<String> getColumns() {
        List<String> cols = new ArrayList<>();
        cols.add("ADDR");
        for (int i = 1; i <= 20; i++) {
            cols.add("SPEC" + i);
            cols.add("CON" + i);
        }
        for (int i = 1; i <= 20; i++) {
            cols.add("KEY" + i);
            cols.add("VAL" + i);
        }
        cols.add("GOTO");
        cols.add("REMARKS");
        return cols;
    }

    /** 한 행의 정규화된 데이터 보관용 */
    private static class Row {
        String addr = "";
        String no = "";
        LinkedHashMap<String, String> cells = new LinkedHashMap<>(); // 컬럼명 -> 정규화 값
        String contentKey = "";                                      // 전체내용 매칭용 키
    }

    private static final String SELECT_SQL = """
            SELECT h.pid AS PID,
                    D.NO AS NO,
                    D.ADDR AS ADDR,
                    NVL(D.SPEC1, '-') AS SPEC1, NVL(D.CON1, '-') AS CON1,
                    NVL(D.SPEC2, '-') AS SPEC2, NVL(D.CON2, '-') AS CON2,
                    NVL(D.SPEC3, '-') AS SPEC3, NVL(D.CON3, '-') AS CON3,
                    NVL(D.SPEC4, '-') AS SPEC4, NVL(D.CON4, '-') AS CON4,
                    NVL(D.SPEC5, '-') AS SPEC5, NVL(D.CON5, '-') AS CON5,
                    NVL(D.SPEC6, '-') AS SPEC6, NVL(D.CON6, '-') AS CON6,
                    NVL(D.SPEC7, '-') AS SPEC7, NVL(D.CON7, '-') AS CON7,
                    NVL(D.SPEC8, '-') AS SPEC8, NVL(D.CON8, '-') AS CON8,
                    NVL(D.SPEC9, '-') AS SPEC9, NVL(D.CON9, '-') AS CON9,
                    NVL(D.SPEC10, '-') AS SPEC10, NVL(D.CON10, '-') AS CON10,
                    NVL(D.SPEC11, '-') AS SPEC11, NVL(D.CON11, '-') AS CON11,
                    NVL(D.SPEC12, '-') AS SPEC12, NVL(D.CON12, '-') AS CON12,
                    NVL(D.SPEC13, '-') AS SPEC13, NVL(D.CON13, '-') AS CON13,
                    NVL(D.SPEC14, '-') AS SPEC14, NVL(D.CON14, '-') AS CON14,
                    NVL(D.SPEC15, '-') AS SPEC15, NVL(D.CON15, '-') AS CON15,
                    NVL(D.SPEC16, '-') AS SPEC16, NVL(D.CON16, '-') AS CON16,
                    NVL(D.SPEC17, '-') AS SPEC17, NVL(D.CON17, '-') AS CON17,
                    NVL(D.SPEC18, '-') AS SPEC18, NVL(D.CON18, '-') AS CON18,
                    NVL(D.SPEC19, '-') AS SPEC19, NVL(D.CON19, '-') AS CON19,
                    NVL(D.SPEC20, '-') AS SPEC20, NVL(D.CON20, '-') AS CON20,
                    NVL(D.KEY1, '-') AS KEY1, NVL(D.VAL1, '-') AS VAL1,
                    NVL(D.KEY2, '-') AS KEY2, NVL(D.VAL2, '-') AS VAL2,
                    NVL(D.KEY3, '-') AS KEY3, NVL(D.VAL3, '-') AS VAL3,
                    NVL(D.KEY4, '-') AS KEY4, NVL(D.VAL4, '-') AS VAL4,
                    NVL(D.KEY5, '-') AS KEY5, NVL(D.VAL5, '-') AS VAL5,
                    NVL(D.KEY6, '-') AS KEY6, NVL(D.VAL6, '-') AS VAL6,
                    NVL(D.KEY7, '-') AS KEY7, NVL(D.VAL7, '-') AS VAL7,
                    NVL(D.KEY8, '-') AS KEY8, NVL(D.VAL8, '-') AS VAL8,
                    NVL(D.KEY9, '-') AS KEY9, NVL(D.VAL9, '-') AS VAL9,
                    NVL(D.KEY10, '-') AS KEY10, NVL(D.VAL10, '-') AS VAL10,
                    NVL(D.KEY11, '-') AS KEY11, NVL(D.VAL11, '-') AS VAL11,
                    NVL(D.KEY12, '-') AS KEY12, NVL(D.VAL12, '-') AS VAL12,
                    NVL(D.KEY13, '-') AS KEY13, NVL(D.VAL13, '-') AS VAL13,
                    NVL(D.KEY14, '-') AS KEY14, NVL(D.VAL14, '-') AS VAL14,
                    NVL(D.KEY15, '-') AS KEY15, NVL(D.VAL15, '-') AS VAL15,
                    NVL(D.KEY16, '-') AS KEY16, NVL(D.VAL16, '-') AS VAL16,
                    NVL(D.KEY17, '-') AS KEY17, NVL(D.VAL17, '-') AS VAL17,
                    NVL(D.KEY18, '-') AS KEY18, NVL(D.VAL18, '-') AS VAL18,
                    NVL(D.KEY19, '-') AS KEY19, NVL(D.VAL19, '-') AS VAL19,
                    NVL(D.KEY20, '-') AS KEY20, NVL(D.VAL20, '-') AS VAL20,
                    NVL(D.REMARKS, '-') AS REMARKS, D.GOTO AS GOTO
             FROM variant_d d, variant_h h
             WHERE H.HOUID = D.HOUID
             AND H.PID = ?
             AND D.HOUID = ?
            """;

    /**
     * 두 버전을 비교해서 행 단위 diff 결과를 만든다.
     *
     * @param pid       제품 ID
     * @param beforeOid 과거 버전 HOUID
     * @param afterOid  최신 버전 HOUID
     * @return columns(컬럼순서), rows(행 diff), summary(상태별 건수)
     */
    public static HashMap<String, Object> diffVersions(String pid, String beforeOid, String afterOid) {

        List<Row> before = readVersion(pid, beforeOid);
        List<Row> after = readVersion(pid, afterOid);

        // ADDR 기준 매칭 (같은 ADDR 이 여러개면 순서대로 큐에서 꺼냄)
        Map<String, ArrayDeque<Integer>> beforeByAddr = new HashMap<>();
        for (int i = 0; i < before.size(); i++) {
            String addr = before.get(i).addr;
            if (!addr.isEmpty()) {
                beforeByAddr.computeIfAbsent(addr, k -> new ArrayDeque<>()).add(i);
            }
        }

        boolean[] beforeMatched = new boolean[before.size()];
        Integer[] afterToBefore = new Integer[after.size()]; // 최신행 -> 매칭된 과거행 index

        // 1차: ADDR 매칭
        for (int j = 0; j < after.size(); j++) {
            String addr = after.get(j).addr;
            if (addr.isEmpty()) continue;
            ArrayDeque<Integer> q = beforeByAddr.get(addr);
            if (q != null && !q.isEmpty()) {
                int bi = q.poll();
                afterToBefore[j] = bi;
                beforeMatched[bi] = true;
            }
        }

        // 2차: 매칭 안된 행끼리 "전체 내용"으로 매칭 (ADDR이 비었거나 바뀐 동일행 구제)
        Map<String, ArrayDeque<Integer>> beforeByContent = new HashMap<>();
        for (int i = 0; i < before.size(); i++) {
            if (!beforeMatched[i]) {
                beforeByContent.computeIfAbsent(before.get(i).contentKey, k -> new ArrayDeque<>()).add(i);
            }
        }
        for (int j = 0; j < after.size(); j++) {
            if (afterToBefore[j] != null) continue;
            ArrayDeque<Integer> q = beforeByContent.get(after.get(j).contentKey);
            if (q != null && !q.isEmpty()) {
                int bi = q.poll();
                afterToBefore[j] = bi;
                beforeMatched[bi] = true;
            }
        }

        // 결과 조립: 최신 순서대로 출력하되, 사이사이에 삭제된 과거행을 위치에 맞게 끼워넣음
        Map<String, String> codeMap = new HashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        int equal = 0, modified = 0, added = 0, deleted = 0;

        int beforePtr = 0; // 아직 출력 안한 과거행 포인터
        for (int j = 0; j < after.size(); j++) {
            Row a = after.get(j);
            Integer biObj = afterToBefore[j];

            if (biObj != null) {
                int bi = biObj;
                // 이 anchor 앞쪽의 매칭 안된 과거행들을 DELETED 로 먼저 출력
                while (beforePtr < bi) {
                    if (!beforeMatched[beforePtr]) {
                        rows.add(buildRow("DELETED", before.get(beforePtr), null, codeMap));
                        deleted++;
                    }
                    beforePtr++;
                }
                Row b = before.get(bi);
                boolean same = a.cells.equals(b.cells);
                rows.add(buildRow(same ? "EQUAL" : "MODIFIED", b, a, codeMap));
                if (same) equal++; else modified++;
                if (beforePtr <= bi) beforePtr = bi + 1;
            } else {
                rows.add(buildRow("ADDED", null, a, codeMap));
                added++;
            }
        }
        // 남은 과거행(=삭제)
        for (int i = beforePtr; i < before.size(); i++) {
            if (!beforeMatched[i]) {
                rows.add(buildRow("DELETED", before.get(i), null, codeMap));
                deleted++;
            }
        }

        HashMap<String, Object> summary = new HashMap<>();
        summary.put("equal", equal);
        summary.put("modified", modified);
        summary.put("added", added);
        summary.put("deleted", deleted);
        summary.put("total", rows.size());

        HashMap<String, Object> result = new HashMap<>();
        result.put("columns", getColumns());
        result.put("rows", rows);
        result.put("summary", summary);
        return result;
    }

    /** 한 행을 출력용 Map 으로 변환. 셀별로 {b:과거표시값, a:최신표시값, c:변경여부} */
    private static Map<String, Object> buildRow(String status, Row before, Row after, Map<String, String> codeMap) {
        Row primary = after != null ? after : before;
        LinkedHashMap<String, Object> cells = new LinkedHashMap<>();

        for (String col : getColumns()) {
            String bRaw = before != null ? nz(before.cells.get(col)) : "";
            String aRaw = after != null ? nz(after.cells.get(col)) : "";

            boolean changed = "MODIFIED".equals(status) && !bRaw.equals(aRaw);

            LinkedHashMap<String, Object> cell = new LinkedHashMap<>();
            cell.put("b", annotate(col, bRaw, codeMap));
            cell.put("a", annotate(col, aRaw, codeMap));
            cell.put("c", changed);
            cells.put(col, cell);
        }

        LinkedHashMap<String, Object> row = new LinkedHashMap<>();
        row.put("status", status);
        row.put("addr", primary.addr);
        row.put("noBefore", before != null ? before.no : "");
        row.put("noAfter", after != null ? after.no : "");
        row.put("cells", cells);
        return row;
    }

    /** SPEC 컬럼이 EL_ 코드면 코드명을 괄호로 덧붙임(표시용). 그 외는 원값. */
    private static String annotate(String col, String value, Map<String, String> codeMap) {
        if (value == null || value.isEmpty()) return "";
        if (col.startsWith("SPEC") && value.startsWith("EL_")) {
            String name = codeMap.get(value);
            if (name == null) {
                name = SubaeCommonUtil.findCodeName(value);
                name = name == null ? "" : name.trim();
                codeMap.put(value, name);
            }
            if (!name.isEmpty() && !"null".equals(name)) {
                return value + " \n(" + name + ")";
            }
        }
        return value;
    }

    /** 한 버전의 모든 행을 읽어 정규화 */
    private static List<Row> readVersion(String pid, String houid) {
        List<Row> rows = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBConnection.getConnection();
            pstmt = con.prepareStatement(SELECT_SQL);
            pstmt.setString(1, pid);
            pstmt.setString(2, houid);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Row row = new Row();
                row.no = nz(rs.getString("NO"));
                row.addr = norm("ADDR", rs.getString("ADDR"));

                StringBuilder content = new StringBuilder();
                for (String col : getColumns()) {
                    String v = norm(col, rs.getString(col));
                    row.cells.put(col, v);
                    content.append(v).append('|');
                }
                row.contentKey = content.toString();
                rows.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return rows;
    }

    /** 컬럼별 정규화 규칙(기존 동작 유지). 비교/표시 모두 동일 규칙 적용 */
    private static String norm(String col, String raw) {
        if (raw == null) return "";
        String v = raw.trim();
        if (col.startsWith("VAL") || "GOTO".equals(col) || "ADDR".equals(col)) {
            // VAL/GOTO/ADDR: 안의 하이픈은 보존, "-" 한 글자만 빈 값 처리
            if ("-".equals(v)) v = "";
            return v;
        }
        // SPEC/CON/KEY/REMARKS: 기존처럼 하이픈 제거
        return v.replace("-", "");
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
