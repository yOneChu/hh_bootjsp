package com.kyhslam.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class JqprDTO {

    private String status; // JQPR 상태
    private String status02; //변상합의상태
    private String bomStatus; //bom상태


    private String eUser; //전기설계
    private String mUser; //기계설계
    private String jqprNo;

    private String global;
    private String manageNo; // 관리번호

    private String projectName; // 프로젝트명
    private String problemPart; //문제자재명
    private String hogi; // 호기
    private String creator; //작성자
    private String creDate; //작성일
    private String jqprType; //JQPR 유형

    private String receptDate; //접수일

    private String finishDate; // 종결완료일
    private String problemStatus; // 고장현상
    private String problemCause; // 고장원인
    private String typeCode; //분류코드
    private String itemType; //ITEM분류명
    private String jajeCost; //자재비
    private String nomoCost; //노무비
    private String failCost; //실패비용

    private String team01; //내부부서1
    private String team01Cost; //내부비용1
    private String team02;
    private String team02Cost;
    private String team03;
    private String team03Cost;


    private String fCompany; //외부업체명
    private String fCompanyCost; //업체변상금액

    private String etcTeam; //기타부서명
    private String etcTeamCost; //기타부서비용
    private String completeStatus; //조치상태

    private String problemName; // 문제점 제목
    private String problemDetail; // 문제점상세
    private String requestDetail; // 요청사항

}
