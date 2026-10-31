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
