package com.kyhslam.controller;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.service.MLBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@Slf4j
public class APIController {

    private final MLBService mlbService;

    @Description("수량 PID 조회 로직")
    @PostMapping("/api/searchPartQtyPid")
    @ResponseBody
    public ArrayList<PartInfoDTO> searchPartQtyPid(String year, String blockNo, String qtyPid, String key) {
        ArrayList<PartInfoDTO> resultList = new ArrayList<>();
        if ("subae".equals(key)) {
            resultList = mlbService.findPIDasQTY(year, blockNo.toUpperCase().trim(), qtyPid.toUpperCase().trim());
        }

        return resultList;
    }
}
