package com.kyhslam.repository;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.repository.mybatis.BlockHistoryMapper;
import com.kyhslam.repository.mybatis.PartUtilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
@RequiredArgsConstructor
public class MybatisPartUtilRepository {

    private final PartUtilMapper partUtilMapper;


    /**
     * PLM에 등록된 중국자재 조회
     * @param partInfoDTO
     * @return
     */
    public ArrayList<PartInfoDTO> findOneToCNPart(PartInfoDTO partInfoDTO) {
        return partUtilMapper.findCNPart(partInfoDTO);
    }

}
