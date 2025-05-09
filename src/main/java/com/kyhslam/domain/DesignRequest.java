package com.kyhslam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//신전산요청
@Entity
@Setter
@Getter
@Table(name = "design_request")
public class DesignRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "design_id")
    private Long id;

    //private LocalDateTime cDate; //입력날짜

    private String batchDate;
    private String designType; //기계-전기
    private String designTypeName;

    private String designWorkType;
    private String designWorkTypeName;

    //등록
    private int cre202501;
    private int cre202502;
    private int cre202503;
    private int cre202504;
    private int cre202505;
    private int cre202506;
    private int cre202507;
    private int cre202508;
    private int cre202509;
    private int cre202510;
    private int cre202511;
    private int cre202512;


    //완료
    private int com202501;
    private int com202502;
    private int com202503;
    private int com202504;
    private int com202505;
    private int com202506;
    private int com202507;
    private int com202508;
    private int com202509;
    private int com202510;
    private int com202511;
    private int com202512;
}
