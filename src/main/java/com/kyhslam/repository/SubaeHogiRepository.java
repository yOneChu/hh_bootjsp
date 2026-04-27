package com.kyhslam.repository;

import com.kyhslam.domain.PlanCDash;
import com.kyhslam.domain.SubaeHogi;
import com.kyhslam.domain.SubaeHogiBOM;
import com.kyhslam.util.VaultDBConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SubaeHogiRepository {


    private final EntityManager em;

    //저장
    public void subaeHogisave(SubaeHogi subae) {
        em.persist(subae);
    }

    //저장-subaeBOM
    public void subaeHogiBOMsave(SubaeHogiBOM subae) {
        em.persist(subae);
    }


    //호기 조회
    public List<SubaeHogi> findSubaeHogi(String batchDate) {
        return em.createQuery("select o from SubaeHogi o where o.batchDate = :batchDate", SubaeHogi.class)
                .setParameter("batchDate", batchDate)
                .getResultList();
    }

    public List<SubaeHogi> findSubaeHogiAsCodat(String codat) {
        return em.createQuery("select o from SubaeHogi o where o.codat = :codat", SubaeHogi.class)
                .setParameter("codat", codat)
                .getResultList();
    }

    //findSubaeHogiLikeCodat
    public List<SubaeHogi> findSubaeHogiLikeCodat(String codat) {
        return em.createQuery("select o from SubaeHogi o where o.codat like :codat", SubaeHogi.class)
                .setParameter("codat", codat + "%")
                .getResultList();
    }


    //BlockNo로 수정된 해당 자재 조회
    public List<SubaeHogiBOM> findSubaeBOMAsBlockNo(String blockNo) {
        /*return em.createQuery("select o from SubaeHogiBOM o where o.blockNo = :blockNo AND o.ucheck = '1' ", SubaeHogiBOM.class)
                .setParameter("blockNo", blockNo)
                .getResultList();*/
        return em.createQuery("select o from SubaeHogiBOM o where o.blockNo = :blockNo ", SubaeHogiBOM.class)
                .setParameter("blockNo", blockNo)
                .getResultList();
    }

    public List<SubaeHogi> findAll() {

        String sql = "select i from SubaeHogi i";


        TypedQuery<SubaeHogi> query = em.createQuery(sql, SubaeHogi.class);

        return query.getResultList();
    }

    /**
     * 품목별 집계
     * @return
     */
    public ArrayList<HashMap<String, String>> findSummaryAsBlockNo() {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        try {

            con = VaultDBConnection.getConnection();

            String sql = """
                    SELECT
                        B.block_no AS BLOCKNO,
                        B.part_name AS PARTNAME,
                        COUNT(*) AS TOTAL_CNT,
                        SUM(CASE WHEN B.ucheck = '1' THEN 1 ELSE 0 END) AS MODIFY_CNT,
                        SUM(CASE WHEN B.ucheck = '1' THEN 0 ELSE 1 END) AS NOT_MODIFY_CNT,
                        ROUND(SUM(CASE WHEN B.ucheck = '1' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS modify_rate
                    FROM subaehogibom B
                        WHERE
                    --WHERE B.codate LIKE '202603%'
                     B.hogi NOT LIKE '%NC%'
                    AND B.hogi NOT LIKE 'V%'
                    AND B.hogi NOT LIKE 'Q%'
                    GROUP BY B.block_no, B.part_name
                    ORDER BY B.block_no, B.part_name
                    """;

            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            while(rs.next()) {
                String blockNo = rs.getString("BLOCKNO");
                String partName = rs.getString("PARTNAME");
                String totalCnt = rs.getString("TOTAL_CNT");
                String modifyCnt = rs.getString("MODIFY_CNT");
                String notModifyCnt = rs.getString("NOT_MODIFY_CNT");
                String modifyRate = rs.getString("modify_rate");


                HashMap<String, String> m = new HashMap<>();
                m.put("blockNo", blockNo);
                m.put("partName", partName);
                m.put("totalCnt", totalCnt);
                m.put("modifyCnt", modifyCnt);
                m.put("notModifyCnt", notModifyCnt);
                m.put("modifyRate", modifyRate);

                list.add(m);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con,pstmt,rs);
        }

        return list;
    }

}
