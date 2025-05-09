package com.kyhslam.controller;


import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.InventorCommonUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class InventorController {


    /**
     * 설계복사 > 호기의 상세 정보
     * @param productNo
     * @return
     * @throws Exception
     */
    @GetMapping("/inventor/findProductInfo")
    public HashMap<String, String> findProductInfo(String productNo) throws Exception {

        HashMap<String, String> result = new HashMap<>();
        result = InventorCommonUtil.findProductInfo(productNo);
        return result;
    }


    /**
     * 설계복사 > 제품의 하위 BOM 중 수정된 자재만 조회
     * @param productNo
     * @return
     * @throws Exception
     */
    @GetMapping("/inventor/findModParts")
    public ArrayList<PartInfoDTO> findModParts(String productNo) throws Exception {

        ArrayList<PartInfoDTO> result = new ArrayList<PartInfoDTO>();
        result = InventorCommonUtil.findModParts(productNo);
        return result;
    }
}
