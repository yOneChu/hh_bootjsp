package com.kyhslam.controller.simulate;

import com.kyhslam.service.simulate.BlockSimulateService;
import com.kyhslam.service.simulate.PickSimulateService;
import com.kyhslam.util.simulate.PidConsts;
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
                                                             @RequestParam(required = false) String blockOpt,
                                                             @RequestParam(defaultValue = "false") boolean pidTestBlock,
                                                             @RequestParam(defaultValue = "false") boolean pidTestElp,
                                                             @RequestParam(required = false) Integer pidVerBlock,
                                                             @RequestParam(required = false) Integer pidVerElp) {
        //http://localhost:8070/simulate/simulateBlock?hogi=N26143L01&blockNo=B128B08&pidTestBlock=true&pidTestElp=true
        //http://localhost:8070/simulate/simulateBlock?hogi=N26143L01&blockNo=B128B08&pidVerBlock=10&pidVerElp=-1
        log.info("simulateBlock hogi:{}, blockNo:{}, blockOpt:{}, pidTestBlock:{}, pidTestElp:{}, pidVerBlock:{}, pidVerElp:{}",
                hogi, blockNo, blockOpt, pidTestBlock, pidTestElp, pidVerBlock, pidVerElp);

        try {
            // 버전을 고르면 버전, 아니면 테스트 토글(-1), 둘 다 없으면 최신(null)
            Integer blockVer = pidVerBlock != null ? pidVerBlock : (pidTestBlock ? (Integer) PidConsts.TEST_VERSION : null);
            Integer elpVer = pidVerElp != null ? pidVerElp : (pidTestElp ? (Integer) PidConsts.TEST_VERSION : null);
            List<SimulateBomVO> list = blockSimulateService.simulateBlock(splitParam(hogi), splitParam(blockNo), splitParam(blockOpt),
                    blockVer, elpVer);

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

    @Description("블럭 1개의 PID 버전 목록 (block : 블럭 PID, elp : EL_P 블럭 PID / 항목 {version, latest})")
    @GetMapping("/pidVersions")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<Map<String, Object>> pidVersions(@RequestParam String blockNo) {
        //http://localhost:8070/simulate/pidVersions?blockNo=B128B08
        try {
            return ResponseEntity.ok(blockSimulateService.getPidVersions(blockNo.trim().toUpperCase()));
        } catch (Exception e) {
            log.error("pidVersions error blockNo:{}", blockNo, e);

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
