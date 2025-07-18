package com.kyhslam.subae;

import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.SubaeCommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class findTest {

    public static void main(String[] args) {

        SubaeCommonUtil ss = new SubaeCommonUtil();
        ArrayList<ProductDto> oidList = new ArrayList<>();
        String productNO = "210310L05";
        oidList = ss.findProductOIDS(productNO);

        HashMap<String,String> map = new HashMap<>();
        HashSet<String> dupCheck = new HashSet<>();

        ArrayList<ProductDto> partList = new ArrayList<>();

        boolean flag = false;
        for (int i = 0; i < oidList.size(); i++) {
            ProductDto dto = oidList.get(i);
            String oid = dto.getProductOid();
            String ver = dto.getVersion();
            String proNo = dto.getProductNo();



            System.out.println(proNo + " > " + ver +">>>"   + oid);

            if(flag == false) {
                flag = ss.checkDesignBOM(oid, partList, map, dupCheck);
            }



            System.out.println("000000000000000000000000000000");
            for (int k = 0; k < partList.size(); k++) {
                ProductDto dto1 = partList.get(k);
                System.out.println(dto1.getPartNo() + " > " + dto1.getBlockopt());


                //DB에 저장


            }
            System.out.println(partList.size());
            System.out.println("map = " + map);


        }

    }
}
