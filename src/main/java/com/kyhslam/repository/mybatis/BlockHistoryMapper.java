package com.kyhslam.repository.mybatis;

import com.kyhslam.dto.BlockHistoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Optional;

@Mapper
public interface BlockHistoryMapper {


    void deleteAll(); //전체삭제


    ArrayList<BlockHistoryDTO> findByBlockNo(String blockNo);

    void saveBlockHistory(@Param("blockDto") BlockHistoryDTO blockDto, @Param("version") String version);
}
