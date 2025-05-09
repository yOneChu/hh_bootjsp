package com.kyhslam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//@Entity
@Table(name = "elvinfo_dash")
@Setter
@Getter
public class ELVInfoDash {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "elvinfo_dash_id")
    private Long id;

    private String elvType; //기종

    private String batchDate;

    private int totalCnt;
    private int dis202401;
    private int dis202402;
    private int dis202403;
    private int dis202404;
    private int dis202405;
    private int dis202406;
    private int dis202407;
    private int dis202408;
    private int dis202409;
    private int dis202410;
    private int dis202411;
    private int dis202412;
}
