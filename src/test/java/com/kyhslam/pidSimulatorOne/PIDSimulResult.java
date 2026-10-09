package com.kyhslam.pidSimulatorOne;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PID 시뮬레이터 실행 결과 (PIDSimulator_right.jsp 의 화면 데이터)
 */
public class PIDSimulResult {

	/** 행 실행 상태 : 미실행(GOTO 로 건너뜀/주석/STOP 이후), 조건 만족, 조건 불만족 */
	public enum State { UNREAD, TRUE, FALSE }

	/** SPEC/CON 한 칸 */
	public static class Condition {
		public String spec;
		public String con;
		/** T / F / "" (비교 안함) */
		public String compare;
		/** 최종 영업사양의 SPEC 값 (화면의 CON 셀 툴팁) */
		public String specValue;
	}

	/** KEY/VAL 한 칸 */
	public static class KeyVal {
		public String key;
		public String val;
	}

	/** PID 로직 한 행 */
	public static class Line {
		/** 화면 순번 (1부터) */
		public int no;
		public String addr;
		public State state;
		/** maxSpecIdx 개 (빈 칸 포함) */
		public List<Condition> conditions = new ArrayList<Condition>();
		/** maxResIdx 개 (빈 칸 포함) */
		public List<KeyVal> results = new ArrayList<KeyVal>();
		public String gotoAddr;
		/** 화면의 OUTPUT 열 (조건 만족 행의 KEY = 결과값). 여러 줄이면 \n 구분 */
		public String output;
		public String remarks;
	}

	// ---------------------------------------------------------------- 입력/대상 정보
	public String projectNo;
	public String iOuid;
	/** 화면 좌측 Project Info : md$number version \n md$desc */
	public String projectTitle;
	public String pid;
	public String pidName;
	/** 층별 PID 여부 (Y/N) */
	public String isFloorSpec;
	/** 실제 적용된 층 (층별 PID 가 아니면 null) */
	public String floor;
	public boolean testVersion;
	public String beforePids;

	// ---------------------------------------------------------------- 결과
	/** JAVA 방식 PID 이면 true : lines 없이 resultMap 만 있다 (화면의 "JAVA 실행결과") */
	public boolean javaMethod;
	public int maxSpecIdx;
	public int maxResIdx;
	public List<Line> lines = new ArrayList<Line>();
	/** 최종 결과값 (debugData / elvEnt 제외) */
	public Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
	/** PID 실행 후 최종 영업사양 */
	public Map elvEnt;
	/** 실행 중 경고 (층 정보 로드 실패 등) */
	public List<String> warnings = new ArrayList<String>();

	/** 콘솔 출력 */
	public void print(PrintStream out, boolean showUnread) {
		out.println("PROJECT : " + projectNo + " (" + iOuid + ")");
		if (projectTitle != null)
			out.println("          " + projectTitle.replace("\n", " / "));
		out.println("PID     : " + pid + (pidName != null ? " " + pidName : "")
				+ (testVersion ? " [TEST]" : " [LASTEST]") + (floor != null ? " floor=" + floor : ""));
		if (beforePids != null && !beforePids.trim().isEmpty())
			out.println("BEFORE  : " + beforePids.trim().replace("\n", ", "));
		for (String w : warnings)
			out.println("[WARN] " + w);
		out.println("--------------------------------------------------");

		if (javaMethod) {
			out.println("JAVA 실행결과");
			for (Map.Entry<String, Object> e : resultMap.entrySet())
				out.println(e.getKey() + " : " + e.getValue());
			return;
		}

		for (Line line : lines) {
			if (!showUnread && line.state == State.UNREAD)
				continue;

			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%4d [%-6s] ADDR=%-6s GOTO=%-6s | ", line.no, line.state,
					SimUtil.NVL(line.addr, ""), SimUtil.NVL(line.gotoAddr, "")));
			for (Condition c : line.conditions) {
				if (c.spec == null)
					continue;
				sb.append(c.spec).append("=").append(SimUtil.NVL(c.con, "")).append("(").append(c.compare).append(") ");
			}
			sb.append("| ");
			for (KeyVal kv : line.results) {
				if (kv.key == null)
					continue;
				sb.append(kv.key).append("=").append(SimUtil.NVL(kv.val, "")).append(" ");
			}
			out.println(sb);
			if (line.output != null && !line.output.isEmpty())
				out.println("       => " + line.output.replace("\n", "\n          "));
		}

		out.println("--------------------------------------------------");
		out.println("RESULT : ");
		for (Map.Entry<String, Object> e : resultMap.entrySet()) {
			if (e.getKey().startsWith("OUTPUT"))
				continue;
			out.println("  " + e.getKey() + " = " + e.getValue());
		}
	}
}
