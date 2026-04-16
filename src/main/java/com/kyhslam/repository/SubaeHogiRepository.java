package com.kyhslam.repository;

import com.kyhslam.domain.PlanCDash;
import com.kyhslam.domain.SubaeHogi;
import com.kyhslam.domain.SubaeHogiBOM;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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


    public List<SubaeHogi> findAll() {

        String sql = "select i from SubaeHogi i";



        TypedQuery<SubaeHogi> query = em.createQuery(sql, SubaeHogi.class);

        return query.getResultList();
    }

}
