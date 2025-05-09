package com.kyhslam.controller;

import com.kyhslam.util.PartCommonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class SapController {


    @GetMapping("/sap/searchProductingStatus")
    public String searchProductingStatusViwe() {

        return "/sap/searchProductingStatus";
    }

    @PostMapping("/sap/getProductIngStatus")
    @ResponseBody
    public ArrayList<HashMap<String, String>> getProductIngStatus(String hogiNumber) {
        System.out.println("searchProductIngStatus hogiNumber = " + hogiNumber);
        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();

        if(hogiNumber != null && !"".equals(hogiNumber)) {
            resultList = PartCommonUtil.searchProductIngStatus(hogiNumber);
        }
        return resultList;
    }


    @GetMapping("/sap/getExportDate")
    public String getExportDate() {

        return "/sap/searchExportIngStatus";
    }


    @PostMapping("/sap/getExportDate")
    @ResponseBody
    public ArrayList<HashMap<String, String>> getExportDate(String hogiNumber) throws Exception {

        ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
        if(hogiNumber != null && !"".equals(hogiNumber)) {
            resultList = PartCommonUtil.getExportDate(hogiNumber);
        }
        return resultList;
    }
}
