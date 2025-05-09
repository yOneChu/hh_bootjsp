package com.kyhslam.controller;

import com.kyhslam.dto.ProductDto;
import com.kyhslam.service.PartUtilService;
import com.kyhslam.util.PartCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PartUtilController {


    @GetMapping("/part/productLists")
    public ArrayList<ProductDto> findProductList(String productNo) {
        ArrayList<ProductDto> result = new ArrayList<>();
        result = PartCommonUtil.findProductInfo(productNo);

        return result;
    }

}
