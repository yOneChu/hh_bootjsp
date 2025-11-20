package com.kyhslam.service;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.MLBCommonUtil;

import java.util.ArrayList;

public class MLBService {


    // 1.해당 년도의 내작 부품 조회
    public void findPartWithYear_inner(String year, String blockNo) {

        //년도, blockNo로 부품 조회 (내작, 활성)
        ArrayList<PartInfoDTO> list = MLBCommonUtil.findPartWithYearBlockNo(year, blockNo);
        for (PartInfoDTO partInfoDTO : list) {

            // 2.하위 조회해서 수량 PID 찾기
            
            
        }
    }


    //findPartWithYear_V2


    //1.제품의 최신 oid 조회


    //2.제품의 1레벨 조회


    //3.


}
