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

        //년도, blockNo로 부품 조회 (내작, 활성)
        ArrayList<PartInfoDTO> list = MLBCommonUtil.findPartWithYearBlockNo(year, blockNo);

        System.out.println(list.size());

        if (list.size() > 1000) {
            //너무많음
            PartInfoDTO parentDto = new PartInfoDTO();
            parentDto.setOid("TT");
            parentDto.setPartNo(String.valueOf(list.size()));
            dataDTOList.add(parentDto);

            return dataDTOList;
        }

        for (int i = 0; i < list.size(); i++) {

            // 2.하위 조회해서 수량 PID 찾기
            PartInfoDTO parentDto = list.get(i);
            MLBCommonUtil.findDownLevelQTY(parentDto, qtyPID, dataDTOList);
        }

        return dataDTOList;
    }





}
