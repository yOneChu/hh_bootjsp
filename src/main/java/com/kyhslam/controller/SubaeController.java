package com.kyhslam.controller;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.dto.LogicDTO;
import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.service.BlockHistoryService;
import com.kyhslam.service.SubaeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 수배 or MLB 화면
 */

@Controller
@Slf4j
@RequiredArgsConstructor
public class SubaeController {

    private final BlockHistoryService blockHistoryService;

    private final SubaeService subaeService;



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

        if (blockNo == null || blockNo.equals("")) {
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

    //메뉴얼 파일 띄우기
    @GetMapping("/subae/blockManual")
    public ResponseEntity<FileSystemResource> blockManual() {
        // 로컬 PDF 파일 경로
        //String filePath = "D:/PDF/" + fileName + ".pdf";

        String filePath = "C:\\Users\\Administrator\\Downloads\\Process-01.pdf";
        File pdfFile = new File(filePath);
        FileSystemResource file = new FileSystemResource(filePath);

        // 파일이 존재하면 반환
        if (file.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getFilename())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(file);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Block 기준정보 메일발송 테스트
     *
     * @return
     */
    @GetMapping("/subae/blockMailTest")
    @ResponseBody
    public String blockMailTest() {
        System.out.println(" --- blockMailTest ");
        blockHistoryService.blockMailTest();

        return "ok";
    }

    //로직뷰 화면
    @GetMapping("/subae/logicView")
    public String logicView(HttpServletResponse response) {
        log.info("========== subae logicView");
        return "subaeLogic/logicView";
    }

    @PostMapping("/subae/logiceditor")
    @ResponseBody
    public List<LogicDTO> logiceditor() {

        log.info("========== subae logiceditor");
        List<LogicDTO> list = new ArrayList<>();

        LogicDTO logic = new LogicDTO();
        logic.setNo("0");
        logic.setAddr("");
        logic.setSpec1("EL_ETM \n aaa");
        logic.setCon1(",?GT50?,?GT70?,?GT71?");

        list.add(logic);
        //list.add(new LogicDTO(2, "이영희", 34, "부산"));

        return list;
    }

    //PLM에서 중국자재 조회화면
    @GetMapping("/subae/searchCNPart")
    public String searchCNPart() {

        return "subaeLogic/searchCNPart";
    }

    //PLM에서 중국자재 조회 로직
    @PostMapping("/subae/searchCNPart")
    @ResponseBody
    public ArrayList<PartInfoDTO> searchCNPart(PartInfoDTO param) {
        ArrayList<PartInfoDTO> result = new ArrayList<>();

        System.out.println("param.toString() = " + param.toString());
        System.out.println("param.getPartNo() = " + param.getPartNo());

        //
        result = subaeService.findOneFromPartNo(param);
        System.out.println("result.size() = " + result.size());
        return result;
    }

    //수배율 계산 로직 수행
    @GetMapping("/subae/subaeBatch")
    @ResponseBody
    public void findSubaeProductNo() {
        subaeService.findSubaeProductNo("");
    }


    // 자재 대시보드 및 엑셀다운로드 화면
    @GetMapping("/subae/partDashboard")
    public String partDashboard() {
        //subaeService.findSubaeProductNo("");

        //partDashboard
        return "dashboard/partDashboardv2";
    }


    /**
     * 자재 Finder 화면
     *
     * @return
     */
    @GetMapping("/subae/searchMissPartofProduct")
    public String searchMissPartofProduct() {
        return "subaeLogic/searchPartFinder";
    }


    /**
     * 자재번호가 사용되고 있는 모든 제품 찾기
     * @param year
     * @param partNo
     * @return
     */
    @PostMapping("/subae/searchMissPartofProduct")
    @ResponseBody
    public ArrayList<ProductDto> searchMissPartofProduct(String year, String partNo) {
        ArrayList<ProductDto> result = new ArrayList<>();

        //subaeService.findMissPart(result, partNo, con01);
        result = subaeService.findPartOfProduct_v2(year, partNo);
        return result;
    }

    //BOM수배율 화면
    @GetMapping("/subae/bomDashboard")
    public String bomDashboard() {
        //return "dashboard/bomSubaeDashboard";
        return "dashboard/bomSubaeDashboard";
    }

    //BOM수배 데이터 조회
    @PostMapping("/subae/bomDashboard")
    @ResponseBody
    public ArrayList<ProductDto> bomDashboard(String year, String month) {

        ArrayList<ProductDto> result = new ArrayList<>();

        ProductDto param = new ProductDto();
        param.setProductAppdate(month);
        result = subaeService.findSubaeProductList(param);

        return result;
    }

    //BOM수배율 팝업화면
    @GetMapping("/subae/bomSubaeDashboardPop")
    public String bomSubaeDashboardPop() {
        //System.out.println("prodNo = " + prodNo);
        return "dashboard/bomSubaeDashboardPop";
    }


    // 2025-07 날짜로 수배율 계산한 PARTNO 전체 조회
    @PostMapping("/subae/findSubaePartNoList")
    @ResponseBody
    public ArrayList<ProductDto> findSubaePartNoList(String ucheck, String month) {

        ArrayList<ProductDto> result = new ArrayList<>();

        ProductDto param = new ProductDto();
        param.setProductAppdate(month);
        param.setUcheck("1");
        result = subaeService.findSubaePartNoList(param);

        return result;
    }

    //PID조회
    @PostMapping("/subae/findPIDLineView")
    @ResponseBody
    public ArrayList<ArrayList<String>> findPIDLineView(String pid) {

        if(pid != null && !pid.equals("")) {
            pid = pid.toUpperCase();
        }

        ArrayList<ArrayList<String>> result = subaeService.findPIDLineView(pid);
        return result;
    }

    //마인드맵
    @GetMapping("/subae/logicViewMaptify")
    public String logicViewMapify(String pid) {
        log.info("========== subae logicViewMapify");
        return "subaeLogic/logicViewMaptify";
    }

    /**
     * @apiNote 2개의 호기에 대한 영업사양 값 비교
     * @return
     */
    @GetMapping("/subae/elevatorSpecDiff")
    public String elevatorSpecDiff() {
        log.info("========== subae elevatorSpecDiff");
        return "subaeLogic/elevatorSpecDiff";
    }

    @PostMapping("/subae/elevatorSpecDiff")
    @ResponseBody
    public ArrayList<HashMap<String, String>> elevatorSpecDiff(String ho1, String ho2) {
        log.info("========== subae elevatorSpecDiff");
        ArrayList<HashMap<String, String>> result = new  ArrayList<>();
        if(ho1 != null && !"".equals(ho1) && ho2 != null && !"".equals(ho2)) {
            result = subaeService.getSalesInfo(ho1, ho2);
        }
        return result;
    }


    /**
     * 층별로직
     * @apiNote 특정호기의 전체 층 검사하여 사양값 추출
     * @param hogi
     * @return
     */
    @GetMapping("/subae/getFloorInfoJson")
    @ResponseBody
    public List<Map<String, Object>> getFloorInfoJson(String hogi, String key) {
        //http://localhost:8070/subae/getFloorInfoJson?hogi=208618L17&key=electUser
        //https://vault-in.hdel.co.kr:8070/getFloorInfoJson?hogi=208618L17&key=electUser

        //https://plmpro.hdel.co.kr/plmetc/vault/findProductInfo?productNo=N26185L01

        log.info("========== subae getFloorInfoJson");

        List<Map<String, Object>> result = null;
        if (key.equals("electUser")) {
            if(hogi != null && !"".equals(hogi)) {
                result = subaeService.getFloorInfoJson(hogi);
            }
        }
        return result;
    }

    //층별 로직 결과 화면
    //logicEachFloorView.jsp


    // 시물레이터 결과만 추출
    @GetMapping("/subae/pidExecute")
    @ResponseBody
    public HashMap<String, String> pidExecute(String pid, String hogi, String testVersion, String floor, String isfloor) {

        //testVersion = on
        //https://plmpro.hdel.co.kr/plmetc/vault/pidExecute?hogi=208223L01&PID=EL_PB186A01&testVersion=on&isfloor&floor=

        //http://localhost:8070/subae/pidExecute?hogi=208223L01&pid=EL_PB186A01&testVersion=on&isfloor&floor=

        HashMap<String, String> result = new HashMap<>();

        result = subaeService.pidExecute(hogi, pid, testVersion, floor, isfloor);

        return  result;
    }


    //시뮬레이터 전체 태그
    //https://plmpro.hdel.co.kr/plmetc/vault/pidExecuteLineData?hogi=208223L01&PID=EL_PB186A01&testVersion=on&isfloor&floor=
}
