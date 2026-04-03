package com.kyhslam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subaehogi")
@Setter
@Getter
public class SubaeHogi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subaehogi_id")
    private Long id;

    private String hogi; //호기번호
    private String codat; //설계완료일
    private String batchDate;
}
