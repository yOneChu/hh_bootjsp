package com.kyhslam.repository;

import com.kyhslam.domain.DashPublic;
import com.kyhslam.domain.DashPublicData;
import com.kyhslam.domain.PartPlanC;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlanCRepository {

    private final EntityManager em;

    public void save(PartPlanC partPlanC) {
        em.persist(partPlanC);
    }


    public List<PartPlanC> findAll() {
        return em.createQuery("select o from PartPlanC o", PartPlanC.class)
                .getResultList();
    }


    public List<PartPlanC> findByBlock(String blockNo) {
        return em.createQuery("select o from PartPlanC o where o.blockNo = :blockNo", PartPlanC.class)
                .setParameter("blockNo", blockNo)
                .getResultList();
    }






}
