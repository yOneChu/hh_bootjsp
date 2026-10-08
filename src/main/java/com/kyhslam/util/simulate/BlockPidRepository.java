package com.kyhslam.util.simulate;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PID 헤더/로직 캐시 (PIDCache 대체). 여러 스레드에서 공유하며, DB 조회는 호출한 스레드의 BlockDb 로 한다.
 */
public class BlockPidRepository {

	private static final Object NOT_FOUND = new Object();

	private final Map<String, Object> lastPidCache = new ConcurrentHashMap<String, Object>();
	private final Map<String, PidVariantMap> logicCache = new ConcurrentHashMap<String, PidVariantMap>();
	private volatile Set<String> allPids = null;

	/** true 면 PID 마다 테스트 버전(VERSION = -1)이 있으면 그것을, 없으면 최신 버전을 사용 */
	private final boolean useTestVersion;
	/** PID → 지정 버전 (화면에서 블럭 1개의 버전을 고른 경우) */
	private final Map<String, Integer> pinnedVersions;

	public BlockPidRepository() {
		this(false);
	}

	public BlockPidRepository(boolean useTestVersion) {
		this(useTestVersion, Collections.<String, Integer>emptyMap());
	}

	/**
	 * @param pinnedVersions PID → 지정 버전. 이 PID 들은 지정 버전으로, 나머지는 useTestVersion 규칙대로 계산한다.
	 */
	public BlockPidRepository(boolean useTestVersion, Map<String, Integer> pinnedVersions) {
		this.useTestVersion = useTestVersion;
		this.pinnedVersions = pinnedVersions;
	}

	public boolean isUseTestVersion() { return useTestVersion; }

	/** variant_h 1건 */
	public static class PidInfo {
		private String pid;
		private String method;
		private int version;

		public String getPid() { return pid; }
		public String getMethod() { return method; }
		public int getVersion() { return version; }
	}

	/** 최신 버전 PID 정보 (없으면 null). 지정 버전이 있으면 그 버전을, 테스트 모드면 테스트 버전을 먼저 찾는다. */
	public PidInfo getLastPid(BlockDb db, String pid) throws SQLException {
		Object cached = lastPidCache.get(pid);
		if (cached == null) {
			Map<String, String> row = null;
			Integer pinned = pinnedVersions.get(pid);
			if (pinned != null)
				row = db.queryForFirst(
						" select a.pid, a.method, a.version from variant_h a where a.pid = ? and a.version = ? order by a.houid desc ",
						pid, String.valueOf(pinned));
			if (row == null && useTestVersion)
				row = db.queryForFirst(
						" select a.pid, a.method, a.version from variant_h a where a.pid = ? and a.version = '-1' order by a.houid desc ", pid);
			if (row == null)
				row = db.queryForFirst(
						" select a.pid, a.method, a.version from variant_h a, variant_id b where a.houid = b.last_houid and a.pid = ? ", pid);
			if (row == null) {
				cached = NOT_FOUND;
			} else {
				PidInfo info = new PidInfo();
				info.pid = row.get("PID");
				info.method = BlockUtil.NVL(row.get("METHOD"), "");
				info.version = BlockUtil.parseInt(row.get("VERSION"));
				cached = info;
			}
			lastPidCache.put(pid, cached);
		}
		return cached == NOT_FOUND ? null : (PidInfo) cached;
	}

	/** StringUtil.isPidPattern 의 PIDCache.hasPID 대체 */
	public boolean hasPid(BlockDb db, String pid) throws SQLException {
		if (allPids == null) {
			synchronized (this) {
				if (allPids == null) {
					Set<String> set = new HashSet<String>();
					// 테스트 모드면 테스트 버전만 있는 신규 PID 도 PID 로 인식
					String sql = useTestVersion
							? " SELECT PID FROM VARIANT_ID UNION SELECT PID FROM VARIANT_H WHERE VERSION = '-1' "
							: " SELECT PID FROM VARIANT_ID ";
					for (Map<String, String> r : db.queryForList(sql)) {
						if (r.get("PID") != null)
							set.add(r.get("PID"));
					}
					// 지정 버전 PID 도 PID 로 인식 (테스트 버전만 있는 신규 PID 대비)
					set.addAll(pinnedVersions.keySet());
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
	public PidVariantMap getLogic(BlockDb db, String pid, int version) {
		String key = pid + "#" + version;
		PidVariantMap cached = logicCache.get(key);
		if (cached != null)
			return cached;

		int maxSpecIdx = 0;
		int maxResIdx = 0;
		PidVariantMap res = new PidVariantMap();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		try {
			// 테스트 버전은 헤더가 여러 건일 수 있으므로 getLastPid 와 같은 최신 HOUID 1건만 읽는다
			List<Map<String, String>> logicDataList = version == PidConsts.TEST_VERSION
					? db.queryForList(
							" select b.* from variant_d b where b.houid = (select max(houid) from variant_h where pid = ? and version = '-1') order by DOUID ",
							pid)
					: db.queryForList(
							" select b.* from variant_h a, variant_d b where a.houid = b.houid AND a.pid = ? AND a.version = ? order by DOUID ",
							pid, version);

			for (Map<String, String> row : logicDataList) {
				ArrayList specList = new ArrayList();
				ArrayList conList = new ArrayList();
				ArrayList keyList = new ArrayList();
				ArrayList valList = new ArrayList();
				boolean isBlankLine = true;

				for (int i = 1; i <= BlockConsts.MAX_SPEC_FIELD_SIZE; i++) {
					String specTmp = row.get("SPEC" + i);
					String conTmp = row.get("CON" + i);
					if (specTmp != null) {
						specList.add(specTmp);
						conList.add(conTmp);
						maxSpecIdx = Math.max(maxSpecIdx, i);
						isBlankLine = false;
					}
				}

				for (int i = 1; i <= BlockConsts.MAX_RES_FIELD_SIZE; i++) {
					String keyTmp = row.get("KEY" + i);
					String valTmp = row.get("VAL" + i);
					if (keyTmp != null) {
						keyList.add(keyTmp);
						valList.add(valTmp);
						maxResIdx = Math.max(maxResIdx, i);
						isBlankLine = false;
					}
				}

				PidVariantMap rowMap = new PidVariantMap();
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
