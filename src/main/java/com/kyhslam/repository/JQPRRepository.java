package com.kyhslam.repository;

import com.kyhslam.domain.JQPR;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JQPRRepository {

    private final EntityManager em;

    public void save(JQPR jqpr) {
        em.persist(jqpr);
    }
}
