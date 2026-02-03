package com.kyhslam.repository;

import com.kyhslam.domain.*;
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

    public void planDashSave(PlanCDash planCDash) {
        em.persist(planCDash);
    }

    //대시보드
    public List<PlanCDash> findPlanDash(String batchDate) {
        return em.createQuery("select o from PlanCDash o where o.batchDate = :batchDate", PlanCDash.class)
                .setParameter("batchDate", batchDate)
                .getResultList();
    }

    //엑셀 자재
    public List<PartPlanC> findAll() {
        return em.createQuery("select o from PartPlanC o", PartPlanC.class)
                .getResultList();
    }

    public List<ProductPlanC> findProductAll() {
        return em.createQuery("select o from ProductPlanC o", ProductPlanC.class)
                .getResultList();
    }


    public List<PartPlanC> findPartByBlock(String blockNo) {
        return em.createQuery("select o from PartPlanC o where o.blockNo = :blockNo", PartPlanC.class)
                .setParameter("blockNo", blockNo)
                .getResultList();
    }


    //제품
    public List<ProductPlanC> findProductByBlock(String blockNo) {
        return em.createQuery("select o from ProductPlanC o where o.blockNo = :blockNo", ProductPlanC.class)
                .setParameter("blockNo", blockNo)
                .getResultList();
    }

    public List<ProductPlanC> findProductByBatchDate(String batchDate) {
        return em.createQuery("select o from ProductPlanC o where o.batchDate = :batchDate and o.aspscd = :aspscd", ProductPlanC.class)
                .setParameter("batchDate", batchDate)
                .setParameter("aspscd", "KC01")
                .getResultList();
    }

    public List<ProductPlanC> findProductByBatchDate_v2(String batchDate, String blockNo) {
        return em.createQuery("select o from ProductPlanC o where o.blockNo = :blockNo and o.batchDate = :batchDate", ProductPlanC.class)
                .setParameter("blockNo", blockNo)
                .setParameter("batchDate", batchDate)
                .getResultList();
    }

    //원가실적조회로 조회 한게 있는지 검사
    public List<ProductPlanC> findProductByHogi(String productNo, String partNo) {
        return em.createQuery("select o from ProductPlanC o where o.productNo = :productNo and o.partNo = :partNo", ProductPlanC.class)
                .setParameter("productNo", productNo)
                .setParameter("partNo", partNo)
                .getResultList();
    }

    public List<ProductPlanC> findProductByPartNoBrand(String partNo, String brand) {
        return em.createQuery("select o from ProductPlanC o where o.brand = :brand and o.partNo = :partNo", ProductPlanC.class)
                .setParameter("brand", brand)
                .setParameter("partNo", partNo)
                .getResultList();
    }

}
