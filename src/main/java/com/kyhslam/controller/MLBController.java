package com.kyhslam.controller;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.service.MLBService;
import com.kyhslam.util.MLBCommonUtil;
import com.kyhslam.util.PIDCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mlb")
public class MLBController {

    private final MLBService mlbService;

    @Description("수량 PID 조회 화면")
    @GetMapping("/searchPartQtyPid")
    public String searchPartQtyPid() {
        //return "mlb/searchPartQtyPid";
        return "thymeleaf/searchPartQtyPid";
    }


    @Description("수량 PID 조회 로직")
    @PostMapping("/searchPartQtyPid")
    @ResponseBody
    public ArrayList<PartInfoDTO> searchPartQtyPid(String year, String blockNo, String qtyPid) {
        ArrayList<PartInfoDTO> resultList = mlbService.findPartWithYear_inner(year, blockNo, qtyPid);
        return resultList;
    }


    @Description("품번으로 속성정보 조회")
    @CrossOrigin
    @GetMapping("/findPartOneWithPartNo")
    @ResponseBody
    public PartInfoDTO findPartOneWithPartNo(String partNo) {
        PartInfoDTO result = MLBCommonUtil.findPartOneWithPartNo(partNo);

        return result;
    }

    //thymeleaf test
    @GetMapping("/thyTest")
    public String thyTest() {
        return "thymeleaf/excelTest";
    }
}
