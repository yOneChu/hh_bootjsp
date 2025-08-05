package com.kyhslam.util;

import java.text.DecimalFormat;

public class UtilCommonAPI {

    /**
     * @apiNote 숫자 문자열에 천 단위 구분 콤마를 추가합니다.
     * 예: "1234567" -> "1,234,567"
     *
     * @param numberString 콤마를 추가할 숫자 문자열
     * @return 콤마가 추가된 문자열 (유효하지 않은 숫자 형식의 경우 원본 문자열 반환 또는 예외 처리)
     */
    public static String formatNumberWithCommas(String numberString) {

        if (numberString == null || numberString.isEmpty()) {
            return numberString; // 또는 빈 문자열, 에러 처리
        }

        long number;
        try {
            number = Long.parseLong(numberString);
        } catch (NumberFormatException e) {
            // 유효하지 않은 숫자 문자열의 경우, 원본 문자열을 반환하거나
            // 특정 에러 메시지를 반환하거나, RuntimeException을 던질 수 있습니다.
            System.err.println("경고: 유효하지 않은 숫자 형식입니다: " + numberString);
            return numberString; // 또는 throw new IllegalArgumentException("Invalid number format", e);
        }

        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number);
    }


}
