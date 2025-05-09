package com.kyhslam.dto;

import lombok.*;


public class DesignRequestDTO {

    private String reqNo;
    private String status;
    private String wosun; //우선순위
    private String gubun; //구분-전기,기계
    private String workGubun; //작업구분


    private String cUser; //요청자
    private String cUserName;
    private String manager; //처리자

    private String reqCause; //요청사유
    private String reqDetail; //요청내용
    private String costInfluence; //원가영향도
    private String creMon;
    private String modMon;

    private String creDate; //작성일
    private String modDate; //수정일

    public String getcUser() {
        return cUser;
    }

    public void setcUser(String cUser) {
        this.cUser = cUser;
    }

    public String getcUserName() {
        return cUserName;
    }

    public void setcUserName(String cUserName) {
        this.cUserName = cUserName;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getReqNo() {
        return reqNo;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWosun() {
        return wosun;
    }

    public void setWosun(String wosun) {
        this.wosun = wosun;
    }

    public String getGubun() {
        return gubun;
    }

    public void setGubun(String gubun) {
        this.gubun = gubun;
    }

    public String getWorkGubun() {
        return workGubun;
    }

    public void setWorkGubun(String workGubun) {
        this.workGubun = workGubun;
    }

    public String getReqCause() {
        return reqCause;
    }

    public void setReqCause(String reqCause) {
        this.reqCause = reqCause;
    }

    public String getReqDetail() {
        return reqDetail;
    }

    public void setReqDetail(String reqDetail) {
        this.reqDetail = reqDetail;
    }

    public String getCostInfluence() {
        return costInfluence;
    }

    public void setCostInfluence(String costInfluence) {
        this.costInfluence = costInfluence;
    }

    public String getCreMon() {
        return creMon;
    }

    public void setCreMon(String creMon) {
        this.creMon = creMon;
    }

    public String getModMon() {
        return modMon;
    }

    public void setModMon(String modMon) {
        this.modMon = modMon;
    }

    public String getCreDate() {
        return creDate;
    }

    public void setCreDate(String creDate) {
        this.creDate = creDate;
    }

    public String getModDate() {
        return modDate;
    }

    public void setModDate(String modDate) {
        this.modDate = modDate;
    }
}
