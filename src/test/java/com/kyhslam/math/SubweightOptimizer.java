package com.kyhslam.math;

public class SubweightOptimizer {

    public static class Result {
        int c;
        int d;
        double totalWeight;
        double totalThickness;

        public Result(int c, int d, double totalWeight, double totalThickness) {
            this.c = c;
            this.d = d;
            this.totalWeight = totalWeight;
            this.totalThickness = totalThickness;
        }

        @Override
        public String toString() {
            return String.format(
                    "최적 해:\n" +
                            "자재1 수량(C): %d\n" +
                            "자재2 수량(D): %d\n" +
                            "총 무게: %.2f kg\n" +
                            "총 두께: %.2f mm",
                    c, d, totalWeight, totalThickness
            );
        }
    }

    public static Result findOptimalSubweight(
            double x, double a, double y, double b, double e, double f) {

        Result bestResult = null;
        double bestScore = Double.MAX_VALUE;

        // C의 최대값 계산 (두께 제약)
        int maxC = (int)(f / a);

        // C를 최대부터 감소시키면서 탐색 (C가 가장 많이 들어가야 함)
        for (int c = maxC; c >= 0; c--) {
            double remainingThickness = f - (a * c);

            // 두께 제약 확인
            if (remainingThickness < 0) continue;

            // D의 최대값 계산 (두께 제약)
            int maxD = (int)(remainingThickness / b);

            // D를 조정하면서 무게 조건을 만족하는 최적값 찾기
            for (int d = 0; d <= maxD; d++) {
                double totalWeight = x * c + y * d;
                double totalThickness = a * c + b * d;

                // 무게 조건 확인: E 이상이어야 함
                if (totalWeight < e) continue;

                // 두께 조건 확인: F 미만이어야 함
                if (totalThickness >= f) continue;

                // 점수 계산: E에 가까울수록, F에 가까울수록 좋음
                // 무게는 E에 가까워야 하고, 두께는 F에 가까워야 함
                double weightDiff = totalWeight - e;
                double thicknessDiff = f - totalThickness;
                double score = weightDiff + thicknessDiff * 0.1; // 무게 우선

                // C가 큰 것을 우선하되, 조건을 만족하는 첫 번째 해를 찾으면 반환
                if (bestResult == null ||
                        (c > bestResult.c) ||
                        (c == bestResult.c && score < bestScore)) {
                    bestResult = new Result(c, d, totalWeight, totalThickness);
                    bestScore = score;
                }
            }

            // C가 큰 값에서 유효한 해를 찾았다면, 더 작은 C는 탐색하지 않음
            if (bestResult != null && bestResult.c == c) {
                break;
            }
        }

        return bestResult;
    }

    public static void main(String[] args) {
        // 예시 입력값
        double X = 30;  // 자재1 무게 (kg)
        double A = 33;  // 자재1 두께 (mm)
        double Y = 40;  // 자재2 무게 (kg)
        double B = 38;  // 자재2 두께 (mm)
        double E = 3000; // 필요 무게 (kg)
        double F = 2800; // 필요 두께 (mm)

        System.out.println("=== SUBWEIGHT 최적화 ===");
        System.out.println("입력 조건:");
        System.out.printf("자재1: 무게=%.0fkg, 두께=%.0fmm\n", X, A);
        System.out.printf("자재2: 무게=%.0fkg, 두께=%.0fmm\n", Y, B);
        System.out.printf("필요 무게: %.0fkg 이상\n", E);
        System.out.printf("필요 두께: %.0fmm 미만\n\n", F);

        Result result = findOptimalSubweight(X, A, Y, B, E, F);

        if (result != null) {
            System.out.println(result);
            System.out.println("\n검증:");
            System.out.printf("무게 조건: %.2f >= %.0f ? %s\n",
                    result.totalWeight, E, result.totalWeight >= E ? "✓" : "✗");
            System.out.printf("두께 조건: %.2f < %.0f ? %s\n",
                    result.totalThickness, F, result.totalThickness < F ? "✓" : "✗");
        } else {
            System.out.println("조건을 만족하는 해를 찾을 수 없습니다.");
        }
    }
}