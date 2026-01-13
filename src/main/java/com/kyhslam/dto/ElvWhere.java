package com.kyhslam.dto;

import lombok.Data;

@Data
public class ElvWhere {

    //영업사양 검색조건
    String year;
    String partNo;
    String blockNo;
    String cmt;
    String spec;

    String creDate;
    String endDate;
    String status;

    String EL_ABRAND; //브랜드
    String EL_ACAPA; //용량
    String EL_AOPEN; //열림
    String EL_ASPD; //속도
    String EL_ECWRL; //CWT RAIL(K)
    String EL_AUSE; //용도
    String EL_ERPW; //ROPE/BELT본수
    String EL_ASPSCD; //생산거점
    String EL_ATYP; //기종
    String EL_ETM; // 권상기 타입
    String EL_COB;

}
