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
    public ArrayList<PartInfoDTO> findPIDasQTY(String year, String blockNo, String qtyPID) {

        ArrayList<PartInfoDTO> dataDTOList = new ArrayList<>();

        System.out.println("year == " + year);

        MLBCommonUtil.findDownLevelQtyPID(year, blockNo, qtyPID, dataDTOList);



        return dataDTOList;
    }


    // 2.해당 년도의 내작 부품 조회 - 수량PID 여러개(콤마 구분) OR 조회
    // yearCond : AFTER(해당년도 이후, 포함) / BEFORE(해당년도 이전)
    public ArrayList<PartInfoDTO> findPIDasQTYMulti(String year, String yearCond, String blockNo, String qtyPIDs) {

        ArrayList<PartInfoDTO> dataDTOList = new ArrayList<>();

        ArrayList<String> qtyPIDList = MLBCommonUtil.parseQtyPIDList(qtyPIDs);

        MLBCommonUtil.findDownLevelQtyPIDMulti(year, yearCond, blockNo, qtyPIDList, dataDTOList);

        return dataDTOList;
    }





}
