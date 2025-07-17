package com.kyhslam.controller;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.dto.LogicDTO;
import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.repository.mybatis.SubaeMapper;
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

    @GetMapping("/subae/partDashboard")
    public String partDashboard() {
        //subaeService.findSubaeProductNo("");

        //partDashboard
        return "dashboard/partDashboard";
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
     *
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
}
