package com.kyhslam.math;

public class pid_02 {

    public static void main(String[] args) {

        calSubWeight();
    }


    public static void calSubWeight() {
        int X = 31; // 개당 무게
        int A = 33; // 개당 두께
        int p1 = 1000; // X의 금액

        int Y = 30; // 개당 무게
        int B = 38; // 개당 두께
        int p2 = 2000; // Y의 금액

        int AA = 130;   // 필요무게 (최소 충족)
        int BB = 10000; // 최대 제한두께 (초과 금지)

        int max = 200;  // 탐색범위

        boolean found = false;

        int bestC = -1;
        int bestD = -1;
        int bestSum1 = 0;
        int bestSum2 = 0;
        int bestCost = Integer.MAX_VALUE;

        // 최적 기준을 위한 diff 값
        int bestDiff1 = Integer.MAX_VALUE; // sum1 - AA (AA 초과분 최소화)
        int bestDiff2 = Integer.MAX_VALUE; // BB - sum2 (BB에 최대한 가깝게)

        System.out.println("조건을 만족하는 C, D 조합(전체):");
        System.out.println("--------------------------------");

        for (int C = 0; C <= max; C++) {
            for (int D = 0; D <= max; D++) {

                int sum1 = X * C + Y * D;  // 조건1: AA 이상(무게)
                int sum2 = A * C + B * D;  // 조건2: BB 미만(두께)

                // 조건 ① : sum1 >= AA
                if (sum1 < AA) continue;

                // 조건 ② : sum2 < BB
                if (sum2 >= BB) continue;

                // 총 비용
                int cost = p1 * C + p2 * D;

                // 조건을 만족하면 출력(원하면 주석 처리 가능)
                System.out.printf("C=%d, D=%d | X*C+Y*D=%d | A*C+B*D=%d | cost=%d%n",
                        C, D, sum1, sum2, cost);

                // 여기서부터 "최적의 해" 갱신 로직
                int diff1 = sum1 - AA;  // AA를 얼마나 초과했는지 (0에 가까울수록 좋음)
                int diff2 = BB - sum2;  // BB에서 얼마나 모자라는지 (0에 가까울수록 좋음)

                /*
                 * 우선순위
                 * 1순위: cost (총 비용 최소)
                 * 2순위: diff1 (AA 초과분 최소화)
                 * 3순위: diff2 (BB와의 차이 최소화)
                 */
                if (cost < bestCost
                        || (cost == bestCost && (diff1 < bestDiff1
                        || (diff1 == bestDiff1 && diff2 < bestDiff2)))) {

                    bestCost = cost;
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
            System.out.println("★ 최적의 해");
            System.out.printf("C=%d, D=%d%n", bestC, bestD);

            System.out.printf("X*C+Y*D = %d (AA=%d, 초과=%d)%n", bestSum1, AA, bestDiff1);
            System.out.printf("A*C+B*D = %d (BB=%d, 차이=%d)%n", bestSum2, BB, bestDiff2);
            System.out.printf("총 비용 = %d (p1*C + p2*D)%n", bestCost);
        }
    }
}
