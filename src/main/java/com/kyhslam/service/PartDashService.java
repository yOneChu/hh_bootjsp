package com.kyhslam.service;

import com.kyhslam.dto.PartDashboardDTO;
import com.kyhslam.repository.mybatis.PartDashMapper;
import com.kyhslam.util.PartDashboardUtil;
import jdk.jfr.Name;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

@Service("PartDashService")
@RequiredArgsConstructor
public class PartDashService {


    private final PartDashMapper partDashMapper ;

    @Scheduled(cron = "0 0 6 * * 1-5")
    public void partDashIng() {

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        //String todayValue = now.format(formatter);
        String todayValue = now.toString();

        //전체개수
        String allCnt = "";
        String active = "";
        String inActive = "";
        String ols = "";
        String batchDate = todayValue;


        allCnt = PartDashboardUtil.findPLMPartSum("ALL");
        active = PartDashboardUtil.findPLMPartSum("ACTIVE");
        inActive = PartDashboardUtil.findPLMPartSum("INACTIVE");
        ols = PartDashboardUtil.findPLMPartSum("OSL");


        PartDashboardDTO dto = new PartDashboardDTO();
        dto.setPartALL(allCnt);
        dto.setPartActive(active);
        dto.setPartInactive(inActive);
        dto.setBatchDate(batchDate);
        dto.setOls(ols);


        partDashMapper.savePartDashboard(dto);
    }


    //findPartDashboard
    public PartDashboardDTO findPartDashboard(PartDashboardDTO dto) {


        return partDashMapper.findPartDashboard(dto);
    }

    public ArrayList<HashMap<String,String>> findTopBlockPart() {

        return partDashMapper.findTopBlockPart();
    }


}
