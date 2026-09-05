package com.kyhslam.repository;

import com.kyhslam.dto.DocMenuDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * 문서 매뉴얼 사이드바 메뉴(PLM_DOC_MENU) CRUD.
 * 테이블 생성 스크립트 : src/main/resources/sql/PLM_DOC_MENU.sql
 */
@Repository
@Slf4j
public class DocMenuRepository {

    private final JdbcTemplate basicTemplate;

    public DocMenuRepository(DataSource dataSource) {
        this.basicTemplate = new JdbcTemplate(dataSource);
    }

    private static final String COLUMNS = """
            ID, MENU_TYPE, NAME, ICON, COLOR, GROUP_ID, PARENT_ID, SORT_NO,
            READ_URL, SAVE_URL, SAVE_METHOD, SAVE_FORMAT, SAVE_FIELD, USE_YN
            """;

    /** 사용 중인 메뉴 전체 — 대분류 → 명세서 → 하위 명세서 순서로 정렬 */
    public List<DocMenuDTO> findAll() {
        String sql = " SELECT " + COLUMNS +
                """
                  FROM PLM_DOC_MENU
                 WHERE USE_YN = 'Y'
                 ORDER BY SORT_NO, NAME
                """;
        return basicTemplate.query(sql, new BeanPropertyRowMapper<>(DocMenuDTO.class));
    }

    /** 한 카테고리(대분류)에 속한 명세서 전체 — 카테고리 고유 API 응답용 */
    public List<DocMenuDTO> findByGroup(String groupId) {
        String sql = " SELECT " + COLUMNS +
                """
                  FROM PLM_DOC_MENU
                 WHERE MENU_TYPE = 'SPEC' AND USE_YN = 'Y' AND GROUP_ID = ?
                 ORDER BY SORT_NO, NAME
                """;
        return basicTemplate.query(sql, new BeanPropertyRowMapper<>(DocMenuDTO.class), groupId);
    }

