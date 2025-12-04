package com.kyhslam.math;

public class JAVA_PID {

    public static void main(String[] args) {
        calSubWeight();
    }


    //함수 = CALL
    public static void calSubWeight() {
        int X = 30; // 개당 무게
        int A = 33; // 개당 두께

        int Y = 40; // 개당 무게
        int B = 38; // 개당 두께

        int AA = 3301;  // 필요무게 (최소 충족
        int BB = 10000;  // 최대 제한두께 (초과 금지)

        int max = 200;  // 탐색범위

        boolean found = false;

        int bestC = -1;
        int bestD = -1;
        int bestSum1 = 0;
        int bestSum2 = 0;

        // 최적 기준을 위한 diff 값 (작을수록 좋음)
        int bestDiff1 = Integer.MAX_VALUE; // sum1 - AA (AA 초과분 최소화)
        int bestDiff2 = Integer.MAX_VALUE; // BB - sum2 (BB에 최대한 가깝게)

        System.out.println("조건을 만족하는 C, D 조합(전체):");
        System.out.println("--------------------------------");

        for (int C = 0; C <= max; C++) {
            for (int D = 0; D <= max; D++) {

                int sum1 = X * C + Y * D;  // 조건1: AA 이상
                int sum2 = A * C + B * D;  // 조건2: BB 미만

                // 조건 ① : sum1 >= AA
                if (sum1 < AA) continue;

                // 조건 ② : sum2 < BB
                if (sum2 >= BB) continue;

                // 조건을 만족하면 출력(원하면 주석 처리 가능)
                System.out.printf("C=%d, D=%d | X*C+Y*D=%d | A*C+B*D=%d%n",
                        C, D, sum1, sum2);

                // 여기서부터 "최적의 해" 갱신 로직
                int diff1 = sum1 - AA;  // AA를 얼마나 초과했는지 (0에 가까울수록 좋음)
                int diff2 = BB - sum2;  // BB에서 얼마나 모자라는지 (0에 가까울수록 좋음)

                // 1순위: diff1 (AA 초과분 최소화)
                // 2순위: diff2 (BB와의 차이 최소화)
                if (diff1 < bestDiff1 || (diff1 == bestDiff1 && diff2 < bestDiff2)) {
                    bestDiff1 = diff1;
                    bestDiff2 = diff2;
                    bestC = C;
                    bestD = D;
                    bestSum1 = sum1;
                    bestSum2 = sum2;
                    found = true;
                }
            }
        }

        System.out.println("--------------------------------");
        if (!found) {
            System.out.println("조건을 만족하는 조합이 없습니다.");
        } else {
            System.out.println("★ 최적의 해 (X*C+Y*D는 AA에, A*C+B*D는 BB에 가장 가까운 해)");
            System.out.printf("C=%d, D=%d%n", bestC, bestD);

            System.out.printf("X*C+Y*D = %d (AA=%d, 초과=%d)%n", bestSum1, AA, bestDiff1);
            System.out.printf("A*C+B*D = %d (BB=%d, 차이=%d)%n", bestSum2, BB, bestDiff2);
        }
    }
}
