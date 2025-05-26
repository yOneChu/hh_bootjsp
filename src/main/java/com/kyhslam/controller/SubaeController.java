package com.kyhslam.controller;

import com.kyhslam.dto.BlockHistoryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

/**
 * 수배 or MLB 화면
 */

@Controller
@Slf4j
public class SubaeController {

    //본사-법인 자재비교
    @GetMapping("/subae/comparePartCN")
    public String partPublicList() {
        return "subaeLogic/searchComparePartCN";
    }


    //법인자재리스트
    @GetMapping("/subae/searchStandardList")
    public String searchStandardList() {
        return "subaeLogic/searchStandardList";
    }


    //조회화면
    @GetMapping("/subae/searchByBlockNo")
    public String searchByBlockNo() {
        return "mlb/searchByBlockNo";
    }

    //Block 기준정보 조회화면
    @GetMapping("/subae/searchBlockStand")
    public String searchBlockStand() {
        return "subaeLogic/searchBlockStandardView";
    }

    //조회 로직
    @GetMapping("/subae/searchBlockLogic")
    @ResponseBody
    public ArrayList<BlockHistoryDTO> searchBlockLogic(String blockNo) {
        ArrayList<BlockHistoryDTO> result = new ArrayList<>();
        return result;
    }

    //Block 기준정보 상세화면
    @GetMapping("/subae/searchBlockStandInfo")
    public String searchBlockStandInfo() {
        return "subaeLogic/searchBlockStandInfo";
    }

}
