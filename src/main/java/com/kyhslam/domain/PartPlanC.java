package com.kyhslam.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "plancdata")
public class PartPlanC {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plancdata_id")
    private Long id;

    private String hogi;

    private String planIndex;

    private String brand;
    private String titleName;
    //private String blockNo;

    //as-is
    private String partNo_as;
    private String partName_as;
    private String qty_as;
    private String blockNo_as;
    private String giSong_as;
    private String spec_as;
    private String cost_as;

    //to-be
    private String partNo;
    private String partName;
    private String qty;
    private String blockNo;
    private String giSong;
    private String spec;
    private String cost;

    private String exportDate; //해당 자재의 출하예정일



}
