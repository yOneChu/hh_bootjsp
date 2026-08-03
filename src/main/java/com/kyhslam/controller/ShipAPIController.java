package com.kyhslam.controller;

import com.kyhslam.dto.CodeInfoDTO;
import com.kyhslam.util.ShipCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@Slf4j
public class ShipAPIController {

    @Description("특성코드 리스트 - 선박")
    @GetMapping("/api/ship/getShipCode")
    @ResponseBody
    @CrossOrigin
    public ArrayList<CodeInfoDTO> getShipCode(String key) {
        //http://localhost:8070/api/ship/getShipCode?key=subae

        ArrayList<CodeInfoDTO> result = new ArrayList<CodeInfoDTO>();
        if ("subae".equals(key)) {
            result = ShipCommonUtil.getShipInfo();
        }

        return  result;
    }

    //공사정보 필드 리스트
    @Description("공사정보 필드 리스트 - 선박")
    @GetMapping("/api/ship/getShipField")
    @ResponseBody
    @CrossOrigin
    public ArrayList<HashMap<String, String>> getShipField(String key) {
        //http://localhost:8070/api/ship/getShipField?key=subae

        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        if ("subae".equals(key)) {
            result = ShipCommonUtil.getShipField();
        }

        return  result;
    }

}
