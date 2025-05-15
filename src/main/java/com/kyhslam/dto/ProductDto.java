package com.kyhslam.dto;


import lombok.Data;

@Data
public class ProductDto {

    private String productOid;
    private String productNo;
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

    private String block_opt; //품목구분(1,2,3,M,C)
    private String uom; //단위
    private String qty; //수량
    private String cmt; //주석
    private String div; //내작 or 외작
    private String username;
    private String userId;
    private String modDate; //수정일자

    private String ucheck; //수정여부
    private String HASCHILD; //하위BOM 존재여부

    public ProductDto() {
    }

}
