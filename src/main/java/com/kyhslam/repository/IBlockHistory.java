package com.kyhslam.repository;


import com.kyhslam.dto.BlockHistoryDTO;

import java.util.ArrayList;

public interface IBlockHistory {


    void saveBlockHistory(BlockHistoryDTO blockHistory);

    ArrayList<BlockHistoryDTO> findByBlockNo(String blockNo);
}
