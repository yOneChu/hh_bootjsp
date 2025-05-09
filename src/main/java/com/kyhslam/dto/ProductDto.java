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
    private String block_opt; //품목구분(1,2,3,M,C)
    private String uom; //단위
    private String qty; //수량
    private String cmt; //주석
    private String div; //내작 or 외작
    private String username;
    private String userId;
    private String modDate; //수정일자

    private String ucheck; //수정여부

    public ProductDto() {
    }

    public ProductDto(String productOid, String productNo, String seq, String parentNo, String partNo, String partNoOID, String partName, String nation, String version, String glCode, String spec, String part_size, String blockNo, String block_opt, String uom, String qty, String cmt, String div, String username, String userId, String ucheck) {
        this.productOid = productOid;
        this.productNo = productNo;
        this.seq = seq;
        this.parentNo = parentNo;
        this.partNo = partNo;
        this.partNoOID = partNoOID;
        this.partName = partName;
        this.nation = nation;
        this.version = version;
        this.glCode = glCode;
        this.spec = spec;
        this.part_size = part_size;
        this.blockNo = blockNo;
        this.block_opt = block_opt;
        this.uom = uom;
        this.qty = qty;
        this.cmt = cmt;
        this.div = div;
        this.username = username;
        this.userId = userId;
        this.ucheck = ucheck;
    }
}
