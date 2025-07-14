package com.kyhslam.repository.mybatis;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.dto.ProductDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface SubaeMapper {


    void deleteAll(); //전체삭제

    ArrayList<String> findUsedProductNo();

    //void saveBlockHistory(@Param("blockDto") BlockHistoryDTO blockDto, @Param("version") String version);
}
