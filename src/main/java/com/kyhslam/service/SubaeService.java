package com.kyhslam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.PartWhere;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.repository.SubaeRepository;
import com.kyhslam.repository.mybatis.SubaeMapper;
import com.kyhslam.util.ElvInfoCommonUtil;
import com.kyhslam.util.MLBCommonUtil;
import com.kyhslam.util.PIDCommonUtil;
import com.kyhslam.util.SubaeCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service("SubaeService")
@Slf4j
@RequiredArgsConstructor
public class SubaeService {

    private final SubaeRepository subaeRepository;

    private final SubaeMapper subaeMapper;

    /**
     * PLM에 등록된 법인자재 조회
     * @param param
     * @return
     */
    public ArrayList<PartInfoDTO> findOneFromPartNo(PartInfoDTO param) {
        ArrayList<PartInfoDTO> result = new ArrayList<PartInfoDTO>();
        result = SubaeCommonUtil.findOneFromPartNo(param);
        return result;
    }


    //1.2025년 수배율 검사할 자재들 조회
    /**
     * 2025년 수배율 검사할 자재들 조회
     */
    public void findSubaeProductNo(String testProductNo) {

        ArrayList<String> productNoList = new ArrayList<>();

        //이미 수배율 계산한 제품번호(호기) 조회
        ArrayList<String> usedProductNoList = new ArrayList<>();
        usedProductNoList = subaeMapper.findUsedProductNo();


        //1.2025년도 수배율 대상 제품번호 조회
        productNoList = SubaeCommonUtil.findSubaeProductNo();
        
        
        //testProductNo값있으면 테스트이기 때문에 해당 호기만 수행
        if(!"".equals(testProductNo)){
            productNoList.clear();
            productNoList.add(testProductNo);
        }
        
        
        for (int i = 0; i < productNoList.size(); i++) {
            String productNo = productNoList.get(i);

            if(usedProductNoList.contains(productNo)){
                continue;
            }

            //2.제품의 모든 oid 조회
            ArrayList<ProductDto> productOIDS = SubaeCommonUtil.findProductOIDS(productNo);

            HashMap<String,String> map = new HashMap<>();
            HashSet<String> dupCheck = new HashSet<>();
            ArrayList<ProductDto> partList = new ArrayList<>();
            boolean flag = false;

            if(productOIDS != null && productOIDS.size() > 0){

                System.out.println(productNo + " = " + productOIDS.size());
                String subaeVersion = "";
                String appDate = "";
                String creDate = "";
                String modDate = "";
                String gisong = "";
                String sujuName = "";


                for (int j = 0; j < productOIDS.size(); j++) {
                    ProductDto d = productOIDS.get(j);
                    String oid = d.getProductOid();

                    //3.그 oid로 수배율 계산하기
                    if (flag == false) {
                        subaeVersion = d.getProductVersion();
                        appDate = d.getProductAppdate();
                        creDate = d.getProductCreDate();
                        modDate = d.getProductModDate();
                        gisong = d.getGisong();
                        sujuName = d.getProductName();
                        flag = SubaeCommonUtil.checkDesignBOM(oid, partList, map, dupCheck);
                    } else {
                        continue;
                    }
                    System.out.println("j = " + j);
                }


                if (flag == true) {
                    for (int j = 0; j < partList.size(); j++) {
                        ProductDto p = partList.get(j);
                        p.setM_ModCount(map.get("m_ModCount"));
                        p.setC_ModCount(map.get("c_ModCount"));
                        p.setOne_ModCount(map.get("one_ModCount"));
                        p.setTwo_ModCount(map.get("two_ModCount"));
                        p.setThree_ModCount(map.get("three_ModCount"));
                        //p.setProductAppdate(map.get("APP_DATE"));
                        p.setProductCreDate(creDate);
                        p.setProductAppdate(appDate);
                        p.setProductModDate(modDate);
                        //p.setProductVersion(map.get("PROD_VERSION"));
                        p.setProductVersion(subaeVersion);
                        p.setGisong(gisong);
                        p.setProductName(sujuName);

                        subaeRepository.saveSubaeProduct(p);
                    }
                }


            }
        } // END FOR
    }

    public void findMissPart(ArrayList<ProductDto> dataList, String partNo, String con01) {

        ArrayList<String> productList = SubaeCommonUtil.findWipBom();

        for (int i = 0; i < productList.size(); i++) {
            String oid = productList.get(i);
            System.out.println("oid = " + oid);
            findPartOfProduct(oid, partNo, con01, dataList);
        }

        System.out.println("------ end -------");
    }


