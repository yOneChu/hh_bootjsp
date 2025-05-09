package com.kyhslam.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Setter
@Getter
@Table(name = "dash_exportprice")
public class DashPublicExportPrice {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dash_exportprice_id")
    private Long id;

    private String partType; //품목명
    private String batchDate; //batch date

    private int price202405;
    private int price202406;
    private int price202407;
    private int price202408;
    private int price202409;
    private int price202410;
    private int price202411;
    private int price202412;
    private int price202501;
    private int price202502;
    private int price202503;
    private int price202504;
    private int price202505;
    private int price202506;
    private int price202507;
    private int price202508;
    private int price202509;
    private int price202510;
    private int price202511;
    private int price202512;
    private int priceEtc;

}
