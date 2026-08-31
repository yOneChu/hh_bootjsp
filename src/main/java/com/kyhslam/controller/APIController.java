package com.kyhslam.controller;

import com.kyhslam.dto.*;
import com.kyhslam.service.MLBService;
import com.kyhslam.service.SubaeService;
import com.kyhslam.util.*;
import com.kyhslam.util.user.UserCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.util.StopWatch;
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


    private final PLM_DB_Definition plmDBDefinition;

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

    @Description("품번을 ,로 구분하여 속성정보 조회")
    @CrossOrigin
    @PostMapping("/findPartInfoWithList")
    @ResponseBody
    public ArrayList<PartDTO> findPartInfoWithList(String key, String PartNoList) {

        //https://vault-in.hdel.co.kr:8070/api/findPartInfoWithList

        ArrayList<PartDTO> resultList = new ArrayList<>();

        if ("subae".equals(key)) {
            resultList = MLBCommonUtil.findPartInfoWithList_v2(PartNoList);
        }

        return resultList;
    }

    @Description("영업 사양")
    @GetMapping("/findElvSearch")
    @ResponseBody
    @CrossOrigin
    public ArrayList<HashMap<String, String>> findElvSearch(String key, String productNo) {
        //http://localhost:8070/apiv2/findElvSearch?key=subae&productNo=211704L17
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        //ElvWhere whereCond = new ElvWhere();
        //whereCond.setHogi(productNo);

        // 약 1분 30초
        StopWatch sw = new StopWatch();
        sw.start();

        HashMap<String, String> rMap = new HashMap<String, String>();

        if ("subae".equals(key)) {
            //result = ElvInfoCommonUtil.findElvSearch(whereCond);
            //result = ElvInfoCommonUtil.getSalesInfo(productNo); // PLM API 연계 추출


            // 5초 대기
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

            ArrayList<HashMap<String, String>> resultData = ElvInfoCommonUtil.findElvSearchInfoV2(productNo);

            for (Map<String, String> row : resultData) {
                System.out.println("--------------------------------------------------");
                // Map을 순회하며 Key(컬럼명)와 Value(데이터) 출력
                for (Map.Entry<String, String> entry : row.entrySet()) {
                    //System.out.println(entry.getKey() + " : " + entry.getValue());

                    if(ElvInfoCommonUtil.isNumeric(entry.getValue())) {
                        String resultVal = ElvInfoCommonUtil.findCodeValue(entry.getValue());

                        if(resultVal != null && !resultVal.isEmpty()) {
                            System.out.println(entry.getKey() + " : " + resultVal);
                            rMap.put(entry.getKey(), resultVal);
                        } else {
                            System.out.println(entry.getKey() + " : " + entry.getValue());
                            rMap.put(entry.getKey(), entry.getValue());
                        }

                    } else {
                        System.out.println(entry.getKey() + " : " + entry.getValue());
                        rMap.put(entry.getKey(), entry.getValue());
                    }

                    result.add(rMap);
                }
            }
        }

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);

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

        StopWatch sw = new StopWatch();
        sw.start();

        if ("subae".equals(key)) {
            //result = InventorCommonUtil.findProductInfo(productNo);
            bomList = ProductCommonUtil.findProductInfo(productNo);
        }

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);

        return bomList;
    }

    @Description("BOM 1레벨 및 하위 전체")
    @GetMapping("/findProductBOM")
    @CrossOrigin
    @ResponseBody
    public ArrayList<BomPartDTO> findProductBOM(String productNo, String key) throws Exception {
        //http://localhost:8070/apiv2/findProductBOM?key=subae&productNo=212133L02

        //https://vault-in.hdel.co.kr:8070/apiv2/findProductBOM?key=subae&productNo=N27748L02
        ArrayList<BomPartDTO> bomList = new ArrayList<BomPartDTO>();

        System.out.println("findProductBOM ==========");

        // 약 4초
        StopWatch sw = new StopWatch();
        sw.start();

        if ("subae".equals(key)) {
            bomList = ProductCommonUtil.findProductBOM(productNo);
        }

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);

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
            //elvData = ElvInfoCommonUtil.findElvSearchInfoV2(hogi); // 영업사양 추출 개선


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
     * 영업사양 DB명세서 - 공유X
     * @param key
     * @return
     */
    @GetMapping("/getSalesMetaInfo")
    @ResponseBody
    @CrossOrigin
    public String getSalesMetaInfo(String key) {
        //https://vault-in.hdel.co.kr:8070/apiv2/getSalesMetaInfo?key=subae

        String result = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;

        if ("subae".equals(key)) {
            //result = PLM_DB_Definition.getPLM_DB_MetaData("SALES_QUERY");

            result = PLM_DB_Definition.getSales_Definition(); // String 형식
                    }
        return result;
    }


    /**
     * 로직작성 정의서-DB
     * @return
     */
    @GetMapping("/getLogicWriteAsDB")
    @ResponseBody
    @CrossOrigin
    public String getLogicWriteAsDB(String key) {
        //https://vault-in.hdel.co.kr:8070/apiv2/getSalesMetaInfo?key=subae

        String result = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;

        if ("subae".equals(key)) {
            result = PLM_DB_Definition.getPLM_DB_MetaData("LOGIC_WRITE");
        }
        return result;
    }

    /**
     * 로직 정의서-DB - 조회
     * @return
     */
    @GetMapping("/getLogicVerifyAsDB")
    @ResponseBody
    @CrossOrigin
    public String getLogicVerifyAsDB(String key, String type) {
        //https://vault-in.hdel.co.kr:8070/apiv2/getSalesMetaInfo?key=subae

        //LOGIC_VERIFY
        String result = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;

        if ("subae".equals(key)) {
            result = PLM_DB_Definition.getPLM_DB_MetaData(type);
        }
        return result;
    }

    /**
     * 로직 정의서 - 수정
     * @param key
     * @param type
     * @param updatedContent
     * @return
     */
    @PostMapping("/update_PLM_DB_MetaData")
    @CrossOrigin
    public String update_PLM_DB_MetaData(String key, String type, String updatedContent) {
        //https://vault-in.hdel.co.kr:8070/apiv2/update_PLM_DB_MetaData?key=subae
        //http://localhost:8070/apiv2/update_PLM_DB_MetaData?key=subae


        //LOGIC_WRITE
        log.info("updatedContent = " + updatedContent);
        if ("subae".equals(key)) {
            //result = PLM_DB_Definition.getPLM_DB_MetaData("LOGIC_WRITE");
            plmDBDefinition.update_PLM_DB_MetaData(updatedContent, type);
        }

        return updatedContent;
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
        //http://localhost:8070/apiv2/getPIDMetaInfo?key=subae
        //https://vault-in.hdel.co.kr:8070/apiv2/getPIDMetaInfo?key=subae

        String result = "";
        if ("subae".equals(key)) {
            result = PLM_DB_Definition.getPID_DB_MetaData();
        }

        return result;
    }

    /**
     * 사용자 정보 리스트
     * @param key
     * @return
     */
    @GetMapping("/getUserInfoList")
    @ResponseBody
    @CrossOrigin
    public ArrayList<UserDTO> getUserInfoList(String key) {
        //http://localhost:8070/apiv2/getPIDMetaInfo?key=subae
        //https://vault-in.hdel.co.kr:8070/apiv2/getPIDMetaInfo?key=subae

        ArrayList<UserDTO> userList = new ArrayList<>();

        if ("subae".equals(key)) {
            userList = UserCommonUtil.getUserInfo();
        }

        return userList;
    }

}
