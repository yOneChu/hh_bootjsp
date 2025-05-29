package com.kyhslam.controller;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.service.BlockHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

/**
 * 수배 or MLB 화면
 */

@Controller
@Slf4j
@RequiredArgsConstructor
public class SubaeController {

    private final BlockHistoryService blockHistoryService;

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
    @GetMapping("/subae/searchBlockStandardView")
    public String searchBlockStand() {
        return "subaeLogic/searchBlockStandardView";
    }

    //Block 기준정보 조회 로직
    @PostMapping("/subae/searchBlockLogic")
    @ResponseBody
    public ArrayList<BlockHistoryDTO> searchBlockLogic(String blockNo) {
        log.info("blockNo:{}", blockNo);
        System.out.println("blockNo = " + blockNo);
        ArrayList<BlockHistoryDTO> result = new ArrayList<>();

        if(blockNo == null || blockNo.equals("")){
            result = (ArrayList<BlockHistoryDTO>) blockHistoryService.findAll();
        } else {
            result = blockHistoryService.findByBlockNo(blockNo);
        }


        /*for(int i=0; i <  result.size(); i++){
            BlockHistoryDTO dto = result.get(i);
            System.out.println(dto.getBlockNo() + " > " + dto.getPickName());
        }*/


        return result;
    }

    //Block 기준정보 상세화면
    @GetMapping("/subae/searchBlockStandardInfo")
    public String searchBlockStandInfo(@RequestParam("blockNo") String blockNo) {

        return "subaeLogic/searchBlockStandardInfo";
    }

}
