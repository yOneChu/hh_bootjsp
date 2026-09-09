package com.kyhslam.dto;

import lombok.Data;

@Data
public class EcoDTO {

    private String econo;
    private String status;
    private String ecoName;
    private String creUser;

    private String gubun;
    private String etcContent; //기타 변경사유
    private String content; //내용및사유

    private String verify; //검증유무
    private String excepFlag; //정합성유무

    private String creDate; //생성일
    private String appDate; //승인일

}
