package com.kyhslam.subae;

import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.SubaeCommonUtil;

import java.util.ArrayList;

public class findTest {

    public static void main(String[] args) {

        SubaeCommonUtil ss = new SubaeCommonUtil();
        ArrayList<ProductDto> oidList = new ArrayList<>();
        String productNO = "207547L20";
        oidList = ss.findProductOIDS(productNO);

        for (int i = 0; i < oidList.size(); i++) {
            ProductDto dto = oidList.get(i);
            String oid = dto.getProductOid();
            String ver = dto.getVersion();
            String proNo = dto.getProductNo();

            //System.out.println(proNo + ">" + ver + ">" + oid);

            ss.checkDesignBOM(oid);

        }


    }
}
