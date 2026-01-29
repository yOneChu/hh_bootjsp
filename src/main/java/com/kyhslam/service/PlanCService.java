package com.kyhslam.service;

import com.kyhslam.domain.PartPlanC;
import com.kyhslam.repository.PlanCRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanCService {

    private final PlanCRepository repository;

    @Transactional
    public void save(PartPlanC partPlanC) {
        repository.save(partPlanC);
    }


}
