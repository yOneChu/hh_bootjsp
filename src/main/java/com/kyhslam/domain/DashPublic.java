package com.kyhslam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;



@Entity
@Table(name = "dash_public")
@Setter
@Getter
public class DashPublic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dash_Public_id")
    private Long id;

    private String partName; //품목명
    private LocalDateTime cDate; //입력날짜

    private String batchDate;
    private int totalCnt;


    private int dis202405;
    private int dis202406;
    private int dis202407;
    private int dis202408;
    private int dis202409;
    private int dis202410;
    private int dis202411;
    private int dis202412;

    private int dis202501;
    private int dis202502;
    private int dis202503;
    private int dis202504;
    private int dis202505;
    private int dis202506;
    private int dis202507;
    private int dis202508;
    private int dis202509;
    private int dis202510;
    private int dis202511;
    private int dis202512;


    private int priceTotalCnt;
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

    //출하예정일
    private int exportTotalCnt;
    private int export202405;
    private int export202406;
    private int export202407;
    private int export202408;
    private int export202409;
    private int export202410;
    private int export202411;
    private int export202412;
    private int export202501;
    private int export202502;
    private int export202503;
    private int export202504;
    private int export202505;
    private int export202506;
    private int export202507;
    private int export202508;
    private int export202509;
    private int export202510;
    private int export202511;
    private int export202512;
    private int exportEtc;
}
