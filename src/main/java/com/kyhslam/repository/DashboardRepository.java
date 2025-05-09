package com.kyhslam.repository;

import com.kyhslam.domain.DashPublic;
import com.kyhslam.domain.DashPublicData;
import com.kyhslam.domain.DashPublicExportPrice;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardRepository {


    private final EntityManager em;

    public void save(DashPublic partData) {
        em.persist(partData);
    }

    public DashPublic findById(String partType) {
        return em.createQuery("select o from DashPublic o where o.partName = :partType", DashPublic.class)
                .setParameter("partType", partType)
                .getSingleResult();
    }

    public List<DashPublic> findAll() {
        return em.createQuery("select o from DashPublic o", DashPublic.class)
                .getResultList();
    }

    public List<DashPublicData> findDataAll() {
        return em.createQuery("select o from DashPublic o", DashPublicData.class)
                .getResultList();
    }

    //data
    public List<DashPublicData> findByDatas(String partType) {
        return em.createQuery("select o from DashPublicData o where o.partType = :partType", DashPublicData.class)
                .setParameter("partType", partType)
                .getResultList();
    }


    public void savePartData(DashPublicData data) {
        em.persist(data);
    }


    /**
     * 출하일로 금액 집계
     * @param exportPrice
     */
    public void saveExportPrice(DashPublicExportPrice exportPrice) {
        em.persist(exportPrice);
    }


}
