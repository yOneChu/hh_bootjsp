package com.kyhslam.api;


import com.kyhslam.dto.PartDTO;
import com.kyhslam.util.MLBCommonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class findPartInfoWithList_Test {



    @Test
    public void testFindPartInfoWithList() {

        String partNoList = "11500324G010A,11500324G0200,11500324G0300,11500324G0400,11500324G0500,11500324G0600,11500324G0700";


        System.out.println("partNoList = " + partNoList);


        ArrayList<PartDTO> resultList = new ArrayList<>();

        resultList = MLBCommonUtil.findPartInfoWithList_v2(partNoList);

        for(PartDTO partDTO : resultList) {
            System.out.println("partDTO = " + partDTO);
            System.out.println(partDTO.getPartNo() + " > " + partDTO.getPartName());
        }


    }

}
