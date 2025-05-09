package com.kyhslam.repository;

import com.kyhslam.domain.ELVInfo;
import com.kyhslam.domain.ELVInfoDash;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ElvInfoRepository {

    private final EntityManager em;

    public void elvInfoSave(ELVInfo elv) {
        em.persist(elv);
    }


    public void dashSave(ELVInfoDash elv) {
        em.persist(elv);
    }
}