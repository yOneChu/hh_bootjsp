package com.kyhslam.util.pidTest;

import com.kyhslam.util.pidSimulatorUp.SimConsts;
import com.kyhslam.util.pidSimulatorUp.SimDb;

import java.sql.SQLException;
import java.util.*;

/**
 * PID 테스트 전용 DB 조회 (원본: Variant.getUsingSpecs(), searchProjectList.jsp)
 * PID 헤더/로직 조회는 SimPidRepository, 오류 기록은 SimVariant 를 사용한다.
 */
public class PidDao {

    private final SimDb db;

    public PidDao(SimDb db) {
        this.db = db;
    }

    /**
     * 해당 PID 에서 사용하는 영업사양(SPEC) 목록 (원본: Variant.getUsingSpecs(PID, version))
     * 수식 SPEC 이면 수식 안의 {변수} 를 먼저 넣고 수식 자체도 넣는다.
     */
    public List<String> getUsingSpecs(String pid, int version) throws SQLException {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        for (int i = 1; i <= SimConsts.MAX_SPEC_FIELD_SIZE; i++) {
            sql.append(" select distinct b.SPEC" + i + " as SPEC from variant_h a, variant_d b ");
            sql.append(" where a.houid = b.houid ");
            sql.append(" and   a.pid = ? ");
            sql.append(" and   a.version = ? ");
            if (i != SimConsts.MAX_SPEC_FIELD_SIZE) {
                sql.append(" union ");
            }
            params.add(pid);
            params.add(version);
        }

        Set<String> result = new LinkedHashSet<String>();
        for (Map<String, String> row : db.queryForList(sql.toString(), params.toArray())) {
            String spec = row.get("SPEC");
            if (spec == null || spec.trim().equals("")) {
                continue;
            }
            if (spec.contains("[$") && spec.contains("$]")) { // 수식사용하는 SPEC
                int startIdx = 0;
                while (spec.indexOf("{", startIdx) != -1) {
                    startIdx = spec.indexOf("{", startIdx);
                    int endIdx = spec.indexOf("}", startIdx);
                    if (endIdx == -1) {
                        break;
                    }
                    result.add(spec.substring(startIdx + 1, endIdx));
                    startIdx = endIdx;
                }
            }
            result.add(spec);
        }
        return new ArrayList<String>(result);
    }

    /** elv_info$vf 의 컬럼명 (대문자) - 현장 자동 조회 시 SQL 에 쓸 수 있는 SPEC 판별용 */
    public Set<String> getElvColumns() throws SQLException {
        Map<String, String> row = db.queryForFirst(" select * from elv_info$vf where rownum = 1 ");
        return row == null ? Collections.<String>emptySet() : new HashSet<String>(row.keySet());
    }

    /**
     * 테스트하기 적합한 현장번호 목록 (원본: searchProjectList.jsp)
     * 사용 SPEC 조합별로 최신 현장 1개씩, SPEC 이 없으면 랜덤 9개.
     *
     * @param columnSpecs elv_info$vf 컬럼으로 존재하는 SPEC 만 넘길 것
     * @param getMore true 면 최근 3년, false 면 최근 1년
     */
    public List<String> searchProjectList(List<String> columnSpecs, boolean getMore) throws SQLException {
        StringBuffer clause = new StringBuffer();
        for (String spec : columnSpecs) {
            if (spec.contains("EL_K")) {
                continue;
            }
            if (!spec.startsWith("EL") && !spec.startsWith("MANAGER")) {
                continue;
            }
            if (clause.length() > 0) {
                clause.append(", ");
            }
            clause.append(spec);
        }

        String dateRange = getMore ? "1095" : "365";

        StringBuffer sql = new StringBuffer();
        if (clause.length() > 0) {
            sql.append(" select /*+RULE*/ md$number from  ");
            sql.append(" ( ");
            sql.append("     select md$number,  ");
            sql.append(clause);
            sql.append("     , row_number() over (partition by " + clause + "  order by vf$ouid desc) rn  ");
            sql.append("     from elv_info$vf, elv_info$id ");
            sql.append("     where vf$Ouid=id$wip ");
            sql.append("     and md$cdate >= to_char(sysdate-" + dateRange + ",'yyyymmdd') ");
            sql.append("     and md$number like '______L__' ");
            sql.append(" ) where rn=1 ");
            sql.append(" order by dbms_random.value");
        } else {
            sql.append(" select /*+RULE*/ * from  ");
            sql.append(" ( ");
            sql.append("   SELECT md$number ");
            sql.append("     FROM elv_info$vf, elv_info$id ");
            sql.append("    WHERE     vf$Ouid = id$wip ");
            sql.append("          AND md$cdate >= TO_CHAR (SYSDATE -" + dateRange + ", 'yyyymmdd') ");
            sql.append("          AND md$number LIKE '______L__' ");
            sql.append(" ORDER BY DBMS_RANDOM.VALUE ");
            sql.append(" ) where rownum < 10 ");
        }

        List<String> list = new ArrayList<String>();
        try {
            for (Map<String, String> row : db.queryForList(sql.toString())) {
                list.add(row.get("MD$NUMBER"));
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] SQL :\n" + sql);
            throw e;
        }
        return list;
    }
}
