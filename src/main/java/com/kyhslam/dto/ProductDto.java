package com.kyhslam.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProductDto {

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

    //품목구분별 카운트
    private String mCount;
    private String m_ModCount;
    private String cCount;
    private String c_ModCount;
    private String oneCount;
    private String one_ModCount;
    private String twoCount;
    private String two_ModCount;
    private String threeCount;
    private String three_ModCount;

    private String ucheck; //수정여부
    private String HASCHILD; //하위BOM 존재여부

    public ProductDto() {
    }

}
