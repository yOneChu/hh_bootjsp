package com.kyhslam.dto;

import lombok.Data;

@Data
public class ElvInfoDTO {

    private String hogi;
    private String speed;
    private String nation;
    private String PRODUCTNO;
    private String EL_AOPEN;
    private String EL_ECWBUFBH; // --CWT BUFFER BLOCKING 높이
    private String EL_ECCH;//CAR 높이; CH
    private String EL_ECBG;//CAR:BG
    private String EL_ECEE;//CAR 무게중심;EE
    private String EL_ECJJ;
    private String EL_ERPW;


    private String EL_ECWRL;
    private String EL_ETM;
    private String EL_ECWBG;
    private String EL_ECWW;
    private String EL_ECSF;
    private String EL_ASPC;
    private String EL_ASPCD;//시방서여부
    private String EL_BCL; // 천장종류

    private String EL_AMAN; //인승
    private String EL_ASPSCD;
    private String EL_ABRAND;
    private String EL_ATYP;
    private String EL_ASPD;
    private String EL_ACAPA;
    private String EL_AUSE;

}
