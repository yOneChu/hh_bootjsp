package com.kyhslam.service;

import com.kyhslam.MasterDataCache;
import com.kyhslam.util.PLMDBConnection;
import com.kyhslam.util.PLMDBPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PID 관계 분석 (로직 Viewer 의 Maptify 화면)
 * <p>
 * 최신 버전 로직의 CALL 만 읽어 PID 호출 그래프를 만들고, 한 PID 를 기준으로
 * 이 PID 를 호출하는 PID(상위)와 이 PID 가 호출하는 PID(하위)를 돌려준다.
 * 로직을 실행하지 않는 정적 분석이라 조건과 무관하게 로직에 적힌 CALL 을 모두 포함한다. 조회 전용.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LogicRelationService {

    /** 호출 그래프를 메모리에 두는 시간 (전체 로직을 한 번 훑으므로 매 요청마다 읽지 않는다) */
    private static final long TTL_MS = 10 * 60 * 1000L;
    /** 한 방향(상위 / 하위)으로 돌려주는 최대 PID 수 */
    private static final int MAX_PIDS = 2000;
    private static final int COLS = 20;
    /** 계산식 등 PID 이름이 아닌 CALL 값은 제외 (시뮬레이터의 기준과 같다) */
    private static final Pattern PID_NAME = Pattern.compile("^[A-Za-z0-9_]+$");
    /** VAL / CON 계산식 안의 사양 참조 : {EL_XXX} */
    private static final Pattern SPEC_REF = Pattern.compile("\\{([^{}]+)\\}");

    private final MasterDataCache masterDataCache;

    /**
     * 서버 기동이 끝나면 호출 그래프와 KEY 선언 색인을 미리 읽어 둔다.
     * 첫 사용자가 관계 맵 / 사양 출처를 열 때 전체 로직을 훑느라 기다리지 않도록.
     * 기동을 늦추지 않게 별도 스레드에서 읽고, 실패해도 요청 때 다시 읽으므로 로그만 남긴다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmUp() {
        Thread t = new Thread(() -> {
            try {
                graph(false);
                keyIndex(false);
            } catch (Exception e) {
                log.warn("PID 관계 / KEY 색인 미리 읽기 실패 (첫 요청 때 다시 읽습니다): {}", e.getMessage());
            }
        }, "logic-relation-warmup");
        t.setDaemon(true);
        t.start();
    }

    private static final class Graph {
        final Map<String, Map<String, Object>> info = new HashMap<>();
        // 호출하는 PID -> (호출되는 PID -> CALL 라인 수), 로직에 적힌 순서
        final Map<String, Map<String, Integer>> down = new HashMap<>();
        final Map<String, Map<String, Integer>> up = new HashMap<>();
        long loadedAt;
    }

    private volatile Graph graph;

    /** 사양 출처 찾기 : KEY 로 값을 만드는 PID 색인 (KEY -> (PID -> 라인 수)). 호출 그래프와 따로, 필요할 때 읽는다 */
    private static final class KeyIndex {
        final Map<String, Map<String, Integer>> byKey = new HashMap<>();
        long loadedAt;
    }

    private volatile KeyIndex keyIndex;
    private final Object keyLock = new Object();
    /** 사양 하나에 돌려주는 선언 PID 최대 수 (흔한 KEY 는 수백 개 PID 가 쓰기도 한다) */
    private static final int MAX_DECL = 60;

    /**
     * 호출 그래프.
     *  - 처음(아직 없음) 또는 refresh : 지금 읽어서 돌려준다 (기다림)
     *  - TTL 이 지났으면 : 갖고 있던 그래프를 바로 돌려주고, 뒤에서 새로 읽어 바꿔 끼운다 (기다리지 않음)
     */
    private Graph graph(boolean refresh) {
        Graph g = graph;
        if (!refresh && g != null) {
            if (System.currentTimeMillis() - g.loadedAt >= TTL_MS)
                refreshInBackground(graphRefreshing, "logic-graph-refresh", () -> {
                    synchronized (this) {
                        graph = loadGraph();
                    }
                });
            return g;
        }
        synchronized (this) {
            g = graph;
            if (!refresh && g != null)
                return g;           // 기다리는 동안 다른 요청이 읽어 둠
            g = loadGraph();
            graph = g;
            return g;
        }
    }

    private final AtomicBoolean graphRefreshing = new AtomicBoolean();
    private final AtomicBoolean keyRefreshing = new AtomicBoolean();

    /** 이미 뒤에서 읽는 중이면 또 시작하지 않는다. 실패하면 갖고 있던 데이터를 그대로 쓰고 다음 요청 때 다시 시도 */
    private void refreshInBackground(AtomicBoolean running, String name, Runnable task) {
        if (!running.compareAndSet(false, true))
            return;
        Thread t = new Thread(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.warn("{} 실패 (이전 데이터를 계속 씁니다): {}", name, e.getMessage());
            } finally {
                running.set(false);
            }
        }, name);
        t.setDaemon(true);
        t.start();
    }

    private static String norm(String v) {
        return v == null ? "" : v.trim().toUpperCase();
    }

    private Graph loadGraph() {
        long started = System.currentTimeMillis();
        Graph g = new Graph();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBPool.getConnection();

            // 1. 최신 버전 PID 목록
            pstmt = con.prepareStatement("""
                    SELECT a.PID, a.NAME, a.METHOD, a.VERSION, a.ISFLOORSPEC
                    FROM variant_h a, variant_id b
                    WHERE a.HOUID = b.LAST_HOUID
                    """);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String pid = norm(rs.getString("PID"));
                if (pid.isEmpty())
                    continue;
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("pid", pid);
                m.put("name", nvl(rs.getString("NAME")));
                m.put("method", nvl(rs.getString("METHOD")));
                m.put("version", nvl(rs.getString("VERSION")));
                m.put("floorSpec", nvl(rs.getString("ISFLOORSPEC")));
                m.put("exists", true);
                g.info.put(pid, m);
            }
            rs.close();
            pstmt.close();

            // 2. 최신 버전 로직에서 CALL 이 있는 라인만
            StringBuilder cols = new StringBuilder();
            StringBuilder where = new StringBuilder();
            for (int i = 1; i <= COLS; i++) {
                cols.append(", d.KEY").append(i).append(", d.VAL").append(i);
                where.append(i == 1 ? "" : " OR ").append("TRIM(d.KEY").append(i).append(") = 'CALL'");
            }
            pstmt = con.prepareStatement(
                    "SELECT h.PID" + cols
                            + " FROM variant_d d, variant_h h, variant_id id"
                            + " WHERE h.HOUID = id.LAST_HOUID AND h.HOUID = d.HOUID"
                            + " AND (" + where + ")"
                            + " ORDER BY h.PID, d.DOUID");
            pstmt.setFetchSize(1000);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String from = norm(rs.getString("PID"));
                for (int i = 1; i <= COLS; i++) {
                    String key = rs.getString("KEY" + i);
                    if (key == null || !"CALL".equals(key.trim()))
                        continue;
                    String to = norm(rs.getString("VAL" + i));
                    if (to.isEmpty() || !PID_NAME.matcher(to).matches())
                        continue;
                    g.down.computeIfAbsent(from, k -> new LinkedHashMap<>()).merge(to, 1, Integer::sum);
                    g.up.computeIfAbsent(to, k -> new LinkedHashMap<>()).merge(from, 1, Integer::sum);
                }
            }
        } catch (Exception e) {
            log.error("PID 호출 그래프 로딩 실패", e);
            throw new IllegalStateException("PID 호출 관계를 읽지 못했습니다. (" + e.getMessage() + ")", e);
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        g.loadedAt = System.currentTimeMillis();
        log.info("PID 호출 그래프 로딩 완료: PID {}건, 호출하는 PID {}건, {}ms",
                g.info.size(), g.down.size(), g.loadedAt - started);
        return g;
    }

    /** KEY 선언 색인. 갱신 방식은 graph() 와 같다 (TTL 이 지나면 있던 색인을 돌려주고 뒤에서 새로 읽음) */
    private KeyIndex keyIndex(boolean refresh) {
        KeyIndex k = keyIndex;
        if (!refresh && k != null) {
            if (System.currentTimeMillis() - k.loadedAt >= TTL_MS)
                refreshInBackground(keyRefreshing, "logic-key-index-refresh", () -> {
                    synchronized (keyLock) {
                        keyIndex = loadKeyIndex();
                    }
                });
            return k;
        }
        synchronized (keyLock) {
            k = keyIndex;
            if (!refresh && k != null)
                return k;
            k = loadKeyIndex();
            keyIndex = k;
            return k;
        }
    }

    /** 최신 버전 로직의 KEY 칸을 모두 읽어 'KEY -> 그 KEY 에 값을 넣는 PID' 색인을 만든다 */
    private KeyIndex loadKeyIndex() {
        long started = System.currentTimeMillis();
        KeyIndex k = new KeyIndex();

        StringBuilder cols = new StringBuilder();
        StringBuilder where = new StringBuilder();
        for (int i = 1; i <= COLS; i++) {
            cols.append(", d.KEY").append(i);
            where.append(i == 1 ? "" : " OR ").append("d.KEY").append(i).append(" IS NOT NULL");
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBPool.getConnection();
            pstmt = con.prepareStatement(
                    "SELECT h.PID" + cols
                            + " FROM variant_d d, variant_h h, variant_id id"
                            + " WHERE h.HOUID = id.LAST_HOUID AND h.HOUID = d.HOUID"
                            + " AND (" + where + ")");
            pstmt.setFetchSize(2000);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String pid = norm(rs.getString("PID"));
                if (pid.isEmpty())
                    continue;
                for (int i = 1; i <= COLS; i++) {
                    String key = specCode(rs.getString("KEY" + i));
                    if (key == null || "CALL".equals(key) || !PID_NAME.matcher(key).matches())
                        continue;
                    k.byKey.computeIfAbsent(key, x -> new HashMap<>()).merge(pid, 1, Integer::sum);
                }
            }
        } catch (Exception e) {
            log.error("KEY 선언 색인 로딩 실패", e);
            throw new IllegalStateException("PID 의 KEY 선언을 읽지 못했습니다. (" + e.getMessage() + ")", e);
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        k.loadedAt = System.currentTimeMillis();
        log.info("KEY 선언 색인 로딩 완료: KEY {}건, {}ms", k.byKey.size(), k.loadedAt - started);
        return k;
    }

    /**
     * 사양(SPEC / {CODE}) 마다 값이 어디서 오는지 : 특성코드 마스터(영업사양)에 있는지,
     * 어떤 PID 가 KEY 로 값을 넣는지와 그 PID 가 기준 PID 의 상위 / 하위 / 무관 중 어디인지.
     * 로직을 실행하지 않는 정적 분석 (조건과 무관하게 로직에 적힌 KEY 를 모두 본다)
     */
    public Map<String, Object> specOrigin(String pidParam, List<String> codes, boolean refresh) {
        String root = norm(pidParam);
        Graph g = graph(refresh);
        KeyIndex ki = keyIndex(refresh);

        Map<String, String> downParent = new HashMap<>();
        Map<String, Integer> downDepth = depths(root, g.down, downParent);
        Map<String, Integer> upDepth = depths(root, g.up, new HashMap<>());

        List<Map<String, Object>> list = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (String raw : codes) {
            String code = specCode(raw);
            if (code == null || !PID_NAME.matcher(code).matches() || !seen.add(code))
                continue;
            String name = masterDataCache.getSpecName(code);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("code", code);
            m.put("name", name);
            m.put("master", !name.isEmpty());

            List<Map<String, Object>> decl = new ArrayList<>();
            for (Map.Entry<String, Integer> e : ki.byKey.getOrDefault(code, Collections.emptyMap()).entrySet()) {
                String p = e.getKey();
                Map<String, Object> d = new LinkedHashMap<>();
                d.put("pid", p);
                d.put("name", nodeInfo(g, p).get("name"));
                d.put("count", e.getValue());
                if (p.equals(root)) {
                    d.put("rel", "self");
                    d.put("depth", 0);
                } else if (downDepth.containsKey(p)) {
                    d.put("rel", "down");
                    d.put("depth", downDepth.get(p));
                    // 기준 PID 가 직접 CALL 하는 PID 중 이 PID 로 이어지는 것 (화면에서 그 CALL 라인을 찾는다)
                    String via = p;
                    while (downParent.containsKey(via) && !root.equals(downParent.get(via)))
                        via = downParent.get(via);
                    d.put("via", via);
                } else if (upDepth.containsKey(p)) {
                    d.put("rel", "up");
                    d.put("depth", upDepth.get(p));
                } else {
                    d.put("rel", "other");
                    d.put("depth", 0);
                }
                decl.add(d);
            }
            decl.sort((a, b) -> {
                int c = Integer.compare(relRank((String) a.get("rel")), relRank((String) b.get("rel")));
                if (c != 0) return c;
                c = Integer.compare((int) a.get("depth"), (int) b.get("depth"));
                return c != 0 ? c : ((String) a.get("pid")).compareTo((String) b.get("pid"));
            });
            m.put("declaredTotal", decl.size());
            m.put("declared", decl.size() > MAX_DECL ? new ArrayList<>(decl.subList(0, MAX_DECL)) : decl);
            list.add(m);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("root", root);
        result.put("found", g.info.containsKey(root));
        result.put("loadedAt", Math.min(g.loadedAt, ki.loadedAt));
        result.put("codes", list);
        return result;
    }

    private static int relRank(String rel) {
        return switch (rel) {
            case "self" -> 0;
            case "up" -> 1;
            case "down" -> 2;
            default -> 3;
        };
    }

    /** 기준에서 한 방향으로 너비 우선 탐색한 단계 (기준 제외). parent 에 처음 만난 부모를 담는다 */
    private static Map<String, Integer> depths(String root, Map<String, Map<String, Integer>> adj, Map<String, String> parent) {
        Map<String, Integer> depth = new LinkedHashMap<>();
        depth.put(root, 0);
        ArrayDeque<String> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            for (String other : adj.getOrDefault(cur, Collections.emptyMap()).keySet()) {
                if (depth.containsKey(other))
                    continue;
                depth.put(other, depth.get(cur) + 1);
                parent.put(other, cur);
                queue.add(other);
            }
        }
        depth.remove(root);
        return depth;
    }

    private static String nvl(String v) {
        return v == null ? "" : v.trim();
    }

    /**
     * 한 PID 를 기준으로 한 상위 / 하위 호출 관계
     * @param refresh true : 메모리에 둔 그래프를 버리고 DB 에서 다시 읽는다
     */
    public Map<String, Object> relation(String pidParam, boolean refresh) {
        String root = norm(pidParam);
        Graph g = graph(refresh);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("root", root);
        result.put("loadedAt", g.loadedAt);
        result.put("found", g.info.containsKey(root));

        Set<String> pids = new LinkedHashSet<>();
        pids.add(root);
        List<Map<String, Object>> downEdges = new ArrayList<>();
        List<Map<String, Object>> upEdges = new ArrayList<>();
        boolean truncated = closure(root, g.down, true, pids, downEdges);
        truncated |= closure(root, g.up, false, pids, upEdges);

        Map<String, Object> nodes = new LinkedHashMap<>();
        for (String pid : pids)
            nodes.put(pid, nodeInfo(g, pid));

        result.put("nodes", nodes);
        result.put("down", downEdges);
        result.put("up", upEdges);
        result.put("truncated", truncated);
        return result;
    }

    /** 너비 우선으로 따라가며 간선을 모은다. 간선은 항상 {from: 호출하는 PID, to: 호출되는 PID} */
    private static boolean closure(String root, Map<String, Map<String, Integer>> adj, boolean isDown,
                                   Set<String> pids, List<Map<String, Object>> edges) {
        Set<String> seen = new LinkedHashSet<>();
        seen.add(root);
        ArrayDeque<String> queue = new ArrayDeque<>();
        queue.add(root);
        boolean truncated = false;
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            for (Map.Entry<String, Integer> e : adj.getOrDefault(cur, Collections.emptyMap()).entrySet()) {
                String other = e.getKey();
                if (!seen.contains(other)) {
                    if (seen.size() > MAX_PIDS) {
                        truncated = true;
                        continue;
                    }
                    seen.add(other);
                    queue.add(other);
                }
                Map<String, Object> edge = new LinkedHashMap<>();
                edge.put("from", isDown ? cur : other);
                edge.put("to", isDown ? other : cur);
                edge.put("count", e.getValue());
                edges.add(edge);
            }
        }
        pids.addAll(seen);
        return truncated;
    }

    private static Map<String, Object> nodeInfo(Graph g, String pid) {
        Map<String, Object> m = g.info.get(pid);
        if (m == null) {
            // 로직에 CALL 은 있지만 최신 버전 PID 목록에 없는 대상
            m = new LinkedHashMap<>();
            m.put("pid", pid);
            m.put("name", "");
            m.put("exists", false);
        }
        return m;
    }

    /** 한 PID 의 상세 : 직접 호출 관계, 로직에서 쓰는 사양, 만드는 결과 KEY */
    public Map<String, Object> detail(String pidParam) {
        String pid = norm(pidParam);
        Graph g = graph(false);

        Map<String, Object> result = new LinkedHashMap<>(nodeInfo(g, pid));
        result.put("callers", neighbors(g, g.up.get(pid)));
        result.put("callees", neighbors(g, g.down.get(pid)));

        // 사양 코드 -> [조건(SPEC)에 쓴 횟수, 계산식({CODE})에서 참조한 횟수]
        Map<String, int[]> specs = new LinkedHashMap<>();
        Map<String, Integer> keys = new LinkedHashMap<>();
        int lines = 0;

        StringBuilder cols = new StringBuilder("d.DOUID");
        for (int i = 1; i <= COLS; i++)
            cols.append(", d.SPEC").append(i).append(", d.CON").append(i)
                    .append(", d.KEY").append(i).append(", d.VAL").append(i);

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBPool.getConnection();
            pstmt = con.prepareStatement(
                    "SELECT " + cols
                            + " FROM variant_d d, variant_h h, variant_id id"
                            + " WHERE h.HOUID = id.LAST_HOUID AND h.HOUID = d.HOUID AND h.PID = ?"
                            + " ORDER BY d.DOUID");
            pstmt.setString(1, pid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                lines++;
                for (int i = 1; i <= COLS; i++) {
                    String spec = specCode(rs.getString("SPEC" + i));
                    if (spec != null)
                        specs.computeIfAbsent(spec, k -> new int[2])[0]++;
                    countRefs(rs.getString("CON" + i), specs);
                    countRefs(rs.getString("VAL" + i), specs);

                    String key = nvl(rs.getString("KEY" + i));
                    if (!key.isEmpty() && !"-".equals(key) && !"CALL".equals(key))
                        keys.merge(key, 1, Integer::sum);
                }
            }
        } catch (Exception e) {
            log.error("PID 상세 조회 실패: {}", pid, e);
            throw new IllegalStateException("PID 로직을 읽지 못했습니다. (" + e.getMessage() + ")", e);
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        List<Map<String, Object>> specList = new ArrayList<>();
        for (Map.Entry<String, int[]> e : specs.entrySet()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("code", e.getKey());
            m.put("name", masterDataCache.getSpecName(e.getKey()));
            m.put("cond", e.getValue()[0]);
            m.put("ref", e.getValue()[1]);
            specList.add(m);
        }
        specList.sort((a, b) -> Integer.compare(
                (int) b.get("cond") + (int) b.get("ref"), (int) a.get("cond") + (int) a.get("ref")));

        List<Map<String, Object>> keyList = new ArrayList<>();
        for (Map.Entry<String, Integer> e : keys.entrySet()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("key", e.getKey());
            m.put("count", e.getValue());
            keyList.add(m);
        }

        result.put("lines", lines);
        result.put("specs", specList);
        result.put("keys", keyList);
        return result;
    }

    private static List<Map<String, Object>> neighbors(Graph g, Map<String, Integer> adj) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (adj == null)
            return list;
        for (Map.Entry<String, Integer> e : adj.entrySet()) {
            Map<String, Object> info = nodeInfo(g, e.getKey());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("pid", e.getKey());
            m.put("name", info.get("name"));
            m.put("exists", info.get("exists"));
            m.put("count", e.getValue());
            list.add(m);
        }
        return list;
    }

    /** 사양 코드로 정리 (XXX_CODE_NAME -> XXX). 빈 값이면 null */
    private static String specCode(String raw) {
        String code = norm(raw);
        if (code.isEmpty() || "-".equals(code))
            return null;
        if (code.endsWith("_CODE_NAME") && code.length() > 10)
            code = code.substring(0, code.length() - 10);
        return code;
    }

    private static void countRefs(String text, Map<String, int[]> specs) {
        if (text == null || text.indexOf('{') < 0)
            return;
        Matcher m = SPEC_REF.matcher(text);
        while (m.find()) {
            String code = specCode(m.group(1));
            if (code != null && PID_NAME.matcher(code).matches())
                specs.computeIfAbsent(code, k -> new int[2])[1]++;
        }
    }
}
