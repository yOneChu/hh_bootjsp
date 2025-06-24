package com.kyhslam.repository;

import com.kyhslam.repository.mybatis.BlockHistoryMapper;
import com.kyhslam.repository.mybatis.PartUtilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MybatisPartUtilRepository {

    private final PartUtilMapper partUtilMapper;



}
