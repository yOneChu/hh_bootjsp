package com.kyhslam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "dash_publicdata")
public class DashPublicData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dash_publicdata_id")
    private Long id;

    private String batchDate;
    private String erpSendDate;
    private String erpSendMonth;
    private String partType;

    private String hogi;
    private String partNo;
    private String qty;
    private String dwgNo;
    private String blockNo;
    private String gongSa;
    private String giSong;
    private String spec;

    private String exportDate; //해당 자재의 출하예정일
    private String partSize;
    private String speed; // 속도 : EL_ASPD
    private String weight; // 용량 : EL_ACAPA
    private String personCnt; // 인승 : EL_AMAN
    private String ause; // 용도 : EL_AUSE
    private String floorCnt; //층수 : EL_AFQ

    private String mUser;
    private String eUser;
}
