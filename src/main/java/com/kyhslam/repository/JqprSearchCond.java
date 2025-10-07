package com.kyhslam.repository;

import lombok.Data;

@Data
public class JqprSearchCond {

    private String month;
    private String year;
    private String jqprNo;
    private String state;

    public JqprSearchCond() {
    }

    public JqprSearchCond(String month, String year, String jqprNo, String state) {
        this.month = month;
        this.year = year;
        this.jqprNo = jqprNo;
        this.state = state;
    }
}
