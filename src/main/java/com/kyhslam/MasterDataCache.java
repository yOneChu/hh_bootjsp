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

    // 특성 CODE(대문자) -> 특성값 목록 (공사번호 추출 화면의 값 CODE 제안)
    private volatile Map<String, List<CodeInfoDTO>> elvValueIndex = Collections.emptyMap();

    private volatile Map<String, List<CodeInfoDTO>> shipValueIndex = Collections.emptyMap();

    private volatile HashMap<String, String> saleMap = new HashMap<>();

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

        List<CodeInfoDTO> list02 = MLBCommonUtil.getCodeListV2();
        this.codeElvList = Collections.unmodifiableList(new ArrayList<>(list02));
        log.info("사양값 팝업 특성코드 마스터 로딩 완료: {}건", codeElvList.size());

        List<CodeInfoDTO> list03 = MLBCommonUtil.getShipCodeList();
        this.codeShipList = Collections.unmodifiableList(new ArrayList<>(list03));
        log.info("선박용 특성코드 마스터 로딩 완료: {}건", codeShipList.size());

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

    public List<CodeInfoDTO> getCodeList() {
        return codeList;
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
