package com.kyhslam.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PartInfoDTO {

    private String partNo;
    private String partName;
    private String version;
    private String mod;
    private String creDate;
    private String modDate;

    private String desc;
    private String eName;
    private String cName;

    private String glCode;
    private String spec;

    private String qty;
    private String uom; // 단위
    private String partSize;


    private String blockNo;
    private String blockName;
    private String status;

    private String div; // 최초구분
    private String disAway; //폐기여부

    private String uCheck; //수정여부
    private String cmt;
    private String nation;


}
