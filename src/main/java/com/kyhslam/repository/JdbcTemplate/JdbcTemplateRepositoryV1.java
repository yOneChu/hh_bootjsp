package com.kyhslam.repository.JdbcTemplate;

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
public class JdbcTemplateRepositoryV1 {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcTemplateRepositoryV1(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    //수량
    public DashDto findById(String partName, String batchDate) {
        String sql = """
                SELECT
                A.dash_public_id AS OID, A.part_name, A.batch_date, A.total_cnt,
                A.price202405, A.price202406, A.price202407, A.price202408, A.price202409,
                A.price202410, A.price202411, A.price202412,
                A.price202501, A.price202502, A.price202503, A.price202504, A.price202505
                FROM DASH_PUBLIC A
                WHERE A.BATCH_DATE=:batchDate
                AND A.part_name=:partName
        """;

        System.out.println("sql.toString() = " + sql.toString());
        
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("batchDate", batchDate)
                .addValue("partName", partName);

        return jdbcTemplate.queryForObject(sql, param, itemRowMapper());
    }


    //대수
    public DashDtoV2 findByIdV2(String batchDate, String partName) {
        String sql = """
                SELECT
                A.dash_public_id AS OID, A.part_name, A.batch_date, A.total_cnt,
                A.DIS202405, A.DIS202406, A.DIS202407, A.DIS202408, A.DIS202409,
                A.DIS202410, A.DIS202411, A.DIS202412,
                A.DIS202501, A.DIS202502, A.DIS202503, A.DIS202504, A.DIS202505
                FROM DASH_PUBLIC A
                WHERE A.BATCH_DATE =:batchDate
                AND A.part_name =:partName
        """;
        System.out.println("sql.toString() = " + sql.toString());

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("batchDate", batchDate)
                .addValue("partName", partName);

        return jdbcTemplate.queryForObject(sql, param, itemRowMapperV2());
    }

    /**
     * 출하예정일
     * @param batchDate
     * @param partName
     * @return
     */
    public DashDtoV3 findByIdV3(String batchDate, String partName) {
        String sql = """
                SELECT
                A.dash_public_id AS OID, A.part_name, A.batch_date, A.total_cnt,
                A.export202405, A.export202406, A.export202407, A.export202408, A.export202409,
                A.export202410, A.export202411, A.export202412,
                A.export202501, A.export202502, A.export202503, A.export202504, A.export202505, export202506, export202507,
                A.export202508, A.export202509, A.export202510, A.export202511, A.export202512, A.export_etc
                FROM DASH_PUBLIC A
                WHERE A.BATCH_DATE =:batchDate
                AND A.part_name =:partName
        """;
        System.out.println("sql.toString() = " + sql.toString());

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("batchDate", batchDate)
                .addValue("partName", partName);

        return jdbcTemplate.queryForObject(sql, param, itemRowMapperV3());
    }


    public ArrayList<DashDto> findByAll(String batchDate) {

        String sql = """
                SELECT
                A.dash_public_id AS OID, A.part_name, A.batch_date, A.total_cnt,
                A.price202405, A.price202406, A.price202407, A.price202408, A.price202409,
                A.price202410, A.price202411, A.price202412,
                A.price202501, A.price202502, A.price202503, A.price202504, A.price202505,
                
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202405' AND A.batch_date =:batchDate) AS EXPORT05,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202406' AND A.batch_date =:batchDate) AS EXPORT06,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202407' AND A.batch_date =:batchDate) AS EXPORT07,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202408' AND A.batch_date =:batchDate) AS EXPORT08,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202409' AND A.batch_date =:batchDate) AS EXPORT09,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202410' AND A.batch_date =:batchDate) AS EXPORT10,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202411' AND A.batch_date =:batchDate) AS EXPORT11,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202412' AND A.batch_date =:batchDate) AS EXPORT12,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202501' AND A.batch_date =:batchDate) AS EXPORT13,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202502' AND A.batch_date =:batchDate) AS EXPORT14,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202503' AND A.batch_date =:batchDate) AS EXPORT15,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202504' AND A.batch_date =:batchDate) AS EXPORT16,
                (SELECT COUNT(DISTINCT A.hogi) FROM dash_publicdata A WHERE SUBSTRING(A.export_date, 1, 6) = '202505' AND A.batch_date =:batchDate) AS EXPORT17 
                FROM DASH_PUBLIC A
                WHERE A.BATCH_DATE=:batchDate
        """;

        System.out.println("sql.toString() = " + sql.toString());

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate)
                .addValue("batchDate", batchDate);
                //.addValue("partName", partName);

        return (ArrayList<DashDto>) jdbcTemplate.query(sql, param, itemRowMapper());
    }


    private RowMapper<DashDto> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(DashDto.class); //camel 변환 지원
    }

    private RowMapper<DashDtoV2> itemRowMapperV2() {
        return BeanPropertyRowMapper.newInstance(DashDtoV2.class); //camel 변환 지원
    }

    private RowMapper<DashDtoV3> itemRowMapperV3() {
        return BeanPropertyRowMapper.newInstance(DashDtoV3.class); //camel 변환 지원
    }


    /*public Optional<PartInfoDTO> findById(Long id) {
        String sql = "select id, item_name, price, quantity from items where id = ?";

        try {
           PartInfoDTO dto = template.queryForObject(sql, itemRowMapper(), id);
           return Optional.of(dto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private RowMapper<PartInfoDTO> itemRowMapper() {
        return ((rs, rowNum) -> {
            PartInfoDTO dto = new PartInfoDTO();
            dto.setPartNo(rs.getString("PARTNO"));
            dto.setVersion(rs.getString("VERSION"));

            System.out.println("dto.getPartNo() = " + dto.getPartNo());
            return dto;
        });
    }

    public List<PartInfoDTO> findAll() {

        List<PartInfoDTO> result = new ArrayList<>();

        String sql = "SELECT PARTNO, VERSION FROM CHINA_PART";

        System.out.println("sql = " + sql.toString());
        //template.query(sql, itemRowMapper());
        return template.query(sql, itemRowMapper());
    }*/

}
