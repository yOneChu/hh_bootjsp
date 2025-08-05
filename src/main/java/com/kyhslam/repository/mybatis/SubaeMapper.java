package com.kyhslam.repository.mybatis;

import com.kyhslam.dto.ProductDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface SubaeMapper {


    void deleteAll(); //전체삭제

    ArrayList<String> findUsedProductNo();

    ArrayList<ProductDto> findSubaeProductList(ProductDto param);

    ArrayList<ProductDto> findSubaePartNoList(ProductDto param);

    ArrayList<HashMap<String, String>> findTopModPartNo();

    //void saveBlockHistory(@Param("blockDto") BlockHistoryDTO blockDto, @Param("version") String version);
}
