package com.kyhslam.publicTest;

import com.kyhslam.util.DateUtil;

/**
 * DateUtil 클래스의 기능을 시연하는 클래스
 * Class to demonstrate the functionality of DateUtil
 */
public class DateUtilDemo {

    public static void main(String[] args) {
        // 기본 날짜 형식 테스트 (Test default date format)
        System.out.println("오늘 날짜 (Today's date): " + DateUtil.getTodayDate());
        
        // 하이픈 없는 날짜 형식 테스트 (Test date format without hyphens)
        System.out.println("하이픈 없는 오늘 날짜 (Today's date without hyphens): " + DateUtil.getTodayDateNoHyphen());
        
        // 날짜와 시간 형식 테스트 (Test date and time format)
        System.out.println("오늘 날짜와 시간 (Today's date and time): " + DateUtil.getTodayDateTime());
        
        // 구분자 없는 날짜와 시간 형식 테스트 (Test date and time format without separators)
        System.out.println("구분자 없는 오늘 날짜와 시간 (Today's date and time without separators): " + 
                DateUtil.getTodayDateTimeNoSeparator());
        
        // 사용자 지정 형식 테스트 (Test custom format)
        System.out.println("사용자 지정 형식 (Custom format - MM/dd/yyyy): " + 
                DateUtil.getTodayDateWithFormat("MM/dd/yyyy"));
        
        // 레거시 Date 클래스 사용 테스트 (Test using legacy Date class)
        System.out.println("레거시 Date 사용 (Using legacy Date): " + DateUtil.getTodayDateUsingLegacyDate());
        
        // 한국어 날짜 형식 테스트 (Test Korean date format)
        System.out.println("한국어 날짜 형식 (Korean date format): " + DateUtil.getTodayDateKorean());
        
        System.out.println("\n모든 테스트가 완료되었습니다. (All tests completed.)");
    }
}