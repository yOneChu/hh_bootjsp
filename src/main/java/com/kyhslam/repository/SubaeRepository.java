package com.kyhslam.repository;

import com.kyhslam.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;


@Repository
@Slf4j
public class SubaeRepository {

    private final JdbcTemplate basicTemplate;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SubaeRepository(DataSource dataSource) {
        this.basicTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }




    //수배대상 db에 저장
    public void saveSubaeProduct(ProductDto param) {


        //오늘날짜시간
        //LocalDateTime now = LocalDateTime.now();
        //Timestamp timestamp = Timestamp.valueOf(now);

        //오늘날짜
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        String sql = """
                INSERT INTO subaeProduct(PRODUCTNO, BATCHDATE, PRODUCTVER, APPDATE, PARTNO, PARTNAME, BLOCK_OPT, BLOCKNO, QTY
                , CMT, UCHECK, GLCODE, m_ModCount, c_ModCount, one_ModCnt, two_ModCnt, three_ModCnt, allPartCnt, CREDATE, MODDATE, GISONG, E_MANAGER, M_MANAGER, PRODUCTNAME)
                VALUES(?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?, ?, ?, ?,?, ?)
            """;

        log.info("productDto = " + param.getProductNo());

        basicTemplate.update(sql,
                param.getProductNo(),
                todayValue,
                param.getProductVersion(),
                param.getProductAppdate(),
                param.getPartNo(),
                param.getPartName(),
                param.getBlock_opt(),
                param.getBlockNo(),
                param.getQty(),
                param.getCmt(),
                param.getUcheck(),
                param.getGlCode(),
                param.getM_ModCount(),
                param.getC_ModCount(),
                param.getOne_ModCount(),
                param.getTwo_ModCount(),
                param.getThree_ModCount(),
                param.getPart_size(),
                param.getProductCreDate(),
                param.getProductModDate(),
                param.getGisong(),
                param.getEmanager(),
                param.getMmanager(),
                param.getProductName()
        );
    }
}
