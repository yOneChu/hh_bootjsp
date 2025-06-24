package com.kyhslam.dto;

import lombok.Data;

@Data
public class ElvInfoDTO {

    private String hogi;
    private String speed;
    private String nation;

    public ElvInfoDTO() {
    }

    public ElvInfoDTO(String hogi, String speed, String nation) {
        this.hogi = hogi;
        this.speed = speed;
        this.nation = nation;
    }
}
