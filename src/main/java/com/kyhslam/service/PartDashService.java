package com.kyhslam.service;

import com.kyhslam.dto.PartDashboardDTO;
import com.kyhslam.repository.mybatis.PartDashMapper;
import com.kyhslam.util.PartDashboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PartDashService {


    private final PartDashMapper partDashMapper ;


    public void partDashIng() {

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        //String todayValue = now.format(formatter);
        String todayValue = now.toString();

        //전체개수
        String allCnt = "";
        String active = "";
        String inActive = "";
        String batchDate = todayValue;


        allCnt = PartDashboardUtil.findPLMPartSum("ALL");
        active = PartDashboardUtil.findPLMPartSum("ACTIVE");
        inActive = PartDashboardUtil.findPLMPartSum("INACTIVE");


        PartDashboardDTO dto = new PartDashboardDTO();
        dto.setPartALL(allCnt);
        dto.setPartActive(active);
        dto.setPartInactive(inActive);
        dto.setBatchDate(batchDate);


        partDashMapper.savePartDashboard(dto);
    }
}
