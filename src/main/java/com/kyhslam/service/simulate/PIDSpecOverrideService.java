package com.kyhslam.service.simulate;

import com.kyhslam.MasterDataCache;
import com.kyhslam.dto.CodeInfoDTO;
import com.kyhslam.util.pidSimulatorUp.PIDSimulResult;
import com.kyhslam.util.pidSimulatorUp.PIDSpecOverrideSimul;
import com.kyhslam.util.pidSimulatorUp.SimDb;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 영업사양 지정 PID 실행 (화면 : thymeleaf/cop/bomCalculatorResult)
 *
 *  1) extract : 공사값 그대로 한 번 실행해서, 실행된 PID 의 SPEC 열 특성코드 중 영업사양 코드(특성코드 사전에 있는 것)만 뽑는다.
 *  2) run     : 공사값 실행 + 지정값 실행을 같은 DB 연결에서 차례로 하고 최종 OUTPUT 을 비교한다.
 *
 * DB 에는 쓰지 않는다 (pid.simul.errorlog=true 일 때 PID 오류 로그만 기존 시뮬레이터와 같이 저장).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PIDSpecOverrideService {

    private static final int MAX_VALUES = 200;

    private final MasterDataCache masterDataCache;

    @Value("${pid.simul.errorlog:false}")
    private boolean saveErrorLog;

    /** 영업사양 추출 */
    public Map<String, Object> extract(String project, String pid, String floor, Integer version, String beforePids,
                                       boolean includeSub) throws Exception {
        long start = System.currentTimeMillis();
        PIDSpecOverrideSimul.Run run;
        try (SimDb db = SimDb.open()) {
            run = PIDSpecOverrideSimul.run(db, project, pid, floor, version, beforePids, null, saveErrorLog);
        }
        PIDSimulResult r = run.result;
        Map<String, String> dict = codeDictionary();

        List<Map<String, Object>> sales = new ArrayList<>();
        List<Map<String, Object>> others = new ArrayList<>();
        List<PIDSpecOverrideSimul.SpecItem> specs = PIDSpecOverrideSimul.collectSpecs(run, includeSub);
        for (PIDSpecOverrideSimul.SpecItem s : specs) {
            String name = dict.get(s.code.toUpperCase());
            if (name == null) {
                Map<String, Object> o = new LinkedHashMap<>();
                o.put("code", s.code);
                o.put("where", s.firstPid);
                others.add(o);
                continue;
            }
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("code", s.code);
            m.put("name", name);
            m.put("cur", s.baseValue);
            m.put("inBase", s.inBase);
            m.put("lines", s.lines);
            m.put("sub", s.sub);
            m.put("subPids", s.subPids);
            m.put("cons", s.cons);
            m.put("values", codeValues(s.code));
            sales.add(m);
        }

        Map<String, Object> res = header(r);
        res.put("totalSpec", specs.size());
        res.put("sales", sales);
        res.put("others", others);
        res.put("includeSub", includeSub);
        res.put("subPidCount", r.subPids.size());
        res.put("javaMethod", r.javaMethod);
        res.put("warnings", r.warnings);
        res.put("elapsedMillis", System.currentTimeMillis() - start);
        if (dict.isEmpty())
            res.put("dictEmpty", true);

        log.info("[PIDSpecOverride] extract {} / {} v{} : SPEC {}개, 영업사양 {}개, {}ms", project, pid, r.runVersion,
                specs.size(), sales.size(), res.get("elapsedMillis"));
        return res;
    }

    /** 공사값 실행 + 지정값 실행 후 최종 OUTPUT 비교 */
    public Map<String, Object> run(String project, String pid, String floor, Integer version, String beforePids,
                                   Map<String, String> overrides) throws Exception {
        long start = System.currentTimeMillis();
        PIDSpecOverrideSimul.Run base, changed, latest;
        try (SimDb db = SimDb.open()) {
            base = PIDSpecOverrideSimul.run(db, project, pid, floor, version, beforePids, null, saveErrorLog);
            changed = PIDSpecOverrideSimul.run(db, project, pid, floor, version, beforePids, overrides, saveErrorLog);
            // 최신 버전 + 공사값 : 선택한 버전이 최신이면 공사값 실행과 같으므로 다시 실행하지 않는다
            boolean baseIsLatest = version == null || base.result.runVersion == base.result.latestVersion && !base.result.testVersion;
            latest = baseIsLatest ? base
                    : PIDSpecOverrideSimul.run(db, project, pid, floor, null, beforePids, null, saveErrorLog);
        }
        long elapsed = System.currentTimeMillis() - start;
        boolean latestSeparate = latest != base;

        // 최종 OUTPUT 비교 (최신 → 공사값 → 지정값 실행 KEY 순서)
        Map<String, Object> l = latest.result.resultMap, b = base.result.resultMap, c = changed.result.resultMap;
        Set<String> keys = new LinkedHashSet<>(l.keySet());
        keys.addAll(b.keySet());
        keys.addAll(c.keySet());
        List<Map<String, Object>> outputs = new ArrayList<>();
        int diff = 0, diffLatest = 0;
        for (String k : keys) {
            boolean d = differs(b, c, k);
            boolean dl = differs(l, c, k);
            if (d) diff++;
            if (dl) diffLatest++;
            Map<String, Object> o = new LinkedHashMap<>();
            o.put("key", k);
            o.put("latest", l.containsKey(k) ? text(l.get(k)) : null);
            o.put("base", b.containsKey(k) ? text(b.get(k)) : null);
            o.put("now", c.containsKey(k) ? text(c.get(k)) : null);
            o.put("diff", d);
            o.put("diffLatest", dl);
            outputs.add(o);
        }

        // 적용한 지정값 (원래 값 → 지정값)
        List<Map<String, Object>> applied = new ArrayList<>();
        for (Map.Entry<String, String> e : changed.applied.entrySet()) {
            Object orig = changed.baseSpec.get(e.getKey());
            Map<String, Object> a = new LinkedHashMap<>();
            a.put("code", e.getKey());
            a.put("from", orig == null ? "" : String.valueOf(orig));
            a.put("to", e.getValue());
            applied.add(a);
        }

        // 지정값 때문에 새로 실행된 PID 에서 쓰인 영업사양 (추출 때는 없던 것)
        Map<String, String> dict = codeDictionary();
        Set<String> baseCodes = new HashSet<>();
        for (PIDSpecOverrideSimul.SpecItem s : PIDSpecOverrideSimul.collectSpecs(base, true))
            baseCodes.add(s.code);
        List<Map<String, Object>> newSpecs = new ArrayList<>();
        for (PIDSpecOverrideSimul.SpecItem s : PIDSpecOverrideSimul.collectSpecs(changed, true)) {
            String name = dict.get(s.code.toUpperCase());
            if (name == null || baseCodes.contains(s.code))
                continue;
            Map<String, Object> n = new LinkedHashMap<>();
            n.put("code", s.code);
            n.put("name", name);
            n.put("cur", s.baseValue);
            n.put("where", s.firstPid);
            newSpecs.add(n);
        }

        Map<String, Object> res = header(changed.result);
        res.put("applied", applied);
        res.put("outputs", outputs);
        res.put("diffCount", diff);
        res.put("diffLatestCount", diffLatest);
        res.put("latestSeparate", latestSeparate);
        res.put("latestRunVersion", latest.result.runVersion);
        res.put("newSpecs", newSpecs);
        res.put("javaMethod", changed.result.javaMethod);
        res.put("maxSpecIdx", changed.result.maxSpecIdx);
        res.put("lines", changed.result.lines);
        res.put("baseLines", lineStates(base.result));
        res.put("subPidCount", changed.result.subPids.size());
        List<String> warnings = new ArrayList<>(changed.result.warnings);
        res.put("warnings", warnings);
        res.put("elapsedMillis", elapsed);

        log.info("[PIDSpecOverride] run {} / {} v{} : 지정값 {}건, OUTPUT {}개 중 공사값 대비 {}개 / 최신(v{}) 대비 {}개 다름, {}ms", project, pid,
                changed.result.runVersion, applied.size(), outputs.size(), diff, latest.result.runVersion, diffLatest, elapsed);
        return res;
    }

    // ------------------------------------------------------------------ 내부

    private static Map<String, Object> header(PIDSimulResult r) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("projectNo", r.projectNo);
        res.put("projectTitle", r.projectTitle);
        res.put("iOuid", r.iOuid);
        res.put("pid", r.pid);
        res.put("pidName", r.pidName);
        res.put("runVersion", r.runVersion);
        res.put("latestVersion", r.latestVersion);
        res.put("testVersion", r.testVersion);
        res.put("isFloorSpec", r.isFloorSpec);
        res.put("floor", r.floor);
        return res;
    }

    /** 공사값 실행의 라인별 상태 (라인 번호 → TRUE / FALSE / UNREAD) : 지정값 실행과 라인 결과 비교용 */
    private static List<String> lineStates(PIDSimulResult r) {
        List<String> list = new ArrayList<>();
        for (PIDSimulResult.Line l : r.lines)
            list.add(l.state == null ? "" : l.state.name());
        return list;
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

    /** 두 실행의 KEY 값이 다른지 (한쪽에만 있는 KEY 도 다름) */
    private static boolean differs(Map<String, Object> a, Map<String, Object> b, String k) {
        return a.containsKey(k) != b.containsKey(k) || !Objects.equals(text(a.get(k)), text(b.get(k)));
    }

    private static String text(Object v) {
        return v == null ? "" : String.valueOf(v);
    }
}
