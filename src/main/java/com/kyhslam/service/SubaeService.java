package com.kyhslam.service;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.SubaeCommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service("SubaeService")
@RequiredArgsConstructor
public class SubaeService {


    /**
     * PLM에 등록된 법인자재 조회
     * @param param
     * @return
     */
    public ArrayList<PartInfoDTO> findOneFromPartNo(PartInfoDTO param) {
        ArrayList<PartInfoDTO> result = new ArrayList<PartInfoDTO>();
        result = SubaeCommonUtil.findOneFromPartNo(param);
        return result;
    }



}
