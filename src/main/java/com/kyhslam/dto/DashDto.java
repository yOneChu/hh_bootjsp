package com.kyhslam.dto;

import lombok.Data;

//수량
@Data
public class DashDto {

    private String part_name;
    private String batch_date;
    private String total_cnt;

    private String price202405;
    private String price202406;
    private String price202407;
    private String price202408;
    private String price202409;
    private String price202410;
    private String price202411;
    private String price202412;
    private String price202501;
    private String price202502;
    private String price202503;
    private String price202504;
    private String price202505;
    private String price202506;
    private String price202507;
    private String price202508;
    private String price202509;
    private String price202510;
    private String price202511;
    private String price202512;

    public DashDto() {
    }

    public DashDto(String part_name, String batch_date, String total_cnt, String price202405, String price202406, String price202407, String price202408, String price202409, String price202410, String price202411, String price202412, String price202501, String price202502, String price202503, String price202504, String price202505, String price202506, String price202507, String price202508, String price202509, String price202510, String price202511, String price202512) {
        this.part_name = part_name;
        this.batch_date = batch_date;
        this.total_cnt = total_cnt;
        this.price202405 = price202405;
        this.price202406 = price202406;
        this.price202407 = price202407;
        this.price202408 = price202408;
        this.price202409 = price202409;
        this.price202410 = price202410;
        this.price202411 = price202411;
        this.price202412 = price202412;
        this.price202501 = price202501;
        this.price202502 = price202502;
        this.price202503 = price202503;
        this.price202504 = price202504;
        this.price202505 = price202505;
    }

    public int getTotal() {
        int total = Integer.parseInt(this.price202405) + Integer.parseInt(this.price202406) + Integer.parseInt(this.price202407) + Integer.parseInt(this.price202408) + Integer.parseInt(this.price202409) + Integer.parseInt(this.price202410)
                + Integer.parseInt(this.price202411) + Integer.parseInt(this.price202412)
                + Integer.parseInt(this.price202501) + Integer.parseInt(this.price202502) + Integer.parseInt(this.price202503) + Integer.parseInt(this.price202504)
                + Integer.parseInt(this.price202505);

                //+ Integer.parseInt(this.price202506) + Integer.parseInt(this.price202507) + Integer.parseInt(this.price202508) + Integer.parseInt(this.price202509) + Integer.parseInt(this.price202510)
                //+ Integer.parseInt(this.price202511) + Integer.parseInt(this.price202512);
        return total;
    }
}
