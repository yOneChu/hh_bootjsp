package com.kyhslam.service.cop;


import com.kyhslam.service.OneCycleFunc;
import com.kyhslam.util.cop.CopUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CopService {

    private final OneCycleFunc oneCycleFunc;

    public ArrayList<HashMap<String, String>> findHogi() {
        return CopUtil.findHogi();
    }

    /**
     * COP 배치 : findHogi 로 조회한 각 호기에 대해
     * WIP 생성 -> 동일정보 생성 -> 속성값 변경(EL_ZFDA~D = 오늘) -> 종속사양 산출 -> BOM 계산
     * 을 수행하고, 성공하면 새로 만들어진 호기를 PLM_SIMUL_HOGI 에 저장한다.
     */
    //@Scheduled(cron = "0 31 23 * * 1-5")
    public List<OneCycleFunc.OneCycleResult> runBatch() {

        List<OneCycleFunc.OneCycleResult> results = new ArrayList<>();
        Map<String, String> attrs = designDateAttrs();

        for (HashMap<String, String> row : findHogi()) {

            String hogi = row.get("HOGI");
            if (hogi == null || hogi.isBlank()) {
                continue;
            }

            OneCycleFunc.OneCycleResult result = oneCycleFunc.runEqualInfoCycle(hogi, attrs);
            results.add(result);

            System.out.println("[COP 배치] " + hogi + " -> " + result.getProductNo()
                    + ", success = " + result.isSuccess() + ", message = " + result.getMessage());

            if (result.isSuccess() && result.getProductNo() != null && !result.getProductNo().isBlank()) {
                CopUtil.insertSimulHogi(result.getProductNo(), hogi,
                        row.get("BRAND"), row.get("SPEED"), row.get("ACAPA"));
            }
        }
        return results;
    }

    /** 관리대상 호기 전체 조회 */
    public List<HashMap<String, String>> findManagedHogi() throws Exception {
        return CopUtil.findManagedHogi();
    }

    /**
     * 관리대상 호기 저장
     * mode = create : 등록 (이미 있는 호기면 거절), update : 수정 (없는 호기면 거절)
     * 입력이 잘못되면 IllegalArgumentException (메시지는 화면에 그대로 보여준다)
     */
    public void saveManagedHogi(Map<String, String> req) throws Exception {

        Map<String, String> hogi = new HashMap<>();
        hogi.put("HOGI", clean(req.get("HOGI"), "호기", 50).toUpperCase());
        hogi.put("BEFOREHOGI", clean(req.get("BEFOREHOGI"), "이전호기", 50).toUpperCase());
        hogi.put("BRAND", clean(req.get("BRAND"), "브랜드", 100));
        hogi.put("ACAPA", clean(req.get("ACAPA"), "용량", 100));
        hogi.put("SPEED", clean(req.get("SPEED"), "속도", 100));
        hogi.put("BATCHDATE", clean(req.get("BATCHDATE"), "배치 날짜", 50));

        String no = hogi.get("HOGI");
        if (no.isEmpty()) {
            throw new IllegalArgumentException("호기를 입력해 주세요.");
        }
        if (no.equals(hogi.get("BEFOREHOGI"))) {
            throw new IllegalArgumentException("이전호기는 호기와 달라야 합니다.");
        }

        if ("create".equals(req.get("mode"))) {
            if (!CopUtil.insertManagedHogi(hogi)) {
                throw new IllegalArgumentException(no + " 호기는 이미 등록되어 있습니다.");
            }
        } else if ("update".equals(req.get("mode"))) {
            if (!CopUtil.updateManagedHogi(hogi)) {
                throw new IllegalArgumentException(no + " 호기를 찾을 수 없습니다. 목록을 새로고침해 주세요.");
            }
        } else {
            throw new IllegalArgumentException("저장 방식이 올바르지 않습니다.");
        }
    }

    /** 관리대상 호기 삭제. 삭제된 건수를 돌려준다 */
    public int deleteManagedHogi(List<String> hogis) throws Exception {
        if (hogis == null || hogis.isEmpty()) {
            throw new IllegalArgumentException("삭제할 호기를 선택해 주세요.");
        }
        return CopUtil.deleteManagedHogi(hogis);
    }

    private static final int HISTORY_MAX_ROWS = 200_000;
    private static final int HISTORY_MAX_DAYS = 366;

    /**
     * BOM 이상감지 대시보드 데이터 (PLM_SIMUL_HOGIBOM 일자별 실행결과 이력)
     * from / to : yyyyMMdd (비우면 오늘 기준 최근 30일). 행은 배열로 보내 응답 크기를 줄인다
     *   columns : HOGI, BLOCKNO, BRAND, ATYP, SPEED, ACAPA, PICK, PARTNO, QTY, CMT, BATCHDATE, PARTNAME(블럭 명칭)
     */
    public Map<String, Object> findBomHistory(String from, String to) throws Exception {
        DateTimeFormatter f = DateTimeFormatter.BASIC_ISO_DATE;
        LocalDate end = parseYmd(to, LocalDate.now());
        LocalDate start = parseYmd(from, end.minusDays(30));
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("시작일이 종료일보다 늦습니다.");
        }
        if (start.plusDays(HISTORY_MAX_DAYS).isBefore(end)) {
            throw new IllegalArgumentException("조회 기간은 최대 " + HISTORY_MAX_DAYS + "일입니다.");
        }

        List<String[]> rows = CopUtil.findHogiBomHistory(start.format(f), end.format(f), HISTORY_MAX_ROWS);
        boolean truncated = rows.size() > HISTORY_MAX_ROWS;
        if (truncated) {
            rows = rows.subList(0, HISTORY_MAX_ROWS);
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("from", start.format(f));
        res.put("to", end.format(f));
        res.put("columns", List.of("HOGI", "BLOCKNO", "BRAND", "ATYP", "SPEED", "ACAPA", "PICK", "PARTNO", "QTY", "CMT", "BATCHDATE", "PARTNAME"));
        res.put("rows", rows);
        res.put("truncated", truncated);
        return res;
    }

    private LocalDate parseYmd(String value, LocalDate fallback) {
        String v = value == null ? "" : value.replace("-", "").trim();
        if (v.isEmpty()) {
            return fallback;
        }
        if (!v.matches("\\d{8}")) {
            throw new IllegalArgumentException("일자 형식이 올바르지 않습니다 : " + value);
        }
        try {
            return LocalDate.parse(v, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (Exception e) {
            throw new IllegalArgumentException("일자 형식이 올바르지 않습니다 : " + value);
        }
    }

    private String clean(String value, String label, int maxLength) {
        String text = value == null ? "" : value.trim();
        if (text.length() > maxLength) {
            throw new IllegalArgumentException(label + "는 " + maxLength + "자 이내로 입력해 주세요.");
        }
        return text;
    }

    /** 최초설계일(EL_ZFDA ~ EL_ZFDD) 을 모두 오늘(yyyyMMdd) 로 */
    private Map<String, String> designDateAttrs() {

        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        Map<String, String> attrs = new LinkedHashMap<>();
        attrs.put("EL_ZFDA", today);
        attrs.put("EL_ZFDB", today);
        attrs.put("EL_ZFDC", today);
        attrs.put("EL_ZFDD", today);
        return attrs;
    }

}
