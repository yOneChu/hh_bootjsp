package com.kyhslam.math;

public class TT03 {

/*



    최적화된 SUBWEIGHT 를 구하는 문제야.
    자재1의 무게는 X kg, 두께는 A mm
    자재2의 무게는 Y kg, 두께는 B mm
    자재1의 수량은 C, 자재 2의 수량은 D라고 했을때
    최적화된 C와 D를 구하는 문제야.(단, C가 가장 많이 들어가는)
    단, 필요무게는 E 로 XC + YD 가 E 이상이 되어야하고(단 , XC+YD가 최대한 E에 가까운)
    필요두께는 F로 AC + BD 는 F 보다 작아야해 (단, AC+BD는 F에 최대한 가까운)
    X, A, Y, B , E, F는 상수로 주어질 거야
    예시로 X = 30, A = 33 Y = 40, B = 38 E = 3000kg F = 2800mm 구할 수 있는 JAVA 코딩해줘

    // 파이썬
    A = 0 // 개당 무게
    B = 38 // 개당 두께

    숫쟈 INT
    문자 STRING

*/


    public static void main(String[] args) {

        int X = 30; // 개당 무게
        int A = 33; // 개당 두께

        int Y = 40; // 개당 무게
        int B = 38; // 개당 두께

        int AA = 3301;  // 필요무게 (최소 충족)
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

