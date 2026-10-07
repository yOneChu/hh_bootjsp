package com.kyhslam.controller.simulate;

import com.kyhslam.service.simulate.BlockSimulateService;
import com.kyhslam.service.simulate.PickSimulateService;
import com.kyhslam.util.simulate.SimulateBomVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/simulate")
@RequiredArgsConstructor
@Slf4j
public class SimulController {

    private final PickSimulateService pickSimulateService;
    private final BlockSimulateService blockSimulateService;

    /**
     * @apiNote Pick시뮬 조회 화면
     * @param key
     * @return
     */
    @GetMapping("/getPickView")
    @CrossOrigin
    public String viewLogic(String key) {
        //http://localhost:8070/simulate/getPickView
        return "thymeleaf/simulate/getPickView";
    }

    /**
     * @apiNote Block시뮬 조회 화면
     * @return
     */
    @GetMapping("/getBlockSimulView")
    @CrossOrigin
    public String getBlockSimulView() {
        //http://localhost:8070/simulate/getBlockSimulView
        return "thymeleaf/simulate/getBlockSimul";
    }



    @Description("호기의 PICK 정보 추출 (pickMap : 품번 → PICK, separateInfo : 품번-PICK → 수량)")
    @GetMapping("/getPick")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<Map<String, Object>> getPick(@RequestParam String hogi,
                                                       @RequestParam(required = false) String optList,
                                                       @RequestParam(required = false) String blockNo) {
        log.info("getPick hogi:{}, optList:{}, blockNo:{}", hogi, optList, blockNo);

        try {
            List<String> opts = splitParam(optList);
            HashMap<String, Object> result = pickSimulateService.getPick(hogi,
                    opts.isEmpty() ? PickSimulateService.DEFAULT_OPT_LIST : opts, splitParam(blockNo));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("getPick error hogi:{}", hogi, e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @Description("블록 BOM 시뮬레이션 (시뮬 결과와 현재 BOM 비교)")
    @GetMapping("/simulateBlock")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<Map<String, Object>> simulateBlock(@RequestParam String hogi,
                                                             @RequestParam(required = false) String blockNo,
                                                             @RequestParam(required = false) String blockOpt) {
        //http://localhost:8070/simulate/simulateBlock?hogi=N26143L01&blockNo=E321A
        log.info("simulateBlock hogi:{}, blockNo:{}, blockOpt:{}", hogi, blockNo, blockOpt);

        try {
            List<SimulateBomVO> list = blockSimulateService.simulateBlock(splitParam(hogi), splitParam(blockNo), splitParam(blockOpt));

            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("count", list.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("simulateBlock error hogi:{}", hogi, e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /** "a, b,c" → [a, b, c] (빈 값 제외) */
    private static List<String> splitParam(String value) {
        List<String> list = new ArrayList<>();
        if (value == null)
            return list;
        for (String v : value.split("[,\\s]+")) {
            if (!v.trim().isEmpty())
                list.add(v.trim());
        }
        return list;
    }
}
