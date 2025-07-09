package com.kyhslam.dto;

import lombok.Data;

@Data
public class PartDashboardDTO {

    //엘리베이터자재만 집계
    //블럭번호, 5,6 제외

    private String partALL; // 총 자재
    private String partActive;
    private String partInactive;

    private String batchDate;

}
