package com.kyhslam;

import com.kyhslam.dto.CodeInfoDTO;
import com.kyhslam.util.MLBCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class MasterDataCache {

    // volatile: 나중에 reload()로 교체해도 다른 스레드에서 바로 보이게 함
    private volatile List<CodeInfoDTO> codeList = Collections.emptyList();

    // 서버 기동이 끝난 뒤 1회 실행
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        load();
    }

    private void load() {
        List<CodeInfoDTO> list = MLBCommonUtil.getCodeList();
        this.codeList = Collections.unmodifiableList(new ArrayList<>(list));
        log.info("특성코드 마스터 로딩 완료: {}건", codeList.size());
    }

    public List<CodeInfoDTO> getCodeList() {
        return codeList;
    }

    // 운영 중 마스터가 바뀌었을 때 재시작 없이 다시 불러오기(선택)
    /*public synchronized void reload() {
        load();
    }*/
}
