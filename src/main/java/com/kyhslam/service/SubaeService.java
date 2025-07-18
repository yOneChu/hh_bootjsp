package com.kyhslam.service;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.repository.SubaeRepository;
import com.kyhslam.repository.mybatis.SubaeMapper;
import com.kyhslam.util.SubaeCommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Service("SubaeService")
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
     * @param year
     * @param partNo
     * @return
     */
    public ArrayList<ProductDto> findPartOfProduct_v2(String year, String partNo) {
        return SubaeCommonUtil.findPartOfProduct_v2(year, partNo.trim());
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
}
