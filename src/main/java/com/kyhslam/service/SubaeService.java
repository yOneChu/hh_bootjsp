package com.kyhslam.service;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.repository.SubaeRepository;
import com.kyhslam.util.SubaeCommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Service("SubaeService")
@RequiredArgsConstructor
public class SubaeService {


    private final SubaeRepository subaeRepository;


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
    public void findSubaeProductNo() {

        ArrayList<String> productNoList = new ArrayList<>();

        //1.2025년도 수배율 대상 제품번호 조회
        productNoList = SubaeCommonUtil.findSubaeProductNo();

        for (int i = 0; i < productNoList.size(); i++) {
            String productNo = productNoList.get(i);
            //2.제품의 모든 oid 조회
            ArrayList<ProductDto> productOIDS = SubaeCommonUtil.findProductOIDS(productNo);

            HashMap<String,String> map = new HashMap<>();
            HashSet<String> dupCheck = new HashSet<>();
            ArrayList<ProductDto> partList = new ArrayList<>();
            boolean flag = false;

            if(productOIDS != null && productOIDS.size() > 0){

                System.out.println(productNo + " = " + productOIDS.size());
                for (int j = 0; j < productOIDS.size(); j++) {

                    ProductDto d = productOIDS.get(j);
                    String oid = d.getProductOid();

                    //3.그 oid로 수배율 계산하기
                    if (flag == false) {
                        flag = SubaeCommonUtil.checkDesignBOM(oid, partList, map, dupCheck);
                    }
                    System.out.println("j = " + j);
                }

                for (int j = 0; j < partList.size(); j++) {
                    ProductDto p = partList.get(j);
                    p.setM_ModCount(map.get("m_ModCount"));
                    p.setC_ModCount(map.get("c_ModCount"));
                    p.setOne_ModCount(map.get("one_ModCount"));
                    p.setTwo_ModCount(map.get("two_ModCount"));
                    p.setThree_ModCount(map.get("three_ModCount"));
                    p.setProductAppdate(map.get("APP_DATE"));
                    p.setProductVersion(map.get("PROD_VERSION"));
                    subaeRepository.saveSubaeProduct(p);
                }
            }


        } // END FOR


    }


    //ProductNo로 수배율 데이터 집계 테스트
    public void subaeTest(String productNo) {

        //String productNo = "206504L05";


        ArrayList<ProductDto> productOIDS = SubaeCommonUtil.findProductOIDS(productNo);

        HashMap<String,String> map = new HashMap<>();
        HashSet<String> dupCheck = new HashSet<>();
        ArrayList<ProductDto> partList = new ArrayList<>();
        boolean flag = false;

        if(productOIDS != null && productOIDS.size() > 0){

            //System.out.println(productNo + " = " + productOIDS.size());
            for (int j = 0; j < productOIDS.size(); j++) {

                ProductDto d = productOIDS.get(j);
                String oid = d.getProductOid();

                //3.그 oid로 수배율 계산하기
                if (flag == false) {
                    flag = SubaeCommonUtil.checkDesignBOM(oid, partList, map, dupCheck);
                }
                //System.out.println("j = " + j);
            }

            for (int j = 0; j < partList.size(); j++) {
                ProductDto p = partList.get(j);
                p.setM_ModCount(map.get("m_ModCount"));
                p.setC_ModCount(map.get("c_ModCount"));
                p.setOne_ModCount(map.get("one_ModCount"));
                p.setTwo_ModCount(map.get("two_ModCount"));
                p.setThree_ModCount(map.get("three_ModCount"));
                p.setProductAppdate(map.get("APP_DATE"));
                p.setProductVersion(map.get("PROD_VERSION"));
                subaeRepository.saveSubaeProduct(p);
            }
        }
    }

}
