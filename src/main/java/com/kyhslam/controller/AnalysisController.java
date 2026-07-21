package com.kyhslam.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.dto.CodeDTO;
import com.kyhslam.dto.PartWhere;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.service.SubaeService;
import com.kyhslam.util.ElvInfoCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AnalysisController {
    /**
     * PLM 기반 영업사양-제품 연계 분석(통계)
     */

    private final SubaeService subaeService;

    /**
     * 데이터분석 화면 (최신)
     * @return
     */
    @GetMapping("/dash/searchPartAnalysis")
    public String searchPartFinderTest() {
        //return "subaeLogic/searchPartFinder";
        return "thymeleaf/searchPartAnalysis";
    }

    @PostMapping("/dash/findCodeList")
    @ResponseBody
    @CrossOrigin
    public ArrayList<CodeDTO> findBrand(String typeName) {
        ArrayList<CodeDTO> result = ElvInfoCommonUtil.findCodeList(typeName);
        return result;
    }
    //조회결과 그래프/통계 팝업 화면
    @GetMapping("/dash/searchGraph")
    public String searchGraph() {

        return "thymeleaf/searchPartGraph";
    }

    @GetMapping("/dash/searchInteractive")
    public String searchInteractive() {

        return "thymeleaf/searchInteractive";
    }

    /**
     * 자재번호가 사용되고 있는 모든 제품 찾기
     *
     * @param whereCond – 검색 조건 (kvConditions 포함)
     * @return
     */
    @PostMapping("/dash/searchMissPartofProduct")
    @ResponseBody
    @CrossOrigin
    public ArrayList<HashMap<String, String>> searchMissPartofProduct(PartWhere whereCond) {
        log.info("searchMissPartofProduct whereCond={}", whereCond);

        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> opList = new ArrayList<>();
        ArrayList<String> valList = new ArrayList<>();


        /* ── 동적 K-V 조건 파싱 & 출력 ── */
        String kvJson = whereCond.getKvConditions();
        if (kvJson != null && !kvJson.isBlank()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, String>> kvList =
                        mapper.readValue(kvJson, new TypeReference<List<Map<String, String>>>() {});

                log.info("=== KV 조건 목록 (총 {}건) ===", kvList.size());
                for (int i = 0; i < kvList.size(); i++) {
                    Map<String, String> kv = kvList.get(i);
                    String key   = kv.getOrDefault("key",   "");
                    String op    = kv.getOrDefault("op",    "");
                    String value = kv.getOrDefault("value", "");
                    keyList.add(key);
                    opList.add(op);
                    valList.add(value);
                    log.info("  [{}] key={} | op={} | value={}", i + 1, key, op, value);
                }
                log.info("================================");
            } catch (Exception e) {
                log.warn("kvConditions 파싱 실패: {}", e.getMessage());
            }
        }

        //ArrayList<ProductDto> result = new ArrayList<>();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        result = subaeService.findPartOfProduct_v2(whereCond);
        return result;
    }

}
