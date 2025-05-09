package com.kyhslam.service;

import com.kyhslam.dto.DashDto;
import com.kyhslam.dto.DashDtoV2;
import com.kyhslam.dto.DashDtoV3;
import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.repository.JdbcTemplate.JdbcTemplateRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//@Service
@RequiredArgsConstructor
@Service("JdbcTestService")
public class JdbcTestService {

    private final JdbcTemplateRepositoryV1 template;


    public DashDto findById(String partName, String batchDate) {
        return template.findById(partName, batchDate);
    }

    public ArrayList<DashDto> findByAll(String batchDate) {
        return template.findByAll(batchDate);
    }

    public DashDtoV2 findByIdV2(String batchDate, String partName) {
        return template.findByIdV2(batchDate, partName);
    }

    public DashDtoV3 findByIdV3(String batchDate, String partName) {
        return template.findByIdV3(batchDate, partName);
    }
}
