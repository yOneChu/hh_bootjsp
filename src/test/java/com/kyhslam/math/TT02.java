package com.kyhslam.math;

public class TT02 {

    public static void main(String[] args) {

        int X = 33;
        int A = 33;
        int C;

        int Y = 44;
        int B = 38;
        int D;

        int AA = 3000;  // 필요무게 (최소 충족)
        int BB = 2800;  // 최대 제한무게 (초과 금지)

        int max = 200;  // 탐색범위

        boolean found = false;

        System.out.println("조건을 만족하는 C, D 조합:");
        System.out.println("--------------------------------");

        for (C = 0; C <= max; C++) {
            for (D = 0; D <= max; D++) {

                int sum1 = X * C + Y * D;  // 조건1: AA
                int sum2 = A * C + B * D;  // 조건2: BB

                // 조건 ① 충족 여부
                if (sum1 < AA) continue;

                // 조건 ② 충족 여부
                if (sum2 >= BB) continue;

                // 조건을 만족하면 출력
                System.out.printf("C=%d, D=%d | X*C+Y*D=%d | A*C+B*D=%d%n",
                        C, D, sum1, sum2);

                found = true;
            }
        }

        if (!found) {
            System.out.println("조건을 만족하는 조합이 없습니다.");
        }
    }

}
