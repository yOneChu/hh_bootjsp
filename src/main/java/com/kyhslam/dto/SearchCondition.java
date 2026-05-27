package com.kyhslam.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchCondition {
    private String key;
    private String op;
    private String value;

}
