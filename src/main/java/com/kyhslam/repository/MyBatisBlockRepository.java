package com.kyhslam.repository;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.repository.mybatis.BlockHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisBlockRepository {

    private final BlockHistoryMapper blockHistoryMapper;


    public void deleteAll() {

    }

    public void saveBlockHistory(BlockHistoryDTO blockHistory, String version) {

    }



    public ArrayList<BlockHistoryDTO> findByBlockNo(String blockNo) {
        return blockHistoryMapper.findByBlockNo(blockNo);
    }
}
