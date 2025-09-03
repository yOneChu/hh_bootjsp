package com.kyhslam.mlb;

import com.kyhslam.util.MLBCommonUtil;
import org.springframework.util.StopWatch;

import java.util.ArrayList;

public class findAssyPart {

    public static void main(String[] args) {


        StopWatch sw = new StopWatch();
        sw.start();

        ArrayList<String> oids = MLBCommonUtil.findPartWithYear("20250903");

        System.out.println("oids = " + oids.size());


        ArrayList<String> data =  new ArrayList<>();

        for (int i = 0; i < oids.size(); i++) {
            System.out.println(i + " = " + oids.get(i));
            String oid = oids.get(i);
            MLBCommonUtil.findDownLevel(oid, data);
        }

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }
}
