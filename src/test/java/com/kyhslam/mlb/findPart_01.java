package com.kyhslam.mlb;

import com.kyhslam.util.MLBCommonUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class findPart_01 {

    @Test
    void findPart_01() {


        ArrayList<String> oids = MLBCommonUtil.searchPartOids("2024", "PLARTFORM");
        ArrayList<String> data = new ArrayList<>();

        System.out.println("oids.size() = " + oids.size());

        for (int i=0; i < oids.size(); i++) {
            String oid = oids.get(i);

            //MLBCommonUtil.findDownLevel(oid, data);

            if (i == 100) {
                break;
            }
        }


        System.out.println("data.size() = " + data.size());
    }
}
