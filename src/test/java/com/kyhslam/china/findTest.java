package com.kyhslam.china;

import com.kyhslam.repository.JdbcTemplate.JdbcTemplateRepositoryV1;
import com.kyhslam.service.ChinaService;
import com.kyhslam.service.JdbcTestService;
import com.kyhslam.util.ChinaCommonUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class findTest {

    public static void main(String[] args) {



        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 날짜 포맷 지정 (YYYY-MM-DD)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("오늘부터 7일 전까지의 날짜:");

        // 오늘부터 7일 전까지 반복하여 출력
        for (int i = 1; i < 8; i++) {
            LocalDate pastDate = today.minusDays(i); // i일 전의 날짜 계산
            System.out.println(pastDate.format(formatter)); // 포맷에 맞춰 출력
        }
    }
}
