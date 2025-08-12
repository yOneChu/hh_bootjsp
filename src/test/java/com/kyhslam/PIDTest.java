package com.kyhslam;

import com.kyhslam.util.PIDCommonUtil;
import org.springframework.util.StopWatch;

public class PIDTest {

    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();


        PIDCommonUtil.findCodeName("EL_ZORINO");

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);

    }
}
