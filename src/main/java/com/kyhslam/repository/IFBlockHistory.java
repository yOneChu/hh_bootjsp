package com.kyhslam.repository;


import com.kyhslam.dto.BlockHistoryDTO;

import java.util.ArrayList;

public interface IFBlockHistory {


    void saveBlockHistory(BlockHistoryDTO blockHistory, String version);

    ArrayList<BlockHistoryDTO> findByBlockNo(String blockNo);
}
