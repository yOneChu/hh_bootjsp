package com.kyhslam.subae;

import com.kyhslam.service.SubaeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.util.StopWatch;

@SpringBootTest
public class findTest_02 {


    @Autowired
    SubaeService subaeService;


    @Test
    void subaeDataTest() {
        StopWatch sw = new StopWatch();
        sw.start();

        //subaeService.subaeTest("207034L13");

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }


    @Description("BOM수배율 데이터 전체 테스트")
    @Test
    void subaeALLTest() {

        //200639Y78로 제품의 버전 확인해보기

        StopWatch sw = new StopWatch();
        sw.start();

        subaeService.findSubaeProductNo("209051L01");

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }

}
