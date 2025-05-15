package com.kyhslam.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;




class PartCommonUtilTest {

    @Test
    void findProductDownLevel() {


        String productOid = "2848884599";
        String partOid = "2832134272";


        ProductCommonUtil.findProductDownLevel(productOid, partOid);

    }
}