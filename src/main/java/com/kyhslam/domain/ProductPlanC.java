package com.kyhslam.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "plancproduct")
public class ProductPlanC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plancproduct_id")
    private Long id;

    private String exportDate;

    private String productOid;
    private String productNo;
    private String productName;
    private String productVersion;
    private String productCreDate;
    private String productModDate;
    private String productAppdate;
    private String productStatus; //상태
    private String aspd; // 속도 EL_ASPD
    private String aspscd; //샌상거점
    private String acapa; //용량
    private String brand; // 브랜드
    private String EL_ETHRU; //관통
    private String EL_COB; //전망용 타입

    private String ecbg; // 	CAR; BG
    private String ecwbg; // CWT; BG
    private String ecww; // cw 폭

    private String gisong;
    private String mmanager;
    private String emanager;


    private String seq;
    private String parentNo;
    private String partNo;
    private String partNoOID;
    private String partName;
    private String nation;
    private String version;
    private String glCode;
    private String spec;
    private String part_size;
    private String blockNo;
    private String blockName;

    private String blockopt; //품목구분(1,2,3,M,C)
    private String uom; //단위
    private String qty; //수량
    private String cmt; //주석
    private String div; //내작 or 외작
    private String username;
    private String userId;
    private String modDate; //수정일자
    private String workQty;




}
