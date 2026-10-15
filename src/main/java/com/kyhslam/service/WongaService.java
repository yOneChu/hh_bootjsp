package com.kyhslam.service;

import com.kyhslam.util.wonga.DrwNumberSearchCondition;
import com.kyhslam.util.wonga.DrwNumberSearchResult;
import com.kyhslam.util.wonga.DrwNumberSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 원가절감 실적조회 서비스
 * (원본: util.wonga.wongaMain / WongaWebServer)
 *
 * 화면(searchWonga.html)에서 넘어온 입력값으로 조회를 실행하고,
 * 화면에 그릴 값(건수, 컬럼, 행 데이터, 안내 메시지)을 만들어 돌려준다.
 */
@Slf4j
@Service
public class WongaService {

    /** DB 값이 null 인 칸에 화면/엑셀에서 대신 보여줄 문자 (원본 화면과 동일) */
    private static final String NULL_MARK = "*";

    /**
     * 조회 실행
     *
     * 1) 화면 입력값(form) -> 조회 조건(DrwNumberSearchCondition) 으로 바꾸고
     * 2) DrwNumberSearchService 로 실제 DB 조회를 한 뒤 (최대 300초)
     * 3) 화면에 넘길 값을 Map 으로 담아 돌려준다.
     *
     * 돌려주는 KEY
     *   searched : 항상 true (검색을 한 번이라도 했는지 화면에서 구분)
     *   message  : 결과 대신 보여줄 안내 문구 (입력 오류, 타임아웃, 0건, 10,000건 초과)
     *   cnt      : 조회 건수
     *   mengeSum : 총 자재수량
     *   head     : 컬럼명 목록
     *   rows     : 행 데이터 (null 은 "*" 로 바꿈)
     */
    public Map<String, Object> search(Map<String, String> form) {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("searched", true);

        DrwNumberSearchCondition cond = toCondition(form);
        log.info("원가절감 실적조회 조건 : {}", cond);

        DrwNumberSearchResult result;
        try {
            // validate() 에서 걸리면 IllegalArgumentException, 300초를 넘기면 TimeoutException
            result = DrwNumberSearchService.search(cond);
        } catch (IllegalArgumentException e) {
            model.put("message", e.getMessage());
            return model;
        } catch (TimeoutException e) {
            model.put("message", "실행 중 타임아웃으로 전체 종료 되었습니다. 조회 조건 추가 또는 기간을 줄인 후 재 실행 부탁드립니다.");
            return model;
        } catch (Exception e) {
            log.error("원가절감 실적조회 실패", e);
            model.put("message", "조회 중 오류가 발생하였습니다. : " + e.getMessage());
            return model;
        }

        int cnt = result.size();
        log.info("원가절감 실적조회 건수 : {}", cnt);

        if (cnt == 0) {
            model.put("message", "데이터가 없습니다.");
        } else if (cnt >= DrwNumberSearchResult.MAX_ROWS) {
            // 너무 많으면 화면이 멈추므로 결과를 버리고 조건을 좁히도록 안내
            model.put("message", "최대로 불러올 수 있는 데이터 수를 초과하였습니다. 조회 조건 추가 또는 기간을 줄인 후 재 실행 부탁드립니다. (한번에 조회 가능한 최대 데이터 수 : 10,000건)");
        } else {
            model.put("cnt", cnt);
            model.put("mengeSum", result.mengeSum);
            model.put("head", result.head);
            model.put("rows", replaceNull(result.data));
        }
        return model;
    }

    /**
     * 화면 입력값(form)을 조회 조건 객체로 바꾼다.
     * KEY 는 searchWonga.html 의 input name 과 같다.
     * 체크박스는 체크했을 때만 값이 넘어오므로 KEY 가 있는지로 판단한다.
     */
    public DrwNumberSearchCondition toCondition(Map<String, String> form) {

        DrwNumberSearchCondition cond = new DrwNumberSearchCondition();
        cond.gLCode = value(form, "g_l_code");
        cond.chkDetail = form.containsKey("chk_detail");
        cond.elAtyp = value(form, "EL_ATYP");
        cond.elEcsf = value(form, "EL_ECSF");
        cond.elEtm = value(form, "EL_ETM");
        cond.elAspd1 = value(form, "EL_ASPD_1");
        cond.elAspd2 = value(form, "EL_ASPD_2");
        cond.elAman1 = value(form, "EL_AMAN_1");
        cond.elAman2 = value(form, "EL_AMAN_2");
        cond.elEcjj1 = value(form, "EL_ECJJ_1");
        cond.elEcjj2 = value(form, "EL_ECJJ_2");
        cond.partNo = value(form, "PARTNO");
        cond.blockNo = value(form, "BLOCKNO");
        cond.specList = value(form, "specList");   // 비어 있으면 normalize() 에서 기본 추출데이터 사용
        cond.startDate = value(form, "start_date_day");
        cond.endDate = value(form, "end_date_day");
        cond.chkGeneral = form.containsKey("chk_general");
        cond.chkBom = form.containsKey("chk_bom");
        return cond;
    }

    /**
     * 화면에 다시 채워 넣을 입력값.
     * 추출데이터(specList)가 없으면 기본 목록을 넣어 준다. (처음 화면 진입 시)
     */
    public Map<String, String> toForm(Map<String, String> form) {

        Map<String, String> result = form == null ? new HashMap<String, String>() : new HashMap<String, String>(form);
        if (!result.containsKey("specList")) {
            result.put("specList", DrwNumberSearchCondition.DEFAULT_SPEC_LIST);
        }
        return result;
    }

    /** null 칸을 "*" 로 바꾼 행 데이터 (원본 화면과 동일한 표시) */
    private static List<List<String>> replaceNull(List<List<String>> data) {

        List<List<String>> rows = new ArrayList<List<String>>(data.size());
        for (List<String> row : data) {
            List<String> cells = new ArrayList<String>(row.size());
            for (String col : row) {
                cells.add(col == null ? NULL_MARK : col);
            }
            rows.add(cells);
        }
        return rows;
    }

    private static String value(Map<String, String> form, String key) {
        String v = form.get(key);
        return v == null ? "" : v;
    }
}
