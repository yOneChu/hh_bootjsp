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

    /**
     * 전체 Block 기준정보 삭제
     */
    public void deleteAll() {

        blockHistoryMapper.deleteAll();
    }

    /**
     * BlockNo 정보 저장
     * @param blockHistory
     */
    public void saveBlockHistory(BlockHistoryDTO blockHistory, String version) {
        blockHistoryMapper.saveBlockHistory(blockHistory, version);
    }


    /**
     * BlockNo에 해당하는 이력 데이터 조회
     * @param blockNo
     * @return
     */
    public ArrayList<BlockHistoryDTO> findByBlockNo(String blockNo) {
        return blockHistoryMapper.findByBlockNo(blockNo);
    }
}
