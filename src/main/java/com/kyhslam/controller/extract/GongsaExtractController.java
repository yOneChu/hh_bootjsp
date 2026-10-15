package com.kyhslam.controller.extract;

import com.kyhslam.MasterDataCache;
import com.kyhslam.dto.CodeInfoDTO;
import com.kyhslam.util.gongsaExtract.GongSaCsvWriter;
import com.kyhslam.util.gongsaExtract.GongSaExtractResult;
import com.kyhslam.util.gongsaExtract.GongSaExtractService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 공사번호 추출
 * (원본: GongSaExtractMapMain)
 *
 * 파라미터 KEY (대소문자 구분 안 함)
 *   tableType     : elv(기본값) / shipelv
 *   startdate     : 등록일 시작 (필수, yyyy-MM-dd 또는 yyyyMMdd)
 *   enddate       : 등록일 종료 (필수)
 *   spec0 ~ spec9 : 영업사양 특성 CODE
 *   cond0 ~ cond9 : 영업사양 값 CODE (비우면 조건 없이 컬럼만 조회)
 *   locale        : MSR 타이틀 locale (기본 ko)
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class GongsaExtractController {

    private final GongSaExtractService gongSaExtractService;
    private final MasterDataCache masterDataCache;

    /**
     * 공사번호 추출 화면
     */
    @GetMapping("/extract/gongsa")
    public String gongsaExtract() {
        return "thymeleaf/gongsa/gongsaExtract";
    }

    /**
     * 특성 CODE 검색 목록 (서버 기동 시 MasterDataCache 에 담아 둔 목록, DB 조회 없음)
     * 예) /extract/gongsa/specs?tableType=shipelv -> [{ "code": "EL_ETM", "name": "권상기" }, ...]
     */
    @GetMapping("/extract/gongsa/specs")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> specs(@RequestParam(defaultValue = "elv") String tableType) {
        List<CodeInfoDTO> codes = "shipelv".equals(tableType)
                ? masterDataCache.getShipCodeList()
                : masterDataCache.getCodeListV2();

        List<Map<String, String>> list = new ArrayList<>(codes.size());
        for (CodeInfoDTO dto : codes) {
            if (dto.getCode() == null) continue;
            Map<String, String> m = new HashMap<>();
            m.put("code", dto.getCode());
            m.put("name", dto.getCodeName() == null ? "" : dto.getCodeName());
            list.add(m);
        }
        // 캐시는 서버 재기동 때만 바뀌므로 브라우저도 잠시 재사용
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES)).body(list);
    }

    /**
     * 값 CODE 제안 목록 (코드형 특성만, MasterDataCache 사용)
     * 조건은 doscoditm.DES 와 비교하므로 value 는 DES(typeVal), name 은 doscoditm.NAME(typeName)
     * 예) /extract/gongsa/specValues?tableType=elv&spec=EL_ETM -> [{ "value": "YJ140", "name": "..." }, ...]
     */
    @GetMapping("/extract/gongsa/specValues")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> specValues(@RequestParam(defaultValue = "elv") String tableType,
                                                                @RequestParam String spec) {
        List<CodeInfoDTO> values = masterDataCache.getCodeValues("shipelv".equals(tableType), spec);

        List<Map<String, String>> list = new ArrayList<>(values.size());
        Set<String> seen = new HashSet<>();
        for (CodeInfoDTO dto : values) {
            if (!seen.add(dto.getTypeVal())) continue;
            Map<String, String> m = new HashMap<>();
            m.put("value", dto.getTypeVal());
            m.put("name", dto.getTypeName() == null ? "" : dto.getTypeName());
            list.add(m);
        }
        list.sort(Comparator.comparing(m -> m.get("value")));
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES)).body(list);
    }

    /**
     * 공사번호 추출 조회
     * 예) /extract/gongsa/search?tableType=shipelv&startdate=2025-10-15&enddate=2026-10-31&spec0=EL_ETM&cond0=YJ140
     */
    @RequestMapping(value = "/extract/gongsa/search", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> search(@RequestParam Map<String, String> paramMap) {
        log.info("GongsaExtractController.search = {}", paramMap);

        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            GongSaExtractResult result = gongSaExtractService.search(paramMap);

            resultMap.put("head", result.getHead());
            resultMap.put("titles", result.getTitles());
            resultMap.put("data", result.getData());
            resultMap.put("count", result.getData().size());
            return ResponseEntity.ok(resultMap);
        } catch (IllegalArgumentException e) {
            resultMap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        } catch (Exception e) {
            log.error("공사번호 추출 조회 실패", e);
            resultMap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
        }
    }

    /**
     * 공사번호 추출 CSV 다운로드 (파라미터는 search 와 동일)
     */
    @RequestMapping(value = "/extract/gongsa/download", method = {RequestMethod.GET, RequestMethod.POST})
    public void download(HttpServletResponse response, @RequestParam Map<String, String> paramMap) throws Exception {
        log.info("GongsaExtractController.download = {}", paramMap);

        GongSaExtractResult result;
        try {
            result = gongSaExtractService.search(paramMap);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        String fileName = "공사번호추출_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);

        Writer w = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        GongSaCsvWriter.write(result, w);
    }
}
