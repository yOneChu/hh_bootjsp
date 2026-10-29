package com.kyhslam.util.pidSimulatorUp;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 기준일 버전 PID 저장소 (블럭 수량 계산의 날짜 지정 실행용)
 *   '최신 버전' 대신 기준일 이전(기준일 당일 포함, 당일이면 그 날의 마지막 시각) 중 가장 늦게 등록된 버전을 돌려준다.
 *   SimVariant 는 대상 / 전처리 / CALL 하위 PID 모두 getLastPid 로 버전을 고르므로 모든 PID 에 같은 기준일이 적용된다.
 *
 * 기존 SimPidRepository 는 그대로 두고 이 클래스로만 바꿔 끼운다. TEST 버전(-1)은 고르지 않는다.
 */
public class SimPidRepositoryAsOf extends SimPidRepository {

	private static final Object NOT_FOUND = new Object();

	/** 기준일 yyyy-MM-dd */
	private final String asOfDate;
	private final Map<String, Object> asOfCache = new ConcurrentHashMap<String, Object>();
	/** 실제로 고른 버전 (PID → {version, regDate}), 고른 순서 */
	private final Map<String, Map<String, Object>> picked = Collections.synchronizedMap(new LinkedHashMap<String, Map<String, Object>>());

	public SimPidRepositoryAsOf(String asOfDate) {
		if (asOfDate == null || !asOfDate.trim().matches("\\d{4}-\\d{2}-\\d{2}"))
			throw new SimExceptions.HdelBusinessException("기준일 형식이 잘못되었습니다 (yyyy-MM-dd) : " + asOfDate);
		this.asOfDate = asOfDate.trim();
	}

	public String getAsOfDate() { return asOfDate; }

	/** 실행 중 고른 PID 별 버전 {version, regDate} (기준일 이전 버전이 없으면 version null) */
	public Map<String, Map<String, Object>> getPicked() {
		synchronized (picked) {
			return new LinkedHashMap<String, Map<String, Object>>(picked);
		}
	}

	/** 기준일 이전(당일 포함) 중 가장 늦게 등록된 버전 (없으면 null) */
	@Override
	public PidInfo getLastPid(SimDb db, String pid) throws SQLException {
		Object cached = asOfCache.get(pid);
		if (cached == null) {
			Map<String, String> row = db.queryForFirst(
					" SELECT * FROM ( "
					+ "   SELECT A.VERSION, TO_CHAR(A.REG_DATE, 'YYYY-MM-DD HH24:MI:SS') REG_DATE "
					+ "   FROM VARIANT_H A "
					+ "   WHERE A.PID = ? AND A.VERSION <> ? AND A.REG_DATE < TO_DATE(?, 'YYYY-MM-DD') + 1 "
					+ "   ORDER BY A.REG_DATE DESC, A.VERSION DESC "
					+ " ) WHERE ROWNUM = 1 ", pid, SimConsts.TEST_VERSION, asOfDate);

			PidInfo info = row == null ? null : getPid(db, pid, SimUtil.parseInt(row.get("VERSION")));
			Map<String, Object> p = new LinkedHashMap<String, Object>();
			p.put("version", info == null ? null : info.getVersion());
			p.put("regDate", row == null ? null : row.get("REG_DATE"));
			picked.put(pid, p);

			cached = (info == null) ? NOT_FOUND : info;
			asOfCache.put(pid, cached);
		}
		return cached == NOT_FOUND ? null : (PidInfo) cached;
	}
}
