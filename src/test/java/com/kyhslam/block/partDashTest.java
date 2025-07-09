package com.kyhslam.block;


import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.repository.BlockHistoryRepository;
import com.kyhslam.repository.MyBatisBlockRepository;
import com.kyhslam.service.BlockHistoryService;
import com.kyhslam.service.PartDashService;
import com.kyhslam.util.PLMBlockUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

/*

    @Test
    void findBlockNo() {
        ArrayList<BlockHistoryDTO> findBlockDto = myBatisBlockRepository.findByBlockNo("B121A03");
        //System.out.println("findBlockDto = " + findBlockDto.toString());

        for(int i=0;i<findBlockDto.size();i++){
            System.out.println(findBlockDto.get(i).getBlockNo());
        }

    }

    @Description("전체조회")
    @Test
    void findAll() {
        List<BlockHistoryDTO> list = service.findAll();
        for(int i=0;i<list.size();i++){
            BlockHistoryDTO dto = list.get(i);
            System.out.println(dto.toString());
        }
    }


    @Description("금일날짜에 수정된 BlockNo History 찾기")
    @Test
    void findPLMBlockNo() {
        //현재날짜 구하기
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        String todayValue = now.format(formatter);

        System.out.println("todayValue = " + todayValue);

        PLMBlockUtil.findByTodayBlockNo();
    }
*/

}
