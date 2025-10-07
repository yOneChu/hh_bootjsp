package com.kyhslam.repository;

import com.kyhslam.domain.JQPR;
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

    public List<JQPR> findAll(JqprSearchCond cond) {
        String jpql = "select i from JQPR i";

        String year = cond.getYear();
        String month = cond.getMonth();

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
        return query.getResultList();
    }
}
