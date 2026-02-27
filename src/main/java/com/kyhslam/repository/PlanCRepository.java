package com.kyhslam.repository;

import com.kyhslam.domain.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
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

    public List<PlanCDash> findPlanDashAsBrand(String batchDate, String brand) {
        return em.createQuery("select o from PlanCDash o where o.batchDate = :batchDate and o.brand = :brand", PlanCDash.class)
                .setParameter("batchDate", batchDate)
                .setParameter("brand", brand)
                .getResultList();
    }

    public List<PlanCDash> findPlanDashAsBrand(String batchDate, String brand, String partName) {
        return em.createQuery("select o from PlanCDash o where o.batchDate = :batchDate and o.brand = :brand and o.partName = :partName", PlanCDash.class)
                .setParameter("batchDate", batchDate)
                .setParameter("brand", brand)
                .setParameter("partName", partName)
                .getResultList();
    }

    //SUBWEIGHT
    public List<PlanCDash> findPlanDashAsPartName(String batchDate, String partName) {
        return em.createQuery("select o from PlanCDash o where o.batchDate = :batchDate and o.partName = :partName", PlanCDash.class)
                .setParameter("batchDate", batchDate)
                .setParameter("partName", partName)
                .getResultList();
    }


    //총 수량
    public List<Tuple> findPlanDashSum(String batchDate) {
        return em.createQuery("select o.brand as brand, SUM(o.totalCnt) AS totalCntSum from PlanCDash o where o.batchDate = :batchDate " +
                        "GROUP BY o.brand", Tuple.class)
                .setParameter("batchDate", batchDate)
                .getResultList();
    }

    //엑셀 자재
    public List<PartPlanC> findAll() {
        return em.createQuery("select o from PartPlanC o", PartPlanC.class)
                .getResultList();
    }

    public List<PartPlanC> findPartByBlock(String blockNo) {
        return em.createQuery("select o from PartPlanC o where o.blockNo = :blockNo", PartPlanC.class)
                .setParameter("blockNo", blockNo)
                .getResultList();
    }

    public List<ProductPlanC> findProductAll() {
        return em.createQuery("select o from ProductPlanC o where o.aspscd = :aspscd", ProductPlanC.class)
                .setParameter("aspscd", "KC01")
                .getResultList();
    }

    public List<ProductPlanC> findProductAll_v2() {
        return em.createQuery("select o from ProductPlanC o where o.aspscd = :aspscd and o.indexNo is not null", ProductPlanC.class)
                .setParameter("aspscd", "KC01")
                .getResultList();
    }

    //제품
    public List<ProductPlanC> findProductByBlock(String blockNo) {
        return em.createQuery("select o from ProductPlanC o where o.blockNo = :blockNo and o.aspscd = :aspscd", ProductPlanC.class)
                .setParameter("blockNo", blockNo)
                .setParameter("aspscd", "KC01")
                .getResultList();
    }


    public List<ProductPlanC> findProductByBatchDate(String batchDate) {
        return em.createQuery("select o from ProductPlanC o where o.batchDate = :batchDate and o.aspscd = :aspscd", ProductPlanC.class)
                .setParameter("batchDate", batchDate)
                .setParameter("aspscd", "KC01")
                .getResultList();
    }



    public List<ProductPlanC> findProductByBatchDate_v2(String batchDate, String blockNo) {
        return em.createQuery("select o from ProductPlanC o where o.blockNo = :blockNo and o.batchDate = :batchDate and o.aspscd = :aspscd", ProductPlanC.class)
                .setParameter("blockNo", blockNo)
                .setParameter("batchDate", batchDate)
                .setParameter("aspscd", "KC01")
                .getResultList();
    }

    public List<ProductPlanC> findProductByBatchDate_v3(String batchDate, String partNo, String brand, String month) {
        return em.createQuery("select o from ProductPlanC o where o.batchDate = :batchDate and o.aspscd = :aspscd " +
                        "and o.partNo = :partNo and o.brand = :brand and o.erpSendDate like :erpSendDate", ProductPlanC.class)
                .setParameter("batchDate", batchDate)
                .setParameter("partNo", partNo)
                .setParameter("brand", brand)
                .setParameter("erpSendDate", month + "%")  // 2026-02%
                .setParameter("aspscd", "KC01")
                .getResultList();
    }

    public List<ProductPlanC> findProductByBatchDate_v4(String batchDate, String blockNo, String partNo, String month) {
        return em.createQuery("select o from ProductPlanC o where o.blockNo = :blockNo and o.batchDate = :batchDate and o.aspscd = :aspscd " +
                        "and o.partNo = :partNo and o.erpSendDate like :erpSendDate", ProductPlanC.class)
                .setParameter("blockNo", blockNo)
                .setParameter("partNo", partNo)
                .setParameter("batchDate", batchDate)
                .setParameter("erpSendDate", month + "%")  // 2026-02%
                .setParameter("aspscd", "KC01")
                .getResultList();
    }

    //원가실적조회로 조회 한게 있는지 검사
    public List<ProductPlanC> findProductByHogi(String productNo, String partNo) {
        return em.createQuery("select o from ProductPlanC o where o.productNo = :productNo and o.partNo = :partNo and o.aspscd = :aspscd", ProductPlanC.class)
                .setParameter("productNo", productNo)
                .setParameter("partNo", partNo)
                .setParameter("aspscd", "KC01")
                .getResultList();
    }

    public List<ProductPlanC> findProductByPartNoBrand(String partNo, String brand) {
        return em.createQuery("select o from ProductPlanC o where o.brand = :brand and o.partNo = :partNo and o.aspscd = :aspscd", ProductPlanC.class)
                .setParameter("brand", brand)
                .setParameter("partNo", partNo)
                .setParameter("aspscd", "KC01")
                .getResultList();
    }

    public List<ProductPlanC> findProductByPartName(String batchDate, String partName) {
        return em.createQuery("select o from ProductPlanC o where o.partName = :partName and o.batchDate = :batchDate and o.aspscd = :aspscd", ProductPlanC.class)
                .setParameter("partName", partName)
                .setParameter("batchDate", batchDate)
                .setParameter("aspscd", "KC01")
                .getResultList();
    }

}
