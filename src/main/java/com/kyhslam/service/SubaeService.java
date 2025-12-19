package com.kyhslam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.PartWhere;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.repository.SubaeRepository;
import com.kyhslam.repository.mybatis.SubaeMapper;
import com.kyhslam.util.ElvInfoCommonUtil;
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
    public ArrayList<ProductDto> findPartOfProduct_v2(PartWhere whereCond) {
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


    //findPIDLineMaptify
    public ArrayList<String> findPIDLineMaptify(String pidName) {
        ArrayList<String> result = PIDCommonUtil.findPIDLineMaptify(pidName);
        return result;
    }


    /**
     * @apiNote 호기의 영업사양 값 추출
     * @param ho1, ho2
     * @return
     */
    public ArrayList<HashMap<String, String>> getSalesInfo(String ho1, String ho2) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        System.out.println(ho1 + " " + ho2);

        ArrayList<HashMap<String, String>> v01 = new ArrayList<>();
        ArrayList<HashMap<String, String>> v02 = new ArrayList<>();

        if(ho1 != null && !"".equals(ho1)) {
            //result = ElvInfoCommonUtil.getSalesInfo(ho1);
            v01 = ElvInfoCommonUtil.getSalesInfo(ho1.trim());
        }

        if(ho2 != null && !"".equals(ho2)) {
            //result = ElvInfoCommonUtil.getSalesInfo(ho2);
            v02 = ElvInfoCommonUtil.getSalesInfo(ho2.trim());
        }

        if(v01 != null && v02 != null) {
            diffSum(v01, v02, result);
        }

        return result;
    }

    public void diffSum(ArrayList<HashMap<String, String>> v01, ArrayList<HashMap<String, String>> v02
                        , ArrayList<HashMap<String, String>> result) {

        for(int i=0; i < v01.size(); i++) {

            HashMap<String, String> temp01 = v01.get(i);
            HashMap<String, String> temp02 = v02.get(i);

            temp01.put("VALUE2", temp02.get("VALUE"));

            result.add(temp01);
        }

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
    public HashMap<String, String> pidExecute(String hogi, String pid, String testVersion, String floor, String isfloor) {

        System.out.println("pidExecute ==============");
        HashMap<String, String> resultMap = new HashMap<>(); //mapper.readValue(jsonString, List.class);

        //https://plmpro.hdel.co.kr/plmetc/vault/pidExecute?hogi=208223L01&PID=EL_PB186A01&testVersion=on&isfloor&floor=

        String apiUrl = "https://plmpro.hdel.co.kr/plmetc/vault/pidExecute?";
        apiUrl += "hogi=" + hogi;
        apiUrl += "&PID=" + pid;
        apiUrl += "&testVersion=" + testVersion;
        apiUrl += "&isfloor=" + isfloor;
        apiUrl += "&floor=" + floor;


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
