package com.kyhslam.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//@Entity
@Setter
@Getter
@Table(name = "elvInfo")
public class ELVInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "elvinfo_id")
    private Long id;

    private String hogi;
    private String version;
    private String sujuName;  //수주명
    private String creDate;
    private String modDate;
    private String modMonth;
    private String status; // 상태

    private String ARKTX; //사양
    private String BDUSE; //건물용도
    private String ABRAND; //브랜드
    private String ATYP; //기종
    private String ASPD; //속도
    private String AUSE; //용도
    private String ACAPA; //용량
    private String ADRV; //운행방식
    private String AEXP; //기종파생모델

    private String AFQ; // 층수
    private String AMAN; // 인승
    private String AOPEN; //열림(1SCO..)
    private String BCDM; // DOOR 재질
    private String BCL; // 천장종류
    private String BCS; // SILL재질

    private String BETC; //TRANSOM 색상/무늬
    private String BETM; //TRANSOM 재질/무늬
    private String BFLOORS; //FLOOR종류/공급주제



}
