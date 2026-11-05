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
import java.util.HashMap;

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

    @Description("수량 PID 조회 화면 개선")
    @GetMapping("/searchLogicQty")
    public String searchLogicQty() {
        //return "mlb/searchPartQtyPid";
        return "thymeleaf/logic/searchLogicQtyPID";
    }


    @Description("수량 PID 조회 로직")
    @PostMapping("/searchPartQtyPid")
    @ResponseBody
    public ArrayList<PartInfoDTO> searchPartQtyPid(String year, String blockNo, String qtyPid) {
        ArrayList<PartInfoDTO> resultList = mlbService.findPIDasQTY(year, blockNo.toUpperCase().trim(), qtyPid.toUpperCase().trim());
        return resultList;
    }


    @Description("수량 PID 조회 로직 - 여러 PID(콤마 구분) OR 조회")
    @PostMapping("/searchPartQtyPidMulti")
    @ResponseBody
    public ArrayList<PartInfoDTO> searchPartQtyPidMulti(String year, String yearCond, String blockNo, String qtyPid) {
        if (blockNo == null || qtyPid == null) return new ArrayList<>();
        return mlbService.findPIDasQTYMulti(year, yearCond, blockNo.toUpperCase().trim(), qtyPid);
    }


    /**
     * [수량 PID 상세 검색 화면(searchLogicQtyPID.html) 전용] 수량 PID 조회 - 여러 PID(콤마 구분) OR 조회 + 시작 부품 상위 링크 포함
     * searchPartQtyPidMulti 결과에 더해, BlockNo 로 찾은 시작 부품이 상위 조립품에 걸린 링크(상위→시작 부품)의 QTY/CMT 도 검사해 포함한다.
     * (searchPartQtyPidMulti 는 searchPartQtyPid.html 화면에서도 쓰이므로 그대로 두고 별도로 만든 경로)
     */
    @Description("[수량 PID 상세 검색] 수량 PID 조회 - 여러 PID OR 조회 + 시작 부품 상위 링크 포함")
    @PostMapping("/searchLogicQtyPidMulti")
    @ResponseBody
    public ArrayList<PartInfoDTO> searchLogicQtyPidMulti(String year, String yearCond, String blockNo, String qtyPid) {
        if (blockNo == null || qtyPid == null) return new ArrayList<>();
        return mlbService.findPIDasQTYMultiWithStartParent(year, yearCond, blockNo.toUpperCase().trim(), qtyPid);
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


    @Description("blockNo로 특정기간에 제품에 수배된 자재 찾기 화면")
    @GetMapping("/searchBomGetPartNo")
    public String searchBomGetPartNo() {
        return "thymeleaf/logic/searchBomGetPartNo";
    }

    @Description("blockNo로 특정기간에 제품에 수배된 자재 찾기")
    @CrossOrigin
    @PostMapping("/searchBomGetPartNo")
    @ResponseBody
    public ArrayList<HashMap<String, String>> searchBomGetPartNo(String proCreDate, String proCreDateTo, String blockNo, String partCreDate) {

        ArrayList<HashMap<String, String>> result = MLBCommonUtil.searchBomGetPartNo(proCreDate, proCreDateTo, blockNo, partCreDate);

        return result;
    }
}
