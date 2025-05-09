package com.kyhslam.china;

import com.kyhslam.repository.JdbcTemplate.JdbcTemplateRepositoryV1;
import com.kyhslam.service.ChinaService;
import com.kyhslam.service.JdbcTestService;
import com.kyhslam.util.ChinaCommonUtil;

import java.time.LocalDate;

public class findTest {

    public static void main(String[] args) {


        //ChinaCommonUtil c = new ChinaCommonUtil();
        //String partNo = "C121P012084";
        //c.getPartInfoWithCN(partNo);

        //ChinaService cs = new ChinaService();
        //cs.releasedPartsSave();

        //JdbcTemplateRepositoryV1 jdbcTemplateRepositoryV1 = new JdbcTemplateRepositoryV1();
        LocalDate now = LocalDate.now();
        String todayVal = now.toString();


    }
}
