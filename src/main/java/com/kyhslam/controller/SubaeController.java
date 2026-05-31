package com.kyhslam.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.dto.*;
import com.kyhslam.service.BlockHistoryService;
import com.kyhslam.service.ELVInfoService;
import com.kyhslam.service.SubaeHogiService;
import com.kyhslam.service.SubaeService;
import com.kyhslam.util.ElvInfoCommonUtil;
import com.kyhslam.util.PIDCommonUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

/**
 * 수배 or MLB 화면
 */

@Controller
@Slf4j
@RequiredArgsConstructor
public class SubaeController {

    private final BlockHistoryService blockHistoryService;

    private final SubaeService subaeService;

    private final ELVInfoService elvInfoService;

    private final SubaeHogiService subaeHogiService;


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
        log.info("========== subae logicView.html");
        return "thymeleaf/logicView";
    }

    @GetMapping("/subae/logicViewV2")
    public String logicViewV2(HttpServletResponse response) {
        log.info("========== subae logicView.html");
        return "thymeleaf/logicViewV2";
    }

    //로직 비교
    @GetMapping("/subae/logicViewDiff")
    public String logicViewDiff(HttpServletResponse response) {
        log.info("========== subae logicViewDiff.html");
        return "thymeleaf/logicViewDiff";
    }

    @GetMapping("/subae/logicViewDiffV2")
    public String logicViewDiffV2(HttpServletResponse response) {
        log.info("========== subae logicViewDiffV2.html");
        return "thymeleaf/logicViewDiffV2";
    }



    //findPIDLineDiff
    @PostMapping("/subae/findPIDLineDiff")
    @ResponseBody
    public HashMap<String, Object> findPIDLineDiff(String pid, String pidOid, String pidOidb) {

        HashMap<String, Object> resultMap = new HashMap<>();
        HashSet<String> beforeMap = new HashSet<>();

        LinkedHashMap<String,PIDDetailDTO> beforeDetailMap = new LinkedHashMap<>();

        PIDCommonUtil.findPIDLineDiffBefore(pid, pidOid, beforeMap, beforeDetailMap); // 이전 버전
        ArrayList<ArrayList<String>> result = PIDCommonUtil.findPIDLineDiffMain(pid, pidOidb, beforeMap, beforeDetailMap);

        resultMap.put("result", result);
        resultMap.put("beforeDetailMap", beforeDetailMap);

        //return result;
        return resultMap;
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
    @CrossOrigin
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
     * @return
     */
    @CrossOrigin
    @GetMapping("/subae/searchMissPartofProduct")
    public String searchMissPartofProduct() {
        //return "subaeLogic/searchPartFinder";
        return "thymeleaf/searchPartFinder";
    }

    /**
     * 자재 Finder 화면 (최신)
     * @return
     */
    @GetMapping("/subae/searchPartFinderTest")
    public String searchPartFinderTest() {
        //return "subaeLogic/searchPartFinder";
        return "thymeleaf/searchPartFinderTest";
    }


    /**
     * 자재번호가 사용되고 있는 모든 제품 찾기
     *
     * @param whereCond – 검색 조건 (kvConditions 포함)
     * @return
     */
    @PostMapping("/subae/searchMissPartofProduct")
    @ResponseBody
    @CrossOrigin
    public ArrayList<HashMap<String, String>> searchMissPartofProduct(PartWhere whereCond) {
        log.info("searchMissPartofProduct whereCond={}", whereCond);

        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> opList = new ArrayList<>();
        ArrayList<String> valList = new ArrayList<>();


        /* ── 동적 K-V 조건 파싱 & 출력 ── */
        String kvJson = whereCond.getKvConditions();
        if (kvJson != null && !kvJson.isBlank()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, String>> kvList =
                        mapper.readValue(kvJson, new TypeReference<List<Map<String, String>>>() {});

                log.info("=== KV 조건 목록 (총 {}건) ===", kvList.size());
                for (int i = 0; i < kvList.size(); i++) {
                    Map<String, String> kv = kvList.get(i);
                    String key   = kv.getOrDefault("key",   "");
                    String op    = kv.getOrDefault("op",    "");
                    String value = kv.getOrDefault("value", "");
                    keyList.add(key);
                    opList.add(op);
                    valList.add(value);
                    log.info("  [{}] key={} | op={} | value={}", i + 1, key, op, value);
                }
                log.info("================================");
            } catch (Exception e) {
                log.warn("kvConditions 파싱 실패: {}", e.getMessage());
            }
        }

        //ArrayList<ProductDto> result = new ArrayList<>();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        result = subaeService.findPartOfProduct_v2(whereCond);
        return result;
    }

    //조회결과 그래프/통계 팝업 화면
    @GetMapping("/subae/searchGraph")
    public String searchGraph() {

        return "thymeleaf/searchPartGraph";
    }

    @GetMapping("/subae/searchInteractive")
    public String searchInteractive() {

        return "thymeleaf/searchInteractive";
    }

    @PostMapping("/subae/searchPartFinder")
    @ResponseBody
    @CrossOrigin
    public ArrayList<ProductDto> searchPartFinder(PartWhere whereCond) {
        System.out.println("searchPartFinder" + whereCond);
        ArrayList<ProductDto> result = new ArrayList<>();

        //result = subaeService.findPartOfProduct_v2(whereCond);
        return result;
    }

    //BOM수배율 화면
    @GetMapping("/subae/bomDashboard")
    public String bomDashboard() {
        //return "dashboard/bomSubaeDashboard";
        return "dashboard/bomSubaeDashboard";
    }

    //BOM품목비교
    @GetMapping("/subae/bomDashboardV2")
    public String bomOPTDashboard() {
        //return "dashboard/bomSubaeDashboard";
        return "thymeleaf/bomOPTDashboard";
    }

    //BOM수배 데이터 조회
    @PostMapping("/subae/bomDashboard")
    @ResponseBody
    @CrossOrigin
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
    @CrossOrigin
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
    @CrossOrigin
    public ArrayList<ArrayList<String>> findPIDLineView(String pid) {

        if (pid != null && !pid.equals("")) {
            pid = pid.toUpperCase();
        }

        ArrayList<ArrayList<String>> result = subaeService.findPIDLineView(pid);
        return result;
    }

    @PostMapping("/subae/findPIDLineViewV2")
    @ResponseBody
    @CrossOrigin
    public ArrayList<ArrayList<String>> findPIDLineViewV2(String pid, String pidOid) {

        log.info("pid = " + pid);
        log.info("pidOid = " + pidOid);
        if (pid != null && !pid.equals("")) {
            pid = pid.toUpperCase();
        }

        ArrayList<ArrayList<String>> result = subaeService.findPIDLineViewV2(pid, pidOid);
        log.info("result.size() = " + result.size());
        return result;
    }

    @PostMapping("/subae/logicViewDiffPopup")
    public String logicViewDiffPopup(@RequestParam("data") String data, Model model) {
        model.addAttribute("data", data);

        System.out.println("data = " + data);
        return "thymeleaf/logicViewDiffPopup";
    }

    //마인드맵
    @GetMapping("/subae/logicViewMaptify")
    public String logicViewMapify(String pid) {
        log.info("========== subae logicViewMapify");
        return "subaeLogic/logicViewMaptify";
    }

    /**
     * @return
     * @apiNote 2개의 호기에 대한 영업사양 값 비교
     */
    @GetMapping("/subae/elevatorSpecDiff")
    public String elevatorSpecDiff() {
        log.info("========== subae elevatorSpecDiff");
        return "subaeLogic/elevatorSpecDiff";
    }

    @PostMapping("/subae/elevatorSpecDiff")
    @ResponseBody
    @CrossOrigin
    public ArrayList<HashMap<String, String>> elevatorSpecDiff(String ho1, String ho2) {
        log.info("========== subae elevatorSpecDiff");
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        if (ho1 != null && !"".equals(ho1) && ho2 != null && !"".equals(ho2)) {
            result = subaeService.getSalesInfo(ho1, ho2);
        }
        return result;
    }


    /**
     * 층별로직
     *
     * @param hogi
     * @return
     * @apiNote 특정호기의 전체 층 검사하여 사양값 추출
     */
    @GetMapping("/subae/getFloorInfoJson")
    @ResponseBody
    @CrossOrigin
    public List<Map<String, Object>> getFloorInfoJson(String hogi, String key) {
        //http://localhost:8070/subae/getFloorInfoJson?hogi=208618L17&key=electUser
        //https://vault-in.hdel.co.kr:8070/getFloorInfoJson?hogi=208618L17&key=electUser

        //https://plmpro.hdel.co.kr/plmetc/vault/findProductInfo?productNo=N26185L01

        log.info("========== subae getFloorInfoJson");

        List<Map<String, Object>> result = null;
        if (key.equals("electUser")) {
            if (hogi != null && !"".equals(hogi)) {
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
    @CrossOrigin
    public HashMap<String, String> pidExecute(String pid, String hogi, String testVersion, String floor, String isfloor) {

        //testVersion = on
        //isfloor = Y
        //https://plmpro.hdel.co.kr/plmetc/vault/pidExecute?hogi=208223L01&PID=EL_PB186A01&testVersion=on&isfloor&floor=

        //http://localhost:8070/subae/pidExecute?hogi=208223L01&pid=EL_PB186A01&testVersion=on&isfloor&floor=

        HashMap<String, String> result = new HashMap<>();

        result = subaeService.pidExecute(hogi, pid, testVersion, floor, isfloor);

        return result;
    }


    //시뮬레이터 전체 태그
    //https://plmpro.hdel.co.kr/plmetc/vault/pidExecuteLineData?hogi=208223L01&PID=EL_PB186A01&testVersion=on&isfloor&floor=


    //공사번호 추출
    @GetMapping("/subae/searchElvInfo")
    public String searchElvInfo() {
        return "thymeleaf/searchElvInfo";
    }

    @PostMapping("/subae/searchElvInfo")
    @ResponseBody
    public ArrayList<ElvInfoDTO> searchElvInfo(ElvWhere where) {
        System.out.println("searchElvInfo where = " + where);
        ArrayList<ElvInfoDTO> result = new ArrayList<>();
        result = elvInfoService.findElvSearch(where);

        return result;
    }

    //공사번호 추출 V2
    @GetMapping("/subae/searchElvInfoV2")
    public String searchElvInfoV2() {
        return "thymeleaf/searchElvInfoV2";
    }

    @GetMapping("/subae/searchElvInfoV3")
    public String searchElvInfoV3() {
        return "thymeleaf/searchElvInfoV3";
    }

    @PostMapping("/subae/searchElvInfoV2")
    @ResponseBody
    public ArrayList<ElvInfoDTO> searchElvInfoV2(ElvWhere where) {
        System.out.println("searchElvInfoV2 where = " + where);
        ArrayList<ElvInfoDTO> result = new ArrayList<>();
        result = elvInfoService.findElvSearch(where);

        return result;
    }

    @GetMapping("/subae/findGisong")
    @ResponseBody
    @CrossOrigin
    public ArrayList<CodeDTO> findGisong() {
        ArrayList<CodeDTO> result = ElvInfoCommonUtil.findDosCode("기종");
        return result;
    }

    @PostMapping("/subae/findCodeList")
    @ResponseBody
    @CrossOrigin
    public ArrayList<CodeDTO> findBrand(String typeName) {
        ArrayList<CodeDTO> result = ElvInfoCommonUtil.findCodeList(typeName);
        return result;
    }

    @Description("블록번호별 요약 정보를 조회하는 메서드")
    @GetMapping("/subae/findSummaryAsBlockNo")
    @ResponseBody
    @CrossOrigin
    public ArrayList<HashMap<String, String>> findSummaryAsBlockNo() {
        ArrayList<HashMap<String, String>> result = subaeHogiService.findSummaryAsBlockNo();
        return result;
    }

    @Description("블록번호별 요약 정보를 조회하는 메서드(총 수배건수, 총 수정건수)")
    @GetMapping("/subae/findSummaryAsCount")
    @ResponseBody
    @CrossOrigin
    public HashMap<String, String> findSummaryAsCount() {
        HashMap<String, String> result = subaeHogiService.findSummaryAsCount();
        return result;
    }

}
