package com.kyhslam.controller;

import com.kyhslam.dto.ProductDto;
import com.kyhslam.service.PartUtilService;
import com.kyhslam.util.PartCommonUtil;
import com.kyhslam.util.ProductCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PartUtilController {


    /**
     * 제품의 최신 1레벨 조회
     * @param productNo
     * @return
     */
    @CrossOrigin
    @GetMapping("/part/findProductBom")
    public ArrayList<ProductDto> findProductBom(String productNo) {
        ArrayList<ProductDto> result = new ArrayList<>();
        result = ProductCommonUtil.findProductInfo(productNo);

        return result;
    }

}
