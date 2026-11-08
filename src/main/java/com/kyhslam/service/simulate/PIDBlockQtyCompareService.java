package com.kyhslam.service.simulate;

import com.kyhslam.MasterDataCache;
import com.kyhslam.dto.CodeInfoDTO;
import com.kyhslam.util.cop.CopUtil;
import com.kyhslam.util.pidSimulatorUp.PIDBlockQtyOverrideSimul;
import com.kyhslam.util.pidSimulatorUp.PIDBlockQtySimul;
import com.kyhslam.util.pidSimulatorUp.SimDb;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 일자별 블럭 수량 계산 비교 (화면 : thymeleaf/cop/bomBlockCalculator)
 *
 *  1) extract : 일자마다 공사값으로 수량 계산을 실행해, 실행된 PID 의 SPEC 열 중 영업사양 코드만 모은다 (일자 합집합).
 *  2) run     : 일자마다 (지정값이 있으면 적용해) 수량 계산을 실행한다.
 *  3) saveHistory : 일자별 실행 결과(PICK / 자재 / 수량 / 주석)를 PLM_SIMUL_HOGIBOM 에 이력으로 저장한다 (BATCHDATE = 일자).
 *
 * 일자 = 기준일 (yyyy-MM-dd, 모든 PID 를 기준일 이전 가장 가까운 버전으로 실행). 비우면 최신 버전.
 * extract / run 은 DB 에 쓰지 않는다 (pid.simul.errorlog=true 일 때 PID 오류 로그만 기존 시뮬레이터와 같이 저장).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PIDBlockQtyCompareService {

    /** 한 번에 비교할 수 있는 일자 수 */
    public static final int MAX_COLUMNS = 5;
    private static final int MAX_VALUES = 200;

    private final MasterDataCache masterDataCache;

    @Value("${pid.simul.errorlog:false}")
    private boolean saveErrorLog;

    /** 비교 열 1개 : 기준일 + 지정값 */
    public static class Column {
        public String date;
        public Map<String, String> overrides = new LinkedHashMap<>();
    }

    /** 영업사양 추출 (일자 합집합) */
    public Map<String, Object> extract(String project, String pid, String floor, String beforePids, List<String> dates,
                                       boolean withPickCmt) throws Exception {
        checkCount(dates.size());
        long start = System.currentTimeMillis();
        Map<String, String> dict = codeDictionary();

        // 코드 → 행 (처음 나온 일자 기준 공사값, 일자별 사용 여부)
        Map<String, Map<String, Object>> sales = new LinkedHashMap<>();
        Map<String, Map<String, Object>> others = new LinkedHashMap<>();
        List<Map<String, Object>> columns = new ArrayList<>();
        int total = 0;

        try (SimDb db = SimDb.open()) {
            for (int i = 0; i < dates.size(); i++) {
                String date = dates.get(i);
                Map<String, Object> col = new LinkedHashMap<>();
                col.put("date", date);
                try {
                    PIDBlockQtyOverrideSimul.Run run = PIDBlockQtyOverrideSimul.run(db, project, pid, floor, beforePids, date, null, true, withPickCmt, saveErrorLog);
                    col.put("success", true);
                    col.put("header", header(run.result));
                    col.put("specCount", run.specs.size());
                    col.put("warnings", run.result.warnings);
                    for (PIDBlockQtyOverrideSimul.SpecItem s : run.specs) {
                        String name = dict.get(s.code.toUpperCase());
                        if (name == null) {
                            Map<String, Object> o = others.computeIfAbsent(s.code, k -> {
                                Map<String, Object> m = new LinkedHashMap<>();
                                m.put("code", k);
                                m.put("where", s.pids.isEmpty() ? "" : s.pids.get(0));
                                return m;
                            });
                            continue;
                        }
                        Map<String, Object> m = sales.get(s.code);
                        if (m == null) {
                            m = new LinkedHashMap<>();
                            m.put("code", s.code);
                            m.put("name", name);
                            m.put("cur", s.baseValue);
                            m.put("inBase", s.inBase);
                            m.put("lines", s.lines);
                            m.put("pids", new ArrayList<>(s.pids));
                            m.put("cons", new ArrayList<>(s.cons));
                            m.put("values", codeValues(s.code));
                            m.put("used", new ArrayList<Integer>());
                            sales.put(s.code, m);
                        } else {
                            m.put("lines", Math.max((Integer) m.get("lines"), s.lines));
                            mergeList(m, "pids", s.pids);
                            mergeList(m, "cons", s.cons);
                        }
                        @SuppressWarnings("unchecked")
                        List<Integer> used = (List<Integer>) m.get("used");
                        used.add(i);
                    }
                } catch (Exception e) {
                    log.warn("[PIDBlockQtyCompare] extract {} / {} date={} 실패 : {}", project, pid, date, e.getMessage());
                    col.put("success", false);
                    col.put("message", message(e));
                }
                columns.add(col);
            }
        }
        total = sales.size() + others.size();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", columns.stream().anyMatch(c -> Boolean.TRUE.equals(c.get("success"))));
        if (!(Boolean) res.get("success"))
            res.put("message", columns.isEmpty() ? "일자가 없습니다." : String.valueOf(columns.get(0).get("message")));
        res.put("columns", columns);
        res.put("totalSpec", total);
        res.put("sales", new ArrayList<>(sales.values()));
        res.put("others", new ArrayList<>(others.values()));
        if (dict.isEmpty())
            res.put("dictEmpty", true);
        res.put("elapsedMillis", System.currentTimeMillis() - start);
        log.info("[PIDBlockQtyCompare] extract {} / {} 일자 {}개 : 영업사양 {}개, {}ms", project, pid, dates.size(), sales.size(), res.get("elapsedMillis"));
        return res;
    }

    /** 일자별 수량 계산 */
    public Map<String, Object> run(String project, String pid, String floor, String beforePids, List<Column> cols,
                                   boolean withPickCmt) throws Exception {
        checkCount(cols.size());
        long start = System.currentTimeMillis();
        List<Map<String, Object>> columns = new ArrayList<>();
        try (SimDb db = SimDb.open()) {
            for (Column c : cols) {
                long t = System.currentTimeMillis();
                Map<String, Object> col = new LinkedHashMap<>();
                col.put("date", c.date);
                try {
                    PIDBlockQtyOverrideSimul.Run run = PIDBlockQtyOverrideSimul.run(db, project, pid, floor, beforePids, c.date, c.overrides, false, withPickCmt, saveErrorLog);
                    List<Map<String, Object>> applied = new ArrayList<>();
                    for (Map.Entry<String, String> e : run.applied.entrySet()) {
                        Object orig = run.baseSpec.get(e.getKey());
                        Map<String, Object> a = new LinkedHashMap<>();
                        a.put("code", e.getKey());
                        a.put("from", orig == null ? "" : String.valueOf(orig));
                        a.put("to", e.getValue());
                        applied.add(a);
                    }
                    col.put("success", true);
                    col.put("applied", applied);
                    col.put("spec", historySpec(run));
                    col.put("result", run.result);
                } catch (Exception e) {
                    log.warn("[PIDBlockQtyCompare] run {} / {} date={} 실패 : {}", project, pid, c.date, e.getMessage());
                    col.put("success", false);
                    col.put("message", message(e));
                }
                col.put("elapsedMillis", System.currentTimeMillis() - t);
                columns.add(col);
            }
        }
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("columns", columns);
        res.put("withPickCmt", withPickCmt);
        res.put("elapsedMillis", System.currentTimeMillis() - start);
        log.info("[PIDBlockQtyCompare] run {} / {} 일자 {}개 완료 : {}ms", project, pid, cols.size(), res.get("elapsedMillis"));
        return res;
    }

    /** 이력 저장 1건 (일자 1개) */
    public static class HistoryColumn {
        public String batchDate;   // yyyyMMdd
        public String brand, speed, acapa;
        public List<HistoryItem> items = new ArrayList<>();
    }

    /** 이력 저장 PICK 1건 */
    public static class HistoryItem {
        public String pick, partNo, qty, cmt;
    }

    /** 일자별 실행 결과 이력 저장 (PLM_SIMUL_HOGIBOM). 같은 호기 / 블럭 / 일자의 기존 이력은 지우고 다시 넣는다 */
    public Map<String, Object> saveHistory(String hogi, String blockNo, List<HistoryColumn> cols) throws Exception {
        if (hogi.isEmpty())
            throw new IllegalArgumentException("호기번호가 없습니다.");
        if (blockNo.isEmpty())
            throw new IllegalArgumentException("블럭 PID 가 없습니다.");
        if (cols.isEmpty())
            throw new IllegalArgumentException("저장할 일자 결과가 없습니다.");
        Set<String> seen = new HashSet<>();
        for (HistoryColumn c : cols) {
            if (c.batchDate == null || !c.batchDate.matches("\\d{8}"))
                throw new IllegalArgumentException("일자 형식이 올바르지 않습니다 : " + c.batchDate);
            // HOGI + BLOCKNO + BATCHDATE 는 한 벌만 저장한다
            if (!seen.add(c.batchDate))
                throw new IllegalArgumentException("같은 일자(" + c.batchDate + ")가 여러 번 있습니다. 호기 / 블럭 / 일자별로 한 결과만 저장할 수 있습니다.");
        }
        // HOGI + BLOCKNO + BATCHDATE + PICK 도 한 건만 저장한다 (같은 PICK 이 또 나오면 처음 것만 남긴다)
        List<String> skipped = new ArrayList<>();
        for (HistoryColumn c : cols) {
            Map<String, HistoryItem> byPick = new LinkedHashMap<>();
            for (HistoryItem it : c.items) {
                if (it.pick == null || it.pick.isEmpty())
                    throw new IllegalArgumentException("PICK 이 비어 있는 결과가 있습니다. (일자 " + c.batchDate + ")");
                if (byPick.putIfAbsent(it.pick, it) != null)
                    skipped.add(c.batchDate + " " + it.pick);
            }
            c.items = new ArrayList<>(byPick.values());
        }
        if (!skipped.isEmpty())
            log.warn("[PIDBlockQtyCompare] saveHistory {} / {} 중복 PICK 제외 : {}", hogi, blockNo, skipped);
        int saved = CopUtil.saveHogiBomHistory(hogi, blockNo, cols);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("saved", saved);
        res.put("skipped", skipped);
        res.put("dates", cols.stream().map(c -> c.batchDate).distinct().toList());
        log.info("[PIDBlockQtyCompare] saveHistory {} / {} 일자 {}개 : {}건", hogi, blockNo, cols.size(), saved);
        return res;
    }

    // ------------------------------------------------------------------ 내부

    /** 이력 저장용 영업사양 (지정값 적용 후) : brand = EL_ABRAND, speed = EL_ASPD, acapa = EL_ACAPA */
    private static Map<String, String> historySpec(PIDBlockQtyOverrideSimul.Run run) {
        Map<String, Object> eff = new HashMap<>(run.baseSpec);
        eff.putAll(run.applied);
        Map<String, String> m = new LinkedHashMap<>();
        m.put("brand", specValue(eff, "EL_ABRAND"));
        m.put("speed", specValue(eff, "EL_ASPD"));
        m.put("acapa", specValue(eff, "EL_ACAPA"));
        return m;
    }

    private static String specValue(Map<String, Object> spec, String code) {
        Object v = spec.get(code);
        if (v == null)
            for (Map.Entry<String, Object> e : spec.entrySet())
                if (code.equalsIgnoreCase(e.getKey())) { v = e.getValue(); break; }
        return v == null ? "" : String.valueOf(v).trim();
    }

    private static void checkCount(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("비교할 일자를 1개 이상 입력하세요.");
        if (n > MAX_COLUMNS)
            throw new IllegalArgumentException("비교할 일자는 최대 " + MAX_COLUMNS + "개입니다.");
    }

    private static Map<String, Object> header(PIDBlockQtySimul.Result r) {
        Map<String, Object> h = new LinkedHashMap<>();
        h.put("type", r.type);
        h.put("projectNo", r.projectNo);
        h.put("projectTitle", r.projectTitle);
        h.put("blockPid", r.blockPid);
        h.put("blockPidName", r.blockPidName);
        h.put("elpPid", r.elpPid);
        h.put("runVersion", r.runVersion);
        h.put("asOfDate", r.asOfDate);
        h.put("floor", r.floor);
        h.put("pickCount", r.runs.size());
        return h;
    }

    @SuppressWarnings("unchecked")
    private static void mergeList(Map<String, Object> m, String key, List<String> add) {
        List<String> list = (List<String>) m.get(key);
        for (String s : add)
            if (!list.contains(s) && list.size() < 50)
                list.add(s);
    }

    private static String message(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    /** 특성코드(대문자) → 사양명 (육상 + 선박용) */
    private Map<String, String> codeDictionary() {
        Map<String, String> map = new HashMap<>();
        addCodes(map, masterDataCache.getCodeListV2());
        addCodes(map, masterDataCache.getShipCodeList());
        return map;
    }

    private static void addCodes(Map<String, String> map, List<CodeInfoDTO> list) {
        if (list == null)
            return;
        for (CodeInfoDTO d : list) {
            if (d.getCode() == null || d.getCode().trim().isEmpty())
                continue;
            map.putIfAbsent(d.getCode().trim().toUpperCase(), d.getCodeName() == null ? "" : d.getCodeName().trim());
        }
    }

    /** 특성코드의 특성값 목록 (입력칸 자동완성용 : 코드형 특성만) */
    private List<Map<String, String>> codeValues(String code) {
        List<CodeInfoDTO> list = masterDataCache.getCodeValues(false, code);
        if (list.isEmpty())
            list = masterDataCache.getCodeValues(true, code);
        List<Map<String, String>> values = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (CodeInfoDTO d : list) {
            if (d.getTypeVal() == null || !seen.add(d.getTypeVal()))
                continue;
            Map<String, String> v = new LinkedHashMap<>();
            v.put("v", d.getTypeVal());
            v.put("n", d.getTypeName() == null ? "" : d.getTypeName());
            values.add(v);
            if (values.size() >= MAX_VALUES)
                break;
        }
        return values;
    }
}
