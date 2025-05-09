package com.kyhslam.controller;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.ChinaCommonUtil;
import com.kyhslam.util.PartCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Controller
@Slf4j
public class ChinaController {


    @PostMapping(value = "/china/getPartInfoWithKO")
    @ResponseBody
    public HashMap<String, String> getPartInfoWithKO(@RequestParam("partNo") String partNo) throws Exception {
        //http://localhost/plmetc/vault/getSalesData
        //http://localhost/plmetc/vault/getSalesData?HogiNumber=N22481L03
        log.info("VaultReportController.getPartInfoWithKO = {}");

        System.out.println("partNo = " + partNo);

        HashMap<String, String> oMap = new HashMap<String, String>();

        if(partNo != null && !"".equals(partNo)) {

            String[] list = partNo.split(",");

            //System.out.println("list = " + list.length);
            //System.out.println("list = " + Arrays.toString(list));

            for(int i=0; i < list.length; i++) {
                String pNo = list[i];
                oMap = PartCommonUtil.findOneFromPartNo(pNo.trim());
            }
        }

        return oMap;
    }

    /**
     * 중국자재 조회
     * @return
     * @throws Exception
     */
    @GetMapping("/china/findChinaParts")
    @ResponseBody
    public ArrayList<PartInfoDTO> findChinaParts() throws Exception {
        ArrayList<PartInfoDTO> result = new ArrayList<>();
        result = ChinaCommonUtil.findChinaParts();
        return result;
    }


    /**
     * 금일 릴리즈된 자재 조회
     * @return
     * @throws Exception
     */
    @GetMapping("/china/findReleasedParts")
    @ResponseBody
    public ArrayList<PartInfoDTO> findReleasedParts() throws Exception {
        ArrayList<PartInfoDTO> result = new ArrayList<>();
        result = ChinaCommonUtil.findReleasedParts();

        return result;
    }


}
