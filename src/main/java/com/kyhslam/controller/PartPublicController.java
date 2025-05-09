package com.kyhslam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PartPublicController {

    //localhost:8070/partPublicList

    ////부품공용화 - 집계목록
    @GetMapping("/public/partPublicList")
    public String partPublicList() {
        return "dashboard/partPublicList";
    }


    //부품공용화 - 수량
    @GetMapping("/public/searchPriceReductionRate")
    public String searchPriceReductionRate() {
        return "partPublic/searchPriceReductionRate";
    }

    //부품공용화 - 대수
    @GetMapping("/public/searchPriceReductionRev")
    public String searchPriceReductionRev() {
        return "partPublic/searchPriceReductionRev";
    }

    //부품공용화 - 출하예정일
    @GetMapping("/public/searchPriceReductionDate")
    public String searchPriceReductionDate() {
        return "partPublic/searchPriceReductionDate";
    }

    //부품공용화 - 금액
    @GetMapping("/public/searchPriceReductionDatePrice")
    public String publicPrice() {
        return "partPublic/searchPriceReductionDatePrice";
    }

}
