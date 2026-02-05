package com.kyhslam.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "plancdash")
public class PlanCDash {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plancdash_id")
    private Long id;


    private String planIndex;
    private String brand;
    private String titleName;
    private String batchDate;

    private String hogi;
    private String partNo;
    private String partName;
    private String blockNo;

    private int totalCnt;
    private int dis202601;
    private int dis202602;
    private int dis202603;
    private int dis202604;
    private int dis202605;
    private int dis202606;
    private int dis202607;
    private int dis202608;
    private int dis202609;
    private int dis202610;
    private int dis202611;
    private int dis202612;

    private int toCost;
}
