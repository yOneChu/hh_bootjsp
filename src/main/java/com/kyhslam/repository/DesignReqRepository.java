package com.kyhslam.repository;

import com.kyhslam.domain.DesignRequest;
import com.kyhslam.domain.DesignRequestData;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DesignReqRepository {
    private final EntityManager em;

    public void reqSave(DesignRequest req) {
        em.persist(req);
    }

    public void reqDataSave(DesignRequestData reqData) {
        em.persist(reqData);
    }
}
