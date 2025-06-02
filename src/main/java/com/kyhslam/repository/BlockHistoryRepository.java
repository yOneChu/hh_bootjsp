package com.kyhslam.repository;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.repository.mybatis.BlockHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Repository
@Slf4j
public class BlockHistoryRepository implements IFBlockHistory {


    private final JdbcTemplate basicTemplate;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    //private final BlockHistoryMapper blockHistoryMapper;

    public BlockHistoryRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.basicTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 전체 Block 기준정보 삭제
     */
    public void deleteAll() {
        String sql = "delete from block_history where BLOCKNO != '' ";
        basicTemplate.update(sql);
    }


    /**
     * BlockNo 정보 저장
     * @param blockHistory
     */
    @Override
    public void saveBlockHistory(BlockHistoryDTO blockHistory, String version) {


        //오늘날짜시간
        //LocalDateTime now = LocalDateTime.now();
        //Timestamp timestamp = Timestamp.valueOf(now);

        //오늘날짜
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

    String sql = """
                INSERT INTO BLOCK_HISTORY(BLOCKNO, BLOCKNAME, VERSION, UOM, GC_PRODUCT, PARTTYPE, BLOCK_OPT, MODDATE, CREDATE, PICK, PICKNAME, QTY, CMT, COLOR)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?,?,  ?,?,?,?,?)

            """;

        log.info("blockHistory = " + blockHistory.toString());

        basicTemplate.update(sql,
                blockHistory.getBlockNo(),
                blockHistory.getBlockName(),
                version,
                blockHistory.getUom(),
                blockHistory.getGc_product(),
                blockHistory.getPartType(),
                blockHistory.getBlock_opt(),
                blockHistory.getModDate(),
                todayValue, // DB 저장일

                blockHistory.getPick(),
                blockHistory.getPickName(),
                blockHistory.getQty(),
                blockHistory.getCmt(),
                blockHistory.getColor()
        );
    }


    /**
     * BlockNo에 해당하는 이력 데이터 조회
     * @param blockNo
     * @return
     */
    @Override
    public ArrayList<BlockHistoryDTO> findByBlockNo(String blockNo) {

        String sql = """
            SELECT
            A.blockNo, A.blockName, A.version, A.gc_product,
            A.partType, A.uom, A.block_opt,
            A.pick, A.pickName, A.qty, A.UOM, A.MODDATE,
            A.cmt, A.color
            FROM BLOCK_HISTORY A
            WHERE A.blockNo LIKE :blockNo
            ORDER BY version DESC
        """;

        System.out.println("sql.toString() = " + sql.toString());

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("blockNo", "%" + blockNo + "%");

        //return jdbcTemplate.queryForObject(sql, param, blockHistoryRowMapper());
        return (ArrayList<BlockHistoryDTO>) jdbcTemplate.query(sql, param, blockHistoryRowMapper());
    }

    public ArrayList<BlockHistoryDTO> findOneByBlockNo(String blockNo) {

        String sql = """
            SELECT
            A.blockNo, A.blockName, A.version, A.gc_product,
            A.partType, A.uom, A.block_opt,
            A.pick, A.pickName, A.qty, A.UOM, A.MODDATE,
            A.cmt, A.color
            FROM BLOCK_HISTORY A
            WHERE A.blockNo = :blockNo
            ORDER BY version DESC
        """;

        System.out.println("sql.toString() = " + sql.toString());

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("blockNo", blockNo);

        //return jdbcTemplate.queryForObject(sql, param, blockHistoryRowMapper());
        return (ArrayList<BlockHistoryDTO>) jdbcTemplate.query(sql, param, blockHistoryRowMapper());
    }

    /**
     * Block 기준정보 전체조회
     * @return
     */
    public List<BlockHistoryDTO> findAll() {

        String sql = """
            SELECT
            A.blockNo, A.blockName, A.version, A.gc_product,
            A.partType, A.uom, A.block_opt,
            A.pick, A.pickName, A.qty, A.UOM, A.MODDATE,
            A.cmt, A.color
            FROM BLOCK_HISTORY A
        """;

        System.out.println("sql.toString() = " + sql.toString());
        return jdbcTemplate.query(sql, blockHistoryRowMapper());
    }


    private RowMapper<BlockHistoryDTO> blockHistoryRowMapper() {
        return BeanPropertyRowMapper.newInstance(BlockHistoryDTO.class); //camel 변환 지원
    }
}
