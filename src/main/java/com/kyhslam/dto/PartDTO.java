package com.kyhslam.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PartDTO {

    private String oid;
    private String partNo;
    private String partName;
    private String version;
    private String nation;

    private String desc;
    private String glCode;
    private String spec;

    private String qty;
    private String uom; // 단위
    private String partSize;

    private String design;
    private String cost;
    private String originDiv;

    private String blockNo;
    private String blockName;
    private String status;
    private String active;
    private String creDate;
    private String modDate;

    private String div; // 최초구분
    private String disAway; //폐기여부



}
