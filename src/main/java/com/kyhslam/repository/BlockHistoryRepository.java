package com.kyhslam.repository;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.dto.DashDto;
import com.kyhslam.util.PLMBlockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.beans.BeanProperty;
import java.util.ArrayList;


@Repository
@Slf4j
public class BlockHistoryRepository implements IBlockHistory {


    private final JdbcTemplate basicTemplate;

    private final NamedParameterJdbcTemplate jdbcTemplate;


    public BlockHistoryRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.basicTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void saveBlockHistory(BlockHistoryDTO blockHistory) {

    String sql = """
                INSERT INTO BLOCK_HISTORY(BLOCKNO, BLOCKNAME, VERSION, GC_PRODUCT, PARTTYPE, BLOCK_OPT, MODDATE, PICK, PICKNAME, QTY, CMT, COLOR)
                VALUES(?, ?, ?, ?, ?, ?, ?,  ?,?,?,?,?)

            """;

        //System.out.println("blockHistory = " + blockHistory.toString());
        log.info("blockHistory = " + blockHistory.toString());

        basicTemplate.update(sql,
                blockHistory.getBlockNo(),
                blockHistory.getBlockName(),
                "1",
                blockHistory.getGc_product(),
                blockHistory.getPartType(),
                blockHistory.getBlock_opt(),
                blockHistory.getModDate(),

                blockHistory.getPick(),
                blockHistory.getPickName(),
                blockHistory.getQty(),
                blockHistory.getCmt(),
                blockHistory.getColor()
        );

        /*String sql = """
                INSERT INTO BLOCK_HISTORY(BLOCKNO, BLOCKNAME, VERSION, GC_PRODUCT, PARTTYPE, MODDATE, PICK, PICKNAME, QTY, CMT, COLOR)
                VALUES(:blockNo, :blockName, :version, :gc_product, :partType, :modDate, :pick, :pickName, :qty, :cmt, :color)
                
            """;

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("blockNo", blockHistory.getBlockNo())
                .addValue("blockName", blockHistory.getBlockName())
                .addValue("version", blockHistory.getVersion())
                .addValue("gc_product", blockHistory.getGc_product())
                .addValue("partType",  blockHistory.getPartType())
                .addValue("modDate", blockHistory.getModDate())
                .addValue("pick", blockHistory.getPick())
                .addValue("pickName", blockHistory.getPickName())
                .addValue("qty", blockHistory.getQty())
                .addValue("cmt", blockHistory.getCmt())
                .addValue("color", blockHistory.getColor());

        jdbcTemplate.update(sql, param);*/
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
            A.pick, A.pickName, A.qty,
            A.cmt, A.color
            FROM BLOCK_HISTORY A
            WHERE A.blockNo=:blockNo
        """;

        System.out.println("sql.toString() = " + sql.toString());

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("blockNo", blockNo);

        //return jdbcTemplate.queryForObject(sql, param, blockHistoryRowMapper());
        return (ArrayList<BlockHistoryDTO>) jdbcTemplate.query(sql, param, blockHistoryRowMapper());
    }


    private RowMapper<BlockHistoryDTO> blockHistoryRowMapper() {
        return BeanPropertyRowMapper.newInstance(BlockHistoryDTO.class); //camel 변환 지원
    }
}
