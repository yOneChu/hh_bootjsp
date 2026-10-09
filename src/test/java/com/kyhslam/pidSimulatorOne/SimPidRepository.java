package com.kyhslam.pidSimulatorOne;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PID 헤더/로직 캐시 (PIDCache 대체). 여러 스레드에서 공유하며, DB 조회는 호출한 스레드의 SimDb 로 한다.
 */
public class SimPidRepository {

	private static final Object NOT_FOUND = new Object();

	private final Map<String, Object> lastPidCache = new ConcurrentHashMap<String, Object>();
	private final Map<String, SimVariantMap> logicCache = new ConcurrentHashMap<String, SimVariantMap>();
	private volatile Set<String> allPids = null;

	/** variant_h 1건 */
	public static class PidInfo {
		private String pid;
		private String name;
		private String method;
		private int version;
		private String isfloorspec;

		public String getPid() { return pid; }
		public String getName() { return name; }
		public String getMethod() { return method; }
		public int getVersion() { return version; }
		/** 층별 PID 여부 (Y/N) */
		public String getIsfloorspec() { return isfloorspec; }
	}

	/** 최신 버전 PID 정보 (없으면 null) */
	public PidInfo getLastPid(SimDb db, String pid) throws SQLException {
		Object cached = lastPidCache.get(pid);
		if (cached == null) {
			PidInfo info = toPidInfo(db.queryForFirst(
					" select a.pid, a.name, a.method, a.version, a.isfloorspec from variant_h a, variant_id b where a.houid = b.last_houid and a.pid = ? ", pid));
			cached = (info == null) ? NOT_FOUND : info;
			lastPidCache.put(pid, cached);
		}
		return cached == NOT_FOUND ? null : (PidInfo) cached;
	}

	/** VariantDao.findPid : 지정 버전 PID 정보 (TEST 버전은 -1, 없으면 null) */
	public PidInfo getPid(SimDb db, String pid, int version) throws SQLException {
		return toPidInfo(db.queryForFirst(
				" select pid, name, method, version, isfloorspec from variant_h where pid = ? and version = ? ", pid, version));
	}

	private static PidInfo toPidInfo(Map<String, String> row) {
		if (row == null)
			return null;
		PidInfo info = new PidInfo();
		info.pid = row.get("PID");
		info.name = row.get("NAME");
		info.method = SimUtil.NVL(row.get("METHOD"), "");
		info.version = SimUtil.parseInt(row.get("VERSION"));
		info.isfloorspec = row.get("ISFLOORSPEC");
		return info;
	}

	/** StringUtil.isPidPattern 의 PIDCache.hasPID 대체 */
	public boolean hasPid(SimDb db, String pid) throws SQLException {
		if (allPids == null) {
			synchronized (this) {
				if (allPids == null) {
					Set<String> set = new HashSet<String>();
					for (Map<String, String> r : db.queryForList(" SELECT PID FROM VARIANT_ID ")) {
						if (r.get("PID") != null)
							set.add(r.get("PID"));
					}
					allPids = Collections.unmodifiableSet(set);
				}
			}
		}
		return allPids.contains(pid);
	}

	/**
	 * Variant.getPIDLogic(PID, version, false) : 운영용 (null 인 SPEC/KEY 는 제외)
	 * 결과는 읽기 전용으로 공유한다.
	 */
	public SimVariantMap getLogic(SimDb db, String pid, int version) {
		String key = pid + "#" + version;
		SimVariantMap cached = logicCache.get(key);
		if (cached != null)
			return cached;

		SimVariantMap res = getLogic(db, pid, version, false);
		if (res.containsKey("data"))
			logicCache.put(key, res);
		return res;
	}

	/**
	 * Variant.getPIDLogic(PID, version, doDebug)
	 * doDebug 이면 null 인 SPEC/KEY 도 자리를 유지하고(화면 열 맞춤), 행마다 실행결과를 기록하므로 캐시하지 않는다.
	 */
	public SimVariantMap getLogic(SimDb db, String pid, int version, boolean doDebug) {
		int maxSpecIdx = 0;
		int maxResIdx = 0;
		SimVariantMap res = new SimVariantMap();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		try {
			List<Map<String, String>> logicDataList = db.queryForList(
					" select b.* from variant_h a, variant_d b where a.houid = b.houid AND a.pid = ? AND a.version = ? order by DOUID ",
					pid, version);

			for (Map<String, String> row : logicDataList) {
				ArrayList specList = new ArrayList();
				ArrayList conList = new ArrayList();
				ArrayList keyList = new ArrayList();
				ArrayList valList = new ArrayList();
				boolean isBlankLine = true;

				for (int i = 1; i <= SimConsts.MAX_SPEC_FIELD_SIZE; i++) {
					String specTmp = row.get("SPEC" + i);
					String conTmp = row.get("CON" + i);
					if (doDebug || specTmp != null) {
						specList.add(specTmp);
						conList.add(conTmp);
					}
					if (specTmp != null) {
						maxSpecIdx = Math.max(maxSpecIdx, i);
						isBlankLine = false;
					}
				}

				for (int i = 1; i <= SimConsts.MAX_RES_FIELD_SIZE; i++) {
					String keyTmp = row.get("KEY" + i);
					String valTmp = row.get("VAL" + i);
					if (doDebug || keyTmp != null) {
						keyList.add(keyTmp);
						valList.add(valTmp);
					}
					if (keyTmp != null) {
						maxResIdx = Math.max(maxResIdx, i);
						isBlankLine = false;
					}
				}

				SimVariantMap rowMap = new SimVariantMap();
				rowMap.put("specList", specList);
				rowMap.put("conList", conList);
				rowMap.put("keyList", keyList);
				rowMap.put("valList", valList);
				rowMap.put("GOTO", row.get("GOTO"));
				rowMap.put("ADDR", row.get("ADDR"));
				rowMap.put("REMARKS", row.get("REMARKS"));
				rowMap.put("DOUID", row.get("DOUID"));
				rowMap.put("isBlankLine", String.valueOf(isBlankLine));
				rowMap.put("line_no", row.get("NO"));

				data.add(rowMap);
			}

			res.put("data", data);
			res.put("maxSpecIdx", String.valueOf(maxSpecIdx));
			res.put("maxResIdx", String.valueOf(maxResIdx));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
}
