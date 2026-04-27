package com.kyhslam.part;

import com.kyhslam.domain.SubaeHogiBOM;
import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.repository.SubaeHogiRepository;
import com.kyhslam.repository.mybatis.SubaeMapper;
import com.kyhslam.service.SubaeService;
import com.kyhslam.util.PartCommonUtil;
import com.kyhslam.util.PartDashboardUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class findPartTest {

    @Autowired
    SubaeService subaeService;

    @Autowired
    SubaeMapper subaeMapper;

    @Autowired
    SubaeHogiRepository subaeHogiRepository;


    @Test
    void findPart() {
        ArrayList<PartInfoDTO> result = new ArrayList<>();
        result = PartDashboardUtil.findPLMPartV1("2025", "10111175G01*", "","ACTIVE");
        System.out.println(result.size());
    }


    @Test
    void findSubaeBOM() {
        List<SubaeHogiBOM> list = subaeHogiRepository.findSubaeBOMAsBlockNo("A101A");
        System.out.println("list.si = " + list.size());
    }

}

