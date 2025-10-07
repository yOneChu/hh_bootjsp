package com.kyhslam.jqpl;

import com.kyhslam.domain.JQPR;
import com.kyhslam.dto.JqprDTO;
import com.kyhslam.repository.JQPRRepository;
import com.kyhslam.repository.JqprSearchCond;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JQPRRepositoryTest {

    @Autowired
    JQPRRepository jqprRepository;


    @Test
    void save() {

    }

    @Test
    void findAll() {

        JqprSearchCond jqprSearchCond = new JqprSearchCond();
        //jqprSearchCond.setYear("2025");
        //jqprSearchCond.setMonth("09");
        jqprSearchCond.setJqprNo("QN26526L037337");


        List<JQPR> result = jqprRepository.findAll(jqprSearchCond);

        System.out.println(result.size());
        for (JQPR jqpr : result) {
            System.out.println(jqpr.getJqprNo() + ">" + jqpr.getJqprType() + ">" + jqpr.getCreDate());
        }

        //List<JqprDTO> dtos = result.stream().map(JQPR::getJqprType).collect(Collectors.toList());


        //assertThat(result).containsExactly(items);

    }
}