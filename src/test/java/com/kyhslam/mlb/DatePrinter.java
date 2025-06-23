package com.kyhslam.mlb;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatePrinter {

    // 2025년 한국 공휴일 (고정 공휴일)
    private static final Set<LocalDate> HOLIDAYS_2025 = new HashSet<>();

    static {
        // 2025년 한국 공휴일 초기화
        HOLIDAYS_2025.add(LocalDate.of(2025, 1, 1));   // 신정
        HOLIDAYS_2025.add(LocalDate.of(2025, 1, 28));  // 설날 연휴
        HOLIDAYS_2025.add(LocalDate.of(2025, 1, 29));  // 설날
        HOLIDAYS_2025.add(LocalDate.of(2025, 1, 30));  // 설날 연휴
        HOLIDAYS_2025.add(LocalDate.of(2025, 3, 1));   // 삼일절
        HOLIDAYS_2025.add(LocalDate.of(2025, 5, 5));   // 어린이날
        HOLIDAYS_2025.add(LocalDate.of(2025, 5, 13));  // 석가탄신일
        HOLIDAYS_2025.add(LocalDate.of(2025, 6, 6));   // 현충일
        HOLIDAYS_2025.add(LocalDate.of(2025, 8, 15));  // 광복절
        HOLIDAYS_2025.add(LocalDate.of(2025, 10, 5));  // 추석 연휴
        HOLIDAYS_2025.add(LocalDate.of(2025, 10, 6));  // 추석
        HOLIDAYS_2025.add(LocalDate.of(2025, 10, 7));  // 추석 연휴
        HOLIDAYS_2025.add(LocalDate.of(2025, 10, 8));  // 추석 대체공휴일
        HOLIDAYS_2025.add(LocalDate.of(2025, 10, 9));  // 한글날
        HOLIDAYS_2025.add(LocalDate.of(2025, 12, 25)); // 크리스마스
    }

    /**
     * 해당 날짜가 공휴일인지 확인
     * @param date 확인할 날짜
     * @return 공휴일이면 true, 아니면 false
     */
    private static boolean isHoliday(LocalDate date) {
        return HOLIDAYS_2025.contains(date);
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

        // 특정 날짜 기준으로 영업일만 7일 출력
        LocalDate specificDate = LocalDate.of(2025, 6, 30);
        printLast7BusinessDaysFromDate(specificDate);

        System.out.println("\n" + "=".repeat(40) + "\n");

        // 오늘이 영업일인지 확인
        LocalDate today = LocalDate.now();
        System.out.println("오늘(" + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ")은 " +
                (isBusinessDay(today) ? "영업일입니다." : "영업일이 아닙니다."));
    }
}