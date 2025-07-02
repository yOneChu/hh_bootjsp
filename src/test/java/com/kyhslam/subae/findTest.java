package com.kyhslam.subae;

import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.SubaeCommonUtil;

import java.util.ArrayList;

public class findTest {

    public static void main(String[] args) {

        SubaeCommonUtil ss = new SubaeCommonUtil();
        ArrayList<ProductDto> list = new ArrayList<>();
        list = ss.findFirstProduct("");


        System.out.println("list.size() = " + list.size());
        for (int i = 0; i < 100; i++) {

            ProductDto d = list.get(i);
            System.out.println(d.getProductNo() + " > " + d.getVersion());

        }



    }
}
