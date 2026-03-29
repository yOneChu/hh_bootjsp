package com.kyhslam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "subaehogibom")
@Setter
@Getter
public class SubaeHogiBOM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bom_id")
    private Long id;

    private String hogi;
    private String hogiVersion;
    private String partNo;
    private String partName;
    private String blockNo;
    private String blockOpt;
    private String ucheck;
    private String codate;
    private String spec;
    private String qty;

    @Lob
    private String cmt;


}
