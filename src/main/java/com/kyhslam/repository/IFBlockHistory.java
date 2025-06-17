package com.kyhslam.repository;


import com.kyhslam.dto.BlockHistoryDTO;

import java.util.ArrayList;
import java.util.Optional;

public interface IFBlockHistory {


    void saveBlockHistory(BlockHistoryDTO blockHistory, String version);

    ArrayList<BlockHistoryDTO> findByBlockNo(String blockNo);

    void updateBlockHistory(BlockHistoryDTO blockHistory);
}
