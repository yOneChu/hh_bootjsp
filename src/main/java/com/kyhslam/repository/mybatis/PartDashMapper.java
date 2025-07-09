package com.kyhslam.repository.mybatis;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.dto.PartDashboardDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface PartDashMapper {

    void deleteAll(); //전체삭제


    //ArrayList<PartDashboardDTO> findByBlockNo(String blockNo);

    void savePartDashboard(@Param("partDto") PartDashboardDTO partDto);
}
