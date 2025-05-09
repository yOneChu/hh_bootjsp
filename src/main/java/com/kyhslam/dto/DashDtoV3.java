package com.kyhslam.dto;

import lombok.Data;

//수량
@Data
public class DashDtoV3 {

    private String part_name;
    private String batch_date;
    private String total_cnt;

    private String export202405;
    private String export202406;
    private String export202407;
    private String export202408;
    private String export202409;
    private String export202410;
    private String export202411;
    private String export202412;
    private String export202501;
    private String export202502;
    private String export202503;
    private String export202504;
    private String export202505;
    private String export202506;
    private String export202507;
    private String export202508;
    private String export202509;
    private String export202510;
    private String export202511;
    private String export202512;
    private String export_etc;

    public DashDtoV3() {
    }

    public DashDtoV3(String part_name, String batch_date, String total_cnt, String export202405, String export202406, String export202407, String export202408, String export202409, String export202410, String export202411, String export202412, String export202501, String export202502, String export202503, String export202504, String export202505, String export202506, String export202507, String export202508, String export202509, String export202510, String export202511, String export202512, String export_etc) {
        this.part_name = part_name;
        this.batch_date = batch_date;
        this.total_cnt = total_cnt;
        this.export202405 = export202405;
        this.export202406 = export202406;
        this.export202407 = export202407;
        this.export202408 = export202408;
        this.export202409 = export202409;
        this.export202410 = export202410;
        this.export202411 = export202411;
        this.export202412 = export202412;
        this.export202501 = export202501;
        this.export202502 = export202502;
        this.export202503 = export202503;
        this.export202504 = export202504;
        this.export202505 = export202505;
        this.export202506 = export202506;
        this.export202507 = export202507;
        this.export202508 = export202508;
        this.export202509 = export202509;
        this.export202510 = export202510;
        this.export202511 = export202511;
        this.export202512 = export202512;
        this.export_etc = export_etc;
    }

    public int getTotal2024() {
        int total = Integer.parseInt(this.export202405) + Integer.parseInt(this.export202406) + Integer.parseInt(this.export202407) + Integer.parseInt(this.export202408) + Integer.parseInt(this.export202409) + Integer.parseInt(this.export202410)
                + Integer.parseInt(this.export202411) + Integer.parseInt(this.export202412);

        return total;
    }

    public int getTotal2025() {
        int total = Integer.parseInt(this.export202501) + Integer.parseInt(this.export202502) + Integer.parseInt(this.export202503) + Integer.parseInt(this.export202504)
                + Integer.parseInt(this.export202505)
                + Integer.parseInt(this.export202506) + Integer.parseInt(this.export202507) + Integer.parseInt(this.export202508) + Integer.parseInt(this.export202509) + Integer.parseInt(this.export202510)
                + Integer.parseInt(this.export202511) + Integer.parseInt(this.export202512);
        return total;
    }

    public int getTotal() {
        int total = Integer.parseInt(this.export202405) + Integer.parseInt(this.export202406) + Integer.parseInt(this.export202407) + Integer.parseInt(this.export202408) + Integer.parseInt(this.export202409) + Integer.parseInt(this.export202410)
                + Integer.parseInt(this.export202411) + Integer.parseInt(this.export202412)
                + Integer.parseInt(this.export202501) + Integer.parseInt(this.export202502) + Integer.parseInt(this.export202503) + Integer.parseInt(this.export202504)
                + Integer.parseInt(this.export202505)
                + Integer.parseInt(this.export202506) + Integer.parseInt(this.export202507) + Integer.parseInt(this.export202508) + Integer.parseInt(this.export202509) + Integer.parseInt(this.export202510)
                + Integer.parseInt(this.export202511) + Integer.parseInt(this.export202512) + Integer.parseInt(this.export_etc);
        return total;
    }
}
