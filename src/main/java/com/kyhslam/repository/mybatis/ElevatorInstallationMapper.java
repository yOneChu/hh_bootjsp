package com.kyhslam.repository.mybatis;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.dto.ELVInfoAPI;
import com.kyhslam.dto.ProductDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface ElevatorInstallationMapper {


    void saveElvInstall(@Param("elvDto") HashMap<String, String> elvDto);
}
