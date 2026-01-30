package com.kyhslam.repository;

import com.kyhslam.domain.DashPublic;
import com.kyhslam.domain.DashPublicData;
import com.kyhslam.domain.PartPlanC;
import com.kyhslam.domain.ProductPlanC;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlanCRepository {

    private final EntityManager em;

    public void partSave(PartPlanC partPlanC) {
        em.persist(partPlanC);
    }

    public void productSave(ProductPlanC productPlanC) {
        em.persist(productPlanC);
    }


    public List<PartPlanC> findAll() {
        return em.createQuery("select o from PartPlanC o", PartPlanC.class)
                .getResultList();
    }

    public List<ProductPlanC> findProductAll() {
        return em.createQuery("select o from ProductPlanC o", ProductPlanC.class)
                .getResultList();
    }


    public List<PartPlanC> findByBlock(String blockNo) {
        return em.createQuery("select o from PartPlanC o where o.blockNo = :blockNo", PartPlanC.class)
                .setParameter("blockNo", blockNo)
                .getResultList();
    }

    //원가실적조회로 조회 한게 있는지 검사
    public List<ProductPlanC> findByBlock(String productNo, String partNo) {
        return em.createQuery("select o from ProductPlanC o where o.productNo = :productNo and o.partNo = :partNo", ProductPlanC.class)
                .setParameter("productNo", productNo)
                .setParameter("partNo", partNo)
                .getResultList();
    }






}
