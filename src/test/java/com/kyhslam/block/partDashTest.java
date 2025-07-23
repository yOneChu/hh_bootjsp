package com.kyhslam.block;


import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.dto.PartDashboardDTO;
import com.kyhslam.repository.BlockHistoryRepository;
import com.kyhslam.repository.MyBatisBlockRepository;
import com.kyhslam.service.BlockHistoryService;
import com.kyhslam.service.PartDashService;
import com.kyhslam.util.PLMBlockUtil;
import com.kyhslam.util.PartDashboardUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
@Transactional
public class partDashTest {


    @Autowired
    PartDashService partDashService;



    @Autowired
    BlockHistoryService service;

    @Autowired
    BlockHistoryRepository blockHistoryRepository;

    @Autowired
    MyBatisBlockRepository myBatisBlockRepository;


    @Description("금일 자재 현황 집계 저장")
    @Commit
    @Test
    void save() {

        //현재날짜 구하기
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        String todayValue = now.format(formatter);
        System.out.println("todayValue = " + todayValue);
        partDashService.partDashIng();
    }


    @Test
    void findPartDashboard() {

        //현재날짜 구하기
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String todayValue = now.format(formatter);
        System.out.println("todayValue = " + todayValue);

        PartDashboardDTO d = new PartDashboardDTO();
        d.setBatchDate(todayValue);

        PartDashboardDTO dto = partDashService.findPartDashboard(d);

        System.out.println(dto.getPartALL() + " > " + dto.getPartActive());
    }

    @Test
    void findTopBlockPart() {

        ArrayList<HashMap<String, String>> r = PartDashboardUtil.findTopBlockPart(); //partDashService.findTopBlockPart();

        System.out.println(r);
    }


}