    public void findPartOfProduct(String productOID, String partNo, String con01, ArrayList<ProductDto> dataList) {
        SubaeCommonUtil.findPartOfProduct(productOID, partNo.trim(), con01, dataList);
    }

    //findPartOfProduct_v2

    /**
     * 자재번호가 사용되고 있는 모든 제품 찾기
     * @param whereCond
     * @return
     */
    public ArrayList<HashMap<String, String>> findPartOfProduct_v2(PartWhere whereCond) {
        return SubaeCommonUtil.findPartOfProduct_v2(whereCond);
    }

    public ArrayList<String> findUsedProductNo(ProductDto param) {
        return subaeMapper.findUsedProductNo();
    }

    /**
     * 2025-07 날짜로 수배율 계산한 제품번호 조회
     * @param param
     * @return
     */
    public ArrayList<ProductDto> findSubaeProductList(ProductDto param) {
        ArrayList<ProductDto> result = new ArrayList<>();

        ArrayList<ProductDto> r = subaeMapper.findSubaeProductList(param);

        return r;
    }

    // 2025-07 날짜로 수배율 계산한 PARTNO 전체 조회
    public ArrayList<ProductDto> findSubaePartNoList(ProductDto param) {
        ArrayList<ProductDto> result = new ArrayList<>();

        //System.out.println("findSubaePartNoList param = " + param);
        ArrayList<ProductDto> r = subaeMapper.findSubaePartNoList(param);

        return r;
    }

    /**
     * 변경자재 Top.10 (HX규격품 제외)
     * @return
     */
    public ArrayList<HashMap<String, String>> findTopModPartNo() {
        return subaeMapper.findTopModPartNo();
    }

    /**
     * 2025년 제품 개수
     * @return
     */
    public String findALLProductCount() { return subaeMapper.findALLProductCount(); }

    /**
     * 2025년 전체 자재 수 (선박제외)
     * @return
     */
    public String findALLPartCount() {
        return subaeMapper.findALLPartCount();
    }

    /**
     * 2025년 전체 수정 자재 수 (선박제외)
     * @return
     */
    public String findALLPartModCount() {
        return subaeMapper.findALLPartModCount();
    }


    /**
     * @apiNote PID코드의 라인 출력
     * @param pidName
     * @return
     */
    public ArrayList<ArrayList<String>> findPIDLineView(String pidName) {
        ArrayList<ArrayList<String>> result = PIDCommonUtil.findPIDLineView(pidName);
        return result;
    }

    //findPIDLineViewV2
    public ArrayList<ArrayList<String>> findPIDLineViewV2(String pidName, String pidOid) {
        ArrayList<ArrayList<String>> result = PIDCommonUtil.findPIDLineViewV2(pidName, pidOid);
        return result;
    }

    //findPIDLineMaptify
    public ArrayList<String> findPIDLineMaptify(String pidName) {
        ArrayList<String> result = PIDCommonUtil.findPIDLineMaptify(pidName);
        return result;
    }


