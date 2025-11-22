package com.kyhslam.service;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.MLBCommonUtil;

import java.util.ArrayList;

public class MLBService {


    //수량 PID 종류도 보여주기.


    // 1.해당 년도의 내작 부품 조회
    public void findPartWithYear_inner(String year, String blockNo, String qtyPID) {

        ArrayList<PartInfoDTO> dataDTOList = new ArrayList<>();

        //년도, blockNo로 부품 조회 (내작, 활성)
        ArrayList<PartInfoDTO> list = MLBCommonUtil.findPartWithYearBlockNo(year, blockNo);

        for (int i = 0; i < list.size(); i++) {

            // 2.하위 조회해서 수량 PID 찾기
            PartInfoDTO parentDto = list.get(i);
            String parentOID = parentDto.getOid();

            MLBCommonUtil.findDownLevelQTY(parentDto, qtyPID, dataDTOList);
        }
    }


    //findPartWithYear_V2


    //1.제품의 최신 oid 조회


    //2.제품의 1레벨 조회


    //3.


}
