package com.kyhslam.controller;


import com.kyhslam.dto.EcoDTO;
import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.eco.EcoCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController()
@RequestMapping("/eco")
@RequiredArgsConstructor
@Slf4j
public class EcoController {


    @Description("수량 PID 조회 로직")
    @PostMapping("/searchPartQtyPid")
    @CrossOrigin
    @ResponseBody
    public ArrayList<EcoDTO> searchPartQtyPid(String date, String key) {
        ArrayList<EcoDTO> resultList = new ArrayList<>();

        if ("subae".equals(key)) {
            resultList = EcoCommonUtil.getECOList(date);
        }

        return resultList;
    }

}
