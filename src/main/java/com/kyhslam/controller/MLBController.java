package com.kyhslam.controller;

import com.kyhslam.util.PIDCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mlb")
public class MLBController {

    @Description("수량 PID 조회 화면")
    @GetMapping("/searchPartQtyPid")
    public String searchPartQtyPid() {
        return "mlb/searchPartQtyPid";
    }


    @Description("수량 PID 조회 로직")
    @PostMapping("/searchPartQtyPid")
    @ResponseBody
    public String searchPartQtyPid(String year, String blockNo, String qtyPid, String cmtPid) {

        return "";
    }


    //thymeleaf test
    @GetMapping("/thyTest")
    public String thyTest() {
        return "thymeleaf/excelTest";
    }
}
