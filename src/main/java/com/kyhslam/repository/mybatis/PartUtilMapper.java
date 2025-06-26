package com.kyhslam.repository.mybatis;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.dto.PartInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface PartUtilMapper {

    HashMap<String, String> findCOD(String module, String nation);



}
