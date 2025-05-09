package com.kyhslam.dto;

import lombok.Data;

//대수
@Data
public class DashDtoV2 {

    private String part_name;
    private String batch_date;
    private String total_cnt;

    private String DIS202405;
    private String DIS202406;
    private String DIS202407;
    private String DIS202408;
    private String DIS202409;
    private String DIS202410;
    private String DIS202411;
    private String DIS202412;
    private String DIS202501;
    private String DIS202502;
    private String DIS202503;
    private String DIS202504;
    private String DIS202505;


    public DashDtoV2() {
    }

    public DashDtoV2(String part_name, String batch_date, String total_cnt, String price202405, String price202406, String price202407, String price202408, String price202409, String price202410, String price202411, String price202412, String price202501, String price202502, String price202503, String price202504, String price202505, String price202506, String price202507, String price202508, String price202509, String price202510, String price202511, String price202512) {
        this.part_name = part_name;
        this.batch_date = batch_date;
        this.total_cnt = total_cnt;
        this.DIS202405 = price202405;
        this.DIS202406 = price202406;
        this.DIS202407 = price202407;
        this.DIS202408 = price202408;
        this.DIS202409 = price202409;
        this.DIS202410 = price202410;
        this.DIS202411 = price202411;
        this.DIS202412 = price202412;
        this.DIS202501 = price202501;
        this.DIS202502 = price202502;
        this.DIS202503 = price202503;
        this.DIS202504 = price202504;
        this.DIS202505 = price202505;
    }

    public int getTotal() {
        int total = Integer.parseInt(this.DIS202405) + Integer.parseInt(this.DIS202406) + Integer.parseInt(this.DIS202407) + Integer.parseInt(this.DIS202408) + Integer.parseInt(this.DIS202409) + Integer.parseInt(this.DIS202410)
                + Integer.parseInt(this.DIS202411) + Integer.parseInt(this.DIS202412)
                + Integer.parseInt(this.DIS202501) + Integer.parseInt(this.DIS202502) + Integer.parseInt(this.DIS202503) + Integer.parseInt(this.DIS202504)
                + Integer.parseInt(this.DIS202505);

                //+ Integer.parseInt(this.price202506) + Integer.parseInt(this.price202507) + Integer.parseInt(this.price202508) + Integer.parseInt(this.price202509) + Integer.parseInt(this.price202510)
                //+ Integer.parseInt(this.price202511) + Integer.parseInt(this.price202512);
        return total;
    }
}
