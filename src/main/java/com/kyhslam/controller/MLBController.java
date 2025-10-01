package com.kyhslam.controller;

import com.kyhslam.util.PIDCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MLBController {

    @Description("수량 PID 조회 화면")
    @GetMapping("/mlb/searchPartQtyPid")
    public String searchPartQtyPid() {
        return "mlb/searchPartQtyPid";
    }

}
