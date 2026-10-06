package com.kyhslam.controller.simulate;

import com.kyhslam.service.simulate.PickSimulateService;
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

    @GetMapping("/getPickView")
    @CrossOrigin
    public String viewLogic(String key) {
        //http://localhost:8070/simulate/getPickView
        return "thymeleaf/simulate/getPickView";
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
