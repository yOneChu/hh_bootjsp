package com.kyhslam;

import com.kyhslam.service.ChinaService;
import com.kyhslam.util.PIDCommonUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PIDTest {

    public static void main(String[] args) {

/*
        PIDCommonUtil p = new PIDCommonUtil();

        p.insert_Type01();

        p.insert_Type02();

        p.insert_Type03();
*/


        /*LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);
        System.out.println(formattedDate);
*/


        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 날짜 형식 지정 (예: yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 오늘부터 과거 6일 전까지 출력
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            System.out.println(date.format(formatter));
        }

        ChinaService chinaService = new ChinaService();
        chinaService.releasedPartsSave();

        System.out.println(" ------- end -------");
    }
}