    /**
     * @apiNote 두 호기의 영업사양 값 비교
     *          값 추출/코드 변환은 APIController.findElvSearch 와 동일한 경로를 사용한다.
     *          (ElvInfoCommonUtil.findElvSearchInfoV2 로 PLM DB 원본 추출
     *           -> 숫자 코드값은 doscoditm 매칭값으로 변환)
     * @param ho1 비교 호기 1
     * @param ho2 비교 호기 2
     * @return TYPE(그룹) / SPEC_VALUE(특성명) / SPEC_CODE(특성코드) / VALUE(호기1) / VALUE2(호기2)
     */
    public ArrayList<HashMap<String, String>> getSalesInfo(String ho1, String ho2) {

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String hogi1 = ho1 == null ? "" : ho1.trim().toUpperCase();
        String hogi2 = ho2 == null ? "" : ho2.trim().toUpperCase();

        log.info("getSalesInfo - hogi1 = {}, hogi2 = {}", hogi1, hogi2);

        if (hogi1.isEmpty() || hogi2.isEmpty()) {
            result.add(messageRow("비교할 두 호기를 모두 입력하세요."));
            return result;
        }

        // ① 호기별 영업사양 원본 추출 (findElvSearch 내부 기능)
        Map<String, String> row1 = findElvInfoRow(hogi1);
        Map<String, String> row2 = findElvInfoRow(hogi2);

        if (row1.isEmpty() && row2.isEmpty()) {
            result.add(messageRow("두 호기 모두 영업사양 정보를 찾을 수 없습니다. (" + hogi1 + ", " + hogi2 + ")"));
            return result;
        }
        if (row1.isEmpty()) {
            result.add(messageRow("호기 " + hogi1 + " 의 영업사양 정보를 찾을 수 없습니다."));
            return result;
        }
        if (row2.isEmpty()) {
            result.add(messageRow("호기 " + hogi2 + " 의 영업사양 정보를 찾을 수 없습니다."));
            return result;
        }

        // ② 비교 대상 컬럼 = 두 호기 컬럼의 합집합 (DB 컬럼 순서 유지)
        LinkedHashSet<String> columns = new LinkedHashSet<>(row1.keySet());
        columns.addAll(row2.keySet());

        // ③ 숫자 코드값은 한 번에 표시값으로 변환 (컬럼 수만큼 단건 조회하면 느리다)
        ArrayList<String> codeTargets = new ArrayList<>();
        for (String column : columns) {
            codeTargets.add(row1.get(column));
            codeTargets.add(row2.get(column));
        }
        HashMap<String, String> codeMap = ElvInfoCommonUtil.findCodeValues(codeTargets);

        // ④ 특성코드 -> 한글 특성명
        HashMap<String, String> titleMap = new HashMap<>();
        for (HashMap<String, String> field : MLBCommonUtil.getCodeField()) {
            String name = field.get("NAME");
            String tit = field.get("TIT");
            if (name != null && tit != null && !tit.isEmpty()) {
                titleMap.put(name.toUpperCase(), tit);
            }
        }

        for (String column : columns) {

            if (isInternalColumn(column)) continue;

            String value1 = displayValue(row1.get(column), codeMap);
            String value2 = displayValue(row2.get(column), codeMap);

            // 양쪽 모두 값이 없는 사양은 비교 의미가 없으므로 제외한다.
            //if (value1.trim().isEmpty() && value2.trim().isEmpty()) continue;

            String title = titleMap.get(column.toUpperCase());

            HashMap<String, String> map = new LinkedHashMap<>();
            map.put("TYPE", specGroup(column, title));                                  // 그룹
            map.put("SPEC_VALUE", (title == null || title.isEmpty()) ? column : title);  // 특성명
            map.put("SPEC_CODE", column);                                               // 특성코드
            map.put("VALUE", value1);                                                   // 호기1 값
            map.put("VALUE2", value2);                                                  // 호기2 값

            result.add(map);
        }

        log.info("getSalesInfo - 비교 항목 수 = {}", result.size());

        return result;
    }

    /**
     * @apiNote 호기 1건의 영업사양 원본(컬럼 -> 값) 추출
     */
    private Map<String, String> findElvInfoRow(String hogi) {
        ArrayList<HashMap<String, String>> rows = ElvInfoCommonUtil.findElvSearchInfoV2(hogi);
        if (rows == null || rows.isEmpty() || rows.get(0) == null) {
            return new LinkedHashMap<>();
        }
        return rows.get(0);
    }

    /**
     * @apiNote 숫자형 코드값이면 매칭된 표시값으로, 아니면 원본값 그대로
     *          (findElvSearch 의 isNumeric -> findCodeValue 판정과 동일)
     */
    private String displayValue(String value, HashMap<String, String> codeMap) {
        if (value == null) return "";
        if (ElvInfoCommonUtil.isNumeric(value)) {
            String matched = codeMap.get(value);
            if (matched != null && !matched.isEmpty()) {
                return matched;
            }
        }
        return value;
    }

    /**
     * @apiNote 비교에서 제외할 내부 키 컬럼 (객체 OUID 등 - 호기마다 항상 달라 의미가 없다)
     */
    private boolean isInternalColumn(String column) {
        if (column == null || column.isEmpty()) return true;
        String upper = column.toUpperCase();
        return upper.startsWith("VF$") || upper.startsWith("ID$");
    }

    /**
     * @apiNote 화면 그룹(TAB) 구분 - MD$ 는 기본정보, 한글명이 정의된 코드는 영업사양
     */
    private String specGroup(String column, String title) {
        String upper = column == null ? "" : column.toUpperCase();
        if (upper.startsWith("MD$")) return "기본정보";
        if (title != null && !title.isEmpty()) return "영업사양";
        return "기타";
    }

