package com.kyhslam.controller;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.service.MLBService;
import com.kyhslam.service.SubaeService;
import com.kyhslam.util.MLBCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
public class APIController {

    private final MLBService mlbService;

    private final SubaeService subaeService;

    @Description("수량 PID 조회 로직")
    @PostMapping("/api/searchPartQtyPid")
    @CrossOrigin
    @ResponseBody
    public ArrayList<PartInfoDTO> searchPartQtyPid(String year, String blockNo, String qtyPid, String key) {
        ArrayList<PartInfoDTO> resultList = new ArrayList<>();
        if ("subae".equals(key)) {
            resultList = mlbService.findPIDasQTY(year, blockNo.toUpperCase().trim(), qtyPid.toUpperCase().trim());
        }

        return resultList;
    }

    @Description("품번으로 속성정보 조회")
    @CrossOrigin
    @GetMapping("/api/findPartOneWithPartNo")
    @ResponseBody
    public PartInfoDTO findPartOneWithPartNo(String partNo,String key) {
        PartInfoDTO result =  new PartInfoDTO();

        if ("subae".equals(key)) {
            result = MLBCommonUtil.findPartOneWithPartNo(partNo);
        }

        return result;
    }

    //품번으로 하위 BOM 조회
    @Description("품번으로 하위 BOM 조회")
    @CrossOrigin
    @GetMapping("/api/findAssyDownBOM")
    @ResponseBody
    public ArrayList<PartInfoDTO> findAssyDownBOM(String partNo, String key) {

        ArrayList<PartInfoDTO> result = new ArrayList<>();
        if ("subae".equals(key)) {
            result = MLBCommonUtil.findAssyDownBOM(partNo);
        }

        return result;
    }


    @Description("시물레이터 결과만 추출")
    @GetMapping("/api/pidExecute")
    @ResponseBody
    @CrossOrigin
    public HashMap<String, String> pidExecute(String pid, String hogi, String testVersion,
                                              String floor, String isfloor, String key) {

        HashMap<String, String> result = new HashMap<>();
        if ("subae".equals(key)) {
            result = subaeService.pidExecute(hogi, pid, testVersion, floor, isfloor);
        }

        return  result;
    }

}
