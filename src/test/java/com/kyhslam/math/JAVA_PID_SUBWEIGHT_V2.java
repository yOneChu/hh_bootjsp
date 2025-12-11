package com.kyhslam.math;

import java.util.HashMap;
import java.util.regex.Pattern;

public class JAVA_PID_SUBWEIGHT_V2 {

    /**
     * 무게 소수점도 되게 테스트
     * @param args
     */

    public static void main(String[] args) {

        if (!isPositiveNumber("111") || !isPositiveNumber("ㄱㄴ") || !isPositiveNumber("25.8") )
        {
            System.out.println("asdf = ");
        }


       // calSubWeightAsDouble();

        //System.out.println(isPositiveNumber("-25"));




    }

    private static boolean isPositiveNumber(String str) {
        if (str == null || str.trim().isEmpty()) return false;

        // 양의 정수 또는 양의 실수(소수점 포함) 허용
        return str.matches("^\\d+(\\.\\d+)?$");
    }



    public static void calSubWeightAsDouble() {

        HashMap result = new HashMap<>();

        double X = Double.parseDouble("30"); // 개당 무게
        System.out.println("X = " + X);
        double A = 52; // 개당 두께
        double p1 = 1000; // X의 금액

        double Y = 30.1; // 개당 무게
        double B = 34; // 개당 두께
        double p2 = 1500; // Y의 금액

        double AA = 1805;   // 필요무게 (최소 충족)
        double BB = 3540; // 최대 제한두께 (초과 금지)

        int max = 200;  // 탐색범위

        boolean found = false;

        int bestC = -1, bestD = -1;

        double bestCost = Double.MAX_VALUE;
        double bestDiff1 = Double.MAX_VALUE;  // (sum1 - AA)
        double bestDiff2 = Double.MAX_VALUE;  // (BB - sum2)


        for (int C = 0; C <= max; C++) {
            for (int D = 0; D <= max; D++) {

                double sum1 = X * C + Y * D;
                double sum2 = A * C + B * D;

                if (sum1 < AA) continue;   // 무게 조건
                if (sum2 >= BB) continue;   // 두께 조건 (≤ BB 허용)

                double cost = p1 * C + p2 * D;

                double diff1 = sum1 - AA; // 최소 초과량
                double diff2 = BB - sum2; // 최대한 근접하도록

                if (cost < bestCost
                        || (cost == bestCost && (diff1 < bestDiff1
                        || (diff1 == bestDiff1 && diff2 < bestDiff2)))) {

                    bestCost = cost;
                    bestDiff1 = diff1;
                    bestDiff2 = diff2;
                    bestC = C;
                    bestD = D;
                    found = true;
                }
            }
        }

        if (!found) {
            result.put("SUB_BEST_Q1", "ERROR");
            result.put("SUB_BEST_Q2", "ERROR");
        } else {
            result.put("SUB_BEST_Q1", String.valueOf(bestC));
            result.put("SUB_BEST_Q2", String.valueOf(bestD));

            System.out.println("result = " + result);
        }


    }


}
