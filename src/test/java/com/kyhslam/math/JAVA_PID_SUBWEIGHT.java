package com.kyhslam.math;

import java.util.HashMap;
import java.util.regex.Pattern;

public class JAVA_PID_SUBWEIGHT {

    public static void main(String[] args) {

        calSubWeight();
    }


    public static void calSubWeight() {

        //VariantMap result = new VariantMap();
        HashMap result = new HashMap<>();

        int X = 40; // 개당 무게
        int A = 48; // 개당 두께
        int p1 = 10000; // X의 금액

        int Y = 30; // 개당 무게
        int B = 25; // 개당 두께
        int p2 = 18000; // Y의 금액

        int AA = 2500;   // 필요무게 (최소 충족)
        int BB = 3024; // 최대 제한두께 (초과 금지)

        int max = 200;  // 탐색범위


        boolean found = false;


        /*if (!isNumeric(X) && !isNumeric(A) && !isNumeric(p1) &&
                !isNumeric(Y) && !isNumeric(B) && !isNumeric(p2)) {

            result.put("BEST_C", "9999");
            result.put("BEST_D", "9999");
            //System.out.println("999999999999999999999");
            return result;
        }*/

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

            result.put("BEST_C", "999");
            result.put("BEST_D", "999");

        } else {
            System.out.println("★ 최적의 해");
            System.out.printf("C=%d, D=%d%n", bestC, bestD);
            System.out.println("bestC = " + bestC);
            System.out.println("bestD = " + bestD);

            result.put("SUB_BEST_Q1", String.valueOf(bestC));
            result.put("SUB_BEST_Q2", String.valueOf(bestD));

            System.out.printf("X*C+Y*D = %d (AA=%d, 초과=%d)%n", bestSum1, AA, bestDiff1);
            System.out.printf("A*C+B*D = %d (BB=%d, 차이=%d)%n", bestSum2, BB, bestDiff2);
            System.out.printf("총 비용 = %d (p1*C + p2*D)%n", bestCost);
        }
    }

    private static boolean isNumeric(String input) {
        /*if (str == null || str.trim().isEmpty()) {
            return false;
        }// 정규식: 음수 부호(-) 가능, 소수점(.) 가능

        return str.matches("-?\\d+(\\.\\d+)?");*/

        // 1. 공백 (null 또는 빈 문자열) 검사
        if (input == null || input.trim().isEmpty()) {
            // System.out.println("공백 또는 빈 문자열입니다.");
            return false;
        }

        // 공백 제거 (trim()은 1번 검사에서 이미 사용되었지만, 안전을 위해)
        String trimmedInput = input.trim();

        // 2. 음수 검사 (마이너스 부호 '-' 포함 여부)
        // 양수만 허용하므로, '-' 문자가 포함되면 false
        if (trimmedInput.startsWith("-")) {
            // System.out.println("음수입니다.");
            return false;
        }

        // 3. 숫자가 아닐 경우 검사 (정규 표현식 사용)
        // ^[0-9]+$ : 문자열 시작(^)부터 끝($)까지 오직 숫자(0-9)로만(+ 하나 이상) 이루어져 있는지 확인
        // 이 검사를 통과하면 '양의 정수' 형태의 문자열임이 확인됨
        if (!Pattern.matches("^[0-9]+$", trimmedInput)) {
            // System.out.println("숫자로만 이루어져 있지 않습니다.");
            return false;
        }

        // 추가: 0만 입력된 경우를 처리하고 싶다면 이 위치에 추가 가능
        // if (trimmedInput.equals("0")) {
        //     return false; // 0을 양의 정수가 아니라고 판단할 경우
        // }

        // 위의 모든 검사를 통과하면 유효한 양의 정수 문자열입니다.
        return true;

    }
}
