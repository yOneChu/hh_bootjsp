package com.kyhslam.repository;

import com.kyhslam.dto.DashDto;
import com.kyhslam.dto.DashDtoV2;
import com.kyhslam.dto.DashDtoV3;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;


@Repository
public class SubaeRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SubaeRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


}
