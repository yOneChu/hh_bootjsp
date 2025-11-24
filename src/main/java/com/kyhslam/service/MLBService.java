package com.kyhslam.service;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.MLBCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class MLBService {


    //수량 PID 종류도 보여주기.


    // 1.해당 년도의 내작 부품 조회
    public ArrayList<PartInfoDTO> findPartWithYear_inner(String year, String blockNo, String qtyPID) {

        ArrayList<PartInfoDTO> dataDTOList = new ArrayList<>();

        System.out.println("year == " + year);

        MLBCommonUtil.findDownLevelQtyPID(year, blockNo, qtyPID, dataDTOList);



        return dataDTOList;
    }





}
