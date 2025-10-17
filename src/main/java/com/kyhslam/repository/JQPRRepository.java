package com.kyhslam.repository;

import com.kyhslam.domain.JQPR;
import com.kyhslam.dto.JqprDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JQPRRepository {

    private final EntityManager em;

    public void save(JQPR jqpr) {
        em.persist(jqpr);
    }

    public void update(Long id, JqprDTO updateParam) {
        JQPR jqpr = em.find(JQPR.class, id);
        jqpr.setJqprType(updateParam.getJqprType());
        jqpr.setTeam01(updateParam.getTeam01());
        jqpr.setTeam02(updateParam.getTeam02());
        jqpr.setTeam03(updateParam.getTeam03());
        jqpr.setTeam01Cost(updateParam.getTeam01Cost());
        jqpr.setTeam02Cost(updateParam.getTeam02Cost());
        jqpr.setTeam03Cost(updateParam.getTeam03Cost());
    }

    public List<JQPR> findAll(JqprSearchCond cond) {
        String jpql = "select i from JQPR i";

        String year = cond.getYear();
        String month = cond.getMonth();
        String status = cond.getState();

        /*if (StringUtils.hasText(year) || StringUtils.hasText(month) || StringUtils.hasText(cond.getJqprNo())) {
            jpql += " where";
        }
        boolean andFlag = false;*/

        jpql += " where";
        jpql += " i.jqprNo != '' ";


        if (StringUtils.hasText(year)) {
            jpql += " and";
            jpql += " i.creDate like concat('%',:year,'%')";
            //andFlag = true;
        }

        if(StringUtils.hasText(cond.getState())){
            jpql += " and";
            jpql += " i.status = :status ";
        }

        if (StringUtils.hasText(month)) {
           /* if (andFlag) {
                jpql += " and";
            }*/
            jpql += " and";
            //jpql += " i.price <= :maxPrice";
            jpql += " i.creDate like concat('%',:month,'%')";
        }

        if(StringUtils.hasText(cond.getJqprNo())){
            /*if (andFlag) {
                jpql += " and";
            }*/
            jpql += " and";
            jpql += " i.jqprNo = :jqprNo ";
        }

        if(StringUtils.hasText(cond.getTeam())){
            
            // 양산개발담당
            if("design".equals(cond.getTeam())){
                jpql += " and (";
                jpql += " i.team01 IN ( '수배로직설계팀', '중저속설계팀', '고속설계팀', '양산개발PM팀', '중저속SI팀', '고속SI팀') OR ";
                jpql += " i.team02 IN ( '수배로직설계팀', '중저속설계팀', '고속설계팀', '양산개발PM팀', '중저속SI팀', '고속SI팀') OR";
                jpql += " i.team03 IN ( '수배로직설계팀', '중저속설계팀', '고속설계팀', '양산개발PM팀', '중저속SI팀', '고속SI팀') ";
                jpql += " )";
            }
        }
        log.info("jpql={}", jpql);


        TypedQuery<JQPR> query = em.createQuery(jpql, JQPR.class);
        if (StringUtils.hasText(year)) {
            query.setParameter("year", year);
        }
        if (StringUtils.hasText(month)) {
            month = year + "-" + month;
            query.setParameter("month", month);
        }
        if (StringUtils.hasText(cond.getJqprNo())) {
            query.setParameter("jqprNo", cond.getJqprNo());
        }
        if (StringUtils.hasText(cond.getState())) {
            query.setParameter("status", cond.getState());
        }
        return query.getResultList();
    }
}
