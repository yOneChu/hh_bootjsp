package com.kyhslam.mlb;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;


/**
 * 현재 날짜 기준으로 7일 이전의 날짜들을 출력
 */
public class DatePrinter_01 {

    /**
     * 해당 날짜가 고정 공휴일인지 확인
     * @param date 확인할 날짜
     * @return 공휴일이면 true, 아니면 false
     */
    private static boolean isHoliday(LocalDate date) {
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // 고정 공휴일 체크
        switch (month) {
            case 1:
                return day == 1; // 신정
            case 3:
                return day == 1; // 삼일절
            case 5:
                return day == 5; // 어린이날
            case 6:
                return day == 6; // 현충일
            case 8:
                return day == 15; // 광복절
            case 10:
                return day == 3 || day == 9; // 개천절, 한글날
            case 12:
                return day == 25; // 크리스마스
            default:
                return false;
        }

        // 참고: 실제로는 설날, 부처님오신날, 추석은 음력 기준이므로
        // 정확한 계산을 위해서는 음력-양력 변환 라이브러리가 필요합니다.
        // 현재는 고정 공휴일만 처리하고 있습니다.
    }

    /**
     * 해당 날짜가 주말인지 확인
     * @param date 확인할 날짜
     * @return 주말이면 true, 아니면 false
     */
    private static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * 해당 날짜가 영업일인지 확인 (주말과 공휴일 제외)
     * @param date 확인할 날짜
     * @return 영업일이면 true, 아니면 false
     */
    private static boolean isBusinessDay(LocalDate date) {
        return !isWeekend(date) && !isHoliday(date);
    }

    /**
     * 현재 날짜 기준으로 영업일만 7일 출력하는 함수 (주말, 공휴일 제외)
     */
    public static void printLast7BusinessDays() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("현재 날짜 기준 영업일만 7일:");

        int count = 0;
        int daysBack = 0;

        while (count < 7) {
            LocalDate date = currentDate.minusDays(daysBack);

            if (isBusinessDay(date)) {
                System.out.println(date.format(formatter) + " (" + getDayOfWeekKorean(date) + ")");
                count++;
            }
            daysBack++;
        }
    }

    /**
     * 현재 날짜 기준으로 영업일만 7일을 리스트로 반환하는 함수
     * @return 영업일 날짜 문자열 리스트
     */
    public static List<String> getLast7BusinessDays() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> businessDates = new ArrayList<>();

        int count = 0;
        int daysBack = 0;

        while (count < 7) {
            LocalDate date = currentDate.minusDays(daysBack);

            if (isBusinessDay(date)) {
                businessDates.add(date.format(formatter));
                count++;
            }
            daysBack++;
        }

        return businessDates;
    }

    /**
     * 특정 날짜 기준으로 영업일만 7일 출력하는 함수
     * @param baseDate 기준이 되는 날짜
     */
    public static void printLast7BusinessDaysFromDate(LocalDate baseDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println(baseDate.format(formatter) + " 기준 영업일만 7일:");

        int count = 0;
        int daysBack = 0;

        while (count < 7) {
            LocalDate date = baseDate.minusDays(daysBack);

            if (isBusinessDay(date)) {
                System.out.println(date.format(formatter) + " (" + getDayOfWeekKorean(date) + ")");
                count++;
            }
            daysBack++;
        }
    }

    /**
     * 요일을 한국어로 반환
     * @param date 날짜
     * @return 한국어 요일
     */
    private static String getDayOfWeekKorean(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY: return "월";
            case TUESDAY: return "화";
            case WEDNESDAY: return "수";
            case THURSDAY: return "목";
            case FRIDAY: return "금";
            case SATURDAY: return "토";
            case SUNDAY: return "일";
            default: return "";
        }
    }

    // 테스트용 메인 메서드
    public static void main(String[] args) {
        // 현재 날짜 기준으로 영업일만 7일 출력
        printLast7BusinessDays();

        System.out.println("\n" + "=".repeat(40) + "\n");

        // 리스트로 받아서 처리
        List<String> businessDates = getLast7BusinessDays();
        System.out.println("리스트로 반환된 영업일 날짜들:");
        businessDates.forEach(System.out::println);

        System.out.println("\n" + "=".repeat(40) + "\n");

        // 다른 연도 테스트 (2024년)
        LocalDate date2024 = LocalDate.of(2024, 12, 31);
        printLast7BusinessDaysFromDate(date2024);

        System.out.println("\n" + "=".repeat(40) + "\n");

        // 다른 연도 테스트 (2026년)
        LocalDate date2026 = LocalDate.of(2026, 3, 15);
        printLast7BusinessDaysFromDate(date2026);

        System.out.println("\n" + "=".repeat(40) + "\n");

        // 오늘이 영업일인지 확인
        LocalDate today = LocalDate.now();
        System.out.println("오늘(" + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ")은 " +
                (isBusinessDay(today) ? "영업일입니다." : "영업일이 아닙니다."));

        System.out.println("\n참고: 현재 고정 공휴일만 처리됩니다.");
        System.out.println("(신정, 삼일절, 어린이날, 현충일, 광복절, 개천절, 한글날, 크리스마스)");
        System.out.println("설날, 부처님오신날, 추석 등 음력 공휴일은 별도 처리가 필요합니다.");
    }
}
