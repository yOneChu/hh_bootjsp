package com.kyhslam.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JqprDTO {

    private String jqprNum; // 관리번호

    private String jqprNo;
    private String hogi;

    private String status; // 상태 > 9:종결완료, 2:접수완료, 1:반려, Z:반려, 3:대책완료

    private String status02; //변상합의상태
    private String bomStat; // BOM상태 > C:BOM완료, A:품질BOM, B-1:기계설계BOM, D:BOM불필요

    private String credt; // 작성일
    private String crenm; // 작성자명
    private String post1; // 프로젝트명
    private String spec; // 사양

    private String matCost; // 자재비
    private String iwbtrCost; // 노무비
    private String failCost; // 실패비용






    /*private String receptDate; //접수일
    private String eUser; //전기설계
    private String mUser; //기계설계

    private String projectName;
    private String problemPart; //문제자재명

    private String creator; //작성자
    private String creDate; //작성일
    private String jqprType; //JQPR 유형

    private String problemStatus; //고장현상
    private String problemCause; //고장원인
    private String typeCode; //분류코드
    private String itemType; //ITEM분류명



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
    private String completeStatus; //조치상태*/
}
