package com.kyhslam.util.simulPlayground;

import com.kyhslam.util.pidSimulatorUp.PIDSimulResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 플레이그라운드 실행 결과 = PIDSimulResult + 라인별 디버그 정보.
 * debug 는 lines 와 같은 순서 (debug[i] 가 lines[i] 의 디버그 정보).
 */
public class PlaygroundResult extends PIDSimulResult {

	/** 조건 1칸을 비교한 그 시점의 값 */
	public static class CondDebug {
		/** SPEC 번호 (1부터) */
		public int idx;
		public String spec;
		public String con;
		/** 비교 시점의 SPEC 값 (앞 라인에서 바뀐 값 반영) */
		public String specValue;
		/** CON 을 계산한 값 ({변수} / [$ 계산식 $] 치환 후). CON 이 비어 있으면 "" (항상 일치) */
		public String conValue;
		/** T / F / "" */
		public String compare;
		/** 값 출처 안내 (계산식 / 코드명 / 부품정보 / 앞 라인에서 변경 / 오류 등) */
		public String note;
	}

	/** 이 라인 실행으로 바뀐 값 1건 */
	public static class Change {
		public String key;
		/** 바뀌기 전 값 (처음 생긴 값이면 null) */
		public String before;
		public String after;
	}

	/** 라인 1개의 디버그 정보 */
	public static class LineDebug {
		/** 실행 순서 (1부터, 미실행이면 0) */
		public int step;
		public List<CondDebug> conds = new ArrayList<CondDebug>();
		/** 조건을 만족해 바뀐 값 (resultMap 기준, 조건 불만족 / 미실행이면 비어 있음) */
		public List<Change> changes = new ArrayList<Change>();
	}

	/** lines 와 같은 순서의 디버그 정보 */
	public List<LineDebug> debug = new ArrayList<LineDebug>();
	/** 실행된 라인 수 (마지막 step) */
	public int steps;
}