    public DocMenuDTO findById(String id) {
        String sql = " SELECT " + COLUMNS + " FROM PLM_DOC_MENU WHERE ID = ? ";
        List<DocMenuDTO> rows = basicTemplate.query(sql, new BeanPropertyRowMapper<>(DocMenuDTO.class), id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public boolean exists(String id) {
        Integer cnt = basicTemplate.queryForObject(
                " SELECT COUNT(*) FROM PLM_DOC_MENU WHERE ID = ? ", Integer.class, id);
        return cnt != null && cnt > 0;
    }

    /**
     * 다음 메뉴 코드 발급 — SPEC00001, SPEC00002 … (5자리, 00001부터)
     * 기존 코드(LOGIC_WRITE 등)는 형식이 달라 순번 계산에서 제외된다.
     */
    public String nextMenuCode() {
        String sql = """
                SELECT ISNULL(MAX(CAST(SUBSTRING(ID, 5, 5) AS INT)), 0) + 1
                  FROM PLM_DOC_MENU
                 WHERE LEN(ID) = 9
                   AND ID LIKE 'SPEC[0-9][0-9][0-9][0-9][0-9]'
                """;
        Integer next = basicTemplate.queryForObject(sql, Integer.class);
        int seq = (next == null || next < 1) ? 1 : next;
        // 같은 코드가 이미 있으면(손으로 넣은 경우 등) 빈 번호까지 넘긴다
        while (seq < 99999 && exists(formatMenuCode(seq))) seq++;
        return formatMenuCode(seq);
    }

    private String formatMenuCode(int seq) {
        return String.format("SPEC%05d", seq);
    }

    public int countGroups() {
        Integer cnt = basicTemplate.queryForObject(
                " SELECT COUNT(*) FROM PLM_DOC_MENU WHERE MENU_TYPE = 'GROUP' ", Integer.class);
        return cnt == null ? 0 : cnt;
    }

    /** 같은 위치(대분류 안 / 상위 명세서 안)의 마지막 순번 다음 값 */
    public int nextSortNo(String groupId, String parentId) {
        String sql = """
                SELECT ISNULL(MAX(SORT_NO), 0) + 10
                  FROM PLM_DOC_MENU
                 WHERE ISNULL(GROUP_ID, '')  = ?
                   AND ISNULL(PARENT_ID, '') = ?
                """;
        Integer no = basicTemplate.queryForObject(sql, Integer.class,
                groupId == null ? "" : groupId,
                parentId == null ? "" : parentId);
        return no == null ? 10 : no;
    }

    public int nextGroupSortNo() {
        Integer no = basicTemplate.queryForObject(
                " SELECT ISNULL(MAX(SORT_NO), 0) + 10 FROM PLM_DOC_MENU WHERE MENU_TYPE = 'GROUP' ",
                Integer.class);
        return no == null ? 10 : no;
    }

    public void insert(DocMenuDTO menu) {
        String sql = """
                INSERT INTO PLM_DOC_MENU
                    (ID, MENU_TYPE, NAME, ICON, COLOR, GROUP_ID, PARENT_ID, SORT_NO,
                     READ_URL, SAVE_URL, SAVE_METHOD, SAVE_FORMAT, SAVE_FIELD, USE_YN, CREATE_DATE)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,'Y', GETDATE())
                """;
        basicTemplate.update(sql,
                menu.getId(),
                menu.getMenuType(),
                menu.getName(),
                menu.getIcon(),
                menu.getColor(),
                menu.getGroupId(),
                menu.getParentId(),
                menu.getSortNo(),
                menu.getReadUrl(),
                menu.getSaveUrl(),
                menu.getSaveMethod(),
                menu.getSaveFormat(),
                menu.getSaveField()
        );
    }

    /** 이름 / 아이콘 변경 (icon 이 비어 있으면 기존 값 유지) */
    public int updateNameIcon(String id, String name, String icon) {
        String sql = """
                UPDATE PLM_DOC_MENU
                   SET NAME = ?, ICON = ISNULL(NULLIF(?, ''), ICON), UPDATE_DATE = GETDATE()
                 WHERE ID = ?
                """;
        return basicTemplate.update(sql, name, icon == null ? "" : icon, id);
    }

    /** 대분류에 속한 명세서 수 */
    public int countByGroup(String groupId) {
        Integer cnt = basicTemplate.queryForObject(
                " SELECT COUNT(*) FROM PLM_DOC_MENU WHERE MENU_TYPE = 'SPEC' AND GROUP_ID = ? ",
                Integer.class, groupId);
        return cnt == null ? 0 : cnt;
    }

    /** 하위 명세서 수 — 손자 이하 모든 단계를 포함해서 센다 */
    public int countByParent(String parentId) {
        String sql = """
                WITH SUB_TREE AS (
                    SELECT ID FROM PLM_DOC_MENU WHERE PARENT_ID = ?
                    UNION ALL
                    SELECT M.ID FROM PLM_DOC_MENU M
                      INNER JOIN SUB_TREE T ON M.PARENT_ID = T.ID
                )
                SELECT COUNT(*) FROM SUB_TREE
                """;
        Integer cnt = basicTemplate.queryForObject(sql, Integer.class, parentId);
        return cnt == null ? 0 : cnt;
    }

    public int delete(String id) {
        return basicTemplate.update(" DELETE FROM PLM_DOC_MENU WHERE ID = ? ", id);
    }

    /** 대분류 삭제 시 그 안의 명세서를 모두 정리 */
    public int deleteByGroup(String groupId) {
        return basicTemplate.update(" DELETE FROM PLM_DOC_MENU WHERE GROUP_ID = ? ", groupId);
    }

    /** 상위 명세서 삭제 시 하위를 모두 정리 — 손자 이하 모든 단계까지 지운다 */
    public int deleteByParent(String parentId) {
        String sql = """
                WITH SUB_TREE AS (
                    SELECT ID FROM PLM_DOC_MENU WHERE PARENT_ID = ?
                    UNION ALL
                    SELECT M.ID FROM PLM_DOC_MENU M
                      INNER JOIN SUB_TREE T ON M.PARENT_ID = T.ID
                )
                DELETE FROM PLM_DOC_MENU WHERE ID IN (SELECT ID FROM SUB_TREE)
                """;
        return basicTemplate.update(sql, parentId);
    }

    /**
     * 명세서 본문 자리를 미리 만들어 둔다.
     * 저장 API(update_PLM_DB_MetaData)가 UPDATE 문이라 행이 없으면 저장이 안 되기 때문.
     * (PLM_LLM_METADATA 에 다른 필수 컬럼이 있어 실패하더라도 메뉴 생성은 진행한다)
     */
    public void ensureContentRow(String category) {
        try {
            String sql = """
                    INSERT INTO PLM_LLM_METADATA (CATEGORY, CONTENT)
                    SELECT ?, ?
                     WHERE NOT EXISTS (SELECT 1 FROM PLM_LLM_METADATA WHERE CATEGORY = ?)
                    """;
            basicTemplate.update(sql, category, "", category);
        } catch (Exception e) {
            log.warn("PLM_LLM_METADATA 초기 행 생성 실패 (CATEGORY={}): {}", category, e.getMessage());
        }
    }
}
