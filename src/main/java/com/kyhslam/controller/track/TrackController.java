package com.kyhslam.controller.track;

import com.kyhslam.dto.ProductDto;
import com.kyhslam.service.SubaeHogiService;
import com.kyhslam.util.ProductCommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/track")
@RequiredArgsConstructor
public class TrackController {

    private final SubaeHogiService subaeHogiService;

    /**
     * @apiNote BOM 수정 검증 화면 : 최초설계완료호기와 그 BOM 을 조회한다
     */
    @GetMapping("/findModifyBom")
    public String findModifyBomView() {
        //http://localhost:8070/track/findModifyBom
        return "thymeleaf/track/findModifyBom";
    }

    /**
     * @apiNote 설계완료일(yyyyMMdd)에 최초설계완료된 호기 목록
     */
    @GetMapping("/initialHogiList")
    @ResponseBody
    public ResponseEntity<?> initialHogiList(@RequestParam("date") String date) {
        String searchDate = date.replace("-", "").trim();
        if (!searchDate.matches("\\d{8}")) {
            return ResponseEntity.badRequest().body(Map.of("message", "설계완료일 형식이 올바르지 않습니다. (yyyy-MM-dd)"));
        }
        try {
            List<HashMap<String, String>> list = subaeHogiService.fetchInitialDesignHogiList(searchDate);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "PLM 호기 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * @apiNote 호기의 최초설계완료 BOM
     */
    @GetMapping("/initialHogiBom")
    @ResponseBody
    public List<Map<String, String>> initialHogiBom(@RequestParam("hogi") String hogi) {
        ArrayList<ProductDto> bom = ProductCommonUtil.getInitialDesignBom(hogi.trim());

        List<Map<String, String>> result = new ArrayList<>(bom.size());
        for (ProductDto d : bom) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("productName", d.getProductName());
            m.put("productVersion", d.getProductVersion());
            m.put("blockNo", d.getBlockNo());
            m.put("blockOpt", d.getBlockopt());
            m.put("partNo", d.getPartNo());
            m.put("partName", d.getPartName());
            m.put("qty", d.getQty());
            m.put("spec", d.getSpec());
            m.put("cmt", d.getCmt());
            m.put("ucheck", d.getUcheck());
            m.put("modUser", d.getPartModUser());
            result.add(m);
        }
        return result;
    }
}
