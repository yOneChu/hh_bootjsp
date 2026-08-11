package com.kyhslam.controller;

import com.kyhslam.dto.*;
import com.kyhslam.service.MLBService;
import com.kyhslam.service.SubaeService;
import com.kyhslam.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController()
@RequestMapping("/apiv2")
@RequiredArgsConstructor
@Slf4j
public class APIController {

    private final MLBService mlbService;

    private final SubaeService subaeService;

    @Description("수량 PID 조회 로직")
    @PostMapping("/searchPartQtyPid")
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
    @GetMapping("/findPartOneWithPartNo")
    @ResponseBody
    public PartDTO findPartOneWithPartNo(String partNo, String key) {
        PartDTO result =  new PartDTO();

        if ("subae".equals(key)) {
            //result = MLBCommonUtil.findPartOneWithPartNo(partNo);
            result = MLBCommonUtil.findPartOneWithPartNoV2(partNo);
        }

        return result;
    }

    @Description("영업사양")
    @GetMapping("/findElvSearch")
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
    @GetMapping("/findAssyDownBOM")
    @ResponseBody
    public ArrayList<PartInfoDTO> findAssyDownBOM(String partNo, String key) {

        ArrayList<PartInfoDTO> result = new ArrayList<>();
        if ("subae".equals(key)) {
            result = MLBCommonUtil.findAssyDownBOM(partNo);
        }

        return result;
    }

    @Description("BOM 1레벨 조회")
    @GetMapping("/findProductInfo")
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

    @Description("BOM 1레벨 및 하위 전체")
    @GetMapping("/findProductBOM")
    @CrossOrigin
    @ResponseBody
    public ArrayList<BomPartDTO> findProductBOM(String productNo, String key) throws Exception {
        //https://vault-in.hdel.co.kr:8070/api/findProductBOM?key=subae&productNo=N27748L02
        ArrayList<BomPartDTO> bomList = new ArrayList<BomPartDTO>();

        if ("subae".equals(key)) {
            bomList = ProductCommonUtil.findProductBOM(productNo);
        }

        return bomList;
    }


    @Description("시물레이터 결과만 추출")
    @GetMapping("/pidExecute")
    @ResponseBody
    @CrossOrigin
    public HashMap<String, String> pidExecute(String pid, String hogi, String testVersion,
                                              String floor, String isfloor, String key, String type) {

        HashMap<String, String> result = new HashMap<>();
        if ("subae".equals(key)) {
            result = subaeService.pidExecute(hogi, pid, testVersion, floor, isfloor, type);
        }

        return  result;
    }


    @Description("메일 발송")
    @GetMapping("/sendMail")
    @ResponseBody
    @CrossOrigin
    public void sendSubaeMail(String sender, String toEmail, String ccEmail, String subject,
                              String htmlContent, String key) {
        https://vault-in.hdel.co.kr:8070/api/sendMail?key=subae&sender=younghwan.kim@hyundaielevator.com&toEmail=younghwan.kim@hyundaielevator.com&ccEmail=&subject=123&htmlContent=1111
        if("subae".equals(key)){
            SendMail.sendToSubaeMail(sender, toEmail, ccEmail, subject, htmlContent);
        }
    }

    @Description("쿼리 수행")
    @GetMapping("/executeQuery")
    @ResponseBody
    @CrossOrigin
    public List<Map<String, Object>> executeQuery(String key, String sql) {
        //https://vault-in.hdel.co.kr:8070/api/executeQuery?key=subae&sql=
        List<Map<String, Object>> result = new ArrayList<>();

        if("subae".equals(key)){
            if(sql != null && !sql.isEmpty()) {
                if(sql.contains("DROP") || sql.contains("DELETE") || sql.contains("ALTER")) return result;
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

    @Description("호기의 영업사양 추출")
    @GetMapping("/getSalesInfo")
    @ResponseBody
    @CrossOrigin
    public ArrayList<HashMap<String, String>> getSalesInfo(String key, String hogi) {
        //https://vault-in.hdel.co.kr:8070/api/getSalesInfo?key=subae&hogi=

        ArrayList<HashMap<String, String>> elvData = null;

        if ("subae".equals(key)) {
            elvData = ElvInfoCommonUtil.getSalesInfo(hogi);
        }
        return elvData;
    }

    //특성값
    @Description("특성코드 리스트 - 육상")
    @GetMapping("/getCodeList")
    @ResponseBody
    @CrossOrigin
    public ArrayList<CodeInfoDTO> getCodeList(String key) {
        //https://vault-in.hdel.co.kr:8070/api/getSalesInfo?key=subae&hogi=

        ArrayList<CodeInfoDTO> result = new ArrayList<>();

        if ("subae".equals(key)) {
            result = MLBCommonUtil.getCodeList();
        }
        return result;
    }

    @Description("공사정보 필드 리스트 - 육상")
    @GetMapping("/getCodeField")
    @ResponseBody
    @CrossOrigin
    public ArrayList<HashMap<String, String>> getCodeField(String key) {
        //https://vault-in.hdel.co.kr:8070/api/getCodeField?key=subae

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        if ("subae".equals(key)) {
            result = MLBCommonUtil.getCodeField();
        }
        return result;
    }


    /**
     * 영업사양 DB명세서
     * @param key
     * @return
     */
    @GetMapping("/getSalesMetaInfo")
    @ResponseBody
    @CrossOrigin
    public String getSalesMetaInfo(String key) {
        //https://vault-in.hdel.co.kr:8070/api/getSalesMetaInfo?key=subae

        String result = "";
        //PreparedStatement pstmt = null;
        //ResultSet rs = null;
        //Connection con = null;

        if ("subae".equals(key)) {
            //result = PLM_DB_Definition.getPLM_DB_MetaData("SALES_QUERY");

            result = PLM_DB_Definition.getSales_Definition();

            /*try {
                con = VaultDBConnection.getConnection();

                StringBuffer sql = new StringBuffer();
                sql.append(" SELECT A.CATEGORY, A.CONTENT ");
                sql.append(" FROM PLM_LLM_METADATA A ");
                sql.append(" WHERE A.CATEGORY = 'SALES_QUERY' ");

                pstmt = con.prepareStatement(sql.toString());

                rs = pstmt.executeQuery();

                while (rs.next()) {
                    String CATEGORY = rs.getString("CATEGORY") == null ? "" : rs.getString("CATEGORY");
                    result = rs.getString("CONTENT") == null ? "" : rs.getString("CONTENT");
                }

                System.out.println("result = " + result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                VaultDBConnection.disconnect(con, pstmt, rs);
            }*/

        }
        return result;
    }

    /**
     * 비표준사양 DB 정의서
     * @param key
     * @return
     */
    @GetMapping("/getDutyMetaInfo")
    @ResponseBody
    @CrossOrigin
    public String getDutyMetaInfo(String key) {
        //https://vault-in.hdel.co.kr:8070/api/getDutyMetaInfo?key=subae

        String result = "";
        if ("subae".equals(key)) {
            result = PLM_DB_Definition.getDuty_Definition();
        }

        return result;
    }

    /**
     * PID DB 정의서
     * @param key
     * @return
     */
    @GetMapping("/getPIDMetaInfo")
    @ResponseBody
    @CrossOrigin
    public String getPIDMetaInfo(String key) {
        //https://vault-in.hdel.co.kr:8070/api/getPIDMetaInfo?key=subae

        String result = "";
        if ("subae".equals(key)) {
            result = PLM_DB_Definition.getPID_DB_MetaData();
        }

        return result;
    }
}
