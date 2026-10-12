package com.kyhslam.util.simulate;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PID 호출 통계 (호기 1건 계산 동안. BlockContext 와 같이 한 스레드에서만 쓴다)
 * PID 별 호출 수(직접 호출 / CALL), 누적시간(하위 CALL 포함), 자체시간(하위 CALL 제외)을 모은다.
 */
public class PidCallStats {

	private static class Stat {
		final String pid;
		String method;
		int calls;
		int innerCalls;
		long totalNs;
		long selfNs;

		Stat(String pid) { this.pid = pid; }
	}

	private final Map<String, Stat> stats = new HashMap<String, Stat>();
	/** 실행 중인 PID 의 하위 CALL 시간 합계 (자체시간 계산용) */
	private final Deque<long[]> childNs = new ArrayDeque<long[]>();
	private int totalCalls;
	private int innerCalls;

	/** PID 실행 시작. 반환값을 end 에 넘긴다. */
	public long begin() {
		childNs.push(new long[1]);
		return System.nanoTime();
	}

	public void end(String pid, String method, int depth, long startNs) {
		long elapsed = System.nanoTime() - startNs;
		long child = childNs.pop()[0];
		if (!childNs.isEmpty())
			childNs.peek()[0] += elapsed;

		Stat s = stats.get(pid);
		if (s == null) {
			s = new Stat(pid);
			stats.put(pid, s);
		}
		s.method = method;
		s.calls++;
		s.totalNs += elapsed;
		s.selfNs += elapsed - child;
		totalCalls++;
		if (depth > 1) {
			s.innerCalls++;
			innerCalls++;
		}
	}

	/** 요약 + 자체시간 상위 topN 개 PID */
	public String summary(int topN) {
		long selfSum = 0;
		for (Stat s : stats.values())
			selfSum += s.selfNs;

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("PID 호출 %d회 (CALL %d회), PID %d종, 중복 호출 %d회 (%.1f%%), PID 실행 자체시간 합계 %dms",
				totalCalls, innerCalls, stats.size(), totalCalls - stats.size(),
				totalCalls == 0 ? 0.0 : (totalCalls - stats.size()) * 100.0 / totalCalls, selfSum / 1_000_000));

		List<Stat> list = new ArrayList<Stat>(stats.values());
		list.sort((a, b) -> Long.compare(b.selfNs, a.selfNs));
		sb.append(String.format("%n  %-4s %-24s %-6s %8s %8s %10s %10s %8s", "순위", "PID", "METHOD", "호출", "CALL", "자체(ms)", "누적(ms)", "평균(ms)"));
		for (int i = 0; i < Math.min(topN, list.size()); i++) {
			Stat s = list.get(i);
			sb.append(String.format("%n  %-4d %-24s %-6s %8d %8d %10.1f %10.1f %8.2f", i + 1, s.pid, s.method, s.calls, s.innerCalls,
					s.selfNs / 1e6, s.totalNs / 1e6, s.totalNs / 1e6 / s.calls));
		}
		return sb.toString();
	}
}
