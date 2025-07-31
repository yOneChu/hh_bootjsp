package com.kyhslam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//@Entity
@Setter
@Getter
@Table(name = "jqprData")
public class JQPR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jqpr_id")
    private Long id;



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

    private String mandt;
    private String aType; // 기종

    private String matCost; // 자재비
    private String iwbtrCost; // 노무비

    private String itemNo;
    private String teamCode1; // IMPKTL
    private String teamCode1p; //내부비용 퍼센테이지
    private String teamCode2;
    private String teamCode2p;
    private String teamCode3;
    private String teamCode3p;



   /* B.IMPLFN,
    B.IMPLFN_P,
    B.REJTXT AS 문제점_제목, -- 문제점 제목
    B.REJLT AS 문제점_상세, -- 문제점 상세
    B.REQLT AS 요청사항, -- 요청사항
    B.CAUSEGRP,
    B.CAUSECOD,
    B.CAUSETXT AS 고장원인, -- 고장원인
    B.PHENOTXT AS 고장현상, -- 고장현상
    B.CORLT AS 조치확인, -- 조치확인
    B.CLODT AS 종결처리일, --종결처리일
    B.CLOID,
    B.WRKLFN AS 사업자등록번호, --사업자등록번호
    B.CATCODE -- 품목번호
       , B.ZPROFCHK AS 귀책증빙, -- 귀책증빙
    B.PROG_STAT AS 진행현황*/

/*
    private String receptDate; //접수일
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
    private String completeStatus; //조치상태*/
}
