package com.kyhslam.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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

        // 버전 이력 추적 결과 (Ver 목록의 index)
        int createdVer = -1;    // 이 라인이 처음 등장한 버전
        int modifiedVer = -1;   // 지금 내용으로 마지막 바뀐 버전
        boolean createdEst;     // 추적 중 유사도 매칭을 거침(추정)
        boolean modifiedEst;
        boolean origin;         // 추적 가능한 최초 버전부터 존재 (그 이전은 알 수 없음)
    }

    /** VARIANT_H 한 건 (버전 헤더) */
    private static class Ver {
        String houid = "";
        String version = "";
        String regDate = "";
        boolean test;           // VERSION = '-1' 테스트 버전
    }

    /** PID 의 버전 헤더 전체. 등록일 순으로 이력을 이어 붙인다. */
    private static final String HEADER_SQL = """
            SELECT H.HOUID AS HOUID, H.VERSION AS VERSION, H.REG_DATE AS REG_DATE
              FROM variant_h h
             WHERE H.PID = ?
             ORDER BY H.REG_DATE, H.HOUID
            """;

    /**
     * PID 의 라인 전체(비교 대상 두 버전 중 늦은 등록일까지).
     * 기존 단일버전 조회 SQL 과 컬럼/정규화는 동일하고, HOUID 조건만 등록일 범위로 바꿨다.
     */
    private static final String SELECT_SQL = """
            SELECT h.pid AS PID, D.HOUID AS HOUID,
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
             AND (D.HOUID IN (?, ?)
                  OR H.REG_DATE <= (SELECT MAX(H2.REG_DATE) FROM variant_h H2
                                     WHERE H2.PID = ? AND H2.HOUID IN (?, ?)))
            """;

    /** SPEC/CON, KEY/VAL 쌍(슬롯) 개수 */
    private static final int PAIRS = 20;
    /** 유사도 매칭 기준: 공통 항목 비율 */
    private static final double SIMILAR_THRESHOLD = 0.6;
    /** 유사도 매칭 최대 비교 횟수(미매칭 과거행 x 미매칭 최신행). 넘으면 생략 */
    private static final long SIMILAR_MAX_PAIRS = 2_000_000L;

    /**
     * 두 버전을 비교해서 행 단위 diff 결과를 만든다.
     *
     * 행 매칭 (3단계)
     *  1) 전체 내용이 완전히 같은 행끼리 먼저 매칭 (ADDR 이 중복돼도 동일행이 정확히 짝지어짐)
     *  2) 남은 행은 ADDR 로 매칭 -> 셀 단위 비교
     *  3) 그래도 남은 행은 내용 유사도로 매칭 (ADDR 이 바뀐 행을 삭제+추가 가 아닌 수정으로 인식)
     *
     * 열 비교
     *  - SPEC/CON, KEY/VAL 은 "쌍" 단위로 정렬해서 비교한다. 중간에 쌍이 끼어들거나 빠져서
     *    뒤 쌍들이 한 칸씩 밀려도, 밀린 쌍은 "위치 이동(mov)" 으로만 표시하고 실제로 바뀐 값만 변경으로 잡는다.
     *
     * @param pid       제품 ID
     * @param beforeOid 과거 버전 HOUID
     * @param afterOid  최신 버전 HOUID
     * @return columns(컬럼순서), rows(행 diff), summary(상태별 건수)
     */
    public static HashMap<String, Object> diffVersions(String pid, String beforeOid, String afterOid) {

        // 버전 헤더 + 라인 전체를 한 번에 읽고, 두 버전의 라인은 여기서 꺼내 쓴다.
        List<Ver> vers = readHeaders(pid);
        Map<String, List<Row>> rowsByVer = readRows(pid, beforeOid, afterOid);
        List<Row> before = rowsByVer.getOrDefault(nz(beforeOid), new ArrayList<>());
        List<Row> after = rowsByVer.getOrDefault(nz(afterOid), new ArrayList<>());

        // 각 라인의 생성일 / 최종 수정일 추적 (Row 에 결과 기록)
        int traced = traceHistory(vers, rowsByVer, nz(beforeOid), nz(afterOid));

        boolean[] beforeMatched = new boolean[before.size()];
        Integer[] afterToBefore = new Integer[after.size()]; // 최신행 -> 매칭된 과거행 index
        boolean[] fuzzy = new boolean[after.size()];         // 유사도로 매칭된 행 여부
        matchRows(before, after, beforeMatched, afterToBefore, fuzzy);

        boolean[] moved = detectMoved(afterToBefore); // 행 순서가 바뀐 매칭행

        // 결과 조립: 최신 순서대로 출력하되, 사이사이에 삭제된 과거행을 위치에 맞게 끼워넣음
        Map<String, String> codeMap = new HashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        int equal = 0, modified = 0, added = 0, deleted = 0;
        int movedCnt = 0, fuzzyCnt = 0, shiftOnlyCnt = 0, changedCells = 0;

        int beforePtr = 0; // 아직 출력 안한 과거행 포인터
        for (int j = 0; j < after.size(); j++) {
            Row a = after.get(j);
            Integer biObj = afterToBefore[j];

            if (biObj != null) {
                int bi = biObj;
                // 이 anchor 앞쪽의 매칭 안된 과거행들을 DELETED 로 먼저 출력
                while (beforePtr < bi) {
                    if (!beforeMatched[beforePtr]) {
                        rows.add(buildRow("DELETED", before.get(beforePtr), null, codeMap, vers));
                        deleted++;
                    }
                    beforePtr++;
                }
                Row b = before.get(bi);
                boolean same = a.cells.equals(b.cells);
                Map<String, Object> row = buildRow(same ? "EQUAL" : "MODIFIED", b, a, codeMap, vers);
                if (moved[j]) { row.put("moved", true); movedCnt++; }
                if (fuzzy[j]) { row.put("fuzzy", true); fuzzyCnt++; }
                if (same) {
                    equal++;
                } else {
                    modified++;
                    changedCells += (Integer) row.get("nChg");
                    if (Boolean.TRUE.equals(row.get("shiftOnly"))) shiftOnlyCnt++;
                }
                rows.add(row);
                if (beforePtr <= bi) beforePtr = bi + 1;
            } else {
                rows.add(buildRow("ADDED", null, a, codeMap, vers));
                added++;
            }
        }
        // 남은 과거행(=삭제)
        for (int i = beforePtr; i < before.size(); i++) {
            if (!beforeMatched[i]) {
                rows.add(buildRow("DELETED", before.get(i), null, codeMap, vers));
                deleted++;
            }
        }

        HashMap<String, Object> summary = new HashMap<>();
        summary.put("equal", equal);
        summary.put("modified", modified);
        summary.put("added", added);
        summary.put("deleted", deleted);
        summary.put("total", rows.size());
        summary.put("moved", movedCnt);
        summary.put("fuzzy", fuzzyCnt);
        summary.put("shiftOnly", shiftOnlyCnt);
        summary.put("changedCells", changedCells);
        summary.put("beforeCount", before.size());
        summary.put("afterCount", after.size());
        summary.put("historyCount", traced);

        HashMap<String, Object> result = new HashMap<>();
        result.put("columns", getColumns());
        result.put("rows", rows);
        result.put("summary", summary);
        return result;
    }

    /**
     * 두 버전의 행 매칭 (3단계). 비교 화면과 이력 추적에서 같은 규칙을 쓴다.
     *  1) 전체 내용 동일  2) ADDR  3) 내용 유사도
     */
    private static void matchRows(List<Row> before, List<Row> after, boolean[] beforeMatched,
                                  Integer[] afterToBefore, boolean[] fuzzy) {
        matchByKey(before, after, beforeMatched, afterToBefore, r -> r.contentKey); // 1차: 완전 동일
        matchByKey(before, after, beforeMatched, afterToBefore, r -> r.addr);       // 2차: ADDR
        matchBySimilarity(before, after, beforeMatched, afterToBefore, fuzzy);      // 3차: 유사도
    }

    /**
     * 버전 이력 추적: 등록일 순으로 인접 버전끼리 행을 매칭해 각 라인의
     * 생성 버전(처음 등장) / 최종 수정 버전(지금 내용이 처음 등장) 을 Row 에 기록한다.
     *
     *  - 테스트 버전(VERSION=-1)은 이력 체인에서 빠진다. 단, 비교 대상이면 그 시점의
     *    직전 정식 버전과 이어서 추적한다.
     *  - 라인이 하나도 없는 버전은 건너뛴다. (전체 삭제 후 재등록으로 오인하지 않도록)
     *  - 두 비교 버전 중 늦은 쪽까지만 추적한다.
     *
     * @return 추적에 사용한 버전 수
     */
    private static int traceHistory(List<Ver> vers, Map<String, List<Row>> rowsByVer,
                                    String beforeOid, String afterOid) {
        int last = -1;
        for (int t = 0; t < vers.size(); t++) {
            String h = vers.get(t).houid;
            if (h.equals(beforeOid) || h.equals(afterOid)) last = t;
        }
        if (last < 0) return 0;

        List<Row> prev = null;
        int traced = 0;
        for (int t = 0; t <= last; t++) {
            Ver v = vers.get(t);
            boolean target = v.houid.equals(beforeOid) || v.houid.equals(afterOid);
            List<Row> cur = rowsByVer.get(v.houid);
            if (!target && (v.test || cur == null || cur.isEmpty())) continue;
            if (cur == null) continue;
            traced++;

            if (prev == null) {
                for (Row r : cur) {
                    r.createdVer = t;
                    r.modifiedVer = t;
                    r.origin = true;
                }
            } else {
                boolean[] pm = new boolean[prev.size()];
                Integer[] map = new Integer[cur.size()];
                boolean[] fz = new boolean[cur.size()];
                matchRows(prev, cur, pm, map, fz);
                for (int j = 0; j < cur.size(); j++) {
                    Row r = cur.get(j);
                    Integer p = map[j];
                    if (p == null) {               // 이 버전에서 새로 생긴 라인
                        r.createdVer = t;
                        r.modifiedVer = t;
                        continue;
                    }
                    Row o = prev.get(p);
                    r.createdVer = o.createdVer;
                    r.createdEst = o.createdEst || fz[j];
                    r.origin = o.origin;
                    if (r.contentKey.equals(o.contentKey)) { // 내용 그대로
                        r.modifiedVer = o.modifiedVer;
                        r.modifiedEst = o.modifiedEst;
                    } else {                                // 이 버전에서 내용이 바뀜
                        r.modifiedVer = t;
                    }
                }
            }
            if (!v.test && !cur.isEmpty()) prev = cur;
        }
        return traced;
    }

    /** 이력 추적 결과를 출력용 Map 으로 */
    private static Map<String, Object> historyOf(Row r, List<Ver> vers) {
        LinkedHashMap<String, Object> h = new LinkedHashMap<>();
        if (r == null || r.createdVer < 0 || r.createdVer >= vers.size()) return h;
        Ver c = vers.get(r.createdVer);
        Ver m = vers.get(Math.max(0, Math.min(r.modifiedVer, vers.size() - 1)));
        h.put("cDate", shortDate(c.regDate));
        h.put("cDateRaw", c.regDate);
        h.put("cVer", verLabel(c));
        h.put("cEst", r.createdEst);
        h.put("origin", r.origin);
        h.put("mDate", shortDate(m.regDate));
        h.put("mDateRaw", m.regDate);
        h.put("mVer", verLabel(m));
        h.put("mEst", r.modifiedEst);
        h.put("same", r.modifiedVer == r.createdVer); // 생성 이후 내용 변경 없음
        return h;
    }

    private static String verLabel(Ver v) {
        if (v.test) return "TEST";
        return v.version.isEmpty() ? "" : "v" + v.version;
    }

    /** 등록일 표시용: 2024-01-05 00:00:00.0 / 20240105... -> 2024-01-05 */
    private static String shortDate(String raw) {
        String s = nz(raw).trim();
        if (s.matches("^\\d{4}-\\d{2}-\\d{2}.*")) return s.substring(0, 10);
        if (s.matches("^\\d{4}/\\d{2}/\\d{2}.*")) return s.substring(0, 10).replace('/', '-');
        if (s.matches("^\\d{8}.*")) return s.substring(0, 4) + "-" + s.substring(4, 6) + "-" + s.substring(6, 8);
        return s;
    }

    /** 아직 매칭 안된 행끼리 같은 키(전체내용/ADDR)를 가진 것을 순서대로 매칭 */
    private static void matchByKey(List<Row> before, List<Row> after, boolean[] beforeMatched,
                                   Integer[] afterToBefore, Function<Row, String> keyFn) {
        Map<String, ArrayDeque<Integer>> index = new HashMap<>();
        for (int i = 0; i < before.size(); i++) {
            if (beforeMatched[i]) continue;
            String key = keyFn.apply(before.get(i));
            if (key == null || key.isEmpty()) continue;
            index.computeIfAbsent(key, k -> new ArrayDeque<>()).add(i);
        }
        for (int j = 0; j < after.size(); j++) {
            if (afterToBefore[j] != null) continue;
            String key = keyFn.apply(after.get(j));
            if (key == null || key.isEmpty()) continue;
            ArrayDeque<Integer> q = index.get(key);
            if (q != null && !q.isEmpty()) {
                int bi = q.poll();
                afterToBefore[j] = bi;
                beforeMatched[bi] = true;
            }
        }
    }

    /**
     * 남은 행끼리 내용 유사도로 매칭. (ADDR 이 바뀌었지만 사실상 같은 행)
     * 유사도 = 공통 항목 수 / 두 행 중 항목이 많은 쪽의 항목 수. 높은 점수부터 1:1 배정.
     */
    private static void matchBySimilarity(List<Row> before, List<Row> after, boolean[] beforeMatched,
                                          Integer[] afterToBefore, boolean[] fuzzy) {
        List<Integer> ub = new ArrayList<>();
        List<Integer> ua = new ArrayList<>();
        for (int i = 0; i < before.size(); i++) if (!beforeMatched[i]) ub.add(i);
        for (int j = 0; j < after.size(); j++) if (afterToBefore[j] == null) ua.add(j);
        if (ub.isEmpty() || ua.isEmpty()) return;
        if ((long) ub.size() * ua.size() > SIMILAR_MAX_PAIRS) return;

        List<Set<String>> tb = new ArrayList<>();
        List<Set<String>> ta = new ArrayList<>();
        for (int i : ub) tb.add(tokens(before.get(i)));
        for (int j : ua) ta.add(tokens(after.get(j)));

        List<int[]> cands = new ArrayList<>(); // {과거 index, 최신 index, 점수(x1000)}
        for (int x = 0; x < ub.size(); x++) {
            Set<String> sb = tb.get(x);
            if (sb.size() < 2) continue;
            for (int y = 0; y < ua.size(); y++) {
                Set<String> sa = ta.get(y);
                if (sa.size() < 2) continue;
                int common = 0;
                for (String t : sa) if (sb.contains(t)) common++;
                if (common < 2) continue;
                double score = (double) common / Math.max(sa.size(), sb.size());
                if (score >= SIMILAR_THRESHOLD) {
                    cands.add(new int[]{ub.get(x), ua.get(y), (int) Math.round(score * 1000)});
                }
            }
        }
        // 점수 높은 순, 같으면 위치가 가까운 순
        cands.sort((p, q) -> p[2] != q[2] ? q[2] - p[2] : Math.abs(p[0] - p[1]) - Math.abs(q[0] - q[1]));
        for (int[] c : cands) {
            if (beforeMatched[c[0]] || afterToBefore[c[1]] != null) continue;
            afterToBefore[c[1]] = c[0];
            beforeMatched[c[0]] = true;
            fuzzy[c[1]] = true;
        }
    }

    /** 유사도 비교용 항목 집합 (열 위치와 무관하게 SPEC=CON, KEY=VAL 쌍 단위) */
    private static Set<String> tokens(Row r) {
        Set<String> s = new HashSet<>();
        for (int i = 1; i <= PAIRS; i++) {
            String sp = nz(r.cells.get("SPEC" + i)), co = nz(r.cells.get("CON" + i));
            if (!sp.isEmpty() || !co.isEmpty()) s.add("S|" + sp + "|" + co);
            String k = nz(r.cells.get("KEY" + i)), v = nz(r.cells.get("VAL" + i));
            if (!k.isEmpty() || !v.isEmpty()) s.add("K|" + k + "|" + v);
        }
        String g = nz(r.cells.get("GOTO"));
        if (!g.isEmpty()) s.add("G|" + g);
        String rm = nz(r.cells.get("REMARKS"));
        if (!rm.isEmpty()) s.add("R|" + rm);
        return s;
    }

    /**
     * 매칭된 행들 중 "상대 순서가 바뀐" 행을 찾는다.
     * 과거 index 의 최장 증가 부분수열(LIS)에 속하지 않는 행 = 위치가 옮겨진 행.
     */
    private static boolean[] detectMoved(Integer[] afterToBefore) {
        boolean[] moved = new boolean[afterToBefore.length];
        List<Integer> js = new ArrayList<>();
        for (int j = 0; j < afterToBefore.length; j++) if (afterToBefore[j] != null) js.add(j);
        int n = js.size();
        if (n == 0) return moved;

        int[] tails = new int[n];  // 길이 k+1 인 증가수열의 마지막 원소(js 내 위치)
        int[] prev = new int[n];
        int len = 0;
        for (int x = 0; x < n; x++) {
            int v = afterToBefore[js.get(x)];
            int lo = 0, hi = len;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (afterToBefore[js.get(tails[mid])] < v) lo = mid + 1; else hi = mid;
            }
            prev[x] = lo > 0 ? tails[lo - 1] : -1;
            tails[lo] = x;
            if (lo == len) len++;
        }
        boolean[] inLis = new boolean[n];
        for (int x = tails[len - 1]; x >= 0; x = prev[x]) inLis[x] = true;
        for (int x = 0; x < n; x++) if (!inLis[x]) moved[js.get(x)] = true;
        return moved;
    }

    /**
     * (first, second) 쌍 20개를 과거/최신 간에 정렬한다.
     * @return m[i] = 최신 i번 슬롯과 짝지어진 과거 슬롯 index (전체가 1:1 대응)
     */
    private static int[] alignPairs(Row before, Row after, String first, String second) {
        int n = PAIRS;
        String[] b1 = new String[n], b2 = new String[n], a1 = new String[n], a2 = new String[n];
        for (int i = 0; i < n; i++) {
            b1[i] = nz(before.cells.get(first + (i + 1)));
            b2[i] = nz(before.cells.get(second + (i + 1)));
            a1[i] = nz(after.cells.get(first + (i + 1)));
            a2[i] = nz(after.cells.get(second + (i + 1)));
        }
        int[] m = new int[n];
        Arrays.fill(m, -1);
        boolean[] used = new boolean[n];

        // 1) 같은 위치, 완전 동일
        for (int i = 0; i < n; i++) {
            if (a1[i].equals(b1[i]) && a2[i].equals(b2[i])) { m[i] = i; used[i] = true; }
        }
        // 2) 다른 위치에 완전 동일한 쌍 (위치 이동)
        for (int i = 0; i < n; i++) {
            if (m[i] >= 0 || (a1[i].isEmpty() && a2[i].isEmpty())) continue;
            for (int k = 0; k < n; k++) {
                if (!used[k] && a1[i].equals(b1[k]) && a2[i].equals(b2[k])) { m[i] = k; used[k] = true; break; }
            }
        }
        // 3) 같은 위치, 앞 항목(SPEC/KEY)만 동일 (값만 변경)
        for (int i = 0; i < n; i++) {
            if (m[i] < 0 && !used[i] && !a1[i].isEmpty() && a1[i].equals(b1[i])) { m[i] = i; used[i] = true; }
        }
        // 4) 다른 위치에 앞 항목이 같은 쌍 (위치 이동 + 값 변경)
        for (int i = 0; i < n; i++) {
            if (m[i] >= 0 || a1[i].isEmpty()) continue;
            for (int k = 0; k < n; k++) {
                if (!used[k] && a1[i].equals(b1[k])) { m[i] = k; used[k] = true; break; }
            }
        }
        // 5) 같은 위치끼리
        for (int i = 0; i < n; i++) {
            if (m[i] < 0 && !used[i]) { m[i] = i; used[i] = true; }
        }
        // 6) 나머지: 빈 슬롯 <-> 값 있는 슬롯을 우선 짝지어 삭제/추가로 보이게 하고, 남으면 순서대로
        for (int pass = 0; pass < 3; pass++) {
            for (int i = 0; i < n; i++) {
                if (m[i] >= 0) continue;
                boolean aEmpty = a1[i].isEmpty() && a2[i].isEmpty();
                for (int k = 0; k < n; k++) {
                    if (used[k]) continue;
                    boolean bEmpty = b1[k].isEmpty() && b2[k].isEmpty();
                    boolean ok = pass == 2 || (pass == 0 && aEmpty && !bEmpty) || (pass == 1 && !aEmpty && bEmpty);
                    if (ok) { m[i] = k; used[k] = true; break; }
                }
            }
        }
        return m;
    }

    /** SPEC3 -> {"SPEC","3"} 처럼 쌍 컬럼이면 {접두어, 번호}, 아니면 null */
    private static String[] splitPairCol(String col) {
        for (String p : PAIR_PREFIXES) {
            if (col.startsWith(p) && col.length() > p.length()) {
                String rest = col.substring(p.length());
                if (rest.chars().allMatch(Character::isDigit)) return new String[]{p, rest};
            }
        }
        return null;
    }

    private static final String[] PAIR_PREFIXES = {"SPEC", "CON", "KEY", "VAL"};

    /**
     * 한 행을 출력용 Map 으로 변환.
     * 셀: {b:과거표시값, a:최신표시값, c:차이여부, t:차이종류, f:과거 컬럼(위치가 다를 때)}
     *   t = chg(값 변경) / add(값 추가) / del(값 삭제) / mov(값은 같고 열 위치만 이동)
     */
    private static Map<String, Object> buildRow(String status, Row before, Row after, Map<String, String> codeMap,
                                                List<Ver> vers) {
        Row primary = after != null ? after : before;
        boolean modified = "MODIFIED".equals(status);
        LinkedHashMap<String, Object> cells = new LinkedHashMap<>();

        int[] mSpec = modified ? alignPairs(before, after, "SPEC", "CON") : null;
        int[] mKey = modified ? alignPairs(before, after, "KEY", "VAL") : null;
        int nChg = 0, nMov = 0;

        for (String col : getColumns()) {
            // 과거값을 가져올 컬럼 (쌍 정렬 결과 다른 슬롯일 수 있음)
            String srcCol = col;
            if (modified) {
                String[] pc = splitPairCol(col);
                if (pc != null) {
                    int slot = Integer.parseInt(pc[1]);
                    if (slot >= 1 && slot <= PAIRS) {
                        int[] m = ("SPEC".equals(pc[0]) || "CON".equals(pc[0])) ? mSpec : mKey;
                        srcCol = pc[0] + (m[slot - 1] + 1);
                    }
                }
            }

            String bRaw = before != null ? nz(before.cells.get(srcCol)) : "";
            String aRaw = after != null ? nz(after.cells.get(col)) : "";

            String type = "";
            if (modified) {
                if (!bRaw.equals(aRaw)) {
                    type = bRaw.isEmpty() ? "add" : aRaw.isEmpty() ? "del" : "chg";
                    nChg++;
                } else if (!srcCol.equals(col) && !aRaw.isEmpty()) {
                    type = "mov";
                    nMov++;
                }
            }

            LinkedHashMap<String, Object> cell = new LinkedHashMap<>();
            cell.put("b", annotate(col, bRaw, codeMap));
            cell.put("a", annotate(col, aRaw, codeMap));
            cell.put("c", !type.isEmpty());
            if (!type.isEmpty()) {
                cell.put("t", type);
                if (!srcCol.equals(col) && !bRaw.isEmpty()) cell.put("f", srcCol);
            }
            cells.put(col, cell);
        }

        LinkedHashMap<String, Object> row = new LinkedHashMap<>();
        row.put("status", status);
        row.put("addr", primary.addr);
        row.put("noBefore", before != null ? before.no : "");
        row.put("noAfter", after != null ? after.no : "");
        if (modified) {
            row.put("nChg", nChg);
            row.put("nMov", nMov);
            row.put("shiftOnly", nChg == 0); // 값 변경 없이 열 위치만 달라진 행
        }
        // 라인 이력: 삭제행은 과거 버전 기준, 나머지는 최신 버전 기준
        row.put("hist", historyOf(after != null ? after : before, vers));
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

    /** PID 의 버전 헤더 (등록일 오름차순) */
    private static List<Ver> readHeaders(String pid) {
        List<Ver> vers = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBConnection.getConnection();
            pstmt = con.prepareStatement(HEADER_SQL);
            pstmt.setString(1, pid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Ver v = new Ver();
                v.houid = nz(rs.getString("HOUID"));
                v.version = nz(rs.getString("VERSION")).trim();
                v.regDate = nz(rs.getString("REG_DATE")).trim();
                v.test = "-1".equals(v.version);
                vers.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return vers;
    }

    /**
     * 비교할 두 버전 + 그 이전 이력 버전들의 모든 행을 한 번에 읽어 HOUID 별로 정규화.
     * (HOUID 별 행 순서는 DB 가 돌려준 순서 그대로 유지)
     */
    private static Map<String, List<Row>> readRows(String pid, String beforeOid, String afterOid) {
        Map<String, List<Row>> byVer = new HashMap<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBConnection.getConnection();
            pstmt = con.prepareStatement(SELECT_SQL);
            pstmt.setFetchSize(1000);
            pstmt.setString(1, pid);
            pstmt.setString(2, beforeOid);
            pstmt.setString(3, afterOid);
            pstmt.setString(4, pid);
            pstmt.setString(5, beforeOid);
            pstmt.setString(6, afterOid);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                List<Row> rows = byVer.computeIfAbsent(nz(rs.getString("HOUID")), k -> new ArrayList<>());
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
        return byVer;
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
