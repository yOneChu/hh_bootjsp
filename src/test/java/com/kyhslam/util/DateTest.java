package com.kyhslam.util;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class DateTest {


    public record WeeklyRange(LocalDate startDate, LocalDate endDate) {
        @Override
        public String toString() {
            return "{" + startDate + " ~ " + endDate + "}";
        }
    }

    /**
     * 특정 기간 내의 1주일 단위 날짜 범위를 추출합니다.
     * 한 주의 시작 요일은 월요일(DayOfWeek.MONDAY), 종료 요일은 일요일(DayOfWeek.SUNDAY)입니다.
     *
     * @param startDate 전체 기간의 시작일
     * @param endDate   전체 기간의 종료일
     * @return 각 주의 시작일과 종료일이 담긴 WeeklyRange 리스트
     */
    public static List<WeeklyRange> getWeeklyDateRanges(LocalDate startDate, LocalDate endDate) {
        List<WeeklyRange> weeklyRanges = new ArrayList<>();

        // 1. 전체 기간의 시작일(startDate)이 속한 주의 시작일(월요일)을 찾습니다.
        LocalDate currentWeekStart = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 2. 만약 startDate가 월요일이 아닐 경우, 첫 번째 주의 시작일은 startDate로 시작하도록 조정합니다.
        //    (예: startDate가 수요일이면 첫 주는 수요일부터 시작)
        if (currentWeekStart.isBefore(startDate)) {
            currentWeekStart = startDate;
        }

        while (!currentWeekStart.isAfter(endDate)) {
            // 3. 현재 주의 종료일(일요일)을 찾습니다.
            LocalDate currentWeekEnd = currentWeekStart.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            // 4. 만약 현재 주의 종료일이 전체 기간의 종료일을 넘어서면,
            //    현재 주의 종료일은 전체 기간의 종료일로 설정합니다.
            if (currentWeekEnd.isAfter(endDate)) {
                currentWeekEnd = endDate;
            }

            weeklyRanges.add(new WeeklyRange(currentWeekStart, currentWeekEnd));

            // 5. 다음 주를 계산하기 위해 현재 주의 종료일 + 1일로 업데이트합니다.
            currentWeekStart = currentWeekEnd.plusDays(1);
        }

        return weeklyRanges;
    }


    // 메인 메서드에서 사용 예시
    public static void main(String[] args) {
        LocalDate startDate = LocalDate.of(2025, 7, 28); // 화요일
        LocalDate endDate = LocalDate.of(2025, 8, 31);   // 화요일

        System.out.println("--- 월요일 ~ 일요일 기준 날짜 범위 ---");
        List<WeeklyRange> weeklyRanges = getWeeklyDateRanges(startDate, endDate);
        for (WeeklyRange range : weeklyRanges) {
            System.out.println(range);
        }
    }

}
