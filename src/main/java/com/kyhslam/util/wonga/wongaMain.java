package com.kyhslam.util.wonga;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * 원가절감 실적조회 (jsp/plmetc/product/ListSearchOfCondition/ListOfBasedOnDrwNumber.jsp) 단독 실행.
 * 화면으로 실행하려면 WongaWebServer 를 실행.
 */
public class wongaMain {

    public static void main(String[] args) {

        // ===== 조회 조건 (ListOfBasedOnDrwNumber_left.jsp 입력 항목) =====
        DrwNumberSearchCondition cond = new DrwNumberSearchCondition();
        cond.gLCode = "";               // G_L_CODE (도면번호)
        cond.chkDetail = false;         // 호기별 세부 사양 조회
        cond.elAtyp = "";               // 기종 (콤마로 여러 건)
        cond.elEcsf = "";               // CAR SAFETY
        cond.elEtm = "";                // TM
        cond.elAspd1 = "";              // 속도 ~
        cond.elAspd2 = "";
        cond.elAman1 = "";              // 인승 ~
        cond.elAman2 = "";
        cond.elEcjj1 = "";              // CAR출입구폭;JJ ~
        cond.elEcjj2 = "";
        cond.partNo = "";               // 자재번호 (콤마로 여러 건, * 와일드카드)
        cond.blockNo = "";              // BLOCK NO. (콤마로 여러 건)
        cond.startDate = "2026-01-01";  // Start 날짜
        cond.endDate = "2026-10-15";    // End 날짜 (최대 1년)
        cond.chkGeneral = false;        // 일반현장만 조회
        cond.chkBom = false;            // 현장 BOM 추출 (자재번호 필수)
        // cond.specList = DrwNumberSearchCondition.DEFAULT_SPEC_LIST; // 추출데이터

        String outputFile = "원가절감실적조회_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";

        DrwNumberSearchResult result = null;
        try {
            result = DrwNumberSearchService.search(cond);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        } catch (TimeoutException e) {
            System.out.println("실행 중 타임아웃으로 전체 종료 되었습니다. 조회 조건 추가 또는 기간을 줄인 후 재 실행 부탁드립니다.");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        int cnt = result.size();
        System.out.println("cnt : " + cnt);

        if (cnt == 0) {
            System.out.println("데이터가 없습니다.");
            return;
        }

        if (cnt >= DrwNumberSearchResult.MAX_ROWS) {
            System.out.println("최대로 불러올 수 있는 데이터 수를 초과하였습니다. 조회 조건 추가 또는 기간을 줄인 후 재 실행 부탁드립니다.");
            System.out.println("(한번에 조회 가능한 최대 데이터 수 : 10,000건)");
            return;
        }

        result.printToConsole();

        try {
            result.writeCsv(outputFile);
            System.out.println("CSV 저장 완료 : " + new java.io.File(outputFile).getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
