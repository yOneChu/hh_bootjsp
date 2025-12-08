package com.kyhslam.math;


public class TT01 {

    public static void main(String[] args) {

        int X = 30;
        int A = 33;  // 기준1 무게
        int C = 0;   // 수량

        int Y = 40;
        int B = 38;  // 기준2 무게
        int D = 0;   // 수량

        int AA = 3000; // 필요 무게 (최소 충족해야 하는)
        int BB = 2600; // 제한 무게 (초과하면 안 되는)

        // 기준값 (C, D가 향해야 하는 중심)
        int targetC = AA / X; // 3000/30 = 100
        int targetD = BB / Y; // 2600/40 = 65

        int bestC = -1;
        int bestD = -1;
        int bestScore = Integer.MAX_VALUE;

        // 탐색 범위 (target 중심 +/– 50)
        for (int c = Math.max(0, targetC - 50); c <= targetC + 50; c++) {
            for (int d = Math.max(0, targetD - 50); d <= targetD + 50; d++) {

                int weight1 = X * c + Y * d;   // AA 조건용
                int weight2 = A * c + B * d;   // BB 조건용

                // 조건 1: 최소 충족 (AA <= (X*C + Y*D))
                if (weight1 < AA) continue;

                // 조건 2: BB > (A*C + B*D)
                if (weight2 >= BB) continue;

                // 최적화: C는 targetC와 가까울수록 좋음
                //        D는 targetD와 가까울수록 좋음
                int score = Math.abs(c - targetC) + Math.abs(d - targetD);

                if (score < bestScore) {
                    bestScore = score;
                    bestC = c;
                    bestD = d;
                }
            }
        }

        System.out.println("=== 결과 ===");
        System.out.println("C = " + bestC);
        System.out.println("D = " + bestD);
        System.out.println("조건1 X*C + Y*D = " + (X * bestC + Y * bestD));
        System.out.println("조건2 A*C + B*D = " + (A * bestC + B * bestD));
    }
}
