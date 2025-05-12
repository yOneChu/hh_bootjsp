package com.kyhslam.dto;

import lombok.Data;

@Data
public class BlockHistoryDTO {

    private String blockNo;
    private String blockName;
    private String version;

    private String modDate;
    private String modUser;

    private String gc_product; //제품군
    private String uom; //단위

    private String partType; //자재유형(외주)
    private String block_opt; //품목구분
    private String drawingOnly; //자재번호 사용 불가
    private String block_status; //활성상태
    private String meterial_check; //재질관리


    private String pick;
    private String pickName;
    private String qty;
    private String cmt;
    private String color;


}
