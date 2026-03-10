package com.kyhslam.dto;

import lombok.Data;

@Data
public class PartWhere {

    String year;
    String brand;
    String partNo;
    String blockNo;
    String cmt;
    String spec;

    String creDate;
    String endDate;
    String status;
    
    String EL_ASPSCD; //생산거점
    String EL_ATYP; //기종
    String EL_ETHRU; //관통
    String EL_COB; //전망타입
    String EL_ZFDA;
    String EL_ZFDA_TYPE;
    String EL_BWALLT; //WALL 구조

}
