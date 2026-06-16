package com.kyhslam.controller;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.service.MLBService;
import com.kyhslam.service.SubaeService;
import com.kyhslam.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @Description("영업사양")
    @GetMapping("/api/findElvSearch")
    @ResponseBody
    @CrossOrigin
    public ArrayList<HashMap<String, String>> findElvSearch(String key, String productNo) {
        //http://localhost:8070/api/findElvSearch?key=subae&productNo=TEST-624822
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        //ElvWhere whereCond = new ElvWhere();
        //whereCond.setHogi(productNo);

        if ("subae".equals(key)) {
            //result = ElvInfoCommonUtil.findElvSearch(whereCond);
            result = ElvInfoCommonUtil.getSalesInfo(productNo);
        }

        return  result;
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

    @Description("BOM 1레벨 조회")
    @GetMapping("/api/findProductInfo")
    @CrossOrigin
    @ResponseBody
    public ArrayList<ProductDto> findProductInfo(String productNo, String key) throws Exception {

        ArrayList<ProductDto> bomList = new ArrayList<ProductDto>();

        if ("subae".equals(key)) {
            //result = InventorCommonUtil.findProductInfo(productNo);
            bomList = ProductCommonUtil.findProductInfo(productNo);
        }

        return bomList;
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


    @Description("메일 발송")
    @GetMapping("/api/sendMail")
    @ResponseBody
    @CrossOrigin
    public void sendSubaeMail(String sender, String toEmail, String ccEmail, String subject,
                              String htmlContent, String key) {
        if("subae".equals(key)){
            SendMail.sendToSubaeMail(sender, toEmail, ccEmail, subject, htmlContent);
        }
    }

    @Description("쿼리 수행")
    @GetMapping("/api/executeQuery")
    @ResponseBody
    @CrossOrigin
    public List<Map<String, Object>> executeQuery(String key, String sql) {
        //https://vault-in.hdel.co.kr:8070/api/executeQuery?key=subae&sql=
        List<Map<String, Object>> result = new ArrayList<>();

        if("subae".equals(key)){
            if(sql != null && !sql.isEmpty()) {
                if( !sql.contains("SELECT") ) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("MSG", "수정, 삭제 등 쿼리는 수행할 수 없습니다.");
                    result.add(row);

                    return result;
                }
            }
            result = SQLCommonUtil.executeQuery(sql);
        }
        return result;
    }

}
