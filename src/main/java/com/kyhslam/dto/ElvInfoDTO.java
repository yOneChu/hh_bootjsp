package com.kyhslam.dto;

import lombok.Data;

@Data
public class ElvInfoDTO {

    private String productoid;
    private String status;
    private String hogi;
    private String speed;
    private String nation;
    private String PRODUCTNO;
    private String EL_AOPEN;
    private String EL_ECWBUFBH; // --CWT BUFFER BLOCKING 높이
    private String EL_ECCH; //CAR 높이; CH
    private String EL_ECBG; //CAR:BG
    private String EL_ECEE; //CAR 무게중심;EE
    private String EL_ECJJ; //도어폭;JJ
    private String EL_ECAA; //CAR 외부가로 ; AA
    private String EL_ECBB; //CAR 외부세로 ; BB
    private String EL_ECCA; //CAR 내부가로 ; CA
    private String EL_ECCB; //CAR 내부세로 ; CB
    private String EL_EHV; //승강로 세로;YY
    private String EL_ECWBG; //CWT; BG
    private String EL_ECWW;  //CWT;폭

    private String EL_ECDOP; //◎ CAR DOOR OPER
    private String EL_ERPW; //ROPE/BELT; 본수

    private String EL_DCRG; //RGS적용
    private String EL_BCDM; //도어재질
    private String EL_BETM; //TRANSOM 재질/무늬
    private String EL_BWALLT; //WALL구조
    private String EL_BCLCDL; //LCD;취부위치
    private String EL_BMOPB; //MAIN OPB 사양
    private String EL_BOPBSWD; //OPB;SWING&WIDE

    private String EL_ECWRL; //CWT RAIL(K)
    private String EL_ETM; //권상기
    private String EL_ETMD; //tm방향

    private String EL_AFQ; //층수
    private String EL_EHTRH;; //주행거리


    private String EL_ECSF; //CAR; SAFETY
    private String EL_ASPC; //시방서
    private String EL_ASPCD;//시방서여부
    private String EL_BCL; // 천장종류

    private String EL_AMAN; //인승
    private String EL_ASPSCD; //생산거점(설계)
    private String EL_ASPSC; //생산거점
    private String EL_ABRAND;
    private String EL_ATYP; //기종
    private String EL_ASPD; //속도
    private String EL_ACAPA; //용량
    private String EL_AUSE; //용도


    private String EL_ZTEXT_B; // --가내 특기사항
    private String EL_ZTEXT_C; // --승장 특기사항
    private String EL_ZTEXT_D; // --옵션 특기사항
    private String EL_ZTEXT_E; // --L/O 특기사항
    private String EL_ZERR_M3_1; // --기계 에러 메시지
    private String EL_ZERR_E3_1; // --전기 에러 메시지
    private String EL_ZERR_M5_1; // --기계 미품목,
    private String EL_ZERR_E5_1; // --전기 미품목
    private String EL_ZERR_C_1; // --공통 에러 메시지
    private String EL_ZERR_A_1; // --자동 입력 오류
    private String MD$USER; // -- 등록자
    private String MD$CDATE; // --등록일
    private String MANAGER_E; // --전기담당자
    private String MANAGER_M; // --기계담당자

}
