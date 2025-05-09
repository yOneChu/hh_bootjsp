package com.kyhslam.controller;

import com.kyhslam.service.ELVInfoService;
import com.kyhslam.util.ElvInfoCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ELVController {

    private final ELVInfoService elvInfoService;

    //https://vault.hdel.co.kr/dashboard/elvinfoDashboard
    @GetMapping("/elvinfoDashboard")
    public String elvinfoDashboard() {
        return "/dashboard/elvinfoDashboard";
    }

    @GetMapping("/elvinfoDashboardPop")
    public String elvinfoDashboardPop(String elvType, String month) {

        return "dashboard/elvinfoDashboardPopUp";
    }

    //https://10.225.80.35:8070/elvInfoMonth?month=202412
    @GetMapping("/elvInfoMonth")
    @ResponseBody
    public String elvInfoMonth(String month) {
        //elvInfoService.findELVInfoData(month);
        return "ok";
    }


    //https://10.225.80.35:8070/elvInfoMonthALL
    @GetMapping("/elvInfoMonthALL")
    @ResponseBody
    public String elvInfoMonthALL() {
        elvInfoService.findELVInfoData("202401");
        elvInfoService.findELVInfoData("202402");
        elvInfoService.findELVInfoData("202403");
        elvInfoService.findELVInfoData("202404");
        elvInfoService.findELVInfoData("202405");
        elvInfoService.findELVInfoData("202406");
        elvInfoService.findELVInfoData("202407");
        elvInfoService.findELVInfoData("202408");
        elvInfoService.findELVInfoData("202409");
        elvInfoService.findELVInfoData("202410");
        elvInfoService.findELVInfoData("202411");
        elvInfoService.findELVInfoData("202412");
        return "ok";
    }


    //데이터가지고 월별 집계 수행
    //https://10.225.80.35:8070/elvInfoDash
    @GetMapping("/elvInfoDash")
    @ResponseBody
    public String elvInfoDash() {
        ArrayList<String> types = ElvInfoCommonUtil.getTypeList();

        for (int i = 0; i < types.size(); i++) {
            //elvInfoService.calculateElvInfo(types.get(i));
        }

        return "OK";
    }
}
