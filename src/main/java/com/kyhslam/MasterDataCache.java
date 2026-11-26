package com.kyhslam;

import com.kyhslam.dto.CodeInfoDTO;
import com.kyhslam.util.ElvInfoCommonUtil;
import com.kyhslam.util.MLBCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MasterDataCache {

    // volatile: 나중에 reload()로 교체해도 다른 스레드에서 바로 보이게 함
    private volatile List<CodeInfoDTO> codeList = Collections.emptyList();

    private volatile List<CodeInfoDTO> codeElvList = Collections.emptyList();

    private volatile List<CodeInfoDTO> codeShipList = Collections.emptyList();

    private volatile List<CodeInfoDTO> codeShipValueList = Collections.emptyList();

    // 특성코드 사전 팝업 전용 (육상+선박, LEFT JOIN: 값 없는 특성도 포함)
    private volatile List<CodeInfoDTO> codeDictList = Collections.emptyList();

    // 특성 CODE(대문자) -> 특성값 목록 (공사번호 추출 화면의 값 CODE 제안)
    private volatile Map<String, List<CodeInfoDTO>> elvValueIndex = Collections.emptyMap();

    private volatile Map<String, List<CodeInfoDTO>> shipValueIndex = Collections.emptyMap();

    private volatile HashMap<String, String> saleMap = new HashMap<>();

    // 특성코드 목록 화면 전용: codeDictList 를 (구분, 특성 CODE) 단위로 묶은 것
    private volatile List<CodeGroup> codeGroupList = Collections.emptyList();

    /**
     * 특성코드 목록 화면용 묶음 (JSON 크기를 줄이려고 필드명을 짧게 둔다)
     * @param d 구분 E(육상) / S(선박)
     * @param c 특성 CODE
     * @param n 사양명
     * @param g 코드 분류명 (선택형이 아니면 빈 문자열)
     * @param v 특성값 목록, 각 항목은 {값 CODE, 값 설명}
     */
    public record CodeGroup(String d, String c, String n, String g, List<String[]> v) {}

    // 특성 CODE(대문자) -> 사양명 (육상 우선, 없으면 선박용. 로직 Viewer 의 SPEC 한글명)
    private volatile Map<String, String> specNameIndex = Collections.emptyMap();

    // 서버 기동이 끝난 뒤 1회 실행
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        load();
    }

    private void load() {
        List<CodeInfoDTO> list = MLBCommonUtil.getCodeList();
        this.codeList = Collections.unmodifiableList(new ArrayList<>(list));
        this.elvValueIndex = indexByCode(codeList);
        log.info("특성코드 마스터 로딩 완료: {}건", codeList.size());

        List<CodeInfoDTO> listDict = MLBCommonUtil.getCodeDictList();
        this.codeDictList = Collections.unmodifiableList(new ArrayList<>(listDict));
        log.info("특성코드 사전 마스터 로딩 완료: {}건", codeDictList.size());
        this.codeGroupList = groupDict(codeDictList);
        log.info("특성코드 목록 화면 묶음 완료: {}개 특성", codeGroupList.size());

        List<CodeInfoDTO> list02 = MLBCommonUtil.getCodeListV2();
        this.codeElvList = Collections.unmodifiableList(new ArrayList<>(list02));
        log.info("사양값 팝업 특성코드 마스터 로딩 완료: {}건", codeElvList.size());

        List<CodeInfoDTO> list03 = MLBCommonUtil.getShipCodeList();
        this.codeShipList = Collections.unmodifiableList(new ArrayList<>(list03));
        log.info("선박용 특성코드 마스터 로딩 완료: {}건", codeShipList.size());

        this.specNameIndex = indexSpecName(codeElvList, codeShipList);

        List<CodeInfoDTO> list04 = MLBCommonUtil.getShipCodeValueList();
        this.codeShipValueList = Collections.unmodifiableList(new ArrayList<>(list04));
        this.shipValueIndex = indexByCode(codeShipValueList);
        log.info("선박용 특성값 마스터 로딩 완료: {}건", codeShipValueList.size());

        HashMap<String, String> saleMap = ElvInfoCommonUtil.findCodeValueAsCache();
        this.saleMap = saleMap;
        log.info("SIMUL을 위한 영업사양 마스터 로딩 완료: {}건", saleMap.size());

    }

    /** 특성 CODE(대문자) 별로 특성값을 묶는다 */
    private static Map<String, List<CodeInfoDTO>> indexByCode(List<CodeInfoDTO> list) {
        Map<String, List<CodeInfoDTO>> index = new HashMap<>();
        for (CodeInfoDTO dto : list) {
            if (dto.getCode() == null || dto.getTypeVal() == null) continue;
            index.computeIfAbsent(dto.getCode().toUpperCase(), k -> new ArrayList<>()).add(dto);
        }
        index.replaceAll((k, v) -> Collections.unmodifiableList(v));
        return Collections.unmodifiableMap(index);
    }

    /** codeDictList(특성 x 값 행) -> 특성 단위 묶음. 구분(육상 먼저) -> CODE 순으로 정렬 */
    private static List<CodeGroup> groupDict(List<CodeInfoDTO> list) {
        Map<String, String[]> head = new LinkedHashMap<>();          // key -> {d, c, n, g}
        Map<String, List<String[]>> values = new HashMap<>();
        for (CodeInfoDTO dto : list) {
            if (dto.getCode() == null) continue;
            String code = dto.getCode().trim();
            if (code.isEmpty()) continue;
            String d = "SHIP".equals(dto.getDiv()) ? "S" : "E";
            String key = d + "|" + code.toUpperCase();
            head.computeIfAbsent(key, k -> new String[]{d, code, clean(dto.getCodeName()), clean(dto.getName())});
            List<String[]> vs = values.computeIfAbsent(key, k -> new ArrayList<>());
            String valCode = clean(dto.getTypeName());
            String valDes = clean(dto.getTypeVal());
            if (!valCode.isEmpty() || !valDes.isEmpty()) vs.add(new String[]{valCode, valDes});
        }
        List<CodeGroup> result = new ArrayList<>(head.size());
        head.forEach((key, h) -> {
            List<String[]> vs = values.get(key);
            vs.sort((a, b) -> a[0].compareToIgnoreCase(b[0]));
            result.add(new CodeGroup(h[0], h[1], h[2], h[3], Collections.unmodifiableList(vs)));
        });
        result.sort((a, b) -> {
            int c = a.d().compareTo(b.d());                          // E(육상) 먼저
            return c != 0 ? c : a.c().compareToIgnoreCase(b.c());
        });
        return Collections.unmodifiableList(result);
    }

    private static String clean(String s) {
        if (s == null) return "";
        String t = s.trim();
        return "null".equals(t) ? "" : t;
    }

    /** 특성 CODE(대문자) -> 사양명. 앞에 넘긴 목록이 우선 */
    @SafeVarargs
    private static Map<String, String> indexSpecName(List<CodeInfoDTO>... lists) {
        Map<String, String> index = new HashMap<>();
        for (List<CodeInfoDTO> list : lists) {
            for (CodeInfoDTO dto : list) {
                if (dto.getCode() == null || dto.getCodeName() == null) continue;
                String name = dto.getCodeName().trim();
                if (name.isEmpty() || "null".equals(name)) continue;
                index.putIfAbsent(dto.getCode().trim().toUpperCase(), name);
            }
        }
        return Collections.unmodifiableMap(index);
    }

    /** 특성 CODE 의 사양명 (DB 조회 없음, 없으면 빈 문자열) */
    public String getSpecName(String code) {
        if (code == null) return "";
        return specNameIndex.getOrDefault(code.trim().toUpperCase(), "");
    }

    public List<CodeInfoDTO> getCodeList() {
        return codeList;
    }

    /** 특성코드 사전 팝업 전용 (값 없는 특성 / 비선택형 특성 포함) */
    public List<CodeInfoDTO> getCodeDictList() {
        return codeDictList;
    }

    /** 특성코드 목록 화면 전용 (구분 x 특성 CODE 단위로 묶은 것, DB 조회 없음) */
    public List<CodeGroup> getCodeGroupList() {
        return codeGroupList;
    }

    public List<CodeInfoDTO> getCodeListV2() {
        return codeElvList;
    }

    /** 선박용 엘리베이터 특성코드 (CODE, CODENAME) */
    public List<CodeInfoDTO> getShipCodeList() {
        return codeShipList;
    }

    /**
     * 특성 CODE 의 특성값 목록 (코드형 특성 TYPE=24 만, 없으면 빈 목록)
     * @param ship true : 선박용 엘리베이터
     */
    public List<CodeInfoDTO> getCodeValues(boolean ship, String code) {
        if (code == null) return Collections.emptyList();
        Map<String, List<CodeInfoDTO>> index = ship ? shipValueIndex : elvValueIndex;
        return index.getOrDefault(code.toUpperCase(), Collections.emptyList());
    }

    public HashMap<String, String> getCodeMapV3() {
        return saleMap;
    }

    // 운영 중 마스터가 바뀌었을 때 재시작 없이 다시 불러오기(선택)
    /*public synchronized void reload() {
        load();
    }*/
}
