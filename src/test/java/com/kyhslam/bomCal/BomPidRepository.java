package com.kyhslam.bomCal;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PID 헤더/로직 캐시 (PIDCache 대체). 여러 스레드에서 공유하며, DB 조회는 호출한 스레드의 BomDb 로 한다.
 */
public class BomPidRepository {

	private static final Object NOT_FOUND = new Object();

	private final Map<String, Object> lastPidCache = new ConcurrentHashMap<String, Object>();
	private final Map<String, BomVariantMap> logicCache = new ConcurrentHashMap<String, BomVariantMap>();
	private volatile Set<String> allPids = null;

	/** variant_h 1건 */
	public static class PidInfo {
		private String pid;
		private String method;
		private int version;

		public String getPid() { return pid; }
		public String getMethod() { return method; }
		public int getVersion() { return version; }
	}

	/** 최신 버전 PID 정보 (없으면 null) */
	public PidInfo getLastPid(BomDb db, String pid) throws SQLException {
		Object cached = lastPidCache.get(pid);
		if (cached == null) {
			Map<String, String> row = db.queryForFirst(
					" select a.pid, a.method, a.version from variant_h a, variant_id b where a.houid = b.last_houid and a.pid = ? ", pid);
			if (row == null) {
				cached = NOT_FOUND;
			} else {
				PidInfo info = new PidInfo();
				info.pid = row.get("PID");
				info.method = BomUtil.NVL(row.get("METHOD"), "");
				info.version = BomUtil.parseInt(row.get("VERSION"));
				cached = info;
			}
			lastPidCache.put(pid, cached);
		}
		return cached == NOT_FOUND ? null : (PidInfo) cached;
	}

	/** StringUtil.isPidPattern 의 PIDCache.hasPID 대체 */
	public boolean hasPid(BomDb db, String pid) throws SQLException {
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
	public BomVariantMap getLogic(BomDb db, String pid, int version) {
		String key = pid + "#" + version;
		BomVariantMap cached = logicCache.get(key);
		if (cached != null)
			return cached;

		int maxSpecIdx = 0;
		int maxResIdx = 0;
		BomVariantMap res = new BomVariantMap();
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

				for (int i = 1; i <= BomConsts.MAX_SPEC_FIELD_SIZE; i++) {
					String specTmp = row.get("SPEC" + i);
					String conTmp = row.get("CON" + i);
					if (specTmp != null) {
						specList.add(specTmp);
						conList.add(conTmp);
						maxSpecIdx = Math.max(maxSpecIdx, i);
						isBlankLine = false;
					}
				}

				for (int i = 1; i <= BomConsts.MAX_RES_FIELD_SIZE; i++) {
					String keyTmp = row.get("KEY" + i);
					String valTmp = row.get("VAL" + i);
					if (keyTmp != null) {
						keyList.add(keyTmp);
						valList.add(valTmp);
						maxResIdx = Math.max(maxResIdx, i);
						isBlankLine = false;
					}
				}

				BomVariantMap rowMap = new BomVariantMap();
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
			logicCache.put(key, res);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
}
