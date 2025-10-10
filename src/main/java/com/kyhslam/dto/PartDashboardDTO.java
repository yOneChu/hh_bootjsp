package com.kyhslam.dto;

import lombok.Data;

@Data
public class PartDashboardDTO {

    //엘리베이터자재만 집계
    //블럭번호, 1,2,3만 집계

    private String partALL; // 총 자재
    private String partActive;
    private String partInactive;
    private String ols;

    private String batchDate;

    public PartDashboardDTO() {
    }

    public PartDashboardDTO(String partALL, String partActive, String partInactive, String ols, String batchDate) {
        this.partALL = partALL;
        this.partActive = partActive;
        this.partInactive = partInactive;
        this.ols = ols;
        this.batchDate = batchDate;
    }
}
