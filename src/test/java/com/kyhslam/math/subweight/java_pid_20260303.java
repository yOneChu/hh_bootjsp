package com.kyhslam.math.subweight;

import java.util.HashMap;

public class java_pid_20260303 {


    public static void main(String[] args) throws Exception {


        FUNCTION_CALSUBWEIGHT();

    }


    //public VariantMap FUNCTION_CALSUBWEIGHT(Map elvEnt, List<Map> floorMasterList, Map partInfo)
    public static HashMap<String, String> FUNCTION_CALSUBWEIGHT()
            throws Exception {

        HashMap<String, String> result = new HashMap<String, String>();

        // 입력값 읽기
        String Xs  = "30"; //StringUtil.NVL(elvEnt.get("SUB_WT_1"), ""); //무게
        String As  = "67"; // StringUtil.NVL(elvEnt.get("SUB_H_1"), ""); // 높이
        String P1s = "1000"; //StringUtil.NVL(elvEnt.get("SUB_P_1"), "");

        String Ys  = "30"; //StringUtil.NVL(elvEnt.get("SUB_WT_2"), "");
        String Bs  = "41"; //StringUtil.NVL(elvEnt.get("SUB_H_2"), ""); // 높이
        String P2s = "2000"; //StringUtil.NVL(elvEnt.get("SUB_P_2"), "");

        String AA_s = "2380"; //StringUtil.NVL(elvEnt.get("SUB_NEED_WT"), "");
        String BB_s = "2375"; //StringUtil.NVL(elvEnt.get("SUB_MAX_LOAD_H"), "");

        // ★ 숫자 체크 (정수만 허용)
        if (!isInteger(Xs) || !isInteger(As) || !isInteger(P1s)
                || !isInteger(Ys) || !isInteger(Bs) || !isInteger(P2s)
                || !isInteger(AA_s) || !isInteger(BB_s)) {

            result.put("SUB_BEST_Q1", "9999");
            result.put("SUB_BEST_Q2", "9999");
            return result;
        }

        // 파싱
        int X  = Integer.parseInt(Xs);
        int A  = Integer.parseInt(As);
        int p1 = Integer.parseInt(P1s);

        int Y  = Integer.parseInt(Ys);
        int B  = Integer.parseInt(Bs);
        int p2 = Integer.parseInt(P2s);

        int AA = Integer.parseInt(AA_s);  // 최소 무게
        int BB = Integer.parseInt(BB_s);  // 최대 두께

        int max = 200;

        boolean found = false;
        int bestC = -1, bestD = -1;

        int bestCost = Integer.MAX_VALUE;
        int bestDiff1 = Integer.MAX_VALUE;  // (sum1 - AA)
        int bestDiff2 = Integer.MAX_VALUE;  // (BB - sum2)

        for (int C = 0; C <= max; C++) {
            for (int D = 0; D <= max; D++) {

                int sum1 = X * C + Y * D;
                int sum2 = A * C + B * D;

                if (sum1 < AA) continue;   // 무게 조건
                if (sum2 >= BB) continue;   // 두께 조건 (≤ BB 허용)

                int cost = p1 * C + p2 * D;

                int diff1 = sum1 - AA; // 최소 초과량
                int diff2 = BB - sum2; // 최대한 근접하도록

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
        }

        System.out.println("result = " + result);
        result.put("FUNCTION_CALSUBWEIGHT", "Y");
        return result;
    }

    /**
     * 숫자인지 판별하는 메소드 (소수점 포함)
     * 빈 값("")이나 문자열이 들어오면 false를 반환
     */
    private static boolean isInteger(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        return str.matches("-?\\d+"); // 음수/양수 정수만 허용
    }

}
