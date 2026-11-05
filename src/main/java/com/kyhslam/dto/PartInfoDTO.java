package com.kyhslam.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PartInfoDTO {

    private String level;

    // [수량 PID 상세 검색] 이 행이 나온 시작 부품(BlockNo 로 찾은 부품)의 품번. 다른 조회의 JSON 응답에는 나오지 않도록 값이 있을 때만 내려준다
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String topPartNo;

    private String parentLevel;
    private String parentOid;
    private String parentPartNo;
    private String parentPartName;
    private String parentBlockNo;
    private String parentGLCode;
    private String parentSpec;
    private String parentSize;
    private String parentQty;
    private String parentCmt;
    private String parentVersion;


    private String oid;
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

    private String design;
    private String cost;
    private String originDiv;

    private String blockNo;
    private String blockName;
    private String status;
    private String active;

    private String div; // 최초구분
    private String disAway; //폐기여부

    private String uCheck; //수정여부
    private String cmt;
    private String nation;
    private String HASCHILD; //하위BOM 존재여부

}
