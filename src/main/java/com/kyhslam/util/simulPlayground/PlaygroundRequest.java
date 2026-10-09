package com.kyhslam.util.simulPlayground;

import java.util.List;

/**
 * PID 플레이그라운드 실행 요청 (POST /simulplay/simulate 의 본문)
 */
public class PlaygroundRequest {

	/** 공사번호 ex) 206663L01, 또는 공사정보 ouid ex) elv_info$vf@a810066d */
	public String project;
	/** 대상 PID (수정본은 이 PID 에만 적용. CALL 하위 PID 는 DB 최신 버전) */
	public String pid;
	/** 대상 PID 버전 (null : 최신, -1 : TEST). 수정본이 있으면 PID 정보(방식/층별 여부) 확인용 */
	public Integer version;
	/** 층별 PID 일 때 적용할 층 (FLOOR_NAME) */
	public String floor;
	/** 전처리 PID (콤마/공백 구분) */
	public String beforePid;
	/** 대상 PID 의 수정본 라인 (null 이면 DB 원본으로 실행) */
	public List<PlaygroundRow> rows;
}
