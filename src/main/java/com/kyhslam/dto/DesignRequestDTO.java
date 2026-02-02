package com.kyhslam.dto;

import lombok.*;


@Data
public class DesignRequestDTO {

    private String reqNo;
    private String status;
    private String wosun; //우선순위
    private String gubun; //구분-전기,기계
    private String workGubun; //작업구분


    private String hogi;
    private String cUser; //작성자
    private String cUserName;
    private String manager; //처리자
    private String answerDetail; //작업내용

    private String first;
    private String designPart;
    private String reqType;

    private String reqCause; //요청사유
    private String reqDetail; //요청내용

    private String subae01; //수배자료적합성(유관부품 포함)
    private String subae02; //수배자료적합성(수배조건)
    private String isLimit; //제한조건작성여부
    private String layout;
    private String dcbFinish;
    private String isIsir; //ISIR(초도품 검사)
    private String isFinish; //인증완료여부
    private String ingStock; //재고처리여부
    private String isDutyTable;

    private String isSeries; //시리즈현장적용
    private String designSite; //기 수주/설계 현장 대응 여부
    private String teamShared;
    private String costInfluence; //원가영향도
    private String subSystem; //SubSystem공급구분

    private String creMon;
    private String modMon;

    private String creDate; //작성일
    private String modDate; //수정일

}