    /**
     * @apiNote 화면에서 alert 처리하는 메시지 행
     */
    private HashMap<String, String> messageRow(String msg) {
        HashMap<String, String> map = new HashMap<>();
        map.put("msg", msg);
        return map;
    }

    /**
     * @apiNote 특정호기의 전체 층 검사하여 사양값 추출
     * @param hogi
     * @return
     */
    public List<Map<String, Object>> getFloorInfoJson(String hogi) {

        List<Map<String, Object>> list = null; //mapper.readValue(jsonString, List.class);

        //N26143L01 층 몇개없는거
        //208618L17 층 많은거
        //https://plmpro.hdel.co.kr/plmetc/vault/getFloorInfo?prodNum=208618L17
        //String apiUrl = "https://plmpro.hdel.co.kr/plmetc/vault/getFloorInfo?prodNum=208618L17";
        String apiUrl = "https://plmpro.hdel.co.kr/plmetc/vault/getFloorInfo?prodNum=";
        apiUrl += hogi;

        try {
            // URL 객체 생성
            URL url = new URL(apiUrl);

            // HttpURLConnection 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // GET 방식 설정
            conn.setRequestMethod("GET");

            // 응답 타입 설정 (JSON, XML 등 필요에 맞게 변경 가능)
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            // 응답 데이터 읽기
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"))) {

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                // response.toString() → JSON 문자열
                String jsonString = response.toString();

                // ObjectMapper 생성
                ObjectMapper mapper = new ObjectMapper();

                // JSON 배열이므로 List<Map>으로 변환
                //List<Map<String, Object>> list = mapper.readValue(jsonString, List.class);
                list = mapper.readValue(jsonString, List.class);

                // 확인
                for (Map<String, Object> item : list) {
                    //System.out.println(item);
                }

                // 결과 출력
                //System.out.println("Response Data: " + response.toString());
            }

            // 연결 종료
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     * @apiNote PID 시뮬레이터 결과만 출력
     * @param hogi
     * @param pid
     * @param testVersion
     * @param floor
     * @return
     */
    public HashMap<String, String> pidExecute(String hogi, String pid, String testVersion, String floor, String isfloor, String type) {

        System.out.println("pidExecute ==============");
        HashMap<String, String> resultMap = new HashMap<>(); //mapper.readValue(jsonString, List.class);

        //https://plmpro.hdel.co.kr/plmetc/vault/pidExecute?hogi=208223L01&PID=EL_PB186A01&testVersion=on&isfloor&floor=

        String apiUrl = "https://plmpro.hdel.co.kr/plmetc/vault/pidExecute?";
        apiUrl += "hogi=" + hogi;
        apiUrl += "&PID=" + pid;
        apiUrl += "&testVersion=" + testVersion;
        apiUrl += "&isfloor=" + isfloor;
        apiUrl += "&floor=" + floor;
        apiUrl += "&type=" + type;

        //http://localhost/plmetc/vault/pidExecute?PID=EL_PB186A01&hogi=208223L01&testVersion=&isfloor=N&floor=
        //https://plmpro.hdel.co.kr/plmetc/vault/pidExecute?PID=EL_PB186A01&hogi=208223L01&testVersion=&isfloor=N&floor=

        try {
            // URL 객체 생성
            URL url = new URL(apiUrl);

            // HttpURLConnection 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // GET 방식 설정
            conn.setRequestMethod("GET");

            // 응답 타입 설정 (JSON, XML 등 필요에 맞게 변경 가능)
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            // 응답 데이터 읽기
            try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"))) {

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    // response.toString() → JSON 문자열
                    String jsonString = response.toString();

                    System.out.println(jsonString.toString());
                    // ObjectMapper 생성
                    ObjectMapper mapper = new ObjectMapper();


                // JSON → HashMap<String, String>
                /*HashMap<String, String> resultMap = mapper.readValue(
                        jsonString, new TypeReference<HashMap<String, String>>() {}
                );*/

                resultMap = mapper.readValue(
                        jsonString, new TypeReference<HashMap<String, String>>() {}
                );


                // HashMap 출력 예시
                for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }

                // 결과 출력
                //System.out.println("Response Data: " + response.toString());
            }

            // 연결 종료
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }
}
