package com.kyhslam.util.wonga;

import java.text.SimpleDateFormat;

/**
 * 원가절감 실적조회(ListOfBasedOnDrwNumber) 조회 조건.
 * ListOfBasedOnDrwNumber_left.jsp 입력 항목 / _mid.jsp 검증 / _right.jsp 전처리를 옮겨 놓은 클래스.
 */
public class DrwNumberSearchCondition {

    // 추출데이터 기본값 (ListOfBasedOnDrwNumber_left.jsp / _right.jsp)
    public static final String DEFAULT_SPEC_LIST =
            "EL_ATYP\n" + "EL_AMAN\n" + "EL_ACAPA\n" + "EL_AUSE\n" + "EL_ASPD\n" +
            "EL_AFQ\n" + "EL_EHTH\n" + "EL_EHO\n" + "EL_BFTH\n" + "EL_BKPL\n" +
            "EL_DVS\n" + "EL_ECRL\n" + "EL_ETMINV\n" + "EL_DLATT\n" + "EL_BMOPB\n" +
            "EL_EMRLHSCP\n" + "EL_ETM\n" + "EL_ECSF\n" + "EL_EHTRH\n" + "EL_AGRS\n" +
            "EL_DSV1\n" + "EL_DRV\n" + "EL_ECW\n" + "EL_ECJJ\n" + "EL_ERPW\n";

    public String gLCode = "";          // G_L_CODE (도면번호)
    public boolean chkDetail = false;   // 호기별 세부 사양 조회
    public String elAtyp = "";          // 기종 (콤마로 여러 건)
    public String elEcsf = "";          // CAR SAFETY
    public String elEtm = "";           // TM
    public String elAspd1 = "";         // 속도 ~
    public String elAspd2 = "";
    public String elAman1 = "";         // 인승 ~
    public String elAman2 = "";
    public String elEcjj1 = "";         // CAR출입구폭;JJ ~
    public String elEcjj2 = "";
    public String partNo = "";          // 자재번호 (콤마로 여러 건, * 또는 % 와일드카드)
    public String blockNo = "";         // BLOCK NO. (콤마로 여러 건)
    public String specList = DEFAULT_SPEC_LIST; // 추출데이터 (줄바꿈 구분)
    public String startDate = "";       // yyyy-MM-dd 또는 yyyyMMdd
    public String endDate = "";
    public boolean chkGeneral = false;  // 일반현장만 조회
    public boolean chkBom = false;      // 현장 BOM 추출

    /**
     * _left.jsp(searchList) / _mid.jsp 의 입력 검증.
     */
    public void validate() throws Exception {

        String start = nvl(startDate).replaceAll("-", "");
        String end = nvl(endDate).replaceAll("-", "");

        if (start.length() < 8 || end.length() < 8)
            throw new IllegalArgumentException("날짜 조건을 형식에 맞게 입력해주세요 (ex: yyyy-mm-dd)");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);
        long diffDays = (sdf.parse(end).getTime() - sdf.parse(start).getTime()) / (1000L * 60 * 60 * 24);

        if (diffDays < 0)
            throw new IllegalArgumentException("시작/종료 연월을 확인해주세요.");
        if (diffDays > 366)
            throw new IllegalArgumentException("기간은 1년을 초과 할 수 없습니다.");

        if (chkBom && isEmpty(partNo))
            throw new IllegalArgumentException("현장 BOM 추출 옵션 선택 시 자재번호는 필수 입력 조건 입니다.");

        if (!isNumberOrEmpty(elAspd1) || !isNumberOrEmpty(elAspd2) || !isNumberOrEmpty(elAman1)
                || !isNumberOrEmpty(elAman2) || !isNumberOrEmpty(elEcjj1) || !isNumberOrEmpty(elEcjj2))
            throw new IllegalArgumentException("속도/인승/CAR출입폭은 숫자만 입력하여 주십시요.");

        if (isEmpty(gLCode) && isEmpty(partNo) && isEmpty(blockNo))
            throw new IllegalArgumentException("도면번호, 자재번호, 블럭번호 중 1가지 이상을 입력해야 합니다.");
    }

    /**
     * _right.jsp 의 파라미터 전처리 (공백 제거, * -> %, 날짜 '-' 제거).
     */
    public DrwNumberSearchCondition normalize() {

        DrwNumberSearchCondition c = new DrwNumberSearchCondition();
        c.gLCode = strip(gLCode);
        c.chkDetail = chkDetail;
        c.elAtyp = strip(elAtyp);
        c.elEcsf = strip(elEcsf);
        c.elEtm = strip(elEtm);
        c.elAspd1 = strip(elAspd1);
        c.elAspd2 = strip(elAspd2);
        c.elAman1 = strip(elAman1);
        c.elAman2 = strip(elAman2);
        c.elEcjj1 = strip(elEcjj1);
        c.elEcjj2 = strip(elEcjj2);
        c.partNo = strip(nvl(partNo).replace("*", "%"));
        c.blockNo = strip(blockNo);
        c.specList = isEmpty(specList) ? DEFAULT_SPEC_LIST : specList;
        c.startDate = nvl(startDate).replace("-", "");
        c.endDate = nvl(endDate).replace("-", "");
        c.chkGeneral = chkGeneral;
        c.chkBom = chkBom;
        return c;
    }

    /** 사양 조건이 하나라도 있으면 elv_info 와 inner join (_right.jsp isOnlyElv) */
    public boolean isOnlyElv() {
        return !(isEmpty(elAtyp) && isEmpty(elAspd1) && isEmpty(elAspd2) && isEmpty(elAman1) && isEmpty(elAman2)
                && isEmpty(elEcsf) && isEmpty(elEtm) && isEmpty(elEcjj1) && isEmpty(elEcjj2));
    }

    @Override
    public String toString() {
        return gLCode + ", " + chkBom + ", " + chkDetail + ", " + elAtyp + ", " + elAspd1 + ", " + elAspd2 + ", "
                + elAman1 + ", " + elAman2 + ", " + elEcsf + ", " + elEtm + ", " + elEcjj1 + ", " + elEcjj2 + ", "
                + partNo + ", " + blockNo + ", " + startDate + ", " + endDate;
    }

    static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    private static String strip(String s) {
        return nvl(s).trim().replace(" ", "");
    }

    private static boolean isNumberOrEmpty(String s) {
        if (isEmpty(s))
            return true;
        try {
            Double.parseDouble(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
