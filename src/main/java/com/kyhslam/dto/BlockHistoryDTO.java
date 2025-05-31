package com.kyhslam.dto;

import lombok.Data;

@Data
public class BlockHistoryDTO {

    private String blockNo;
    private String blockName;
    private String version;

    private String modDate;
    private String modUser;
    private String creDate; //등록일

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

    public BlockHistoryDTO() {
    }

    public BlockHistoryDTO(String blockNo, String blockName, String version, String modDate, String modUser, String creDate, String gc_product, String uom, String partType, String block_opt, String drawingOnly, String block_status, String meterial_check, String pick, String pickName, String qty, String cmt, String color) {
        this.blockNo = blockNo;
        this.blockName = blockName;
        this.version = version;
        this.modDate = modDate;
        this.modUser = modUser;
        this.creDate = creDate;
        this.gc_product = gc_product;
        this.uom = uom;
        this.partType = partType;
        this.block_opt = block_opt;
        this.drawingOnly = drawingOnly;
        this.block_status = block_status;
        this.meterial_check = meterial_check;
        this.pick = pick;
        this.pickName = pickName;
        this.qty = qty;
        this.cmt = cmt;
        this.color = color;
    }
}
