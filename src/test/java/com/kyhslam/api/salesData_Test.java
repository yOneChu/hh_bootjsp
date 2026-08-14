package com.kyhslam.api;

import com.kyhslam.service.SubaeHogiService;
import com.kyhslam.util.ElvInfoCommonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class salesData_Test {

    //@Autowired
    //SubaeHogiService subaeHogiService;


    @Test
    public void getSales() {

        ArrayList<HashMap<String, String>> resultData = ElvInfoCommonUtil.findElvSearchInfoV2("211704L17");

        // 결과 출력 (확인용)
        System.out.println("총 조회된 데이터 건수: " + resultData.size() + "건");

        for (Map<String, String> row : resultData) {
            System.out.println("--------------------------------------------------");
            // Map을 순회하며 Key(컬럼명)와 Value(데이터) 출력
            for (Map.Entry<String, String> entry : row.entrySet()) {
                //System.out.println(entry.getKey() + " : " + entry.getValue());

                if(ElvInfoCommonUtil.isNumeric(entry.getValue())) {
                    String resultVal = ElvInfoCommonUtil.findCodeValue(entry.getValue());

                    if(resultVal != null && !resultVal.isEmpty()) {
                        System.out.println(entry.getKey() + " : " + resultVal);
                    } else {
                        System.out.println(entry.getKey() + " : " + entry.getValue());
                    }

                } else {
                   System.out.println(entry.getKey() + " : " + entry.getValue());
                }
            }
        }
    }


}
