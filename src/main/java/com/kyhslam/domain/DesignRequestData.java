package com.kyhslam.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "design_request_data")
public class DesignRequestData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dash_req_id")
    private Long id;

    private String batchDate;

    private String reqNo;
    private String hogi;
    private String status;
    private String wosun; //우선순위

    private String gubun; //구분-전기,기계
    private String gubunoid; //구분-전기,기계OID

    private String workGubun; //작업구분
    private String workGubunoid; //작업구분OID


    private String cUser; //요청자
    private String cUserName;
    private String manager; //처리자

    @Lob
    private String reqCause; //요청사유

    @Lob
    private String reqDetail; //요청내용

    private String costInfluence; //원가영향도
    private String creMon;
    private String modMon;

    private String creDate; //작성일
    private String modDate; //수정일

}